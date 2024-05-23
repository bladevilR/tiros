package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuRepairTaskStaffArrange;
import org.jeecg.modules.third.jdx.bean.BuWorkOrderTask;

/**
 * <p>
 * 任务人员安排 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-07-11
 */
public interface BuRepairTaskStaffArrangeThirdMapper extends BaseMapper<BuRepairTaskStaffArrange> {

    BuWorkOrderTask selectOrderTaskByStaffArrangeId(@Param("staffArrangeId") String staffArrangeId);

}
