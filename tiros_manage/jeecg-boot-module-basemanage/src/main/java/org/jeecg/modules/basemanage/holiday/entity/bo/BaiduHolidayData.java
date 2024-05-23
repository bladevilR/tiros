package org.jeecg.modules.basemanage.holiday.entity.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 百度节假日查询返回结果-data
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/9
 */
@Data
@Accessors(chain = true)
public class BaiduHolidayData {
    private List<BaiduHoliday> holiday;
}
