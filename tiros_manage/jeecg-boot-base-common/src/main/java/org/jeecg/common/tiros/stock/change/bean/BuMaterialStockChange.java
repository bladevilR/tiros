package org.jeecg.common.tiros.stock.change.bean;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 库存变动记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-10-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_material_stock_change")
@ApiModel(value = "BuMaterialStockChange对象", description = "库存变动记录")
public class BuMaterialStockChange extends Model<BuMaterialStockChange> {
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

    @ApiModelProperty(value = "变动时间")
    private Date changeTime;

    @ApiModelProperty(value = "变动原因 文本描述")
    private String changeReason;

    @ApiModelProperty(value = "变动类型 1新增、2增加、3减少、4删除")
    private Integer changeType;

    @ApiModelProperty(value = "旧值")
    private Double oldValue;

    @ApiModelProperty(value = "新值")
    private Double newValue;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "工单号")
    private String orderCode;

    @ApiModelProperty(value = "分配明细id stock_type=1库存量时使用")
    private String assignDetailId;

    @ApiModelProperty(value = "退料明细id stock_type=1库存量时使用")
    private String returnedDetailId;

    @ApiModelProperty(value = "工单物料实际消耗id stock_type=4班组库存量时使用")
    private String orderMaterialActId;

    @ApiModelProperty(value = "操作人")
    private String operationUser;

    @ApiModelProperty(value = "备注")
    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
