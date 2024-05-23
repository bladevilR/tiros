package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.jeecg.modules.third.common.bean.bo.FaultCompareBO;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuFaultHandleRecord;
import org.jeecg.modules.third.jdx.bean.BuFaultInfo;
import org.jeecg.modules.third.jdx.bean.BuTrainInfo;
import org.jeecg.modules.third.jdx.bean.bo.FaultTimes;
import org.jeecg.modules.third.jdx.bean.vo.FaultDiff;
import org.jeecg.modules.third.jdx.mapper.BuFaultInfoThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuTrainInfoThirdMapper;
import org.jeecg.modules.third.jdx.mapper.ThirdCommonMapper;
import org.jeecg.modules.third.jdx.service.BuFaultInfoThirdService;
import org.jeecg.modules.third.maximo.bean.JdxSrIn;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.jeecg.modules.third.utils.ExcelUtil;
import org.jeecg.modules.third.utils.UUIDGenerator;
import org.jeecg.modules.third.workshopdb.bean.WSFault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
public class BuFaultInfoThirdServiceImpl extends ServiceImpl<BuFaultInfoThirdMapper, BuFaultInfo> implements BuFaultInfoThirdService {

    @Resource
    private BuFaultInfoThirdMapper buFaultInfoThirdMapper;
    @Resource
    private BuTrainInfoThirdMapper buTrainInfoThirdMapper;
    @Resource
    private ThirdCommonMapper thirdCommonMapper;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    //    private final DateTimeFormatter dateFormatterPoint = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    /**
     * 数据库支持500长度，最多可存汉字500/3=166个
     * 暂时不支持修改数据库长度，因为maximo检修系统也限制了该长度
     */
    private static final int FAULT_DESC_MAX_LENGTH = 160;
    /**
     * 跟踪情况的长度限制:2000/3=666
     */
    private static final int TRACK_MESSAGE_MAX_LENGTH = 660;
    /**
     * 车间数据库历史质保期故障
     */
    private static final String WARRANTY_FAULT_SN_SUFFIX = "LSZB";


