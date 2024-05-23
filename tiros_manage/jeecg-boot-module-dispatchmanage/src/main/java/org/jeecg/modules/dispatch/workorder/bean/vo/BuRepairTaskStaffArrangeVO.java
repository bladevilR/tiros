package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
public class BuRepairTaskStaffArrangeVO implements Serializable {
    private String id;
    @ApiModelProperty(value = "工单id")
    private String orderId;
    @ApiModelProperty(value = "工位id")
    private String workstationId;
    @ApiModelProperty(value = "人员id")
    private List<String> userId;

}
