package org.jeecg.modules.material.apply.bean;

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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工单任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderTask对象", description = "工单任务对象")
@TableName("bu_work_order_task")
public class BuWorkOrderTask extends Model<BuWorkOrderTask> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属工单id")
    private String orderId;

    @ApiModelProperty(value = "任务类型 1计划任务 2故障任务 3整改任务 4作业项任务 5自建任务  字典dictCode=bu_task_type")
    @Dict(dicCode = "bu_task_type")
    private Integer taskType;

    @ApiModelProperty(value = "任务对象id 根据任务类型取对应的业务数据id:1计划任务（取列计划任务ID） 2故障任务（取故障记录ID） 3 整改任务（取整改记录ID） 4作业项任务（取规程明细ID）  5自建任务（为空）")
    private String taskObjId;

    @ApiModelProperty(value = "任务序号")
    private Integer taskNo;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务内容")
    private String taskContent;

    @ApiModelProperty(value = "计划工时 单位小时")
    private Integer workTime;

    @ApiModelProperty(value = "任务备注")
    private String remark;

    @ApiModelProperty(value = "任务开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date taskStart;

    @ApiModelProperty(value = "任务完成时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date taskFinish;

    @ApiModelProperty(value = "任务的实际时间,单位小时  任务完成时间-任务开始时间算出的时间")
    private Integer taskTime;

    @ApiModelProperty(value = "工班长填报的工时")
    private Integer reportTime;

    @ApiModelProperty(value = "任务状态 字典dicCode=bu_order_task_status")
    @Dict(dicCode = "bu_order_task_status")
    private Integer taskStatus;

    @ApiModelProperty(value = "是否委外任务 0不是委外任务 1委外送修任务 2委外接收任务 3其他委外任务")
    private Integer outTask;

    @ApiModelProperty(value = "设备类型id 来自车型设备类型表id")
    private String assetTypeId;

    @ApiModelProperty(value = "车辆结构明细id 来自车辆结构明细id")
    private String structDetailId;

    @ApiModelProperty(value = "设备id 来自车辆设备表id")
    private String trainAssetId;

    @ApiModelProperty(value = "是否重要工序 bu_state")
    @Dict(dicCode = "bu_state")
    private Integer important;

    @ApiModelProperty(value = "作业手段 字典dicCode=bu_regu_method")
    @Dict(dicCode = "bu_regu_method")
    private Integer method;

    @ApiModelProperty(value = "任务安全预想")
    private String safeNotice;


    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "工单类型 1计划工单 2故障工单 3临时工单  字典dicCode=bu_order_type")
    @Dict(dicCode = "bu_order_type")
    @TableField(exist = false)
    private Integer orderType;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