    /**
     * @see BuFaultInfoThirdService#listMaximoFaultNeedWrite(Date)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxSrIn> listMaximoFaultNeedWrite(Date date) throws Exception {
        List<JdxSrIn> maximoFaultList = new ArrayList<>();

        List<BuFaultInfo> faultList = buFaultInfoThirdMapper.selectListByDate(date);
        if (CollectionUtils.isNotEmpty(faultList)) {
            for (BuFaultInfo fault : faultList) {
                JdxSrIn maximoFault = transformToMaximoFault(fault);
                maximoFaultList.add(maximoFault);
            }
        }

        return maximoFaultList;
    }

    /**
     * @see BuFaultInfoThirdService#getMaximoFaultNeedWriteByFaultId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public JdxSrIn getMaximoFaultNeedWriteByFaultId(String faultId) throws Exception {
        BuFaultInfo fault = buFaultInfoThirdMapper.selectFaultById(faultId);
        return transformToMaximoFault(fault);
    }

    /**
     * @see BuFaultInfoThirdService#getMaximoFaultNeedWriteByFault(BuFaultInfo)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public JdxSrIn getMaximoFaultNeedWriteByFault(BuFaultInfo fault) throws Exception {
        return transformToMaximoFault(fault);
    }

    /**
     * @see BuFaultInfoThirdService#getFaultDiffOfJdxAndMaximo(List)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public FaultDiff getFaultDiffOfJdxAndMaximo(List<String> maximoFaultSnList) throws Exception {
        // 查询已提交的故障
        LambdaQueryWrapper<BuFaultInfo> wrapper = new LambdaQueryWrapper<BuFaultInfo>()
                .eq(BuFaultInfo::getSubmitStatus, 1);
        List<BuFaultInfo> faultList = buFaultInfoThirdMapper.selectList(wrapper);

        // 计算
        List<String> bothExistFaultSnList = new ArrayList<>();
        List<String> onlyJdxExistFaultSnList = new ArrayList<>();
        List<String> onlyMaximoExistFaultSnList = new ArrayList<>(maximoFaultSnList);
        for (BuFaultInfo fault : faultList) {
            String faultSn = fault.getFaultSn();
            if (maximoFaultSnList.contains(faultSn)) {
                bothExistFaultSnList.add(faultSn);
                onlyMaximoExistFaultSnList.remove(faultSn);
            } else {
                onlyJdxExistFaultSnList.add(faultSn);
            }
        }

        return new FaultDiff()
                .setJdxFaultCount(faultList.size())
                .setMaximoFaultCount(maximoFaultSnList.size())
                .setBothExistFaultCount(bothExistFaultSnList.size())
                .setBothExistFaultSnList(bothExistFaultSnList)
                .setOnlyJdxExistFaultCount(onlyJdxExistFaultSnList.size())
                .setOnlyJdxExistFaultSnList(onlyJdxExistFaultSnList)
                .setOnlyMaximoExistFaultCount(onlyMaximoExistFaultSnList.size())
                .setOnlyMaximoExistFaultSnList(onlyMaximoExistFaultSnList);
    }

    /**
     * @see BuFaultInfoThirdService#exportFaultDiffOfJdxAndMaximo(Map)
     */
    @Override
    public HSSFWorkbook exportFaultDiffOfJdxAndMaximo(Map<String, FaultCompareBO> faultSnBOMap) {
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 获取数据
        // 查询架大修中手动新增的数据
        LambdaQueryWrapper<BuFaultInfo> faultWrapper = new LambdaQueryWrapper<BuFaultInfo>()
                .eq(BuFaultInfo::getSubmitStatus, 1)
                .eq(BuFaultInfo::getFromType, 1);
        List<BuFaultInfo> faultInfoList = buFaultInfoThirdMapper.selectList(faultWrapper);
        for (BuFaultInfo faultInfo : faultInfoList) {
            FaultCompareBO faultCompareBO = faultSnBOMap.getOrDefault(faultInfo.getFaultSn(), new FaultCompareBO());
            faultCompareBO.setJdxFaultSn(faultInfo.getFaultSn())
                    .setJdxFaultStatus(faultInfo.getStatus().toString())
                    .setJdxFaultStatusText(getFaultStatusText(faultInfo.getStatus()))
                    .setJdxFaultFromType(faultInfo.getFromType().toString())
                    .setJdxFaultFromTypeText(getFaultFromTypeText(faultInfo.getFromType()));
            faultSnBOMap.put(faultInfo.getFaultSn(), faultCompareBO);
        }
        // 如果不是手动新增的故障传到maximo了
        List<FaultCompareBO> faultCompareBOList = new ArrayList<>(faultSnBOMap.values());
        for (FaultCompareBO faultCompareBO : faultCompareBOList) {
            if (StringUtils.isNotBlank(faultCompareBO.getMaximoFaultSn()) && StringUtils.isBlank(faultCompareBO.getJdxFaultSn())) {
                LambdaQueryWrapper<BuFaultInfo> faultSnWrapper = new LambdaQueryWrapper<BuFaultInfo>()
                        .eq(BuFaultInfo::getFaultSn, faultCompareBO.getMaximoFaultSn());
                BuFaultInfo faultInfo = buFaultInfoThirdMapper.selectOne(faultSnWrapper);
                if (null != faultInfo) {
                    faultCompareBO.setJdxFaultSn(faultInfo.getFaultSn())
                            .setJdxFaultStatus(faultInfo.getStatus().toString())
                            .setJdxFaultStatusText(getFaultStatusText(faultInfo.getStatus()))
                            .setJdxFaultFromType(faultInfo.getFromType().toString())
                            .setJdxFaultFromTypeText(getFaultFromTypeText(faultInfo.getFromType()));
                    faultSnBOMap.put(faultInfo.getFaultSn(), faultCompareBO);
                }
            }
        }

        // 比较
        for (FaultCompareBO faultCompareBO : faultCompareBOList) {
            boolean snSame = StringUtils.compare(faultCompareBO.getJdxFaultSn(), faultCompareBO.getMaximoFaultSn()) == 0;
            boolean statusSame = StringUtils.compare(faultCompareBO.getJdxFaultStatusText(), faultCompareBO.getMaximoFaultStatusText()) == 0;

            if (snSame && statusSame) {
                faultCompareBO.setSame(1)
                        .setSameText("一致");
            } else {
                faultCompareBO.setSame(0)
                        .setSameText("不一致");
            }

            boolean jdxExist = StringUtils.isNotBlank(faultCompareBO.getJdxFaultSn());
            boolean maximoExist = StringUtils.isNotBlank(faultCompareBO.getMaximoFaultSn());
            faultCompareBO.setJdxExist(jdxExist ? 1 : 0)
                    .setMaximoExist(maximoExist ? 1 : 0);
        }
        faultCompareBOList.sort(Comparator.comparing(FaultCompareBO::getSame)
                .thenComparing(FaultCompareBO::getJdxExist)
                .thenComparing(FaultCompareBO::getMaximoExist));

        String[] excelHeader = {
                "架大修故障编码",
                "maximo故障编码",
                "是否一致",
                "架大修状态",
                "maximo状态",
                "架大修故障来源"
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

        HSSFCellStyle redCellStyle = workbook.createCellStyle();
        // 单元格格式，水平居中
        redCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font redFont = workbook.createFont();
        // 将字体设置为“红色”
        redFont.setColor(Font.COLOR_NORMAL);
        // 加粗
        redFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        redCellStyle.setFont(redFont);

        for (FaultCompareBO faultCompareBO : faultCompareBOList) {
            if (null != faultCompareBO) {
                HSSFRow row = sheet.createRow(faultCompareBOList.indexOf(faultCompareBO) + 1);
                row.createCell(0).setCellValue(faultCompareBO.getJdxFaultSn());
                row.createCell(1).setCellValue(faultCompareBO.getMaximoFaultSn());
                row.createCell(2).setCellValue(faultCompareBO.getSameText());
                if ("不一致".equals(faultCompareBO.getSameText())) {
                    row.getCell(2).setCellStyle(redCellStyle);
                }
                row.createCell(3).setCellValue(faultCompareBO.getJdxFaultStatusText());
                row.createCell(4).setCellValue(faultCompareBO.getMaximoFaultStatusText());
                row.createCell(5).setCellValue(faultCompareBO.getJdxFaultFromTypeText());
            }
        }

        // 设置自动列宽
        ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());

        return workbook;
    }

