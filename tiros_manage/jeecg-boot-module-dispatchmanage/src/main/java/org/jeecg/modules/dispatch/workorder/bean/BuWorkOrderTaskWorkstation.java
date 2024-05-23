package org.jeecg.modules.dispatch.workorder.bean;

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
import java.util.List;

/**
 * <p>
 * 工单工位
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderTaskWorkstation对象", description = "")
@TableName("bu_work_order_task_workstation")
public class BuWorkOrderTaskWorkstation extends Model<BuWorkOrderTaskWorkstation> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "作业任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "工位id")
    private String workstationId;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "计划工时")
    private Integer workTime;


    @ApiModelProperty(value = "工位号")
    @TableField(exist = false)
    private String workstationNo;

    @ApiModelProperty(value = "工位名称")
    @TableField(exist = false)
    private String workstationName;

    @ApiModelProperty(value = "人员安排")
    @TableField(exist = false)
    private List<BuRepairTaskStaffArrange> staffArrangeList;

    @ApiModelProperty(value = "分配人员名称 人员安排中人员名称，多个逗号分割")
    @TableField(exist = false)
    private String personNames;

    @ApiModelProperty(value = "分配人员id列表 人员安排中人员id列表，传入userId数组，后端会根据此列表生产人员安排记录")
    @TableField(exist = false)
    private List<String> staffUserIds;

    @ApiModelProperty(value = "工位关联表单id列表", hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private List<String> refFormIdList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
