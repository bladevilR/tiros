package org.jeecg.modules.basemanage.holiday.entity.bo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 百度节假日查询返回结果holiday
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/9
 */
@Data
@Accessors(chain = true)
public class BaiduHoliday {
    /**
     * 名称
     */
    private String name;
    /**
     * 详情
     */
    private String desc;
    /**
     * 节日日期
     */
    @JSONField(format="yyyy-MM-dd")
    private Date festival;
    /**
     * 调休备注
     */
    private String rest;
    /**
     *
     */
    private List<BaiduHolidayDate> list;
}
