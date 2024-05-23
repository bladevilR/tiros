package org.jeecg.modules.basemanage.workstation.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author yyg
 */
@Data
@Accessors(chain = true)
public class WorkshopTree {

    @ApiModelProperty(value = "类型 1运营公司 2车辆段 3车间")
    private Integer type;

    @ApiModelProperty(value = "车间名称")
    private String name;

    @ApiModelProperty(value = "车间id")
    private String id;

    @ApiModelProperty(value = "工班列表")
    private List<WorkGroupTree> children;

}
