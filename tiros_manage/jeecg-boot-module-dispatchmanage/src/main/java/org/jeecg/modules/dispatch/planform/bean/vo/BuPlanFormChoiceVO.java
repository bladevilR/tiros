package org.jeecg.modules.dispatch.planform.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 列计划表单选择vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-18
 */
@Data
@Accessors(chain = true)
public class BuPlanFormChoiceVO {

    @ApiModelProperty(value = "数据记录表实例id或作业记录表实例id")
    private String id;

    @ApiModelProperty(value = "表单名称")
    private String name;

    @ApiModelProperty(value = "表单序号")
    private Integer formIndex;

    @ApiModelProperty(value = "表单类型  字典dictCode=bu_work_form_type")
    @Dict(dicCode = "bu_work_form_type")
    private Integer formType;

    @ApiModelProperty(value = "设备名称  有具体设备=具体设备名，没有具体设备=设备类型名")
    private String assetName;

    /**
     * 列计划表单id，用于后端查询并设置到表单中
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String planFormId;

}
