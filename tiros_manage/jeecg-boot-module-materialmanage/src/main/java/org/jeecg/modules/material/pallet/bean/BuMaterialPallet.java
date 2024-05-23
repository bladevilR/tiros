package org.jeecg.modules.material.pallet.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 物资托盘
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "物资托盘对象", description = "物资托盘")
@TableName("bu_material_pallet")
public class BuMaterialPallet extends Model<BuMaterialPallet> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编码", required = true)
    @NotBlank
    private String code;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "尺寸")
    @TableField(value = "p_size")
    private String palletSize;

    @ApiModelProperty(value = "材质")
    private String texture;

    @ApiModelProperty(value = "物资类型 物资类型id，多个逗号分隔")
    private String materialTypes;

    @ApiModelProperty(value = "使用状态 0未使用1使用中 字典dictCode=bu_pallet_use_status 发料确认后将托盘设置为使用中，工单提交后设置为未使用")
    @Dict(dicCode = "bu_pallet_use_status")
    private Integer useStatus;

    @ApiModelProperty(value = "状态 0无效 1有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "物资类型名称 多个逗号分隔")
    @TableField(exist = false)
    private String materialTypeNames;

    @ApiModelProperty(value = "是否批量新增")
    @TableField(exist = false)
    private Boolean isBatchAdd;

    @ApiModelProperty(value = "批量新增开始序号")
    @TableField(exist = false)
    private Integer batchAddStartNum;

    @ApiModelProperty(value = "批量新增结束序号")
    @TableField(exist = false)
    private Integer batchAddEndNum;

    @ApiModelProperty(value = "标识码图片url 生成过二维码后保存图片的路径，相对路径")
    @TableField(exist = false)
    private String qrcodeImgUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
