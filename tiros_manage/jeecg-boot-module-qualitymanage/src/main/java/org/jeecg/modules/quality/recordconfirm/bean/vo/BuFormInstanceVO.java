package org.jeecg.modules.quality.recordconfirm.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 记录/数据表单实例vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
@Data
@Accessors(chain = true)
public class BuFormInstanceVO {

    @ApiModelProperty(value = "表单实例id")
    private String id;

    @ApiModelProperty(value = "表单编码")
    private String code;

    @ApiModelProperty(value = "表单名称")
    private String title;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type")
    @Dict(dicCode = "bu_work_form_type")
    private Integer formType;

    @ApiModelProperty(value = "列计划表单id")
    private String planFormId;

    @ApiModelProperty(value = "表单对象id")
    private String formObjId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单编码")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "工单时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orderTime;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "列计划名称")
    private String planName;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "修程id")
    private String repairProgramId;

    @ApiModelProperty(value = "修程名称")
    private String repairProgramName;

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

    @ApiModelProperty(value = "填写状态 字典dictCode=bu_form_result_status")
    @Dict(dicCode = "bu_form_result_status")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "任务与表单实例的关联关系id")
    private String task2InstId;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表")
    private Integer workRecordType;

    @ApiModelProperty(value = "规程名称")
    private String reguName;

    @ApiModelProperty(value = "规程版本")
    private String reguVersion;

}
