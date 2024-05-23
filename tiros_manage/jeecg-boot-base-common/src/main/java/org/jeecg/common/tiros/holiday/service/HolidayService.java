package org.jeecg.common.tiros.holiday.service;

import org.jeecg.common.tiros.holiday.bean.SpecialHoliday;

import java.util.Calendar;

/**
 * <p>
 * 节假日获取及计算 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-12
 */
public interface HolidayService {

    /**
     * 获取特殊休息日/工作日
     *
     * @return 特殊休息日/工作日
     * @throws RuntimeException 异常
     */
    SpecialHoliday getSpecialHoliday(Integer year) throws RuntimeException;

    /**
     * 获取下个工作日的日历对象
     *
     * @param calendar       当前日历
     * @param specialHoliday 特殊休息日/工作日
     * @return 下个工作日的日历对象
     * @throws RuntimeException 异常
     */
    Calendar getNextWorkDayCalendar(Calendar calendar, SpecialHoliday specialHoliday) throws RuntimeException;

    /**
     * 获取上个工作日的日历对象
     *
     * @param calendar       当前日历
     * @param specialHoliday 特殊休息日/工作日
     * @return 上个工作日的日历对象
     * @throws RuntimeException 异常
     */
    Calendar getPreviousWorkDayCalendar(Calendar calendar, SpecialHoliday specialHoliday) throws RuntimeException;

}
