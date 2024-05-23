package org.jeecg.common.tiros.stock.use.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 库存占用
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_material_stock_use")
@ApiModel(value = "BuMaterialStockUse对象", description = "库存占用")
public class BuMaterialStockUse extends Model<BuMaterialStockUse> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "库存类型：1库存量、4班组库存量")
    private Integer stockType;

    @ApiModelProperty(value = "库存id")
    private String stockId;

    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "批次号")
    private String tradeNo;

    @ApiModelProperty(value = "占用时间")
    private Date useTime;

    @ApiModelProperty(value = "占用原因 文本描述")
    private String useReason;

    @ApiModelProperty(value = "占用类型：0预占用 1实际占用")
    private Integer useType;

    @ApiModelProperty(value = "占用量")
    private Double useAmount;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "工单号")
    private String orderCode;

    @ApiModelProperty(value = "分配明细id stock_type=1库存量时使用")
    private String assignDetailId;

    @ApiModelProperty(value = "工单物料实际消耗id stock_type=4班组库存量时使用")
    private String orderMaterialActId;

    @ApiModelProperty(value = "操作人")
    private String operationUser;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "所属仓库wbs")
    @TableField(exist = false)
    private String warehouseWbs;

    @ApiModelProperty(value = "所属仓库名称")
    @TableField(exist = false)
    private String warehouseName;

    @ApiModelProperty(value = "所属仓库级别")
    @TableField(exist = false)
    private Integer warehouseLevel;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
