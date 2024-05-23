package org.jeecg.modules.tiros.rpt.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.tiros.dataisolation.service.LineWorkshopCompanyService;
import org.jeecg.common.tiros.rpt.service.AlertCheckService;
import org.jeecg.common.tiros.rpt.service.HomepageItemRptService;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.alertaccept.entity.BuBaseAlert;
import org.jeecg.modules.basemanage.alertaccept.mapper.BuBaseAlertAcceptMapper;
import org.jeecg.modules.basemanage.alertaccept.mapper.BuBaseAlertMapper;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeAttrMapper;
import org.jeecg.modules.material.material.service.BuMaterialTypeReplaceService;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.service.BuMaterialStockService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 生成预警信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-24
 */
@Slf4j
@Service
public class AlertCheckServiceImpl implements AlertCheckService {

    @Resource
    private BuBaseAlertMapper buBaseAlertMapper;
    @Resource
    private BuBaseAlertAcceptMapper buBaseAlertAcceptMapper;
    @Resource
    private BuMaterialTypeAttrMapper buMaterialTypeAttrMapper;
    @Resource
    private BuMaterialStockService buMaterialStockService;
    @Resource
    private BuMaterialTypeReplaceService buMaterialTypeReplaceService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private HomepageItemRptService homepageItemRptService;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private LineWorkshopCompanyService lineWorkshopCompanyService;


    /**
     * @see AlertCheckService#generateAlertInfo(Integer)
     */
    @Override
    public boolean generateAlertInfo(Integer alertType) throws RuntimeException {
        List<Integer> alertTypeList;
        if (null == alertType) {
            alertTypeList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        } else {
            alertTypeList = Collections.singletonList(alertType);
        }

        Date now = new Date();
        // TODO-zhaiyantao 2023/2/7 15:31 注释掉物料预警，等物资预警功能重做后再启用
//        if (alertTypeList.contains(1)) {
//            // 1物资库存预警=物料库存预警
//            addMaterialStockAlert(now);
//        }
//        if (alertTypeList.contains(2)) {
//            // 2物资到期预警=物资有效期预警
//            addMaterialExpireAlert(now);
//        }
        if (alertTypeList.contains(3)) {
            // 3工器具送检预警=器具送检预警
            addToolCheckAlert(now);
        }
        if (alertTypeList.contains(4)) {
            // 4部件质保预警=部件质保期预警
            addOutsourceAssetExpireAlert(now);
        }
        if (alertTypeList.contains(5)) {
            // 5测量异常预警=测量数据预警
            addMeasureAlert(now);
        }
        if (alertTypeList.contains(6)) {
            // 6委外逾期预警=委外逾期预警
            addDelayOutsourceOutinDetailAlert(now);
        }
        if (alertTypeList.contains(7)) {
            // 7工单逾期预警=逾期工单预警
            addDelayOrderAlert(now);
        }
        if (alertTypeList.contains(8)) {
            // 8故障处理预警=未处理故障预警
            addUnHandleFaultAlert(now);
        }

        // 更新首页预警区数据
        homepageItemRptService.rewriteAlertItem();
        return true;
    }

    void saveAlertList(List<BuBaseAlert> needAddAlertList, String msg) {
        if (CollectionUtils.isEmpty(needAddAlertList)) {
            return;
        }

        // 保存预警信息
        List<List<BuBaseAlert>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddAlertList);
        for (List<BuBaseAlert> batchSub : batchSubList) {
            buBaseAlertMapper.insertList(batchSub);
        }