    /**
     * @see BuFaultInfoThirdService#initSaveWorkshopFaultData(List, Map)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String initSaveWorkshopFaultData(List<WSFault> wsFaultList, Map<String, String> lineNameIdMap) {
        if (CollectionUtils.isEmpty(wsFaultList)) {
            return "车间质保期故障数据为空，不进行操作";
        }

        Date now = new Date();

        // 车间历史质保 故障后缀
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        // 删除旧的
        LambdaQueryWrapper<BuFaultInfo> faultWrapper = new LambdaQueryWrapper<BuFaultInfo>()
                .eq(BuFaultInfo::getFromType, 3);
//        List<BuFaultInfo> oldFaultList = buFaultInfoThirdMapper.selectList(faultWrapper);
        int oldFaultCount = buFaultInfoThirdMapper.delete(faultWrapper);

        // 查询车辆信息
        List<BuTrainInfo> trainInfoList = buTrainInfoThirdMapper.selectList(Wrappers.emptyWrapper());
        // 查询线路信息
//        List<BuMtrLine> lineList = buTrainInfoThirdMapper.selectAllLine();
//        Map<String, String> lineNameIdMap = new HashMap<>();
//        lineList.forEach(item -> lineNameIdMap.put(item.getLineName(), item.getLineId()));
        // 查询班组信息
        List<Map<String, Object>> groupIdAndNameMapList = thirdCommonMapper.selectGroupIdAndName();
        Map<String, String> groupNameIdMap = toPropertyMap(groupIdAndNameMapList, "groupName", "id");
        // 查询人员信息
        List<Map<String, Object>> userIdAndRealNameMapList = thirdCommonMapper.selectUserIdAndRealName();
        Map<String, String> userRealNameIdMap = toPropertyMap(userIdAndRealNameMapList, "realname", "id");
        List<Map<String, Object>> userIdAndWorkNoMapList = thirdCommonMapper.selectUserIdAndWorkNo();
        Map<String, String> userWorkNoIdMap = toPropertyMap(userIdAndWorkNoMapList, "workNo", "id");
        // 查询工位信息
        List<Map<String, Object>> workstationIdAndNameMapList = thirdCommonMapper.selectWorkstationIdAndName();
        Map<String, String> workstationNameIdMap = toPropertyMap(workstationIdAndNameMapList, "name", "id");
        // 查询设备类型系统信息
        List<Map<String, Object>> assetTypeSystemIdAndNameMapList = thirdCommonMapper.selectAssetTypeSystemIdAndName();
        Map<String, String> systemNameIdMap = toPropertyMap(assetTypeSystemIdAndNameMapList, "name", "id");
        String defaultSystemId = systemNameIdMap.getOrDefault("其他", "-1");

        // 转化
        List<BuFaultInfo> faultInfoList = new ArrayList<>();
        log.info("转化前wsFaultList长度 = " + wsFaultList.size());
        int count = 1;
        for (WSFault wsFault : wsFaultList) {
//            log.info("第" + count + "次转化开始，");
//            log.info(JSON.toJSONString(wsFault));

            // 故障编号
            Integer sn = wsFault.get序号();
            String faultSn = sn + WARRANTY_FAULT_SN_SUFFIX;
            String faultDesc = trim(wsFault.get故障描述()).replaceAll(" ", "");
            faultDesc = cutString(faultDesc, FAULT_DESC_MAX_LENGTH, true);
            // 故障日期
            LocalDate faultLocalDate = getLocalDateFromWorkshopFaultDateStr(wsFault.get日期(), null);
            // 处理日期
            LocalDate handleLocalDate = getLocalDateFromWorkshopFaultDateStr(wsFault.get修复日期(), faultLocalDate);
//            String handleDateStr = StringUtils.isBlank(trim(wsFault.get修复日期())) ? trim(wsFault.get日期()) : trim(wsFault.get修复日期());
//            LocalDate handleLocalDate;
//            try {
//                handleLocalDate = getLocalDateFromWorkshopFaultDateStr(handleDateStr);
//            } catch (Exception ex) {
//                handleLocalDate = faultLocalDate;
//            }
            // 发现时间
            Date happenTime = getDateFromWorkshopFaultLocalDateAndTimeStr(faultLocalDate, wsFault.get发生时间());
            Date reportTime = getDateFromWorkshopFaultLocalDateAndTimeStr(faultLocalDate, wsFault.get接报时间());
//            LocalTime happenLocalTime = StringUtils.isBlank(trim(wsFault.get发生时间())) ? LocalTime.MIN : LocalTime.parse(trim(wsFault.get发生时间()), timeFormatter);
//            LocalTime reportLocalTime = StringUtils.isBlank(trim(wsFault.get接报时间())) ? LocalTime.MIN : LocalTime.parse(trim(wsFault.get接报时间()), timeFormatter);
//            Date happenTime = Date.from(LocalDateTime.of(faultLocalDate, happenLocalTime).atZone(ZoneId.systemDefault()).toInstant());
//            Date reportTime = Date.from(LocalDateTime.of(faultLocalDate, reportLocalTime).atZone(ZoneId.systemDefault()).toInstant());
            // 处理时间=关闭时间
            Date handleTime = getDateFromWorkshopFaultLocalDateAndTimeStr(handleLocalDate, wsFault.get修复时间());
//            LocalTime handleLocalTime = StringUtils.isBlank(trim(wsFault.get修复时间())) ? LocalTime.MIN : LocalTime.parse(trim(wsFault.get修复时间()), timeFormatter);
            // 线路
            String lineId = lineNameIdMap.getOrDefault(trim(wsFault.get线别()), null);
            // 车号
            String trainNo = cutString(trim(wsFault.get列车号()), 4, false);
            // 等级
            Integer faultLevel = transToFaultLevelInt(trim(wsFault.get故障等级()));
            // 是否正线
            String wsFaultTypeStr = trim(wsFault.get类别());
            Integer faultOnline = "正线".equals(wsFaultTypeStr) ? 1 : 0;
            // 提报班组
            String reportGroupId = groupNameIdMap.getOrDefault(wsFaultTypeStr, cutString(wsFaultTypeStr, 8, false));
            // 提报人
            String wsFaultReportUserStr = trim(wsFault.get报告人());
            String reportUserId = userRealNameIdMap.getOrDefault(wsFaultReportUserStr, cutString(wsFaultReportUserStr, 8, false));
            // 关闭人
            String wsFaultHandleUserStr = trim(wsFault.get处理人());
            String closeUserId = userWorkNoIdMap.getOrDefault(wsFaultHandleUserStr, cutString(wsFaultHandleUserStr, 8, false));
            // 跟踪情况
            String trackMessage = trim(wsFault.get跟踪情况());
            trackMessage = cutString(trackMessage, TRACK_MESSAGE_MAX_LENGTH, true);
            // 工位
            String workstationId = workstationNameIdMap.getOrDefault(wsFaultTypeStr, cutString(wsFaultTypeStr, 8, false));
            // 系统
            String wsFaultSystemStr = trim(wsFault.get系统());
            String systemId = systemNameIdMap.getOrDefault(wsFaultSystemStr, cutString(wsFaultSystemStr, 8, false));
            if (StringUtils.isBlank(systemId)) {
                systemId = defaultSystemId;
            }

            BuFaultInfo faultInfo = new BuFaultInfo()
                    .setId(UUIDGenerator.generate())
                    .setCategoryId(null)
                    .setFaultCodeId(null)
                    .setFaultSn(faultSn)
                    .setFaultDesc(StringUtils.isNotBlank(faultDesc) ? faultDesc : "无")
                    .setSysId(StringUtils.isNotBlank(systemId) ? systemId : defaultSystemId)
                    .setSubSysId(null)
                    .setFaultAssetId(null)
                    .setHappenTime(happenTime)
                    .setWorkOrderId(null)
                    .setOrderTaskId(null)
                    .setLineId(StringUtils.isNotBlank(lineId) ? lineId : "1")
                    .setTrainNo(StringUtils.isNotBlank(trainNo) ? trainNo : "-1")
                    .setPhase(2)
                    .setFaultLevel(faultLevel)
                    .setFaultOnline(faultOnline)
                    .setHasDuty(0)
                    .setOutsource(0)
                    .setReportGroupId(StringUtils.isNotBlank(reportGroupId) ? reportGroupId : "-1")
                    .setReportUserId(StringUtils.isNotBlank(reportUserId) ? reportUserId : "-1")
                    .setReportTime(reportTime)
                    .setDutyEngineer(null)
                    .setStatus(2)
                    .setCloseUserId(closeUserId)
                    .setCloseTime(handleTime)
                    .setSubmitStatus(1)
                    .setWorkStationId(workstationId)
                    .setCreateTime(null)
                    .setCreateBy("admin")
                    .setUpdateTime(null)
                    .setUpdateBy(null)
                    .setCreateType(1)
                    .setTrainStructureId(null)
                    .setHandleStatus(2)
                    .setHandleOrderId(null)
                    .setOutSupplierId(null)
                    .setFormValueId(null)
                    .setPlanId("-1")
                    .setCompanyId("YY1")
                    .setWorkshopId("CJ1")
                    .setTrackMessage(trackMessage);
            // 设置故障来源
            faultInfo.setFromType(3);
            faultInfoList.add(faultInfo);
//            log.info("第" + count + "次转化结束。");
            count++;
        }

        // 插入新的
        faultInfoList.sort(Comparator.comparing(BuFaultInfo::getFaultSn));
        log.info("转化后faultInfoList长度 = " + faultInfoList.size());
        List<List<BuFaultInfo>> batchSubList = DatabaseBatchSubUtil.batchSubList(faultInfoList, 20);
        int saveCount = 1;
        for (List<BuFaultInfo> batchSub : batchSubList) {
            String startFaultSn = batchSub.get(0).getFaultSn();
            String endFaultSn = batchSub.get(batchSub.size() - 1).getFaultSn();
            log.info("第" + saveCount + "次保存开始， 此次保存数据长度 = " + batchSub.size() + "，故障编码为" + startFaultSn + "至" + endFaultSn);
            buFaultInfoThirdMapper.insertList(batchSub);
            log.info("第" + saveCount + "次保存结束， 此次保存数据长度 = " + batchSub.size() + "，故障编码为" + startFaultSn + "至" + endFaultSn);
            saveCount++;
        }

        return String.format("删除了旧的故障数据%s条，插入了新的故障数据%s条。", oldFaultCount, faultInfoList.size());
    }


    private JdxSrIn transformToMaximoFault(BuFaultInfo fault) {
        /**
         * 故障代码逻辑：
         * 1、故障系统C_FAILURESYSTEM（FAILTYPE IS NULL AND SPECIALTY='电客车' AND PARENT IS NULL）
         * 2、故障子系统C_FAILURESUBSYSTEM（FAILTYPE IS NULL AND SPECIALTY='电客车' AND PARENT=故障系统）
         * 3、故障代码FAILURECODE（FAILTYPE IS NULL AND SPECIALTY='电客车' AND PARENT=故障子系统）
         * 4、故障原因PROBLEMCODE（FAILTYPE=PROBLEM AND SPECIALTY='电客车'）
         * 5、采取措施C_CAUSE（FAILTYPE=CAUSE AND SPECIALTY='电客车'）
         * 6、原因分类C_REMEDY（FAILTYPE=REMEDY AND SPECIALTY='电客车'）
         */
        if (null == fault) {
            return null;
        }

