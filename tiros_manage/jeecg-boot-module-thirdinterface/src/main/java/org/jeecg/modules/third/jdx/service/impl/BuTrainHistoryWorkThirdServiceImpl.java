package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuMaximoTrainAsset;
import org.jeecg.modules.third.jdx.bean.BuMtrLine;
import org.jeecg.modules.third.jdx.bean.BuTrainHistoryWork;
import org.jeecg.modules.third.jdx.bean.BuTrainInfo;
import org.jeecg.modules.third.jdx.mapper.BuMaximoTrainAssetThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuTrainHistoryWorkThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuTrainInfoThirdMapper;
import org.jeecg.modules.third.jdx.service.BuTrainHistoryWorkThirdService;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;
import org.jeecg.modules.third.maximo.bean.JdxLabtransOut;
import org.jeecg.modules.third.maximo.bean.JdxWoOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆履历-作业记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Slf4j
@Service
public class BuTrainHistoryWorkThirdServiceImpl extends ServiceImpl<BuTrainHistoryWorkThirdMapper, BuTrainHistoryWork> implements BuTrainHistoryWorkThirdService {

    @Resource
    private BuTrainHistoryWorkThirdMapper buTrainHistoryWorkThirdMapper;
    @Resource
    private BuMaximoTrainAssetThirdMapper buMaximoTrainAssetThirdMapper;
    @Resource
    private BuTrainInfoThirdMapper buTrainInfoThirdMapper;


