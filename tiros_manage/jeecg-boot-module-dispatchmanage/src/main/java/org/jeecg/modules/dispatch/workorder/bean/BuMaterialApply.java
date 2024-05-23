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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物料申请(领用)
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "物料申请(领用)对象", description = "物料申请(领用)")
@TableName("bu_material_apply")
public class BuMaterialApply extends Model<BuMaterialApply> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编码 自动生成")
    private String code;

    @ApiModelProperty(value = "标题 默认自动生成：班组+申请日期，用户可修改", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "类型 1自动生成 2故障申领 3缺料申请  字典dictCode=bu_material_apply_type")
    @Dict(dicCode = "bu_material_apply_type")
    private Integer applyType;

    @ApiModelProperty(value = "关联工单id")
    private String workOrderId;

    @ApiModelProperty(value = "关联车辆编号")
    private String trainNo;

    @ApiModelProperty(value = "申请部门id   字典dictCode=(sys_depart,depart_name,id)", required = true)
    @NotBlank
    private String deptId;

    @ApiModelProperty(value = "申请人员id   接口获取/tiros/sys/user/queryUserByDepId", required = true)
    @NotBlank
    private String userId;

    @ApiModelProperty(value = "申请日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date applyDate;

    @ApiModelProperty(value = "备料状态 0未备料  1已备料  字典dictCode=bu_material_apply_ready_status")
    @Dict(dicCode = "bu_material_apply_ready_status")
    private Integer readyStatus;

    @ApiModelProperty(value = "领用状态 0未领用  1领用中(未领完)  2已领用   字典dictCode=bu_material_apply_status")
    @Dict(dicCode = "bu_material_apply_status")
    private Integer status;

    @ApiModelProperty(value = "领用时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date receiveTime;

    @ApiModelProperty(value = "领用班组id   字典dictCode=(bu_mtr_workshop_group,group_name,id)")
    private String receiveGroupId;

    @ApiModelProperty(value = "领用人员id   接口获取/tiros/sys/user/listByGroupId  app端接口/tiros/app/user/listByGroupId")
    private String receiveUserId;

    @ApiModelProperty(value = "所属车辆段id", required = true)
    @NotBlank
    private String depotId;

    @ApiModelProperty(value = "所属线路id   字典dictCode=(bu_mtr_line,line_name,line_id)", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "所属车间id   接口获取/tiros/workshop/listByLineId", required = true)
    @NotBlank
    private String workshopId;

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

    @ApiModelProperty(value = "物料消耗发送时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @ApiModelProperty(value = "物料消耗返回状态")
    private String syncResult;

    @ApiModelProperty(value = "物料消耗返回状态时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncResultTime;

    @ApiModelProperty(value = "公司id")
    private String companyId;


    @ApiModelProperty(value = "领用明细对象列表")
    @TableField(exist = false)
    private List<BuMaterialApplyDetail> detailList;

    @ApiModelProperty(value = "关联工单名称")
    @TableField(exist = false)
    private String workOrderName;

    @ApiModelProperty(value = "申请部门名称")
    @TableField(exist = false)
    private String deptName;

    @ApiModelProperty(value = "申请人员名称")
    @TableField(exist = false)
    private String userName;

    @ApiModelProperty(value = "领用班组名称")
    @TableField(exist = false)
    private String receiveGroupName;

    @ApiModelProperty(value = "领用人员名称")
    @TableField(exist = false)
    private String receiveUserName;

    @ApiModelProperty(value = "物料数量 根据明细统计得来")
    @TableField(exist = false)
    private BigDecimal materialQuantity;

    @ApiModelProperty(value = "发料人 来自明细的确认人员")
    @TableField(exist = false)
    private String confirmUserName;

    @ApiModelProperty(value = "发料日期 来自明细的确认时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date confirmTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
