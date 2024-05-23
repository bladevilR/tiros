package org.jeecg.modules.third.task;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.*;
import org.jeecg.modules.third.jdx.service.*;
import org.jeecg.modules.third.maximo.bean.*;
import org.jeecg.modules.third.maximo.service.*;
import org.jeecg.modules.third.trans.bean.BuMaximoTransData;
import org.jeecg.modules.third.trans.service.BuMaximoTransDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 扫描并同步数据到maximo 定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-06
 */
@Slf4j
@Component
@EnableScheduling
public class TransToMaximoTask {

    @Resource
    private MxinInterTransService mxinInterTransService;
    @Resource
    private BuWorkOrderThirdService buWorkOrderThirdService;
    @Resource
    private JdxWoInService jdxWoInService;
    @Resource
    private BuMaterialAssignDetailThirdService buMaterialAssignDetailThirdService;
    @Resource
    private BuMaterialReturnedDetailThirdService buMaterialReturnedDetailThirdService;
    @Resource
    private BuRepairTaskStaffArrangeThirdService buRepairTaskStaffArrangeThirdService;
    @Resource
    private JdxMatusetransInService jdxMatusetransInService;
    @Resource
    private JdxLabtransInService jdxLabtransInService;
    @Resource
    private BuFaultInfoThirdService buFaultInfoThirdService;
    @Resource
    private JdxSrInService jdxSrInService;
    @Resource
    private BuMaximoTransDataService buMaximoTransDataService;
    @Resource
    private JdxInvbalancesOutService jdxInvbalancesOutService;

    @Value("${serviceTask.transToMaximo.all.enable}")
    private String enableTask;

    @Value("${serviceTask.transToMaximo.all.notTransToMaximoType}")
    private String notTransToMaximoType;


