package org.jeecg.modules.report.fault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.modules.report.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.report.fault.bean.BuFaultInfo;
import org.jeecg.modules.report.fault.bean.vo.FaultDetailQueryVO;
import org.jeecg.modules.report.fault.mapper.BuFaultInfoReportMapper;
import org.jeecg.modules.report.fault.service.BuFaultDetailReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Slf4j
@Service
public class BuFaultDetailReportServiceImpl extends ServiceImpl<BuFaultInfoReportMapper, BuFaultInfo> implements BuFaultDetailReportService {

    @Resource
    private BuFaultInfoReportMapper buFaultInfoReportMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see BuFaultDetailReportService#listFault(FaultDetailQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultInfo> listFault(FaultDetailQueryVO queryVO) throws Exception {
        queryVO.toStartEndDate();
        // 故障明细信息
        List<BuFaultInfo> faultInfoList = buFaultInfoReportMapper.selectListByCondition(queryVO);
        extractHandleDesc(faultInfoList);

        Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            for (BuFaultInfo faultInfo : faultInfoList) {
                faultInfo.setReportUserName(userIdNameMap.get(faultInfo.getReportUserId()));
                faultInfo.setDutyEngineerName(userIdNameMap.get(faultInfo.getDutyEngineer()));
                faultInfo.setCloseUserName(userIdNameMap.get(faultInfo.getCloseUserId()));
            }
        }

        return faultInfoList;
    }

    /**
     * @see BuFaultDetailReportService#exportFaultDetail(FaultDetailQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public HSSFWorkbook exportFaultDetail(FaultDetailQueryVO queryVO) throws Exception {
        queryVO.toStartEndDate();
        // 故障明细信息
        List<BuFaultInfo> faultInfoList = buFaultInfoReportMapper.selectListByCondition(queryVO);
        extractHandleDesc(faultInfoList);

        return getExcel(faultInfoList);
    }


    private void extractHandleDesc(List<BuFaultInfo> faultInfoList) {
        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            for (BuFaultInfo faultInfo : faultInfoList) {
                List<BuFaultHandleRecord> handleRecordList = faultInfo.getHandleRecordList();
                if (CollectionUtils.isNotEmpty(handleRecordList)) {
                    String solutionDescJoin = handleRecordList.stream()
                            .map(BuFaultHandleRecord::getSolutionDesc)
                            .collect(Collectors.joining(";"));
                    String handlePerson = handleRecordList.stream()
                            .map(BuFaultHandleRecord::getSolutionUserName)
                            .collect(Collectors.joining("、"));
                    Date lastSolutionTime = handleRecordList.stream()
                            .map(BuFaultHandleRecord::getSolutionTime)
                            .max(Comparator.comparing(Date::getTime))
                            .orElse(null);

                    faultInfo.setHandleDesc(solutionDescJoin);
                    faultInfo.setHandlePerson(handlePerson);
                    faultInfo.setHandleTime(lastSolutionTime);
                }
            }
        }
    }

    private HSSFWorkbook getExcel(List<BuFaultInfo> faultInfoList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String[] excelHeader = {
                "序号",
                "故障分类",
                "发生日期",
                "车号",
                "所属系统",
                "所属工位",
                "故障位置",
                "车辆故障详细描述",
                "车辆故障详细处理措施",
                "故障等级",
                "故障影响",
                "完成情况",
                "故障类别",
                "关闭日期",
                "处理人",
                "更换部件名称",
                "是否有责",
                "是否委外件故障",
                "规程内要求情况"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("故障明细-" + nowTimeString);
        HSSFRow sheetRow = sheet.createRow(0);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 单元格格式，水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell.setCellValue(header);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        AtomicInteger index = new AtomicInteger(1);
        for (BuFaultInfo fault : faultInfoList) {
            int indexInt = index.getAndIncrement();
            HSSFRow row = sheet.createRow(indexInt);
            row.createCell(0).setCellValue(indexInt);
            row.createCell(1).setCellValue(getPhaseStr(fault.getPhase()));
            row.createCell(2).setCellValue(dateFormat.format(fault.getHappenTime()));
            row.createCell(3).setCellValue(fault.getTrainNo());
            row.createCell(4).setCellValue(fault.getSysName());
            row.createCell(5).setCellValue(fault.getWorkstationName());
            row.createCell(6).setCellValue(fault.getFaultAssetName());
            row.createCell(7).setCellValue(fault.getFaultDesc());
            row.createCell(8).setCellValue(fault.getHandleDesc());
            row.createCell(9).setCellValue(getLevelStr(fault.getFaultLevel()));
            row.createCell(10).setCellValue("");
            row.createCell(11).setCellValue(getStatusStr(fault.getStatus()));
            row.createCell(12).setCellValue(fault.getReportGroupName());
            row.createCell(13).setCellValue(null == fault.getCloseTime() ? "" : dateFormat.format(fault.getCloseTime()));
            row.createCell(14).setCellValue(getLatestHandleUser(fault.getHandleRecordList()));
            row.createCell(15).setCellValue("");
            row.createCell(16).setCellValue(1 == fault.getHasDuty() ? "是" : "否");
            row.createCell(17).setCellValue(1 == fault.getOutsource() ? "是" : "否");
            row.createCell(18).setCellValue("");
        }

        // 设置自动列宽
        ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());

        return workbook;
    }

    private String getLatestHandleUser(List<BuFaultHandleRecord> handleRecordList) {
        String name = "";
        if (CollectionUtils.isNotEmpty(handleRecordList)) {
            handleRecordList.sort(Comparator.comparing(BuFaultHandleRecord::getSolutionTime, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            name = handleRecordList.get(0).getSolutionUserName();
        }
        return name;
    }

    private String getPhaseStr(Integer phase) {
        String phaseStr = "";
        if (phase != null) {
            // 1架修期 2质保期 3出保期 4大修期
            if (phase == 1) {
                phaseStr = "架修期";
            }
            if (phase == 2) {
                phaseStr = "质保期";
            }
            if (phase == 3) {
                phaseStr = "出保期";
            }
            if (phase == 4) {
                phaseStr = "大修期";
            }
        }
        return phaseStr;
    }

    private String getLevelStr(Integer level) {
        String phaseStr = "";
        if (level != null) {
            // 1A 2B 3C 4D
            if (level == 1) {
                phaseStr = "A";
            }
            if (level == 2) {
                phaseStr = "B";
            }
            if (level == 3) {
                phaseStr = "C";
            }
            if (level == 4) {
                phaseStr = "D";
            }
        }
        return phaseStr;
    }

    private String getStatusStr(Integer phase) {
        String phaseStr = "";
        if (phase != null) {
            // 0未处理 1已处理 2已关闭
            if (phase == 0) {
                phaseStr = "未处理";
            }
            if (phase == 1) {
                phaseStr = "已处理";
            }
            if (phase == 2) {
                phaseStr = "已关闭";
            }
        }
        return phaseStr;
    }

}
