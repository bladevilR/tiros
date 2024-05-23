package org.jeecg.modules.report.fault.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.modules.report.fault.bean.BuFaultInfo;
import org.jeecg.modules.report.fault.bean.BuRptBoardSysFault;
import org.jeecg.modules.report.fault.bean.BuRptBoardTrainFault;
import org.jeecg.modules.report.fault.bean.vo.FaultCountQueryVO;
import org.jeecg.modules.report.fault.bean.vo.FaultSysCountVO;
import org.jeecg.modules.report.fault.bean.vo.FaultTrainCountVO;
import org.jeecg.modules.report.fault.mapper.BuFaultInfoReportMapper;
import org.jeecg.modules.report.fault.mapper.BuRptBoardSysFaultReportMapper;
import org.jeecg.modules.report.fault.mapper.BuRptBoardTrainFaultReportMapper;
import org.jeecg.modules.report.fault.service.BuFaultCountReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 故障汇总 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-12
 */
@Slf4j
@Service
public class BuFaultCountReportServiceImpl extends ServiceImpl<BuRptBoardTrainFaultReportMapper, BuRptBoardTrainFault> implements BuFaultCountReportService {

    @Resource
    private BuRptBoardTrainFaultReportMapper buRptBoardTrainFaultReportMapper;
    @Resource
    private BuRptBoardSysFaultReportMapper buRptBoardSysFaultReportMapper;
    @Resource
    private BuFaultInfoReportMapper buFaultInfoReportMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuFaultCountReportService#countTrainFault(FaultCountQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<FaultTrainCountVO> countTrainFault(FaultCountQueryVO queryVO) throws Exception {
//        queryVO.toYearMonthList();
//        List<BuRptBoardTrainFault> trainFaultList = buRptBoardTrainFaultReportMapper.selectListByFaultCountQueryVO(queryVO);
        if (null != queryVO.getStartDate()) {
            queryVO.setStartDate(DateUtil.beginOfDay(queryVO.getStartDate()));
        }
        if (null != queryVO.getEndDate()) {
            queryVO.setEndDate(DateUtil.endOfDay(queryVO.getEndDate()));
        }
        List<BuFaultInfo> faultList = buFaultInfoReportMapper.selectListByFaultCountQueryVO(queryVO);
        List<BuFaultInfo> collect = faultList.stream()
                .filter(item -> "0111".equals(item.getTrainNo()))
                .collect(Collectors.toList());
        System.out.println(collect);

