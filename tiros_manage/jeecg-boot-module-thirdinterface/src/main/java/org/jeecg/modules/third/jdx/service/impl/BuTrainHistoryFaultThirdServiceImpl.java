package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.*;
import org.jeecg.modules.third.jdx.mapper.BuMaximoTrainAssetThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuTrainHistoryFaultThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuTrainInfoThirdMapper;
import org.jeecg.modules.third.jdx.service.BuTrainHistoryFaultThirdService;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;
import org.jeecg.modules.third.maximo.bean.JdxSrOut;
import org.jeecg.modules.third.maximo.bean.JdxWoOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.jeecg.modules.third.utils.UUIDGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆履历-故障记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Slf4j
@Service
public class BuTrainHistoryFaultThirdServiceImpl extends ServiceImpl<BuTrainHistoryFaultThirdMapper, BuTrainHistoryFault> implements BuTrainHistoryFaultThirdService {

    @Resource
    private BuTrainHistoryFaultThirdMapper buTrainHistoryFaultThirdMapper;
    @Resource
    private BuMaximoTrainAssetThirdMapper buMaximoTrainAssetThirdMapper;
    @Resource
    private BuTrainInfoThirdMapper buTrainInfoThirdMapper;


    /**
     * @see BuTrainHistoryFaultThirdService#initConsumeMaximoFaultData(List, List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean initConsumeMaximoFaultData(List<JdxSrOut> maximoFaultList, List<JdxAssetOut> maximoAssetList) throws Exception {
        if (CollectionUtils.isEmpty(maximoFaultList)) {
            return true;
        }

        String sourceScene = "初始化";

        Set<String> assetNoSet = new HashSet<>();
        for (JdxSrOut maximoFault : maximoFaultList) {
            if (StringUtils.isNotBlank(maximoFault.getAssetnum())) {
                assetNoSet.add(maximoFault.getAssetnum());
            }
        }

        // 查询车辆设备
        List<BuMaximoTrainAsset> maximoTrainAssetList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetNoSet)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(assetNoSet));
            for (List<String> batchSub : batchSubList) {
                LambdaQueryWrapper<BuMaximoTrainAsset> maximoTrainAssetWrapper = new LambdaQueryWrapper<BuMaximoTrainAsset>()
                        .in(BuMaximoTrainAsset::getCode, batchSub);
                List<BuMaximoTrainAsset> assetList = buMaximoTrainAssetThirdMapper.selectList(maximoTrainAssetWrapper);
                maximoTrainAssetList.addAll(assetList);
            }
        }
        maximoTrainAssetList.sort(Comparator.comparing(BuMaximoTrainAsset::getTrainNo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuMaximoTrainAsset::getCode, Comparator.nullsLast(Comparator.naturalOrder())));
        // 查询车辆信息
        List<BuTrainInfo> trainInfoList = buTrainInfoThirdMapper.selectList(Wrappers.emptyWrapper());
        // 查询线路信息
        List<BuMtrLine> lineList = buTrainInfoThirdMapper.selectAllLine();
        // 查询旧的故障：同步所有故障时，查所有
        LambdaQueryWrapper<BuTrainHistoryFault> historyFaultWrapper = new LambdaQueryWrapper<BuTrainHistoryFault>()
                .eq(BuTrainHistoryFault::getArchiveType, 1);
        List<BuTrainHistoryFault> historyFaultList = buTrainHistoryFaultThirdMapper.selectList(historyFaultWrapper);
        Set<String> oldHistoryFaultIdSet = new HashSet<>();
        for (BuTrainHistoryFault fault : historyFaultList) {
            fault.setNeedDelete(true)
                    .setNeedAdd(false)
                    .setNeedUpdate(false);
            oldHistoryFaultIdSet.add(fault.getId());
        }

        for (JdxSrOut maximoFault : maximoFaultList) {
            // 故障号为空的，跳过
            if (StringUtils.isBlank(maximoFault.getTicketid())) {
                continue;
            }

            // 故障号
            String faultSn = maximoFault.getTicketid();
            // 查找匹配对应当前故障
            List<BuTrainHistoryFault> matchFaultList = historyFaultList.stream()
                    .filter(fault -> StringUtils.isNotBlank(fault.getFaultSn()) && faultSn.equals(fault.getFaultSn()))
                    .collect(Collectors.toList());

            String transAction = maximoFault.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                for (BuTrainHistoryFault fault : matchFaultList) {
                    fault.setNeedDelete(true)
                            .setNeedAdd(false)
                            .setNeedUpdate(false);
                }
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                if (CollectionUtils.isEmpty(matchFaultList)) {
                    // 添加到故障列表
                    BuTrainHistoryFault fault = transToTrainHistoryFault(maximoFault, maximoTrainAssetList, trainInfoList, maximoAssetList, lineList);
                    fault.setNeedAdd(true)
                            .setNeedDelete(false);
                    historyFaultList.add(fault);
                } else {
                    matchFaultList.sort(Comparator.comparing(BuTrainHistoryFault::getNeedAdd).thenComparing(BuTrainHistoryFault::getId));
                    for (int i = 0; i < matchFaultList.size(); i++) {
                        BuTrainHistoryFault fault = matchFaultList.get(i);
                        if (i == 0) {
                            fault.setNeedDelete(false);
                            if (oldHistoryFaultIdSet.contains(fault.getId())) {
                                // 1、数据库原来就有的故障，要更新；
                                fault.setNeedUpdate(true);
                            }
                        } else {
                            fault.setNeedDelete(true)
                                    .setNeedAdd(false)
                                    .setNeedUpdate(false);
                        }
                    }
                }
            }
        }

        // 过滤出需要处理的数据
        List<BuTrainHistoryFault> needAddFaultList = new ArrayList<>();
        List<BuTrainHistoryFault> needUpdateFaultList = new ArrayList<>();
        List<BuTrainHistoryFault> needDeleteFaultList = new ArrayList<>();
        extractDifferentProcessTypeFaultList(historyFaultList, oldHistoryFaultIdSet, needAddFaultList, needUpdateFaultList, needDeleteFaultList);

        // 保存数据
        saveFaultDataFromList(needAddFaultList, needUpdateFaultList, needDeleteFaultList);

        // 日志中记录本次处理信息
        log.info(String.format(sourceScene + "从maximo同步数据--故障：处理故障信息%s条（新增%s，更新%s，删除%s）",
                maximoFaultList.size(),
                needAddFaultList.size(),
                needUpdateFaultList.size(),
                needDeleteFaultList.size()));

        return true;
    }

    /**
     * @see BuTrainHistoryFaultThirdService#taskConsumeMaximoFaultData(List, List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean taskConsumeMaximoFaultData(List<JdxSrOut> maximoFaultList, List<JdxAssetOut> maximoAssetList) throws Exception {
        if (CollectionUtils.isEmpty(maximoFaultList)) {
            return true;
        }

        String sourceScene = "定时";

        Set<String> assetNoSet = new HashSet<>();
        Set<String> faultSnSet = new HashSet<>();
        for (JdxSrOut maximoFault : maximoFaultList) {
            if (StringUtils.isNotBlank(maximoFault.getAssetnum())) {
                assetNoSet.add(maximoFault.getAssetnum());
            }
            if (StringUtils.isNotBlank(maximoFault.getTicketid())) {
                faultSnSet.add(maximoFault.getTicketid());
            }
        }

        // 查询车辆设备
        List<BuMaximoTrainAsset> maximoTrainAssetList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetNoSet)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(assetNoSet));
            for (List<String> batchSub : batchSubList) {
                LambdaQueryWrapper<BuMaximoTrainAsset> maximoTrainAssetWrapper = new LambdaQueryWrapper<BuMaximoTrainAsset>()
                        .in(BuMaximoTrainAsset::getCode, batchSub);
                List<BuMaximoTrainAsset> assetList = buMaximoTrainAssetThirdMapper.selectList(maximoTrainAssetWrapper);
                maximoTrainAssetList.addAll(assetList);
            }
        }
        maximoTrainAssetList.sort(Comparator.comparing(BuMaximoTrainAsset::getTrainNo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuMaximoTrainAsset::getCode, Comparator.nullsLast(Comparator.naturalOrder())));
        // 查询车辆信息
        List<BuTrainInfo> trainInfoList = buTrainInfoThirdMapper.selectList(Wrappers.emptyWrapper());
        // 查询线路信息
        List<BuMtrLine> lineList = buTrainInfoThirdMapper.selectAllLine();
        // 查询旧的故障：定时同步故障时，查此次处理的故障
        List<BuTrainHistoryFault> historyFaultList = new ArrayList<>();
        Set<String> oldHistoryFaultIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(faultSnSet)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(faultSnSet));
            for (List<String> batchSub : batchSubList) {
                LambdaQueryWrapper<BuTrainHistoryFault> historyFaultWrapper = new LambdaQueryWrapper<BuTrainHistoryFault>()
                        .in(BuTrainHistoryFault::getFaultSn, batchSub)
                        .eq(BuTrainHistoryFault::getArchiveType, 1);
                List<BuTrainHistoryFault> subHistoryFaultList = buTrainHistoryFaultThirdMapper.selectList(historyFaultWrapper);
                if (CollectionUtils.isNotEmpty(subHistoryFaultList)) {
                    for (BuTrainHistoryFault fault : subHistoryFaultList) {
                        fault.setNeedDelete(false)
                                .setNeedAdd(false)
                                .setNeedUpdate(false);
                        historyFaultList.add(fault);
                        oldHistoryFaultIdSet.add(fault.getId());
                    }
                }
            }
        }

        for (JdxSrOut maximoFault : maximoFaultList) {
            // 故障号为空的，跳过
            if (StringUtils.isBlank(maximoFault.getTicketid())) {
                continue;
            }

            // 故障号
            String faultSn = maximoFault.getTicketid();
            // 查找匹配对应当前故障
            List<BuTrainHistoryFault> matchFaultList = historyFaultList.stream()
                    .filter(fault -> StringUtils.isNotBlank(fault.getFaultSn()) && faultSn.equals(fault.getFaultSn()))
                    .collect(Collectors.toList());

            String transAction = maximoFault.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                for (BuTrainHistoryFault fault : matchFaultList) {
                    fault.setNeedDelete(true)
                            .setNeedAdd(false)
                            .setNeedUpdate(false);
                }
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                if (CollectionUtils.isEmpty(matchFaultList)) {
                    // 添加到故障列表
                    BuTrainHistoryFault fault = transToTrainHistoryFault(maximoFault, maximoTrainAssetList, trainInfoList, maximoAssetList, lineList);
                    fault.setNeedAdd(true)
                            .setNeedDelete(false);
                    historyFaultList.add(fault);
                } else {
                    matchFaultList.sort(Comparator.comparing(BuTrainHistoryFault::getNeedAdd).thenComparing(BuTrainHistoryFault::getId));
                    for (int i = 0; i < matchFaultList.size(); i++) {
                        BuTrainHistoryFault fault = matchFaultList.get(i);
                        if (i == 0) {
                            fault.setNeedDelete(false);
                            if (oldHistoryFaultIdSet.contains(fault.getId())) {
                                // 1、数据库原来就有的故障，要更新；
                                fault.setNeedUpdate(true);
                            }
                        } else {
                            fault.setNeedDelete(true)
                                    .setNeedAdd(false)
                                    .setNeedUpdate(false);
                        }
                    }
                }
            }
        }

        // 过滤出需要处理的数据
        List<BuTrainHistoryFault> needAddFaultList = new ArrayList<>();
        List<BuTrainHistoryFault> needUpdateFaultList = new ArrayList<>();
        List<BuTrainHistoryFault> needDeleteFaultList = new ArrayList<>();
        extractDifferentProcessTypeFaultList(historyFaultList, oldHistoryFaultIdSet, needAddFaultList, needUpdateFaultList, needDeleteFaultList);

        // 保存数据
        saveFaultDataFromList(needAddFaultList, needUpdateFaultList, needDeleteFaultList);

        // 日志中记录本次处理信息
        List<String> transIdList = maximoFaultList.stream()
                .map(JdxSrOut::getTransid)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toList());
        log.info(String.format(sourceScene + "从maximo同步数据--故障：处理故障信息%s条（新增%s，更新%s，删除%s），transIds=%s",
                maximoFaultList.size(),
                needAddFaultList.size(),
                needUpdateFaultList.size(),
                needDeleteFaultList.size(),
                String.join(",", transIdList)));

        return true;
    }


    private BuTrainHistoryFault transToTrainHistoryFault(JdxSrOut maximoFault,
                                                         List<BuMaximoTrainAsset> maximoTrainAssetList,
                                                         List<BuTrainInfo> trainInfoList,
                                                         List<JdxAssetOut> maximoAssetList,
                                                         List<BuMtrLine> lineList) {
        BuTrainHistoryFault fault = new BuTrainHistoryFault()
                .setId(UUIDGenerator.generate())
                .setArchiveType(1)
                .setFaultSn(maximoFault.getTicketid())
                .setFaultDesc(maximoFault.getDescription())
                .setCategoryId(maximoFault.getFailurecode())
                .setCategoryName(maximoFault.getFailurecode())
                .setFaultCodeId(maximoFault.getProblemcode())
                .setFaultCodeName(maximoFault.getProblemcode())
                .setLineId(maximoFault.getCLinenum())
                .setLineName(maximoFault.getCLinenum())
                .setHappenTime(maximoFault.getAffecteddate())
                .setWorkOrderId(null)
                .setWorkOrderName(null)
                .setOrderTaskId(null)
                .setOrderTaskName(null)
                .setWorkStationId(null)
                .setPhase(null)
                .setFaultLevel(getLevelInt(maximoFault.getCFaultrank()))
                .setFaultOnline(null)
                .setHasDuty(0)
                .setOutsource(0)
                .setReportGroup(maximoFault.getPersongroup())
                .setReportUserId(maximoFault.getReportedby())
                .setReportTime(maximoFault.getReportdate())
                .setDutyEngineer(maximoFault.getSupervisor())
                .setStatus(getStatusInt(maximoFault.getStatus()))
                .setHandleStatus(getHandleStatusInt(maximoFault.getResultstatus()))
                .setHandleTime(null == maximoFault.getActualfinish() ? maximoFault.getCTmprepairetime() : maximoFault.getActualfinish())
//                .setHandleGroup(maximoFault.getVendor())
                .setHandleUser(maximoFault.getCVendorsupervisor())
                .setExLocation(maximoFault.getCLocation())
                .setExFaultDetail(maximoFault.getDescriptionLongdescription())
                .setExFaultType(null)
                .setExEffect(getCFailureaffectString(maximoFault.getCFailureaffect()))
                .setExTypical(1D == maximoFault.getCZdgz() ? "是" : "否")
                .setRemark("maximo导入。");

        // 查找并设置线路信息
        transLine(maximoFault, fault, lineList);
        // 查找并设置车辆及设备信息
        transTrainAndAssetInfo(maximoFault, fault, maximoTrainAssetList, trainInfoList, maximoAssetList);
        // maximo故障中比较重要的信息如果找不到对应的字段存放，放入备注中
        transSpecialInfoToRemark(maximoFault, fault);

        return fault;
    }

    private void transLine(JdxSrOut maximoFault,
                           BuTrainHistoryFault fault,
                           List<BuMtrLine> lineList) {
        String lineValue;
        if (StringUtils.isNotBlank(maximoFault.getCLinenum())) {
            String cLinenum = maximoFault.getCLinenum();
            lineValue = cLinenum.replaceAll(MaximoThirdConstant.LINE_PREFIX, "");
        } else {
            String siteid = maximoFault.getSiteid();
            lineValue = siteid.replaceAll(MaximoThirdConstant.SITE_PREFIX, "");
        }

        List<BuMtrLine> matchLineList = lineList.stream()
                .filter(line -> line.getLineId().equals(lineValue))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(matchLineList)) {
            fault.setLineId(matchLineList.get(0).getLineId())
                    .setLineName(matchLineList.get(0).getLineName());
        } else {
            fault.setLineId(lineValue)
                    .setLineName(lineValue);
        }
    }

    private void transTrainAndAssetInfo(JdxSrOut maximoFault,
                                        BuTrainHistoryFault fault,
                                        List<BuMaximoTrainAsset> maximoTrainAssetList,
                                        List<BuTrainInfo> trainInfoList,
                                        List<JdxAssetOut> maximoAssetList) {
        if (StringUtils.isBlank(maximoFault.getAssetnum())) {
            return;
        }
        if (StringUtils.isBlank(maximoFault.getCFailureclass())) {
            return;
        }

        String assetnum = maximoFault.getAssetnum();
        String cFailureclass = maximoFault.getCFailureclass();
        // 所属专业：电客车（默认）、轨道设备等
        if (StringUtils.isBlank(cFailureclass) || "电客车".equals(cFailureclass)) {
            fault.setTrainId(assetnum);
            String trainNo = getTrainNoByTrainId(assetnum, trainInfoList);
            if (StringUtils.isNotBlank(trainNo)) {
                fault.setTrainName(trainNo);
            } else {
                String trainInfoErrorMessage = "异常：无法根据maximo资产(车辆)号【" + assetnum + "】找到对应架大修系统中的车辆；";
                fault.setRemark(fault.getRemark() + trainInfoErrorMessage);
            }

            fault.setFaultAssetId(assetnum)
                    .setFaultAssetName(trainNo)
                    .setSysName("固定资产");
        } else {
            Optional<BuMaximoTrainAsset> maximoTrainAssetOptional = maximoTrainAssetList.stream()
                    .filter(trainAsset -> assetnum.equals(trainAsset.getCode()) && StringUtils.isNotBlank(trainAsset.getTrainNo()))
                    .findFirst();
            if (!maximoTrainAssetOptional.isPresent()) {
                maximoTrainAssetOptional = maximoTrainAssetList.stream()
                        .filter(trainAsset -> assetnum.equals(trainAsset.getCode()))
                        .findFirst();
            }
            if (maximoTrainAssetOptional.isPresent()) {
                BuMaximoTrainAsset maximoTrainAsset = maximoTrainAssetOptional.get();
                fault.setFaultAssetId(maximoTrainAsset.getId())
                        .setFaultAssetName(maximoTrainAsset.getName())
                        .setTrainId(getTrainIdByTrainNo(maximoTrainAsset.getTrainNo(), trainInfoList))
                        .setTrainName(maximoTrainAsset.getTrainNo());
                //TODO-zhaiyantao 2021/5/13 9:44 系统暂时不做，等maximo资产整理好了再做
                fault.setSysId(null)
                        .setSysName(maximoTrainAsset.getMaximoAssetType())
                        .setSubSysId(null)
                        .setSubSysName(maximoTrainAsset.getMaximoAssetType());
            } else {
                String trainId = getTrainIdByMaximoAsset(assetnum, maximoAssetList);
                if (StringUtils.isNotBlank(trainId)) {
                    fault.setTrainId(trainId);
                    String trainNo = getTrainNoByTrainId(trainId, trainInfoList);
                    if (StringUtils.isNotBlank(trainNo)) {
                        fault.setTrainName(trainNo);
                    } else {
                        String trainInfoErrorMessage = "异常：无法根据maximo资产(车辆)号【" + trainId + "】找到对应架大修系统中的车辆；";
                        fault.setRemark(fault.getRemark() + trainInfoErrorMessage);
                    }
                } else {
                    String trainAssetErrorMessage = "异常：无法根据maximo资产(设备)号【" + assetnum + "】找到对应架大修系统中的车辆级设备；";
                    fault.setRemark(fault.getRemark() + trainAssetErrorMessage);
                }
            }
        }
    }

    private String getTrainNoByTrainId(String trainId, List<BuTrainInfo> trainInfoList) {
        if (StringUtils.isBlank(trainId)) {
            return null;
        }
        Optional<BuTrainInfo> trainInfoOptional = trainInfoList.stream()
                .filter(trainInfo -> trainId.equals(trainInfo.getId()))
                .findFirst();
        return trainInfoOptional.map(BuTrainInfo::getTrainNo).orElse(null);
    }

    private String getTrainIdByTrainNo(String trainNo, List<BuTrainInfo> trainInfoList) {
        if (StringUtils.isBlank(trainNo)) {
            return null;
        }
        Optional<BuTrainInfo> trainInfoOptional = trainInfoList.stream()
                .filter(trainInfo -> trainNo.equals(trainInfo.getTrainNo()))
                .findFirst();
        return trainInfoOptional.map(BuTrainInfo::getId).orElse(null);
    }

    private String getTrainIdByMaximoAsset(String assetnum, List<JdxAssetOut> maximoAssetList) {
        if (StringUtils.isBlank(assetnum) || CollectionUtils.isEmpty(maximoAssetList)) {
            return null;
        }

        List<JdxAssetOut> assetList = maximoAssetList.stream()
                .filter(assetOut -> assetnum.equals(assetOut.getAssetnum()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(assetList)) {
            return null;
        } else {
            for (JdxAssetOut asset : assetList) {
                if ("固定资产".equals(asset.getAssettype())
                        && "电客车".equals(asset.getCSpecialty())
                        && asset.getDescription().contains("车")) {
                    return asset.getAssetnum();
                }
            }

            for (JdxAssetOut asset : assetList) {
                return getTrainIdByMaximoAsset(asset.getParent(), maximoAssetList);
            }

            return null;
        }
    }

    private void transSpecialInfoToRemark(JdxSrOut maximoFault, BuTrainHistoryFault fault) {
        String statusMessage = "状态=" + getStatusString(maximoFault.getStatus()) + "；";
        String cStationMessage = "所在车站=" + maximoFault.getCStation() + "；";
//        String cAreaMessage = "所在区间=" + maximoFault.getCArea() + "；";
        String cOccsdMessage = "OCC调度=" + getCOccsdString(maximoFault.getCOccsd()) + "；";
        String cProductsdMessage = "车间生产调度=" + getCProductsdString(maximoFault.getCProductsd()) + "；";
        String cUsedmaterialMessage = "备品备件消耗=" + maximoFault.getCUsedmaterial() + "；";
//        fault.setRemark(fault.getRemark() + statusMessage + cStationMessage + cAreaMessage + cOccsdMessage + cProductsdMessage + cUsedmaterialMessage);
        fault.setRemark(fault.getRemark() + statusMessage + cStationMessage + cOccsdMessage + cProductsdMessage + cUsedmaterialMessage);
    }

    private Integer getLevelInt(String level) {
        int levelInt = 1;

        if (StringUtils.isBlank(level)) {
            return levelInt;
        }

        /**
         *  A
         *  B
         *  C
         */
        switch (level) {
            case "A":
                levelInt = 1;
                break;
            case "B":
                levelInt = 2;
                break;
            case "C":
                levelInt = 3;
                break;
            default:
                break;
        }

