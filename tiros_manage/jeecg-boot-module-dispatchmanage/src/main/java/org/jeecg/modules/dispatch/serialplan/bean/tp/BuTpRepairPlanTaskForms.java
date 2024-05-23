package org.jeecg.modules.dispatch.serialplan.bean.tp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;

/**
 * <p>
 * 任务与模板表单的关联关系表
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_tp_repair_plan_task_forms")
@ApiModel(value = "BuTpRepairPlanTaskForms对象", description = "任务与模板表单的关联关系表")
public class BuTpRepairPlanTaskForms extends Model<BuTpRepairPlanTaskForms> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "计划模板id")
    private String planId;

    @ApiModelProperty(value = "计划模板任务id")
    private String taskId;

    @ApiModelProperty(value = "计划模板表单id （计划模板表单记录bu_tp_repair_plan_forms的id）")
    private String planFormId;


    @ApiModelProperty(value = "表单类型  字典dictCode=bu_work_form_type")
    @Dict(dicCode = "bu_work_form_type")
    @TableField(exist = false)
    private Integer formType;

    @ApiModelProperty(value = "表单对象id")
    @TableField(exist = false)
    private String objId;

    @ApiModelProperty(value = "备注")
    @TableField(exist = false)
    private String remark;

    @ApiModelProperty(value = "表单标题")
    @TableField(exist = false)
    private String title;

    @ApiModelProperty(value = "车辆结构")
    @TableField(exist = false)
    private String trainStructId;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表")
    @TableField(exist = false)
    private Integer workRecordType;

    @ApiModelProperty(value = "编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "在线表单(数据记录表)类型 formType=1在线表单(数据记录表)时的在线表单类型")
    @Dict(dicCode = "bu_form_type")
    @TableField(exist = false)
    private Integer type;

    @ApiModelProperty(value = "关联的设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "车辆结构名称")
    @TableField(exist = false)
    private String trainStructName;

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
