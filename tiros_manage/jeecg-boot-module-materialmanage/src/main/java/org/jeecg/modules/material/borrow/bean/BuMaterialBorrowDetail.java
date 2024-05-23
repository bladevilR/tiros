package org.jeecg.modules.material.borrow.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author youGen
 * @since 2021-05-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BuMaterialBorrowDetail对象", description="")
public class BuMaterialBorrowDetail extends Model<BuMaterialBorrowDetail> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.UUID)
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "借用单id",required = true)
    @NotBlank
    private String borrowId;

    @ApiModelProperty(value = "物资类型id",required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "借用数量",required = true)
    @NotNull
    private Double amount;

    @ApiModelProperty(value = "借用原因")
    private String reason;

    @ApiModelProperty(value = "0 未归还 1 已归还，字典bu_material_return_status",required = true)
    @NotNull
    @Dict(dicCode = "bu_material_return_status")
    private Integer returnStatus;

    @ApiModelProperty(value = "归还日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date returnDate;

    @ApiModelProperty(value = "借用部门名称")
    @TableField(exist = false)
    private String deptName;

    @ApiModelProperty(value = "所属库位")
    private String warehouseLocationId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String materialTypeCode;

    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "物资描述")
    @TableField(exist = false)
    private String materialTypeSpec;

    @ApiModelProperty(value = "物资单位")
    @TableField(exist = false)
    private String materialTypeUnit;

    @ApiModelProperty(value = "仓库名称")
    @TableField(exist = false)
    private String warehouseName;
    @ApiModelProperty(value = "归还类型 1物料归还 2调拨归还 字典dictCode=bu_material_return_type")
    @Dict(dicCode = "bu_material_return_type")
    @TableField(exist = false)
    private Integer returnType;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
