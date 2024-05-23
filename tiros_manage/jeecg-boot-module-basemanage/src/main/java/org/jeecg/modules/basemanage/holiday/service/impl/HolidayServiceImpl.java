package org.jeecg.modules.basemanage.holiday.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.tiros.holiday.bean.SpecialHoliday;
import org.jeecg.common.tiros.holiday.service.HolidayService;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.basemanage.holiday.entity.BuBaseHoliday;
import org.jeecg.modules.basemanage.holiday.mapper.BuBaseHolidayMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 节假日获取及计算 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-12
 */
@Service
public class HolidayServiceImpl implements HolidayService {

    @Resource
    private BuBaseHolidayMapper buBaseHolidayMapper;


    /**
     * @see HolidayService#getSpecialHoliday(Integer)
     */
    @Override
    public SpecialHoliday getSpecialHoliday(Integer year) throws RuntimeException {
        // 查询节假日信息
        LambdaQueryWrapper<BuBaseHoliday> baseHolidayWrapper = new LambdaQueryWrapper<>();
        if (null != year) {
            baseHolidayWrapper.ge(BuBaseHoliday::getYear, year);
        }
        List<BuBaseHoliday> holidayList = buBaseHolidayMapper.selectList(baseHolidayWrapper);

        // 计算出特殊休息日和特殊工作日
        Set<Date> specialHolidayDaySet = new HashSet<>();
        Set<Date> specialWorkDaySet = new HashSet<>();
        getSpecialDayList(holidayList, specialHolidayDaySet, specialWorkDaySet);

        return new SpecialHoliday()
                .setSpecialHolidayDaySet(specialHolidayDaySet)
                .setSpecialWorkDaySet(specialWorkDaySet);
    }

    /**
     * @see HolidayService#getNextWorkDayCalendar(Calendar, SpecialHoliday)
     */
    @Override
    public Calendar getNextWorkDayCalendar(Calendar calendar, SpecialHoliday specialHoliday) throws RuntimeException {
        // 日期加一天
        calendar.add(Calendar.DATE, 1);
        boolean isWorkDay = isWorkDay(calendar, specialHoliday);

        if (isWorkDay) {
            return calendar;
        } else {
            return getNextWorkDayCalendar(calendar, specialHoliday);
        }
    }

    /**
     * @see HolidayService#getPreviousWorkDayCalendar(Calendar, SpecialHoliday)
     */
    @Override
    public Calendar getPreviousWorkDayCalendar(Calendar calendar, SpecialHoliday specialHoliday) throws RuntimeException {
        // 日期减一天
        calendar.add(Calendar.DATE, -1);
        boolean isWorkDay = isWorkDay(calendar, specialHoliday);

        if (isWorkDay) {
            return calendar;
        } else {
            return getPreviousWorkDayCalendar(calendar, specialHoliday);
        }
    }


    private void getSpecialDayList(List<BuBaseHoliday> holidayList, Set<Date> specialHolidayDaySet, Set<Date> specialWorkDaySet) {
        if (CollectionUtils.isEmpty(holidayList)) {
            return;
        }

        for (BuBaseHoliday baseHoliday : holidayList) {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(baseHoliday.getStartTime());

            if (1 == baseHoliday.getDays()) {
                if (baseHoliday.getWork() == 0) {
                    specialHolidayDaySet.add(startCalendar.getTime());
                } else {
                    specialWorkDaySet.add(startCalendar.getTime());
                }
            } else {
                Integer days = baseHoliday.getDays();
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(baseHoliday.getStartTime());
                endCalendar.add(Calendar.DATE, days);

                if (baseHoliday.getWork() == 0) {
                    while (startCalendar.before(endCalendar)) {
                        specialHolidayDaySet.add(startCalendar.getTime());
                        startCalendar.add(Calendar.DATE, 1);
                    }
                } else {
                    while (startCalendar.before(endCalendar)) {
                        specialWorkDaySet.add(startCalendar.getTime());
                        startCalendar.add(Calendar.DATE, 1);
                    }
                }
            }
        }
    }

    private boolean isWorkDay(Calendar calendar, SpecialHoliday specialHoliday) {
        boolean isWorkDay = false;
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1 || dayOfWeek == 7) {
            // 是一般休息日，周六周日
            if (DateUtils.containsDay(specialHoliday.getSpecialWorkDaySet(), calendar.getTime())) {
                // 是特殊工作日，放假调休
                isWorkDay = true;
            }
        } else {
            isWorkDay = true;
            // 是一般工作日，周一至周五
            if (DateUtils.containsDay(specialHoliday.getSpecialHolidayDaySet(), calendar.getTime())) {
                // 是特殊休息日
                isWorkDay = false;
            }
        }

        return isWorkDay;
    }

}
