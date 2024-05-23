package org.jeecg.modules.third.jdx.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 故障时间bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/12/13
 */
@Data
@Accessors(chain = true)
public class FaultTimes {

    private Date affecteddate;
    private Date reportdate;
    private Date actualstart;
    private Date cTmprepairetime;
    private Date actualfinish;

}
