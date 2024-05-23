package org.jeecg.modules.basemanage.tpplan.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.aspect.annotation.DictIgnore;
import org.jeecg.common.bean.PlusProjectGantt;

import java.util.Date;

/**
 * <p>
 * 维修计划模版--VO,加入甘特图信息
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@DictIgnore
public class BuTpRepairPlanVOGantt extends PlusProjectGantt<BuTpRepairPlanTaskVOGantt> {

    @ApiModelProperty(value = "计划模板id")
    private String id;

    @ApiModelProperty(value = "模板编码")
    private String code;

    @ApiModelProperty(value = "模板名称")
    private String tpName;

    @ApiModelProperty(value = "所属线路ID")
    private String lineId;

    @ApiModelProperty(value = "所属车型id")
    private String trainTypeId;

    @ApiModelProperty(value = "修程类型id")
    private String repairProgramId;

    @ApiModelProperty(value = "编组数量")
    private Integer groupQuantity;

    @ApiModelProperty(value = "计划工期")
    private Integer duration;

    @ApiModelProperty(value = "用于作为计划模版的的任务开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date baseDate;

    @ApiModelProperty(value = "状态 字典dicCode=bu_valid_status")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;
    @ApiModelProperty(value = "状态字典文本")
    private String status_dictText;

    @ApiModelProperty(value = "是否默认 字典dicCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer defaultTp;
    @ApiModelProperty(value = "是否默认字典文本")
    private String defaultTp_dictText;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "规程id")
    private String reguId;

    @ApiModelProperty(value = "所属线路名称")
    private String lineName;

    @ApiModelProperty(value = "所属车型名称")
    private String trainTypeName;

    @ApiModelProperty(value = "所属修程名称")
    private String repairProgramName;

    @ApiModelProperty(value = "规程名称")
    private String reguName;

    @ApiModelProperty(value = "所属财务项目")
    private String fdProject;

    @ApiModelProperty(value = "所属财务任务")
    private String fdTask;

    @ApiModelProperty(value = "所属财务开支编码")
    private String fdCostType;

    @ApiModelProperty(value = "所属财务项目编码")
    private String fdProjectCode;

    @ApiModelProperty(value = "所属财务项目名称")
    private String fdProjectName;

    @ApiModelProperty(value = "所属财务任务编码")
    private String fdTaskCode;

    @ApiModelProperty(value = "所属财务任务名称")
    private String fdTaskName;

}
