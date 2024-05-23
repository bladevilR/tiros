package org.jeecg.modules.third.jdx.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车辆履历-作业记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTrainHistoryWork对象", description = "车辆履历-作业记录")
@TableName("bu_train_history_work")
public class BuTrainHistoryWork extends Model<BuTrainHistoryWork> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单编码")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "工单类型  字典dicCode=bu_order_type", notes = "1计划工单 2故障工单 3临时工单 也存放来自检修系统的---大类")
//    @Dict(dicCode = "bu_order_type")
    private String orderType;

    @ApiModelProperty(value = " 自动生成  字典dicCode=bu_state")
//    @Dict(dicCode = "bu_state")
    private Integer generate;

    @ApiModelProperty(value = "所属修程 来自检修系统的文本类型", notes = "车辆架修、车辆大修、预防维修......等")
    private String programType;

    @ApiModelProperty(value = "列计划id 根据列计划自动生成的工单填写")
    private String planId;

    @ApiModelProperty(value = "列计划名称")
    @TableField("plan_Name")
    private String planName;

    @ApiModelProperty(value = "计划开始时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "计划完成时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    @ApiModelProperty(value = "工单工期")
    private String duration;

    @ApiModelProperty(value = "作业工班id")
    private String groupId;

    @ApiModelProperty(value = "作业工班名称")
    private String groupName;

    @ApiModelProperty(value = "班长id")
    private String monitor;

    @ApiModelProperty(value = "班长名称")
    private String monitorName;

    @ApiModelProperty(value = "车辆段id", notes = "如果从检修系统来，则直接根据系统中的配置写入")
    private String depotId;

    @ApiModelProperty(value = "车辆段名称", notes = "如果从检修系统来，则直接根据系统中的配置写入")
    private String depotName;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "车间名称")
    private String workshopName;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "作业车辆id", notes = "如果是从检修系统过来，则这里存放 资产设备编码")
    private String trainId;

    @ApiModelProperty(value = "作业车辆名称")
    private String trainName;

    @ApiModelProperty(value = "作业设备id 具体车辆设备id")
    private String trainAssetId;

    @ApiModelProperty(value = "作业设备名称")
    private String trainAssetName;

    @ApiModelProperty(value = "所属系统id")
    private String sysId;

    @ApiModelProperty(value = "所属系统名称")
    private String sysName;

    @ApiModelProperty(value = "所属子系统id")
    private String subSysId;

    @ApiModelProperty(value = "所属子系统名称")
    private String subSysName;

    @ApiModelProperty(value = "实际开始时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actStart;

    @ApiModelProperty(value = "实际完成时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actFinish;

    @ApiModelProperty(value = "工单状态 字典dictCode=bu_order_status")
//    @Dict(dicCode = "bu_order_status")
    private Integer orderStatus;

    @ApiModelProperty(value = "作业状态 字典dictCode=bu_work_status")
//    @Dict(dicCode = "bu_work_status")
    private Integer workStatus;

    @ApiModelProperty(value = "工单创建时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "工单下发时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date issuingTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "所属财务项目")
    private String fdProject;

    @ApiModelProperty(value = "所属财务任务")
    private String fdTask;

    @ApiModelProperty(value = "所属财务开支类型")
    private String fdCostType;

    @ApiModelProperty(value = "位置编码")
    private String exLocation;

    @ApiModelProperty(value = "检修周期 主要存放检修系统过来的： 双日检、周检、三月检等..")
    private String exPeriod;

    @ApiModelProperty(value = "检修等级")
    private String exCheckLevel;

    @ApiModelProperty(value = "归档来源 0架大修自身数据 1检修系统同步 默认0")
    private Integer archiveType;


    @ApiModelProperty(value = "设备类型id")
    @TableField(exist = false)
    private String assetTypeId;

    /**
     * 是否需要添加，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private Boolean needAdd;

    /**
     * 是否需要更新，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private Boolean needUpdate;

    /**
     * 是否需要删除，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private Boolean needDelete;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
