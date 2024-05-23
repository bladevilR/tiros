package org.jeecg.common.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 不同时间类型查询条件
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-16
 */
@Data
@Accessors(chain = true)
public abstract class TimeQueryMonth {

    @ApiModelProperty(value = "开始月份，格式：2021-11")
    private String startMonth;

    @ApiModelProperty(value = "结束月份，格式：2021-11")
    private String endMonth;

    /**
     * 年份月份列表
     */
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private List<YearMonth> yearMonthList;

    @ApiModelProperty(value = "开始日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;


    public void toYearMonthList() throws ParseException {
        yearMonthList = new ArrayList<>();
        if (StringUtils.isBlank(startMonth) && StringUtils.isBlank(endMonth)) {
            addToYearMonthList(yearMonthList, Calendar.getInstance());
        } else {
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            if (StringUtils.isNotBlank(startMonth)) {
                Date parse = DateUtils.SDF_YYYY_MM.get().parse(startMonth);
                startCalendar.setTime(parse);
            }
            if (StringUtils.isNotBlank(endMonth)) {
                Date parse = DateUtils.SDF_YYYY_MM.get().parse(endMonth);
                endCalendar.setTime(parse);
            }

            while (startCalendar.before(endCalendar)) {
                addToYearMonthList(yearMonthList, startCalendar);
                startCalendar.add(Calendar.MONTH, 1);
            }
            addToYearMonthList(yearMonthList, endCalendar);
        }
    }

    @Data
    @Accessors(chain = true)
    private static class YearMonth {
        private Integer year;
        private Integer month;
    }


    private void addToYearMonthList(List<YearMonth> yearMonthList, Calendar calendar) {
        if (null == calendar) {
            return;
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        addToYearMonthList(yearMonthList, year, month);
    }

    private void addToYearMonthList(List<YearMonth> yearMonthList, int year, int month) {
        if (null == yearMonthList) {
            yearMonthList = new ArrayList<>();
        }

        boolean exist = yearMonthList.stream()
                .anyMatch(item -> year == item.getYear() && month == item.getMonth());
        if (!exist) {
            YearMonth yearMonth = new YearMonth()
                    .setYear(year)
                    .setMonth(month);
            yearMonthList.add(yearMonth);
        }
    }

    public void toStartEndDate() throws ParseException {
        if (StringUtils.isBlank(startMonth) || StringUtils.isBlank(endMonth)) {
            throw new JeecgBootException("请输入开始月份和结束月份");
        }

        // 处理日期，转换成开始和结束时间
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        if (StringUtils.isNotBlank(startMonth)) {
            Date parse = DateUtils.SDF_YYYY_MM.get().parse(startMonth);
            startCalendar.setTime(parse);

            startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (StringUtils.isNotBlank(endMonth)) {
            Date parse = DateUtils.SDF_YYYY_MM.get().parse(endMonth);
            endCalendar.setTime(parse);

            int monthLastDay = endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            endCalendar.set(Calendar.DAY_OF_MONTH, monthLastDay);
        }
        startDate = DateUtils.transToDayStart(startCalendar).getTime();
        endDate = DateUtils.transToDayEnd(endCalendar).getTime();
    }

}
