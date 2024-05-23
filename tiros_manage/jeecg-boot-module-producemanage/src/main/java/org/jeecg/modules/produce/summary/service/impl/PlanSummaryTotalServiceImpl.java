package org.jeecg.modules.produce.summary.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.jeecg.modules.produce.summary.bean.BuRepairExchang;
import org.jeecg.modules.produce.summary.bean.PlanBase;
import org.jeecg.modules.produce.summary.bean.TotalInfo;
import org.jeecg.modules.produce.summary.mapper.BuRepairExchangMapper;
import org.jeecg.modules.produce.summary.service.PlanSummaryBaseService;
import org.jeecg.modules.produce.summary.service.PlanSummaryTotalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * 总体情况 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Slf4j
@Service
public class PlanSummaryTotalServiceImpl implements PlanSummaryTotalService {

    @Resource
    private PlanSummaryBaseService planSummaryBaseService;

    @Resource(name = "produceRepairExchang")
    private BuRepairExchangMapper repairExchangMapper;


    /**
     * @see PlanSummaryTotalService#getTotalInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public TotalInfo getTotalInfo(String planId) throws Exception {
        PlanBase planBase = planSummaryBaseService.getPlanBase(planId);
        BuRepairExchang receiveTrain = repairExchangMapper.selectRepairExchangeById(planBase.getExchangeId());
        BuRepairExchang deliverTrain = repairExchangMapper.selectOne(Wrappers.<BuRepairExchang>lambdaQuery().eq(BuRepairExchang::getReceiveId, planBase.getExchangeId()));
        TotalInfo totalInfo = new TotalInfo();
        BeanUtils.copyProperties(planBase, totalInfo);

        int receiveEarlierDays = 0;
        int deliverEarlierDays = 0;
        if (receiveTrain.getPlanStartDate() != null && receiveTrain.getAcceptDate() != null) {
            long days = DateUtil.betweenDay(receiveTrain.getAcceptDate(), receiveTrain.getPlanStartDate(), false);
            if (days > 0) {
                receiveEarlierDays = Long.valueOf(days).intValue();
            }
        }
        if(deliverTrain!=null) {
            if (receiveTrain.getPlanFinishDate() != null && deliverTrain.getSendDate() != null) {
                long days = DateUtil.betweenDay(deliverTrain.getSendDate(), receiveTrain.getPlanFinishDate(), false);
                if (days > 0) {
                    deliverEarlierDays = Long.valueOf(days).intValue();
                }
            }
        }

        return totalInfo
                .setReceiveTime(receiveTrain.getPlanStartDate())
                .setDeliverTime(receiveTrain.getPlanFinishDate())
                .setActualReceiveTime(receiveTrain.getAcceptDate())
                .setActualDeliverTime(deliverTrain!=null?deliverTrain.getSendDate():null)
                .setReceiveEarlierDays(receiveEarlierDays)
                .setDeliverEarlierDays(deliverEarlierDays);
    }

}
