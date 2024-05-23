package org.jeecg.modules.dispatch.workorder.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/9
 */
@Data
@Accessors
public class SysUserBO {
    private String id;
    private String username;
    private String realname;
    private String password;
    private String salt;

    /**
     * 用于发送消息时，存储工班id
     */
    private String groupId;
}