        List<BuFaultHandleRecord> handleRecordList = fault.getHandleRecordList();

        Date firstHandleTime = null;
        String solutionDesc = null;
        String reasonDesc = null;
        String solutionUserId = null;
        String solutionGroupId = null;
        String faultReasonCode = null;
        String faultSolutionCode = null;

        if (CollectionUtils.isNotEmpty(handleRecordList)) {
            handleRecordList.sort(Comparator.comparing(BuFaultHandleRecord::getSolutionTime, Comparator.nullsFirst(Comparator.naturalOrder())));

            Optional<Date> min = handleRecordList.stream()
                    .map(BuFaultHandleRecord::getSolutionTime)
                    .min(Date::compareTo);
            if (min.isPresent()) {
                firstHandleTime = min.get();
            }

//            reasonDesc = handleRecordList.stream()
//                    .map(BuFaultHandleRecord::getReasonDesc)
//                    .filter(StringUtils::isNotBlank)
//                    .collect(Collectors.joining(";"));
//            solutionDesc = handleRecordList.stream()
//                    .map(BuFaultHandleRecord::getSolutionDesc)
//                    .filter(StringUtils::isNotBlank)
//                    .collect(Collectors.joining(";"));

            for (BuFaultHandleRecord handleRecord : handleRecordList) {
                if (StringUtils.isNotBlank(handleRecord.getReasonDesc())) {
                    reasonDesc = handleRecord.getReasonDesc();
                }
                if (StringUtils.isNotBlank(handleRecord.getSolutionDesc())) {
                    solutionDesc = handleRecord.getSolutionDesc();
                }

                if (StringUtils.isNotBlank(handleRecord.getFaultReasonCode())) {
                    faultReasonCode = handleRecord.getFaultReasonCode();
                }
                if (StringUtils.isNotBlank(handleRecord.getFaultSolutionCode())) {
                    faultSolutionCode = handleRecord.getFaultSolutionCode();
                }
                if (StringUtils.isNotBlank(handleRecord.getSolutionUserId())) {
                    solutionUserId = handleRecord.getSolutionUserId();
                }
                if (StringUtils.isNotBlank(handleRecord.getSolutionGroupId())) {
                    solutionGroupId = handleRecord.getSolutionGroupId();
                }
            }
        }

