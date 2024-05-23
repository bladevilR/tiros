package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuMaterialAssignDetail;
import org.jeecg.modules.third.jdx.bean.bo.ConsumeSyncBO;
import org.jeecg.modules.third.jdx.mapper.BuMaterialAssignDetailThirdMapper;
import org.jeecg.modules.third.jdx.mapper.ThirdCommonMapper;
import org.jeecg.modules.third.jdx.service.BuMaterialAssignDetailThirdService;
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
 * 物料分配明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Slf4j
@Service
public class BuMaterialAssignDetailThirdServiceImpl extends ServiceImpl<BuMaterialAssignDetailThirdMapper, BuMaterialAssignDetail> implements BuMaterialAssignDetailThirdService {

    @Resource
    private BuMaterialAssignDetailThirdMapper buMaterialAssignDetailThirdMapper;
    @Resource
    private ThirdCommonMapper thirdCommonMapper;

    @Value("${tiros.base.defaultSysHouseCategory:AJ1}")
    private String defaultSysHouseCategory;


    /**
     * @see BuMaterialAssignDetailThirdService#getMaximoMaterialConsumeNeedWriteByApplyDetailId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<JdxMatusetransIn> getMaximoMaterialConsumeNeedWriteByApplyDetailId(String applyDetailId) throws Exception {
        List<JdxMatusetransIn> maximoOrderMaterialList = new ArrayList<>();

        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailThirdMapper.selectListByApplyDetailId(applyDetailId);
        if (CollectionUtils.isNotEmpty(assignDetailList)) {
            for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                JdxMatusetransIn maximoOrderMaterial = transformToJdxMatusetransinIface(assignDetail);
                maximoOrderMaterialList.add(maximoOrderMaterial);
            }
        }

        return maximoOrderMaterialList;
    }

    /**
     * @see BuMaterialAssignDetailThirdService#getMaximoMaterialConsumeNeedWriteByAssignDetail(BuMaterialAssignDetail)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public JdxMatusetransIn getMaximoMaterialConsumeNeedWriteByAssignDetail(BuMaterialAssignDetail assignDetail) throws Exception {
        return transformToJdxMatusetransinIface(assignDetail);
    }

    /**
     * @see BuMaterialAssignDetailThirdService#listMaximoMaterialConsumeNeedWrite(Date)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<JdxMatusetransIn> listMaximoMaterialConsumeNeedWrite(Date date) throws Exception {
        List<JdxMatusetransIn> maximoOrderMaterialList = new ArrayList<>();

        //TODO-zhaiyantao 2021/4/30 14:08 根据时间和状态查询会错过一部分，状态不符合时间符合的数据，和统计数据的定时任务同样的问题
        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailThirdMapper.selectMaterialConsumedListByDate(date);
        if (CollectionUtils.isNotEmpty(assignDetailList)) {
            for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                JdxMatusetransIn maximoOrderMaterial = transformToJdxMatusetransinIface(assignDetail);
                maximoOrderMaterialList.add(maximoOrderMaterial);
            }
        }

        return maximoOrderMaterialList;
    }

    /**
     * @see BuMaterialAssignDetailThirdService#updateApplySyncTime(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateApplySyncTime(List<String> assignDetailIdList) throws Exception {
        if (CollectionUtils.isEmpty(assignDetailIdList)) {
            return true;
        }

        // 根据分配明细查询对应的领用单
        List<String> applyIdList = new ArrayList<>();
        List<List<String>> assignDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(assignDetailIdList);
        for (List<String> assignDetailIdBatchSub : assignDetailIdBatchSubList) {
            List<String> subApplyIdList = buMaterialAssignDetailThirdMapper.selectApplyIdListByAssignDetailIdList(assignDetailIdBatchSub);
            for (String applyId : subApplyIdList) {
                if (StringUtils.isNotBlank(applyId) && !applyIdList.contains(applyId)) {
                    applyIdList.add(applyId);
                }
            }
        }
        // 查询领用单下所有分配明细
        List<BuMaterialAssignDetail> allAssignDetailList = buMaterialAssignDetailThirdMapper.selectListByApplyIdList(applyIdList);

        List<ConsumeSyncBO> applySyncList = new ArrayList<>();
        // 所有的分配明细都已经同步过，取最晚的同步时间写入到领用单的同步时间中
        for (String applyId : applyIdList) {
            // 过滤出这个领用单的、数量不为0的、分配明细
            List<BuMaterialAssignDetail> assignDetailList = allAssignDetailList.stream()
                    .filter(assignDetail -> applyId.equals(assignDetail.getApplyId())
                            && null != assignDetail.getAmount()
                            && assignDetail.getAmount() > 0D)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(assignDetailList)) {
                continue;
            }

            boolean allTrans = true;
            Date lastTransTime = null;
            for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                Long transId = assignDetail.getTransId();
                Date transTime = assignDetail.getTransTime();
                if (null == transId) {
                    allTrans = false;
                    break;
                }
                if (null == lastTransTime || (null != transTime && lastTransTime.before(transTime))) {
                    lastTransTime = transTime;
                }
            }
            if (allTrans) {
                ConsumeSyncBO applySync = new ConsumeSyncBO()
                        .setApplyId(applyId)
                        .setSyncTime(lastTransTime);
                applySyncList.add(applySync);
            }
        }

        if (CollectionUtils.isNotEmpty(applySyncList)) {
            List<List<ConsumeSyncBO>> batchSubList = DatabaseBatchSubUtil.batchSubList(applySyncList);
            for (List<ConsumeSyncBO> batchSub : batchSubList) {
                buMaterialAssignDetailThirdMapper.updateApplySyncTime(batchSub);
            }
        }

        return true;
    }

    /**
     * @see BuMaterialAssignDetailThirdService#updateApplySyncResult(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateApplySyncResult(List<String> assignDetailIdList) throws Exception {
        if (CollectionUtils.isEmpty(assignDetailIdList)) {
            return true;
        }

        // 根据分配明细查询对应的领用单
        List<String> applyIdList = new ArrayList<>();
        List<List<String>> assignDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(assignDetailIdList);
        for (List<String> assignDetailIdBatchSub : assignDetailIdBatchSubList) {
            List<String> subApplyIdList = buMaterialAssignDetailThirdMapper.selectApplyIdListByAssignDetailIdList(assignDetailIdBatchSub);
            for (String applyId : subApplyIdList) {
                if (StringUtils.isNotBlank(applyId) && !applyIdList.contains(applyId)) {
                    applyIdList.add(applyId);
                }
            }
        }
        // 查询领用单下所有分配明细
        List<BuMaterialAssignDetail> allAssignDetailList = buMaterialAssignDetailThirdMapper.selectListByApplyIdList(applyIdList);

        List<ConsumeSyncBO> applySyncList = new ArrayList<>();
        // 所有的分配明细都已经同步成功，取最晚的同步成功时间写入到领用单的返回状态时间，同时写入同步成功状态
        // 所有的分配明细没有全部同步成功，如果有错误信息，取错误信息拼接后写入到返回状态，不写入时间
        for (String applyId : applyIdList) {
            // 过滤出这个领用单的、数量不为0的、分配明细
            List<BuMaterialAssignDetail> assignDetailList = allAssignDetailList.stream()
                    .filter(assignDetail -> applyId.equals(assignDetail.getApplyId())
                            && null != assignDetail.getAmount()
                            && assignDetail.getAmount() > 0D)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(assignDetailList)) {
                continue;
            }

            List<BuMaterialAssignDetail> notSuccessList = assignDetailList.stream()
                    .filter(assignDetail -> !(null != assignDetail.getSuccessStatus() && 1 == assignDetail.getSuccessStatus()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(notSuccessList)) {
                Date lastSuccessTime = null;
                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    Date successTime = assignDetail.getSuccessTime();
                    if (null == lastSuccessTime || (null != successTime && lastSuccessTime.before(successTime))) {
                        lastSuccessTime = successTime;
                    }
                }

                ConsumeSyncBO applySync = new ConsumeSyncBO()
                        .setApplyId(applyId)
                        .setSyncResult("全部成功")
                        .setSyncResultTime(lastSuccessTime);
                applySyncList.add(applySync);
            } else {
                Date lastResultTime = null;
                StringBuilder syncResultBuilder = new StringBuilder();
                for (BuMaterialAssignDetail notSuccess : notSuccessList) {
                    Date successTime = notSuccess.getSuccessTime();
                    String message = notSuccess.getMessage();
                    if (null == lastResultTime || (null != successTime && lastResultTime.before(successTime))) {
                        lastResultTime = successTime;
                    }
                    if (StringUtils.isNotBlank(message) && !"0".equals(message)) {
                        String itemErrorMessage = String.format("%s失败：%s；", notSuccess.getMaterialTypeId(), message);
                        syncResultBuilder.append(itemErrorMessage);
                    }
                }
                String syncResult = null;
                if (syncResultBuilder.length() > 0) {
                    syncResult = syncResultBuilder.deleteCharAt(syncResultBuilder.length() - 1).toString();
                }

                ConsumeSyncBO applySync = new ConsumeSyncBO()
                        .setApplyId(applyId)
                        .setSyncResult(syncResult)
                        .setSyncResultTime(lastResultTime);
                applySyncList.add(applySync);
            }
        }

        if (CollectionUtils.isNotEmpty(applySyncList)) {
            List<List<ConsumeSyncBO>> batchSubList = DatabaseBatchSubUtil.batchSubList(applySyncList);
            for (List<ConsumeSyncBO> batchSub : batchSubList) {
                buMaterialAssignDetailThirdMapper.updateApplySyncResult(batchSub);
            }
        }

        return true;
    }


    private JdxMatusetransIn transformToJdxMatusetransinIface(BuMaterialAssignDetail assignDetail) {
        assignDetail.setMaterialTotalPrice(assignDetail.getMaterialUnitPrice().multiply(BigDecimal.valueOf(assignDetail.getAmount())));

        String workshopCode = thirdCommonMapper.selectDepartMaximoCodeById(assignDetail.getWorkshopId());

        String description;
        if (StringUtils.isNotBlank(assignDetail.getMaterialSpec())) {
            description = assignDetail.getMaterialName() + "-[规格：" + assignDetail.getMaterialSpec() + "]";
        } else {
            description = assignDetail.getMaterialName();
        }

        // 库存组织
        String sysHouseCategory = StringUtils.isBlank(assignDetail.getSysHouseCategory()) ? defaultSysHouseCategory : assignDetail.getSysHouseCategory();

        return new JdxMatusetransIn()
                .setBinnum(assignDetail.getEbsWhChildCode())
                .setCDept(StringUtils.isBlank(workshopCode) ? MaximoThirdConstant.JDX_WORKSHOP_1 : workshopCode)
                .setCInventoryorg(sysHouseCategory)
                .setCStatus(null)
                .setDescription(description)
                .setEnterby(null)
                .setExternalrefid(assignDetail.getId())
                .setFcprojectid(assignDetail.getFdProjectCode())
                .setFctaskid(assignDetail.getFdTaskCode())
                .setIssueto(null)
                .setIssuetype("ISSUE")
                .setItemnum(assignDetail.getMaterialCode())
                .setItemsetid("ITEM1")// maximo系统内部的值
                .setLinecost(null)
                .setLinetype("ITEM")// maximo系统内部的值
                .setLocation(null)
                .setLotnum(assignDetail.getTradeNo())
                .setMatusetransid(null)
                .setOwnersysid(null)
                .setPositivequantity(assignDetail.getAmount())
                .setQuantity(-assignDetail.getAmount())
                .setRefwo(assignDetail.getOrderCode())
                .setSendersysid(null)
                .setSiteid(null)
                .setSourcesysid("JDX")
                .setStoreloc(assignDetail.getEbsWhCode())
                .setTositeid(StringUtils.isBlank(assignDetail.getLineId()) ? null : MaximoThirdConstant.SITE_PREFIX + assignDetail.getLineId())
                .setTransdate(assignDetail.getConfirmTime())
                .setUnitcost(null)
                .setTransid(null)
                .setTransseq(1L);
    }

}
