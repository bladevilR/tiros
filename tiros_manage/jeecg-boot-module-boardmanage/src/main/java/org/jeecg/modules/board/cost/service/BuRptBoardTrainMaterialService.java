package org.jeecg.modules.board.cost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.AreaChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.board.cost.bean.BuRptBoardTrainMaterial;
import org.jeecg.modules.board.cost.bean.vo.BuBoardCostQueryVO;
import org.jeecg.modules.board.cost.bean.vo.BuRptBoardTrainMaterialProgramVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 物料成本统计 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
public interface BuRptBoardTrainMaterialService extends IService<BuRptBoardTrainMaterial> {

    /**
     * 根据条件查询近一年总金额面积图
     *
     * @param queryVO 查询条件
     * @return 近一年总金额面积图数据
     * @throws Exception 异常信息
     */
    List<AreaChartVO> getYearTotalCostAreaChart(BuBoardCostQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询车辆段物资成本统计
     *
     * @param queryVO 查询条件
     * @return 车辆段物资成本统计
     * @throws Exception 异常信息
     */
    List<BuRptBoardTrainMaterialProgramVO> listMaterialCost(BuBoardCostQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询最近的10列车车号
     *
     * @param queryVO 查询条件
     * @return 最近的10列车车号
     * @throws Exception 异常信息
     */
    List<String> getLastTenTrainNo(BuBoardCostQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询最近的10列车物资消耗成本
     *
     * @param queryVO 查询条件
     * @return 最近的10列车物资消耗成本
     * @throws Exception 异常信息
     */
    List<Map<String, Object>> getLastTenTrainCost(BuBoardCostQueryVO queryVO) throws Exception;

}
