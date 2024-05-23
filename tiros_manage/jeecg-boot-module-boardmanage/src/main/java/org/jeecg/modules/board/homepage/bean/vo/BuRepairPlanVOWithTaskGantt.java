package org.jeecg.modules.board.homepage.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.bean.PlusProjectGanttTask;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 看板首页 架大修计划任务全貌
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BuRepairPlanVOWithTaskGantt extends PlusProjectGanttTask {

    @ApiModelProperty(value = "列计划id")
    private String id;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "计划名称")
    private String planName;

    @ApiModelProperty(value = "车辆段id ,字典bu_mtr_depot,name,id")
    private String depotId;

    @ApiModelProperty(value = "线路id,字典bu_mtr_line,line_name,line_id")
    private String lineId;

    @ApiModelProperty(value = "修程类型id,字典bu_repair_program,name,id")
    private String repairProgramId;

    @ApiModelProperty(value = "交接车记录表中的id")
    private String exchangeId;

    @ApiModelProperty(value = "维修车辆编号")
    private String trainNo;

    @ApiModelProperty(value = "当前里程")
    private Double mileage;

    @ApiModelProperty(value = "计划模板id")

    private String planTemplateId;

    @ApiModelProperty(value = "开始日期 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "完成日期 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishDate;

    @ApiModelProperty(value = "实际开始时间 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date actStart;

    @ApiModelProperty(value = "实际完成时间 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date actFinish;

    @ApiModelProperty(value = "工期")
    private Integer duration;

    @ApiModelProperty(value = "状态  字典：bu_plan_status")
    @Dict(dicCode = "bu_plan_status")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "所属年计划明细 匹配年份和车辆自动获取年计划明细")
    private String yearDetailId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "进度状态 字典dictCode=bu_progress_status")
    @Dict(dicCode = "bu_progress_status")
    private Integer progressStatus;

    @ApiModelProperty(value = "当前进度：为1~100的数量，列计划中当前已完成任务的工期，占所有任务工期的比例，后端程序自动计算")
    private Integer progress;

    @ApiModelProperty(value = "实际工期")
    private Integer actDuration;

    @ApiModelProperty(value = "车辆序号，来自交接车记录明细中的序号")
    private Integer trainIndex;

    @ApiModelProperty(value = "规程id")
    private String reguId;


    @ApiModelProperty(value = "车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "修程类型名称")
    private String repairProgramName;

    @ApiModelProperty(value = "计划模板名称")
    private String planTemplateName;

    @ApiModelProperty(value = "规程名称")
    private String reguName;

}
