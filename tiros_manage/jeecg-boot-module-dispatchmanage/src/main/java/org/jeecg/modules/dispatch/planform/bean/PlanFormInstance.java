package org.jeecg.modules.dispatch.planform.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "表单实例对象")
public class PlanFormInstance {

    @ApiModelProperty(value = "表单实例id")
    private String id;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "表单名称")
    private String title;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "计划名称")
    private String planName;

    @ApiModelProperty(value = "列计划表单id")
    private String planFormId;

    @ApiModelProperty(value = "表单id")
    private String formObjId;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type")
    @Dict(dicCode = "bu_work_form_type")
    private Integer formType;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表")
    private Integer workRecordType;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "关联的设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "关联的设备类型名称")
    private String assetTypeName;

    @ApiModelProperty(value = "车辆结构id")
    private String trainStructId;

    @ApiModelProperty(value = "车辆结构名称")
    private String trainStructName;

    @ApiModelProperty(value = "车辆结构wbs编码")
    private String trainStructWbs;

    @ApiModelProperty(value = "设备id")
    private String assetId;

    @ApiModelProperty(value = "关联的设备类型名称")
    private String assetName;

    @ApiModelProperty(value = "是否已检查")
    @Dict(dicCode = "bu_state")
    private Integer status;

    @ApiModelProperty(value = "表单序号")
    private Integer formIndex;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "填写日期")
    private Date writeTime;

    @ApiModelProperty(value = "规程名称")
    private String reguName;

    @ApiModelProperty(value = "规程版本")
    private String reguVersion;

}
