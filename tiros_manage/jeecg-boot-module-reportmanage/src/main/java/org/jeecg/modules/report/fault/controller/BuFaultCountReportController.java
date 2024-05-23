package org.jeecg.modules.report.fault.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.report.fault.bean.vo.FaultCountQueryVO;
import org.jeecg.modules.report.fault.bean.vo.FaultSysCountVO;
import org.jeecg.modules.report.fault.bean.vo.FaultTrainCountVO;
import org.jeecg.modules.report.fault.service.BuFaultCountReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 故障报表 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Api(tags = "故障汇总")
@Slf4j
@RestController
@RequestMapping("/report/fault/count")
public class BuFaultCountReportController {

    private final BuFaultCountReportService buFaultCountReportService;

    public BuFaultCountReportController(BuFaultCountReportService buFaultCountReportService) {
        this.buFaultCountReportService = buFaultCountReportService;
    }


    @GetMapping("/trains")
    @ApiOperation(value = "各车辆故障统计")
    @OperationLog()
    public Result<List<FaultTrainCountVO>> countTrainFault(@Validated FaultCountQueryVO queryVO) throws Exception {
        List<FaultTrainCountVO> trainCountVOList = buFaultCountReportService.countTrainFault(queryVO);
        return new Result<List<FaultTrainCountVO>>().successResult(trainCountVOList);
    }

    @GetMapping("/systems")
    @ApiOperation(value = "各系统故障统计/分布")
    @OperationLog()
    public Result<List<FaultSysCountVO>> countSysFault(@Validated FaultCountQueryVO queryVO) throws Exception {
        List<FaultSysCountVO> sysCountVOList = buFaultCountReportService.countSysFault(queryVO);
        return new Result<List<FaultSysCountVO>>().successResult(sysCountVOList);
    }

//    @GetMapping("/repair-trains")
//    @ApiOperation(value = "架修车辆各类故障分布")
//    @OperationLog()
//    public Result<List<FaultTrainCountVO>> countRepairTrainFault(@Validated FaultCountQueryVO queryVO) throws Exception {
//        List<FaultTrainCountVO> trainCountVOList = buFaultCountReportService.countRepairTrainFault(queryVO);
//        return new Result<List<FaultTrainCountVO>>().successResult(trainCountVOList);
//    }
//
//    @GetMapping("/systems/repair")
//    @ApiOperation(value = "架修期各系统故障占比")
//    @OperationLog()
//    public Result<List<PieChartVO>> countSysFaultRepairByYear(@Validated FaultCountQueryVO queryVO) throws Exception {
//        List<PieChartVO> pieChartVOList = buFaultCountReportService.countSysFaultRepair(queryVO);
//        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
//    }
//
//    @GetMapping("/systems/warranty")
//    @ApiOperation(value = "质保期各系统故障占比")
//    @OperationLog()
//    public Result<List<PieChartVO>> countSysFaultWarrantyByYear(@Validated FaultCountQueryVO queryVO) throws Exception {
//        List<PieChartVO> pieChartVOList = buFaultCountReportService.countSysFaultWarranty(queryVO);
//        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
//    }
//
//    @GetMapping("/trains/repair")
//    @ApiOperation(value = "架修期各车故障占比")
//    @OperationLog()
//    public Result<List<PieChartVO>> countTrainFaultRepairByYear(@Validated FaultCountQueryVO queryVO) throws Exception {
//        List<PieChartVO> pieChartVOList = buFaultCountReportService.countTrainFaultRepair(queryVO);
//        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
//    }
//
//    @GetMapping("/trains/warranty")
//    @ApiOperation(value = "质保期各车故障占比")
//    @OperationLog()
//    public Result<List<PieChartVO>> countTrainFaultWarrantyByYear(@Validated FaultCountQueryVO queryVO) throws Exception {
//        List<PieChartVO> pieChartVOList = buFaultCountReportService.countTrainFaultWarranty(queryVO);
//        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
//    }
//
//    @GetMapping("/export")
//    @ApiOperation(value = "故障统计汇总导出")
//    public Result<Boolean> exportFaultCount(@Validated FaultCountQueryVO queryVO,
//                                            HttpServletResponse httpServletResponse) {
//        Result<Boolean> result = new Result<>();
//        try {
//            Workbook workbook = buFaultCountReportService.exportFaultCount(year);
//            String fileName = workbook.getWorksheets().get(0).getName();
//
//            httpServletResponse.setContentType("application/vnd.ms-excel");
//            httpServletResponse.setHeader("Content-disposition", String.format("attachment;filename=%s.xls", new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1")));
//            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
//            workbook.saveToStream(outputStream);
//            outputStream.flush();
//            outputStream.close();
//
//            return null;
//        } catch (JeecgBootException je) {
//            log.error(je.getMessage(), je);
//            result.error500(je.getMessage());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            result.error500("操作失败");
//        }
//        return result;
//    }

}

