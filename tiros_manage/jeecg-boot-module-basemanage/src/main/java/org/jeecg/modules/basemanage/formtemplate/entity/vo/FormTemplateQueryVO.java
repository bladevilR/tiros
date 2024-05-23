package org.jeecg.modules.basemanage.formtemplate.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 数据记录表模版信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FormTemplateQueryVO {

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "所属修程id")
    private String repairProId;

    @ApiModelProperty(value = "表单名称或编码")
    private String name;

    @ApiModelProperty(value = "工位id")
    private String workstationId;

    @ApiModelProperty(value = "所属分类id")
    private String category;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_form_type")
    @Dict(dicCode = "bu_form_type")
    private Integer type;

    @ApiModelProperty(value = "有效状态 字典dictCode=bu_valid_status")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;

}
