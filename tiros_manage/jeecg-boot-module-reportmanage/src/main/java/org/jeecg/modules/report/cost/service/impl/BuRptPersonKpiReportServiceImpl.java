package org.jeecg.modules.report.cost.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.modules.report.cost.bean.BuRptPersonKpi;
import org.jeecg.modules.report.cost.bean.vo.BuKpiTimeItemVO;
import org.jeecg.modules.report.cost.bean.vo.KpiQueryVO;
import org.jeecg.modules.report.cost.mapper.BuRptPersonKpiReportMapper;
import org.jeecg.modules.report.cost.service.BuRptPersonKpiReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 个人绩效 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
@Service
public class BuRptPersonKpiReportServiceImpl extends ServiceImpl<BuRptPersonKpiReportMapper, BuRptPersonKpi> implements BuRptPersonKpiReportService {

    @Resource
    private BuRptPersonKpiReportMapper buRptPersonKpiReportMapper;


    /**
     * @see BuRptPersonKpiReportService#pageTimeKpi(KpiQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuKpiTimeItemVO> pageTimeKpi(KpiQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        List<BuKpiTimeItemVO> timeList = listTimeKpi(queryVO);
        if (CollectionUtils.isEmpty(timeList)) {
            return new Page<>();
        }

        return transToPage(timeList, pageNo, pageSize);
    }

    /**
     * @see BuRptPersonKpiReportService#exportTimeKpi(KpiQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public HSSFWorkbook exportTimeKpi(KpiQueryVO queryVO) throws Exception {
        List<BuKpiTimeItemVO> timeList = listTimeKpi(queryVO);

        return getExcel(timeList, queryVO.getType());
    }


    private List<BuKpiTimeItemVO> listTimeKpi(KpiQueryVO queryVO) {
        if (null == queryVO.getType()) {
            queryVO.setType(1);
        }
        if (null != queryVO.getEndDate()) {
            queryVO.setEndDate(DateUtils.transToDayEnd(queryVO.getEndDate()));
        }

        List<BuRptPersonKpi> kpiList = buRptPersonKpiReportMapper.selectListForTimeByKpiQueryVO(queryVO);
        if (CollectionUtils.isEmpty(kpiList)) {
            return new ArrayList<>();
        }

        return transToTimeList(kpiList, queryVO.getType());
    }

    private List<BuKpiTimeItemVO> transToTimeList(List<BuRptPersonKpi> kpiList, int type) {
        List<BuKpiTimeItemVO> timeList = new ArrayList<>();

        for (BuRptPersonKpi kpi : kpiList) {
            BuKpiTimeItemVO timeItem = getOrGenerateTimeItemVO(kpi, timeList, type);

            int totalTime = kpi.getTotalTime();
            int repairTime = kpi.getRepairTime();
            int faultTime = kpi.getFaultTime();

            timeItem.setTotalTime(timeItem.getTotalTime() + totalTime)
                    .setRepairTime(timeItem.getRepairTime() + repairTime)
                    .setFaultTime(timeItem.getFaultTime() + faultTime);
        }

        timeList.sort(Comparator.comparing(BuKpiTimeItemVO::getTotalTime).reversed()
                .thenComparing(BuKpiTimeItemVO::getLineName)
                .thenComparing(BuKpiTimeItemVO::getTrainNo)
                .thenComparing(BuKpiTimeItemVO::getGroupName)
                .thenComparing(BuKpiTimeItemVO::getWorkNo, Comparator.nullsLast(Comparator.naturalOrder())));
        for (int i = 0; i < timeList.size(); i++) {
            timeList.get(i).setRealSortNo(i + 1);
        }

        return timeList;
    }

    private BuKpiTimeItemVO getOrGenerateTimeItemVO(BuRptPersonKpi kpi, List<BuKpiTimeItemVO> timeList, int type) {
        for (BuKpiTimeItemVO timeItemVO : timeList) {
            boolean sameLine = kpi.getLineId().equals(timeItemVO.getLineId());
            boolean sameTrain = kpi.getTrainNo().equals(timeItemVO.getTrainNo());
            boolean sameGroup = kpi.getGroupId().equals(timeItemVO.getGroupId());
            boolean sameUser = 2 == type || kpi.getUserId().equals(timeItemVO.getUserId());
            if (sameLine && sameTrain && sameGroup && sameUser) {
                return timeItemVO;
            }
        }

        BuKpiTimeItemVO timeItemVO = new BuKpiTimeItemVO()
                .setLineId(kpi.getLineId())
                .setLineName(kpi.getLineName())
                .setTrainNo(kpi.getTrainNo())
                .setGroupId(kpi.getGroupId())
                .setGroupName(kpi.getGroupName())
                .setTotalTime(0)
                .setRepairTime(0)
                .setFaultTime(0);
        if (1 == type) {
            timeItemVO.setUserId(kpi.getUserId())
                    .setUserName(kpi.getUserName())
                    .setWorkNo(kpi.getWorkNo());
        }

        timeList.add(timeItemVO);
        return timeItemVO;
    }

    private Page<BuKpiTimeItemVO> transToPage(List<BuKpiTimeItemVO> timeList, int pageNo, int pageSize) {
        List<BuKpiTimeItemVO> pageList = timeList.stream()
                .skip((long) pageSize * (pageNo - 1))
                .limit(pageSize)
                .collect(Collectors.toList());
        Page<BuKpiTimeItemVO> page = new Page<>();
        page.setRecords(pageList);
        page.setCurrent(pageNo);
        page.setPages(pageSize);
        page.setTotal(timeList.size());
        return page;
    }

    private HSSFWorkbook getExcel(List<BuKpiTimeItemVO> timeList, int type) {
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        List<String> excelHeaderList = new ArrayList<>();
        if (1 == type) {
            String[] excelHeader = {
                    "线路",
                    "车辆",
                    "工号",
                    "姓名",
                    "班组",
                    "维修工时",
                    "故障处理工时",
                    "总计工时"
            };
            excelHeaderList = Arrays.asList(excelHeader);
        }
        if (2 == type) {
            String[] excelHeader = {
                    "线路",
                    "车辆",
                    "班组",
                    "维修工时",
                    "故障处理工时",
                    "总计工时"
            };
            excelHeaderList = Arrays.asList(excelHeader);
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
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

        for (BuKpiTimeItemVO time : timeList) {
            if (null == time) {
                continue;
            }

            HSSFRow row = sheet.createRow(time.getRealSortNo());
            if (1 == type) {
                setUserRow(time, row);
            }
            if (2 == type) {
                setGroupRow(time, row);
            }
        }

        // 设置自动列宽
        ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());

        return workbook;
    }

    private void setUserRow(BuKpiTimeItemVO time, HSSFRow row) {
        row.createCell(0).setCellValue(time.getLineName());
        row.createCell(1).setCellValue(time.getTrainNo());
        row.createCell(2).setCellValue(time.getWorkNo());
        row.createCell(3).setCellValue(time.getUserName());
        row.createCell(4).setCellValue(time.getGroupName());
        row.createCell(5).setCellValue(null == time.getRepairTime() ? 0 : time.getRepairTime());
        row.createCell(6).setCellValue(null == time.getFaultTime() ? 0 : time.getFaultTime());
        row.createCell(7).setCellValue(null == time.getTotalTime() ? 0 : time.getTotalTime());
    }

    private void setGroupRow(BuKpiTimeItemVO time, HSSFRow row) {
        row.createCell(0).setCellValue(time.getLineName());
        row.createCell(1).setCellValue(time.getTrainNo());
        row.createCell(2).setCellValue(time.getGroupName());
        row.createCell(3).setCellValue(null == time.getRepairTime() ? 0 : time.getRepairTime());
        row.createCell(4).setCellValue(null == time.getFaultTime() ? 0 : time.getFaultTime());
        row.createCell(5).setCellValue(null == time.getTotalTime() ? 0 : time.getTotalTime());
    }

}