    /**
     * @see BuTrainHistoryWorkThirdService#initConsumeMaximoOrderData(List, List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean initConsumeMaximoOrderData(List<JdxWoOut> maximoOrderList, List<JdxAssetOut> maximoAssetList) throws Exception {
        if (CollectionUtils.isEmpty(maximoOrderList)) {
            return true;
        }

        String sourceScene = "初始化";

        Set<String> assetNoSet = new HashSet<>();
        for (JdxWoOut maximoOrder : maximoOrderList) {
            if (StringUtils.isNotBlank(maximoOrder.getAssetnum())) {
                assetNoSet.add(maximoOrder.getAssetnum());
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
        // 查询旧的工单：同步所有工单时，查所有
        LambdaQueryWrapper<BuTrainHistoryWork> historyWorkWrapper = new LambdaQueryWrapper<BuTrainHistoryWork>()
                .eq(BuTrainHistoryWork::getArchiveType, 1);
        List<BuTrainHistoryWork> historyWorkList = buTrainHistoryWorkThirdMapper.selectList(historyWorkWrapper);
        Set<String> oldHistoryWorkIdSet = new HashSet<>();
        for (BuTrainHistoryWork work : historyWorkList) {
            work.setNeedDelete(true)
                    .setNeedAdd(false)
                    .setNeedUpdate(false);
            oldHistoryWorkIdSet.add(work.getId());
        }

        for (JdxWoOut maximoOrder : maximoOrderList) {
            // 工单号为空的，跳过
            if (StringUtils.isBlank(maximoOrder.getWonum())) {
                continue;
            }

            // 工单号
            String orderCode = maximoOrder.getWonum();
            // 查找匹配对应当前工单
            List<BuTrainHistoryWork> matchWorkList = historyWorkList.stream()
                    .filter(work -> StringUtils.isNotBlank(work.getOrderCode()) && orderCode.equals(work.getOrderCode()))
                    .collect(Collectors.toList());

            String transAction = maximoOrder.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                for (BuTrainHistoryWork work : matchWorkList) {
                    work.setNeedDelete(true)
                            .setNeedAdd(false)
                            .setNeedUpdate(false);
                }
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                if (CollectionUtils.isEmpty(matchWorkList)) {
                    // 添加到工单列表
                    BuTrainHistoryWork work = transToTrainsHistoryWork(maximoOrder, maximoTrainAssetList, trainInfoList, maximoAssetList, lineList);
                    work.setNeedAdd(true)
                            .setNeedDelete(false);
                    historyWorkList.add(work);
                } else {
                    matchWorkList.sort(Comparator.comparing(BuTrainHistoryWork::getNeedAdd).thenComparing(BuTrainHistoryWork::getId));
                    for (int i = 0; i < matchWorkList.size(); i++) {
                        BuTrainHistoryWork work = matchWorkList.get(i);
                        if (i == 0) {
                            work.setNeedDelete(false);
                            if (oldHistoryWorkIdSet.contains(work.getId())) {
                                // 1、数据库原来就有的工单，要更新；
                                work.setNeedUpdate(true);
                            }
                        } else {
                            work.setNeedDelete(true)
                                    .setNeedAdd(false)
                                    .setNeedUpdate(false);
                        }
                    }
                }
            }
        }

        // 过滤出需要处理的数据
        List<BuTrainHistoryWork> needAddOrderList = new ArrayList<>();
        List<BuTrainHistoryWork> needUpdateOrderList = new ArrayList<>();
        List<BuTrainHistoryWork> needDeleteOrderList = new ArrayList<>();
        extractDifferentProcessTypeOrderList(historyWorkList, oldHistoryWorkIdSet, needAddOrderList, needUpdateOrderList, needDeleteOrderList);

        // 保存数据
        saveOrderDataFromList(needAddOrderList, needUpdateOrderList, needDeleteOrderList);

        // 日志中记录本次处理信息
        log.info(String.format(sourceScene + "从maximo同步数据--工单：处理工单信息%s条（新增%s，更新%s，删除%s）",
                maximoOrderList.size(),
                needAddOrderList.size(),
                needUpdateOrderList.size(),
                needDeleteOrderList.size()));

        return true;
    }

    /**
     * @see BuTrainHistoryWorkThirdService#taskConsumeMaximoOrderData(List, List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean taskConsumeMaximoOrderData(List<JdxWoOut> maximoOrderList, List<JdxAssetOut> maximoAssetList) throws Exception {
        if (CollectionUtils.isEmpty(maximoOrderList)) {
            return true;
        }

        String sourceScene = "定时";

        Set<String> assetNoSet = new HashSet<>();
        Set<String> orderCodeSet = new HashSet<>();
        for (JdxWoOut maximoOrder : maximoOrderList) {
            if (StringUtils.isNotBlank(maximoOrder.getAssetnum())) {
                assetNoSet.add(maximoOrder.getAssetnum());
            }
            if (StringUtils.isNotBlank(maximoOrder.getWonum())) {
                orderCodeSet.add(maximoOrder.getWonum());
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
        // 查询旧的工单：定时同步工单时，查此次处理的工单
        List<BuTrainHistoryWork> historyWorkList = new ArrayList<>();
        Set<String> oldHistoryWorkIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(orderCodeSet)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(orderCodeSet));
            for (List<String> batchSub : batchSubList) {
                LambdaQueryWrapper<BuTrainHistoryWork> historyWorkWrapper = new LambdaQueryWrapper<BuTrainHistoryWork>()
                        .in(BuTrainHistoryWork::getOrderCode, batchSub)
                        .eq(BuTrainHistoryWork::getArchiveType, 1);
                List<BuTrainHistoryWork> subHistoryWorkList = buTrainHistoryWorkThirdMapper.selectList(historyWorkWrapper);
                if (CollectionUtils.isNotEmpty(subHistoryWorkList)) {
                    for (BuTrainHistoryWork work : subHistoryWorkList) {
                        work.setNeedDelete(false)
                                .setNeedAdd(false)
                                .setNeedUpdate(false);
                        historyWorkList.add(work);
                        oldHistoryWorkIdSet.add(work.getId());
                    }
                }
            }
        }

        for (JdxWoOut maximoOrder : maximoOrderList) {
            // 工单号为空的，跳过
            if (StringUtils.isBlank(maximoOrder.getWonum())) {
                continue;
            }

            // 工单号
            String orderCode = maximoOrder.getWonum();
            // 查找匹配对应当前工单
            List<BuTrainHistoryWork> matchWorkList = historyWorkList.stream()
                    .filter(work -> StringUtils.isNotBlank(work.getOrderCode()) && orderCode.equals(work.getOrderCode()))
                    .collect(Collectors.toList());

            String transAction = maximoOrder.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                for (BuTrainHistoryWork work : matchWorkList) {
                    work.setNeedDelete(true)
                            .setNeedAdd(false)
                            .setNeedUpdate(false);
                }
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                if (CollectionUtils.isEmpty(matchWorkList)) {
                    // 添加到工单列表
                    BuTrainHistoryWork work = transToTrainsHistoryWork(maximoOrder, maximoTrainAssetList, trainInfoList, maximoAssetList, lineList);
                    work.setNeedAdd(true)
                            .setNeedDelete(false);
                    historyWorkList.add(work);
                } else {
                    matchWorkList.sort(Comparator.comparing(BuTrainHistoryWork::getNeedAdd).thenComparing(BuTrainHistoryWork::getId));
                    for (int i = 0; i < matchWorkList.size(); i++) {
                        BuTrainHistoryWork work = matchWorkList.get(i);
                        if (i == 0) {
                            work.setNeedDelete(false);
                            if (oldHistoryWorkIdSet.contains(work.getId())) {
                                // 1、数据库原来就有的工单，要更新；
                                work.setNeedUpdate(true);
                            }
                        } else {
                            work.setNeedDelete(true)
                                    .setNeedAdd(false)
                                    .setNeedUpdate(false);
                        }
                    }
                }
            }
        }

        // 过滤出需要处理的数据
        List<BuTrainHistoryWork> needAddOrderList = new ArrayList<>();
        List<BuTrainHistoryWork> needUpdateOrderList = new ArrayList<>();
        List<BuTrainHistoryWork> needDeleteOrderList = new ArrayList<>();
        extractDifferentProcessTypeOrderList(historyWorkList, oldHistoryWorkIdSet, needAddOrderList, needUpdateOrderList, needDeleteOrderList);

        // 保存数据
        saveOrderDataFromList(needAddOrderList, needUpdateOrderList, needDeleteOrderList);

        // 日志中记录本次处理信息
        List<String> transIdList = maximoOrderList.stream()
                .map(JdxWoOut::getTransid)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toList());
        log.info(String.format(sourceScene + "从maximo同步数据--工单：处理工单信息%s条（新增%s，更新%s，删除%s），transIds=%s",
                maximoOrderList.size(),
                needAddOrderList.size(),
                needUpdateOrderList.size(),
                needDeleteOrderList.size(),
                String.join(",", transIdList)));

        return true;
    }

    /**
     * @see BuTrainHistoryWorkThirdService#insertOrderUserFromMaximoData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertOrderUserFromMaximoData(List<JdxLabtransOut> maximoOrderUserList) throws Exception {
        if (CollectionUtils.isEmpty(maximoOrderUserList)) {
            return true;
        }

        //TODO-zhaiyantao 2021/4/14 9:22 暂时无操作
        for (JdxLabtransOut maximoOrderUser : maximoOrderUserList) {

        }


        return true;
    }


    private BuTrainHistoryWork transToTrainsHistoryWork(JdxWoOut maximoOrder,
                                                        List<BuMaximoTrainAsset> maximoTrainAssetList,
                                                        List<BuTrainInfo> trainInfoList,
                                                        List<JdxAssetOut> maximoAssetList,
                                                        List<BuMtrLine> lineList) {
        if (null == maximoOrder) {
            return null;
        }

        BuTrainHistoryWork work = new BuTrainHistoryWork()
                .setId(maximoOrder.getWonum())
                .setArchiveType(1)
                .setOrderCode(maximoOrder.getWonum())
                .setOrderName(StringUtils.isBlank(maximoOrder.getDescription()) ? "无工单描述：工单号=" + maximoOrder.getWonum() : maximoOrder.getDescription())
                .setOrderType(getWorktypeString(maximoOrder.getWorktype()))
                .setGenerate(0)
                .setProgramType(maximoOrder.getCNZqclass())
                .setStartTime(maximoOrder.getTargstartdate())
                .setFinishTime(maximoOrder.getTargcompdate())
                .setDuration(getDurationString(maximoOrder.getEstdur()))
                //TODO-zhaiyantao 2021/4/13 18:29 线路、车间、班组、人员等，找maximo对应
                .setGroupId(maximoOrder.getPersongroup())
                .setGroupName(null)
                .setMonitor(null)
                .setMonitorName(null)
                .setDepotId(null)
                .setDepotName(null)
                .setWorkshopId(maximoOrder.getCrewid())
                .setWorkshopName(null)
                .setActStart(maximoOrder.getActstart())
                .setActFinish(maximoOrder.getActfinish())
                .setOrderStatus(getStatusInt(maximoOrder.getStatus()))
                .setWorkStatus(2)
                .setCreateTime(maximoOrder.getReportdate())
                .setFdProject(maximoOrder.getFcprojectid())
                .setFdTask(maximoOrder.getFctaskid())
                .setExLocation(maximoOrder.getLocation())
                .setExPeriod(getCNZqclassString(maximoOrder.getCNZqclass()))
                .setExCheckLevel(getCNDjclassString(maximoOrder.getCNDjclass()))
                .setRemark("maximo导入。");

        // 查找并设置线路信息
        transLine(maximoOrder, work, lineList);
        // 查找并设置车辆及设备信息
        transTrainAndAssetInfo(maximoOrder, work, maximoTrainAssetList, trainInfoList, maximoAssetList);
        // maximo工单中比较重要的信息如果找不到对应的字段存放，放入备注中
        transSpecialInfoToRemark(maximoOrder, work);

        return work;
    }

    private void transLine(JdxWoOut maximoOrder,
                           BuTrainHistoryWork work,
                           List<BuMtrLine> lineList) {
        String lineValue;
        if (StringUtils.isNotBlank(maximoOrder.getCLinecode())) {
            String cLinecode = maximoOrder.getCLinecode();
            lineValue = cLinecode.replaceAll(MaximoThirdConstant.LINE_PREFIX, "");
        } else {
            String siteid = maximoOrder.getSiteid();
            lineValue = siteid.replaceAll(MaximoThirdConstant.SITE_PREFIX, "");
        }

        List<BuMtrLine> matchLineList = lineList.stream()
                .filter(line -> line.getLineId().equals(lineValue))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(matchLineList)) {
            work.setLineId(matchLineList.get(0).getLineId())
                    .setLineName(matchLineList.get(0).getLineName());
        } else {
            work.setLineId(lineValue)
                    .setLineName(lineValue);
        }
    }

    private void transTrainAndAssetInfo(JdxWoOut maximoOrder,
                                        BuTrainHistoryWork work,
                                        List<BuMaximoTrainAsset> maximoTrainAssetList,
                                        List<BuTrainInfo> trainInfoList,
                                        List<JdxAssetOut> maximoAssetList) {
        if (StringUtils.isBlank(maximoOrder.getAssetnum())) {
            return;
        }
        if (StringUtils.isBlank(maximoOrder.getCSpecialty())) {
            return;
        }

        String assetnum = maximoOrder.getAssetnum();
        String cSpecialty = maximoOrder.getCSpecialty();
        // 所属专业：电客车（默认）、轨道设备等
        if (StringUtils.isBlank(cSpecialty) || "电客车".equals(cSpecialty)) {
            work.setTrainId(assetnum);
            String trainNo = getTrainNoByTrainId(assetnum, trainInfoList);
            if (StringUtils.isNotBlank(trainNo)) {
                work.setTrainName(trainNo);
            } else {
                String trainInfoErrorMessage = "异常：无法根据maximo资产(车辆)号【" + assetnum + "】找到对应架大修系统中的车辆；";
                work.setRemark(work.getRemark() + trainInfoErrorMessage);
            }

            work.setTrainAssetId(assetnum)
                    .setTrainAssetName(trainNo)
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
                work.setTrainAssetId(maximoTrainAsset.getId())
                        .setTrainAssetName(maximoTrainAsset.getName())
                        .setTrainId(getTrainIdByTrainNo(maximoTrainAsset.getTrainNo(), trainInfoList))
                        .setTrainName(maximoTrainAsset.getTrainNo());
                //TODO-zhaiyantao 2021/5/13 9:44 系统暂时不做，等maximo资产整理好了再做
                work.setSysId(null)
                        .setSysName(maximoTrainAsset.getMaximoAssetType())
                        .setSubSysId(null)
                        .setSubSysName(maximoTrainAsset.getMaximoAssetType());
            } else {
                String trainId = getTrainIdByMaximoAsset(assetnum, maximoAssetList);
                if (StringUtils.isNotBlank(trainId)) {
                    work.setTrainId(trainId);
                    String trainNo = getTrainNoByTrainId(trainId, trainInfoList);
                    if (StringUtils.isNotBlank(trainNo)) {
                        work.setTrainName(trainNo);
                    } else {
                        String trainInfoErrorMessage = "异常：无法根据maximo资产(车辆)号【" + trainId + "】找到对应架大修系统中的车辆；";
                        work.setRemark(work.getRemark() + trainInfoErrorMessage);
                    }
                } else {
                    String trainAssetErrorMessage = "异常：无法根据maximo资产(设备)号【" + assetnum + "】找到对应架大修系统中的车辆级设备；";
                    work.setRemark(work.getRemark() + trainAssetErrorMessage);
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

    private void transSpecialInfoToRemark(JdxWoOut maximoOrder, BuTrainHistoryWork work) {
        String statusMessage = "工单状态=" + getStatusString(maximoOrder.getStatus()) + "；";
        work.setRemark(work.getRemark() + statusMessage);
    }

    private String getDurationString(Float hour) {
        if (null == hour || 0F == hour) {
            return "0";
        }
        return String.valueOf(hour / 8);
    }

    private Integer getStatusInt(String status) {
        int statusInt = 4;

        if (StringUtils.isBlank(status)) {
            return statusInt;
        }
        /**
         * APPR	车间主任已审批
         * APPR1	已批准
         * APPR2	再编制
         * CAN	已取消
         * CANSG	施工调度系统取消
         * CLOSE	已关闭
         * COMP	已完成
         * HISTEDIT	编辑历史记录
         * INPRG	进行中
         * SCHAPPR	施工计划已批准
         * SCHCAN	施工计划取消
         * SCHGNRT	施工计划已生成
         * SCHREQ	施工计划申请
         * SDCANCEL	施工计划取消
         * WACNMG	中心主任审批
         * WACNPM	中心材料工程师审核
         * WAPPR	编制中
         * WAWSDP	车间生产调度审核
         * WAWSMG	车间主任审核
         * WAWSPM	车间专业工程师已审批
         * WMATL	等待物料
         * WPCOND	等待厂家处理
         * WSCH	等待计划
         */

