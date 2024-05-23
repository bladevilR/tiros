package org.jeecg.modules.material.entry.bean;

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

/**
 * <p>
 *
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "入库明细对象第四级仓库")
@TableName("bu_material_entry_4_detail")
public class BuMaterialEntryLevelFourDetail extends Model<BuMaterialEntryLevelFourDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属入库单id",required = true)
    @NotBlank
    private String entryOrderId;
    @ApiModelProperty(value = "入库明细id", required = true)
    @NotBlank
    private String enterDetailId;

    @ApiModelProperty(value = "入库物资类型id", required = true)
    @NotBlank
    private String materialTypeId;
    @ApiModelProperty(value = "四级库id", required = true)
    @NotBlank
    private String selfWarehouseId;

    @ApiModelProperty(value = "入库数量", required = true)
    @NotNull
    private Double amount;

    @ApiModelProperty(value = "入库人员id")
    private String oprationUserId;

    @ApiModelProperty(value = "入库日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date oprationDate;

    @ApiModelProperty(value = "架大修四级库位名称")
    @TableField(exist = false)
    private String selfWarehouseName;
    @ApiModelProperty(value = "入库人员名称")
    @TableField(exist = false)
    private String oprationUserName;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
