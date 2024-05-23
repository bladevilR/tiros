package org.jeecg.modules.material.entry.bean;

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
import org.jeecg.modules.material.entry.bean.vo.LevelFourEntryData;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 每次同步新增的物资或数量都都在该表记录，一次同步如果发现有新增，则创建一条入库单及相关物资明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "入库明细对象", description = "每次同步新增的物资或数量都都在该表记录，一次同步如果发现有新增，则创建一条入库单及相关物资明细")
@TableName("bu_material_entry_detail")
public class BuMaterialEntryDetail extends Model<BuMaterialEntryDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属入库单id")
    private String entryOrderId;

    @ApiModelProperty(value = "EBS库位，这里是EBS的库位信息，架大修系统不能修改")
    private String ebsWarehouseId;

    @ApiModelProperty(value = "二级库编码", required = true)
    @NotBlank
    @TableField(value = "ebs_warehouse_levl_2")
    private String ebsWarehouseLevl2;

    @ApiModelProperty(value = "架大修库位id(目标货位)")
    private String selfWarehouseId;

    @ApiModelProperty(value = "入库物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "入库数量", required = true)
    @NotNull
    private BigDecimal amount;

    @ApiModelProperty(value = "入库价格", required = true)
    @NotNull
    private BigDecimal price;

    @ApiModelProperty(value = "入库人员id")
    private String entryUserId;

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

    @ApiModelProperty(value = "入库确认 字典dicCode=bu_state 0否 1是， 是否扫描进行了确认")
    @Dict(dicCode = "bu_state")
    private Integer confirm;

    @ApiModelProperty(value = "入库类别，是否新增 1表示新增以前库存中没有  2表示增加库存量  字典dictCode=bu_entryClass_type")
    @Dict(dicCode = "bu_entryClass_type")
    private Integer entryClass;

    @ApiModelProperty(value = "确认数量")
    private Double confirmAmount;
    @ApiModelProperty(value = "入库确认人")
    private String confirmUserId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "入库单号,自动生成")
    @TableField(exist = false)
    private String entryNo;

    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String materialTypeCode;

    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "物料描述")
    @TableField(exist = false)
    private String materialTypeSpec;

    @ApiModelProperty(value = "单位")
    @TableField(exist = false)
    private String materialTypeUnit;

    @ApiModelProperty(value = "总金额=入库数量*入库价格")
    @TableField(exist = false)
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "物料分类 字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    @TableField(exist = false)
    private String materialTypeCategory1;

    @ApiModelProperty(value = "架大修库位名称")
    @TableField(exist = false)
    private String selfWarehouseName;

    @ApiModelProperty(value = "二级库位名称")
    @TableField(exist = false)
    private String selfWarehouseName2;

    @ApiModelProperty(value = "入库人员名称")
    @TableField(exist = false)
    private String entryUserName;

    @ApiModelProperty(value = "入库部门id")
    @TableField(exist = false)
    private String entryDeptId;

    @ApiModelProperty(value = "入库部门名称")
    @TableField(exist = false)
    private String entryDeptName;
    @ApiModelProperty(value = "确认人名称")
    @TableField(exist = false)
    private String confirmUserName;
    @ApiModelProperty(value = "第四级库的信息",hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private List<BuMaterialEntryLevelFourDetail> detailList;
    @ApiModelProperty(value = "第四级库的信息")
    @TableField(exist = false)
    private List<LevelFourEntryData> levelFourDetail;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
