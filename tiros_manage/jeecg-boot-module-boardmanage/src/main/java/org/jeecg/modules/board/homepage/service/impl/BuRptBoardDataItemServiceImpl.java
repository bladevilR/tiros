package org.jeecg.modules.board.homepage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.rpt.constrant.HomepageItemCode;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.modules.board.homepage.bean.BuRptBoardDataItem;
import org.jeecg.modules.board.homepage.bean.bo.SysDictItemBO;
import org.jeecg.modules.board.homepage.mapper.BuRptBoardDataItemMapper;
import org.jeecg.modules.board.homepage.service.BuRptBoardDataItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 统计数据项表（首页） 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
@Slf4j
@Service
public class BuRptBoardDataItemServiceImpl extends ServiceImpl<BuRptBoardDataItemMapper, BuRptBoardDataItem> implements BuRptBoardDataItemService {

    @Resource
    private BuRptBoardDataItemMapper buRptBoardDataItemMapper;


    /**
     * @see BuRptBoardDataItemService#listByCategoryId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRptBoardDataItem> listByCategoryId(String categoryId) throws Exception {
        List<BuRptBoardDataItem> dataItemList = buRptBoardDataItemMapper.selectListByCategoryId(categoryId);

        // 设置颜色
        setItemColor(categoryId, dataItemList);

        return dataItemList;
    }


    private void setItemColor(String categoryId, List<BuRptBoardDataItem> dataItemList) {
        String homepageColorAlertDictCode = "bu_board_homepage_color_alert";
        String homepageColorDataDictCode = "bu_board_homepage_color_data";
        String dictCode = "1".equals(categoryId) ? homepageColorAlertDictCode : homepageColorDataDictCode;

        List<SysDictItemBO> dictItemBOList = buRptBoardDataItemMapper.selectColorDictItemListByDictCode(dictCode);
        Map<String, String> itemNameColorMap = dictItemBOList.stream().collect(Collectors.toMap(SysDictItemBO::getItemText, SysDictItemBO::getItemValue));

        String alertItemZeroColor = "#F2F2F2";
        for (BuRptBoardDataItem item : dataItemList) {
            if ("1".equals(item.getCategoryId())) {
                // 预警区
                if ("0".equals(item.getItemValue())) {
                    // 预警值为0，用灰色
                    item.setItemColor(alertItemZeroColor);
                } else {
                    // 预警值不为0，用配置的颜色
                    item.setItemColor(itemNameColorMap.getOrDefault(item.getItemTitle(), alertItemZeroColor));
                }
            } else {
                // 数据区，用配置的颜色
                item.setItemColor(itemNameColorMap.get(item.getItemTitle()));
            }
        }
    }

}
