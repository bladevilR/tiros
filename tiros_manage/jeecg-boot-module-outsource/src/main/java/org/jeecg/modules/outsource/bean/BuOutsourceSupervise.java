package org.jeecg.modules.outsource.bean;

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
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 委外监修
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuOutsourceSupervise对象", description = "委外监修")
@TableName("bu_outsource_supervise")
public class BuOutsourceSupervise extends Model<BuOutsourceSupervise> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务内容", required = true)
    @NotBlank
    private String taskContent;

    @ApiModelProperty(value = "车辆信息表id", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "设备类型表id", required = true)
    @NotBlank
    private String assetTypeId;

    @ApiModelProperty(value = "派遣班组,字典 bu_mtr_workshop_group,group_name,id", required = true)
    @NotBlank
    private String dispatchGroupId;

    @ApiModelProperty(value = "派遣人员,字典 sys_user,username,id", required = true)
    @NotBlank
    private String dispatchUserId;

    @ApiModelProperty(value = "联系方式")
    private String userPhone;

    @ApiModelProperty(value = "厂商表id,字典 bu_base_supplier,name,id", required = true)
    @NotBlank
    private String supplierId;

    @ApiModelProperty(value = "厂商电话")
    private String supplierPhone;

    @ApiModelProperty(value = "厂商地址")
    private String supplierAddress;

    @ApiModelProperty(value = "出派时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dispatchDate;

    @ApiModelProperty(value = "返回时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date returnDate;

    @ApiModelProperty(value = "出库明细", required = true)
    @NotBlank
    private String outinDetailId;

    @ApiModelProperty(value = "合同id")
    private String contractId;

    @ApiModelProperty(value = "申请时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "工单id")
    private String workOrderId;

    @ApiModelProperty(value = "工单任务id")
    private String orderTaskId;


    @ApiModelProperty(value = "线路名称", hidden = true)
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "设备名称", hidden = true)
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "派遣班组名字", hidden = true)
    @TableField(exist = false)
    private String dispatchGroupName;

    @ApiModelProperty(value = "派遣人员名字", hidden = true)
    @TableField(exist = false)
    private String dispatchUserName;

    @ApiModelProperty(value = "厂商名字", hidden = true)
    @TableField(exist = false)
    private String supplierName;

    @ApiModelProperty(value = "合同编码", hidden = true)
    @TableField(exist = false)
    private String contractNo;

    @ApiModelProperty(value = "状态")
    @TableField(exist = false)
    @Dict(dicCode = "bu_supervise_status")
    private String status;

    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String workOrderName;

    @ApiModelProperty(value = "工单任务名称")
    @TableField(exist = false)
    private String orderTaskName;

    @ApiModelProperty(value = "审批流程状态")
    @TableField(exist = false)
    private String wfStatus;

    @ApiModelProperty(value = "流程当前处理人")
    @TableField(exist = false)
    private String processCandidate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
