package org.jeecg.modules.material.cost.bean;

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
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderMaterial对象", description = "")
@TableName("bu_work_order_material_acts")
public class BuWorkOrderMaterialActs extends Model<BuWorkOrderMaterialActs> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单物料id")
    private String orderMaterialId;

    @ApiModelProperty(value = "班组库存id")
    private String groupStockId;

    @ApiModelProperty(value = "领用单id")
    private String applyId;

    @ApiModelProperty(value = "领用明细id")
    private String applyDetailId;

    @ApiModelProperty(value = "分配明细id")
    private String assignDetailId;

    @ApiModelProperty(value = "批次号")
    private String tradeNo;

    @ApiModelProperty(value = "实际消耗数量")
    private Double actAmount;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;


    @ApiModelProperty(value = "物资类型id")
    @TableField(exist = false)
    private String materialTypeId;

    @ApiModelProperty(value = "物资类型编码")
    @TableField(exist = false)
    private String materialTypeCode;

    @ApiModelProperty(value = "物资类型名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "工单号")
    @TableField(exist = false)
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String orderName;

    @ApiModelProperty(value = "工单")
    @TableField(exist = false)
    private Integer orderStatus;

    @ApiModelProperty(value = "班组库存的车号")
    @TableField(exist = false)
    private String groupStockTrainNo;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}