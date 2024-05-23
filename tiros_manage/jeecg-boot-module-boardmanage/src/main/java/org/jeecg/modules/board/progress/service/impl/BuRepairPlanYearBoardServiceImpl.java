package org.jeecg.modules.board.progress.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.board.progress.bean.BuRepairPlan;
import org.jeecg.modules.board.progress.bean.BuRepairPlanYear;
import org.jeecg.modules.board.progress.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.board.progress.bean.vo.*;
import org.jeecg.modules.board.progress.mapper.BuRepairPlanBoardMapper;
import org.jeecg.modules.board.progress.mapper.BuRepairPlanYearBoardMapper;
import org.jeecg.modules.board.progress.service.BuRepairPlanYearBoardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 年计划 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-02
 */
@Slf4j
@Service
public class BuRepairPlanYearBoardServiceImpl extends ServiceImpl<BuRepairPlanYearBoardMapper, BuRepairPlanYear> implements BuRepairPlanYearBoardService {

    @Resource
    private BuRepairPlanYearBoardMapper buRepairPlanYearBoardMapper;
    @Resource
    private BuRepairPlanBoardMapper buRepairPlanBoardMapper;


    /**
     * @see BuRepairPlanYearBoardService#listPlanYearProgress(BuBoardProgressQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuPlanYearProgressProgramVO> listPlanYearProgress(BuBoardProgressQueryVO queryVO) throws Exception {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        // 查询年计划明细，求总数
        List<BuRepairPlanYearDetail> buRepairPlanYearDetailList = buRepairPlanYearBoardMapper.selectRepairPlanYearDetailListByYearCondition(year, queryVO);
        // 查询列计划信息
        List<BuRepairPlan> repairPlanList = buRepairPlanBoardMapper.selectRepairPlanListByYearAndCondition(year, queryVO);
//        // 过滤关联了年计划明细的列计划，用于求完成数
//        repairPlanList = repairPlanList.stream()
//                .filter(plan -> StringUtils.isNotBlank(plan.getYearDetailId()))
//                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(buRepairPlanYearDetailList)) {
            BuPlanYearProgressDepotVO depotVO = getProgressDepotVO(repairPlanList, buRepairPlanYearDetailList, queryVO.getDepotId());
            return getProgramVOListForFrontTable(depotVO);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @see BuRepairPlanYearBoardService#getFinishQuality(BuBoardProgressQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<PieChartVO> getFinishQuality(BuBoardProgressQueryVO queryVO) throws Exception {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<BuRepairPlan> repairPlanList = buRepairPlanBoardMapper.selectRepairPlanListByYearAndCondition(year, queryVO);

        BuPlanYearFinishQualityVO finishQualityVO = new BuPlanYearFinishQualityVO()
                .setAmount(0)
                .setNormal(0)
                .setDelay(0)
                .setLeading(0);

        if (CollectionUtils.isNotEmpty(repairPlanList)) {
            Map<Integer, Long> progressStatusCountMap = repairPlanList.stream()
                    .collect(Collectors.groupingBy(BuRepairPlan::getProgressStatus, Collectors.counting()));

            finishQualityVO
                    .setNormal(progressStatusCountMap.containsKey(3) ? progressStatusCountMap.get(3).intValue() : 0)
                    .setDelay(progressStatusCountMap.containsKey(4) ? progressStatusCountMap.get(4).intValue() : 0)
                    .setLeading(progressStatusCountMap.containsKey(5) ? progressStatusCountMap.get(5).intValue() : 0);
            finishQualityVO.setAmount(finishQualityVO.getNormal() + finishQualityVO.getDelay() + finishQualityVO.getLeading());
        }

        return transformToPieChartVOList(finishQualityVO);
    }

    /**
     * @see BuRepairPlanYearBoardService#getBurnDownChartData(BuBoardProgressQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<LineChartVO> getBurnDownChartData(BuBoardProgressQueryVO queryVO) throws Exception {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        // 查询年计划明细，用于求总数
        List<BuRepairPlanYearDetail> buRepairPlanYearDetailList = buRepairPlanYearBoardMapper.selectRepairPlanYearDetailListByYearCondition(year, queryVO);
        // 查询列计划信息
        List<BuRepairPlan> repairPlanList = buRepairPlanBoardMapper.selectRepairPlanListByYearAndCondition(year, queryVO);
//        // 过滤关联了年计划明细的列计划，用于求完成数
//        repairPlanList = repairPlanList.stream()
//                .filter(plan -> StringUtils.isNotBlank(plan.getYearDetailId()))
//                .collect(Collectors.toList());

        List<BuPlanYearMonthFinishVO> monthFinishVOList = getMonthFinishVOList(year, buRepairPlanYearDetailList, repairPlanList);

        return transformToLineChartVOList(monthFinishVOList);
    }


    private List<BuPlanYearProgressDepotVO> getProgressDepotVOList(List<BuRepairPlanYearDetail> buRepairPlanYearDetailList, List<BuRepairPlan> repairPlanList) {
        List<BuPlanYearProgressDepotVO> resultList = new ArrayList<>();

        Map<String, List<BuRepairPlanYearDetail>> depotIdPlanYearDetailListMap = buRepairPlanYearDetailList.stream()
                .collect(Collectors.groupingBy(BuRepairPlanYearDetail::getDepotId));
        for (Map.Entry<String, List<BuRepairPlanYearDetail>> depotIdPlanYearDetailListEntry : depotIdPlanYearDetailListMap.entrySet()) {
            String depotId = depotIdPlanYearDetailListEntry.getKey();
            List<BuRepairPlanYearDetail> planYearDetailList = depotIdPlanYearDetailListEntry.getValue();

            BuPlanYearProgressDepotVO depotVO = getProgressDepotVO(repairPlanList, planYearDetailList, depotId);

            resultList.add(depotVO);
        }

        return resultList;
    }

    private List<BuPlanYearProgressProgramVO> getProgramVOListForFrontTable(BuPlanYearProgressDepotVO depotVO) {
        if (null == depotVO || CollectionUtils.isEmpty(depotVO.getPrograms())) {
            return new ArrayList<>();
        }

        List<BuPlanYearProgressProgramVO> programs = depotVO.getPrograms();
        for (BuPlanYearProgressProgramVO program : programs) {
            program.setDepotName(depotVO.getDepotName())
                    .setOverallProgress(depotVO.getOverallProgress());
        }

        return programs;
    }

    private BuPlanYearProgressDepotVO getProgressDepotVO(List<BuRepairPlan> repairPlanList, List<BuRepairPlanYearDetail> planYearDetailList, String depotId) {
        int overallAmount = 0;
        int overallFinish = 0;

        Set<String> programNameSet = repairPlanList.stream()
                .filter(plan -> StringUtils.isNotBlank(plan.getRepairProgramName()))
                .collect(Collectors.groupingBy(BuRepairPlan::getRepairProgramName))
                .keySet();
        Map<String, List<BuRepairPlanYearDetail>> programNamePlanYearDetailListMap = planYearDetailList.stream()
                .filter(planYearDetail -> StringUtils.isNotBlank(planYearDetail.getProgramName()))
                .collect(Collectors.groupingBy(BuRepairPlanYearDetail::getProgramName));
        for (String programName : programNameSet) {
            if (!programNamePlanYearDetailListMap.containsKey(programName)) {
                programNamePlanYearDetailListMap.put(programName, new ArrayList<>());
            }
        }

        Map<String, BuPlanYearProgressProgramVO> programNameProgramMap = new HashMap<>(2);
        for (Map.Entry<String, List<BuRepairPlanYearDetail>> programNamePlanYearDetailListEntry : programNamePlanYearDetailListMap.entrySet()) {
            String programName = programNamePlanYearDetailListEntry.getKey();
            List<BuRepairPlanYearDetail> yearDetailList = programNamePlanYearDetailListEntry.getValue();

            BuPlanYearProgressProgramVO programVO = getProgressProgramVO(repairPlanList, depotId, programName, yearDetailList);

            programNameProgramMap.put(programVO.getProgramName(), programVO);

            overallAmount = overallAmount + programVO.getAmount();
            overallFinish = overallFinish + programVO.getFinish();
        }

        return new BuPlanYearProgressDepotVO()
                .setDepotName(StringUtils.isBlank(depotId) ? "所有车辆段" : planYearDetailList.get(0).getDepotName())
                .setPrograms(new ArrayList<>(programNameProgramMap.values()))
                .setOverallProgress(PercentUtils.percentWithSign(overallFinish, overallAmount).toString());
    }

    private BuPlanYearProgressProgramVO getProgressProgramVO(List<BuRepairPlan> repairPlanList, String depotId, String programName, List<BuRepairPlanYearDetail> yearDetailList) {
        // 符合条件（车辆段和修程）的列计划
        List<BuRepairPlan> planList;
        if (StringUtils.isNotBlank(depotId)) {
            planList = repairPlanList.stream()
                    .filter(rp -> depotId.equals(rp.getDepotId()) && programName.equals(rp.getRepairProgramName()))
                    .collect(Collectors.toList());
        } else {
            planList = repairPlanList.stream()
                    .filter(rp -> programName.equals(rp.getRepairProgramName()))
                    .collect(Collectors.toList());
        }
        // 年计划明细维修数
        int yearDetailAmountSum = yearDetailList.stream()
                .mapToInt(BuRepairPlanYearDetail::getAmount)
                .sum();
        // 列计划完成数
        long planFinishCount = planList.stream()
                .filter(rp -> Arrays.asList(3, 4, 5).contains(rp.getProgressStatus()))
                .count();
        // 列计划正在维修数
        long planRepairingCount = planList.stream()
                .filter(rp -> Arrays.asList(1, 2).contains(rp.getProgressStatus()))
                .count();

        BuPlanYearProgressProgramVO programVO = new BuPlanYearProgressProgramVO()
                .setProgramName(programName)
                .setAmount(yearDetailAmountSum)
                .setFinish(new Long(planFinishCount).intValue())
                .setRepairing(new Long(planRepairingCount).intValue());
        if (programVO.getAmount() == 0) {
            programVO.setFinishProgress("100%");
        } else {
            programVO.setFinishProgress(PercentUtils.percent(programVO.getFinish(), programVO.getAmount()).toString());
        }

        return programVO;
    }

    private List<BuPlanYearMonthFinishVO> getMonthFinishVOList(int year, List<BuRepairPlanYearDetail> buRepairPlanYearDetailList, List<BuRepairPlan> repairPlanList) {
        // 年计划明细维修总数
        int yearDetailAmountSum = buRepairPlanYearDetailList.stream()
                .mapToInt(BuRepairPlanYearDetail::getAmount)
                .sum();
        int planRemainingQuality = yearDetailAmountSum;
        int actualRemainingQuality = yearDetailAmountSum;

        int currentMonth = DateUtils.getMonth(new Date());
        int monthSize = 12;
        List<BuPlanYearMonthFinishVO> monthFinishVOList = new ArrayList<>(monthSize);
        // 加第一条数据=初始数据
        BuPlanYearMonthFinishVO zeroFinishVO = new BuPlanYearMonthFinishVO()
                .setMonth(year + "-" + 0)
                .setPlan(planRemainingQuality)
                .setActual(actualRemainingQuality);
        monthFinishVOList.add(zeroFinishVO);
        for (int i = 1; i <= monthSize; i++) {
            int month = i;

            // 本月年计划明细计划完成数
            int yearDetailPlanFinishCount = buRepairPlanYearDetailList.stream()
                    .filter(pyd -> null != pyd.getFinishDate()
                            && year == DateUtils.getYear(pyd.getFinishDate())
                            && month == DateUtils.getMonth(pyd.getFinishDate()))
                    .mapToInt(BuRepairPlanYearDetail::getAmount)
                    .sum();
            // 本月实际完成数=列计划的完成数
            long actualFinishCount = repairPlanList.stream()
                    .filter(rp -> (null != rp.getActFinish()
                            && year == DateUtils.getYear(rp.getActFinish())
                            && month == DateUtils.getMonth(rp.getActFinish()))
                            && Arrays.asList(3, 4, 5).contains(rp.getProgressStatus()))
                    .count();

            planRemainingQuality = planRemainingQuality - yearDetailPlanFinishCount;
            actualRemainingQuality = actualRemainingQuality - new Long(actualFinishCount).intValue();

            BuPlanYearMonthFinishVO monthFinishVO = new BuPlanYearMonthFinishVO()
                    .setMonth(year + "-" + month)
                    .setPlan(planRemainingQuality)
                    .setActual(actualRemainingQuality);

            // 实际完成，当前月后面的数据不应该设置
            if (month > currentMonth) {
                monthFinishVO.setActual(null);
            }

            monthFinishVOList.add(monthFinishVO);
        }

        return monthFinishVOList;
    }

    private List<PieChartVO> transformToPieChartVOList(BuPlanYearFinishQualityVO finishQualityVO) {
        List<PieChartVO> result = new ArrayList<>(3);
        result.add(new PieChartVO().setItem("正常完工").setCount(finishQualityVO.getNormal().doubleValue()));
        result.add(new PieChartVO().setItem("延期完工").setCount(finishQualityVO.getDelay().doubleValue()));
        result.add(new PieChartVO().setItem("超前完工").setCount(finishQualityVO.getLeading().doubleValue()));
        return result;
    }

    private List<LineChartVO> transformToLineChartVOList(List<BuPlanYearMonthFinishVO> monthFinishVOList) {
        List<LineChartVO> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(monthFinishVOList)) {
            for (BuPlanYearMonthFinishVO monthFinishVO : monthFinishVOList) {
                result.add(
                        new LineChartVO()
                                .setType(monthFinishVO.getMonth())
                                .setJeecg(null == monthFinishVO.getPlan() ? null : monthFinishVO.getPlan().doubleValue())
                                .setJeebt(null == monthFinishVO.getActual() ? null : monthFinishVO.getActual().doubleValue())
                );
            }
        }

        return result;
    }

}
