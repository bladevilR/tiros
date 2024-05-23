package org.jeecg.modules.dispatch.plan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.holiday.bean.SpecialHoliday;
import org.jeecg.common.tiros.holiday.service.HolidayService;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.plan.bean.bo.BuTpRepairPlanBO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearAutoGenerateVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearDetailVOWithTaskGantt;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanYearDetailMapper;
import org.jeecg.modules.dispatch.plan.service.BuRepairPlanYearDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 年计划明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Slf4j
@Service
public class BuRepairPlanYearDetailServiceImpl extends ServiceImpl<BuRepairPlanYearDetailMapper, BuRepairPlanYearDetail> implements BuRepairPlanYearDetailService {

    @Resource
    private BuRepairPlanYearDetailMapper buRepairPlanYearDetailMapper;
    @Resource
    private HolidayService holidayService;


    /**
     * @see BuRepairPlanYearDetailService#listByYearAndStatus(Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairPlanYearDetail> listByYearAndStatus(Integer year, Integer status) throws Exception {
        return buRepairPlanYearDetailMapper.selectListByYearAndStatus(year, status);
    }

    /**
     * @see BuRepairPlanYearDetailService#autoGenerate(BuRepairPlanYearAutoGenerateVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<BuRepairPlanYearDetail> autoGenerate(BuRepairPlanYearAutoGenerateVO generateVO) throws Exception {
        if (generateVO.getHightAmount() > 0 && StringUtils.isBlank(generateVO.getHightTpRepairPlanId())) {
            throw new JeecgBootException("大修数 = " + generateVO.getHightAmount() + "，大修模板不能为空");
        }
        if (generateVO.getMiddleAmount() > 0 && StringUtils.isBlank(generateVO.getMiddleTpRepairPlanId())) {
            throw new JeecgBootException("架修数 = " + generateVO.getHightAmount() + "，架修模板不能为空");
        }

        BuTpRepairPlanBO highTpRepairPlan = null;
        BuTpRepairPlanBO middleTpRepairPlan = null;
        if (StringUtils.isNotBlank(generateVO.getHightTpRepairPlanId())) {
            // 查询计划模板信息
            highTpRepairPlan = buRepairPlanYearDetailMapper.selectBuTpRepairPlanBOByTpRepairPlanId(generateVO.getHightTpRepairPlanId());
            // 获取计划模板任务工期序号和对应作业班组信息，用于排程
            setDayIndexWorkGroupInfo(highTpRepairPlan);
        }
        if (StringUtils.isNotBlank(generateVO.getMiddleTpRepairPlanId())) {
            // 查询计划模板信息
            middleTpRepairPlan = buRepairPlanYearDetailMapper.selectBuTpRepairPlanBOByTpRepairPlanId(generateVO.getMiddleTpRepairPlanId());
            // 获取计划模板任务工期序号和对应作业班组信息，用于排程
            setDayIndexWorkGroupInfo(middleTpRepairPlan);
        }

        // 获取车间能力
        Integer ability = buRepairPlanYearDetailMapper.selectWorkshopAbilityByWorkshopId(generateVO.getWorkshopId());
        if (null == ability) {
            ability = 1;
        }

        // 根据系统中节假日信息获取特殊日期
        SpecialHoliday specialHoliday = holidayService.getSpecialHoliday(generateVO.getYear());

        // 获取年计划明细跳过节假日的实际开始时间
        Calendar planActStartCalendar = Calendar.getInstance();
        planActStartCalendar.setTime(generateVO.getFirstTime());
        planActStartCalendar.add(Calendar.DATE, -1);
        planActStartCalendar = holidayService.getNextWorkDayCalendar(planActStartCalendar, specialHoliday);
        Date planActStartDate = planActStartCalendar.getTime();
        generateVO.setFirstTime(planActStartDate);

        // 生成年计划明细
        return generatePlanYearDetail(generateVO, highTpRepairPlan, middleTpRepairPlan, ability, specialHoliday);
    }

    /**
     * @see BuRepairPlanYearDetailService#saveGanttList(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveGanttList(List<BuRepairPlanYearDetailVOWithTaskGantt> ganttList) throws Exception {
        List<BuRepairPlanYearDetail> detailList = transformFromGantt(ganttList);

        if (CollectionUtils.isNotEmpty(detailList)) {
            String yearPlanId = detailList.get(0).getYearPlanId();

            // 删除旧的年计划明细
            LambdaQueryWrapper<BuRepairPlanYearDetail> detailWrapper = new LambdaQueryWrapper<BuRepairPlanYearDetail>()
                    .eq(BuRepairPlanYearDetail::getYearPlanId, yearPlanId);
            buRepairPlanYearDetailMapper.delete(detailWrapper);

            // 重新插入
            saveBatch(detailList);
        }

        return true;
    }


    private void setDayIndexWorkGroupInfo(BuTpRepairPlanBO tpRepairPlanBO) {
        String tpRepairPlanId = tpRepairPlanBO.getId();
        List<Map<String, Object>> dayIndexGroupIdMapList = buRepairPlanYearDetailMapper.selectTpRepairPlanDayIndexAndGroupByTpRepairPlanId(tpRepairPlanId);
        if (CollectionUtils.isEmpty(dayIndexGroupIdMapList)) {
            return;
        }

        Map<Integer, Set<String>> dayIndexWorkGroupIdSetMap = new HashMap<>(32);
        for (Map<String, Object> dayIndexGroupIdMap : dayIndexGroupIdMapList) {
            Object dayIndexObj = dayIndexGroupIdMap.get("dayIndex");
            Object groupIdObj = dayIndexGroupIdMap.get("groupId");
            if (null == dayIndexObj || null == groupIdObj) {
                continue;
            }

            Integer dayIndex = DataTypeCastUtil.transNumber(dayIndexObj).intValue();
            String groupId = (String) groupIdObj;

            Set<String> groupIdSet = dayIndexWorkGroupIdSetMap.getOrDefault(dayIndex, new HashSet<>());
            groupIdSet.add(groupId);
            dayIndexWorkGroupIdSetMap.put(dayIndex, groupIdSet);
        }
        tpRepairPlanBO.setDayIndexWorkGroupIdSetMap(dayIndexWorkGroupIdSetMap);
    }

    private List<BuRepairPlanYearDetail> generatePlanYearDetail(BuRepairPlanYearAutoGenerateVO generateVO,
                                                                BuTpRepairPlanBO highTpRepairPlan,
                                                                BuTpRepairPlanBO middleTpRepairPlan,
                                                                Integer ability,
                                                                SpecialHoliday specialHoliday) {
        List<BuRepairPlanYearDetail> planYearDetailList = new ArrayList<>();

        int trainIndexInt = 1;
        int highAmount = generateVO.getHightAmount();
        int middleAmount = generateVO.getMiddleAmount();
        int highProgramIndexInt = 1;
        int middleProgramIndexInt = 1;
        LinkedHashMap<Integer, BuTpRepairPlanBO> trainIndexTpRepairPlanMap = new LinkedHashMap<>(16);
        LinkedHashMap<Integer, Integer> trainIndexProgramIndexMap = new LinkedHashMap<>(16);
        while (highAmount > 0) {
            trainIndexTpRepairPlanMap.put(trainIndexInt, highTpRepairPlan);
            trainIndexProgramIndexMap.put(trainIndexInt, highProgramIndexInt);
            highAmount--;
            trainIndexInt++;
            highProgramIndexInt++;
        }
        while (middleAmount > 0) {
            trainIndexTpRepairPlanMap.put(trainIndexInt, middleTpRepairPlan);
            trainIndexProgramIndexMap.put(trainIndexInt, middleProgramIndexInt);
            middleAmount--;
            trainIndexInt++;
            middleProgramIndexInt++;
        }

        Date firstTime = generateVO.getFirstTime();
        List<Date> usableTimeQueue = new ArrayList<>();
        for (int i = 1; i <= ability; i++) {
            usableTimeQueue.add(firstTime);
        }
        for (Map.Entry<Integer, BuTpRepairPlanBO> trainIndexTpRepairPlanEntry : trainIndexTpRepairPlanMap.entrySet()) {
            int trainIndex = trainIndexTpRepairPlanEntry.getKey();
            BuTpRepairPlanBO currentTpRepairPlanBO = trainIndexTpRepairPlanEntry.getValue();
            BuTpRepairPlanBO nextTpRepairPlanBO = trainIndexTpRepairPlanMap.get(trainIndex + 1);
            Integer programIndex = trainIndexProgramIndexMap.get(trainIndex);

            Date usableTime = usableTimeQueue.remove(0);
            Date finishDate = generateDetail(trainIndex, programIndex, currentTpRepairPlanBO, nextTpRepairPlanBO, specialHoliday, planYearDetailList, usableTime);

            usableTimeQueue.add(finishDate);
            usableTimeQueue.sort(Comparator.comparing(Date::getTime));
        }

        return planYearDetailList;
    }

    private Date generateDetail(int trainIndex,
                                int programIndex,
                                BuTpRepairPlanBO currentTpRepairPlanBO,
                                BuTpRepairPlanBO nextTpRepairPlanBO,
                                SpecialHoliday specialHoliday,
                                List<BuRepairPlanYearDetail> planYearDetailList,
                                Date startDate) {
        BuRepairPlanYearDetail planYearDetail = new BuRepairPlanYearDetail()
                .setLineId(currentTpRepairPlanBO.getLineId())
                .setLineName(currentTpRepairPlanBO.getLineName())
                .setTrainIndex(trainIndex)
                .setProgramIndex(programIndex)
                .setAmount(1)
                .setProgramId(currentTpRepairPlanBO.getRepairProgramId())
                .setProgramName(currentTpRepairPlanBO.getRepairProgramName())
                .setStatus(1)
                .setRemark(String.format("根据【%s】自动生成，共需%s个工作日", currentTpRepairPlanBO.getTpName(), currentTpRepairPlanBO.getDuration()))
                .setStartDate(startDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int workDays = 1;
        while (workDays < currentTpRepairPlanBO.getDuration()) {
            calendar = holidayService.getNextWorkDayCalendar(calendar, specialHoliday);
            workDays++;
        }
        planYearDetail.setFinishDate(calendar.getTime());
        planYearDetailList.add(planYearDetail);
        calendar = holidayService.getNextWorkDayCalendar(calendar, specialHoliday);

        // 判断可提前天数：如下个计划开始第1天，和当前计划结束倒数第x天，作业班组不重复，则可提前x天
        int daysInAdvance = getDaysInAdvance(currentTpRepairPlanBO, nextTpRepairPlanBO);
        while (daysInAdvance > 0) {
            calendar = holidayService.getPreviousWorkDayCalendar(calendar, specialHoliday);
            daysInAdvance--;
        }

        return calendar.getTime();
    }

    private int getDaysInAdvance(BuTpRepairPlanBO currentTpRepairPlanBO,
                                 BuTpRepairPlanBO nextTpRepairPlanBO) {
        if (null == currentTpRepairPlanBO || null == nextTpRepairPlanBO) {
            return 0;
        }

        Map<Integer, Set<String>> currentTpDayIndexWorkGroupIdSetMap = currentTpRepairPlanBO.getDayIndexWorkGroupIdSetMap();
        Map<Integer, Set<String>> nextTpDayIndexWorkGroupIdSetMap = nextTpRepairPlanBO.getDayIndexWorkGroupIdSetMap();
        if (currentTpDayIndexWorkGroupIdSetMap.isEmpty() || nextTpDayIndexWorkGroupIdSetMap.isEmpty()) {
            return 0;
        }

        int currentTpMaxDayIndex = getMaxDayIndex(currentTpDayIndexWorkGroupIdSetMap);
        int nextTpMinDayIndex = getMinDayIndex(nextTpDayIndexWorkGroupIdSetMap);

        int daysInAdvance = 0;
        boolean canAdvance = true;

        Set<String> nextTpMinDayIndexGroupIdSet = nextTpDayIndexWorkGroupIdSetMap.get(nextTpMinDayIndex);
        while (canAdvance) {
            Set<String> currentTpGroupIdSet = currentTpDayIndexWorkGroupIdSetMap.get(currentTpMaxDayIndex - daysInAdvance);

            Set<String> allGroupIdSet = new HashSet<>();
            allGroupIdSet.addAll(currentTpGroupIdSet);
            allGroupIdSet.addAll(nextTpMinDayIndexGroupIdSet);
            boolean groupNotOverlap = allGroupIdSet.size() == currentTpGroupIdSet.size() + nextTpMinDayIndexGroupIdSet.size();
            if (groupNotOverlap) {
                daysInAdvance++;
            } else {
                canAdvance = false;
            }
        }

        return daysInAdvance;
    }

    private int getMaxDayIndex(Map<Integer, Set<String>> dayIndexWorkGroupIdSetMap) {
        Optional<Integer> maxOptional = dayIndexWorkGroupIdSetMap.keySet().stream()
                .max(Integer::compareTo);
        return maxOptional.orElse(0);
    }

    private int getMinDayIndex(Map<Integer, Set<String>> dayIndexWorkGroupIdSetMap) {
        Optional<Integer> minOptional = dayIndexWorkGroupIdSetMap.keySet().stream()
                .min(Integer::compareTo);
        return minOptional.orElse(0);
    }

    private List<BuRepairPlanYearDetail> transformFromGantt(List<BuRepairPlanYearDetailVOWithTaskGantt> ganttList) {
        if (CollectionUtils.isEmpty(ganttList)) {
            return new ArrayList<>();
        }

        List<BuRepairPlanYearDetail> detailList = new ArrayList<>();
        for (BuRepairPlanYearDetailVOWithTaskGantt detailGantt : ganttList) {
            detailList.add(transformFromGantt(detailGantt));
        }

        return detailList;
    }

    private BuRepairPlanYearDetail transformFromGantt(BuRepairPlanYearDetailVOWithTaskGantt detailGantt) {
        if (null == detailGantt) {
            return null;
        }

        BuRepairPlanYearDetail detail = new BuRepairPlanYearDetail();
        BeanUtils.copyProperties(detailGantt, detail);
        // 甘特图字段转业务表字段
        if (StringUtils.isBlank(detail.getId())) {
            detail.setId(detailGantt.getUID());
        }
        if (null != detailGantt.getID()) {
            detail.setTrainIndex(detailGantt.getID());
        }
        if (null != detailGantt.getStart()) {
            detail.setStartDate(detailGantt.getStart());
        }
        if (null != detailGantt.getFinish()) {
            detail.setFinishDate(detailGantt.getFinish());
        }

        return detail;
    }

}
