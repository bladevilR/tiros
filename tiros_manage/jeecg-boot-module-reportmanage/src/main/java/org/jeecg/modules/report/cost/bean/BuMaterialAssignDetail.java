package org.jeecg.modules.report.cost.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 物料分配明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuMaterialAssignDetail对象", description = "")
@TableName("bu_material_assign_detail")
public class BuMaterialAssignDetail extends Model<BuMaterialAssignDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "领用明细id")
    private String applyDetailId;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "仓库wbs 新增时前端从所选库存记录带入")
    private String locationWbs;

    @ApiModelProperty(value = "仓库名称 新增时前端从所选库存记录带入")
    private String locationName;

    @ApiModelProperty(value = "托盘id 设置托盘，设置了托盘则表示已完成备料")
    private String palletId;

    @ApiModelProperty(value = "分配数量")
    private Double amount;

    @ApiModelProperty(value = "EBS 二级库编码，保存后，后端更新存储")
    private String ebsWhCode;

    @ApiModelProperty(value = "EBS 库位编码，保存后，后端更新存储")
    private String ebsWhChildCode;

    @ApiModelProperty(value = "批次号")
    private String tradeNo;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;


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

    @ApiModelProperty(value = "工单编码")
    @TableField(exist = false)
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String orderName;

    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String materialTypeCode;

    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "规格型号")
    @TableField(exist = false)
    private String materialTypeSpec;

    @ApiModelProperty(value = "申请数量")
    @TableField(exist = false)
    private Double applyAmount;

    @ApiModelProperty(value = "发放数量")
    @TableField(exist = false)
    private Double receiveAmount;

    @ApiModelProperty(value = "库房名称（所属组织）")
    @TableField(exist = false)
    private String warehouseName;

    @ApiModelProperty(value = "托盘名称")
    @TableField(exist = false)
    private String palletName;

    @ApiModelProperty(value = "发料人员名称")
    @TableField(exist = false)
    private String sendUserName;

    @ApiModelProperty(value = "确认人员名称")
    @TableField(exist = false)
    private String confirmUserName;

    @ApiModelProperty(value = "确认时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date confirmTime;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String repairProName;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    @TableField(exist = false)
    private Integer useCategory;

    @ApiModelProperty(value = "物料消耗发送时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date syncTime;

    @ApiModelProperty(value = "物料消耗返回状态")
    @TableField(exist = false)
    private String syncResult;

    @ApiModelProperty(value = "物料消耗返回状态时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date syncResultTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
