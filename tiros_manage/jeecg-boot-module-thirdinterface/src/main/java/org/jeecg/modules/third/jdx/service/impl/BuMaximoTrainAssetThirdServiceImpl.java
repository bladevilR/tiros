package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuMaximoTrainAsset;
import org.jeecg.modules.third.jdx.bean.BuMtrLine;
import org.jeecg.modules.third.jdx.bean.BuTrainInfo;
import org.jeecg.modules.third.jdx.mapper.BuMaximoTrainAssetThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuTrainInfoThirdMapper;
import org.jeecg.modules.third.jdx.service.BuMaximoTrainAssetThirdService;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * maximo资产设备 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
@Slf4j
@Service
public class BuMaximoTrainAssetThirdServiceImpl extends ServiceImpl<BuMaximoTrainAssetThirdMapper, BuMaximoTrainAsset> implements BuMaximoTrainAssetThirdService {

    @Resource
    private BuMaximoTrainAssetThirdMapper buMaximoTrainAssetThirdMapper;
    @Resource
    private BuTrainInfoThirdMapper buTrainInfoThirdMapper;


    /**
     * @see BuMaximoTrainAssetThirdService#insertAllAssetFromMaximoData(List, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertAllAssetFromMaximoData(List<JdxAssetOut> maximoAssetList, Boolean needUpdateOldBusinessTableData) throws Exception {
        if (CollectionUtils.isEmpty(maximoAssetList)) {
            return true;
        }

        Date now = new Date();

        // 更新旧的业务表关联资产设备id
        if (null == needUpdateOldBusinessTableData || needUpdateOldBusinessTableData) {
            updateOldBusinessTableData(maximoAssetList);
        }

        // 删除旧数据
        int deleteCount = buMaximoTrainAssetThirdMapper.delete(Wrappers.emptyWrapper());
        log.info("初始化同步maximo资产：删除了" + deleteCount + "条旧数据；");

        // 查询车辆信息
        List<BuTrainInfo> trainInfoList = buTrainInfoThirdMapper.selectList(Wrappers.emptyWrapper());
        // 查询线路信息
        List<BuMtrLine> lineList = buTrainInfoThirdMapper.selectAllLine();

        // 去重
        List<JdxAssetOut> validAssetList = new ArrayList<>();
        Map<Long, List<JdxAssetOut>> assetUidListMap = maximoAssetList.stream()
                .collect(Collectors.groupingBy(JdxAssetOut::getAssetuid));
        for (Map.Entry<Long, List<JdxAssetOut>> assetUidListEntry : assetUidListMap.entrySet()) {
            List<JdxAssetOut> sameUidAssetList = assetUidListEntry.getValue();
            if (CollectionUtils.isEmpty(sameUidAssetList)) {
                continue;
            }

            if (sameUidAssetList.size() > 1) {
                sameUidAssetList.sort(Comparator.comparing(JdxAssetOut::getChangedate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
            validAssetList.add(sameUidAssetList.get(0));
        }
        // maximo数据转换
        List<BuMaximoTrainAsset> assetList = new ArrayList<>();
        for (JdxAssetOut jdxAssetOut : validAssetList) {
            BuMaximoTrainAsset asset = new BuMaximoTrainAsset()
                    .setId(jdxAssetOut.getAssetuid().toString())
                    .setCode(jdxAssetOut.getAssetnum())
                    .setName(jdxAssetOut.getDescription())
                    .setParentCode(jdxAssetOut.getParent())
                    .setLocationCode(jdxAssetOut.getLocation())
                    .setStatus(getStatusInt(jdxAssetOut.getStatus()))
                    .setRemark("maximo初始化同步；")
                    .setMaximoStatus(jdxAssetOut.getStatus())
                    .setMaximoAssetType(jdxAssetOut.getAssettype())
                    .setMaximoSpecialty(jdxAssetOut.getCSpecialty())
                    .setMaximoOrgId(jdxAssetOut.getOrgid())
                    .setMaximoSiteId(jdxAssetOut.getSiteid())
                    .setMaximoDept(jdxAssetOut.getCDept())
                    .setCreateTime(now)
                    .setCreateBy("admin");

            // 查找并设置线路信息
            transLine(jdxAssetOut, asset, lineList);
            // 查找并设置车辆及设备信息
            transTrainInfo(jdxAssetOut, asset, trainInfoList, validAssetList);

            assetList.add(asset);
        }

        // 插入新数据
        if (CollectionUtils.isNotEmpty(assetList)) {
            List<List<BuMaximoTrainAsset>> batchSubList = DatabaseBatchSubUtil.batchSubList(assetList);
            for (List<BuMaximoTrainAsset> batchSub : batchSubList) {
                buMaximoTrainAssetThirdMapper.insertList(batchSub);
            }
            log.info("初始化同步maximo资产：新增了" + assetList.size() + "条新数据；");
        }

        // 更新旧数据

        return true;
    }


    private void updateOldBusinessTableData(List<JdxAssetOut> maximoAssetList) {
        // 查询旧数据，用于映射id为新的
        List<BuMaximoTrainAsset> oldAssetList = buMaximoTrainAssetThirdMapper.selectListUsed();
        Map<String, BuMaximoTrainAsset> idOldAssetMap = new HashMap<>();
        oldAssetList.forEach(oldAsset -> idOldAssetMap.put(oldAsset.getId(), oldAsset));
        // 故障、工单任务、表单实例会有关联资产设备
        List<Map<String, Object>> faultIdAssetIdMapList = buMaximoTrainAssetThirdMapper.selectFaultIdAssetIdList();
        List<Map<String, Object>> orderTaskIdAssetIdMapList = buMaximoTrainAssetThirdMapper.selectOrderTaskIdAssetIdList();
        List<Map<String, Object>> checkInstIdAssetIdMapList = buMaximoTrainAssetThirdMapper.selectCheckInstIdAssetIdList();
        List<Map<String, Object>> dataInstIdAssetIdMapList = buMaximoTrainAssetThirdMapper.selectDataInstIdAssetIdList();
        List<Map<String, Object>> workInstIdAssetIdMapList = buMaximoTrainAssetThirdMapper.selectWorkInstIdAssetIdList();
        // 故障
        if (CollectionUtils.isNotEmpty(faultIdAssetIdMapList)) {
            List<Map<String, Object>> faultIdNewAssetIdMapList = getIdNewAssetIdMapList(maximoAssetList, idOldAssetMap, faultIdAssetIdMapList);

            if (CollectionUtils.isNotEmpty(faultIdNewAssetIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(faultIdNewAssetIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaximoTrainAssetThirdMapper.updateFaultAssetIdBatch(batchSub);
                }
            }
        }
        // 工单任务
        if (CollectionUtils.isNotEmpty(orderTaskIdAssetIdMapList)) {
            List<Map<String, Object>> orderTaskIdNewAssetIdMapList = getIdNewAssetIdMapList(maximoAssetList, idOldAssetMap, orderTaskIdAssetIdMapList);

            if (CollectionUtils.isNotEmpty(orderTaskIdNewAssetIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderTaskIdNewAssetIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaximoTrainAssetThirdMapper.updateOrderTaskAssetIdBatch(batchSub);
                }
            }
        }
        // 检查表实例
        if (CollectionUtils.isNotEmpty(checkInstIdAssetIdMapList)) {
            List<Map<String, Object>> checkInstIdNewAssetIdMapList = getIdNewAssetIdMapList(maximoAssetList, idOldAssetMap, checkInstIdAssetIdMapList);

            if (CollectionUtils.isNotEmpty(checkInstIdNewAssetIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(checkInstIdNewAssetIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaximoTrainAssetThirdMapper.updateCheckInstAssetIdBatch(batchSub);
                }
            }
        }
        // 数据表实例
        if (CollectionUtils.isNotEmpty(dataInstIdAssetIdMapList)) {
            List<Map<String, Object>> dataInstIdNewAssetIdMapList = getIdNewAssetIdMapList(maximoAssetList, idOldAssetMap, dataInstIdAssetIdMapList);

            if (CollectionUtils.isNotEmpty(dataInstIdNewAssetIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(dataInstIdNewAssetIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaximoTrainAssetThirdMapper.updateDataInstAssetIdBatch(batchSub);
                }
            }
        }
        // 作业记录表实例
        if (CollectionUtils.isNotEmpty(workInstIdAssetIdMapList)) {
            List<Map<String, Object>> workInstIdNewAssetIdMapList = getIdNewAssetIdMapList(maximoAssetList, idOldAssetMap, workInstIdAssetIdMapList);

            if (CollectionUtils.isNotEmpty(workInstIdNewAssetIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(workInstIdNewAssetIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaximoTrainAssetThirdMapper.updateWorkInstAssetIdBatch(batchSub);
                }
            }
        }
    }

    private List<Map<String, Object>> getIdNewAssetIdMapList(List<JdxAssetOut> maximoAssetList,
                                                             Map<String, BuMaximoTrainAsset> idOldAssetMap,
                                                             List<Map<String, Object>> idOldAssetIdMapList) {
        List<Map<String, Object>> idNewAssetIdMapList = new ArrayList<>();

        for (Map<String, Object> idOldAssetIdMap : idOldAssetIdMapList) {
            String id = (String) idOldAssetIdMap.get("id");
            String assetId = (String) idOldAssetIdMap.get("assetId");

            BuMaximoTrainAsset oldAsset = idOldAssetMap.get(assetId);
            if (null != oldAsset) {
                List<JdxAssetOut> matchAssetList = maximoAssetList.stream()
                        .filter(maximoAsset -> oldAsset.getCode().equals(maximoAsset.getAssetnum())
                                && oldAsset.getName().equals(maximoAsset.getDescription())
                                && parentSame(oldAsset, maximoAsset)
                                && oldAsset.getMaximoAssetType().equals(maximoAsset.getAssettype())
                        ).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(matchAssetList)) {
                    Long assetuid = matchAssetList.get(0).getAssetuid();
                    if (null != assetuid) {
                        Map<String, Object> workInstIdNewAssetIdMap = new HashMap<>();
                        workInstIdNewAssetIdMap.put("id", id);
                        workInstIdNewAssetIdMap.put("assetId", assetuid.toString());
                        idNewAssetIdMapList.add(workInstIdNewAssetIdMap);
                    }
                }
            }
        }

        return idNewAssetIdMapList;
    }

    private boolean parentSame(BuMaximoTrainAsset oldAsset, JdxAssetOut maximoAsset) {
        if (StringUtils.isBlank(oldAsset.getParentCode()) || StringUtils.isBlank(maximoAsset.getParent())) {
            return StringUtils.isBlank(oldAsset.getParentCode()) && StringUtils.isBlank(maximoAsset.getParent());
        } else {
            return oldAsset.getParentCode().equals(maximoAsset.getParent());
        }
    }

    //    private Integer getStatus(Double disabled) {
    //        if (null == disabled) {
    //            return 1;
    //        }
    //        if (0D == disabled) {
    //            return 1;
    //        }
    //        return 0;
    //    }
    private Integer getStatusInt(String status) {
        int statusInt = 1;

        if (StringUtils.isBlank(status)) {
            return statusInt;
        }
        /**
         * OPERATING	有效
         * DECOMMISSIONED	无效
         */
        switch (status) {
            case "OPERATING":
                statusInt = 1;
                break;
            case "DECOMMISSIONED":
                statusInt = 0;
                break;
            default:
                break;
        }