        // 时间
        // 发生时间affecteddate < 提报时间reportdate < 班组响应时间actualstart < 临时修复时间c_tmprepairetime < 完全修复时间actualfinish
        Date affecteddate = fault.getHappenTime();
        Date reportdate = fault.getReportTime();
        Date actualstart = firstHandleTime;
        Date cTmprepairetime = null;
        Date actualfinish = null;
        if (null != fault.getHandleStatus() && 3 == fault.getHandleStatus()) {
            // 临时修复
            cTmprepairetime = fault.getCloseTime();
        } else {
            actualfinish = fault.getCloseTime();
        }

        FaultTimes faultTimes = new FaultTimes()
                .setAffecteddate(affecteddate)
                .setReportdate(reportdate)
                .setActualstart(actualstart)
                .setCTmprepairetime(cTmprepairetime)
                .setActualfinish(actualfinish);
        checkTimes(faultTimes);
        affecteddate = faultTimes.getAffecteddate();
        reportdate = faultTimes.getReportdate();
        actualstart = faultTimes.getActualstart();
        cTmprepairetime = faultTimes.getCTmprepairetime();
        actualfinish = faultTimes.getActualfinish();

        String trainId = thirdCommonMapper.selectTrainIdByTrainNo(fault.getTrainNo());
        String workshopCode = thirdCommonMapper.selectDepartMaximoCodeById(fault.getWorkshopId());

        JdxSrIn jdxSrIn = new JdxSrIn()
                .setActualfinish(actualfinish)
                .setActualstart(actualstart)
                .setAffecteddate(affecteddate)
                .setAssetorgid("ORG1")
//                .setChangeby(MaximoThirdConstant.TEST_USER)
//                .setChangedate(null == fault.getUpdateTime() ? new Date() : fault.getUpdateTime())
                .setChangeby(null)
                .setChangedate(null)
                .setSrclass("SR")
                .setCActual(solutionDesc)
                .setCCause(faultSolutionCode)
                .setCFailureaffect(null)
                .setCFailureclass("电客车")
                .setCFaultrank(transToCFaultrank(fault.getFaultLevel()))
                .setCFrsg(reasonDesc)
                .setCLocation(null)
                .setCOccsd(null)
                .setCProductsd(null)
                .setCRemedy(fault.getCategoryCode())
                .setCStation(null)
                .setCTmprepairetime(cTmprepairetime)
                .setCUsedmaterial(null)
                .setCZdgz(null)
                .setCZhanfasd(null)
                .setDescription(fault.getFaultDesc())
                .setDescriptionLongdescription(null)
                .setDescriptionLongdescription2(null)
                .setFailurecode(fault.getFaultCodeCode())
                .setLead(null)
                .setLocation(null)
//                .setOrgid("ORG1")
                .setProblemcode(faultReasonCode)
                .setReportdate(reportdate)
                .setReportedphone(fault.getReportUserPhone())
                .setResultstatus(transToResultstatus(fault.getHandleStatus()))
                .setStatus(transToStatus(fault.getStatus(), fault.getSubmitStatus()))
                .setStatusdate(null)
                .setTargetstart(null)
                .setTicketid(fault.getFaultSn())
                .setTransid(null)
                .setTransseq(1L);

        // 设置故障代码系统子系统
        if (StringUtils.isNotBlank(jdxSrIn.getFailurecode())) {
            String failurecode = jdxSrIn.getFailurecode();
            String sysCode = failurecode.substring(0, 4);
            String subSysCode = failurecode.substring(0, 6);
            jdxSrIn.setCFailuresystem(sysCode)
                    .setCFailuresubsystem(subSysCode);
        }

