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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆结构明细，可以从车车型设备结构导入，导入的话应根据初始数量生成对应设备部件的数据
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTrainStructureDetail对象", description = "车辆结构明细，可以从车车型设备结构导入，导入的话应根据初始数量生成对应设备部件的数据")
@TableName("bu_train_structure_detail")
public class BuTrainStructureDetail extends Model<BuTrainStructureDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "车辆结构id")
    private String trainStructId;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "结构WBS,为上级的wbs+.+本级编码,如：上级wbs.xxxx，没有上级则上级wbs为空")
    private String wbs;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "上级结构id")
    private String parentId;

    @ApiModelProperty(value = "类型 字典dictCode=bu_train_asset_type")
    @Dict(dicCode = "bu_train_asset_type")
    private Integer structType;

    @ApiModelProperty(value = "物资类型id 结构对应的物资类型")
    private String materialId;

    @ApiModelProperty(value = "是否是虚拟结构")
    @Dict(dicCode = "bu_state")
    private Integer subjunctive;

    @ApiModelProperty(value = "是否是周转设备")
    @Dict(dicCode = "bu_state")
    private Integer turnover;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "所属位置id")
    private String placeId;

    @ApiModelProperty(value = "位置描述")
    private String placeDesc;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态 字典dictCode=bu_train_type_status")
    @Dict(dicCode = "bu_train_type_status")
    private Integer status;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "排序(同级)")
    private String sortNo;

    @ApiModelProperty(value = "主要用于系统")
    private String shortName;


    @ApiModelProperty(value = "所属设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "上级结构名称")
    @TableField(exist = false)
    private String parentName;

    @ApiModelProperty(value = "位置编码")
    @TableField(exist = false)
    private String placeCode;

    @ApiModelProperty(value = "车型id")
    @TableField(exist = false)
    private String trainTypeId;

    @ApiModelProperty(value = "是否有子结点")
    @TableField(exist = false)
    private Boolean hasChildren; 

    @ApiModelProperty(value = "子结构")
    @TableField(exist = false)
    private List<BuTrainStructureDetail> children;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
