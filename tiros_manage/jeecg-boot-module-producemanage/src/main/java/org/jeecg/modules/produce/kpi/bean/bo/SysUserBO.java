package org.jeecg.modules.produce.kpi.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户信息bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-19
 */
@Data
@Accessors(chain = true)
public class SysUserBO {

    private String id;
    private String realname;
    private String workNo;

}
