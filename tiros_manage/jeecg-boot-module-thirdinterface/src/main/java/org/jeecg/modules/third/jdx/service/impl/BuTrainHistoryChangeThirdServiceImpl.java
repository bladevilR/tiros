package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.jdx.bean.BuMaximoTrainAsset;
import org.jeecg.modules.third.jdx.bean.BuMtrLine;
import org.jeecg.modules.third.jdx.bean.BuTrainHistoryChange;
import org.jeecg.modules.third.jdx.bean.BuTrainInfo;
import org.jeecg.modules.third.jdx.mapper.BuMaximoTrainAssetThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuTrainHistoryChangeThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuTrainInfoThirdMapper;
import org.jeecg.modules.third.jdx.service.BuTrainHistoryChangeThirdService;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;
import org.jeecg.modules.third.maximo.bean.JdxRealassettransOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.jeecg.modules.third.utils.UUIDGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆履历-更换记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Slf4j
@Service
public class BuTrainHistoryChangeThirdServiceImpl extends ServiceImpl<BuTrainHistoryChangeThirdMapper, BuTrainHistoryChange> implements BuTrainHistoryChangeThirdService {

    @Resource
    private BuTrainHistoryChangeThirdMapper buTrainHistoryChangeThirdMapper;
    @Resource
    private BuMaximoTrainAssetThirdMapper buMaximoTrainAssetThirdMapper;
    @Resource
    private BuTrainInfoThirdMapper buTrainInfoThirdMapper;


