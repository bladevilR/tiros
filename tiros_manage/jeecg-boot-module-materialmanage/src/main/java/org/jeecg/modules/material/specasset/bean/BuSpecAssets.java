package org.jeecg.modules.material.specasset.bean;

import com.baomidou.mybatisplus.annotation.*;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 特种设备
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuSpecAssets对象", description = "特种设备")
@TableName("bu_spec_assets")
public class BuSpecAssets extends Model<BuSpecAssets> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "编码", required = true)
    @NotBlank
    private String assetCode;

    @ApiModelProperty(value = "状态  字典dictCode=bu_tools_status")
    @Dict(dicCode = "bu_tools_status")
    private Integer status;

    @ApiModelProperty(value = "线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "车间id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "出厂编码")
    private String manufNo;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "物资编码")
    private String materialCode;

    @ApiModelProperty(value = "规格型号")
    private String model;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "出厂日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date leaveFactory;

    @ApiModelProperty(value = "投入日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date useDate;

    @ApiModelProperty(value = "有效日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date expirDate;

    @ApiModelProperty(value = "厂商id")
    private String supplierId;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "所属系统id")
    private String systemId;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "所属工班id")
    private String groupId;

    @ApiModelProperty(value = "当前位置")
    private String currentLocation;

    @ApiModelProperty(value = "责任人员id")
    private String dutyUserId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "公司id")
    private String companyId;


    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "车间名称")
    @TableField(exist = false)
    private String workshopName;

    @ApiModelProperty(value = "厂商名称")
    @TableField(exist = false)
    private String supplierName;

    @ApiModelProperty(value = "所属工班名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "责任人员名称")
    @TableField(exist = false)
    private String dutyUserName;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
