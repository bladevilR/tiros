package org.jeecg.modules.produce.plan.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 任务人员安排
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_repair_task_staff_arrange")
@ApiModel(value = "BuRepairTaskStaffArrange对象", description = "任务人员安排")
public class BuRepairTaskStaffArrange extends Model<BuRepairTaskStaffArrange> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单作业工位id")
    private String ordTskWkStationId;

    @ApiModelProperty(value = "人员id 作业填报新增时必填")
    private String userId;

    @ApiModelProperty(value = "作业工时")
    private BigDecimal workTime;


    @ApiModelProperty(value = "列计划id")
    @TableField(exist = false)
    private String planId;

    @ApiModelProperty(value = "序号")
    @TableField(exist = false)
    private Integer repairIndex;

    @ApiModelProperty(value = "车号")
    @TableField(exist = false)
    private String trainNo;

    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "工位id")
    @TableField(exist = false)
    private String workstationId;

    @ApiModelProperty(value = "工位号")
    @TableField(exist = false)
    private String workstationNo;

    @ApiModelProperty(value = "工位名称")
    @TableField(exist = false)
    private String workstationName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
