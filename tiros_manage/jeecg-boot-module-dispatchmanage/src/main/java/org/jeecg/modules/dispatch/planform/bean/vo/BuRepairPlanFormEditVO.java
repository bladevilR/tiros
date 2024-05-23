package org.jeecg.modules.dispatch.planform.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BuRepairPlanFormEditVO {

    @ApiModelProperty(value = "表单id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "车辆设备")
    private String trainStructId;

    @ApiModelProperty(value = "表单类型")
    private Integer formType;

}
