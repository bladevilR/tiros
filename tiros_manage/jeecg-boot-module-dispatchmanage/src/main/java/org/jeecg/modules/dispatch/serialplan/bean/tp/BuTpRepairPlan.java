package org.jeecg.modules.dispatch.serialplan.bean.tp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.List;

/**
 * <p>
 * 维修计划模版
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTpRepairPlan对象", description = "维修计划模版")
@TableName("bu_tp_repair_plan")
public class BuTpRepairPlan extends Model<BuTpRepairPlan> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编码", required = true)
    @NotBlank
    private String code;

    @ApiModelProperty(value = "模板名称", required = true)
    @NotBlank
    private String tpName;

    @ApiModelProperty(value = "所属线路ID", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "所属车型id")
    private String trainTypeId;

    @ApiModelProperty(value = "修程类型id", required = true)
    @NotBlank
    private String repairProgramId;

    @ApiModelProperty(value = "编组数量", required = true)
    @NotNull
    private Integer groupQuantity;

    @ApiModelProperty(value = "计划工期", required = true)
    @NotNull
    private Integer duration;

    @ApiModelProperty(value = "用于作为计划模版的的任务开始日期 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date baseDate;

    @ApiModelProperty(value = "0 无效  1 有效", required = true)
    @Dict(dicCode = "bu_valid_status")
    @NotNull
    private Integer status;

    @ApiModelProperty(value = "0 否  1 是")
    @Dict(dicCode = "bu_state")
    private Integer defaultTp;

    @ApiModelProperty(value = "备注", required = true)
    @NotBlank
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;

    @ApiModelProperty(value = "更新时间 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "规程id")
    private String reguId;

    @ApiModelProperty(value = "所属财务项目")
    private String fdProject;

    @ApiModelProperty(value = "所属财务任务")
    private String fdTask;

    @ApiModelProperty(value = "所属财务开支编码")
    private String fdCostType;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属车型名称")
    @TableField(exist = false)
    private String trainTypeName;

    @ApiModelProperty(value = "所属修程名称")
    @TableField(exist = false)
    private String repairProgramName;

    @ApiModelProperty(value = "任务集合")
    @TableField(exist = false)
    private List<BuTpRepairPlanTask> tasks;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