    @Scheduled(fixedDelayString = "${serviceTask.transToMaximo.all.interval}")
    private void checkAndDealTransData() {
        if ("false".equals(enableTask)) {
            return;
        }

        try {
            Date now = new Date();

            // 扫描maximo数据同步中间表数据
            List<BuMaximoTransData> transDataList = buMaximoTransDataService.listAllNeedTransTransData();

            // 过滤掉配置中不同步到maximo的数据类型
            if (StringUtils.isNotBlank(notTransToMaximoType)) {
                String[] types = notTransToMaximoType.split(",");
                List<Integer> typeList = new ArrayList<>();
                for (String type : types) {
                    typeList.add(Integer.parseInt(type));
                }
                transDataList.removeIf(transData -> typeList.contains(transData.getType()));
            }

            //TODO-zhaiyantao 2021/8/1 13:12 过滤掉人员工时中员工号长度大于8的数据，以后再找为什么工号大于8，以及工号大于8的能不能存在于maximo
            Set<String> userWorkNoNotMatchTransDataIdSet = new HashSet<>();
            for (BuMaximoTransData transData : transDataList) {
                if (6 == transData.getType()) {
                    BuRepairTaskStaffArrange staffArrange = JSON.parseObject(transData.getObjJson(), BuRepairTaskStaffArrange.class);
                    if (null != staffArrange && StringUtils.isNotBlank(staffArrange.getWorkNo()) && staffArrange.getWorkNo().length() > 8) {
                        userWorkNoNotMatchTransDataIdSet.add(transData.getId());
//                        log.error("员工号[" + staffArrange.getWorkNo() + "]长度大于8，无法进行写入人员工时，人员工时信息：" + transData.getObjJson());
                    }
                }
            }
            transDataList.removeIf(transData -> userWorkNoNotMatchTransDataIdSet.contains(transData.getId()));

            // 如果有未消耗成功的物料消耗或物料退库，则不能关闭工单
            // 如果有未处理的人员工时，则不能关闭工单
            // 如果有未处理的修改工单，则不能关闭工单
            List<BuMaximoTransData> closeOrderList = transDataList.stream()
                    .filter(transData -> 2 == transData.getType())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(closeOrderList)) {
                // 本次的消耗记录、人员工时
                List<BuMaximoTransData> notTransList = transDataList.stream()
                        .filter(transData -> Arrays.asList(3, 4, 6, 7).contains(transData.getType()))
                        .collect(Collectors.toList());
                // 以前的消耗记录
                List<BuMaximoTransData> notSuccessMaterialCostList = buMaximoTransDataService.listAllTransferredNotSuccessMaterialTransData();
                List<BuMaximoTransData> notTransferredMaterialCostList = buMaximoTransDataService.listAllNotTransferredMaterialTransData();
                List<BuMaximoTransData> notTransferredOrderReplaceList = buMaximoTransDataService.listAllNotTransferredOrderReplaceTransData();
                notTransList.addAll(notSuccessMaterialCostList);
                notTransList.addAll(notTransferredMaterialCostList);
                notTransList.addAll(notTransferredOrderReplaceList);

                if (CollectionUtils.isNotEmpty(notTransList)) {
                    Set<String> cannotTransOrderCodeSet = new HashSet<>();
                    for (BuMaximoTransData transData : notTransList) {
                        if (3 == transData.getType()) {
                            BuMaterialAssignDetail assignDetail = JSON.parseObject(transData.getObjJson(), BuMaterialAssignDetail.class);
                            if (null != assignDetail) {
                                cannotTransOrderCodeSet.add(assignDetail.getOrderCode());
                            }
                        } else if (4 == transData.getType()) {
                            BuMaterialReturnedDetail returnedDetail = JSON.parseObject(transData.getObjJson(), BuMaterialReturnedDetail.class);
                            if (null != returnedDetail) {
                                cannotTransOrderCodeSet.add(returnedDetail.getOrderCode());
                            }
                        } else if (6 == transData.getType()) {
                            BuRepairTaskStaffArrange staffArrange = JSON.parseObject(transData.getObjJson(), BuRepairTaskStaffArrange.class);
                            if (null != staffArrange) {
                                cannotTransOrderCodeSet.add(staffArrange.getOrderCode());
                            }
                        } else if (7 == transData.getType()) {
                            BuWorkOrder orderReplace = JSON.parseObject(transData.getObjJson(), BuWorkOrder.class);
                            if (null != orderReplace) {
                                cannotTransOrderCodeSet.add(orderReplace.getOrderCode());
                            }
                        }
                    }

                    Set<String> cannotTransTransDataIdSet = new HashSet<>();
                    for (BuMaximoTransData transData : closeOrderList) {
                        BuWorkOrder order = JSON.parseObject(transData.getObjJson(), BuWorkOrder.class);
                        if (cannotTransOrderCodeSet.contains(order.getOrderCode())) {
                            cannotTransTransDataIdSet.add(transData.getId());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(cannotTransTransDataIdSet)) {
                        transDataList.removeIf(transData -> cannotTransTransDataIdSet.contains(transData.getId()));
                    }
                }
            }

            if (CollectionUtils.isEmpty(transDataList)) {
                return;
            }

            int addOrderCount = 0;
            int replaceOrderCount = 0;
            int consumeMaterialCount = 0;
            int returnMaterialCount = 0;
            int userTimeCount = 0;
            int closeOrderCount = 0;
            int addFaultCount = 0;
            int replaceFaultCount = 0;
            int deleteFaultCount = 0;
            List<String> assignDetailIdList = new ArrayList<>();
            List<String> returnDetailIdList = new ArrayList<>();

            // 做下排序，按顺序处理
//            Map<Integer, List<BuMaximoTransData>> typeTransDataListMap = transDataList.stream()
//                    .collect(Collectors.groupingBy(BuMaximoTransData::getType));
            Integer[] transToMaximoTypes = MaximoThirdConstant.TRANS_TO_MAXIMO_TYPES;
            for (Integer type : transToMaximoTypes) {
//                List<BuMaximoTransData> typeTransDataList = typeTransDataListMap.get(type);
                List<BuMaximoTransData> typeTransDataList = transDataList.stream()
                        .filter(data -> type.equals(data.getType()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(typeTransDataList)) {
                    continue;
                }

                for (BuMaximoTransData transData : typeTransDataList) {
                    String objJson = transData.getObjJson();
                    long transId = mxinInterTransService.getInTransId();

                    // 1新增工单
                    if (1 == type) {
                        BuWorkOrder order = JSON.parseObject(objJson, BuWorkOrder.class);
                        if (null != order.getActFinish() && order.getActFinish().after(now)) {
                            // 工单实际结束时间晚于当前时间，不能传输
                            continue;
                        }

                        JdxWoIn maximoOrder = buWorkOrderThirdService.getMaximoOrderNeedWriteByOrder(order);

                        if (null != maximoOrder) {
                            maximoOrder.setTransid(transId);

                            MxinInterTrans inTrans = new MxinInterTrans()
                                    .setTransid(transId)
                                    .setAction("Add")
                                    .setExtsysname("JDX")
                                    .setIfacename(MaximoThirdConstant.IFACENAME_ORDER)
                                    .setTranslanguage("ZH");

                            // 保存数据
                            boolean saveFlag = jdxWoInService.saveOneAndInTrans(maximoOrder, inTrans);
                            if (saveFlag) {
                                // 回写transId到数据同步中间表
                                transData.setTransId(transId)
                                        .setTransTime(now);
                                buMaximoTransDataService.updateTransData(transData);

                                addOrderCount++;
                            }
                        }
                    }

                    // 7修改工单
                    if (7 == type) {
                        BuWorkOrder order = JSON.parseObject(objJson, BuWorkOrder.class);
                        if (null != order.getActFinish() && order.getActFinish().after(now)) {
                            // 工单实际结束时间晚于当前时间，不能传输
                            continue;
                        }

                        JdxWoIn maximoOrder = buWorkOrderThirdService.getMaximoOrderNeedWriteByOrder(order);

                        if (null != maximoOrder) {
                            maximoOrder.setTransid(transId);

                            MxinInterTrans inTrans = new MxinInterTrans()
                                    .setTransid(transId)
                                    .setAction("Replace")
                                    .setExtsysname("JDX")
                                    .setIfacename(MaximoThirdConstant.IFACENAME_ORDER)
                                    .setTranslanguage("ZH");

                            // 保存数据
                            boolean saveFlag = jdxWoInService.saveOneAndInTrans(maximoOrder, inTrans);
                            if (saveFlag) {
                                // 回写transId到数据同步中间表
                                transData.setTransId(transId)
                                        .setTransTime(now);
                                buMaximoTransDataService.updateTransData(transData);

                                replaceOrderCount++;
                            }
                        }
                    }

                    // 3物料消耗
                    if (3 == type) {
                        BuMaterialAssignDetail assignDetail = JSON.parseObject(objJson, BuMaterialAssignDetail.class);
                        boolean verifiedOrderExistAndNotClosed = jdxWoInService.checkVerifiedOrderExistAndNotClosed(assignDetail.getOrderCode());
                        if (!verifiedOrderExistAndNotClosed) {
//                            log.error("已核实未关闭工单[" + assignDetail.getOrderCode() + "]不存在，无法进行物料消耗，消耗信息：" + objJson);
                            continue;
                        }

                        JdxMatusetransIn maximoOrderMaterial = buMaterialAssignDetailThirdService.getMaximoMaterialConsumeNeedWriteByAssignDetail(assignDetail);

                        if (null != maximoOrderMaterial) {
                            // 查询并设置对应的地点值
                            //TODO-zhaiyantao 2021/7/4 21:30 更好的方法，在架大修系统的仓库表表中直接记录maximo的SITE值
                            if (StringUtils.isNotBlank(maximoOrderMaterial.getStoreloc())) {
                                String site = jdxInvbalancesOutService.getSiteByLocation(maximoOrderMaterial.getStoreloc());
                                if (StringUtils.isNotBlank(site)) {
                                    maximoOrderMaterial.setSiteid(site);
                                }
                            }

                            maximoOrderMaterial.setTransid(transId);

                            MxinInterTrans inTrans = new MxinInterTrans()
                                    .setTransid(transId)
                                    .setAction("Add")
                                    .setExtsysname("JDX")
                                    .setIfacename("JDX_MATUSETRANS")
                                    .setTranslanguage("ZH");

                            // 保存数据
                            boolean saveFlag = jdxMatusetransInService.saveOneAndInTrans(maximoOrderMaterial, inTrans);
                            if (saveFlag) {
                                // 回写transId到数据同步中间表
                                transData.setTransId(transId)
                                        .setTransTime(now);
                                buMaximoTransDataService.updateTransData(transData);

                                // 记录同步时间
                                assignDetailIdList.add(assignDetail.getId());

                                consumeMaterialCount++;
                            }
                        }
                    }

                    // 4物料退库
                    if (4 == type) {
                        BuMaterialReturnedDetail returnedDetail = JSON.parseObject(objJson, BuMaterialReturnedDetail.class);
                        boolean verifiedOrderExistAndNotClosed = jdxWoInService.checkVerifiedOrderExistAndNotClosed(returnedDetail.getOrderCode());
                        if (!verifiedOrderExistAndNotClosed) {
//                            log.error("已核实未关闭工单[" + returnedDetail.getOrderCode() + "]不存在，无法进行物料退库，退库信息：" + objJson);
                            continue;
                        }

                        JdxMatusetransIn maximoOrderMaterial = buMaterialReturnedDetailThirdService.getMaximoMaterialReturnNeedWriteByReturnedDetail(returnedDetail);

                        if (null != maximoOrderMaterial) {
                            maximoOrderMaterial.setTransid(transId);

                            // 查询并设置对应的地点值
                            //TODO-zhaiyantao 2021/7/4 21:30 更好的方法，在架大修系统的仓库表表中直接记录maximo的SITE值
                            if (StringUtils.isNotBlank(maximoOrderMaterial.getStoreloc())) {
                                String site = jdxInvbalancesOutService.getSiteByLocation(maximoOrderMaterial.getStoreloc());
                                if (StringUtils.isNotBlank(site)) {
                                    maximoOrderMaterial.setSiteid(site);
                                }
                            }

                            MxinInterTrans inTrans = new MxinInterTrans()
                                    .setTransid(transId)
                                    .setAction("Add")
                                    .setExtsysname("JDX")
                                    .setIfacename("JDX_MATUSETRANS")
                                    .setTranslanguage("ZH");

                            // 保存数据
                            boolean saveFlag = jdxMatusetransInService.saveOneAndInTrans(maximoOrderMaterial, inTrans);
                            if (saveFlag) {
                                // 回写transId到数据同步中间表
                                transData.setTransId(transId)
                                        .setTransTime(now);
                                buMaximoTransDataService.updateTransData(transData);

                                // 记录同步时间
                                returnDetailIdList.add(returnedDetail.getId());

                                returnMaterialCount++;
                            }
                        }
                    }

                    // 6人员工时
                    if (6 == type) {
                        BuRepairTaskStaffArrange staffArrange = JSON.parseObject(objJson, BuRepairTaskStaffArrange.class);
                        if (null != staffArrange.getTaskFinish() && staffArrange.getTaskFinish().after(now)) {
                            // 工时结束时间晚于当前时间，不能传输
                            continue;
                        }
                        boolean verifiedOrderExistAndNotClosed = jdxWoInService.checkVerifiedOrderExistAndNotClosed(staffArrange.getOrderCode());
                        if (!verifiedOrderExistAndNotClosed) {
//                            log.error("已核实未关闭工单[" + staffArrange.getOrderCode() + "]不存在，无法进行写入人员工时，人员工时信息：" + objJson);
                            continue;
                        }

                        JdxLabtransIn maximoWorkTime = buRepairTaskStaffArrangeThirdService.getMaximoWorkTimeNeedWriteByStaffArrange(staffArrange);

                        if (null != maximoWorkTime) {
                            maximoWorkTime.setTransid(transId);

                            MxinInterTrans inTrans = new MxinInterTrans()
                                    .setTransid(transId)
                                    .setAction("Add")
                                    .setExtsysname("JDX")
                                    .setIfacename("JDX_LABTRANS")
                                    .setTranslanguage("ZH");

                            // 保存数据
                            boolean saveFlag = jdxLabtransInService.saveOneAndInTrans(maximoWorkTime, inTrans);
                            if (saveFlag) {
                                // 回写transId到数据同步中间表
                                transData.setTransId(transId)
                                        .setTransTime(now);
                                buMaximoTransDataService.updateTransData(transData);

                                userTimeCount++;
                            }
                        }
                    }

                    // 2关闭工单
                    if (2 == type) {
                        BuWorkOrder order = JSON.parseObject(objJson, BuWorkOrder.class);
                        if (null != order.getActFinish() && order.getActFinish().after(now)) {
                            // 工单实际结束时间晚于当前时间，不能传输
                            continue;
                        }
                        boolean verifiedOrderExistAndNotClosed = jdxWoInService.checkVerifiedOrderExistAndNotClosed(order.getOrderCode());
                        if (!verifiedOrderExistAndNotClosed) {
//                            log.error("已核实未关闭工单[" + order.getOrderCode() + "]不存在，无法进行关闭工单，关闭工单信息：" + objJson);
                            continue;
                        }

                        JdxWoIn maximoOrder = buWorkOrderThirdService.getMaximoOrderNeedWriteByOrder(order);

                        if (null != maximoOrder) {
                            maximoOrder.setTransid(transId);

                            MxinInterTrans inTrans = new MxinInterTrans()
                                    .setTransid(transId)
                                    .setAction("Replace")
                                    .setExtsysname("JDX")
                                    .setIfacename(MaximoThirdConstant.IFACENAME_ORDER)
                                    .setTranslanguage("ZH");

                            // 保存数据
                            boolean saveFlag = jdxWoInService.saveOneAndInTrans(maximoOrder, inTrans);
                            if (saveFlag) {
                                // 回写transId到数据同步中间表
                                transData.setTransId(transId)
                                        .setTransTime(now);
                                buMaximoTransDataService.updateTransData(transData);

                                closeOrderCount++;
                            }
                        }
                    }

                    // 5新增故障
                    if (5 == type) {
                        BuFaultInfo fault = JSON.parseObject(objJson, BuFaultInfo.class);
                        JdxSrIn maximoFault = buFaultInfoThirdService.getMaximoFaultNeedWriteByFault(fault);

                        if (null == maximoFault) {
                            // 故障为空，不能传输
                            continue;
                        }
                        if ((null != maximoFault.getAffecteddate() && maximoFault.getAffecteddate().after(now))
                                || (null != maximoFault.getReportdate() && maximoFault.getReportdate().after(now))
                                || (null != maximoFault.getActualstart() && maximoFault.getActualstart().after(now))
                                || (null != maximoFault.getCTmprepairetime() && maximoFault.getCTmprepairetime().after(now))
                                || (null != maximoFault.getActualfinish() && maximoFault.getActualfinish().after(now))) {
                            // 故障的几个时间晚于当前时间，不能传输
                            continue;
                        }

                        maximoFault.setTransid(transId);

                        MxinInterTrans inTrans = new MxinInterTrans()
                                .setTransid(transId)
                                .setAction("Add")
                                .setExtsysname("JDX")
                                .setIfacename(MaximoThirdConstant.IFACENAME_FAULT)
                                .setTranslanguage("ZH");

                        // 保存数据
                        boolean saveFlag = jdxSrInService.saveOneAndInTrans(maximoFault, inTrans);
                        if (saveFlag) {
                            // 回写transId到数据同步中间表
                            transData.setTransId(transId)
                                    .setTransTime(now);
                            buMaximoTransDataService.updateTransData(transData);

                            addFaultCount++;
                        }
                    }

                    // 8修改故障
                    if (8 == type) {
                        BuFaultInfo fault = JSON.parseObject(objJson, BuFaultInfo.class);
                        JdxSrIn maximoFault = buFaultInfoThirdService.getMaximoFaultNeedWriteByFault(fault);

                        if (null == maximoFault) {
                            // 故障为空，不能传输
                            continue;
                        }
                        if ((null != maximoFault.getAffecteddate() && maximoFault.getAffecteddate().after(now))
                                || (null != maximoFault.getReportdate() && maximoFault.getReportdate().after(now))
                                || (null != maximoFault.getActualstart() && maximoFault.getActualstart().after(now))
                                || (null != maximoFault.getCTmprepairetime() && maximoFault.getCTmprepairetime().after(now))
                                || (null != maximoFault.getActualfinish() && maximoFault.getActualfinish().after(now))) {
                            // 故障的几个时间晚于当前时间，不能传输
                            continue;
                        }

                        maximoFault.setTransid(transId);

                        MxinInterTrans inTrans = new MxinInterTrans()
                                .setTransid(transId)
                                .setAction("Replace")
                                .setExtsysname("JDX")
                                .setIfacename(MaximoThirdConstant.IFACENAME_FAULT)
                                .setTranslanguage("ZH");

                        // 保存数据
                        boolean saveFlag = jdxSrInService.saveOneAndInTrans(maximoFault, inTrans);
                        if (saveFlag) {
                            // 回写transId到数据同步中间表
                            transData.setTransId(transId)
                                    .setTransTime(now);
                            buMaximoTransDataService.updateTransData(transData);

                            replaceFaultCount++;
                        }
                    }

                    // 9删除故障
                    if (9 == type) {
                        BuFaultInfo fault = JSON.parseObject(objJson, BuFaultInfo.class);
                        JdxSrIn maximoFault = buFaultInfoThirdService.getMaximoFaultNeedWriteByFault(fault);

                        if (null == maximoFault) {
                            // 故障为空，不能传输
                            continue;
                        }
                        if ((null != maximoFault.getAffecteddate() && maximoFault.getAffecteddate().after(now))
                                || (null != maximoFault.getReportdate() && maximoFault.getReportdate().after(now))
                                || (null != maximoFault.getActualstart() && maximoFault.getActualstart().after(now))
                                || (null != maximoFault.getCTmprepairetime() && maximoFault.getCTmprepairetime().after(now))
                                || (null != maximoFault.getActualfinish() && maximoFault.getActualfinish().after(now))) {
                            // 故障的几个时间晚于当前时间，不能传输
                            continue;
                        }

                        maximoFault.setTransid(transId);

                        MxinInterTrans inTrans = new MxinInterTrans()
                                .setTransid(transId)
                                .setAction("Delete")
                                .setExtsysname("JDX")
                                .setIfacename(MaximoThirdConstant.IFACENAME_FAULT)
                                .setTranslanguage("ZH");

                        // 保存数据
                        boolean saveFlag = jdxSrInService.saveOneAndInTrans(maximoFault, inTrans);
                        if (saveFlag) {
                            // 回写transId到数据同步中间表
                            transData.setTransId(transId)
                                    .setTransTime(now);
                            buMaximoTransDataService.updateTransData(transData);

                            deleteFaultCount++;
                        }
                    }
                }
            }

            // 更新发料单、退料单的同步时间
            buMaterialAssignDetailThirdService.updateApplySyncTime(assignDetailIdList);
            buMaterialReturnedDetailThirdService.updateReturnSyncTime(returnDetailIdList);

            // 不要在最后批量插入，不然可能导致bug：前面循环中部分数据已同步，但循环中报错了，所有的transDataL都没更新transId，导致下次还会同步
//            // 回写transId到数据同步中间表，有transId表示已同步过一次，不需要再次同步
//            buMaximoTransDataService.updateTransDataTransId(transDataList);

            int totalCount = addOrderCount + replaceOrderCount + consumeMaterialCount + returnMaterialCount + userTimeCount + closeOrderCount + addFaultCount + replaceFaultCount + deleteFaultCount;
            if (totalCount > 0) {
                log.info(String.format("同步数据到maximo：新增工单%s条，修改工单%s条，物资消耗%s条，物资退库%s条，人员工时%s条，关闭工单%s条，新增故障%s条，修改故障%s条，删除故障%s条",
                        addOrderCount, replaceOrderCount, consumeMaterialCount, returnMaterialCount, userTimeCount, closeOrderCount, addFaultCount, replaceFaultCount, deleteFaultCount));
            }
        } catch (Exception ex) {
            log.error("同步数据到maximo：定时任务执行失败，原因如下：" + ex.getMessage());
            StringWriter stringWriter = new StringWriter();
            try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                ex.printStackTrace(printWriter);
            }
            log.error(stringWriter.toString());
        }
    }

}
