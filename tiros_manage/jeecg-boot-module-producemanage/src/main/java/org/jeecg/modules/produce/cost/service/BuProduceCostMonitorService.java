package org.jeecg.modules.produce.cost.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.produce.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.produce.cost.bean.vo.*;

/**
 * <p>
 * 物资消耗 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-22
 */
public interface BuProduceCostMonitorService {

    /**
     * 根据条件分页查询物资消耗记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuWorkOrderMaterial> pageOrderMaterial(BuCostQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 查询物资消耗总金额和各分类金额
     *
     * @param queryVO 查询条件
     * @return 物资消耗总金额和各分类金额
     * @throws Exception 异常信息
     */
    CostCountVO countCost(BuCostQueryVO queryVO) throws Exception;

//    /**
//     * 车辆架大修成本趋势(近6年物资消耗)
//     *
//     * @param queryVO 查询条件
//     * @return 近6年物资消耗总金额和各分类金额
//     * @throws Exception 异常信息
//     */
//    CostCompareYearTrendVO getCostYearTrend(BuCostQueryVO queryVO) throws Exception;

    /**
     * 车辆架大修成本趋势(近6年物资消耗)
     *
     * @return 近6年物资消耗总金额和各分类金额
     * @throws Exception 异常信息
     */
    CostCompareYearTrendVO getCostYearTrend() throws Exception;

    /**
     * 根据条件统计物资消耗数据
     *
     * @param queryVO 查询条件
     * @return 物资消耗总金额和各分类金额
     * @throws Exception 异常信息
     */
    CostStatisticsVO getCostStatistics(BuCostQueryVO queryVO) throws Exception;

    /**
     * 根据条件比较分析 查询物资消耗总金额和各分类金额、和各分类各系统金额
     *
     * @param compareQueryVO 查询条件
     * @return 物资消耗总金额和各分类金额
     * @throws Exception 异常信息countMaterialExpendGroupBySystem
     */
    BuCostCompareResultVO compareCostCount(BuCostCompareQueryVO compareQueryVO) throws Exception;

}
