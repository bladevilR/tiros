package org.jeecg.modules.material.apply.bean;

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
 * 领用明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "领用明细对象", description = "领用明细")
@TableName("bu_material_apply_detail")
public class BuMaterialApplyDetail extends Model<BuMaterialApplyDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属申请单id")
    private String applyId;

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "申请数量", required = true)
    @NotNull
    private BigDecimal amount;

    @ApiModelProperty(value = "发放数量")
    private BigDecimal receive;

    @ApiModelProperty("入库明细id 表示备料是哪一批物资，通过选择的物资库存记录可以获取")
    private String entryDetailId;

    @ApiModelProperty(value = "领用明细状态 字典dictCode=bu_material_apply_detail_status")
    @Dict(dicCode = "bu_material_apply_detail_status")
    private Integer status;

    @ApiModelProperty(value = "确认人员id")
    private String confirmUser;

    @ApiModelProperty(value = "确认时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "发料人员id 物料人员")
    private String sendUser;

    @ApiModelProperty(value = "发料时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer useCategory;

    @ApiModelProperty(value = "工单物料id")
    private String orderMaterialId;


    @ApiModelProperty(value = "领料单类型 字典dictCode=bu_material_apply_type")
    @Dict(dicCode = "bu_material_apply_type")
    @TableField(exist = false)
    private Integer applyType;

    @ApiModelProperty(value = "领用时间(来自领用单) yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date receiveTime;

    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String materialTypeCode;

    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "规格型号")
    @TableField(exist = false)
    private String materialTypeSpec;

    @ApiModelProperty(value = "单位")
    @TableField(exist = false)
    private String materialTypeUnit;

    @ApiModelProperty(value = "确认人员名称")
    @TableField(exist = false)
    private String confirmUserName;

    @ApiModelProperty(value = "发料人员名称")
    @TableField(exist = false)
    private String sendUserName;

    @ApiModelProperty(value = "来源库位 多个时用逗号分隔")
    @TableField(exist = false)
    private String sourceLocationName;

    @ApiModelProperty(value = "存放托盘名称 多个时用逗号分隔")
    @TableField(exist = false)
    private String palletName;

    @ApiModelProperty(value = "来源库位+数量+托盘（|分割） 多个时用逗号分隔")
    @TableField(exist = false)
    private String sourceLocationAndPalletName;

    @ApiModelProperty(value = "分配明细列表")
    @TableField(exist = false)
    private List<BuMaterialAssignDetail> assignDetailList;

    @ApiModelProperty(value = "线路id")
    @TableField(exist = false)
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "车号")
    @TableField(exist = false)
    private String trainNo;

    @ApiModelProperty(value = "工单id")
    @TableField(exist = false)
    private String orderId;

    @ApiModelProperty(value = "工单类型")
    @TableField(exist = false)
    @Dict(dicCode = "bu_order_type")
    private Integer orderType;

    @ApiModelProperty(value = "工单编码")
    @TableField(exist = false)
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String orderName;

    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "作业班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "列计划id")
    @TableField(exist = false)
    private String planId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
