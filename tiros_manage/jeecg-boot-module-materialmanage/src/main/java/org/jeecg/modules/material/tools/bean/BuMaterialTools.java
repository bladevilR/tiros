package org.jeecg.modules.material.tools.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 工具信息，包括工器具、工装等信息，一种物资类型可能存在多条记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "工艺装备对象", description = "工具信息，包括工器具、工装等信息，一种物资类型可能存在多条记录")
@TableName("bu_material_tools")
public class BuMaterialTools extends Model<BuMaterialTools> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "类型  字典dictCode=bu_tools_type")
    @Dict(dicCode = "bu_tools_type")
    private Integer toolType;

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "编码 对应业务中的物资编码")
    private String code;

    @ApiModelProperty(value = "资产编码")
    private String assetCode;

    @ApiModelProperty(value = "出厂编码")
    private String serialNo;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "规格型号")
    private String model;

    @ApiModelProperty(value = "生产厂家id 字典dictCode=(bu_base_supplier,name,id)")
    private String supplierId;

    @ApiModelProperty(value = "入场使用日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date entraceDate;

    @ApiModelProperty(value = "使用寿命 单位天")
    private Integer lifetime;

    @ApiModelProperty(value = "出厂日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date leaveFactory;

    @ApiModelProperty(value = "有效日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirDate;

    @ApiModelProperty(value = "入库日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date entryDate;

    @ApiModelProperty(value = "所属线路id 接口获取/tiros/line/list", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "所属车间id 接口获取/tiros/workshop/listByLineId", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "所属工班id 字典dictCode=(bu_mtr_workshop_group,group_name,id)")
    private String groupId;

    @ApiModelProperty(value = "责任人员id")
    private String userId;

    @ApiModelProperty(value = "所属仓库id 字典dictCode=(bu_mtr_warehouse,name,id)")
    private String warehouseId;

    @ApiModelProperty(value = "状态  字典dictCode=bu_tools_status")
    @Dict(dicCode = "bu_tools_status")
    private Integer status;

    @ApiModelProperty(value = "保养周期 单位天")
    private Integer serviceInterval;

    @ApiModelProperty(value = "上次送检时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastCheckTime;

    @ApiModelProperty(value = "下次送检时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date nextCheckTime;

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

    @ApiModelProperty(value = "是否自检,字典bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isSelfCheck;

    @ApiModelProperty(value = "上次自检时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastSelfCheckTime;

    @ApiModelProperty(value = "单价：单位元")
    private BigDecimal price;

    @ApiModelProperty(value = "是否是maximo同步过来的")
    @Dict(dicCode = "bu_state")
    private Integer sync;

    @ApiModelProperty(value = "是否电动工具，字典bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isElectric;

    @ApiModelProperty(value = "是否固定资产，字典bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isFixedAsset;

    @ApiModelProperty(value = "存放位置")
    private String storageLocation;

    @ApiModelProperty(value = "公司id")
    private String companyId;


    @ApiModelProperty(value = "所属仓库名称")
    @TableField(exist = false)
    private String warehouseName;

    @ApiModelProperty(value = "所属工班名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "生产厂家名称")
    @TableField(exist = false)
    private String supplierName;

    @ApiModelProperty(value = "数量")
    @TableField(exist = false)
    private String amount;

    @ApiModelProperty("物资类型--规格描述")
    @TableField(exist = false)
    private String spec;

    @ApiModelProperty("物资类型--单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty(value = "物资类型--种类 字典dictCode=bu_material_kind")
    @Dict(dicCode = "bu_material_kind")
    @TableField(exist = false)
    private Integer kind;

    @ApiModelProperty(value = "工器具分类  字典dictCode=bu_tools_type")
    @Dict(dicCode = "bu_tools_type")
    @TableField(exist = false)
    private Integer category1;

    @ApiModelProperty(value = "物资类型--属性 字典dictCode=bu_material_attr")
    @Dict(dicCode = "bu_material_attr")
    @TableField(exist = false)
    private String category2;

    @ApiModelProperty(value = "责任人员名字")
    @TableField(exist = false)
    private String userName;

    @ApiModelProperty(value = "是否过期,字典bu_state")
    @TableField(exist = false)
    @Dict(dicCode = "bu_state")
    private Integer overdue;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
