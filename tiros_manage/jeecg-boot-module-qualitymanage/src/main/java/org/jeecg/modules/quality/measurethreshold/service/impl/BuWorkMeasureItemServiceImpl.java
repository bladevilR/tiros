package org.jeecg.modules.quality.measurethreshold.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.util.MybatisWrapperUtil;
import org.jeecg.modules.quality.measurethreshold.bean.BuWorkMeasureItem;
import org.jeecg.modules.quality.measurethreshold.bean.vo.BuWorkMeasureItemQueryVO;
import org.jeecg.modules.quality.measurethreshold.mapper.BuWorkMeasureItemMapper;
import org.jeecg.modules.quality.measurethreshold.service.BuWorkMeasureItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 测量项定义 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
@Slf4j
@Service
public class BuWorkMeasureItemServiceImpl extends ServiceImpl<BuWorkMeasureItemMapper, BuWorkMeasureItem> implements BuWorkMeasureItemService {

    @Resource
    private BuWorkMeasureItemMapper buWorkMeasureItemMapper;


    /**
     * @see BuWorkMeasureItemService#listMeasureItemByFormId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkMeasureItem> listMeasureItemByFormId(String formId) throws Exception {
        return buWorkMeasureItemMapper.listMeasureItemByFormId(formId);
    }

    /**
     * @see BuWorkMeasureItemService#getMeasureItemByFormIdAndRow(BuWorkMeasureItemQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkMeasureItem getMeasureItemByFormIdAndRow(BuWorkMeasureItemQueryVO queryVO) throws Exception {
        queryVO.checkParam();
        List<BuWorkMeasureItem> itemList = buWorkMeasureItemMapper.selectByCondition(queryVO);

        if (CollectionUtils.isNotEmpty(itemList)) {
            if (itemList.size() > 1) {
                List<String> linkDomainList = itemList.stream().map(BuWorkMeasureItem::getLinkDomain).collect(Collectors.toList());
                throw new JeecgBootException(String.format("数据不唯一，所跨区域%s已设置测量项", StringUtils.join(linkDomainList, ",")));
            }
            return itemList.get(0);
        }

        return null;
    }

    /**
     * @see BuWorkMeasureItemService#saveMeasureItem(BuWorkMeasureItem)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveMeasureItem(BuWorkMeasureItem buWorkMeasureItem) throws Exception {
        checkRepeat(buWorkMeasureItem);
        buWorkMeasureItemMapper.insert(buWorkMeasureItem);
        return true;
    }

    /**
     * @see BuWorkMeasureItemService#updateMeasureItem(BuWorkMeasureItem)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateMeasureItem(BuWorkMeasureItem measureItem) throws Exception {
        checkRepeat(measureItem);
//        buWorkMeasureItemMapper.updateById(buWorkMeasureItem);
        // 解决updateById不可更新为null的问题
        UpdateWrapper<BuWorkMeasureItem> updateWrapper = MybatisWrapperUtil.getSetFieldNullByIdUpdateWrapper(measureItem);
        buWorkMeasureItemMapper.update(measureItem, updateWrapper);

        return true;
    }


    private void checkRepeat(BuWorkMeasureItem requestItem) {
        LambdaQueryWrapper<BuWorkMeasureItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuWorkMeasureItem::getCustomId, requestItem.getCustomId());
        List<BuWorkMeasureItem> dbItemList = buWorkMeasureItemMapper.selectList(wrapper);

        for (BuWorkMeasureItem dbItem : dbItemList) {
            if (StringUtils.isNotBlank(requestItem.getId()) && requestItem.getId().equals(dbItem.getId())) {
                continue;
            }

            List<String> requestPosition = getMiddlePosition(requestItem.getRow1(), requestItem.getRow2(), requestItem.getCol1(), requestItem.getCol2());
            List<String> dbPosition = getMiddlePosition(dbItem.getRow1(), dbItem.getRow2(), dbItem.getCol1(), dbItem.getCol2());

            requestPosition.retainAll(dbPosition);
            if (requestPosition.size() > 0) {
                throw new JeecgBootException(String.format("区域%s已设置测量项", dbItem.getLinkDomain()));
            }
        }
    }

    private List<String> getMiddlePosition(Integer rowStart, Integer rowEnd, Integer colStart, Integer colEnd) {
        List<String> list = new ArrayList<>();

        if (null != rowStart && null != rowEnd && rowStart <= rowEnd
                && null != colStart && null != colEnd && colStart <= colEnd) {
            for (int i = rowStart; i <= rowEnd; i++) {
                for (int j = colStart; j <= colEnd; j++) {
                    list.add(i + "." + j);
                }
            }
        }

        return list;
    }

}
