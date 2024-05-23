package org.jeecg.modules.dispatch.workorder.bean;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 日程信息， 在创建工单时，自动创建一条日程信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuBaseSchedule对象", description = "日程信息对象")
@TableName("bu_base_schedule")
public class BuBaseSchedule extends Model<BuBaseSchedule> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "所属用户id", required = true)
    @NotBlank
    private String userId;

    @ApiModelProperty(value = "日程类型 字典dictCode=bu_schedule_type", notes = "1工单任务 2会议任务 3其他任务", required = true)
    @Dict(dicCode = "bu_schedule_type")
    @NotNull
    private Integer scheduleType;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss", notes = "有的任务可能跨几天", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date startTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss", notes = "可以与开始时间一样", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date endTime;

    @ApiModelProperty(value = "是否完成 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer finished;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
