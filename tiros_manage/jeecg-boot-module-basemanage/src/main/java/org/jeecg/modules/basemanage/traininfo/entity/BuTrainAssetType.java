package org.jeecg.modules.basemanage.traininfo.entity;

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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备类型结构
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTrainAssetType对象", description = "设备类型结构")
@TableName("bu_train_asset_type")
public class BuTrainAssetType extends Model<BuTrainAssetType> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属车型id")
    private String trainTypeId;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "wbs")
    private String wbs;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "上级结构id")
    private String parentId;

    @ApiModelProperty(value = "结构类型  字典dictCode=bu_train_asset_type")
    @Dict(dicCode = "bu_train_asset_type")
    private Integer structType;

    @ApiModelProperty(value = "初始数量 在根据结构生成具体设备时，默认生成多少个同样的部件设备，默认为1")
    private Integer initNum;

    @ApiModelProperty(value = "设备分类id")
    private String assetCategoryId;

    @ApiModelProperty(value = "物资类型id")
    private String materialId;

    @ApiModelProperty(value = "是否虚拟结构  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer subjunctive;

    @ApiModelProperty(value = "是否周转设备  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer turnover;

    @ApiModelProperty(value = "是否BOM  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer bom;

    @ApiModelProperty(value = "排序(同级)")
    private String sortNum;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "状态 字典dictCode=bu_train_type_status")
    @Dict(dicCode = "bu_train_type_status")
    private Integer status;

    @ApiModelProperty(value = "所属位置id")
    private String placeId;

    @ApiModelProperty(value = "位置描述")
    private String placeDesc;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "短名称，主要用于系统")
    private String shortName;


    @ApiModelProperty(value = "上级结构名称")
    @TableField(exist = false)
    private String parentName;

    @ApiModelProperty(value = "位置编码")
    @TableField(exist = false)
    private String placeCode;

    @ApiModelProperty(value = "子结构")
    @TableField(exist = false)
    private List<BuTrainAssetType> children;

    @ApiModelProperty(value = "是否有子结点")
    @TableField(exist = false)
    private Boolean hasChildren; 


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
