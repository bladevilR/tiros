package org.jeecg.modules.basemanage.workstation.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工班
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/9
 */
@Data
@Accessors(chain = true)
public class WorkGroupTree {

    @ApiModelProperty(value = "类型 1运营公司 2车辆段 3车间")
    private Integer type;

    @ApiModelProperty(value = "工班名称")
    private String name;

    @ApiModelProperty(value = "工班id")
    private String id;

}
