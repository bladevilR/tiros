package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.jdx.bean.BuMaterialStockUse;
import org.jeecg.modules.third.jdx.mapper.BuMaterialStockUseThirdMapper;
import org.jeecg.modules.third.jdx.service.BuMaterialAssignDetailThirdService;
import org.jeecg.modules.third.jdx.service.BuMaterialReturnedDetailThirdService;
import org.jeecg.modules.third.jdx.service.BuOrderMaterialRewriteService;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransIn;
import org.jeecg.modules.third.trans.bean.BuMaximoTransData;
import org.jeecg.modules.third.trans.mapper.BuMaximoTransDataMapper;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物料消耗或退料，maximo回写状态的处理 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-31
 */
@Slf4j
@Service
public class BuOrderMaterialRewriteServiceImpl implements BuOrderMaterialRewriteService {

    @Resource
    private BuMaximoTransDataMapper buMaximoTransDataMapper;
    @Resource
    private BuMaterialStockUseThirdMapper buMaterialStockUseThirdMapper;
    @Resource
    private BuMaterialAssignDetailThirdService buMaterialAssignDetailThirdService;
    @Resource
    private BuMaterialReturnedDetailThirdService buMaterialReturnedDetailThirdService;


    /**
     * @see BuOrderMaterialRewriteService#consumeMaximoTransData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean consumeMaximoTransData(List<JdxMatusetransIn> maximoOrderMaterialList) throws Exception {
        if (CollectionUtils.isEmpty(maximoOrderMaterialList)) {
            return true;
        }

        // 物料同步数据
        Map<String, BuMaximoTransData> objIdTransDataMap = new HashMap<>();
        List<String> objIdList = maximoOrderMaterialList.stream()
                .map(JdxMatusetransIn::getExternalrefid)
                .collect(Collectors.toList());
        List<List<String>> objIdBatchSubList = DatabaseBatchSubUtil.batchSubList(objIdList);
        for (List<String> objIdBatchSub : objIdBatchSubList) {
            LambdaQueryWrapper<BuMaximoTransData> maximoTransDataWrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                    .in(BuMaximoTransData::getObjId, objIdBatchSub);
            List<BuMaximoTransData> subTransDataList = buMaximoTransDataMapper.selectList(maximoTransDataWrapper);
            for (BuMaximoTransData transData : subTransDataList) {
                objIdTransDataMap.put(transData.getObjId(), transData);
            }
        }

        // 处理数据
        List<BuMaximoTransData> needUpdateTransDataList = new ArrayList<>();
        List<String> needUpdateAssignDetailIdList = new ArrayList<>();
        List<String> needUpdateReturnDetailIdList = new ArrayList<>();
        List<BuMaximoTransData> errorList = new ArrayList<>();
        List<BuMaximoTransData> repeatList = new ArrayList<>();
        for (JdxMatusetransIn materialConsumeRewrite : maximoOrderMaterialList) {
            if (null == materialConsumeRewrite.getCStatus()) {
                // C_STATUS=null，说明maximo系统还没有处理
                continue;
            }

            String issuetype = materialConsumeRewrite.getIssuetype();
            String cStatus = materialConsumeRewrite.getCStatus();
            String externalrefid = materialConsumeRewrite.getExternalrefid();
            Date maximoTransDate = materialConsumeRewrite.getTransdate();

            BuMaximoTransData transData = objIdTransDataMap.get(externalrefid);
            if (null == transData) {
                log.error("从maximo同步数据--工单物料消耗或退料回写：无法根据maximo工单物料找到对应的同步数据，externalrefid=" + externalrefid);
                continue;
            }

            Integer oldSuccessStatus = transData.getSuccessStatus();
            String oldMessage = transData.getMessage();

            if ("ISSUE".equals(issuetype)) {
                if (3 != transData.getType()) {
                    log.error("从maximo同步数据--工单物料消耗或退料回写：消耗类型不匹配，maximo消耗类型issuetype=" + issuetype + "，架大修同步数据type=" + transData.getType());
                } else {
                    // 消耗
                    if ("0".equals(cStatus)) {
                        // 消耗成功
                        if (null != oldSuccessStatus && 1 == oldSuccessStatus) {
                            // 之前已经成功了，记录重复消耗
                            transData.setMessage("重复消耗且成功");

                            repeatList.add(transData);
                        } else {
                            transData.setSuccessStatus(1)
                                    .setSuccessTime(maximoTransDate);
                            if (null != oldSuccessStatus && 2 == oldSuccessStatus) {
                                // 之前失败了，记录消耗失败到成功的变化
                                transData.setMessage(oldMessage + " -> 消耗成功");
                            }
                        }
                    } else {
                        // 消耗失败，记录消耗失败原因
                        transData.setSuccessStatus(2)
                                .setMessage(cStatus);

                        errorList.add(transData);
                    }

                    needUpdateAssignDetailIdList.add(externalrefid);
                }
            } else if ("RETURN".equals(issuetype)) {
                if (4 != transData.getType()) {
                    log.error("从maximo同步数据--工单物料消耗或退料回写：消耗类型不匹配，maximo消耗类型issuetype=" + issuetype + "，架大修同步数据type=" + transData.getType());
                } else {
                    // 退料
                    if ("0".equals(cStatus)) {
                        // 退料成功
                        if (null != oldSuccessStatus && 1 == oldSuccessStatus) {
                            // 之前已经成功了，记录重复退料
                            transData.setMessage("重复退料且成功");

                            repeatList.add(transData);
                        } else {
                            transData.setSuccessStatus(1)
                                    .setSuccessTime(maximoTransDate);
                            if (null != oldSuccessStatus && 2 == oldSuccessStatus) {
                                // 之前失败了，记录退料失败到成功的变化
                                transData.setMessage(oldMessage + " -> 退料成功");
                            }
                        }
                    } else {
                        // 退料失败，记录消耗失败原因
                        transData.setSuccessStatus(2)
                                .setMessage(cStatus);

                        errorList.add(transData);
                    }

                    needUpdateReturnDetailIdList.add(externalrefid);
                }
            }

            needUpdateTransDataList.add(transData);
        }

        // 更新数据同步中间表状态
        if (CollectionUtils.isNotEmpty(needUpdateTransDataList)) {
            List<List<BuMaximoTransData>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateTransDataList);
            for (List<BuMaximoTransData> batchSub : batchSubList) {
                buMaximoTransDataMapper.updateStatusAndMessageAndTime(batchSub);
            }
        }
        // 更新发料单、退料单的同步时间
        buMaterialAssignDetailThirdService.updateApplySyncTime(needUpdateAssignDetailIdList);
        buMaterialReturnedDetailThirdService.updateReturnSyncTime(needUpdateReturnDetailIdList);
        // 更新发料单、退料单的同步结果
        buMaterialAssignDetailThirdService.updateApplySyncResult(needUpdateAssignDetailIdList);
        buMaterialReturnedDetailThirdService.updateReturnSyncResult(needUpdateReturnDetailIdList);

        // 删除分配明细对应的库存占用
        if (CollectionUtils.isNotEmpty(needUpdateAssignDetailIdList)) {
            List<List<String>> handledAssignDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateAssignDetailIdList);
            for (List<String> handledAssignDetailIdBatchSub : handledAssignDetailIdBatchSubList) {
                LambdaQueryWrapper<BuMaterialStockUse> stockUseWrapper = new LambdaQueryWrapper<BuMaterialStockUse>()
                        .in(BuMaterialStockUse::getAssignDetailId, handledAssignDetailIdBatchSub);
                buMaterialStockUseThirdMapper.delete(stockUseWrapper);
            }
        }

        if (CollectionUtils.isNotEmpty(errorList)) {
            String objIds = errorList.stream()
                    .map(BuMaximoTransData::getObjId)
                    .collect(Collectors.joining(","));
            log.error("从maximo同步数据--工单物料消耗或退料回写：消耗或退料回写了错误信息，同步数据objIds=" + objIds);
        }
        if (CollectionUtils.isNotEmpty(repeatList)) {
            String objIds = repeatList.stream()
                    .map(BuMaximoTransData::getObjId)
                    .collect(Collectors.joining(","));
            log.error("从maximo同步数据--工单物料消耗或退料回写：消耗或退料重复处理了，同步数据objIds=" + objIds);

            //TODO-zhaiyantao 2021/12/16 17:00 以后需加上发送邮件功能，好及时发现问题并处理
        }

        return true;
    }

}
