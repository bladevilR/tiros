package org.jeecg.modules.produce.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.cost.bean.BuMaterialType;
import org.jeecg.modules.produce.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.produce.cost.bean.BuWorkOrderMaterialActs;
import org.jeecg.modules.produce.cost.bean.bo.MaterialCostSumBO;
import org.jeecg.modules.produce.cost.bean.vo.BuCostQueryVO;

import java.util.List;

/**
 * <p>
 * 工单物料 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-22
 */
public interface BuWorkOrderMaterialProduceMapper extends BaseMapper<BuWorkOrderMaterial> {

    /**
     * 根据条件分页查询物料工单物料
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuWorkOrderMaterial> selectPageByCondition(IPage<BuWorkOrderMaterial> page, @Param("queryVO") BuCostQueryVO queryVO);

    /**
     * 根据条件查询物料工单物料简单信息，用于统计数量
     *
     * @param queryVO 查询条件
     * @return 物料工单物料简单信息
     */
    List<BuWorkOrderMaterial> selectSimpleListByCondition(@Param("queryVO") BuCostQueryVO queryVO);

    /**
     * 查询指定工单的物料及相关数据，用于生成中间统计数据
     *
     * @param orderIdList 工单id列表
     * @return 工单物料相关数据
     */
    List<BuWorkOrderMaterial> selectListForRptByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 查询指定列计划下工单的物料消耗总和bo
     *
     * @param planId    列计划id
     * @param groupType 分组类型：1物资类型id、2物资类型名称、3用途类型
     * @return 工单物料消耗总和bo
     */
    List<MaterialCostSumBO> selectMaterialCostSumBOListByPlanId(@Param("planId") String planId, @Param("groupType") Integer groupType);

    /**
     * 根据列计划id查询该列计划下已提交和已关闭的工单物料实际消耗
     *
     * @param planId 列计划id
     * @return 工单物料实际消耗
     */
    List<BuWorkOrderMaterialActs> selectActListOfSubmitOrderByPlanId(@Param("planId") String planId);

    /**
     * 根据物资类型id查询物资类型
     *
     * @param materialTypeIdList 物资类型id列表
     * @return 物资类型列表
     */
    List<BuMaterialType> selectMaterialListByMaterialTypeIdList(@Param("materialTypeIdList") List<String> materialTypeIdList);

}
