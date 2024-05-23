package org.jeecg.modules.produce.trainhistory.bean;

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
import org.jeecg.modules.produce.fault.bean.BuFaultHandleRecord;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆履历-故障记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTrainHistoryFault对象", description = "车辆履历-故障记录")
@TableName("bu_train_history_fault")
public class BuTrainHistoryFault extends Model<BuTrainHistoryFault> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "故障编码")
    private String faultSn;

    @ApiModelProperty(value = "故障描述")
    private String faultDesc;

    @ApiModelProperty(value = "故障分类id")
    private String categoryId;

    @ApiModelProperty(value = "故障分类名称")
    private String categoryName;

    @ApiModelProperty(value = "故障代码id")
    private String faultCodeId;

    @ApiModelProperty(value = "故障代码名称")
    private String faultCodeName;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "作业车辆id", notes = "如果是从检修系统过来，则这里存放 资产设备编码")
    private String trainId;

    @ApiModelProperty(value = "作业车辆名称")
    private String trainName;

    @ApiModelProperty(value = "所属系统id")
    private String sysId;

    @ApiModelProperty(value = "所属系统名称")
    private String sysName;

    @ApiModelProperty(value = "所属子系统id")
    private String subSysId;

    @ApiModelProperty(value = "所属子系统名称")
    private String subSysName;

    @ApiModelProperty(value = "故障部件id 具体车辆设备id")
    private String faultAssetId;

    @ApiModelProperty(value = "故障部件名称")
    private String faultAssetName;

    @ApiModelProperty(value = "故障时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date happenTime;

    @ApiModelProperty(value = "所属工单id")
    private String workOrderId;

    @ApiModelProperty(value = "所属工单名称")
    private String workOrderName;

    @ApiModelProperty(value = "所属工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "所属工单任务名称")
    private String orderTaskName;

    @ApiModelProperty(value = "所属工位 工位名称")
    private String workStationId;

    @ApiModelProperty(value = "发现阶段 字典dictCode=bu_fault_phase", notes = "1架修期 2质保 3出保期")
    @Dict(dicCode = "bu_fault_phase")
    private Integer phase;

    @ApiModelProperty(value = "故障等级 字典dictCode=bu_fault_level", notes = "1A 2B 3C 4D")
    @Dict(dicCode = "bu_fault_level")
    private Integer faultLevel;

    @ApiModelProperty(value = "是否正线   字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    @TableField("f_online")
   private Integer faultOnline;

    @ApiModelProperty(value = "是否有责   字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer hasDuty;

    @ApiModelProperty(value = "是否委外   字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer outsource;

    @ApiModelProperty(value = "提报班组名称")
    private String reportGroup;

    @ApiModelProperty(value = "提报人员名称")
    private String reportUserId;

    @ApiModelProperty(value = "提报日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date reportTime;

    @ApiModelProperty(value = "责任工程师名称")
    private String dutyEngineer;

    @ApiModelProperty(value = "故障状态 字典dictCode=bu_fault_status")
    @Dict(dicCode = "bu_fault_status")
    private Integer status;

    @ApiModelProperty(value = "处理状态 ", notes = "1=设备正常，2=已修复，2=临时修复，3=未修复")
    private Integer handleStatus;

    @ApiModelProperty(value = "处理时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    @ApiModelProperty(value = "处理工班名称")
    private String handleGroup;

    @ApiModelProperty(value = "处理人员名称 多个逗号分开")
    private String handleUser;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "所属地点 来自检修系统的故障地点")
    private String exLocation;

    @ApiModelProperty(value = "故障详细描述")
    private String exFaultDetail;

    @ApiModelProperty(value = "故障类型")
    private String exFaultType;

    @ApiModelProperty(value = "故障影响")
    private String exEffect;

    @ApiModelProperty(value = "是否典型故障")
    private String exTypical;

    @ApiModelProperty(value = "归档来源 0架大修自身数据 1检修系统同步 默认0")
    private Integer archiveType;


    /**
     * 故障处理记录，用于后端归档车辆履历故障记录时，生成handleStatus等属性
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private List<BuFaultHandleRecord> handleRecordList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
