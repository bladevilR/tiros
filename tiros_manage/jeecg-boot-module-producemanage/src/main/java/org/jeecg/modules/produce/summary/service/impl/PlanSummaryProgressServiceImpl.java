package org.jeecg.modules.produce.summary.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.modules.produce.plan.bean.BuRepairPlan;
import org.jeecg.modules.produce.plan.mapper.BuRepairPlanProduceMapper;
import org.jeecg.modules.produce.plan.service.BuRepairPlanProduceService;
import org.jeecg.modules.produce.summary.bean.BuRepairReguDetail;
import org.jeecg.modules.produce.summary.bean.PlanBase;
import org.jeecg.modules.produce.summary.bean.progress.ProgressCurrent;
import org.jeecg.modules.produce.summary.bean.progress.ProgressInfo;
import org.jeecg.modules.produce.summary.bean.progress.ProgressLast;
import org.jeecg.modules.produce.summary.bean.progress.WorkItem;
import org.jeecg.modules.produce.summary.mapper.BuRepairReguDetailMapper;
import org.jeecg.modules.produce.summary.service.PlanSummaryBaseService;
import org.jeecg.modules.produce.summary.service.PlanSummaryProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 生产进度控制情况 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Slf4j
@Service
public class PlanSummaryProgressServiceImpl implements PlanSummaryProgressService {

    @Resource
    private PlanSummaryBaseService planSummaryBaseService;

    @Resource(name = "summaryReguDetailMapper")
    private BuRepairReguDetailMapper buRepairReguDetailMapper;

    @Resource
    private BuRepairPlanProduceMapper repairPlanProduceMapper;


