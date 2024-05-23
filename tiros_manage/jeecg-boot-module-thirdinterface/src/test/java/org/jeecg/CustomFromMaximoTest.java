package org.jeecg;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.service.*;
import org.jeecg.modules.third.maximo.mapper.CustomMaximoMapper;
import org.jeecg.modules.third.maximo.service.*;
import org.jeecg.modules.third.utils.ExcelUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 第三方接口测试
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CustomFromMaximoTest {

    @Resource
    private CustomMaximoService customMaximoService;


    /**
     * 搜集现有的队列表错误数据
     */
    @Test
    public void collectInErrors() throws Exception {
        List<Map<String, Object>> blockingErrorInQueueList = customMaximoService.listBlockingErrorInQueue();

        List<ExcelData> excelDataList = new ArrayList<>();

        for (Map<String, Object> blockingErrorInQueue : blockingErrorInQueueList) {
            String ifacename = (String) blockingErrorInQueue.get("IFACENAME");
            String errorMessage = (String) blockingErrorInQueue.get("errorMessage");
            BigDecimal transIdBigDecimal = (BigDecimal) blockingErrorInQueue.get("transId");
            String transId = String.valueOf(transIdBigDecimal);

            // 工单
            if (MaximoThirdConstant.IFACENAME_ORDER.equals(ifacename)) {
                String orderCode = (String) blockingErrorInQueue.get("orderCode");
                String orderStatus = (String) blockingErrorInQueue.get("orderStatus");

                if ("APPR".equals(orderStatus)) {
                    List<ExcelData> matchList = excelDataList.stream()
                            .filter(excelData -> 1 == excelData.getType()
                                    && orderCode.equals(excelData.getOrderCode()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(matchList)) {
                        ExcelData excelData = new ExcelData()
                                .setType(1)
                                .setOrderCode(orderCode)
                                .setRepeatTime(0)
                                .setTransId(transId)
                                .setErrorMessage(errorMessage);
                        excelDataList.add(excelData);
                    } else {
                        ExcelData excelData = matchList.get(0);
                        excelData.setRepeatTime(excelData.getRepeatTime() + 1)
                                .setTransId(excelData.getTransId() + "," + transId);
                        if (!errorMessage.equals(excelData.getErrorMessage())) {
                            excelData.setErrorMessage(excelData.getErrorMessage() + " ; " + errorMessage);
                        }
                    }
                }
                if ("CLOSE".equals(orderStatus)) {
                    List<ExcelData> matchList = excelDataList.stream()
                            .filter(excelData -> 2 == excelData.getType()
                                    && orderCode.equals(excelData.getOrderCode()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(matchList)) {
                        ExcelData excelData = new ExcelData()
                                .setType(2)
                                .setOrderCode(orderCode)
                                .setRepeatTime(0)
                                .setTransId(transId)
                                .setErrorMessage(errorMessage);
                        excelDataList.add(excelData);
                    } else {
                        ExcelData excelData = matchList.get(0);
                        excelData.setRepeatTime(excelData.getRepeatTime() + 1)
                                .setTransId(excelData.getTransId() + "," + transId);
                        if (!errorMessage.equals(excelData.getErrorMessage())) {
                            excelData.setErrorMessage(excelData.getErrorMessage() + " ; " + errorMessage);
                        }
                    }
                }
            }
            // 物料
            if ("JDX_MATUSETRANS".equals(ifacename)) {
                String materialIssueType = (String) blockingErrorInQueue.get("materialIssueType");
                String materialOrderCode = (String) blockingErrorInQueue.get("materialOrderCode");
                String materialCode = (String) blockingErrorInQueue.get("materialCode");
                BigDecimal materialQuantityBigDecimal = (BigDecimal) blockingErrorInQueue.get("materialQuantity");
                String materialQuantity = String.valueOf(materialQuantityBigDecimal);
                String materialStatus = (String) blockingErrorInQueue.get("materialStatus");

                if ("ISSUE".equals(materialIssueType)) {
                    List<ExcelData> matchList = excelDataList.stream()
                            .filter(excelData -> 3 == excelData.getType()
                                    && materialOrderCode.equals(excelData.getOrderCode())
                                    && materialCode.equals(excelData.getMaterialCode()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(matchList)) {
                        ExcelData excelData = new ExcelData()
                                .setType(3)
                                .setOrderCode(materialOrderCode)
                                .setMaterialCode(materialCode)
                                .setMaterialQuantity(materialQuantity)
                                .setMaterialStatus(materialStatus)
                                .setRepeatTime(0)
                                .setTransId(transId)
                                .setErrorMessage(errorMessage);
                        excelDataList.add(excelData);
                    } else {
                        ExcelData excelData = matchList.get(0);
                        excelData.setRepeatTime(excelData.getRepeatTime() + 1)
                                .setTransId(excelData.getTransId() + "," + transId);
                        if (!errorMessage.equals(excelData.getErrorMessage())) {
                            excelData.setErrorMessage(excelData.getErrorMessage() + " ; " + errorMessage);
                        }
                    }
                }
                if ("RETURN".equals(materialIssueType)) {
                    List<ExcelData> matchList = excelDataList.stream()
                            .filter(excelData -> 4 == excelData.getType()
                                    && materialOrderCode.equals(excelData.getOrderCode())
                                    && materialCode.equals(excelData.getMaterialCode()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(matchList)) {
                        ExcelData excelData = new ExcelData()
                                .setType(4)
                                .setOrderCode(materialOrderCode)
                                .setMaterialCode(materialCode)
                                .setMaterialQuantity(materialQuantity)
                                .setMaterialStatus(materialStatus)
                                .setRepeatTime(0)
                                .setTransId(transId)
                                .setErrorMessage(errorMessage);
                        excelDataList.add(excelData);
                    } else {
                        ExcelData excelData = matchList.get(0);
                        excelData.setRepeatTime(excelData.getRepeatTime() + 1)
                                .setTransId(excelData.getTransId() + "," + transId);
                        if (!errorMessage.equals(excelData.getErrorMessage())) {
                            excelData.setErrorMessage(excelData.getErrorMessage() + " ; " + errorMessage);
                        }
                    }
                }
            }
            // 人员工时"
            if ("JDX_LABTRANS".equals(ifacename)) {
                String userOrderCode = (String) blockingErrorInQueue.get("userOrderCode");
                String userWorkNo = (String) blockingErrorInQueue.get("userWorkNo");
                String userHour = (String) blockingErrorInQueue.get("userHour");

                List<ExcelData> matchList = excelDataList.stream()
                        .filter(excelData -> 6 == excelData.getType()
                                && userOrderCode.equals(excelData.getOrderCode())
                                && userWorkNo.equals(excelData.getUserWorkNo()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(matchList)) {
                    ExcelData excelData = new ExcelData()
                            .setType(6)
                            .setOrderCode(userOrderCode)
                            .setUserWorkNo(userWorkNo)
                            .setUserHour(userHour)
                            .setRepeatTime(0)
                            .setTransId(transId)
                            .setErrorMessage(errorMessage);
                    excelDataList.add(excelData);
                } else {
                    ExcelData excelData = matchList.get(0);
                    excelData.setRepeatTime(excelData.getRepeatTime() + 1)
                            .setTransId(excelData.getTransId() + "," + transId);
                    if (!errorMessage.equals(excelData.getErrorMessage())) {
                        excelData.setErrorMessage(excelData.getErrorMessage() + " ; " + errorMessage);
                    }
                }
            }
            // 故障
            if (MaximoThirdConstant.IFACENAME_FAULT.equals(ifacename)) {
                String faultSn = (String) blockingErrorInQueue.get("faultSn");

                List<ExcelData> matchList = excelDataList.stream()
                        .filter(excelData -> 5 == excelData.getType()
                                && faultSn.equals(excelData.getFaultSn()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(matchList)) {
                    ExcelData excelData = new ExcelData()
                            .setType(5)
                            .setFaultSn(faultSn)
                            .setRepeatTime(0)
                            .setTransId(transId)
                            .setErrorMessage(errorMessage);
                    excelDataList.add(excelData);
                } else {
                    ExcelData excelData = matchList.get(0);
                    excelData.setRepeatTime(excelData.getRepeatTime() + 1)
                            .setTransId(excelData.getTransId() + "," + transId);
                    if (!errorMessage.equals(excelData.getErrorMessage())) {
                        excelData.setErrorMessage(excelData.getErrorMessage() + " ; " + errorMessage);
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(excelDataList)) {
            excelDataList.sort(Comparator.comparing(ExcelData::getOrderCode, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(ExcelData::getType, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(ExcelData::getMaterialCode, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(ExcelData::getUserWorkNo, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(ExcelData::getFaultSn, Comparator.nullsLast(Comparator.naturalOrder())));

            HSSFWorkbook workbook = getExcel(excelDataList);

            try {
                String fileName = String.format("队列表错误数据-%s", workbook.getSheetAt(0).getSheetName());

                // 输出Excel文件
                FileOutputStream outputStream = new FileOutputStream("D:\\" + fileName + ".xls");
                workbook.write(outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Data
    @Accessors(chain = true)
    private static class ExcelData {
        private Integer type;
        private String orderCode;
        private String materialCode;
        private String materialQuantity;
        private String materialStatus;
        private String userWorkNo;
        private String userHour;
        private String faultSn;
        private Integer repeatTime;
        private String transId;
        private String errorMessage;
    }

    private HSSFWorkbook getExcel(List<ExcelData> excelDataList) {
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String[] excelHeader = {
                "操作类型",
                "工单号",
                "物料号",
                "物料数量",
                "物料消耗状态",
                "人员",
                "工时",
                "故障号",
                "重复次数",
                "transId",
                "错误信息"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

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

        for (ExcelData excelData : excelDataList) {
            if (null != excelData) {
                HSSFRow row = sheet.createRow(excelDataList.indexOf(excelData) + 1);
                row.createCell(0).setCellValue(getTypeNameString(excelData.getType()));
                row.createCell(1).setCellValue(excelData.getOrderCode());
                row.createCell(2).setCellValue(excelData.getMaterialCode());
                row.createCell(3).setCellValue(excelData.getMaterialQuantity());
                row.createCell(4).setCellValue(excelData.getMaterialStatus());
                row.createCell(5).setCellValue(excelData.getUserWorkNo());
                row.createCell(6).setCellValue(excelData.getUserHour());
                row.createCell(7).setCellValue(excelData.getFaultSn());
                row.createCell(8).setCellValue(excelData.getRepeatTime());
                row.createCell(9).setCellValue(excelData.getTransId());
                row.createCell(10).setCellValue(excelData.getErrorMessage());

            }
        }

        // 设置自动列宽
        //  ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
        ExcelUtil.setColumnWidth(sheet, excelHeaderList.size());

        return workbook;
    }

    private String getTypeNameString(Integer type) {
        String typeNameString = "";
        switch (type) {
            case 1:
                typeNameString = "新增工单";
                break;
            case 2:
                typeNameString = "关闭工单";
                break;
            case 3:
                typeNameString = "物料消耗";
                break;
            case 4:
                typeNameString = "物料退库";
                break;
            case 5:
                typeNameString = "新增故障";
                break;
            case 6:
                typeNameString = "人员工时";
                break;
            default:
                break;
        }

        return typeNameString;
    }

}
