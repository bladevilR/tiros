package org.jeecg.modules.produce.trainhistory.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆设备
 * 按车辆分区存放，如果后期设备太多，可以线路分表，设备更换时，如果这个设备对应的是周转件，从车上换下的设备需要在
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTrainAsset对象", description = "车辆设备 按车辆分区存放，如果后期设备太多，可以线路分表，设备更换时，如果这个设备对应的是周转件，从车上换下的设备需要在")
@TableName("bu_train_asset")
public class BuTrainAsset extends Model<BuTrainAsset> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "设备名称", required = true)
    @NotBlank
    private String assetName;

    @ApiModelProperty(value = "设备编码 根据规则自动生成的唯一编码(用于系统内部使用)")
    private String assetNo;

    @ApiModelProperty(value = "上级设备id 对应着本表的id，为null表示为顶级")
    private String parentId;

    @ApiModelProperty(value = "所属车辆结构明细id 对应的车辆结构明细id")
    private String structDetailId;

    @ApiModelProperty(value = "设备类型 来自设备类型结构表id")
    private String assetTypeId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "所属车辆id")
    private String trainId;

    @ApiModelProperty(value = "设备品牌")
    private String brand;

    @ApiModelProperty(value = "设备厂商")
    private String vendor;

    @ApiModelProperty(value = "规格型号")
    private String model;

    @ApiModelProperty(value = "出厂编号 用户提供")
    private String manufNo;

    @ApiModelProperty(value = "物资编码 由用户提供")
    private String materialCode;

    @ApiModelProperty(value = "资产编码")
    private String assetCode;

    @ApiModelProperty(value = "是否虚拟结构  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer subjunctive;

    @ApiModelProperty(value = "是否周转设备  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer turnover;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "所属位置id")
    private String placeId;

    @ApiModelProperty(value = "位置描述")
    private String placeDesc;

    @ApiModelProperty(value = "状态")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "结构类型  字典dictCode=bu_train_asset_type")
    @Dict(dicCode = "bu_train_asset_type")
    private Integer structType;

    @ApiModelProperty(value = "wbs")
    private String wbs;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "排序(同级)")
    private String sortNo;

    @ApiModelProperty(value = "周转件id 来自周转设备表 bu_material_spare_part主键")
    private String turnoverAssetId;

    @ApiModelProperty(value = "短名称，主要用于系统")
    private String shortName;


    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属车辆号")
    @TableField(exist = false)
    private String trainNo;

    @ApiModelProperty(value = "所属系统id")
    @TableField(exist = false)
    private String systemId;

    @ApiModelProperty(value = "所属系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "车辆结构明细名称")
    @TableField(exist = false)
    private String structDetailName;

    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "位置编码")
    @TableField(exist = false)
    private String placeCode;

    @ApiModelProperty(value = "上级设备名称")
    @TableField(exist = false)
    private String parentName;

    @ApiModelProperty(value = "是否有子节点")
    @TableField(exist = false)
    private Boolean hasChildren; 

    @ApiModelProperty(value = "子节点")
    @TableField(exist = false)
    private List<BuTrainAsset> children;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
