package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryMeasure;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryMeasureQueryVO;

/**
 * <p>
 * 车辆履历-测量记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryMeasureService extends IService<BuTrainHistoryMeasure> {

    /**
     * 根据条件查询车辆履历测量记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 测量记录分页
     * @throws Exception 异常信息
     */
    IPage<BuTrainHistoryMeasure> pageMeasureRecord(BuTrainHistoryMeasureQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
