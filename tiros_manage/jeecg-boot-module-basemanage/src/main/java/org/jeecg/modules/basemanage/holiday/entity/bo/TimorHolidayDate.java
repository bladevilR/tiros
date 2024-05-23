package org.jeecg.modules.basemanage.holiday.entity.bo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 提莫免费节假日API查询返回结果--日期列表对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-30
 */
@Data
@Accessors(chain = true)
public class TimorHolidayDate {

    private Boolean holiday;
    private String name;
    private Boolean after;
    private Integer wage;
    private String target;
    @JSONField(format = "yyyy-MM-dd")
    private Date date;
    private Integer rest;

}
