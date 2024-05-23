package org.jeecg.modules.board.progress.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.PlusProjectGanttTask;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 委外工单，带甘特图信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "委外工单甘特图", description = "委外工单，带甘特图信息")
public class BuWorkOutsourceOrderVO extends PlusProjectGanttTask {

    @ApiModelProperty(value = "工单id")
    private String id;

    @ApiModelProperty(value = "工单编码")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "进度")
    private Integer progress;

    @ApiModelProperty(value = "工单工期")
    private Integer duration;

    @ApiModelProperty(value = "计划开始时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "计划完成时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    @ApiModelProperty(value = "实际开始时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actStart;

    @ApiModelProperty(value = "实际完成时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actFinish;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "所有任务数")
    @JsonIgnore
    private Integer taskCount;

    @ApiModelProperty(value = "已完成任务数")
    @JsonIgnore
    private Integer finishedTaskCount;

}
