package org.jeecg.modules.produce.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.plan.bean.BuRepairTaskStaffArrange;
import org.jeecg.modules.produce.plan.bean.bo.WorkTimeSumBO;

import java.util.List;

/**
 * <p>
 * 任务人员安排 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-11-22
 */
public interface BuRepairTaskStaffArrangeProduceMapper extends BaseMapper<BuRepairTaskStaffArrange> {

    /**
     * 根据线路和修程查询任务人员安排
     *
     * @param lineId          线路id
     * @param repairProgramId 修程id
     * @return 任务人员安排
     */
    List<WorkTimeSumBO> selectWorkTimeSumBOListByLineAndProgram(@Param("lineId") String lineId, @Param("repairProgramId") String repairProgramId);

}
