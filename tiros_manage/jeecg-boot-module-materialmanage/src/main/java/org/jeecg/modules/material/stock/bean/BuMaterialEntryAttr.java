package org.jeecg.modules.material.stock.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 物资批次属性
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_material_entry_attr")
@ApiModel(value = "BuMaterialEntryAttr对象", description = "物资批次属性")
public class BuMaterialEntryAttr extends Model<BuMaterialEntryAttr> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "批次号")
    private String tradeNo;

    @ApiModelProperty(value = "入库日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date entryDate;

    @ApiModelProperty(value = "出厂日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date leaveFactory;

    @ApiModelProperty(value = "生产日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date productionDate;

    @ApiModelProperty(value = "有效期，有效天数")
    private Integer expirDay;

    @ApiModelProperty(value = "有效日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirDate;


    @ApiModelProperty(value = "物资类型名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "物资类型描述")
    @TableField(exist = false)
    private String materialTypeSpec;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
