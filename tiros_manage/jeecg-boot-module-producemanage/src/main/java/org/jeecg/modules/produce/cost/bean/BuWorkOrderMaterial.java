package org.jeecg.modules.produce.cost.bean;

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
import java.math.BigDecimal;
import java.util.Date;

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
    private Integer  needDispatchin;

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

    @ApiModelProperty(value = "物资类型中价格")
    @TableField(exist = false)
    private BigDecimal materialTypePrice;

    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "工位号")
    @TableField(exist = false)
    private String workstationNo;

    @ApiModelProperty(value = "工位名称")
    @TableField(exist = false)
    private String workstationName;

    @ApiModelProperty(value = "发放数量")
    @TableField(exist = false)
    private BigDecimal receive;

    @ApiModelProperty(value = "退回数量")
    @TableField(exist = false)
    private Double returnAmount;

    @ApiModelProperty(value = "用于统计数据的消耗数量")
    @TableField(exist = false)
    private BigDecimal consumeAmount;

    @ApiModelProperty(value = "单价")
    @TableField(exist = false)
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "用于统计数据的消耗总金额")
    @TableField(exist = false)
    private BigDecimal consumeTotalPrice;

    @ApiModelProperty(value = "消耗时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date consumeTime;

    @ApiModelProperty(value = "线路id")
    @TableField(exist = false)
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "车辆段id")
    @TableField(exist = false)
    private String depotId;

    @ApiModelProperty(value = "车辆段名称")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "车号")
    @TableField(exist = false)
    private String trainNo;

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

    @ApiModelProperty(value = "车间id")
    @TableField(exist = false)
    @JsonIgnore
    private String workshopId;

    @ApiModelProperty(value = "公司id")
    @TableField(exist = false)
    @JsonIgnore
    private String companyId;

    @ApiModelProperty(value = "车辆id")
    @TableField(exist = false)
    @JsonIgnore
    private String trainId;

    @ApiModelProperty(value = "架修周期id")
    @TableField(exist = false)
    @JsonIgnore
    private String periodId;

    @ApiModelProperty(value = "架修序号")
    @TableField(exist = false)
    @JsonIgnore
    private Integer repairIndex;

    @ApiModelProperty(value = "修程id")
    @TableField(exist = false)
    @JsonIgnore
    private String programId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
