package org.jeecg.modules.report.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.report.fault.bean.BuRptBoardTrainFault;
import org.jeecg.modules.report.fault.bean.vo.FaultCountQueryVO;
import org.jeecg.modules.report.fault.bean.vo.FaultSysCountVO;
import org.jeecg.modules.report.fault.bean.vo.FaultTrainCountVO;

import java.util.List;

/**
 * <p>
 * 故障汇总 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-12
 */
public interface BuFaultCountReportService extends IService<BuRptBoardTrainFault> {

    /**
     * 车辆故障统计获取
     *
     * @param queryVO 查询条件
     * @return 车辆故障统计
     * @throws Exception 异常信息
     */
    List<FaultTrainCountVO> countTrainFault(FaultCountQueryVO queryVO) throws Exception;

    /**
     * 系统故障统计获取
     *
     * @param queryVO 查询条件
     * @return 系统故障统计
     * @throws Exception 异常信息
     */
    List<FaultSysCountVO> countSysFault(FaultCountQueryVO queryVO) throws Exception;

//    /**
//     * 架修车辆故障统计获取
//     *
//     * @param queryVO 查询条件
//     * @return 架修车辆故障统计
//     * @throws Exception 异常信息
//     */
//    List<FaultTrainCountVO> countRepairTrainFault(FaultCountQueryVO queryVO) throws Exception;
//
//    /**
//     * 架修期各系统故障占比获取
//     *
//     * @param queryVO 查询条件
//     * @return 架修期各系统故障占比
//     * @throws Exception 异常信息
//     */
//    List<PieChartVO> countSysFaultRepair(FaultCountQueryVO queryVO) throws Exception;
//
//    /**
//     * 质保期各系统故障占比获取
//     *
//     * @param queryVO 查询条件
//     * @return 质保期各系统故障占比
//     * @throws Exception 异常信息
//     */
//    List<PieChartVO> countSysFaultWarranty(FaultCountQueryVO queryVO) throws Exception;
//
//    /**
//     * 架修期各车故障占比获取
//     *
//     * @param queryVO 查询条件
//     * @return 架修期各车故障占比
//     * @throws Exception 异常信息
//     */
//    List<PieChartVO> countTrainFaultRepair(FaultCountQueryVO queryVO) throws Exception;
//
//    /**
//     * 质保期各车故障占比获取
//     *
//     * @param queryVO 查询条件
//     * @return 质保期各车故障占比
//     * @throws Exception 异常信息
//     */
//    List<PieChartVO> countTrainFaultWarranty(FaultCountQueryVO queryVO) throws Exception;
//
//    /**
//     * 导出故障统计汇总
//     *
//     * @param queryVO 查询条件
//     * @return 故障统计汇总excel
//     * @throws Exception 异常信息
//     */
//    Workbook exportFaultCount(FaultCountQueryVO queryVO) throws Exception;

}
