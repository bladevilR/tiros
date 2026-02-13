package org.jeecg.modules.quality.exceptiontransfer.bean;

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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "例外转序对象", description = "工序异常例外转序记录")
@TableName("bu_work_exception_transfer")
public class BuWorkExceptionTransfer extends Model<BuWorkExceptionTransfer> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "转序编号")
    @NotBlank
    private String transferCode;

    @ApiModelProperty(value = "关联故障ID")
    @NotBlank
    private String faultId;

    @ApiModelProperty(value = "关联工单ID")
    @NotBlank
    private String orderId;

    @ApiModelProperty(value = "关联工单任务ID")
    private String orderTaskId;

    @ApiModelProperty(value = "关联列计划ID")
    private String planId;

    @ApiModelProperty(value = "线路ID")
    private String lineId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "系统ID")
    private String sysId;

    @ApiModelProperty(value = "部件ID")
    private String faultAssetId;

    @ApiModelProperty(value = "提报人ID")
    private String reportUserId;

    @ApiModelProperty(value = "故障发生时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date happenTime;

    @ApiModelProperty(value = "故障描述")
    private String faultDesc;

    @ApiModelProperty(value = "工序名称")
    private String processName;

    @ApiModelProperty(value = "工步名称")
    private String stepName;

    @ApiModelProperty(value = "受影响的后续工序")
    private String nextProcess;

    @ApiModelProperty(value = "例外转序说明")
    private String transferDesc;

    @ApiModelProperty(value = "状态 0待处理 1已放行 2已驳回")
    private Integer status;

    @ApiModelProperty(value = "完成方式 1审批流 2一键放行")
    private Integer decisionType;

    @ApiModelProperty(value = "决策人ID")
    private String decisionUserId;

    @ApiModelProperty(value = "决策时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date decisionTime;

    @ApiModelProperty(value = "决策备注")
    private String decisionRemark;

    @ApiModelProperty(value = "提报时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    @ApiModelProperty(value = "提报人ID")
    private String submitUserId;

    @ApiModelProperty(value = "公司ID")
    private String companyId;

    @ApiModelProperty(value = "车间ID")
    private String workshopId;

    @ApiModelProperty(value = "删除标记 0否1是")
    private Integer delFlag;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "工单号")
    @TableField(exist = false)
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String orderName;

    @ApiModelProperty(value = "工单任务名称")
    @TableField(exist = false)
    private String taskName;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private String sysName;

    @ApiModelProperty(value = "部件名称")
    @TableField(exist = false)
    private String faultAssetName;

    @ApiModelProperty(value = "提报人名称")
    @TableField(exist = false)
    private String submitUserName;

    @ApiModelProperty(value = "决策人名称")
    @TableField(exist = false)
    private String decisionUserName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}

