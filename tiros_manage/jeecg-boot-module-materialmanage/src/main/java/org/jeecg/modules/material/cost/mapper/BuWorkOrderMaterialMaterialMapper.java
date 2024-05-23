package org.jeecg.modules.material.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.cost.bean.BuRepairPlan;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.material.cost.bean.vo.CheckQueryVO;
import org.jeecg.modules.material.cost.bean.vo.CompareQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工单物资消耗 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-22
 */
public interface BuWorkOrderMaterialMaterialMapper extends BaseMapper<BuWorkOrderMaterial> {

    /**
     * 根据班组id查询已核实未提交的非发料工单中的物料
     *
     * @param groupId         班组id
     * @param orderStatusList 工单状态
     * @return 工单物料
     */
    List<BuWorkOrderMaterial> selectNotMaterialApplyOrderListByGroupIdAndOrderStatus(@Param("groupId") String groupId, @Param("orderStatusList") List<Integer> orderStatusList);

    /**
     * 根据条件查询工单物料，用于消耗核实
     *
     * @param queryVO 查询条件
     * @return 工单物料
     */
    List<BuWorkOrderMaterial> selectListForCostCompare(@Param("queryVO") CompareQueryVO queryVO);

    /**
     * 根据条件查询未关闭的工单的工单物料，用于消耗核实
     *
     * @param queryVO 查询条件
     * @return 未关闭的消耗工单的工单物料
     */
    List<BuWorkOrderMaterial> selectListOfNotCloseConsumeOrderForCostCompare(@Param("queryVO") CompareQueryVO queryVO);

    /**
     * 根据条件查询工单物料，用于列计划物料核实
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 工单物料分页结果
     */
    IPage<BuWorkOrderMaterial> selectPageForCostCheck(IPage<BuWorkOrderMaterial> page, @Param("queryVO") CheckQueryVO queryVO);

    List<Map<String, Object>> selectLineList(@Param("lineId") String lineId);

    List<Map<String, Object>> selectGroupList(@Param("groupId") String groupId);

    BuRepairPlan selectPlanByPlanId(@Param("planId") String planId);

    /**
     * 根据工单物料id列表查询工单物料
     *
     * @param orderMaterialIdList 工单物料id列表
     * @return 工单物料
     */
    List<BuWorkOrderMaterial> selectListByIdList(@Param("orderMaterialIdList") List<String> orderMaterialIdList);

    /**
     * 根据id查询工单物料详情
     *
     * @param orderMaterialId 工单物料id
     * @return 工单物料详情
     */
    BuWorkOrderMaterial selectOrderMaterialById(@Param("orderMaterialId") String orderMaterialId);

}
