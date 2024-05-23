package org.jeecg.modules.produce.summary.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.produce.plan.bean.BuRepairPlan;
import org.jeecg.modules.produce.plan.mapper.BuRepairPlanProduceMapper;
import org.jeecg.modules.produce.summary.bean.PlanBase;
import org.jeecg.modules.produce.summary.bean.outsource.*;
import org.jeecg.modules.produce.summary.mapper.BuOutsourceOutinDetailMapper;
import org.jeecg.modules.produce.summary.service.PlanSummaryBaseService;
import org.jeecg.modules.produce.summary.service.PlanSummaryOutsourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 委外维修部件维修进度控制情况 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Slf4j
@Service
public class PlanSummaryOutsourceServiceImpl implements PlanSummaryOutsourceService {

    @Resource
    private PlanSummaryBaseService planSummaryBaseService;
    @Resource
    private BuRepairPlanProduceMapper repairPlanProduceMapper;
    @Resource(name = "summaryOutsourceOutinDetail")
    private BuOutsourceOutinDetailMapper outsourceOutinDetailMapper;


    /**
     * @see PlanSummaryOutsourceService#getOutsourceInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public OutsourceInfo getOutsourceInfo(String planId) throws Exception {
        PlanBase planBase = planSummaryBaseService.getPlanBase(planId);

        List<BuRepairPlan> repairPlanList = repairPlanProduceMapper.selectRepairPlanNearlyColumns(planBase.getLineId(), planBase.getRepairProgramId(),3);
        // 项目合同天数
        List<ContractDays> contractDaysList = new ArrayList<>();

        // 近三列车各项目维修天数
        List<ItemDays> itemList = new ArrayList<>();

        // 近三列车车辆项目维修详情
        List<TrainDetailGroup> trainItemDetailList = new ArrayList<>();

        // 委外部件验收问题
        InProblemInfo inProblemInfo = new InProblemInfo();

        if (CollectionUtil.isNotEmpty(repairPlanList)) {
            List<TrainDays> trainDaysList = new ArrayList<>();
            repairPlanList.forEach(plan -> {
                List<TrainDetailItem> trainDetailItemList = new ArrayList<>();
                TrainDetailGroup trainDetailGroup = new TrainDetailGroup()
                        .setTrainNo(plan.getTrainNo())
                        .setRepairIndex(plan.getItemNo());
                List<BuOutsourceOutinDetail> outinDetailList = outsourceOutinDetailMapper.selectOutInDetailListByPlanId(plan.getId(), 0);
                if (CollectionUtil.isNotEmpty(outinDetailList)) {
                    Map<String, List<BuOutsourceOutinDetail>> outinDetailMap = outinDetailList.stream().collect(Collectors.groupingBy(BuOutsourceOutinDetail::getAssetName));
                    outinDetailList.clear();
                    outinDetailMap.forEach((assetName, sameOutinDetail) -> outinDetailList.add(sameOutinDetail.get(0)));
                    outinDetailList.forEach(outinDetail -> {
                        ContractDays contractDays = new ContractDays()
                                .setItemName(outinDetail.getAssetName())
                                .setTrainNo(outinDetail.getTrainNo())
                                .setDays(outinDetail.getContractDays());
                        contractDaysList.add(contractDays);

                        int actDays = 0;
                        Date transferDate = outinDetail.getTransferDate();
                        Date actReturnTime = outinDetail.getActReturnTime();
                        if (transferDate != null && actReturnTime != null) {
                            actDays = (int) ((actReturnTime.getTime() - transferDate.getTime()) / (1000 * 3600 * 24));
                        }

                        TrainDays trainDays = new TrainDays()
                                .setRepairIndex(plan.getItemNo())
                                .setTrainNo(outinDetail.getTrainNo())
                                .setDays(actDays)
                                .setItemName(outinDetail.getAssetName());
                        trainDaysList.add(trainDays);


                        int delayDays = 0;
                        if (actReturnTime != null) {
                            if (actReturnTime.getTime() - outinDetail.getReturnTime().getTime() > 0) {
                                delayDays = (int) ((actReturnTime.getTime() - outinDetail.getReturnTime().getTime()) / (1000 * 3600 * 24));
                            }
                        } else {
                            Date now = new Date();
                            if (now.getTime() - outinDetail.getReturnTime().getTime() > 0) {
                                delayDays = (int) ((now.getTime() - outinDetail.getReturnTime().getTime()) / (1000 * 3600 * 24));
                            }
                        }

                        TrainDetailItem trainDetailItem = new TrainDetailItem()
                                .setItemName(outinDetail.getAssetName())
                                .setOutDate(outinDetail.getTransferDate())
                                .setInDate(actReturnTime)
                                .setAffectProgress(0)
                                .setRemark(outinDetail.getDelayReason())
                                .setDelayDays(delayDays);
                        trainDetailItemList.add(trainDetailItem);


                    });
                }
                trainDetailGroup.setItemList(trainDetailItemList);
                trainItemDetailList.add(trainDetailGroup);
            });

            if (CollectionUtil.isNotEmpty(trainDaysList)) {
                Map<String, List<TrainDays>> trainDaysMap = trainDaysList.stream().collect(Collectors.groupingBy(TrainDays::getItemName));
                trainDaysMap.forEach((k, v) -> {
                    ItemDays itemDays = new ItemDays().setItemName(k).setTrainList(v);
                    itemList.add(itemDays);
                });
            }
        }

        int handled = 0;
        int total = 0;
        List<InProblem> inProblemList = new ArrayList<>();

        List<BuOutsourceOutinDetail> problemPartList = outsourceOutinDetailMapper.selectProblemPart(planBase.getLineId(), planBase.getRepairProgramId(), planBase.getActFinish());

        if (CollectionUtil.isNotEmpty(problemPartList)) {
            List<BuOutsourceOutinDetail> handledOutinDetails = problemPartList.stream().filter(item -> item.getFacadeStatus() == 1 && item.getActReturnTime() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(handledOutinDetails)) {
                handled = handledOutinDetails.size();
            }
            List<BuOutsourceOutinDetail> problemOutinDetails = problemPartList.stream().filter(item -> item.getFacadeStatus() == 1).collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(problemOutinDetails)) {
                total = problemOutinDetails.size();
            }
            if (total != 0) {
                Map<String, List<BuOutsourceOutinDetail>> problemOutinDetailsMap = problemPartList.stream().collect(Collectors.groupingBy(BuOutsourceOutinDetail::getAssetName));
                problemOutinDetailsMap.forEach((k, v) -> {
                    List<BuOutsourceOutinDetail> problemDetails = v.stream().filter(item -> item.getFacadeStatus() == 1).collect(Collectors.toList());
                    List<BuOutsourceOutinDetail> handledDetails = v.stream().filter(item -> item.getFacadeStatus() == 1 && item.getActReturnTime() != null).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(problemDetails)) {
                        InProblem inProblem = new InProblem()
                                .setItemName(k)
                                .setTracking(0)
                                .setHandled(CollectionUtil.isNotEmpty(handledDetails) ? handledDetails.size() : 0)
                                .setTotal(problemDetails.size())
                                .setSupplierShortName(v.get(0).getSupplierName());
                        inProblemList.add(inProblem);
                    }
                });
            }
        }

        int itemCount = outsourceOutinDetailMapper.selectOutInCount(planBase.getLineId(), planBase.getRepairProgramId(), planBase.getActFinish());

        inProblemInfo.setOutDate(planBase.getActFinish())
                .setHandled(handled)
                .setItemCount(itemCount)
                .setTotal(total)
                .setProblemList(inProblemList);

        return new OutsourceInfo()
                .setContractDaysList(contractDaysList)
                .setItemList(itemList)
                .setTrainItemDetailList(trainItemDetailList)
                .setInProblemInfo(inProblemInfo);
    }

}
