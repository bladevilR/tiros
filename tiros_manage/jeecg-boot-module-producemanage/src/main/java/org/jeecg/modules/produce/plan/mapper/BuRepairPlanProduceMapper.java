package org.jeecg.modules.produce.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.plan.bean.BuRepairPlan;
import org.jeecg.modules.produce.plan.bean.bo.BuRepairReguDetailBO;
import org.jeecg.modules.produce.plan.bean.vo.BuRepairPlanRelationVO;
import org.jeecg.modules.produce.plan.bean.vo.BuRepairPlanVO;
import org.jeecg.modules.produce.plan.bean.vo.BuWorkOrderVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 列计划 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairPlanProduceMapper extends BaseMapper<BuRepairPlan> {

    /**
     * 根据id查询列计划详情
     *
     * @param planId 列计划id
     * @return 列计划详情
     */
    BuRepairPlan selectPlanById(@Param("planId") String planId);

    /**
     * 根据线路和修程查询列计划详情
     *
     * @param lineId          线路id
     * @param repairProgramId 修程id
     * @return 列计划详情
     */
    List<BuRepairPlan> selectPlanListByLineAndRepairProgram(@Param("lineId") String lineId, @Param("repairProgramId") String repairProgramId);

    /**
     * 查询所有列计划信息，用于转换为BuRepairPlanVO
     *
     * @return 所有列计划信息
     */
    List<BuRepairPlan> selectApprovedNotFinishListFromRepairPlanVO();

    /**
     * 根据列计划id查询列计划详细信息
     *
     * @param planId 列计划id
     * @return 列计划详细信息
     */
    BuRepairPlanVO selectAsRepairPlanVOByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询计划关联信息
     *
     * @param planId 列计划id
     * @return 计划关联信息
     */
    BuRepairPlanRelationVO selectPlanRelevanceInfoByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id统计故障数量
     *
     * @param planId 列计划id
     * @return 该列计划的故障数量
     */
    Integer selectFaultCountByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id统计已处理故障数量
     *
     * @param planId 列计划id
     * @return 该列计划的已处理故故障数量
     */
    Integer selectFinishFaultCountByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询列计划关联规程的明细信息树
     *
     * @param planId 列计划id
     * @return 关联规程的明细信息树
     */
    List<BuRepairReguDetailBO> selectAllReguDetailListByPlanId(@Param("planId") String planId);

//    /**
//     * 根据列计划id查询列计划车辆的车辆结构信息树
//     *
//     * @param planId 列计划id
//     * @return 列计划车辆的车辆结构信息树
//     */
//    List<BuTrainStructureDetailBO> selectAllStructDetailListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询工单和规程明细的关联信息
     *
     * @param planId 列计划id
     * @return 工单和规程明细的关联信息
     */
    List<Map<String, Object>> selectOrderIdAndReguDetailIdByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询列计划任务和规程明细的关联信息
     *
     * @param planId 列计划id
     * @return 列计划任务和规程明细的关联信息
     */
    List<Map<String, Object>> selectPlanTaskIdAndReguDetailIdByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询列计划任务和故障数量的关联信息
     *
     * @param planId 列计划id
     * @return 列计划任务和故障数量的关联信息
     */
    List<Map<String, Object>> selectPlanTaskIdAndFaultCountByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询工单和设备类型的关联信息
     *
     * @param planId 列计划id
     * @return 工单和设备类型的关联信息
     */
    List<Map<String, Object>> selectOrderIdAndAssetTypeIdByPlanId(@Param("planId") String planId);

    /**
     * 根据工单id列表查询工单信息列表
     *
     * @param orderIdList 工单id列表
     * @return 工单信息列表
     */
    List<BuWorkOrderVO> selectWorkOrderVOListByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 根据线路id查询所属车型
     *
     * @param lineId 线路id
     * @return 车型id
     */
    String selectTrainTypeIdByLineId(@Param("lineId") String lineId);

    /**
     *  查询列计划维修周期和实际天数
     * @param lineId  线路id
     * @param repairProgramId  修程id
     * @return  列计划
     */
    List<BuRepairPlan> selectRepairDayList(@Param("lineId")String lineId, @Param("repairProgramId")String repairProgramId);

    /**
     * 查询近几列车列计划
     * @param lineId 线路id
     * @param repairProgramId 修程id
     * @param column  几列车
     * @return 列计划
     */

    List<BuRepairPlan> selectRepairPlanNearlyColumns(@Param("lineId")String lineId, @Param("repairProgramId")String repairProgramId,@Param("column") Integer column);

    /**
     * 未完成任务的最新一个
     * @param planId 列计划id
     * @return 任务名
     */
    String selectUnfinishedFirstInfo(@Param("planId") String planId);
}
