package org.jeecg.modules.board.cost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.board.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.board.cost.bean.vo.BuBoardCostQueryVO;
import org.jeecg.modules.board.cost.bean.vo.BuMaterialAlertVO;
import org.jeecg.modules.board.cost.bean.vo.BuWorkshopCostItemVO;
import org.jeecg.modules.board.cost.bean.vo.WorkshopMonthCostData;

import java.util.List;

/**
 * <p>
 * 工单物料 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-02
 */
public interface BuWorkOrderMaterialBoardService extends IService<BuWorkOrderMaterial> {

    /**
     * 根据条件查询物资消耗成本前10
     *
     * @param queryVO 查询条件
     * @return 物资消耗成本前10饼图信息
     * @throws Exception 异常信息
     */
    List<PieChartVO> getMaterialCostTopTen(BuBoardCostQueryVO queryVO) throws Exception;

    /**
     * 查询指定月份的车间的成本数据
     *
     * @param queryVO 查询条件
     * @return 成本数据
     * @throws Exception 异常信息
     */
    WorkshopMonthCostData getWorkshopMonthCostData(BuBoardCostQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询物料消耗统计
     *
     * @param queryVO 查询条件
     * @return 物料消耗统计
     * @throws Exception 异常信息
     */
    List<BuWorkshopCostItemVO> listMaterialCostItem(BuBoardCostQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询物料消耗占比
     *
     * @param queryVO 查询条件
     * @return 物料消耗占比
     * @throws Exception 异常信息
     */
    List<PieChartVO> listMaterialCostProportion(BuBoardCostQueryVO queryVO) throws Exception;

    /**
     * 查询物料安全库存预警差距最大的10条
     *
     * @return 查询物料安全库存预警差距最大的10条
     * @throws Exception 异常信息
     */
    List<BuMaterialAlertVO> listMaterialThresholdAlertTopTen() throws Exception;

}
