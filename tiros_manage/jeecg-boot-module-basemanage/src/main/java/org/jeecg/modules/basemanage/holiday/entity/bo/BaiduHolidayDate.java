package org.jeecg.modules.basemanage.holiday.entity.bo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 百度节假日查询返回结果holiday--日期列表对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/9
 */
@Data
@Accessors(chain = true)
public class BaiduHolidayDate {
    /**
     * 日期
     */
    @JSONField(format="yyyy-MM-dd")
    private Date date;
    /**
     * 类型 1休息 2上班
     */
    private String status;
}
