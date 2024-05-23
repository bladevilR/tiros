package org.jeecg.modules.basemanage.holiday.entity.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 节假日bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/9
 */
@Data
@Accessors(chain = true)
public class HolidayBO {
    private String name;
    private Date date;
    /**
     * 0休息1上班
     */
    private Integer work;
    private String remark;
}
