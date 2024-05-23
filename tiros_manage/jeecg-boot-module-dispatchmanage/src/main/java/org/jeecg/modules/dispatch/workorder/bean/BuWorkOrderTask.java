package org.jeecg.modules.dispatch.workorder.bean;

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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 工单任务
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderTask对象", description = "工单任务")
@TableName("bu_work_order_task")
public class BuWorkOrderTask extends Model<BuWorkOrderTask> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属工单id", required = true)
    private String orderId;

    @ApiModelProperty(value = " 任务类型 字典bu_task_type", required = true)
    @Dict(dicCode = "bu_task_type")
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

    @ApiModelProperty(value = "任务状态 字典dicCode=bu_order_task_status")
    @Dict(dicCode = "bu_order_task_status")
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
    @Dict(dicCode = "bu_state")
    private Integer important;

    @ApiModelProperty(value = "作业手段 字典dicCode=bu_regu_method")
    @Dict(dicCode = "bu_regu_method")
    private Integer method;

    @ApiModelProperty(value = "任务安全预想")
    private String safeNotice;


    @ApiModelProperty(value = "工单编码")
    @TableField(exist = false)
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String orderName;

    @ApiModelProperty(value = "车辆号")
    @TableField(exist = false)
    private String trainNo;

    @ApiModelProperty(value = "进度")
    @TableField(exist = false)
    private String progress;

    @ApiModelProperty(value = "作业人数")
    @TableField(exist = false)
    private Integer sumPerson;

    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "车辆设备编码")
    @TableField(exist = false)
    private String trainAssetCode;

    @ApiModelProperty(value = "车辆设备名称")
    @TableField(exist = false)
    private String trainAssetName;

    @ApiModelProperty(value = "作业工位")
    @TableField(exist = false)
    @JsonIgnore
    List<BuWorkOrderTaskWorkstation> workstations;

    @ApiModelProperty(value = "作业物资")
    @TableField(exist = false)
    @JsonIgnore
    List<BuWorkOrderMaterial> materials;

    @ApiModelProperty(value = "作业工具")
    @TableField(exist = false)
    @JsonIgnore
    List<BuWorkOrderTool> tools;

    @ApiModelProperty(value = "作业工艺文件")
    @TableField(exist = false)
    @JsonIgnore
    List<BuWorkOrderTechFile> techFiles;

    @ApiModelProperty(value = "作业指导书明细")
    @TableField(exist = false)
    @JsonIgnore
    private List<BuWorkOrderBookStep> bookSteps;

    @ApiModelProperty(value = "是否有作业指导书")
    @TableField(exist = false)
    private Boolean isBookSteps;

    @ApiModelProperty(value = "目标设备")
    @TableField(exist = false)
    private List<BuWorkOrderTaskEqu> equipments;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