        // 发送预警消息
        addAlertSendAnnouncement(needAddAlertList);
    }

    /**
     * 删除指定类型的未处理的预警记录（好出入新的啊）
     */
    void deleteAlertByType(int type) {
        // 查询现有的状态=0新增的预警信息，用于过滤，不生成重复的预警信息
        LambdaQueryWrapper<BuBaseAlert> alertWrapper = new LambdaQueryWrapper<BuBaseAlert>()
                .eq(BuBaseAlert::getStatus, 0)
                .eq(BuBaseAlert::getAlertType, type);

        buBaseAlertMapper.delete(alertWrapper);
    }

    void addMaterialStockAlert(Date now) {
        List<BuBaseAlert> needAddAlertList = new ArrayList<>();
        // 查询安全库存量
        List<BuMaterialTypeAttr> attrList = buMaterialTypeAttrMapper.selectValidThresholdList();

        int alertType = 1;

        deleteAlertByType(alertType);
        if (CollectionUtils.isEmpty(attrList)) {
            return;
        }

        // 查询物资库存
        List<String> materialTypeIdList = attrList.stream()
                .map(BuMaterialTypeAttr::getMaterialTypeId)
                .distinct()
                .collect(Collectors.toList());
        List<BuMaterialStock> allStockList = buMaterialStockService.listStockByMaterialTypeIdList(materialTypeIdList, true);
        // 查询可替换物资
        Map<String, List<String>> materialTypeIdReplaceIdListMap = buMaterialTypeReplaceService.mapCanReplaceMaterialTypeIdListByMaterialTypeIdList(materialTypeIdList);

        Set<String> companyIdSet = new HashSet<>();
        Set<String> workshopIdSet = new HashSet<>();
        Set<String> lineIdSet = new HashSet<>();
        for (BuMaterialStock stock : allStockList) {
            companyIdSet.add(stock.getCompanyId());
            workshopIdSet.add(stock.getWorkshopId());
            lineIdSet.add(stock.getLineId());
        }

        Map<String, Map<String, Set<String>>> companyMap = getValidCompanyWorkshopLineMap(companyIdSet, workshopIdSet, lineIdSet);
        for (Map.Entry<String, Map<String, Set<String>>> companyEntry : companyMap.entrySet()) {
            String companyId = companyEntry.getKey();
            Map<String, Set<String>> workshopMap = companyEntry.getValue();
            for (Map.Entry<String, Set<String>> workshopEntry : workshopMap.entrySet()) {
                String workshopId = workshopEntry.getKey();
                Set<String> lineSet = workshopEntry.getValue();
                for (String lineId : lineSet) {
                    List<BuMaterialStock> stockList = allStockList.stream()
                            .filter(item -> lineId.equals(item.getLineId())
                                    && workshopId.equals(item.getWorkshopId())
                                    && companyId.equals(item.getCompanyId()))
                            .collect(Collectors.toList());

                    for (BuMaterialTypeAttr attr : attrList) {
                        BigDecimal theshold = DataTypeCastUtil.transNumber(attr.getTheshold());
                        String materialTypeId = attr.getMaterialTypeId();
                        String materialTypeName = attr.getMaterialTypeName();

                        // 对比库存
                        Set<String> materialTypeIdSet = new HashSet<>(Collections.singletonList(materialTypeId));
                        List<String> replaceIdList = materialTypeIdReplaceIdListMap.get(materialTypeId);
                        if (CollectionUtils.isNotEmpty(replaceIdList)) {
                            materialTypeIdSet.addAll(replaceIdList);
                        }
                        BigDecimal currentStock = stockList.stream()
                                .filter(item -> materialTypeIdSet.contains(item.getMaterialTypeId()))
                                .map(BuMaterialStock::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (currentStock.compareTo(theshold) < 0) {
                            // 当前库存小于安全库存量，预警
                            String alertContent = String.format("【%s】物资当前库存量【%s】，少于【%s】", materialTypeName, currentStock, theshold);
                            BuBaseAlert alert = new BuBaseAlert()
                                    .setId(UUIDGenerator.generate())
                                    .setAlertType(alertType)
                                    .setAlertObjId(materialTypeId)
                                    .setAlertContent(alertContent)
                                    .setGenNotice(1)
                                    .setStatus(0)
                                    .setCreateTime(now)
                                    .setCreateBy("admin")
                                    .setCompanyId(companyId)
                                    .setWorkshopId(workshopId)
                                    .setLineId(lineId);
                            needAddAlertList.add(alert);
                        }
                    }
                }
            }
        }

        saveAlertList(needAddAlertList, "物资库存预警记录");
    }

    void addMaterialExpireAlert(Date now) {
        List<BuBaseAlert> needAddAlertList = new ArrayList<>();
        Date materialExpireDateLine;
        String configDay = sysConfigService.getConfigValueByCode(TirosConstant.MATERIAL_EXPIRE_WARNING_CODE);
        if (StringUtils.isNotBlank(configDay)) {
            materialExpireDateLine = new Date(System.currentTimeMillis() + (Long.parseLong(configDay) * 24 * 60 * 60 * 1000));
        } else {
            materialExpireDateLine = new Date();
        }
        List<Map<String, Object>> expireAlertList = buBaseAlertMapper.countMaterialExpireAlert(materialExpireDateLine);

        int alertType = 2;
        deleteAlertByType(alertType);
        if (CollectionUtils.isEmpty(expireAlertList)) {
            return;
        }

        for (Map<String, Object> map : expireAlertList) {
            String companyId = (String) map.get("companyId");
            String workshopId = (String) map.get("workshopId");
            String lineId = (String) map.get("lineId");
            String entryDetailId = (String) map.get("entryDetailId");
            String materialTypeName = (String) map.get("materialTypeName");
            Date entryDate = DataTypeCastUtil.transDate(map.get("entryDate"));
            Date expireDate = DataTypeCastUtil.transDate(map.get("expireDate"));

            String alertContent = String.format("【%s】物资入库日期为【%s】，将到达质保期【%s】", materialTypeName, transToDateString(entryDate), transToDateString(expireDate));
            BuBaseAlert alert = new BuBaseAlert()
                    .setId(UUIDGenerator.generate())
                    .setAlertType(alertType)
                    .setAlertObjId(entryDetailId)
                    .setAlertContent(alertContent)
                    .setGenNotice(1)
                    .setStatus(0)
                    .setCreateTime(now)
                    .setCreateBy("admin")
                    .setCompanyId(companyId)
                    .setWorkshopId(workshopId)
                    .setLineId(lineId);
            needAddAlertList.add(alert);
        }

        saveAlertList(needAddAlertList, "物资过期预警记录");
    }

    void addToolCheckAlert(Date now) {
        List<BuBaseAlert> needAddAlertList = new ArrayList<>();
        Date toolsCheckDateLine;
        String configDay = sysConfigService.getConfigValueByCode(TirosConstant.TOOLS_CHECK_EARLIER_WARNING_CODE);
        if (StringUtils.isNotBlank(configDay)) {
            toolsCheckDateLine = new Date(System.currentTimeMillis() + (Long.parseLong(configDay) * 24 * 60 * 60 * 1000));
        } else {
            toolsCheckDateLine = new Date(now.getTime() + TirosConstant.TOOLS_CHECK_EARLIER_WARNING_TIME);
        }

        // 已送检（送检中12）、已送修（送修中11）、报废审批中（报废审批中6）、已报废（已报废15）、报废（报废19）、停用（停用16）的状态下，这些工具的数量不要记录在送检预警
        List<Integer> notAlertToolStatusList = Arrays.asList(12, 11, 6, 15, 19, 16);
        List<Map<String, Object>> checkAlertList = buBaseAlertMapper.countToolsCheckAlert(toolsCheckDateLine, notAlertToolStatusList);
        if (CollectionUtils.isEmpty(checkAlertList)) {
            return;
        }

        // 删除旧的未处理预警
        int alertType = 3;
//        deleteAlertByType(alertType);
        // 查询已存在未处理的预警，不重复生成
        LambdaQueryWrapper<BuBaseAlert> alertWrapper = new LambdaQueryWrapper<BuBaseAlert>()
                .eq(BuBaseAlert::getStatus, 0)
                .eq(BuBaseAlert::getAlertType, alertType);
        List<BuBaseAlert> oldUnhandledAlertList = buBaseAlertMapper.selectList(alertWrapper);
        List<String> oldUnhandledAlertObjIdList = oldUnhandledAlertList.stream()
                .map(BuBaseAlert::getAlertObjId)
                .distinct()
                .collect(Collectors.toList());

        for (Map<String, Object> map : checkAlertList) {
            String toolId = (String) map.get("toolId");
            if (oldUnhandledAlertObjIdList.contains(toolId)) {
                // 已存在未处理的预警，不重复生成
                continue;
            }

            String companyId = (String) map.get("companyId");
            String workshopId = (String) map.get("workshopId");
            String lineId = (String) map.get("lineId");
            String toolName = (String) map.get("toolName");
            Date nextCheckTime = DataTypeCastUtil.transDate(map.get("nextCheckTime"));

            String alertContent = String.format("【%s】工器具需在【%s】前送检", toolName, transToDateString(nextCheckTime));
            BuBaseAlert alert = new BuBaseAlert()
                    .setId(UUIDGenerator.generate())
                    .setAlertType(alertType)
                    .setAlertObjId(toolId)
                    .setAlertContent(alertContent)
                    .setGenNotice(1)
                    .setStatus(0)
                    .setCreateTime(now)
                    .setCreateBy("admin")
                    .setCompanyId(companyId)
                    .setWorkshopId(workshopId)
                    .setLineId(lineId);
            needAddAlertList.add(alert);
        }

        saveAlertList(needAddAlertList, "工器具送检预警");
    }

    void addOutsourceAssetExpireAlert(Date now) {
        List<BuBaseAlert> needAddAlertList = new ArrayList<>();
        Date outsourceAssetQualityDateLine = new Date(now.getTime() + TirosConstant.OUTSOURCE_ASSET_QUALITY_EARLIER_WARNING_TIME);
        List<Map<String, Object>> outsourceAssetExpireAlertList = buBaseAlertMapper.countOutsourceTrainAssetExpireAlert(outsourceAssetQualityDateLine);

        int alertType = 4;
        deleteAlertByType(alertType);
        if (CollectionUtils.isEmpty(outsourceAssetExpireAlertList)) {
            return;
        }

        for (Map<String, Object> map : outsourceAssetExpireAlertList) {
            String companyId = (String) map.get("companyId");
            String workshopId = (String) map.get("workshopId");
            String lineId = (String) map.get("lineId");
            String turnoverAssetId = (String) map.get("turnoverAssetId");
            String assetName = (String) map.get("assetName");
            Date expireDate = DataTypeCastUtil.transDate(map.get("expireDate"));
            String billNo = (String) map.get("billNo");

            String alertContent = String.format("【%s】部件将到达质保期【%s】，委外交接单号【%s】", assetName, transToDateString(expireDate), billNo);
            BuBaseAlert alert = new BuBaseAlert()
                    .setId(UUIDGenerator.generate())
                    .setAlertType(alertType)
                    .setAlertObjId(turnoverAssetId)
                    .setAlertContent(alertContent)
                    .setGenNotice(1)
                    .setStatus(0)
                    .setCreateTime(now)
                    .setCreateBy("admin")
                    .setCompanyId(companyId)
                    .setWorkshopId(workshopId)
                    .setLineId(lineId);
            needAddAlertList.add(alert);
        }

        saveAlertList(needAddAlertList, "委外部件质保预警");
    }

    void addMeasureAlert(Date now) {
        List<BuBaseAlert> needAddAlertList = new ArrayList<>();
        List<Map<String, Object>> measureAlertList = buBaseAlertMapper.countMeasureAlert();

        int alertType = 5;
        // 查询已存在的测量异常预警
        deleteAlertByType(alertType);
        if (CollectionUtils.isEmpty(measureAlertList)) {
            return;
        }

        for (Map<String, Object> map : measureAlertList) {
            String companyId = (String) map.get("companyId");
            String workshopId = (String) map.get("workshopId");
            String lineId = (String) map.get("lineId");
            String formValuesId = (String) map.get("formValuesId");
            String alertMessage = (String) map.get("alertMessage");

            BuBaseAlert alert = new BuBaseAlert()
                    .setId(UUIDGenerator.generate())
                    .setAlertType(alertType)
                    .setAlertObjId(formValuesId)
                    .setAlertContent(alertMessage)
                    .setGenNotice(1)
                    .setStatus(0)
                    .setCreateTime(now)
                    .setCreateBy("admin")
                    .setCompanyId(companyId)
                    .setWorkshopId(workshopId)
                    .setLineId(lineId);
            needAddAlertList.add(alert);
        }

        saveAlertList(needAddAlertList, "测量预警记录");
    }

    void addDelayOutsourceOutinDetailAlert(Date now) {
        List<BuBaseAlert> needAddAlertList = new ArrayList<>();
        Date outsourceOutinDetailDelayDateLine = new Date(now.getTime() + TirosConstant.OUTSOURCE_OUTIN_DETAIL_EARLIER_WARNING_TIME);
        List<Map<String, Object>> delayOutsourceOutinDetailList = buBaseAlertMapper.countDelayOutsourceOutinDetail(outsourceOutinDetailDelayDateLine);

        int alertType = 6;
        deleteAlertByType(alertType);
        if (CollectionUtils.isEmpty(delayOutsourceOutinDetailList)) {
            return;
        }
        for (Map<String, Object> map : delayOutsourceOutinDetailList) {
            String companyId = (String) map.get("companyId");
            String workshopId = (String) map.get("workshopId");
            String lineId = (String) map.get("lineId");
            String outinDetailId = (String) map.get("outinDetailId");
            Date finishTime = DataTypeCastUtil.transDate(map.get("finishTime"));
            String assetName = (String) map.get("assetName");
            String billNo = (String) map.get("billNo");

            String alertContent = String.format("【%s】部件的委外任务将到返厂时间【%s】，委外交接单号【%s】", assetName, transToDateString(finishTime), billNo);
            BuBaseAlert alert = new BuBaseAlert()
                    .setId(UUIDGenerator.generate())
                    .setAlertType(alertType)
                    .setAlertObjId(outinDetailId)
                    .setAlertContent(alertContent)
                    .setGenNotice(1)
                    .setStatus(0)
                    .setCreateTime(now)
                    .setCreateBy("admin")
                    .setCompanyId(companyId)
                    .setWorkshopId(workshopId)
                    .setLineId(lineId);
            needAddAlertList.add(alert);
        }

        saveAlertList(needAddAlertList, "委外部件返厂逾期记录");
    }

    void addDelayOrderAlert(Date now) {
        List<BuBaseAlert> needAddAlertList = new ArrayList<>();
        List<Map<String, Object>> delayOrderList = buBaseAlertMapper.countDelayOrder(DateUtils.transToDayStart(now));

        int alertType = 7;
        deleteAlertByType(alertType);
        if (CollectionUtils.isEmpty(delayOrderList)) {
            return;
        }
        for (Map<String, Object> map : delayOrderList) {
            String companyId = (String) map.get("companyId");
            String workshopId = (String) map.get("workshopId");
            String lineId = (String) map.get("lineId");
            String orderId = (String) map.get("orderId");
            String orderCode = (String) map.get("orderCode");
            String orderName = (String) map.get("orderName");
            Date finishTime = DataTypeCastUtil.transDate(map.get("finishTime"));

            String alertContent = String.format("【%s】工单逾期，计划完成时间【%s】，工单编号【%s】", orderName, transToDateString(finishTime), orderCode);
            BuBaseAlert alert = new BuBaseAlert()
                    .setId(UUIDGenerator.generate())
                    .setAlertType(alertType)
                    .setAlertObjId(orderId)
                    .setAlertContent(alertContent)
                    .setGenNotice(1)
                    .setStatus(0)
                    .setCreateTime(now)
                    .setCreateBy("admin")
                    .setCompanyId(companyId)
                    .setWorkshopId(workshopId)
                    .setLineId(lineId);
            needAddAlertList.add(alert);
        }

        saveAlertList(needAddAlertList, "工单逾期预警记录");
    }

    void addUnHandleFaultAlert(Date now) {
        List<BuBaseAlert> needAddAlertList = new ArrayList<>();
        List<Map<String, Object>> unHandleFaultList = buBaseAlertMapper.countUnHandleFault();

        int alertType = 8;
        deleteAlertByType(alertType);
        if (CollectionUtils.isEmpty(unHandleFaultList)) {
            return;
        }
        for (Map<String, Object> map : unHandleFaultList) {
            String companyId = (String) map.get("companyId");
            String workshopId = (String) map.get("workshopId");
            String lineId = (String) map.get("lineId");
            String faultId = (String) map.get("faultId");
            String faultSn = (String) map.get("faultSn");
            Date reportTime = DataTypeCastUtil.transDate(map.get("reportTime"));

            String alertContent = String.format("故障未处理，故障提报时间【%s】，故障编号【%s】", transToDateString(reportTime), faultSn);
            BuBaseAlert alert = new BuBaseAlert()
                    .setId(UUIDGenerator.generate())
                    .setAlertType(alertType)
                    .setAlertObjId(faultId)
                    .setAlertContent(alertContent)
                    .setGenNotice(1)
                    .setStatus(0)
                    .setCreateTime(now)
                    .setCreateBy("admin")
                    .setCompanyId(companyId)
                    .setWorkshopId(workshopId)
                    .setLineId(lineId);
            needAddAlertList.add(alert);
        }

        saveAlertList(needAddAlertList, "故障超时处理记录");
    }

    private List<BuBaseAlert> getExistValidAlertObjIdList(int alertType) {
        // 查询现有的状态=0新增的预警信息，用于过滤，不生成重复的预警信息
        LambdaQueryWrapper<BuBaseAlert> alertWrapper = new LambdaQueryWrapper<BuBaseAlert>()
                .eq(BuBaseAlert::getStatus, 0)
                .eq(BuBaseAlert::getAlertType, alertType)
                .orderByAsc(BuBaseAlert::getCreateTime)
                .select(BuBaseAlert::getAlertObjId, BuBaseAlert::getId);
        List<BuBaseAlert> existAlertList = buBaseAlertMapper.selectList(alertWrapper);
        return existAlertList;
       /* return existAlertList.stream()
                .map(BuBaseAlert::getAlertObjId)
                .distinct()
                .collect(Collectors.toList());*/
    }

    private String transToDateString(Date date) {
        if (null == date) {
            return "";
        }

        return DateUtils.date_sdf.get().format(date);
    }

    private void addAlertSendAnnouncement(List<BuBaseAlert> addAlertList) {
        if (CollectionUtils.isEmpty(addAlertList)) {
            return;
        }

        int count = 0;
        Map<Integer, List<BuBaseAlert>> alertTypeAlertListMap = addAlertList.stream()
                .collect(Collectors.groupingBy(BuBaseAlert::getAlertType));
        for (Map.Entry<Integer, List<BuBaseAlert>> alertTypeAlertListEntry : alertTypeAlertListMap.entrySet()) {
            Integer alertType = alertTypeAlertListEntry.getKey();
            if (null == alertType) {
                continue;
            }
            String alertTypeName = getAlertTypeName(alertType);
            List<BuBaseAlert> alertList = alertTypeAlertListEntry.getValue();
            if (CollectionUtils.isEmpty(alertList)) {
                continue;
            }

            List<String> usernameList = buBaseAlertAcceptMapper.selectUsernameListByAlertType(alertType);
            if (CollectionUtils.isEmpty(usernameList)) {
                continue;
            }

            count = sendMessageByNewThread(alertTypeName, alertList, usernameList);
        }
    }

    private String getAlertTypeName(Integer alertType) {
        String result = "预警";

        // 1物资库存预警 2物资到期预警 3工器具送检预警 4部件质保预警 5测量异常预警 6委外逾期预警 7工单逾期预警 8故障处理预警
        switch (alertType) {
            case 1:
                result = "物资库存预警";
                break;
            case 2:
                result = "物资到期预警";
                break;
            case 3:
                result = "工器具送检预警";
                break;
            case 4:
                result = "部件质保预警";
                break;
            case 5:
                result = "测量异常预警";
                break;
            case 6:
                result = "委外逾期预警";
                break;
            case 7:
                result = "工单逾期预警";
                break;
            case 8:
                result = "故障处理预警";
                break;
            default:
                break;
        }

        return result + "消息";
    }

    private int sendMessageByNewThread(String alertTypeName, List<BuBaseAlert> alertList, List<String> usernameList) {
        AtomicInteger count = new AtomicInteger();
        String usernames = String.join(",", usernameList);

        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                for (BuBaseAlert alert : alertList) {
                    String alertContent = alert.getAlertContent();
                    sysBaseAPI.sendSysAnnouncement("admin", usernames, alertTypeName, alertContent);
                    count.getAndIncrement();
                }
            } catch (Exception ex) {
                log.error("开线程发送消息，执行失败，原因如下：" + ex.getMessage());
                StringWriter stringWriter = new StringWriter();
                try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                    ex.printStackTrace(printWriter);
                }
                log.error(stringWriter.toString());
            }
        });
        singleExecutor.shutdown();

        return count.get();
    }

    private Map<String, Map<String, Set<String>>> getValidCompanyWorkshopLineMap(Set<String> companyIdSet, Set<String> workshopIdSet, Set<String> lineIdSet) {
        Map<String, Map<String, Set<String>>> companyMap = lineWorkshopCompanyService.treeCompanyWorkshopLine();
        return copyCompanyWorkshopLine(companyMap, companyIdSet, workshopIdSet, lineIdSet);
    }

    private Map<String, Map<String, Set<String>>> copyCompanyWorkshopLine(Map<String, Map<String, Set<String>>> companyMap,
                                                                          Set<String> companyIdSet,
                                                                          Set<String> workshopIdSet,
                                                                          Set<String> lineIdSet) {
        if (null == companyMap) {
            companyMap = new HashMap<>();
        }

        Map<String, Map<String, Set<String>>> copyCompanyMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Set<String>>> companyEntry : companyMap.entrySet()) {
            String companyId = companyEntry.getKey();
            Map<String, Set<String>> workshopMap = companyEntry.getValue();

            if (companyIdSet.contains(companyId)) {
                Map<String, Set<String>> copyWorkshopMap = new HashMap<>();
                for (Map.Entry<String, Set<String>> workshopEntry : workshopMap.entrySet()) {
                    String workshopId = workshopEntry.getKey();
                    Set<String> lineSet = workshopEntry.getValue();

                    Set<String> copyLineSet = new HashSet<>();
                    if (workshopIdSet.contains(workshopId)) {
                        for (String line : lineSet) {
                            if (lineIdSet.contains(line)) {
                                copyLineSet.add(line);
                            }
                        }
                        copyWorkshopMap.put(workshopId, copyLineSet);
                    }
                }
                copyCompanyMap.put(companyId, copyWorkshopMap);
            }
        }
        return copyCompanyMap;
    }

}
