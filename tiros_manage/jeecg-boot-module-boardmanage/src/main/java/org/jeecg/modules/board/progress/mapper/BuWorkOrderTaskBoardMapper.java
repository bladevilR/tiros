package org.jeecg.modules.board.progress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.progress.bean.BuWorkOrderTask;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工单任务 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
public interface BuWorkOrderTaskBoardMapper extends BaseMapper<BuWorkOrderTask> {

    /**
     * 根据条件查询查询完成的工单任务
     *
     * @param queryVO 查询条件
     * @return 完成的工单任务
     */
    List<BuWorkOrderTask> listFinishWorkOrderTaskByCondition(@Param("queryVO") BuBoardProgressQueryVO queryVO);

    /**
     * 根据条件查询查询所有工单任务
     *
     * @param queryVO 查询条件
     * @return 所有工单任务
     */
    List<BuWorkOrderTask> listIssuedWorkOrderTaskByCondition(@Param("queryVO") BuBoardProgressQueryVO queryVO);

    /**
     * 根据工单id和任务状态查询工单任务列表
     *
     * @param orderId    工单id
     * @param taskStatus 任务状态
     * @return 工单任务列表
     */
    List<BuWorkOrderTask> listByOrderIdAndTaskStatus(@Param("orderId") String orderId, @Param("taskStatus") Integer taskStatus);

    /**
     * 根据工单任务id查询任务人员安排信息
     *
     * @param orderTaskIdList 工单任务id列表
     * @return 任务人员安排信息
     */
    List<Map<String, Object>> selectOrderTaskUserByOrderTaskIdList(@Param("orderTaskIdList") List<String> orderTaskIdList);

    /**
     * 根据列计划id查询列计划下工单任务
     * @param planId 列计划id
     * @return 工单任务列表
     */
    List<BuWorkOrderTask> selectPlanOrderTaskListByPlanId(@Param("planId") String planId);

}
