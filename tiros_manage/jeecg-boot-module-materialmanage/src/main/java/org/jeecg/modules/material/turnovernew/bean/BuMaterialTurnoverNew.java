package org.jeecg.modules.material.turnovernew.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 周转件
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_material_turnover")
@ApiModel(value = "周转件对象", description = "周转件")
public class BuMaterialTurnoverNew extends Model<BuMaterialTurnoverNew> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "税率，如1.3")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "含税价格")
    private BigDecimal taxPrice;

    @ApiModelProperty(value = "消耗量")
    private BigDecimal useAmount;

    @ApiModelProperty(value = "总额")
    private BigDecimal useAmountPrice;

    @ApiModelProperty(value = "工单号")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String orderCode;

    @ApiModelProperty(value = "出库单号")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String outOrderCode;

    @ApiModelProperty(value = "子库编码")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String warehouseCode;

    @ApiModelProperty(value = "子库id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String warehouseId;

    @ApiModelProperty(value = "消耗时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date useTime;

    @ApiModelProperty(value = "所属系统id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String systemId;

    @ApiModelProperty(value = "首次投入使用列计划id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String firstUsePlanId;

    @ApiModelProperty(value = "修程id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String programId;

    @ApiModelProperty(value = "使用年限")
    private Double serviceYear;

    @ApiModelProperty(value = "使用年限备注")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String serviceYearRemark;

    @ApiModelProperty(value = "备注")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "公司id")
    private String companyId;


    @ApiModelProperty(value = "所属系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "所属系统短名称")
    @TableField(exist = false)
    private String systemShortName;

    @ApiModelProperty(value = "首次投入使用列计划名称")
    @TableField(exist = false)
    private String firstUsePlanName;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String programName;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "车间名称")
    @TableField(exist = false)
    private String workshopName;

    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String companyName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
