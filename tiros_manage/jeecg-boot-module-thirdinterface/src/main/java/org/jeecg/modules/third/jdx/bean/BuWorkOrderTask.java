package org.jeecg.modules.third.jdx.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderTask对象", description = "")
@TableName("bu_work_order_task")
public class BuWorkOrderTask extends Model<BuWorkOrderTask> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属工单id", required = true)
    private String orderId;

    @ApiModelProperty(value = " 任务类型 字典bu_task_type", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "根据任务类型，取对应的业务数据id，1 计划任务（取列计划任务ID） 2 故障任务（取故障记录ID） 3 整改任务（取整改记录ID） 4 作业项任务（取规程明细ID） 5 自建任务（为空）")
    private String taskObjId;

    @ApiModelProperty(value = "任务序号")
    private Integer taskNo;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务内容")
    private String taskContent;

    @ApiModelProperty(value = "计划工时，单位小时")
    private Integer workTime;

    @ApiModelProperty(value = "备注")
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

    @ApiModelProperty(value = "任务状态  字典dicCode=bu_order_task_status 0未开始1已开始2已完成(工班长提交任务，并设置工时） 3 已暂停")
    private Integer taskStatus;

    @ApiModelProperty(value = "是否委外,0 不是委外任务 1 委外送修任务  2 委外接收任务 3 其他委外任务")
    private Integer outTask;

    @ApiModelProperty(value = "来自车型设备类型表id,字典bu_train_asset_type,name,id ")
    private String assetTypeId;

    @ApiModelProperty(value = "来自车辆结构明细id,字典bu_train_structure_detail,name,id")
    private String structDetailId;

    @ApiModelProperty(value = "来自车辆设备表id,字典bu_train_asset,asset_name,id")
    private String trainAssetId;

    @ApiModelProperty(value = "是否重要工序 bu_state")
    private Integer important;

    @ApiModelProperty(value = "作业手段 字典dicCode=bu_regu_method")
    private Integer method;

    @ApiModelProperty(value = "任务安全预想")
    private String safeNotice;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
