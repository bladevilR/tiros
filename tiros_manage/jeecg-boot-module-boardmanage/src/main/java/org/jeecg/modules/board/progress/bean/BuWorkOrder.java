package org.jeecg.modules.board.progress.bean;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工单
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrder对象", description = "")
@TableName("bu_work_order")
public class BuWorkOrder extends Model<BuWorkOrder> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单编号")
    private String orderCode;

    @ApiModelProperty(value = "工单名称", required = true)
    @NotBlank
    private String orderName;

    @ApiModelProperty(value = "工单类型 字典dicCode=bu_order_type", required = true)
    @Dict(dicCode = "bu_order_type")
    @NotNull
    private Integer orderType;

    @ApiModelProperty(value = "是否自动生成 字典dicCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer generate;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "计划开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "计划结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishTime;

    @ApiModelProperty(value = "工单工期")
    private Integer duration;

    @ApiModelProperty(value = "作业班组id")
    private String groupId;

    @ApiModelProperty(value = "班长id")
    private String monitor;

    @ApiModelProperty(value = "所属车间id")
    private String workshopId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "作业车号")
    private String trainNo;

    @ApiModelProperty(value = "实际开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actStart;

    @ApiModelProperty(value = "实际结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actFinish;

    @ApiModelProperty(value = "工单状态 字典dicCode=bu_order_status")
    @Dict(dicCode = "bu_order_status")
    private Integer orderStatus;

    @ApiModelProperty(value = "作业状态 字典dicCode=bu_work_status")
    @Dict(dicCode = "bu_work_status")
    private Integer workStatus;

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

    @ApiModelProperty(value = "下发时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date issuingTime;

    @ApiModelProperty(value = "所属财务项目")
    private String fdProject;

    @ApiModelProperty(value = "所属财务任务")
    private String fdTask;

    @ApiModelProperty(value = "所属财务开支编码")
    private String fdCostType;

    @ApiModelProperty(value = "工单创建来源 1自动生成 2调度创建 3班组创建")
    private Integer fromType;

    @ApiModelProperty(value = "maximo工单id 工单同步后由maximo设置，用于设备更换跳转")
    private Long maximoWorkOrderId;

    @ApiModelProperty(value = "公司id")
    private String companyId;


    @ApiModelProperty(value = "作业班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "所属财务项目编码")
    @TableField(exist = false)
    private String fdProjectCode;

    @ApiModelProperty(value = "所属财务项目名称")
    @TableField(exist = false)
    private String fdProjectName;

    @ApiModelProperty(value = "所属财务任务编码")
    @TableField(exist = false)
    private String fdTaskCode;

    @ApiModelProperty(value = "所属财务任务名称")
    @TableField(exist = false)
    private String fdTaskName;

    @ApiModelProperty(value = "所有任务数")
    @TableField(exist = false)
    private Integer taskCount;

    @ApiModelProperty(value = "已完成任务数")
    @TableField(exist = false)
    private Integer finishedTaskCount;

    @ApiModelProperty(value = "未完成任务数")
    @TableField(exist = false)
    private Integer unfinishedTaskCount;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