        // 设置故障资产设备编码
        if (StringUtils.isNotBlank(fault.getFaultAssetCode())) {
            // 有资产设备编码，用资产设备编码
            jdxSrIn.setAssetnum(fault.getFaultAssetCode());
        } else {
            // 没有资产设备编码，用车辆资产编码
            // 没有车辆资产编码，用车辆资产的上级
            if (StringUtils.isBlank(trainId)) {
                String topAssetNum = MaximoThirdConstant.TRAIN_PARENT_LINE123;
                String lineId = fault.getLineId();
                if (lineId.equals("1") || lineId.equals("2") || lineId.equals("3")) {
                    topAssetNum = MaximoThirdConstant.TRAIN_PARENT_LINE123;
                } else if (lineId.equals("4")) {
                    topAssetNum = MaximoThirdConstant.TRAIN_PARENT_LINE4;
                }

                jdxSrIn.setAssetnum(topAssetNum);
            } else {
                jdxSrIn.setAssetnum(trainId);
            }
        }

        jdxSrIn.setCrewid(StringUtils.isBlank(workshopCode) ? MaximoThirdConstant.JDX_WORKSHOP_1 : workshopCode)
                .setCLinenum(StringUtils.isBlank(fault.getLineId()) ? null : MaximoThirdConstant.LINE_PREFIX + fault.getLineId())
                .setSiteid(StringUtils.isBlank(fault.getLineId()) ? null : MaximoThirdConstant.SITE_PREFIX + fault.getLineId())
                .setSupervisor(null);

        if (StringUtils.isNotBlank(fault.getReportGroupId())) {
            String reportGroupId = fault.getReportGroupId();
            String reportGroupMonitorWorkNo = thirdCommonMapper.selectMonitorWorkNoByGroupId(reportGroupId);
            String reportGroupCode = thirdCommonMapper.selectDepartMaximoCodeById(reportGroupId);

            if (StringUtils.isNotBlank(reportGroupCode) && StringUtils.isNotBlank(reportGroupMonitorWorkNo)) {
                jdxSrIn.setCConfirmperson(reportGroupMonitorWorkNo)
                        .setCFindby(reportGroupMonitorWorkNo)
                        .setCPersongroup(reportGroupCode)
                        .setReportedby(reportGroupMonitorWorkNo);
            }
        }

        if (StringUtils.isNotBlank(solutionGroupId)) {
            String solutionGroupMonitorWorkNo = thirdCommonMapper.selectMonitorWorkNoByGroupId(solutionGroupId);
            String solutionGroupCode = thirdCommonMapper.selectDepartMaximoCodeById(solutionGroupId);

            if (StringUtils.isNotBlank(solutionGroupCode) && StringUtils.isNotBlank(solutionGroupMonitorWorkNo)) {
                jdxSrIn.setCVendorsupervisor(solutionGroupMonitorWorkNo)
                        .setPersongroup(solutionGroupCode);
            }
        }

        return jdxSrIn;
    }

    //    private void checkTimes(Date affecteddate, Date reportdate, Date actualstart, Date cTmprepairetime, Date actualfinish) {
//        if (null == affecteddate) {
//            affecteddate = new Date();
//        }
//        if (null == reportdate) {
//            reportdate = new Date();
//        }
//        if (null == actualstart) {
//            actualstart = new Date();
//        }
//
//        if (!reportdate.after(affecteddate)) {
//            reportdate = timePlus10Seconds(affecteddate);
//        }
//        if (!actualstart.after(reportdate)) {
//            actualstart = timePlus10Seconds(reportdate);
//        }
//
//        if (null != cTmprepairetime) {
//            if (!cTmprepairetime.after(actualstart)) {
//                cTmprepairetime = timePlus10Seconds(actualstart);
//            }
//        }
//        if (null != actualfinish) {
//            if (!actualfinish.after(actualstart)) {
//                actualfinish = timePlus10Seconds(actualstart);
//            }
//        }
//
//        if (null != cTmprepairetime && null != actualfinish) {
//            if (!actualfinish.after(cTmprepairetime)) {
//                actualfinish = timePlus10Seconds(cTmprepairetime);
//            }
//        }
//    }
    private void checkTimes(FaultTimes faultTimes) {
        // 发生时间affecteddate < 提报时间reportdate < 班组响应时间actualstart < 临时修复时间c_tmprepairetime < 完全修复时间actualfinish
        Date now = new Date();

        if (null == faultTimes.getAffecteddate()) {
            faultTimes.setAffecteddate(now);
        }

        if (null == faultTimes.getReportdate() || !faultTimes.getReportdate().after(faultTimes.getAffecteddate())) {
            faultTimes.setReportdate(timePlus10Seconds(faultTimes.getAffecteddate()));
        }
        if (null == faultTimes.getActualstart() || !faultTimes.getActualstart().after(faultTimes.getReportdate())) {
            faultTimes.setActualstart(timePlus10Seconds(faultTimes.getReportdate()));
        }

        if (null != faultTimes.getCTmprepairetime()) {
            if (!faultTimes.getCTmprepairetime().after(faultTimes.getActualstart())) {
                faultTimes.setCTmprepairetime(timePlus10Seconds(faultTimes.getActualstart()));
            }
        }
        if (null != faultTimes.getActualfinish()) {
            if (!faultTimes.getActualfinish().after(faultTimes.getActualstart())) {
                faultTimes.setActualfinish(timePlus10Seconds(faultTimes.getActualstart()));
            }
        }

        if (null != faultTimes.getCTmprepairetime() && null != faultTimes.getActualfinish()) {
            if (!faultTimes.getActualfinish().after(faultTimes.getCTmprepairetime())) {
                faultTimes.setActualfinish(timePlus10Seconds(faultTimes.getCTmprepairetime()));
            }
        }
    }

    private Date timePlus10Seconds(Date date) {
        long time = date.getTime();
        return new Date(time + 10000);
    }

    private String transToFailurecode(BuFaultInfo fault) {
        if (null == fault) {
            return null;
        }
        if (StringUtils.isBlank(fault.getCategoryCode())) {
            return null;
        }

        String categoryCodeDes = "";
        if (StringUtils.isNotBlank(fault.getCategoryCodeDes())) {
            categoryCodeDes = "(" + fault.getCategoryCodeDes() + ")";
        }

        return fault.getCategoryCode() + categoryCodeDes;
    }

    private String transToProblemcode(BuFaultInfo fault) {
        if (null == fault) {
            return null;
        }
        if (StringUtils.isBlank(fault.getFaultCodeCode())) {
            return null;
        }

        String faultCodeCodeDes = "";
        if (StringUtils.isNotBlank(fault.getFaultCodeCodeDes())) {
            faultCodeCodeDes = "(" + fault.getFaultCodeCodeDes() + ")";
        }

        return fault.getFaultCodeCode() + faultCodeCodeDes;
    }