        return statusInt;
    }

    private void transLine(JdxAssetOut jdxAssetOut,
                           BuMaximoTrainAsset asset,
                           List<BuMtrLine> lineList) {
        String lineValue;
        if (StringUtils.isNotBlank(jdxAssetOut.getCLinenum())) {
            String cLinenum = jdxAssetOut.getCLinenum();
            lineValue = cLinenum.replaceAll(MaximoThirdConstant.LINE_PREFIX, "");
        } else {
            String siteid = jdxAssetOut.getSiteid();
            lineValue = siteid.replaceAll(MaximoThirdConstant.SITE_PREFIX, "");
        }

        List<BuMtrLine> matchLineList = lineList.stream()
                .filter(line -> line.getLineId().equals(lineValue))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(matchLineList)) {
            asset.setLineId(matchLineList.get(0).getLineId());
        } else {
            asset.setLineId(lineValue);
        }
    }

    private void transTrainInfo(JdxAssetOut assetOut,
                                BuMaximoTrainAsset asset,
                                List<BuTrainInfo> trainInfoList,
                                List<JdxAssetOut> maximoAssetList) {
//        if ("01010386002300168".equals(assetOut.getAssetnum())) {
//            System.out.println("debug");
//        }

        String trainNo = getTrainNoByMaximoAsset(assetOut, trainInfoList, maximoAssetList);
        if (StringUtils.isNotBlank(trainNo)) {
            asset.setTrainNo(trainNo);
        } else {
            String trainInfoErrorMessage = "异常：无法根据资产【" + assetOut.getAssetnum() + "】找到对应架大修系统中的车辆；";
            asset.setRemark(asset.getRemark() + trainInfoErrorMessage);
        }
    }

    private String getTrainNoByMaximoAsset(JdxAssetOut jdxAssetOut, List<BuTrainInfo> trainInfoList, List<JdxAssetOut> maximoAssetList) {
        if ("固定资产".equals(jdxAssetOut.getAssettype())
                && "电客车".equals(jdxAssetOut.getCSpecialty())
                && jdxAssetOut.getDescription().contains("车")) {
            String trainId = jdxAssetOut.getAssetnum();
            return getTrainNoByTrainId(trainId, trainInfoList);
        } else {
            String parentAssetnum = jdxAssetOut.getParent();
            if (StringUtils.isBlank(parentAssetnum)) {
                return null;
            } else {
                List<JdxAssetOut> parentList = maximoAssetList.stream()
                        .filter(assetOut -> parentAssetnum.equals(assetOut.getAssetnum()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(parentList)) {
                    return null;
                } else {
                    String trainNo;
                    for (JdxAssetOut parent : parentList) {
                        trainNo = getTrainNoByMaximoAsset(parent, trainInfoList, maximoAssetList);
                        if (StringUtils.isNotBlank(trainNo)) {
                            return trainNo;
                        }
                    }
                }
            }
        }

        return null;
    }

    private String getTrainNoByTrainId(String trainId, List<BuTrainInfo> trainInfoList) {
        Optional<BuTrainInfo> trainInfoOptional = trainInfoList.stream()
                .filter(trainInfo -> trainId.equals(trainInfo.getId()))
                .findFirst();
        return trainInfoOptional.map(BuTrainInfo::getTrainNo).orElse(null);
    }

}
