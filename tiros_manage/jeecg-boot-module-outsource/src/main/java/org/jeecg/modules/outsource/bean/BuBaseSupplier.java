package org.jeecg.modules.outsource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 厂商信息表
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuBaseSupplier对象", description = "厂商信息表")
@TableName("bu_base_supplier")
public class BuBaseSupplier extends Model<BuBaseSupplier> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @ApiModelProperty(value = "厂商类型 1 供应商  2 维修商 字典 bu_bizType", required = true)
    @NotNull
    @Dict(dicCode = "bu_bizType")
    private Integer bizType;

    @ApiModelProperty(value = "厂商类别1 车辆  2  部件 3 耗材 字典 bu_vendor_category", required = true)
    @NotNull
    @Dict(dicCode = "bu_vendor_category")
    private Integer category;

    @ApiModelProperty(value = "对应的设备类型如果是部件类别则表示对应的设备类型id，如果是耗材类别则对应的是物资类型id")
    private String objTypeId;

    @ApiModelProperty(value = "联系人")
    private String contactName;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = " 状态0 无效  1 有效，字典 bu_effective", required = true)
    @NotNull
    @Dict(dicCode = "bu_effective")
    private Integer status;

    @ApiModelProperty(value = "评分")
    private Double appraise;

    @ApiModelProperty(value = "备注")
    private String  remarks;

    @ApiModelProperty(value = "线路")
    private String lineId;

    @ApiModelProperty(value = "修程类型")
    private String repairProgramId;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String repairProgramName;

    @TableField(exist = false)
    private String  objTypeName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
