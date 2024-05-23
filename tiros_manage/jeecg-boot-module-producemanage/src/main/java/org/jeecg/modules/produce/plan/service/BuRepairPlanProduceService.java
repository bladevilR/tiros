package org.jeecg.modules.produce.plan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.plan.bean.BuRepairPlan;
import org.jeecg.modules.produce.plan.bean.vo.*;

import java.util.List;

/**
 * <p>
 * 列计划 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairPlanProduceService extends IService<BuRepairPlan> {

    /**
     * 获取所有列计划列表(按线路分组)
     *
     * @return 所有列计划列表
     * @throws Exception 异常
     */
    List<BuLineRepairPlanVO> listAllGroupByLine() throws Exception;

    /**
     * 获取所有列计划列表(按车辆段分组)
     *
     * @return 所有列计划列表
     * @throws Exception 异常
     */
    List<BuDepotRepairPlanVO> listAllGroupByDepot() throws Exception;

    /**
     * 根据列计划id查询列计划详细信息
     *
     * @param planId 列计划id
     * @return 列计划详细信息
     * @throws Exception 异常
     */
    BuRepairPlanVO getRepairPlanVOById(String planId, boolean needCountInfo) throws Exception;

    /**
     * 规程结构方式-查询规程进度列表
     *
     * @param planId 列计划id
     * @return 规程结构方式vo（规程作业项）列表
     * @throws Exception 异常
     */
    List<BuRepairPlanReguVO> listReguVOByPlanId(String planId) throws Exception;

    /**
     * 车辆结构方式-查询结构进度列表
     *
     * @param planId 列计划id
     * @return 车辆结构方式vo（车辆结构树）列表
     * @throws Exception 异常
     */
    List<BuRepairPlanTrainStructVO> listTrainStructVOByPlanId(String planId) throws Exception;

    /**
     * 车辆结构方式-查询工单列表
     *
     * @param orderIds 工单ids
     * @return 工单vo列表
     * @throws Exception 异常
     */
    List<BuWorkOrderVO> listWorkOrderVOByOrderIds(String orderIds) throws Exception;

//    /**
//     * 根据列计划id查询计划关联信息
//     *
//     * @param id 列计划id
//     * @return 计划关联信息
//     * @throws Exception 异常
//     */
//    PlanRelevanceInfo selectPlanRelevanceInfo(String id) throws Exception;

}
