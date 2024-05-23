package org.jeecg.modules.material.cost.bean;

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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 工单物资
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "工单物资对象", description = "工单物资")
@TableName("bu_work_order_material")
public class BuWorkOrderMaterial extends Model<BuWorkOrderMaterial> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "数量")
    private Double amount;

    @ApiModelProperty(value = "实际消耗")
    private Double actAmount;

    @ApiModelProperty(value = "计划数量")
    private Double planAmount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否需要发料 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer needDispatchin;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer useCategory;

    @ApiModelProperty(value = "是否核实  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isVerify;

    @ApiModelProperty(value = "工单物料操作类型 字典dictCode=bu_order_material_op_type")
    @Dict(dicCode = "bu_order_material_op_type")
    private Integer opType;

    @ApiModelProperty(value = "是否已生成单据 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isGenOrder;

    @ApiModelProperty(value = "系统id")
    private String systemId;

    @ApiModelProperty(value = "工位id")
    private String workstationId;


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

    @ApiModelProperty(value = "物资属性")
    @TableField(exist = false)
    private String materialTypeCategory3;

    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "工位号")
    @TableField(exist = false)
    private String workstationNo;

    @ApiModelProperty(value = "工位名称")
    @TableField(exist = false)
    private String workstationName;

    @ApiModelProperty(value = "线路id")
    @TableField(exist = false)
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "车号")
    @TableField(exist = false)
    private String trainNo;

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

    @ApiModelProperty(value = "工单状态 字典dicCode=bu_order_status")
    @Dict(dicCode = "bu_order_status")
    @TableField(exist = false)
    private Integer orderStatus;

    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "作业班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "列计划id")
    @TableField(exist = false)
    private String planId;

    @ApiModelProperty(value = "列计划名称")
    @TableField(exist = false)
    private String planName;

    @ApiModelProperty(value = "工单日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date orderTime;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String repairProName;

    @ApiModelProperty(value = "实际消耗列表")
    @TableField(exist = false)
    private List<BuWorkOrderMaterialActs> actList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
