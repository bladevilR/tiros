package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuMaterialReturnedDetail;
import org.jeecg.modules.third.jdx.bean.bo.ConsumeSyncBO;
import org.jeecg.modules.third.jdx.mapper.BuMaterialReturnedDetailThirdMapper;
import org.jeecg.modules.third.jdx.mapper.ThirdCommonMapper;
import org.jeecg.modules.third.jdx.service.BuMaterialReturnedDetailThirdService;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransIn;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 退料明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
@Service
public class BuMaterialReturnedDetailThirdServiceImpl extends ServiceImpl<BuMaterialReturnedDetailThirdMapper, BuMaterialReturnedDetail> implements BuMaterialReturnedDetailThirdService {

    @Resource
    private BuMaterialReturnedDetailThirdMapper buMaterialReturnedDetailThirdMapper;
    @Resource
    private ThirdCommonMapper thirdCommonMapper;

    @Value("${tiros.base.defaultSysHouseCategory:AJ1}")
    private String defaultSysHouseCategory;


    /**
     * @see BuMaterialReturnedDetailThirdService#getMaximoMaterialReturnNeedWriteByReturnedId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<JdxMatusetransIn> getMaximoMaterialReturnNeedWriteByReturnedId(String returnedId) throws Exception {
        List<JdxMatusetransIn> maximoOrderMaterialList = new ArrayList<>();

        List<BuMaterialReturnedDetail> returnedDetailList = buMaterialReturnedDetailThirdMapper.selectListByReturnId(returnedId);
        if (CollectionUtils.isNotEmpty(returnedDetailList)) {
            for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
                JdxMatusetransIn maximoOrderMaterial = transformToJdxMatusetransinIface(returnedDetail);
                maximoOrderMaterialList.add(maximoOrderMaterial);
            }
        }

        return maximoOrderMaterialList;
    }

    /**
     * @see BuMaterialReturnedDetailThirdService#getMaximoMaterialReturnNeedWriteByReturnedDetail(BuMaterialReturnedDetail)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public JdxMatusetransIn getMaximoMaterialReturnNeedWriteByReturnedDetail(BuMaterialReturnedDetail returnedDetail) throws Exception {
        return transformToJdxMatusetransinIface(returnedDetail);
    }

    /**
     * @see BuMaterialReturnedDetailThirdService#updateReturnSyncTime(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateReturnSyncTime(List<String> returnDetailIdList) throws Exception {
        if (CollectionUtils.isEmpty(returnDetailIdList)) {
            return true;
        }

        // 根据退料明细查询对应的退料单
        List<String> returnIdList = new ArrayList<>();
        List<List<String>> returnDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(returnDetailIdList);
        for (List<String> returnDetailIdBatchSub : returnDetailIdBatchSubList) {
            List<String> subReturnIdList = buMaterialReturnedDetailThirdMapper.selectReturnIdListByReturnDetailIdList(returnDetailIdBatchSub);
            for (String returnId : subReturnIdList) {
                if (StringUtils.isNotBlank(returnId) && !returnIdList.contains(returnId)) {
                    returnIdList.add(returnId);
                }
            }
        }
        // 查询退料单下所有退料明细
        List<BuMaterialReturnedDetail> allReturnDetailList = buMaterialReturnedDetailThirdMapper.selectListByReturnIdList(returnIdList);

        List<ConsumeSyncBO> returnSyncList = new ArrayList<>();
        // 所有的退料明细都已经同步过，取最晚的同步时间写入到退料单的同步时间中
        for (String returnId : returnIdList) {
            // 过滤出这个退料单的、数量不为0的、退料明细
            List<BuMaterialReturnedDetail> returnDetailList = allReturnDetailList.stream()
                    .filter(returnedDetail -> returnId.equals(returnedDetail.getReturnedId())
                            && null != returnedDetail.getReturnAmount()
                            && returnedDetail.getReturnAmount() > 0D)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(returnDetailList)) {
                continue;
            }

            boolean allTrans = true;
            Date lastTransTime = null;
            for (BuMaterialReturnedDetail returnDetail : returnDetailList) {
                Long transId = returnDetail.getTransId();
                Date transTime = returnDetail.getTransTime();
                if (null == transId) {
                    allTrans = false;
                    break;
                }
                if (null == lastTransTime || (null != transTime && lastTransTime.before(transTime))) {
                    lastTransTime = transTime;
                }
            }
            if (allTrans) {
                ConsumeSyncBO returnSync = new ConsumeSyncBO()
                        .setReturnId(returnId)
                        .setSyncTime(lastTransTime);
                returnSyncList.add(returnSync);
            }
        }

        if (CollectionUtils.isNotEmpty(returnSyncList)) {
            List<List<ConsumeSyncBO>> batchSubList = DatabaseBatchSubUtil.batchSubList(returnSyncList);
            for (List<ConsumeSyncBO> batchSub : batchSubList) {
                buMaterialReturnedDetailThirdMapper.updateReturnSyncTime(batchSub);
            }
        }

        return true;
    }

    /**
     * @see BuMaterialReturnedDetailThirdService#updateReturnSyncResult(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateReturnSyncResult(List<String> returnDetailIdList) throws Exception {
        if (CollectionUtils.isEmpty(returnDetailIdList)) {
            return true;
        }

        // 根据退料明细查询对应的退料单
        List<String> returnIdList = new ArrayList<>();
        List<List<String>> returnDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(returnDetailIdList);
        for (List<String> returnDetailIdBatchSub : returnDetailIdBatchSubList) {
            List<String> subReturnIdList = buMaterialReturnedDetailThirdMapper.selectReturnIdListByReturnDetailIdList(returnDetailIdBatchSub);
            for (String returnId : subReturnIdList) {
                if (StringUtils.isNotBlank(returnId) && !returnIdList.contains(returnId)) {
                    returnIdList.add(returnId);
                }
            }
        }
        // 查询退料单下所有退料明细
        List<BuMaterialReturnedDetail> allReturnDetailList = buMaterialReturnedDetailThirdMapper.selectListByReturnIdList(returnIdList);

        List<ConsumeSyncBO> returnSyncList = new ArrayList<>();
        // 所有的退料明细都已经同步成功，取最晚的同步成功时间写入到退料单的返回状态时间，同时写入同步成功状态
        // 所有的退料明细没有全部同步成功，如果有错误信息，取错误信息拼接后写入到返回状态，不写入时间
        for (String returnId : returnIdList) {
            // 过滤出这个退料单的、数量不为0的、退料明细
            List<BuMaterialReturnedDetail> returnDetailList = allReturnDetailList.stream()
                    .filter(returnDetail -> returnId.equals(returnDetail.getReturnedId())
                            && null != returnDetail.getReturnAmount()
                            && returnDetail.getReturnAmount() > 0D)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(returnDetailList)) {
                continue;
            }

            List<BuMaterialReturnedDetail> notSuccessList = returnDetailList.stream()
                    .filter(returnDetail -> null == returnDetail.getSuccessStatus() || 0 == returnDetail.getSuccessStatus())
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(notSuccessList)) {
                Date lastSuccessTime = null;
                for (BuMaterialReturnedDetail returnDetail : returnDetailList) {
                    Date successTime = returnDetail.getSuccessTime();
                    if (null == lastSuccessTime || (null != successTime && lastSuccessTime.before(successTime))) {
                        lastSuccessTime = successTime;
                    }
                }

                ConsumeSyncBO returnSync = new ConsumeSyncBO()
                        .setReturnId(returnId)
                        .setSyncResult("全部成功")
                        .setSyncResultTime(lastSuccessTime);
                returnSyncList.add(returnSync);
            } else {
                Date lastResultTime = null;
                StringBuilder syncResultBuilder = new StringBuilder();
                for (BuMaterialReturnedDetail notSuccess : notSuccessList) {
                    Date successTime = notSuccess.getSuccessTime();
                    String message = notSuccess.getMessage();
                    if (null == lastResultTime || (null != successTime && lastResultTime.before(successTime))) {
                        lastResultTime = successTime;
                    }
                    if (StringUtils.isBlank(message) && !"0".equals(message)) {
                        String itemErrorMessage = String.format("物料%s退料失败原因：%s；", notSuccess.getMaterialTypeId(), message);
                        syncResultBuilder.append(itemErrorMessage);
                    }
                }
                String syncResult = null;
                if (syncResultBuilder.length() > 0) {
                    syncResult = syncResultBuilder.deleteCharAt(syncResultBuilder.length() - 1).toString();
                }

                ConsumeSyncBO returnSync = new ConsumeSyncBO()
                        .setReturnId(returnId)
                        .setSyncResult(syncResult)
                        .setSyncResultTime(lastResultTime);
                returnSyncList.add(returnSync);
            }
        }

        if (CollectionUtils.isNotEmpty(returnSyncList)) {
            List<List<ConsumeSyncBO>> batchSubList = DatabaseBatchSubUtil.batchSubList(returnSyncList);
            for (List<ConsumeSyncBO> batchSub : batchSubList) {
                buMaterialReturnedDetailThirdMapper.updateReturnSyncResult(batchSub);
            }
        }

        return true;
    }


    private JdxMatusetransIn transformToJdxMatusetransinIface(BuMaterialReturnedDetail returnedDetail) {
        returnedDetail.setMaterialTotalPrice(returnedDetail.getMaterialUnitPrice().multiply(BigDecimal.valueOf(returnedDetail.getReturnAmount())));

        String workshopCode = thirdCommonMapper.selectDepartMaximoCodeById(returnedDetail.getWorkshopId());

        String description;
        if (StringUtils.isNotBlank(returnedDetail.getMaterialTypeSpec())) {
            description = returnedDetail.getMaterialTypeName() + "-[规格：" + returnedDetail.getMaterialTypeSpec() + "]";
        } else {
            description = returnedDetail.getMaterialTypeName();
        }

        // 库存组织
        String sysHouseCategory = StringUtils.isBlank(returnedDetail.getSysHouseCategory()) ? defaultSysHouseCategory : returnedDetail.getSysHouseCategory();

        return new JdxMatusetransIn()
                .setBinnum(returnedDetail.getEbsWhChildCode())
                .setCDept(StringUtils.isBlank(workshopCode) ? MaximoThirdConstant.JDX_WORKSHOP_1 : workshopCode)
                .setCInventoryorg(sysHouseCategory)
                .setCStatus(null)
                .setDescription(description)
                .setEnterby(null)
                .setExternalrefid(returnedDetail.getId())
                // 财务项目maximo有校验，先不传
                .setFcprojectid(returnedDetail.getFdProjectCode())
                .setFctaskid(returnedDetail.getFdTaskCode())
                .setIssueto(null)
                .setIssuetype("RETURN")
                .setItemnum(returnedDetail.getMaterialTypeCode())
                .setItemsetid("ITEM1")// maximo系统内部的值
                .setLinecost(null)
                .setLinetype("ITEM")// maximo系统内部的值
                .setLocation(null)
                .setLotnum(returnedDetail.getTradeNo())
                .setMatusetransid(null)
                .setOwnersysid(null)
                .setPositivequantity(returnedDetail.getReturnAmount())
                .setQuantity(returnedDetail.getReturnAmount())
                .setRefwo(returnedDetail.getOrderCode())
                .setSendersysid(null)
                .setSiteid(null)
                .setSourcesysid("JDX")
                .setTositeid(StringUtils.isBlank(returnedDetail.getLineId()) ? null : MaximoThirdConstant.SITE_PREFIX + returnedDetail.getLineId())
                .setStoreloc(returnedDetail.getEbsWhCode())
                .setTransdate(returnedDetail.getConfirmTime())
                .setUnitcost(null)
                .setTransid(null)
                .setTransseq(1L);
    }

}
