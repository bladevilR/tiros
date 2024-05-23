package org.jeecg.modules.basemanage.holiday.entity.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * <p>
 * 提莫免费节假日API查询返回结果
 * 网站：http://timor.tech/api/holiday/
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-30
 */
@Data
@Accessors(chain = true)
public class TimorHolidayResponse {

    private int code;
    private Map<String, TimorHolidayDate> holiday;

}
