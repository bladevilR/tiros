
package org.jeecg.common.workflow.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工作流 用户对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/28
 */
@Data
@Accessors(chain = true)
public class WorkFlowUser {

    private String id;
    private String userName;
    private String userNo;
    private String userPhone;
    private Integer userSex;
    private String email;
    private String groupId;
    private Integer groupCategory;
    private String sortBy;

}
