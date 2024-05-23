package org.jeecg.modules.third.jdx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 列管备件
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_material_spare_part")
@ApiModel(value = "BuMaterialSparePart对象", description = "列管备件")
public class BuMaterialSparePart extends Model<BuMaterialSparePart> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "物资编码 对应业务中的物资编码", required = true)
    @NotBlank
    private String materialCode;

    @ApiModelProperty(value = "资产编码")
    private String assetCode;

    @ApiModelProperty(value = "出厂编码")
    private String manufNo;

    @ApiModelProperty(value = "名称", required = true)
    private String name;

    @ApiModelProperty(value = "规格型号")
    private String model;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "出厂日期")
    private Date leaveFactory;

    @ApiModelProperty(value = "有效日期")
    private Date expirDate;

    @ApiModelProperty(value = "厂商id 字典dictCode=(bu_base_supplier,name,id)")
    private String supplierId;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "所属系统id 接口获取/tiros/trainAssetType/system")
    private String systemId;

    @ApiModelProperty(value = "设备类型id 接口获取/tiros/trainAssetType/list（需用到获取线路接口返回的trainTypeId）")
    private String assetTypeId;

    @ApiModelProperty(value = "状态 1使用中 2待使用 3维修中 4已报废，在进行设备更换时更新 字典dictCode=bu_turnover_status")
    private Integer status;

    @ApiModelProperty(value = "所属线路id 接口获取/tiros/line/list", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "所属车间id 接口获取/tiros/workshop/listByLineId")
    private String workshopId;

    @ApiModelProperty(value = "所属仓库id 字典dictCode=(bu_mtr_warehouse,name,id)")
    private String warehouseId;

    @ApiModelProperty(value = "所属工班id 字典dictCode=(bu_mtr_workshop_group,group_name,id)")
    private String groupId;

    @ApiModelProperty(value = "出库日期 yyyy-MM-dd", example = "2020-01-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date outDate;

    @ApiModelProperty(value = "出库单号")
    private String outOrderNo;

    @ApiModelProperty(value = "当前位置")
    private String currentLocation;

    @ApiModelProperty(value = "责任人员id 接口获取/tiros/sys/user/listByGroupId  app端接口/tiros/app/user/listByGroupId")
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
