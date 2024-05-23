package org.jeecg.modules.dispatch.workorder.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * <p>
 * 班组库存
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuMaterialGroupStock对象", description = "班组库存")
@TableName("bu_material_group_stock")
public class BuMaterialGroupStock extends Model<BuMaterialGroupStock> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属仓库id 暂时不需要")
    private String warehouseId;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "领用单id")
    private String applyId;

    @ApiModelProperty(value = "领用明细id")
    private String applyDetailId;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "分配明细id")
    private String assignDetailId;

    @ApiModelProperty(value = "批次号")
    private String tradeNo;

    @ApiModelProperty(value = "所属系统id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String systemId;

    @ApiModelProperty(value = "工位id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String workstationId;

    @ApiModelProperty(value = "类别")
    @Dict(dicCode = "bu_material_type")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer useCategory;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String materialTypeCode;

    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "物资价格 来自物资类型表中的最新价格")
    @TableField(exist = false)
    private BigDecimal materialTypePrice;

    @ApiModelProperty(value = "所属仓库wbs")
    @TableField(exist = false)
    private String warehouseWbs;

    @ApiModelProperty(value = "所属仓库名称")
    @TableField(exist = false)
    private String warehouseName;

    @ApiModelProperty(value = "来源库位")
    @TableField(exist = false)
    private String sourceLocationName;

    @ApiModelProperty(value = "物料分类 1必换件 2故障件(偶换件) 3耗材")
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_type")
    private String materialType;

    @ApiModelProperty(value = "物料属性 1紧固件 2备品备件 3车体 4车上电气 5辅助 6牵引 7制定 8转向架")
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_attr")
    private String materialAttr;

    @ApiModelProperty(value = "单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty("规格描述")
    @TableField(exist = false)
    private String spec;

    @ApiModelProperty(value = "所属系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "工位名称")
    @TableField(exist = false)
    private String workstationName;

    @ApiModelProperty(value = "当前可用数量")
    @TableField(exist = false)
    private BigDecimal usableAmount;

    @ApiModelProperty(value = "班组库存占用详情")
    @TableField(exist = false)
    private String usedDetailInfo;

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
     * 相关的分配明细id，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private Set<String> relativeAssignDetailIdSet;

    /**
     * 相关的工单物料实际消耗id，后端用于处理数据变动
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private Set<String> relativeOrderMaterialActIdSet;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
