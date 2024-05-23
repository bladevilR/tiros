package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanTrainPark;

/**
 * <p>
 * 车辆的停放计划，子任务没有设置的话，就继承父任务的设置，如果有设置则覆盖父任务的设置 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanTrainParkMapper extends BaseMapper<BuTpRepairPlanTrainPark> {

}
