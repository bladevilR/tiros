package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuRepairTaskStaffArrange;

import java.util.List;

/**
 * <p>
 * 任务人员安排 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuRepairTaskStaffArrangeMapper extends BaseMapper<BuRepairTaskStaffArrange> {

    /**
     * 批量插入
     *
     * @param list 任务人员安排列表
     */
    void insertList(@Param("list") List<BuRepairTaskStaffArrange> list);

    /**
     * 根据工单id删除工单物料实际消耗
     *
     * @param orderIdList 工单id列表
     */
    void deleteByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 根据工单任务id查询任务人员安排
     *
     * @param taskId 工单任务id
     * @return 任务人员安排
     */
    List<BuRepairTaskStaffArrange> selectListByOrderTaskId(@Param("taskId") String taskId);

    /**
     * 根据工单id查询任务人员安排
     *
     * @param orderId 工单id
     * @return 任务人员安排
     */
    List<BuRepairTaskStaffArrange> selectListByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单id查询任务人员安排
     *
     * @param orderIdList 工单id
     * @return 任务人员安排
     */
    List<BuRepairTaskStaffArrange> selectListForMaximoByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 根据工单id查询任务人员安排，用于绩效统计
     *
     * @param orderId 工单id
     * @return 任务人员安排
     */
    List<BuRepairTaskStaffArrange> selectListForRptByOrderId(@Param("orderId") String orderId);

}
