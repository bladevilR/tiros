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
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆履历-测量记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTrainHistoryMeasure对象", description = "车辆履历-测量记录")
@TableName("bu_train_history_measure")
public class BuTrainHistoryMeasure extends Model<BuTrainHistoryMeasure> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

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

    @ApiModelProperty(value = "测量项id")
    private String measureId;

    @ApiModelProperty(value = "测量项名称")
    private String measureName;

    @ApiModelProperty(value = "作业班组名称")
    private String workGroup;

    @ApiModelProperty(value = "作业人员名称 人名，多个逗号分隔")
    private String workUser;

    @ApiModelProperty(value = "作业时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workTime;

    @ApiModelProperty(value = "所属工单id")
    private String workOrderId;

    @ApiModelProperty(value = "所属工单名称")
    private String workOrderName;

    @ApiModelProperty(value = "测量值")
    private String mValues;

    @ApiModelProperty(value = "是否预警 字典dictCode=bu_state", notes = "0否1是")
    @Dict(dicCode = "bu_state")
    private Integer isAlert;

    @ApiModelProperty(value = "预警信息")
    private String alertMessage;

    @ApiModelProperty(value = "预警阈值 为预警阈值设置的最后表达式")
    private String thresholds;

    @ApiModelProperty(value = "修正值")
    private String fixValues;

    @ApiModelProperty(value = "预警原因")
    private String alertReason;

    @ApiModelProperty(value = "测量器具 用到的工器具编码")
    private String tools;

    @ApiModelProperty(value = "归档来源 0架大修自身数据 1检修系统同步 默认0")
    private Integer archiveType;

    @ApiModelProperty(value = "备注")
    private String remark;


    @ApiModelProperty(value = "设备类型id")
    @TableField(exist = false)
    private String assetTypeId;

    /**
     * 作业人员名称集合，用于后端归档车辆履历测量记录时，生成workUser属性
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private List<String> workUserIdList;

    /**
     * 工器具id集合，用于后端归档车辆履历测量记录时，生成tools属性
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private List<String> toolIdList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