        // 只从maximo同步完成的工单
        switch (status) {
            case "COMP":
                statusInt = 3;
                break;
            case "CLOSE":
                statusInt = 4;
                break;
            default:
                break;
        }

        return statusInt;
    }

    private String getStatusString(String status) {
        /**
         * APPR	车间主任已审批
         * APPR1	已批准
         * APPR2	再编制
         * CAN	已取消
         * CANSG	施工调度系统取消
         * CLOSE	已关闭
         * COMP	已完成
         * HISTEDIT	编辑历史记录
         * INPRG	进行中
         * SCHAPPR	施工计划已批准
         * SCHCAN	施工计划取消
         * SCHGNRT	施工计划已生成
         * SCHREQ	施工计划申请
         * SDCANCEL	施工计划取消
         * WACNMG	中心主任审批
         * WACNPM	中心材料工程师审核
         * WAPPR	编制中
         * WAWSDP	车间生产调度审核
         * WAWSMG	车间主任审核
         * WAWSPM	车间专业工程师已审批
         * WMATL	等待物料
         * WPCOND	等待厂家处理
         * WSCH	等待计划
         */

        if (StringUtils.isBlank(status)) {
            return "";
        }

        String resultString = "";
        switch (status) {
            case "APPR":
                resultString = "车间主任已审批";
                break;
            case "APPR1":
                resultString = "已批准";
                break;
            case "APPR2":
                resultString = "再编制";
                break;
            case "CAN":
                resultString = "已取消";
                break;
            case "CANSG":
                resultString = "施工调度系统取消";
                break;
            case "CLOSE":
                resultString = "已关闭";
                break;
            case "COMP":
                resultString = "已完成";
                break;
            case "HISTEDIT":
                resultString = "编辑历史记录";
                break;
            case "INPRG":
                resultString = "进行中";
                break;
            case "SCHAPPR":
                resultString = "施工计划已批准";
                break;
            case "SCHCAN":
                resultString = "施工计划取消";
                break;
            case "SCHGNRT":
                resultString = "施工计划已生成";
                break;
            case "SCHREQ":
                resultString = "施工计划申请";
                break;
            case "SDCANCEL":
                resultString = "施工计划取消";
                break;
            case "WACNMG":
                resultString = "中心主任审批";
                break;
            case "WACNPM":
                resultString = "中心材料工程师审核";
                break;
            case "WAPPR":
                resultString = "编制中";
                break;
            case "WAWSDP":
                resultString = "车间生产调度审核";
                break;
            case "WAWSMG":
                resultString = "车间主任审核";
                break;
            case "WAWSPM":
                resultString = "车间专业工程师已审批";
                break;
            case "WMATL":
                resultString = "等待物料";
                break;
            case "WPCOND":
                resultString = "等待厂家处理";
                break;
            case "WSCH":
                resultString = "等待计划";
                break;
        }

        return resultString;
    }

    private String getWorktypeString(String worktype) {
        /**
         * FHYD	防寒越冬
         * PM	预防维修
         * TM	临时维修
         * TR	车辆大修
         * EM	紧急维修
         * OM	车辆架修
         * AM	设备保养
         * CM	设备维修
         * EI	设备检查
         * FM	故障维修
         * AW	辅助施工
         * AT	设备试验
         */

        if (StringUtils.isBlank(worktype)) {
            return "设备维修";
        }

        String resultString = "";
        switch (worktype) {
            case "FHYD":
                resultString = "防寒越冬";
                break;
            case "PM":
                resultString = "预防维修";
                break;
            case "TM":
                resultString = "临时维修";
                break;
            case "TR":
                resultString = "车辆大修";
                break;
            case "EM":
                resultString = "紧急维修";
                break;
            case "OM":
                resultString = "车辆架修";
                break;
            case "AM":
                resultString = "设备保养";
                break;
            case "CM":
                resultString = "设备维修";
                break;
            case "EI":
                resultString = "设备检查";
                break;
            case "FM":
                resultString = "故障维修";
                break;
            case "AW":
                resultString = "辅助施工";
                break;
            case "AT":
                resultString = "设备试验";
                break;
        }

        return resultString;
    }

    private String getCNDjclassString(String cNDjclass) {
        /**
         * 17	定期检修
         * 02	特殊巡视
         * 03	日常保养
         * 04	定期保养
         * 05	一级保养
         * 06	二级保养
         * 07	小修
         * 08	中修
         * 09	综合维修
         * 10	大修
         * 11	保护校验
         * 12	预防性试验
         * 13	设备更换
         * 14	工器具送检
         * 15	工器具送修
         * 16	工器具自修
         * 18	三级保养
         * 19	四级保养
         * 22	定期维护
         * 23	特殊维护
         * 24	故障维修
         * 25	配合施工
         * 26	临时维修
         * 28	车辆大修
         * 29	车辆架修
         * 20	C2巡视
         * 21	非C2巡视
         * 30	三月检
         * 31	年检
         * 32	步行巡视
         * 33	登乘巡视
         * 34	日检
         * 35	均衡修
         * 36	特别修
         * 37	专项修
         */

        if (StringUtils.isBlank(cNDjclass)) {
            return "定期检修";
        }

        String resultString = "";
        switch (cNDjclass) {
            case "17":
                resultString = "定期检修";
                break;
            case "02":
                resultString = "特殊巡视";
                break;
            case "03":
                resultString = "日常保养";
                break;
            case "04":
                resultString = "定期保养";
                break;
            case "05":
                resultString = "一级保养";
                break;
            case "06":
                resultString = "二级保养";
                break;
            case "07":
                resultString = "小修";
                break;
            case "08":
                resultString = "中修";
                break;
            case "09":
                resultString = "综合维修";
                break;
            case "10":
                resultString = "大修";
                break;
            case "11":
                resultString = "保护校验";
                break;
            case "12":
                resultString = "预防性试验";
                break;
            case "13":
                resultString = "设备更换";
                break;
            case "14":
                resultString = "工器具送检";
                break;
            case "15":
                resultString = "工器具送修";
                break;
            case "16":
                resultString = "工器具自修";
                break;
            case "18":
                resultString = "三级保养";
                break;
            case "19":
                resultString = "四级保养";
                break;
            case "22":
                resultString = "定期维护";
                break;
            case "23":
                resultString = "特殊维护";
                break;
            case "24":
                resultString = "故障维修";
                break;
            case "25":
                resultString = "配合施工";
                break;
            case "26":
                resultString = "临时维修";
                break;
            case "28":
                resultString = "车辆大修";
                break;
            case "29":
                resultString = "车辆架修";
                break;
            case "20":
                resultString = "C2巡视";
                break;
            case "21":
                resultString = "非C2巡视";
                break;
            case "30":
                resultString = "三月检";
                break;
            case "31":
                resultString = "年检";
                break;
            case "32":
                resultString = "步行巡视";
                break;
            case "33":
                resultString = "登乘巡视";
                break;
            case "34":
                resultString = "日检";
                break;
            case "35":
                resultString = "均衡修";
                break;
            case "36":
                resultString = "特别修";
                break;
            case "37":
                resultString = "专项修";
                break;
        }

        return resultString;
    }

    private String getCNZqclassString(String cNZqclass) {
        /**
         * 01	日检
         * 02	周检
         * 03	双周检
         * 04	月检
         * 05	三月检
         * 06	半年检
         * 07	年检
         * 08	三年检
         * 09	临修
         * 10	两年检
         * 11	双日检
         * 12	二月检
         * 13	五年检
         * 14	六年检
         * 15	十年检
         * 16	非定期检
         */

        if (StringUtils.isBlank(cNZqclass)) {
            return "非定期检";
        }

        String resultString = "";
        switch (cNZqclass) {
            case "01":
                resultString = "日检";
                break;
            case "02":
                resultString = "周检";
                break;
            case "03":
                resultString = "双周检";
                break;
            case "04":
                resultString = "月检";
                break;
            case "05":
                resultString = "三月检";
                break;
            case "06":
                resultString = "半年检";
                break;
            case "07":
                resultString = "年检";
                break;
            case "08":
                resultString = "三年检";
                break;
            case "09":
                resultString = "临修";
                break;
            case "10":
                resultString = "两年检";
                break;
            case "11":
                resultString = "双日检";
                break;
            case "12":
                resultString = "二月检";
                break;
            case "13":
                resultString = "五年检";
                break;
            case "14":
                resultString = "六年检";
                break;
            case "15":
                resultString = "十年检";
                break;
            case "16":
                resultString = "非定期检";
                break;
        }

        return resultString;
    }

    private void extractDifferentProcessTypeOrderList(List<BuTrainHistoryWork> historyWorkList, Set<String> oldHistoryWorkIdSet, List<BuTrainHistoryWork> needAddOrderList, List<BuTrainHistoryWork> needUpdateOrderList, List<BuTrainHistoryWork> needDeleteOrderList) {
        for (BuTrainHistoryWork work : historyWorkList) {
            boolean needAdd = null != work.getNeedAdd() && work.getNeedAdd();
            boolean needUpdate = null != work.getNeedUpdate() && work.getNeedUpdate();
            boolean needDelete = null != work.getNeedDelete() && work.getNeedDelete();

            if (needDelete && oldHistoryWorkIdSet.contains(work.getId())) {
                needDeleteOrderList.add(work);
            } else {
                if (needAdd && !oldHistoryWorkIdSet.contains(work.getId())) {
                    needAddOrderList.add(work);
                }
                if (needUpdate && oldHistoryWorkIdSet.contains(work.getId())) {
                    needUpdateOrderList.add(work);
                }
            }
        }
    }

    private void saveOrderDataFromList(List<BuTrainHistoryWork> needAddOrderList,
                                       List<BuTrainHistoryWork> needUpdateOrderList,
                                       List<BuTrainHistoryWork> needDeleteOrderList) {
        if (CollectionUtils.isNotEmpty(needAddOrderList)) {
            List<List<BuTrainHistoryWork>> addBatchSubList = DatabaseBatchSubUtil.batchSubList(needAddOrderList);
            for (List<BuTrainHistoryWork> addBatchSub : addBatchSubList) {
                buTrainHistoryWorkThirdMapper.insertList(addBatchSub);
            }
        }

        if (CollectionUtils.isNotEmpty(needUpdateOrderList)) {
            List<List<BuTrainHistoryWork>> updateBatchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateOrderList);
            for (List<BuTrainHistoryWork> updateBatchSub : updateBatchSubList) {
                List<String> updateIdList = updateBatchSub.stream()
                        .map(BuTrainHistoryWork::getId)
                        .collect(Collectors.toList());
                buTrainHistoryWorkThirdMapper.deleteBatchIds(updateIdList);
                buTrainHistoryWorkThirdMapper.insertList(updateBatchSub);
            }
        }

        if (CollectionUtils.isNotEmpty(needDeleteOrderList)) {
            List<String> deleteIdList = needDeleteOrderList.stream()
                    .map(BuTrainHistoryWork::getId)
                    .collect(Collectors.toList());
            List<List<String>> deleteIdBatchSubList = DatabaseBatchSubUtil.batchSubList(deleteIdList);
            for (List<String> deleteIdBatchSub : deleteIdBatchSubList) {
                buTrainHistoryWorkThirdMapper.deleteBatchIds(deleteIdBatchSub);
            }
        }
    }

}