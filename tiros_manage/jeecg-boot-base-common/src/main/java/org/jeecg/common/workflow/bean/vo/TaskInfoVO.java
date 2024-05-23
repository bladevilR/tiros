package org.jeecg.common.workflow.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 流程业务vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class TaskInfoVO {
    /**
     * id : d05d79fb-ab37-11eb-be76-52540003ca3b
     * name : 编制下发工单 （架修调度）
     * startUser : 管理员
     * owner :
     * assignee : 管理员
     * createTime : 2021-05-02 19:16:17
     * priority : 50
     * taskDefinitionKey : Activity_1d8995z
     * processInstanceId : d059a962-ab37-11eb-be76-52540003ca3b
     * processInstanceName : 测试自动发送流程1
     * processDefinitionId : WORK_ORDER_001:1:b25db5ae-a5ed-11eb-8904-005056976b34
     * processDefinitionVersion : 0
     * suspended : false
     * involvedPeople : []
     */
    private String id;
    private String name;
    private String startUser;
    private String owner;
    private String assignee;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    private Integer priority;
    private String taskDefinitionKey;
    private String processInstanceId;
    private String processInstanceName;
    private String processDefinitionId;
    private Integer processDefinitionVersion;
    private Boolean suspended;
    private List<Object> involvedPeople;

}