    /**
     * @see PlanSummaryProgressService#getProgressInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public ProgressInfo getProgressInfo(String planId) throws Exception {
        PlanBase planBase = planSummaryBaseService.getPlanBase(planId);
        List<BuRepairReguDetail> reguDetailList = buRepairReguDetailMapper.selectListByPlanId(planId);
        List<BuRepairReguDetail> allReguDetailList = buRepairReguDetailMapper.selectListAllByPlanId(planId);

        if (CollectionUtil.isNotEmpty(reguDetailList) && CollectionUtil.isNotEmpty(allReguDetailList)) {
            Set<String> detailIdSet = new HashSet<>();
            Map<String, BuRepairReguDetail> reguDetailMap = allReguDetailList.stream().collect(Collectors.toMap(BuRepairReguDetail::getId,item->item));
            Map<String, List<BuRepairReguDetail>> reguDetailParentMap = allReguDetailList.stream().filter(item->StringUtils.isNotBlank(item.getParentId())).collect(Collectors.groupingBy(BuRepairReguDetail::getParentId));
            reguDetailList.forEach(detail -> {
                if (StringUtils.isNotBlank(detail.getParentId())) {
                    BuRepairReguDetail topParentReguDetail = findTopParentReguDetail(detail.getParentId(), reguDetailMap);
                    detailIdSet.add(topParentReguDetail.getId());
                } else {
                    detailIdSet.add(detail.getId());
                }
            });

             List<BuRepairReguDetail> topReguDetails = buRepairReguDetailMapper.selectBatchIds(new ArrayList<>(detailIdSet));
            Map<String, String> topReguDetailNameMap = topReguDetails.stream().collect(Collectors.toMap(BuRepairReguDetail::getId, BuRepairReguDetail::getTitle));
            List<String> topDetailIdList = new ArrayList<>(topReguDetailNameMap.keySet());
            List<String> detailIdList = reguDetailList.stream().map(BuRepairReguDetail::getId).collect(Collectors.toList());
            Map<String, List<String>> detailIdMap = new HashMap<>(16);

            topDetailIdList.forEach(topId -> {
                List<String> subIdList = new ArrayList<>();
                detailIdList.forEach(subId -> {
                    if (topId.equals(subId)) {
                        subIdList.add(subId);
                    } else {
                        List<String> allSubIdList = new ArrayList<>();
                        List<BuRepairReguDetail> subReguDetailList=reguDetailParentMap.get(topId);
                        findReguDetialAllChild(subReguDetailList, allSubIdList,reguDetailParentMap);
                        if (CollectionUtil.isNotEmpty(allSubIdList) && allSubIdList.contains(subId)) {
                            subIdList.add(subId);
                        }
                    }
                });
                detailIdMap.put(topId, subIdList);
            });

            List<WorkItem> workItemList = new ArrayList<>();

            detailIdMap.forEach((k, reguDetailIdList) -> {
                WorkItem workItem = new WorkItem()
                        .setName(topReguDetailNameMap.get(k))
                        .setSortNo(new ArrayList<>(detailIdMap.keySet()).indexOf(k));
                if (CollectionUtil.isNotEmpty(reguDetailIdList)) {
                    Integer plannedDays = buRepairReguDetailMapper.selectPlannedDays(reguDetailIdList, planId);
                    workItem.setPlannedDays(plannedDays);
                    workItem.setActualDays(plannedDays);
                } else {
                    workItem.setActualDays(0);
                }
                workItemList.add(workItem);

            });

            // 本列车
            ProgressCurrent current = new ProgressCurrent();
            current.setDayCompareList(workItemList);
            current.setItemNameList(new ArrayList<>(topReguDetailNameMap.values()));
            current.setCategoryName("所有分类");

            // 近两列车
            List<ProgressLast> lastTwo = new ArrayList<>();

            List<BuRepairPlan> repairPlanList = repairPlanProduceMapper.selectRepairPlanNearlyColumns(planBase.getLineId(), planBase.getRepairProgramId(), 2);
            if (CollectionUtil.isNotEmpty(repairPlanList)) {
                repairPlanList.forEach(plan -> {
                    ProgressLast progressLast = (ProgressLast) new ProgressLast()
                            .setProgressStatus(plan.getActFinish() != null ? "1" : "0")
                            .setStartTimeEarlierDays(setEarlierDays(plan.getActStart(), plan.getStartDate()))
                            .setEndTimeEarlierDays(setEarlierDays(plan.getActFinish(), plan.getFinishDate()))
                            .setWorkingItemName("")
                            .setRepairDays(plan.getActDuration())
                            .setPeriodDays(plan.getDuration())
                            .setTrainNo(plan.getTrainNo())
                            .setRepairIndex(plan.getItemNo())
                            .setLineName(planBase.getLineName());
                    if (plan.getActFinish() == null) {
                        String taskName = repairPlanProduceMapper.selectUnfinishedFirstInfo(plan.getId());
                        progressLast.setWorkingItemName(taskName);
                    }
                    lastTwo.add(progressLast);
                });
            }
            return new ProgressInfo()
                    .setCurrent(current)
                    .setLastTwo(lastTwo);
        }
        return new ProgressInfo();
    }

    private int setEarlierDays(Date startDate, Date finishDate) {
        int earlierDays = 0;
        if (startDate != null && finishDate != null) {
            long days = DateUtil.betweenDay(finishDate, startDate, false);
            if (days > 0) {
                return Long.valueOf(days).intValue();
            }
        }
        return earlierDays;
    }


    private void findReguDetialAllChild(List<BuRepairReguDetail> reguDetailList, List<String> idList,Map<String, List<BuRepairReguDetail>> map) {
        if (CollectionUtil.isNotEmpty(reguDetailList)) {
            reguDetailList.forEach(detail -> {
                idList.add(detail.getId());
                List<BuRepairReguDetail> subReguDetailList = map.get(detail.getId());
                    findReguDetialAllChild(subReguDetailList, idList,map);
                }
            );
        }
    }


    private BuRepairReguDetail findTopParentReguDetail(String parentId, Map<String, BuRepairReguDetail> map) {
        BuRepairReguDetail  reguDetail = map.get(parentId);

        if (StringUtils.isNotBlank(reguDetail.getParentId())) {
            return findTopParentReguDetail(reguDetail.getParentId(),map);
        } else {
            return reguDetail;
        }
    }
}
