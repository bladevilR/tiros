package org.jeecg.modules.system.vo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQueryMonth;

/**
 * <p>
 * 用户系统访问统计 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-11-13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class LoginStatisticQueryVO extends TimeQueryMonth {

}