        return levelInt;
    }

    private Integer getStatusInt(String status) {
        int statusInt = 0;

        if (StringUtils.isBlank(status)) {
            return statusInt;
        }

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
        switch (status) {
            case "NEW":
                statusInt = 0;
                break;
            case "BZCOM":
            case "INPROG":
            case "RESOLVED":
                statusInt = 1;
                break;
            case "CLOSED":
            case "WOCLOSED":
                statusInt = 2;
                break;
            case "PENDING":
                statusInt = 3;
                break;
            default:
                break;
        }

        return statusInt;
    }

    private Integer getHandleStatusInt(String resultStatus) {
        int handleStatusInt = 0;

        if (StringUtils.isBlank(resultStatus)) {
            return handleStatusInt;
        }

        /**
         * 设备正常
         * 已修复
         * 未修复
         * 维修结束
         */
        switch (resultStatus) {
            case "设备正常":
                handleStatusInt = 1;
                break;
            case "已修复":
                handleStatusInt = 2;
                break;
            case "未修复":
                handleStatusInt = 3;
                break;
            case "维修结束":
                handleStatusInt = 4;
                break;
            default:
                break;
        }

        return handleStatusInt;
    }

    private String getCFailureaffectString(String cFailureaffect) {
        /**
         * XH_01	列车无法以自动防护模式运行
         * JCW_01	单边供电
         * JCW_02	部分分区失电
         * JCW_03	其他
         * GYGD_01	部分分区失电
         * GYGD_02	单边供电
         * GYGD_03	其他
         * XH_02	部分区段无速度码
         * XH_03	发生道岔失去表示
         * FAS_01	FAS联动
         * DFT_01	电梯停运困人
         */

        if (StringUtils.isBlank(cFailureaffect)) {
            return "其他";
        }

        String resultString = "";
        switch (cFailureaffect) {
            case "XH_01":
                resultString = "列车无法以自动防护模式运行";
                break;
            case "JCW_01":
                resultString = "单边供电";
                break;
            case "JCW_02":
                resultString = "部分分区失电";
                break;
            case "JCW_03":
                resultString = "其他";
                break;
            case "GYGD_01":
                resultString = "部分分区失电";
                break;
            case "GYGD_02":
                resultString = "单边供电";
                break;
            case "GYGD_03":
                resultString = "其他";
                break;
            case "XH_02":
                resultString = "部分区段无速度码";
                break;
            case "XH_03":
                resultString = "发生道岔失去表示";
                break;
            case "FAS_01":
                resultString = "FAS联动";
                break;
            case "DFT_01":
                resultString = "电梯停运困人";
                break;
        }

        return resultString;
    }

    private String getCOccsdString(String cOccsd) {
        /**
         * 01	行车调度
         * 02	电力调度
         * 03	环空调度
         * 04	设备维修调度
         * 05	车场调度
         * 06	信号楼调度
         */

        if (StringUtils.isBlank(cOccsd)) {
            return "设备维修调度";
        }

        String resultString = "";
        switch (cOccsd) {
            case "01":
                resultString = "行车调度";
                break;
            case "02":
                resultString = "电力调度";
                break;
            case "03":
                resultString = "环空调度";
                break;
            case "04":
                resultString = "设备维修调度";
                break;
            case "05":
                resultString = "车场调度";
                break;
            case "06":
                resultString = "信号楼调度";
                break;
        }

        return resultString;
    }

    private String getCProductsdString(String cProductsd) {
        /**
         * 01	车辆检修调度
         * 02	工务车间生产调度
         * 03	通号车间生产调度
         * 04	供电车间生产调度
         * 05	机电车间生产调度
         * 06	AFC车间调度
         * 07	设备车间调度
         */

        if (StringUtils.isBlank(cProductsd)) {
            return "车辆检修调度";
        }

        String resultString = "";
        switch (cProductsd) {
            case "01":
                resultString = "车辆检修调度";
                break;
            case "02":
                resultString = "工务车间生产调度";
                break;
            case "03":
                resultString = "通号车间生产调度";
                break;
            case "04":
                resultString = "供电车间生产调度";
                break;
            case "05":
                resultString = "机电车间生产调度";
                break;
            case "06":
                resultString = "AFC车间调度";
                break;
            case "07":
                resultString = "设备车间调度";
                break;
        }

        return resultString;
    }

    private String getStatusString(String status) {
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

        if (StringUtils.isBlank(status)) {
            return "";
        }

        String resultString = "";
        switch (status) {
            case "BZCOM":
                resultString = "班组已处理完成";
                break;
            case "CAN":
                resultString = "取消";
                break;
            case "CLOSED":
                resultString = "已关闭";
                break;
            case "HISTEDIT":
                resultString = "编辑历史记录";
                break;
            case "INPROG":
                resultString = "进行中";
                break;
            case "NEW":
                resultString = "新建";
                break;
            case "NEWOCC":
                resultString = "OCC调度已确认";
                break;
            case "NEWSD":
                resultString = "车间调度已确认";
                break;
            case "PENDING":
                resultString = "暂挂";
                break;
            case "QUEUED":
                resultString = "已排队";
                break;
            case "RESOLVED":
                resultString = "班组已处理";
                break;
            case "WAPPR":
                resultString = "调度待处理";
                break;
            case "WOCLOSED":
                resultString = "已维修";
                break;
        }

        return resultString;
    }

    private void extractDifferentProcessTypeFaultList(List<BuTrainHistoryFault> historyFaultList, Set<String> oldHistoryFaultIdSet, List<BuTrainHistoryFault> needAddFaultList, List<BuTrainHistoryFault> needUpdateFaultList, List<BuTrainHistoryFault> needDeleteFaultList) {
        for (BuTrainHistoryFault fault : historyFaultList) {
            boolean needAdd = null != fault.getNeedAdd() && fault.getNeedAdd();
            boolean needUpdate = null != fault.getNeedUpdate() && fault.getNeedUpdate();
            boolean needDelete = null != fault.getNeedDelete() && fault.getNeedDelete();

            if (needDelete && oldHistoryFaultIdSet.contains(fault.getId())) {
                needDeleteFaultList.add(fault);
            } else {
                if (needAdd && !oldHistoryFaultIdSet.contains(fault.getId())) {
                    needAddFaultList.add(fault);
                }
                if (needUpdate && oldHistoryFaultIdSet.contains(fault.getId())) {
                    needUpdateFaultList.add(fault);
                }
            }
        }
    }

    private void saveFaultDataFromList(List<BuTrainHistoryFault> needAddFaultList,
                                       List<BuTrainHistoryFault> needUpdateFaultList,
                                       List<BuTrainHistoryFault> needDeleteFaultList) {
        if (CollectionUtils.isNotEmpty(needAddFaultList)) {
            List<List<BuTrainHistoryFault>> addBatchSubList = DatabaseBatchSubUtil.batchSubList(needAddFaultList);
            for (List<BuTrainHistoryFault> addBatchSub : addBatchSubList) {
                buTrainHistoryFaultThirdMapper.insertList(addBatchSub);
            }
        }

        if (CollectionUtils.isNotEmpty(needUpdateFaultList)) {
            List<List<BuTrainHistoryFault>> updateBatchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateFaultList);
            for (List<BuTrainHistoryFault> updateBatchSub : updateBatchSubList) {
                List<String> updateIdList = updateBatchSub.stream()
                        .map(BuTrainHistoryFault::getId)
                        .collect(Collectors.toList());
                buTrainHistoryFaultThirdMapper.deleteBatchIds(updateIdList);
                buTrainHistoryFaultThirdMapper.insertList(updateBatchSub);
            }
        }

        if (CollectionUtils.isNotEmpty(needDeleteFaultList)) {
            List<String> deleteIdList = needDeleteFaultList.stream()
                    .map(BuTrainHistoryFault::getId)
                    .collect(Collectors.toList());
            List<List<String>> deleteIdBatchSubList = DatabaseBatchSubUtil.batchSubList(deleteIdList);
            for (List<String> deleteIdBatchSub : deleteIdBatchSubList) {
                buTrainHistoryFaultThirdMapper.deleteBatchIds(deleteIdBatchSub);
            }
        }
    }

}
