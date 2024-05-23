package org.jeecg.modules.produce.kpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.produce.kpi.bean.BuRptPersonKpi;
import org.jeecg.modules.produce.kpi.bean.vo.BuKpiFaultItemVO;
import org.jeecg.modules.produce.kpi.bean.vo.BuKpiTimeItemVO;
import org.jeecg.modules.produce.kpi.bean.vo.BuKpiVO;
import org.jeecg.modules.produce.kpi.bean.vo.KpiQueryVO;
import org.jeecg.modules.produce.kpi.mapper.BuRptPersonKpiMapper;
import org.jeecg.modules.produce.kpi.service.BuRptPersonKpiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 个人绩效 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
@Service
public class BuRptPersonKpiServiceImpl extends ServiceImpl<BuRptPersonKpiMapper, BuRptPersonKpi> implements BuRptPersonKpiService {

    @Resource
    private BuRptPersonKpiMapper buRptPersonKpiMapper;


    /**
     * @see BuRptPersonKpiService#getUserKpi(KpiQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuKpiVO getUserKpi(KpiQueryVO queryVO) throws Exception {
        List<BuRptPersonKpi> buRptPersonKpiList = buRptPersonKpiMapper.selectList(getQueryWrapper(queryVO));
        if (CollectionUtils.isEmpty(buRptPersonKpiList)) {
            return new BuKpiVO().setTimeList(new ArrayList<>()).setFaultList(new ArrayList<>());
        }

        List<BuKpiTimeItemVO> timeList = new ArrayList<>();
        List<BuKpiFaultItemVO> faultList = new ArrayList<>();

        Map<String, List<BuRptPersonKpi>> userIdPersonKpiListMap = buRptPersonKpiList.stream()
                .collect(Collectors.groupingBy(BuRptPersonKpi::getUserId));
        for (Map.Entry<String, List<BuRptPersonKpi>> userIdPersonKpiListEntry : userIdPersonKpiListMap.entrySet()) {
            List<BuRptPersonKpi> userKpiList = userIdPersonKpiListEntry.getValue();
            if (CollectionUtils.isEmpty(userKpiList)) {
                continue;
            }

            int totalTime = userKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getTotalTime)
                    .sum();
            int repairTime = userKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getRepairTime)
                    .sum();
            int faultTime = userKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getFaultTime)
                    .sum();
            int totalHappen = userKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getTotalHappen)
                    .sum();
            int faultAmount = userKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getFaultAmount)
                    .sum();
            int handleAmount = userKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getHandleAmount)
                    .sum();

            BuRptPersonKpi personKpi = userKpiList.get(0);

            if (totalTime > 0) {
                BuKpiTimeItemVO timeItem = new BuKpiTimeItemVO()
                        .setUserId(personKpi.getUserId())
                        .setUserName(personKpi.getUserName())
                        .setWorkNo(personKpi.getWorkNo())
                        .setGroupId(personKpi.getGroupId())
                        .setGroupName(personKpi.getGroupName())
                        .setTotalTime(totalTime)
                        .setRepairTime(repairTime)
                        .setFaultTime(faultTime);
                timeList.add(timeItem);
            }
            if (totalHappen > 0) {
                BuKpiFaultItemVO faultItem = new BuKpiFaultItemVO()
                        .setUserId(personKpi.getUserId())
                        .setUserName(personKpi.getUserName())
                        .setWorkNo(personKpi.getWorkNo())
                        .setGroupId(personKpi.getGroupId())
                        .setGroupName(personKpi.getGroupName())
                        .setTotalHappen(totalHappen)
                        .setFaultAmount(faultAmount)
                        .setHandleAmount(handleAmount);
                faultList.add(faultItem);
            }
        }

        if (CollectionUtils.isNotEmpty(timeList)) {
            timeList.sort(Comparator.comparing(BuKpiTimeItemVO::getTotalTime).reversed());

            int realSortNo = 1;
            int showSortNo = 1;
            int lastTotalTime = timeList.get(0).getTotalTime();
            for (BuKpiTimeItemVO itemVO : timeList) {
                Integer totalTime = itemVO.getTotalTime();
                if (lastTotalTime != totalTime) {
                    lastTotalTime = totalTime;
                    showSortNo = realSortNo;
                }
                realSortNo++;
                itemVO.setSortNo(showSortNo);
            }
        }

        if (CollectionUtils.isNotEmpty(faultList)) {
            faultList.sort(Comparator.comparing(BuKpiFaultItemVO::getTotalHappen).reversed());

            int realSortNo = 1;
            int showSortNo = 1;
            int lastTotalHappen = faultList.get(0).getTotalHappen();
            for (BuKpiFaultItemVO itemVO : faultList) {
                Integer totalHappen = itemVO.getTotalHappen();
                if (lastTotalHappen != totalHappen) {
                    lastTotalHappen = totalHappen;
                    showSortNo = realSortNo;
                }
                realSortNo++;
                itemVO.setSortNo(showSortNo);
            }
        }

        return new BuKpiVO().setTimeList(timeList).setFaultList(faultList);
    }

    /**
     * @see BuRptPersonKpiService#getGroupKpi(KpiQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuKpiVO getGroupKpi(KpiQueryVO queryVO) throws Exception {
        List<BuRptPersonKpi> buRptPersonKpiList = buRptPersonKpiMapper.selectList(getQueryWrapper(queryVO));
        if (CollectionUtils.isEmpty(buRptPersonKpiList)) {
            return new BuKpiVO().setTimeList(new ArrayList<>()).setFaultList(new ArrayList<>());
        }

        List<BuKpiTimeItemVO> timeList = new ArrayList<>();
        List<BuKpiFaultItemVO> faultList = new ArrayList<>();

        Map<String, List<BuRptPersonKpi>> groupIdPersonKpiListMap = buRptPersonKpiList.stream()
                .collect(Collectors.groupingBy(BuRptPersonKpi::getGroupId));
        for (Map.Entry<String, List<BuRptPersonKpi>> groupIdPersonKpiListEntry : groupIdPersonKpiListMap.entrySet()) {
            List<BuRptPersonKpi> groupKpiList = groupIdPersonKpiListEntry.getValue();
            if (CollectionUtils.isEmpty(groupKpiList)) {
                continue;
            }

            int totalTime = groupKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getTotalTime)
                    .sum();
            int repairTime = groupKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getRepairTime)
                    .sum();
            int faultTime = groupKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getFaultTime)
                    .sum();
            int totalHappen = groupKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getTotalHappen)
                    .sum();
            int faultAmount = groupKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getFaultAmount)
                    .sum();
            int handleAmount = groupKpiList.stream()
                    .mapToInt(BuRptPersonKpi::getHandleAmount)
                    .sum();

            BuRptPersonKpi groupKpi = groupKpiList.get(0);
            BuKpiTimeItemVO timeItem = new BuKpiTimeItemVO()
                    .setGroupId(groupKpi.getGroupId())
                    .setGroupName(groupKpi.getGroupName())
                    .setTotalTime(totalTime)
                    .setRepairTime(repairTime)
                    .setFaultTime(faultTime);
            timeList.add(timeItem);
            BuKpiFaultItemVO faultItem = new BuKpiFaultItemVO()
                    .setGroupId(groupKpi.getGroupId())
                    .setGroupName(groupKpi.getGroupName())
                    .setTotalHappen(totalHappen)
                    .setFaultAmount(faultAmount)
                    .setHandleAmount(handleAmount);
            faultList.add(faultItem);
        }

        if (CollectionUtils.isNotEmpty(timeList)) {
            timeList.sort(Comparator.comparing(BuKpiTimeItemVO::getTotalTime).reversed());
            int timeSortNo = 1;
            int lastTotalTime = timeList.get(0).getTotalTime();
            for (BuKpiTimeItemVO itemVO : timeList) {
                Integer totalTime = itemVO.getTotalTime();
                if (lastTotalTime != totalTime) {
                    lastTotalTime = totalTime;
                    timeSortNo++;
                }
                itemVO.setSortNo(timeSortNo);
            }
        }

        if (CollectionUtils.isNotEmpty(faultList)) {
            faultList.sort(Comparator.comparing(BuKpiFaultItemVO::getTotalHappen).reversed());
            int faultSortNo = 1;
            int lastTotalHappen = faultList.get(0).getTotalHappen();
            for (BuKpiFaultItemVO itemVO : faultList) {
                Integer totalHappen = itemVO.getTotalHappen();
                if (lastTotalHappen != totalHappen) {
                    lastTotalHappen = totalHappen;
                    faultSortNo++;
                }
                itemVO.setSortNo(faultSortNo);
            }
        }

        return new BuKpiVO().setTimeList(timeList).setFaultList(faultList);
    }


    private LambdaQueryWrapper<BuRptPersonKpi> getQueryWrapper(KpiQueryVO queryVO) {
        LambdaQueryWrapper<BuRptPersonKpi> wrapper = new LambdaQueryWrapper<>();
        if (null == queryVO) {
            return wrapper;
        }

        if (StringUtils.isNotBlank(queryVO.getDepotId())) {
            wrapper.eq(BuRptPersonKpi::getDepotId, queryVO.getDepotId());
        }
        if (StringUtils.isNotBlank(queryVO.getLineId())) {
            wrapper.eq(BuRptPersonKpi::getLineId, queryVO.getLineId());
        }
        if (StringUtils.isNotBlank(queryVO.getPlanId())) {
            wrapper.eq(BuRptPersonKpi::getPlanId, queryVO.getPlanId());
        }
        if (StringUtils.isNotBlank(queryVO.getGroupId())) {
            wrapper.eq(BuRptPersonKpi::getGroupId, queryVO.getGroupId());
        }

        return wrapper;
    }

}
