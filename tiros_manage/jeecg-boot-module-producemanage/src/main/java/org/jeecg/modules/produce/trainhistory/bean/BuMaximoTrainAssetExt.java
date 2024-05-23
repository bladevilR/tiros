package org.jeecg.modules.produce.trainhistory.bean;

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
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * maximo资产设备扩展信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_maximo_train_asset_ext")
@ApiModel(value = "BuMaximoTrainAssetExt对象", description = "maximo资产设备扩展信息")
public class BuMaximoTrainAssetExt extends Model<BuMaximoTrainAssetExt> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "资产设备id", required = true)
    @NotBlank
    private String assetId;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @ApiModelProperty(value = "车厢")
    private String carriage;

    @ApiModelProperty(value = "设备系统")
    private String systemId;

    @ApiModelProperty(value = "设备类型")
    private String assetTypeId;

    @ApiModelProperty(value = "设备分类")
    private String assetCategory;

    @ApiModelProperty(value = "厂商")
    private String vendor;

    @ApiModelProperty(value = "单位")
    private String company;

    @ApiModelProperty(value = "合同编号")
    private String contractNo;

    @ApiModelProperty(value = "合同名称")
    private String contractName;

    @ApiModelProperty(value = "制造日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date madeDate;

    @ApiModelProperty(value = "购入日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date buyDate;

    @ApiModelProperty(value = "安装日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date installDate;

    @ApiModelProperty(value = "验收日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date verifyDate;

    @ApiModelProperty(value = "质保日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warrantedDate;

    @ApiModelProperty(value = "运营日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date operateDate;

    @ApiModelProperty(value = "使用年限")
    private Double ageYear;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "规格型号")
    private String model;

    @ApiModelProperty(value = "bom名称")
    private String bomName;

    @ApiModelProperty(value = "资产编码")
    private String assetCode;

    @ApiModelProperty(value = "出厂编码")
    private String manufNo;

    @ApiModelProperty(value = "标签编码")
    private String tagCode;

    @ApiModelProperty(value = "归属部门")
    private String belongDepart;

    @ApiModelProperty(value = "责任班组")
    private String dutyGroup;

    @ApiModelProperty(value = "责任人")
    private String dutyUser;

    @ApiModelProperty(value = "是否虚拟 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer subjunctive;

    @ApiModelProperty(value = "是否委外维修 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer outsourceRepair;

    @ApiModelProperty(value = "是否探伤 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer flawDetection;

    @ApiModelProperty(value = "是否送检 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer sendCheck;


    @ApiModelProperty(value = "设备系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "厂商名称")
    @TableField(exist = false)
    private String vendorName;

    @ApiModelProperty(value = "归属部门名称")
    @TableField(exist = false)
    private String belongDepartName;

    @ApiModelProperty(value = "责任班组名称")
    @TableField(exist = false)
    private String dutyGroupName;

    @ApiModelProperty(value = "责任人名称")
    @TableField(exist = false)
    private String dutyUserName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