        return addTotal(getTrainCountVOList(faultList));
    }

    /**
     * @see BuFaultCountReportService#countSysFault(FaultCountQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<FaultSysCountVO> countSysFault(FaultCountQueryVO queryVO) throws Exception {
//        queryVO.toYearMonthList();
//        List<BuRptBoardSysFault> sysFaultList = buRptBoardSysFaultReportMapper.selectListByFaultCountQueryVO(queryVO);
        if (null != queryVO.getStartDate()) {
            queryVO.setStartDate(DateUtil.beginOfDay(queryVO.getStartDate()));
        }
        if (null != queryVO.getEndDate()) {
            queryVO.setEndDate(DateUtil.endOfDay(queryVO.getEndDate()));
        }
        List<BuFaultInfo> faultList = buFaultInfoReportMapper.selectListByFaultCountQueryVO(queryVO);

        return getSysCountVOList(faultList);
    }

//    /**
//     * @see BuFaultCountReportService#countRepairTrainFault(FaultCountQueryVO)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public List<FaultTrainCountVO> countRepairTrainFault(FaultCountQueryVO queryVO) throws Exception {
//        queryVO.toYearMonthList();
//        List<BuRptBoardTrainFault> trainFaultList = buRptBoardTrainFaultReportMapper.selectListByFaultCountQueryVO(queryVO);
//        List<BuRptBoardTrainFault> middleRepairTrainFaultList = trainFaultList.stream()
//                .filter(fault -> Arrays.asList(1, 2).contains(fault.getProgramType()))
//                .collect(Collectors.toList());
//
//        return getTrainCountVOList(middleRepairTrainFaultList);
//    }
//
//    /**
//     * @see BuFaultCountReportService#countSysFaultRepair(FaultCountQueryVO)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public List<PieChartVO> countSysFaultRepair(FaultCountQueryVO queryVO) throws Exception {
//        queryVO.toYearMonthList();
//        List<BuRptBoardSysFault> sysFaultList = buRptBoardSysFaultReportMapper.selectListByFaultCountQueryVO(queryVO);
//
//        return getSysFaultPercentList(sysFaultList, 1);
//    }
//
//    /**
//     * @see BuFaultCountReportService#countSysFaultWarranty(FaultCountQueryVO)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public List<PieChartVO> countSysFaultWarranty(FaultCountQueryVO queryVO) throws Exception {
//        queryVO.toYearMonthList();
//        List<BuRptBoardSysFault> sysFaultList = buRptBoardSysFaultReportMapper.selectListByFaultCountQueryVO(queryVO);
//
//        return getSysFaultPercentList(sysFaultList, 2);
//    }
//
//    /**
//     * @see BuFaultCountReportService#countTrainFaultRepair(FaultCountQueryVO)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public List<PieChartVO> countTrainFaultRepair(FaultCountQueryVO queryVO) throws Exception {
//        queryVO.toYearMonthList();
//        List<BuRptBoardTrainFault> trainFaultList = buRptBoardTrainFaultReportMapper.selectListByFaultCountQueryVO(queryVO);
//
//        return getTrainFaultPercentList(trainFaultList, 1);
//    }
//
//    /**
//     * @see BuFaultCountReportService#countTrainFaultWarranty(FaultCountQueryVO)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public List<PieChartVO> countTrainFaultWarranty(FaultCountQueryVO queryVO) throws Exception {
//        queryVO.toYearMonthList();
//        List<BuRptBoardTrainFault> trainFaultList = buRptBoardTrainFaultReportMapper.selectListByFaultCountQueryVO(queryVO);
//
//        return getTrainFaultPercentList(trainFaultList, 2);
//    }
//
//    /**
//     * @see BuFaultCountReportService#exportFaultCount(Integer)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public Workbook exportFaultCount(FaultCountQueryVO queryVO) throws Exception {
//        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//
//        // 获取工作表
//        Workbook workbook = new Workbook();
//        Worksheet sheet = workbook.getWorksheets().get(0);
//        sheet.setName("故障汇总-" + nowTimeString);
//
//        // 开始添加数据
//        Integer row = 1;
//        int baseStartColumn = 2;
//        int rankStartColumn = 10;
//        int pieChartResetRowSize = 18;
//
//        // 列表数据
//        // 车辆故障统计
//        List<BuFaultTrainCountVO> trainCountList = countTrainFaultByYear(year);
//        row = addTrainFaultCountList(sheet, row, baseStartColumn, trainCountList);
//        // 系统故障统计
//        List<BuFaultSysCountVO> sysCountList = countSysFaultByYear(year);
//        row = addSysFaultCountList(sheet, row, baseStartColumn, sysCountList);
//
//        // 条形图数据
//        int barChartResetRowSize = 30;
//        List<BuFaultTrainCountVO> repairTrainCountList = countRepairTrainFaultByYear(year);
//        if (sysCountList.size() > barChartResetRowSize) {
//            barChartResetRowSize = sysCountList.size();
//        }
//        if (repairTrainCountList.size() > barChartResetRowSize) {
//            barChartResetRowSize = repairTrainCountList.size();
//        }
//        // 各系统故障分布(二维条形图)
//        row = addSysFaultCountChart(sheet, row, barChartResetRowSize, baseStartColumn, sysCountList);
//        // 架修车各类故障分布(二维条形图)
//        row = addRepairTrainFaultCountChart(sheet, row, barChartResetRowSize, rankStartColumn, repairTrainCountList);
//
//        // 饼图数据
//        // 架修期各系统故障占比(三维饼图)
//        List<PieChartVO> sysFaultRepairCountList = countSysFaultRepairByYear(year);
//        row = addPieChart(sheet, row, false, pieChartResetRowSize, baseStartColumn, sysFaultRepairCountList, true, "架修期各系统故障占比");
//        // 架修期各车故障占比(三维饼图)
//        List<PieChartVO> trainFaultRepairCountList = countTrainFaultRepairByYear(year);
//        row = addPieChart(sheet, row, true, pieChartResetRowSize, rankStartColumn, trainFaultRepairCountList, true, "架修期各车故障占比");
//        // 质保期各系统故障占比(三维饼图)
//        List<PieChartVO> sysFaultWarrantyCountList = countSysFaultWarrantyByYear(year);
//        row = addPieChart(sheet, row, false, pieChartResetRowSize, baseStartColumn, sysFaultWarrantyCountList, true, "质保期各系统故障占比");
//        // 质保期各车故障占比(三维饼图)
//        List<PieChartVO> trainFaultWarrantyCountList = countTrainFaultWarrantyByYear(year);
//        row = addPieChart(sheet, row, true, pieChartResetRowSize, rankStartColumn, trainFaultWarrantyCountList, true, "质保期各车故障占比");
//
//        return workbook;
//    }

//    private void setLineBorder(CellRange cellRange) {
//        cellRange.getBorders().setLineStyle(LineStyleType.Thin);
//        cellRange.getBorders().getByBordersLineType(BordersLineType.DiagonalDown).setLineStyle(LineStyleType.None);
//        cellRange.getBorders().getByBordersLineType(BordersLineType.DiagonalUp).setLineStyle(LineStyleType.None);
//        cellRange.getBorders().setKnownColor(ExcelColors.Black);
//    }
//
//    private Integer addTrainFaultCountList(Worksheet sheet, Integer row, int startColumn, List<BuFaultTrainCountVO> trainCountVOList) {
//        int startRow = row;
//        row = row + 2;
//        // 故障总数
//        BuFaultTrainCountVO totalCount = trainCountVOList.get(0);
//        addTrainFaultCount(sheet, row, startColumn, totalCount);
//        row++;
//        // 设置标题行
//        sheet.getCellRange(row, startColumn).setValue("车号");
//        sheet.getCellRange(row, startColumn + 1).setValue("架修期");
//        sheet.getCellRange(row, startColumn + 2).setValue("质保期");
//        sheet.getCellRange(row, startColumn + 3).setValue("质保期正线");
//        sheet.getCellRange(row, startColumn + 4).setValue("质保期B类以上");
//        sheet.getCellRange(row, startColumn + 5).setValue("质保期有责故障");
//        sheet.getCellRange(row, startColumn + 6).setValue("出质保故障数");
//        row++;
//        // 各车辆数据
//        trainCountVOList.remove(0);
//        for (BuFaultTrainCountVO trainCountVO : trainCountVOList) {
//            addTrainFaultCount(sheet, row, startColumn, trainCountVO);
//            row++;
//        }
//        // 设置边框
//        CellRange cellRange = sheet.getCellRange(startRow + 2, startColumn, row - 1, startColumn + 6);
//        setLineBorder(cellRange);
//
//        return row;
//    }
//
//    private void addTrainFaultCount(Worksheet sheet, Integer row, int startColumn, BuFaultTrainCountVO trainCountVO) {
//        sheet.getCellRange(row, startColumn).setValue(trainCountVO.getTrainNo());
//        sheet.getCellRange(row, startColumn + 1).setNumberValue(trainCountVO.getRepair());
//        sheet.getCellRange(row, startColumn + 2).setNumberValue(trainCountVO.getWarranty());
//        sheet.getCellRange(row, startColumn + 3).setNumberValue(trainCountVO.getWarrantyOnline());
//        sheet.getCellRange(row, startColumn + 4).setNumberValue(trainCountVO.getWarrantyAboveB());
//        sheet.getCellRange(row, startColumn + 5).setNumberValue(trainCountVO.getWarrantyHasDuty());
//        sheet.getCellRange(row, startColumn + 6).setNumberValue(trainCountVO.getExpireWarranty());
//    }
//
//    private Integer addSysFaultCountList(Worksheet sheet, Integer row, int startColumn, List<BuFaultSysCountVO> sysCountVOList) {
//        int startRow = row;
//        row = row + 2;
//        // 设置标题行
//        sheet.getCellRange(row, startColumn).setValue("系统");
//        sheet.getCellRange(row, startColumn + 1).setValue("架修期");
//        sheet.getCellRange(row, startColumn + 2).setValue("质保期");
//        row++;
//        // 各系统数据
//        for (BuFaultSysCountVO sysCountVO : sysCountVOList) {
//            addSysFaultCount(sheet, row, startColumn, sysCountVO);
//            row++;
//        }
//        // 设置边框
//        CellRange cellRange = sheet.getCellRange(startRow + 2, startColumn, row - 1, startColumn + 2);
//        setLineBorder(cellRange);
//
//        return row;
//    }
//
//    private Integer addSysFaultCountChart(Worksheet sheet, Integer row, int resetSize, int startColumn, List<BuFaultSysCountVO> sysCountVOList) {
//        int startRow = row;
//        row = row + 2;
//        // 设置标题行
//        sheet.getCellRange(row, startColumn).setValue("系统");
//        sheet.getCellRange(row, startColumn + 1).setValue("架修期");
//        sheet.getCellRange(row, startColumn + 2).setValue("质保期");
//        row++;
//        int dataSize = sysCountVOList.size();
//        // 各系统数据
//        for (BuFaultSysCountVO sysCountVO : sysCountVOList) {
//            addSysFaultCount(sheet, row, startColumn, sysCountVO);
//            row++;
//        }
//        // 添加二维条形图
//        addBarChart(sheet, startRow, resetSize, startColumn, 2, dataSize, "各系统故障分布");
//
//        return startRow + resetSize;
//    }
//
//    private void addSysFaultCount(Worksheet sheet, Integer row, int startColumn, BuFaultSysCountVO sysCountVO) {
//        sheet.getCellRange(row, startColumn).setValue(sysCountVO.getSys());
//        sheet.getCellRange(row, startColumn + 1).setNumberValue(sysCountVO.getRepair());
//        sheet.getCellRange(row, startColumn + 2).setNumberValue(sysCountVO.getWarranty());
//    }
//
//    private Integer addRepairTrainFaultCountChart(Worksheet sheet, Integer row, int resetSize, int startColumn, List<BuFaultTrainCountVO> repairTrainCountVOList) {
//        row = row - resetSize;
//        int startRow = row;
//        row = row + 2;
//        // 设置标题行
//        sheet.getCellRange(row, startColumn).setValue("车号");
//        sheet.getCellRange(row, startColumn + 1).setValue("架修期");
//        sheet.getCellRange(row, startColumn + 2).setValue("质保期");
//        sheet.getCellRange(row, startColumn + 3).setValue("质保期正线");
//        sheet.getCellRange(row, startColumn + 4).setValue("质保期B类以上");
//        sheet.getCellRange(row, startColumn + 5).setValue("质保期有责故障");
//        sheet.getCellRange(row, startColumn + 6).setValue("出质保故障数");
//        row++;
//        int dataSize = repairTrainCountVOList.size();
//        // 各车辆数据
//        for (BuFaultTrainCountVO trainCountVO : repairTrainCountVOList) {
//            addTrainFaultCount(sheet, row, startColumn, trainCountVO);
//            row++;
//        }
//        // 添加二维条形图
//        addBarChart(sheet, startRow, resetSize, startColumn, 6, dataSize, "架修车各类故障分布");
//
//        return startRow + resetSize;
//    }
//
//    private void addBarChart(Worksheet sheet, int startRow, int resetSize, int startColumn, int columnOffset, int dataSize, String chartTitle) {
//        Chart barChart = sheet.getCharts().add(ExcelChartType.BarClustered);
//        // 设置图表数据区域
//        barChart.setDataRange(sheet.getCellRange(startRow + 2, startColumn, startRow + 2 + dataSize, startColumn + columnOffset));
//        barChart.setSeriesDataFromRange(false);
//        // 设置图表位置
//        barChart.setLeftColumn(startColumn);
//        barChart.setTopRow(startRow + 2);
//        barChart.setRightColumn(startColumn + 7);
//        barChart.setBottomRow(startRow + resetSize);
//        // 图表可访问
//        barChart.setVisible(true);
//        // 图表绘图区可操作
////        barChart.getPlotArea().setBackGroundKnownColor(ExcelColors.White);
//        barChart.getPlotArea().setForeGroundKnownColor(ExcelColors.White);
//        barChart.getPlotArea().setVisible(true);
//        barChart.getPlotArea().isAutoSize(true);
//        // 设置图表标题
//        barChart.setChartTitle(chartTitle);
//        barChart.getChartTitleArea().isBold(true);
//        barChart.getChartTitleArea().setSize(12);
//        // 设置显示图例
//        barChart.getLegend().setIncludeInLayout(true);
//        barChart.setDisplayLegendFieldButtons(true);
//        barChart.hasLegend(true);
//        // 设置系列标签
//        ChartSerie barChartSeries = barChart.getSeries().get(0);
//        // 位置
//        barChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().setPosition(DataLabelPositionType.Outside);
//        // 引线
//        barChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().setShowLeaderLines(true);
//        // 外部标注
//        barChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasWedgeCallout(true);
//        // 系列名称
////        barChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasSeriesName(true);
//        // 类别名称
////        barChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasCategoryName(true);
//        // 图例标示(颜色块)
//        barChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasLegendKey(true);
//        // 值
//        barChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasValue(true);
//        // 百分比
////        barChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasPercentage(true);
//    }
//
//    private Integer addPieChart(Worksheet sheet, Integer row, boolean resetRow, int resetSize, int startColumn, List<PieChartVO> pieChartVOList, boolean filterZeroData, String chartTitle) {
//        if (resetRow) {
//            row = row - resetSize;
//        }
//        int startRow = row;
//        row = row + 2;
//        // 数据
//        int dataSize = 0;
//        for (PieChartVO pieChartVO : pieChartVOList) {
//            if (filterZeroData && pieChartVO.getCount() != null && pieChartVO.getCount() != 0D) {
//                sheet.getCellRange(row, startColumn).setValue(pieChartVO.getItem());
//                sheet.getCellRange(row, startColumn + 1).setNumberValue(pieChartVO.getCount());
//                row++;
//                dataSize++;
//            }
//        }
//
//        // 添加三维饼图
//        Chart pieChart = sheet.getCharts().add(ExcelChartType.Pie3D);
//        // 设置图表数据区域
//        pieChart.setDataRange(sheet.getCellRange(startRow + 2, startColumn, startRow + 2 + dataSize - 1, startColumn + 1));
//        pieChart.setSeriesDataFromRange(false);
//        // 设置图表位置
//        pieChart.setLeftColumn(startColumn);
//        pieChart.setTopRow(startRow + 2);
//        pieChart.setRightColumn(startColumn + 7);
//        pieChart.setBottomRow(startRow + resetSize);
//        // 图表可访问
//        pieChart.setVisible(true);
//        // 图表绘图区可操作
//        pieChart.getPlotArea().setVisible(true);
//        pieChart.getPlotArea().isAutoSize(true);
//        // 设置图表标题
//        pieChart.setChartTitle(chartTitle);
//        pieChart.getChartTitleArea().isBold(true);
//        pieChart.getChartTitleArea().setSize(12);
//        // 设置不显示图例
////        pieChart.getLegend().setIncludeInLayout(false);
//        pieChart.setDisplayLegendFieldButtons(false);
//        pieChart.hasLegend(false);
//        // 设置系列标签
//        ChartSerie pieChartSeries = pieChart.getSeries().get(0);
//        // 位置
//        pieChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().setPosition(DataLabelPositionType.BestFit);
//        // 引线
//        pieChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().setShowLeaderLines(true);
//        // 外部标注
//        pieChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasWedgeCallout(true);
//        // 系列名称
////        pieChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasSeriesName(true);
//        // 类别名称
//        pieChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasCategoryName(true);
//        // 图例标示(颜色块)
//        pieChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasLegendKey(true);
//        // 值
////        pieChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasValue(true);
//        // 百分比
//        pieChartSeries.getDataPoints().getDefaultDataPoint().getDataLabels().hasPercentage(true);
//
//        return startRow + resetSize;
//    }

    private String getLongTrainNo(String trainNo) {
//        if (repairIndex == 0) {
//            repairIndex += 1;
//        }
//        return "第" + repairIndex + "列" + trainNo + "车故障数";
        return trainNo + "车";
    }

    private List<FaultTrainCountVO> addTotal(List<FaultTrainCountVO> countVOList) {
        FaultTrainCountVO total = new FaultTrainCountVO()
                .setTrainNo("故障总数")
                .setRepair(countVOList.stream().mapToInt(FaultTrainCountVO::getRepair).sum())
                .setWarranty(countVOList.stream().mapToInt(FaultTrainCountVO::getWarranty).sum())
                .setWarrantyOnline(countVOList.stream().mapToInt(FaultTrainCountVO::getWarrantyOnline).sum())
                .setWarrantyAB(countVOList.stream().mapToInt(FaultTrainCountVO::getWarrantyAB).sum())
                .setWarrantyAboveB(countVOList.stream().mapToInt(FaultTrainCountVO::getWarrantyAboveB).sum())
                .setWarrantyHasDuty(countVOList.stream().mapToInt(FaultTrainCountVO::getWarrantyHasDuty).sum())
                .setExpireWarranty(countVOList.stream().mapToInt(FaultTrainCountVO::getExpireWarranty).sum())
                .setTrainSort("0000");

        countVOList.sort(Comparator.comparing(FaultTrainCountVO::getTrainSort, Comparator.nullsLast(Comparator.naturalOrder())));
        countVOList.add(0, total);

        return countVOList;
    }

    //    private List<FaultTrainCountVO> getTrainCountVOList(List<BuRptBoardTrainFault> trainFaultList) {
//        List<FaultTrainCountVO> countVOList = new ArrayList<>();
//
//        Map<String, List<BuRptBoardTrainFault>> trainNoFaultListMap = trainFaultList.stream().collect(Collectors.groupingBy(BuRptBoardTrainFault::getTrainNo));
//        for (Map.Entry<String, List<BuRptBoardTrainFault>> trainNoFaultListEntry : trainNoFaultListMap.entrySet()) {
//            String trainNo = trainNoFaultListEntry.getKey();
//            List<BuRptBoardTrainFault> faultList = trainNoFaultListEntry.getValue();
//
//            int repair = faultList.stream().mapToInt(BuRptBoardTrainFault::getSum1).sum();
//            int warranty = faultList.stream().mapToInt(BuRptBoardTrainFault::getSum3).sum();
//            int warrantyOnline = faultList.stream().mapToInt(BuRptBoardTrainFault::getSum4).sum();
//            int warrantyAB = faultList.stream().mapToInt(BuRptBoardTrainFault::getSum5).sum();
//            int warrantyHasDuty = faultList.stream().mapToInt(BuRptBoardTrainFault::getSum6).sum();
//            int expireWarranty = faultList.stream().mapToInt(BuRptBoardTrainFault::getSum7).sum();
//            Integer repairIndex = faultList.get(0).getRepairIndex();
//
//            FaultTrainCountVO countVO = new FaultTrainCountVO()
//                    .setTrainNo(getLongTrainNo(trainNo, repairIndex))
//                    .setRepair(repair)
//                    .setWarranty(warranty)
//                    .setWarrantyOnline(warrantyOnline)
//                    .setWarrantyAB(warrantyAB)
//                    .setWarrantyAboveB(warrantyAB)
//                    .setWarrantyHasDuty(warrantyHasDuty)
//                    .setExpireWarranty(expireWarranty)
//                    .setRepairIndex(repairIndex);
//            countVOList.add(countVO);
//        }
//
//        countVOList.sort(Comparator.comparing(FaultTrainCountVO::getRepairIndex, Comparator.nullsLast(Comparator.naturalOrder())));
//        return countVOList;
//    }
//
//    private List<FaultSysCountVO> getSysCountVOList(List<BuRptBoardSysFault> sysFaultList) {
//        List<FaultSysCountVO> countVOList = new ArrayList<>();
//
//        Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(null);
//        for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
//            String sysId = sysEntry.getKey();
//            BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
//            String sysShortName = sysAssetTypeBO.getShortName();
//
//            List<BuRptBoardSysFault> faultList = sysFaultList.stream()
//                    .filter(fault -> sysId.equals(fault.getSysId()))
//                    .collect(Collectors.toList());
//            int repair = faultList.stream().mapToInt(BuRptBoardSysFault::getSum1).sum();
//            int warranty = faultList.stream().mapToInt(BuRptBoardSysFault::getSum3).sum();
//
//            FaultSysCountVO countVO = new FaultSysCountVO()
//                    .setSys(sysShortName)
//                    .setRepair(repair)
//                    .setWarranty(warranty);
//            countVOList.add(countVO);
//        }
//
//        countVOList.sort(Comparator.comparing(FaultSysCountVO::getSys, Comparator.nullsLast(Comparator.naturalOrder())));
//        return countVOList;
//    }
    private List<FaultTrainCountVO> getTrainCountVOList(List<BuFaultInfo> faultList) {
        Map<String, FaultTrainCountVO> trainNoCountMap = new HashMap<>();
        for (BuFaultInfo fault : faultList) {
            String trainNo = fault.getTrainNo();

            int repair = 0;
            int warranty = 0;
            int warrantyOnline = 0;
            int warrantyAB = 0;
            int warrantyHasDuty = 0;
            int expireWarranty = 0;

            int phase = null == fault.getPhase() ? -1 : fault.getPhase();
            int level = null == fault.getFaultLevel() ? -1 : fault.getFaultLevel();
            int online = null == fault.getFaultOnline() ? -1 : fault.getFaultOnline();
            int hasDuty = null == fault.getHasDuty() ? -1 : fault.getHasDuty();
            if (phase == 1 || phase == 4) {
                repair = 1;
            } else if (phase == 2) {
                warranty = 1;
                if (online == 1) {
                    warrantyOnline = 1;
                }
                if (level == 1 || level == 2) {
                    warrantyAB = 1;
                }
                if (hasDuty == 1) {
                    warrantyHasDuty = 1;
                }
            } else if (phase == 3) {
                expireWarranty = 1;
            }

            FaultTrainCountVO countVO = trainNoCountMap.get(trainNo);
            if (null == countVO) {
                countVO = new FaultTrainCountVO()
                        .setTrainNo(getLongTrainNo(trainNo))
                        .setRepair(repair)
                        .setWarranty(warranty)
                        .setWarrantyOnline(warrantyOnline)
                        .setWarrantyAB(warrantyAB)
                        .setWarrantyAboveB(warrantyAB)
                        .setWarrantyHasDuty(warrantyHasDuty)
                        .setExpireWarranty(expireWarranty)
                        .setTrainSort(trainNo);
            } else {
                countVO.setRepair(countVO.getRepair() + repair)
                        .setWarranty(countVO.getWarranty() + warranty)
                        .setWarrantyOnline(countVO.getWarrantyOnline() + warrantyOnline)
                        .setWarrantyAB(countVO.getWarrantyAB() + warrantyAB)
                        .setWarrantyAboveB(countVO.getWarrantyAboveB() + warrantyAB)
                        .setWarrantyHasDuty(countVO.getWarrantyHasDuty() + warrantyHasDuty)
                        .setExpireWarranty(countVO.getExpireWarranty() + expireWarranty);
            }
            trainNoCountMap.put(trainNo, countVO);
        }

        List<FaultTrainCountVO> countVOList = new ArrayList<>(trainNoCountMap.values());
        countVOList.sort(Comparator.comparing(FaultTrainCountVO::getTrainSort, Comparator.nullsLast(Comparator.naturalOrder())));
        return countVOList;
    }

    private List<FaultSysCountVO> getSysCountVOList(List<BuFaultInfo> faultList) {
        List<FaultSysCountVO> countVOList = new ArrayList<>();

        Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(null);
        for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
            String sysId = sysEntry.getKey();
            BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
            String sysShortName = sysAssetTypeBO.getShortName();

            int repair = 0;
            int warranty = 0;
            for (BuFaultInfo fault : faultList) {
                if (sysId.equals(fault.getSysId())) {
                    int phase = null == fault.getPhase() ? -1 : fault.getPhase();

                    if (phase == 1 || phase == 4) {
                        repair++;
                    } else if (phase == 2) {
                        warranty++;
                    }
                }
            }

            FaultSysCountVO countVO = new FaultSysCountVO()
                    .setSys(sysShortName)
                    .setRepair(repair)
                    .setWarranty(warranty);
            countVOList.add(countVO);
        }

        countVOList.sort(Comparator.comparing(FaultSysCountVO::getSys, Comparator.nullsLast(Comparator.naturalOrder())));
        return countVOList;
    }

    private List<PieChartVO> getTrainFaultPercentList(List<BuRptBoardTrainFault> trainFaultList, int period) {
        Map<String, Integer> trainNoCountMap = new HashMap<>();

        Map<String, List<BuRptBoardTrainFault>> trainNoFaultListMap = trainFaultList.stream().collect(Collectors.groupingBy(BuRptBoardTrainFault::getTrainNo));
        for (Map.Entry<String, List<BuRptBoardTrainFault>> trainNoFaultListEntry : trainNoFaultListMap.entrySet()) {
            String trainNo = trainNoFaultListEntry.getKey();
            List<BuRptBoardTrainFault> faultList = trainNoFaultListEntry.getValue();

            Integer repairIndex = faultList.get(0).getRepairIndex();
            int faultCount = 0;
            if (1 == period) {
                // 1架修期
                faultCount = faultList.stream().mapToInt(BuRptBoardTrainFault::getSum1).sum();
            } else if (2 == period) {
                // 2质保期
                faultCount = faultList.stream().mapToInt(BuRptBoardTrainFault::getSum3).sum();
            }
            trainNoCountMap.put(getLongTrainNo(trainNo), faultCount);
        }

        return transformToPieChartVOList(trainNoCountMap);
    }

    private List<PieChartVO> getSysFaultPercentList(List<BuRptBoardSysFault> sysFaultList, int period) {
        Map<String, Integer> sysNameCountMap = new HashMap<>();

        Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(null);
        for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
            String sysId = sysEntry.getKey();
            BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
            String sysShortName = sysAssetTypeBO.getShortName();

            List<BuRptBoardSysFault> faultList = sysFaultList.stream()
                    .filter(fault -> sysId.equals(fault.getSysId()))
                    .collect(Collectors.toList());

            int faultCount = 0;
            if (1 == period) {
                // 1架修期
                faultCount = faultList.stream().mapToInt(BuRptBoardSysFault::getSum1).sum();
            } else if (2 == period) {
                // 2质保期
                faultCount = faultList.stream().mapToInt(BuRptBoardSysFault::getSum3).sum();
            }
            sysNameCountMap.put(sysShortName, faultCount);
        }

        return transformToPieChartVOList(sysNameCountMap);
    }

    private List<PieChartVO> transformToPieChartVOList(Map<String, Integer> itemCountMap) {
        List<PieChartVO> pieChartVOList = new ArrayList<>();

        for (Map.Entry<String, Integer> itemCountEntry : itemCountMap.entrySet()) {
            String item = itemCountEntry.getKey();
            Integer count = itemCountEntry.getValue();

            PieChartVO pieChartVO = new PieChartVO()
                    .setItem(item)
                    .setCount(count.doubleValue());
            pieChartVOList.add(pieChartVO);
        }

        pieChartVOList.sort(Comparator.comparing(PieChartVO::getItem, Comparator.nullsLast(Comparator.naturalOrder())));
        return pieChartVOList;
    }

}
