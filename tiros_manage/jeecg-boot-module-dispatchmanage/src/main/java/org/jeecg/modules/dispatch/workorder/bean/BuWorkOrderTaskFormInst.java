package org.jeecg.modules.dispatch.workorder.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 表单实例和工单任务关联
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderTaskFormInst对象", description = "表单实例和工单任务关联")
@TableName("bu_work_order_task_form_inst")
public class BuWorkOrderTaskFormInst extends Model<BuWorkOrderTaskFormInst> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type 1在线表单（数据表单） 2文件表单 3作业记录表", required = true)
    @Dict(dicCode = "bu_work_form_type")
    @NotNull
    private Integer instType;

    @ApiModelProperty(value = "表单实例id")
    private String formInstId;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "工单id")
    private String workOrderId;

    @ApiModelProperty(value = "工单任务id", required = true)
    @NotBlank
    private String workOrderTaskId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "设置填写项的具体明细，多个用,分隔")
    private String recordIds;


    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表")
    @TableField(exist = false)
    private Integer workRecordType;

    @ApiModelProperty(value = "工单任务名称")
    @TableField(exist = false)
    private String taskName;

    @ApiModelProperty(value = "列计划表单id")
    @TableField(exist = false)
    private String planFormId;

    @ApiModelProperty(value = "表单来源 1表示来自列计划生成2表示来自工单添加")
    @TableField(exist = false)
    private Integer fromBy;

    @ApiModelProperty(value = "表单对象id")
    @TableField(exist = false)
    private String formObjId;

    @ApiModelProperty(value = "表单对象编码")
    @TableField(exist = false)
    private String formCode;

    @ApiModelProperty(value = "表单对象名称")
    @TableField(exist = false)
    private String formName;

    @ApiModelProperty(value = "关联的设备类型id")
    @TableField(exist = false)
    private String assetTypeId;

    @ApiModelProperty(value = "关联的设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "车辆结构id")
    @TableField(exist = false)
    private String trainStructId;

    @ApiModelProperty(value = "车辆结构名称")
    @TableField(exist = false)
    private String trainStructName;

    @ApiModelProperty(value = "车辆结构wbs编码")
    @TableField(exist = false)
    private String trainStructWbs;

    @ApiModelProperty(value = "设备id")
    @TableField(exist = false)
    private String trainAssetId;

    @ApiModelProperty(value = "设备名称")
    @TableField(exist = false)
    private String trainAssetName;

    @ApiModelProperty(value = "设备序号")
    @TableField(exist = false)
    private String assetNo;

    @ApiModelProperty(value = "填写状态 0未填写1已填写  字典dictCode=bu_form_result_status")
    @Dict(dicCode = "bu_form_result_status")
    @TableField(exist = false)
    private Integer status;

    @ApiModelProperty(value = "检查结果 0未通过1通过  字典dicCode=bu_work_order_record_result")
    @Dict(dicCode = "bu_work_order_record_result")
    @TableField(exist = false)
    private Integer checkResult;

    @ApiModelProperty(value = "检查日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date checkDate;

    @ApiModelProperty(value = "质量负责人id")
    @TableField(exist = false)
    private String checkUserId;

    @ApiModelProperty(value = "质量负责人名称")
    @TableField(exist = false)
    private String checkUserName;

    @ApiModelProperty(value = "规程名称")
    @TableField(exist = false)
    private String reguName;

    @ApiModelProperty(value = "规程版本")
    @TableField(exist = false)
    private String reguVersion;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
