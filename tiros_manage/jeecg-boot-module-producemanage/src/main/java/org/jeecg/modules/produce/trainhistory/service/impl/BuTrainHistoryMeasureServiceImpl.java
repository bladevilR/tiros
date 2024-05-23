package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryMeasure;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryMeasureQueryVO;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainHistoryMeasureMapper;
import org.jeecg.modules.produce.trainhistory.service.BuTrainHistoryMeasureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 车辆履历-测量记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Service
public class BuTrainHistoryMeasureServiceImpl extends ServiceImpl<BuTrainHistoryMeasureMapper, BuTrainHistoryMeasure> implements BuTrainHistoryMeasureService {

    @Resource
    private BuTrainHistoryMeasureMapper buTrainHistoryMeasureMapper;


    /**
     * @see BuTrainHistoryMeasureService#pageMeasureRecord(BuTrainHistoryMeasureQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuTrainHistoryMeasure> pageMeasureRecord(BuTrainHistoryMeasureQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buTrainHistoryMeasureMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

}
