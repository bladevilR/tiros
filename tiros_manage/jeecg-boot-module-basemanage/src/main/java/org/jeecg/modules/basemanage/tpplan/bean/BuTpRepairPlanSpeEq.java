package org.jeecg.modules.basemanage.tpplan.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 特种设备需求
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTpRepairPlanSpeEq对象", description = "特种设备需求")
@TableName("bu_tp_repair_plan_spe_eq")
public class BuTpRepairPlanSpeEq extends Model<BuTpRepairPlanSpeEq> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "模板任务id")
    private String tpTaskId;

    @ApiModelProperty(value = "特种设备id")
    private String specAssetId;

    @ApiModelProperty(value = "设备名称")
    private String assetName;

    @ApiModelProperty(value = "设备编码")
    private String assetCode;

    @ApiModelProperty(value = "开始时间 时分字符串 hh:mm")
    private String startTime;

    @ApiModelProperty(value = "结束时间 时分字符串 hh:mm")
    private String endTime;

    @ApiModelProperty(value = "时长")
    private Integer timeLen;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;

    @ApiModelProperty(value = "工班id")
    @TableField(exist = false)
    private String workGroupId;

    @ApiModelProperty(value = "工班名称")
    @TableField(exist = false)
    private String workGroupName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
