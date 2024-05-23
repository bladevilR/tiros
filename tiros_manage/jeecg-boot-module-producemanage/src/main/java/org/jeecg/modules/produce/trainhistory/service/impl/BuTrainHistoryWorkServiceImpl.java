package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryWork;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryWorkQueryVO;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainHistoryWorkMapper;
import org.jeecg.modules.produce.trainhistory.service.BuTrainHistoryWorkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 车辆履历-作业记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Service
public class BuTrainHistoryWorkServiceImpl extends ServiceImpl<BuTrainHistoryWorkMapper, BuTrainHistoryWork> implements BuTrainHistoryWorkService {

    @Resource
    private BuTrainHistoryWorkMapper buTrainHistoryWorkMapper;


    /**
     * @see BuTrainHistoryWorkService#pageWorkRecord(BuTrainHistoryWorkQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuTrainHistoryWork> pageWorkRecord(BuTrainHistoryWorkQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buTrainHistoryWorkMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

}
