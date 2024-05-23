package org.jeecg.modules.produce.plan.bean.vo;

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

    @ApiModelProperty(value = "记录表id或作业记录id")
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

}
