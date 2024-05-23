package org.jeecg.modules.group.turnoverchange.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.group.turnoverchange.bean.BuWorkTurnoverChange;
import org.jeecg.modules.group.turnoverchange.bean.vo.BuWorkTurnoverChangeQueryVO;
import org.jeecg.modules.group.turnoverchange.mapper.BuWorkTurnoverChangeMapper;
import org.jeecg.modules.group.turnoverchange.service.BuWorkTurnoverChangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 周转件更换，如果换上件没有记录，应先建立记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Slf4j
@Service
public class BuWorkTurnoverChangeServiceImpl extends ServiceImpl<BuWorkTurnoverChangeMapper, BuWorkTurnoverChange> implements BuWorkTurnoverChangeService {

    @Resource
    private BuWorkTurnoverChangeMapper buWorkTurnoverChangeMapper;

    /**
     * @see BuWorkTurnoverChangeService#page(BuWorkTurnoverChangeQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuWorkTurnoverChange> page(BuWorkTurnoverChangeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuWorkTurnoverChange> page = buWorkTurnoverChangeMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        List<BuWorkTurnoverChange> turnoverChangeList = page.getRecords();
        if (CollectionUtils.isNotEmpty(turnoverChangeList)) {
            for (BuWorkTurnoverChange turnoverChange : turnoverChangeList) {
                String upTurnoverId = turnoverChange.getUpTurnoverId();
                String lastTrainNo = buWorkTurnoverChangeMapper.selectLastTrainNoByTurnoverId(upTurnoverId);
                turnoverChange.setUpTurnoverTrainNo(lastTrainNo);
            }
        }

        return page;
    }

    /**
     * @see BuWorkTurnoverChangeService#selectById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkTurnoverChange selectById(String id) throws Exception {
        BuWorkTurnoverChange turnoverChange = buWorkTurnoverChangeMapper.selectById(id);
        if (null == turnoverChange) {
            throw new JeecgBootException("周转件更换记录不存在");
        }

        String upTurnoverId = turnoverChange.getUpTurnoverId();
        String lastTrainNo = buWorkTurnoverChangeMapper.selectLastTrainNoByTurnoverId(upTurnoverId);
        turnoverChange.setUpTurnoverTrainNo(lastTrainNo);

        return turnoverChange;
    }

    /**
     * @see BuWorkTurnoverChangeService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        buWorkTurnoverChangeMapper.deleteBatchIds(idList);

        return true;
    }

}
