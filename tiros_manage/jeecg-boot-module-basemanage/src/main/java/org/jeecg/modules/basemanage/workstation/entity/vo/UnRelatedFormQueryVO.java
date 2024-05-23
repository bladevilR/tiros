package org.jeecg.modules.basemanage.workstation.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 未关联表单查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Data
@Accessors(chain = true)
public class UnRelatedFormQueryVO {

    @ApiModelProperty(value = "工位id", required = true)
    private String workstationId;

    @ApiModelProperty(value = "表单名称或编码")
    private String searchText;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type")
    private Integer type;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "所属修程id")
    private String repairProId;

}
