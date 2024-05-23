package org.jeecg.modules.third.jdx.bean;

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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 物资库存  一种物资类型可能存在多条，所以在查询库存时要注意汇总，汇总时价格用最新的，且只要显示 物资类型 的kind为
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "物资库存对象", description = "物资库存  一种物资类型可能存在多条，所以在查询库存时要注意汇总，汇总时价格用最新的，且只要显示 物资类型 的kind为")
@TableName("bu_material_stock")
public class BuMaterialStock extends Model<BuMaterialStock> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属仓库id", required = true)
    @NotBlank
    private String warehouseId;

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "入库明细id")
    private String entryDetailId;

    @ApiModelProperty(value = "批次号")
    private String tradeNo;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String materialTypeCode;

    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "库位wbs")
    @TableField(exist = false)
    private String warehouseWbs;

    @ApiModelProperty(value = "物料分类 1必换件 2故障件(偶换件) 3耗材")
    @TableField(exist = false)
//    @Dict(dicCode = "bu_material_type")
    private String materialType;

    @ApiModelProperty(value = "物料属性 1紧固件 2备品备件 3车体 4车上电气 5辅助 6牵引 7制定 8转向架")
    @TableField(exist = false)
//    @Dict(dicCode = "bu_material_attr")
    private String materialAttr;

    @ApiModelProperty(value = "单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty(value = "当前价格")
    @TableField(exist = false)
    private BigDecimal currentPrice;

    @ApiModelProperty("规格描述")
    @TableField(exist = false)
    private String spec;

    @ApiModelProperty(value = "入库日期 yyyy-MM-dd")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date entryDate;

    @ApiModelProperty(value = "生产日期 yyyy-MM-dd")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date productionDate;

    @ApiModelProperty(value = "有效日期 yyyy-MM-dd")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirDate;

    @ApiModelProperty(value = "所属仓库名称")
    @TableField(exist = false)
    private String warehouseName;


    /**
     * 是否需要添加，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private Boolean needAdd;

    /**
     * 是否需要更新，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private Boolean needUpdate;

    /**
     * 是否需要删除，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private Boolean needDelete;

    /**
     * 旧的原始数量，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private BigDecimal oldAmount;

    /**
     * 旧的原始价格，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private BigDecimal oldPrice;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
