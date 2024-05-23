
package org.jeecg.common.workflow.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 工作流 组对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/28
 */
@Data
@Accessors(chain = true)
public class WorkFlowGroup {

    private String id;
    private String parentId;
    private String groupName;
    private Integer groupCategory;
    private String groupDesc;
    private String sortBy;
    private List<WorkFlowUser> users;
    private List<WorkFlowGroup> children;

}
