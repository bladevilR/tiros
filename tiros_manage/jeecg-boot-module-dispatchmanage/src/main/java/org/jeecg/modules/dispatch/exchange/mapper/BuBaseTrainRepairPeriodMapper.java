package org.jeecg.modules.dispatch.exchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.exchange.bean.BuBaseTrainRepairPeriod;

/**
 * <p>
 * 架修周期，表示车辆的架修周期，在接车流程审批完成后，自动创建，如果已存在同样的车辆了，则将填写该记录的结束日期，并创建一 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-03
 */
public interface BuBaseTrainRepairPeriodMapper extends BaseMapper<BuBaseTrainRepairPeriod> {

//    /**
//     * 根据车号查询车辆最后一个架修周期
//     *
//     * @param trainNo 车号
//     * @return 车辆最后一个架修周期
//     */
//    BuBaseTrainRepairPeriod selectLastTrainPeriod(@Param("trainNo") String trainNo);

}
