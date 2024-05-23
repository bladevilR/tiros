package org.jeecg.common.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;
import java.util.stream.Collectors;

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
public abstract class TimeQuery {

    @ApiModelProperty(value = "时间类型 1年(需传入year) 2季度(需传入year+quarter) 3月(需传入year+month) 4日(需传入startDate+endDate)")
    private Integer dateType;

    @ApiModelProperty(value = "年")
    private Integer year;

    @ApiModelProperty(value = "季度")
    private Integer quarter;

    @ApiModelProperty(value = "月")
    private Integer month;

    @ApiModelProperty(value = "开始日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 月份列表，dateType为季度和月的时候，转化为monthList，用于在统计表中查询数据
     */
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private List<Integer> monthList;


    private void checkTimeParam(int... allowDateTypes) {
        if (dateType != null) {
            List<Integer> dateTypeList = Arrays.stream(allowDateTypes).boxed().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(dateTypeList)) {
                dateTypeList = Arrays.asList(1, 2, 3, 4);
            }

            if (!dateTypeList.contains(dateType)) {
                throw new JeecgBootException("请选择正确的时间类型");
            }
            if (dateTypeList.contains(dateType) && dateType == 1) {
                if (null == year) {
                    throw new JeecgBootException("请输入年份");
                }
            }
            if (dateTypeList.contains(dateType) && dateType == 2) {
                if (null == year) {
                    throw new JeecgBootException("请输入年份");
                }
                if (null == quarter) {
                    throw new JeecgBootException("请输入季度");
                }
            }
            if (dateTypeList.contains(dateType) && dateType == 3) {
                if (null == year) {
                    throw new JeecgBootException("请输入年份");
                }
                if (null == month) {
                    throw new JeecgBootException("请输入月份");
                }
            }
            if (dateTypeList.contains(dateType) && dateType == 4) {
                if (null == startDate || null == endDate) {
                    throw new JeecgBootException("请输入开始和结束时间");
                }
            }
        }
    }

    public void toStartEndDate(int... allowDateTypes) {
        checkTimeParam(allowDateTypes);

        if (null == dateType || dateType == 0) {
            startDate = null;
            endDate = null;
            return;
        }

        // 处理日期，转换成开始和结束时间
        Calendar startDateCalendar = Calendar.getInstance();
        Calendar endDateCalendar = Calendar.getInstance();
        int monthLastDay;
        switch (dateType) {
            case 1:
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, Calendar.JANUARY);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);

                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, Calendar.DECEMBER);
                monthLastDay = endDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, monthLastDay);
                break;
            case 2:
                int startMonth = (quarter - 1) * 3;
                int endMonth = startMonth + 2;
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, startMonth);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);

                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, endMonth);
                monthLastDay = endDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, monthLastDay);
                break;
            case 3:
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month - 1);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);

                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month - 1);
                monthLastDay = endDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, monthLastDay);
                break;
            case 4:
                startDateCalendar.setTime(startDate);
                endDateCalendar.setTime(endDate);
                break;
            default:
                break;
        }
        startDate = DateUtils.transToDayStart(startDateCalendar).getTime();
        endDate = DateUtils.transToDayEnd(endDateCalendar).getTime();
    }

}