//    private String transToCFailureaffect() {
//        /**
//         * XH_01	列车无法以自动防护模式运行
//         * JCW_01	单边供电
//         * JCW_02	部分分区失电
//         * JCW_03	其他
//         * GYGD_01	部分分区失电
//         * GYGD_02	单边供电
//         * GYGD_03	其他
//         * XH_02	部分区段无速度码
//         * XH_03	发生道岔失去表示
//         * FAS_01	FAS联动
//         * DFT_01	电梯停运困人
//         */
//        return null;
//    }

//    private Double transToCZdgz() {
//        // 是否典型故障：0=否，1=是；也可以称为重点故障
//        return null;
//    }

//    private String transToCOccsd() {
//        /**
//         * 01	行车调度
//         * 02	电力调度
//         * 03	环空调度
//         * 04	设备维修调度
//         * 05	车场调度
//         * 06	信号楼调度
//         */
//
//        return null;
//    }

//    private String transToCProductsd() {
//        /**
//         * 01	车辆检修调度
//         * 02	工务车间生产调度
//         * 03	通号车间生产调度
//         * 04	供电车间生产调度
//         * 05	机电车间生产调度
//         * 06	AFC车间调度
//         * 07	设备车间调度
//         */
//
//        return null;
//    }

    private String transToCFaultrank(Integer level) {
        /**
         * A
         * B
         * C
         */

        if (null == level) {
            return null;
        }
        if (1 == level) {
            return "A";
        }
        if (2 == level) {
            return "B";
        }
        if (3 == level) {
            return "C";
        }
        if (4 == level) {
            return "C";
        }

        return "A";
    }

    private String transToResultstatus(Integer handleStatus) {
        /**
         * 设备正常
         * 已修复
         * 未修复
         * 维修结束
         */
        if (null == handleStatus) {
            return "已修复";
        }
        if (1 == handleStatus) {
            return "设备正常";
        }
        if (2 == handleStatus) {
            return "已修复";
        }
        if (3 == handleStatus) {
            return "未修复";
        }
        if (4 == handleStatus) {
            return "维修结束";
        }

        return "已修复";
    }

    private String transToStatus(Integer status, Integer submitStatus) {
        /**
         * BZCOM	班组已处理完成
         * CAN	取消
         * CLOSED	已关闭
         * HISTEDIT	编辑历史记录
         * INPROG	进行中
         * NEW	新建
         * NEWOCC	OCC调度已确认
         * NEWSD	车间调度已确认
         * PENDING	暂挂
         * QUEUED	已排队
         * RESOLVED	班组已处理
         * WAPPR	调度待处理
         * WOCLOSED	已维修
         */

        if (null != submitStatus && 2 == submitStatus) {
            return "CAN";
        }

        if (null == status) {
            return "NEW";
        }
        if (0 == status) {
            return "NEW";
        }
        if (1 == status) {
            return "RESOLVED";
        }
        if (2 == status) {
            return "CLOSED";
        }
        if (3 == status) {
            return "PENDING";
        }

        return "NEW";
    }

    private Integer transToFaultLevelInt(String level) {
        /**
         * 1A
         * 2B
         * 3C
         */

        if (null == level) {
            return 1;
        }
        if ("A".equals(level)) {
            return 1;
        }
        if ("B".equals(level)) {
            return 2;
        }
        if ("C".equals(level)) {
            return 3;
        }

        return 1;
    }

    private String trim(String input) {
        if (null == input) {
            return null;
        } else {
            return input.trim();
        }
    }

    private LocalDate getLocalDateFromWorkshopFaultDateStr(String localDateStr, LocalDate localDate) {
        try {
            if (localDateStr.contains(".")) {
                String[] split = localDateStr.split("\\.");
                String year = split[0];
                if (year.length() < 4) {
                    return localDate;
                }
                StringBuilder month = new StringBuilder(split[1]);
                StringBuilder day = new StringBuilder(split[2]);

                while (month.length() < 2) {
                    month.insert(0, "0");
                }
                while (day.length() < 2) {
                    day.insert(0, "0");
                }

                localDateStr = year + month + day;
            }
            if (localDateStr.contains("/")) {
                String[] split = localDateStr.split("/");
                String year = split[0];
                if (year.length() < 4) {
                    return localDate;
                }
                StringBuilder month = new StringBuilder(split[1]);
                StringBuilder day = new StringBuilder(split[2]);

                while (month.length() < 2) {
                    month.insert(0, "0");
                }
                while (day.length() < 2) {
                    day.insert(0, "0");
                }

                localDateStr = year + month + day;
            }
            if (localDateStr.contains("-")) {
                String[] split = localDateStr.split("-");
                String year = split[0];
                if (year.length() < 4) {
                    return localDate;
                }
                StringBuilder month = new StringBuilder(split[1]);
                StringBuilder day = new StringBuilder(split[2]);

                while (month.length() < 2) {
                    month.insert(0, "0");
                }
                while (day.length() < 2) {
                    day.insert(0, "0");
                }

                localDateStr = year + month + day;
            }

            if (localDateStr.length() < 8) {
                // 不匹配yyyyMMdd，如"修复日期":"40917"
                return null == localDate ? LocalDate.MIN : localDate;
            } else {
                LocalDate result = null;
                boolean resultOk = false;
                while (!resultOk) {
                    try {
                        result = LocalDate.parse(localDateStr, dateFormatter);
                        resultOk = true;
                    } catch (DateTimeException ex) {
                        localDateStr = String.valueOf(Integer.parseInt(trim(localDateStr)) - 1);
                    }
                }
                return result;
            }
        } catch (Exception ex) {
            return localDate;
        }
    }

    private Date getDateFromWorkshopFaultLocalDateAndTimeStr(LocalDate localDate, String timeStr) {
        // 16:30
        // 8:33
        // 16:30:00
        // 次日2:48
        // 次日2：:00
        // 下午 11:30:00
        // 上午 12:40:00
        // 下午 3:30:00
        // 2013.10.18
        // 11;00
        // 6；30
        // 08:17、08:30、08:38
        // 16:16、17：38
        // 10:50/18:41
        // 7:27 20:21

        if (StringUtils.isBlank(timeStr)) {
            timeStr = "00:00:00";
        }
        if (timeStr.contains(".")) {
            // 2013.10.18
            timeStr = "00:00:00";
        }

        boolean isAfternoon = false;
        boolean isTomorrow = false;
        LocalTime localTime;
        try {
            timeStr = timeStr.trim();
            while (timeStr.contains("、")) {
                timeStr = timeStr.split("、")[0];
            }
            while (timeStr.contains("：")) {
                timeStr = timeStr.replaceAll("：", ":").trim();
            }
            while (timeStr.contains("；")) {
                timeStr = timeStr.replaceAll("；", ":").trim();
            }
            while (timeStr.contains(";")) {
                timeStr = timeStr.replaceAll(";", ":").trim();
            }
            while (timeStr.contains("::")) {
                timeStr = timeStr.replaceAll("::", ":").trim();
            }
            while (timeStr.contains("下午")) {
                timeStr = timeStr.replaceAll("下午", "").trim();
                isAfternoon = true;
            }
            while (timeStr.contains("上午")) {
                timeStr = timeStr.replaceAll("上午", "").trim();
            }
            while (timeStr.contains("次日")) {
                timeStr = timeStr.replaceAll("次日", "").trim();
                isTomorrow = true;
            }
//            while (timeStr.contains(" ")) {
//                timeStr = timeStr.replaceAll(" ", "").trim();
//            }

            String[] split = timeStr.split(":");
            String hour = split[0].trim();
            if (hour.length() < 2) {
                hour = "0" + hour;
            }
            String minute = split[1].trim();
            if (minute.length() < 2) {
                minute = "0" + minute;
            }
            timeStr = hour + ":" + minute + ":00";

            localTime = LocalTime.parse(timeStr, timeFormatter);
        } catch (Exception ex) {
            localTime = LocalTime.MIN;
        }
        Date date = Date.from(LocalDateTime.of(localDate, localTime).atZone(ZoneId.systemDefault()).toInstant());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (isTomorrow) {
            calendar.add(Calendar.DATE, 1);
        }
        if (isAfternoon) {
            calendar.add(Calendar.HOUR, 12);
        }
        return calendar.getTime();
    }

    private Map<String, String> toPropertyMap(List<Map<String, Object>> mapList, String keyStr, String valueStr) {
        Map<String, String> propertyMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map<String, Object> map : mapList) {
                Object key = map.get(keyStr);
                Object value = map.get(valueStr);

                if (null != value && null != key) {
                    propertyMap.put((String) key, (String) value);
                }
            }
        }
        return propertyMap;
    }

    private String cutString(String var, int length, boolean addEllipsis) {
        if (StringUtils.isBlank(var)) {
            return null;
        }

        var = var.trim();
        if (StringUtils.isNotBlank(var) && var.length() > length) {
            if (addEllipsis) {
                return var.substring(0, length - 6) + "......";
            } else {
                return var.substring(0, length);
            }
        }

        return var;
    }

    private String getFaultStatusText(Integer status) {
        String statusText = "";
        if (null == status) {
            return statusText;
        }
        /**
         * 未处理	0
         * 已处理	1
         * 已关闭	2
         * 遗留故障	3
         */
        switch (status) {
            case 0:
                statusText = "未处理";
                break;
            case 1:
                statusText = "已处理";
                break;
            case 2:
                statusText = "已关闭";
                break;
            case 3:
                statusText = "遗留故障";
                break;
            default:
                break;
        }
        return statusText;
    }

    private String getFaultFromTypeText(Integer fromType) {
        String fromTypeText = "";
        if (null == fromType) {
            return fromTypeText;
        }
        /**
         * 1手动创建 2导入历史故障 3同步历史质保故障
         */
        switch (fromType) {
            case 1:
                fromTypeText = "手动创建";
                break;
            case 2:
                fromTypeText = "导入历史故障";
                break;
            case 3:
                fromTypeText = "同步历史质保故障";
                break;
            default:
                break;
        }
        return fromTypeText;
    }

}
