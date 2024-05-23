package org.jeecg.modules.material.qrcode.bean;

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
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 查询时，自动生成该表中不存在的对应数据以及二维码
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "标识码对象", description = "查询时，自动生成该表中不存在的对应数据以及二维码")
@TableName("bu_material_qrcode")
public class BuMaterialQrcode extends Model<BuMaterialQrcode> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "对象类型 1列管备件 2工器具 3托盘 4物资 5仓库", required = true)
    @NotNull
    @Dict(dicCode = "bu_objType_type")
    private Integer objType;

    @ApiModelProperty(value = "对象ID 当对象类型是4物资时，对象ID为物资类型ID", required = true)
    @NotBlank
    private String objId;

    @ApiModelProperty(value = "物资编码")
    private String materialCode;

    @ApiModelProperty(value = "资产编码")
    private String assetCode;

    @ApiModelProperty(value = "出厂编码")
    private String manufNo;

    @ApiModelProperty(value = "标识码图片url 生成过二维码后保存图片的路径，相对路径")
    private String qrcodeImgUrl;

    @ApiModelProperty(value = "是否打印 0未打印 1已打印")
    @Dict(dicCode = "bu_print_type")
    private Integer print;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    // 仓库二维码列表展示信息
    @ApiModelProperty(value = "仓库二维码信息：编码")
    @TableField(exist = false)
    private String warehouseCode;

    @ApiModelProperty(value = "仓库二维码信息：名称")
    @TableField(exist = false)
    private String warehouseName;

    @ApiModelProperty(value = "仓库二维码信息：位置")
    @TableField(exist = false)
    private String warehouseLocation;

    @ApiModelProperty(value = "仓库二维码信息：类别")
    @TableField(exist = false)
    @Dict(dicCode = "bu_warehouse_type")
    private Integer warehouseType;

    @ApiModelProperty(value = "仓库二维码信息：备注")
    @TableField(exist = false)
    private String warehouseRemark;

    // 物资二维码列表展示信息
    @ApiModelProperty("物资二维码信息：物资名称")
    @TableField(exist = false)
    private String materialName;

    @ApiModelProperty(value = "物资二维码信息：物料分类 在物资分类中定义，如果是物料种类，1必换件 2故障件(耦换件) 3耗材")
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_type")
    private String materialCategory;

    @ApiModelProperty(value = "物资二维码信息：物料属性 来自物资分类表，1紧固件 2备品备件 3车体 4车上电气 5辅助 6牵引 7制定 8转向架")
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_attr")
    private String materialAttr;

    @ApiModelProperty("物资二维码信息：备注")
    @TableField(exist = false)
    private String materialRemark;

    // 托盘二维码列表展示信息
    @ApiModelProperty(value = "托盘二维码信息：编码")
    @TableField(exist = false)
    private String palletCode;

    @ApiModelProperty(value = "托盘二维码信息：名称")
    @TableField(exist = false)
    private String palletName;

    @ApiModelProperty(value = "托盘二维码信息：尺寸")
    @TableField(exist = false)
    private String palletSize;

    @ApiModelProperty(value = "托盘二维码信息：备注")
    @TableField(exist = false)
    private String palletRemark;

    // 周转件二维码列表展示信息
    @ApiModelProperty(value = "周转件二维码信息：名称")
    @TableField(exist = false)
    private String turnoverName;

    // 工器具二维码列表展示信息
    @ApiModelProperty(value = "工器具二维码信息：名称")
    @TableField(exist = false)
    private String toolsName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
