package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderMaterial;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOrderMaterialQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderMaterialMapper extends BaseMapper<BuWorkOrderMaterial> {

    /**
     * 批量插入
     *
     * @param list 工单物料列表
     */
    void insertList(@Param("list") List<BuWorkOrderMaterial> list);

    /**
     * 批量更新工单物料实际消耗数量
     *
     * @param list 工单物料列表
     */
    void updateListActAmount(@Param("list") List<BuWorkOrderMaterial> list);

    /**
     * 批量更新工单物料系统和工位
     *
     * @param list 工单物料列表
     */
    void updateListSystemWorkstation(@Param("list") List<BuWorkOrderMaterial> list);

    /**
     * 根据工单id删除工单物料实际消耗
     *
     * @param orderIdList 工单id列表
     */
    int deleteByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 根据条件查询工单物料
     *
     * @param queryVO 查询条件
     * @return 工单物料列表
     */
    List<BuWorkOrderMaterial> selectListByQueryVO(@Param("queryVO") BuOrderMaterialQueryVO queryVO);

    /**
     * 根据工单id和类型查询工单物料
     *
     * @param orderId 工单id
     * @param opType  工单物料操作类型
     * @return 工单物料列表
     */
    List<BuWorkOrderMaterial> selectNotGenOrderListByOrderIdAndOpType(@Param("orderId") String orderId, @Param("opType") Integer opType);

    /**
     * 根据工单任务id查询工单物料
     *
     * @param taskId 工单任务id
     * @return 工单物料列表
     */
    List<BuWorkOrderMaterial> selectListByOrderTaskId(@Param("taskId") String taskId);

    /**
     * 根据工单id列表查询工单物料
     *
     * @param orderIdList 工单id
     * @return 工单物料
     */
    List<BuWorkOrderMaterial> selectListByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 根据工单id查询必换件物料
     *
     * @param orderId 工单id
     * @return 必换件物料
     */
    List<BuWorkOrderMaterial> selectMustMaterialListByOrderId(@Param("orderId") String orderId);

    /**
     * 查询非发料、已提交已关闭、的工单的需求数量大于0的工单物料
     *
     * @param groupId 工班id
     * @return 工单物料
     */
    List<BuWorkOrderMaterial> selectListOfSubmitOrCloseOrder(@Param("groupId") String groupId);

    /**
     * 查询系统或工位无效的工单物料
     *
     * @param planId 列计划id
     * @return 系统或工位无效的工单物料
     */
    List<BuWorkOrderMaterial> selectSystemOrWorkstationInvalidList(@Param("planId") String planId);

    /**
     * 根据班组id查询已核实未提交的非发料工单中的物料
     *
     * @param groupId         班组id
     * @param orderStatusList 工单状态
     * @return 工单物料
     */
    List<BuWorkOrderMaterial> selectNotMaterialApplyOrderListByGroupIdAndOrderStatus(@Param("groupId") String groupId, @Param("orderStatusList") List<Integer> orderStatusList);

    /**
     * 查询物资类型id和code
     *
     * @param codeList 物资编码列表
     * @return 物资类型id和code
     */
    List<Map<String, Object>> selectMaterialTypeIdCodeByCodeList(@Param("codeList") List<String> codeList);

}
