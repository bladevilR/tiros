package org.jeecg.modules.report.cost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.report.cost.bean.BuRptBoardSysMaterial;
import org.jeecg.modules.report.cost.bean.vo.BuCostSystemTotalCountVO;
import org.jeecg.modules.report.cost.bean.vo.BuReportCostSingleTrainQueryVO;

import java.util.List;

/**
 * <p>
 * 物料成本统计 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-07
 */
public interface BuRptBoardSysMaterialService extends IService<BuRptBoardSysMaterial> {

    /**
     * 查询车辆的车厢数量
     *
     * @param lineId  线路id
     * @param trainNo 车号
     * @return 车辆的车厢数量
     * @throws Exception 异常
     */
    Integer getTrainCarsNumber(String lineId, String trainNo) throws Exception;

    /**
     * 根据条件查询车辆各系统消耗金额
     *
     * @param queryVO 查询条件
     * @return 车辆各系统消耗金额
     * @throws Exception 异常
     */
    List<BuCostSystemTotalCountVO> getCostSystemStatistic(BuReportCostSingleTrainQueryVO queryVO) throws Exception;

//    /**
//     * 根据条件查询车辆累计消耗金额
//     *
//     * @param queryVO 查询条件
//     * @return 车辆累计消耗金额
//     * @throws Exception 异常
//     */
//    List<BuCostSystemTotalCountVO> getCostSystemTotalStatistic(BuReportCostSingleTrainQueryVO queryVO) throws Exception;

}
