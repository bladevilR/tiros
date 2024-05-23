package org.jeecg.modules.basemanage.regu.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yyg
 */
@Data
@Accessors(chain = true)
public class RepairReguDetailQueryVO {

    @ApiModelProperty(value = "规程id")
    private String reguId;

    @ApiModelProperty(value = "上级id")
    private String parentId;

    @ApiModelProperty(value = "规程名称")
    private String title;

    @ApiModelProperty(value = "规程类型")
    private Integer type;

    @ApiModelProperty(value = "计划模板id")
    private String tpPlanId;

    @ApiModelProperty(value = "计划模板是否已关联")
    private Integer tpPlanRelated;

}