    /**
     * @see BuTrainHistoryChangeThirdService#initConsumeMaximoChangeData(List, List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean initConsumeMaximoChangeData(List<JdxRealassettransOut> maximoChangeList, List<JdxAssetOut> maximoAssetList) throws Exception {
        if (CollectionUtils.isEmpty(maximoChangeList)) {
            return true;
        }

        String sourceScene = "初始化";

        Set<String> assetNoSet = new HashSet<>();
        for (JdxRealassettransOut maximoChange : maximoChangeList) {
            if (StringUtils.isNotBlank(maximoChange.getAssetnum())) {
                assetNoSet.add(maximoChange.getAssetnum());
            }
            if (StringUtils.isNotBlank(maximoChange.getRealassetnum())) {
                assetNoSet.add(maximoChange.getRealassetnum());
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
        // 查询旧的更换：同步所有更换时，查所有
        LambdaQueryWrapper<BuTrainHistoryChange> historyChangeWrapper = new LambdaQueryWrapper<BuTrainHistoryChange>()
                .eq(BuTrainHistoryChange::getArchiveType, 1);
        List<BuTrainHistoryChange> historyChangeList = buTrainHistoryChangeThirdMapper.selectList(historyChangeWrapper);
        Set<String> oldHistoryChangeIdSet = new HashSet<>();
        for (BuTrainHistoryChange change : historyChangeList) {
            change.setNeedDelete(true)
                    .setNeedAdd(false)
                    .setNeedUpdate(false);
            oldHistoryChangeIdSet.add(change.getId());
        }

        for (JdxRealassettransOut maximoChange : maximoChangeList) {
            // 更换号为空的，跳过
            if (null == maximoChange.getCRealassettransid()) {
                continue;
            }

            // 更换号
            String changeId = String.valueOf(maximoChange.getCRealassettransid());
            // 查找匹配对应当前更换
            List<BuTrainHistoryChange> matchChangeList = historyChangeList.stream()
                    .filter(change -> StringUtils.isNotBlank(change.getId()) && changeId.equals(change.getId()))
                    .collect(Collectors.toList());

            String transAction = maximoChange.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                for (BuTrainHistoryChange change : matchChangeList) {
                    change.setNeedDelete(true)
                            .setNeedAdd(false)
                            .setNeedUpdate(false);
                }
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                if (CollectionUtils.isEmpty(matchChangeList)) {
                    // 添加到更换列表
                    BuTrainHistoryChange change = transToTrainHistoryChange(maximoChange, maximoTrainAssetList, trainInfoList, maximoAssetList);
                    change.setNeedAdd(true)
                            .setNeedDelete(false);
                    historyChangeList.add(change);
                } else {
                    matchChangeList.sort(Comparator.comparing(BuTrainHistoryChange::getNeedAdd).thenComparing(BuTrainHistoryChange::getId));
                    for (int i = 0; i < matchChangeList.size(); i++) {
                        BuTrainHistoryChange change = matchChangeList.get(i);
                        if (i == 0) {
                            change.setNeedDelete(false);
                            if (oldHistoryChangeIdSet.contains(change.getId())) {
                                // 1、数据库原来就有的更换，要更新；
                                change.setNeedUpdate(true);
                            }
                        } else {
                            change.setNeedDelete(true)
                                    .setNeedAdd(false)
                                    .setNeedUpdate(false);
                        }
                    }
                }
            }
        }

        // 过滤出需要处理的数据
        List<BuTrainHistoryChange> needAddChangeList = new ArrayList<>();
        List<BuTrainHistoryChange> needUpdateChangeList = new ArrayList<>();
        List<BuTrainHistoryChange> needDeleteChangeList = new ArrayList<>();
        extractDifferentProcessTypeChangeList(historyChangeList, oldHistoryChangeIdSet, needAddChangeList, needUpdateChangeList, needDeleteChangeList);

        // 保存数据
        saveChangeDataFromList(needAddChangeList, needUpdateChangeList, needDeleteChangeList);

        // 日志中记录本次处理信息
        log.info(String.format(sourceScene + "从maximo同步数据--更换：处理更换信息%s条（新增%s，更新%s，删除%s）",
                maximoChangeList.size(),
                needAddChangeList.size(),
                needUpdateChangeList.size(),
                needDeleteChangeList.size()));

        return true;
    }

    /**
     * @see BuTrainHistoryChangeThirdService#taskConsumeMaximoChangeData(List, List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean taskConsumeMaximoChangeData(List<JdxRealassettransOut> maximoChangeList, List<JdxAssetOut> maximoAssetList) throws Exception {
        if (CollectionUtils.isEmpty(maximoChangeList)) {
            return true;
        }

        String sourceScene = "定时";

        Set<String> assetNoSet = new HashSet<>();
        Set<String> changeIdSet = new HashSet<>();
        for (JdxRealassettransOut maximoChange : maximoChangeList) {
            if (StringUtils.isNotBlank(maximoChange.getAssetnum())) {
                assetNoSet.add(maximoChange.getAssetnum());
            }
            if (StringUtils.isNotBlank(maximoChange.getRealassetnum())) {
                assetNoSet.add(maximoChange.getRealassetnum());
            }
            if (null != maximoChange.getCRealassettransid()) {
                changeIdSet.add(String.valueOf(maximoChange.getCRealassettransid()));
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
        // 查询旧的更换：定时同步更换时，查此次处理的更换
        List<BuTrainHistoryChange> historyChangeList = new ArrayList<>();
        Set<String> oldHistoryChangeIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(changeIdSet)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(changeIdSet));
            for (List<String> batchSub : batchSubList) {
                LambdaQueryWrapper<BuTrainHistoryChange> historyChangeWrapper = new LambdaQueryWrapper<BuTrainHistoryChange>()
                        .in(BuTrainHistoryChange::getId, batchSub)
                        .eq(BuTrainHistoryChange::getArchiveType, 1);
                List<BuTrainHistoryChange> subHistoryChangeList = buTrainHistoryChangeThirdMapper.selectList(historyChangeWrapper);
                if (CollectionUtils.isNotEmpty(subHistoryChangeList)) {
                    for (BuTrainHistoryChange change : subHistoryChangeList) {
                        change.setNeedDelete(false)
                                .setNeedAdd(false)
                                .setNeedUpdate(false);
                        historyChangeList.add(change);
                        oldHistoryChangeIdSet.add(change.getId());
                    }
                }
            }
        }

        for (JdxRealassettransOut maximoChange : maximoChangeList) {
            // 更换号为空的，跳过
            if (null == maximoChange.getCRealassettransid()) {
                continue;
            }

            // 更换号
            String changeId = String.valueOf(maximoChange.getCRealassettransid());
            // 查找匹配对应当前更换
            List<BuTrainHistoryChange> matchChangeList = historyChangeList.stream()
                    .filter(change -> StringUtils.isNotBlank(change.getId()) && changeId.equals(change.getId()))
                    .collect(Collectors.toList());

            String transAction = maximoChange.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                for (BuTrainHistoryChange change : matchChangeList) {
                    change.setNeedDelete(true)
                            .setNeedAdd(false)
                            .setNeedUpdate(false);
                }
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                if (CollectionUtils.isEmpty(matchChangeList)) {
                    // 添加到更换列表
                    BuTrainHistoryChange change = transToTrainHistoryChange(maximoChange, maximoTrainAssetList, trainInfoList, maximoAssetList);
                    change.setNeedAdd(true)
                            .setNeedDelete(false);
                    historyChangeList.add(change);
                } else {
                    matchChangeList.sort(Comparator.comparing(BuTrainHistoryChange::getNeedAdd).thenComparing(BuTrainHistoryChange::getId));
                    for (int i = 0; i < matchChangeList.size(); i++) {
                        BuTrainHistoryChange change = matchChangeList.get(i);
                        if (i == 0) {
                            change.setNeedDelete(false);
                            if (oldHistoryChangeIdSet.contains(change.getId())) {
                                // 1、数据库原来就有的更换，要更新；
                                change.setNeedUpdate(true);
                            }
                        } else {
                            change.setNeedDelete(true)
                                    .setNeedAdd(false)
                                    .setNeedUpdate(false);
                        }
                    }
                }
            }
        }

        // 过滤出需要处理的数据
        List<BuTrainHistoryChange> needAddChangeList = new ArrayList<>();
        List<BuTrainHistoryChange> needUpdateChangeList = new ArrayList<>();
        List<BuTrainHistoryChange> needDeleteChangeList = new ArrayList<>();
        extractDifferentProcessTypeChangeList(historyChangeList, oldHistoryChangeIdSet, needAddChangeList, needUpdateChangeList, needDeleteChangeList);

        // 保存数据
        saveChangeDataFromList(needAddChangeList, needUpdateChangeList, needDeleteChangeList);

        // 日志中记录本次处理信息
        List<String> transIdList = maximoChangeList.stream()
                .map(JdxRealassettransOut::getTransid)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toList());
        log.info(String.format(sourceScene + "从maximo同步数据--更换：处理更换信息%s条（新增%s，更新%s，删除%s），transIds=%s",
                maximoChangeList.size(),
                needAddChangeList.size(),
                needUpdateChangeList.size(),
                needDeleteChangeList.size(),
                String.join(",", transIdList)));

        return true;
    }

    private BuTrainHistoryChange transToTrainHistoryChange(JdxRealassettransOut maximoChange,
                                                           List<BuMaximoTrainAsset> maximoTrainAssetList,
                                                           List<BuTrainInfo> trainInfoList,
                                                           List<JdxAssetOut> maximoAssetList) {
        String id = null == maximoChange.getCRealassettransid() ? UUIDGenerator.generate() : String.valueOf(maximoChange.getCRealassettransid());
        BuTrainHistoryChange change = new BuTrainHistoryChange()
                .setId(id)
                .setArchiveType(1)
                .setOrderId(maximoChange.getWonum())
                .setOrderName(maximoChange.getWonum())
                .setDutyUser(maximoChange.getLead())
                .setWorkUser(maximoChange.getEnterby())
                .setExchangeNo(String.valueOf(maximoChange.getSequence()))
                .setMethod(maximoChange.getOperatemode())
//                .setTrainId(maximoChange.getAssetnum())
//                .setTrainName(maximoChange.getAssetnum())
                .setTrainAssetId(maximoChange.getAssetnum())
                .setTrainAssetName(maximoChange.getAssetnum())
                .setSysId(null)
                .setSysName(null)
                .setSubSysId(null)
                .setSubSysName(null)
                .setAssetTypeId(maximoChange.getAssetnum())
                .setAssetTypeName(maximoChange.getAssetnum())
                .setMaterialTypeId(maximoChange.getItemnum())
                .setMaterialTypeName(maximoChange.getItemnum())
                .setDownAssetId(maximoChange.getAssetnum())
                .setDownAssetName(maximoChange.getAssetnum())
                .setDownAssetSn(maximoChange.getAssetnum())
                .setUpAssetId(maximoChange.getRealassetnum())
                .setUpAssetName(maximoChange.getRealassetnum())
                .setUpAssetSn(maximoChange.getRealassetnum())
                .setExchangeTime(maximoChange.getTransdate())
                .setRemark("maximo导入。");

        // 查找并设置车辆及设备信息
        transTrainAndAssetInfo(maximoChange, change, maximoTrainAssetList, trainInfoList, maximoAssetList);

        return change;
    }

    private void transTrainAndAssetInfo(JdxRealassettransOut maximoChange,
                                        BuTrainHistoryChange change,
                                        List<BuMaximoTrainAsset> maximoTrainAssetList,
                                        List<BuTrainInfo> trainInfoList,
                                        List<JdxAssetOut> maximoAssetList) {
        if (StringUtils.isBlank(maximoChange.getAssetnum())) {
            return;
        }

        String assetnum = maximoChange.getAssetnum();

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
            change.setTrainAssetId(maximoTrainAsset.getId())
                    .setTrainAssetName(maximoTrainAsset.getName())
                    .setTrainId(getTrainIdByTrainNo(maximoTrainAsset.getTrainNo(), trainInfoList))
                    .setTrainName(maximoTrainAsset.getTrainNo());
            //TODO-zhaiyantao 2021/5/13 9:44 系统暂时不做，等maximo资产整理好了再做
            change.setSysId(null)
                    .setSysName(maximoTrainAsset.getMaximoAssetType())
                    .setSubSysId(null)
                    .setSubSysName(maximoTrainAsset.getMaximoAssetType());
        } else {
            String trainId = getTrainIdByMaximoAsset(assetnum, maximoAssetList);
            if (StringUtils.isNotBlank(trainId)) {
                change.setTrainId(trainId);
                String trainNo = getTrainNoByTrainId(trainId, trainInfoList);
                if (StringUtils.isNotBlank(trainNo)) {
                    change.setTrainName(trainNo);
                } else {
                    String trainInfoErrorMessage = "异常：无法根据maximo资产(车辆)号【" + trainId + "】找到对应架大修系统中的车辆；";
                    change.setRemark(change.getRemark() + trainInfoErrorMessage);
                }
            } else {
                String trainAssetErrorMessage = "异常：无法根据maximo资产(设备)号【" + assetnum + "】找到对应架大修系统中的车辆级设备；";
                change.setRemark(change.getRemark() + trainAssetErrorMessage);
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

    private void extractDifferentProcessTypeChangeList(List<BuTrainHistoryChange> historyChangeList, Set<String> oldHistoryChangeIdSet, List<BuTrainHistoryChange> needAddChangeList, List<BuTrainHistoryChange> needUpdateChangeList, List<BuTrainHistoryChange> needDeleteChangeList) {
        for (BuTrainHistoryChange change : historyChangeList) {
            boolean needAdd = null != change.getNeedAdd() && change.getNeedAdd();
            boolean needUpdate = null != change.getNeedUpdate() && change.getNeedUpdate();
            boolean needDelete = null != change.getNeedDelete() && change.getNeedDelete();

            if (needDelete && oldHistoryChangeIdSet.contains(change.getId())) {
                needDeleteChangeList.add(change);
            } else {
                if (needAdd && !oldHistoryChangeIdSet.contains(change.getId())) {
                    needAddChangeList.add(change);
                }
                if (needUpdate && oldHistoryChangeIdSet.contains(change.getId())) {
                    needUpdateChangeList.add(change);
                }
            }
        }
    }

    private void saveChangeDataFromList(List<BuTrainHistoryChange> needAddChangeList,
                                        List<BuTrainHistoryChange> needUpdateChangeList,
                                        List<BuTrainHistoryChange> needDeleteChangeList) {
        if (CollectionUtils.isNotEmpty(needAddChangeList)) {
            List<List<BuTrainHistoryChange>> addBatchSubList = DatabaseBatchSubUtil.batchSubList(needAddChangeList);
            for (List<BuTrainHistoryChange> addBatchSub : addBatchSubList) {
                buTrainHistoryChangeThirdMapper.insertList(addBatchSub);
            }
        }

        if (CollectionUtils.isNotEmpty(needUpdateChangeList)) {
            List<List<BuTrainHistoryChange>> updateBatchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateChangeList);
            for (List<BuTrainHistoryChange> updateBatchSub : updateBatchSubList) {
                List<String> updateIdList = updateBatchSub.stream()
                        .map(BuTrainHistoryChange::getId)
                        .collect(Collectors.toList());
                buTrainHistoryChangeThirdMapper.deleteBatchIds(updateIdList);
                buTrainHistoryChangeThirdMapper.insertList(updateBatchSub);
            }
        }

        if (CollectionUtils.isNotEmpty(needDeleteChangeList)) {
            List<String> deleteIdList = needDeleteChangeList.stream()
                    .map(BuTrainHistoryChange::getId)
                    .collect(Collectors.toList());
            List<List<String>> deleteIdBatchSubList = DatabaseBatchSubUtil.batchSubList(deleteIdList);
            for (List<String> deleteIdBatchSub : deleteIdBatchSubList) {
                buTrainHistoryChangeThirdMapper.deleteBatchIds(deleteIdBatchSub);
            }
        }
    }

}
