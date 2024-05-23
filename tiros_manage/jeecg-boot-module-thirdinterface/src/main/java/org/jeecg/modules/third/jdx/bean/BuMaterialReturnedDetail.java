package org.jeecg.modules.third.jdx.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 退料明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuMaterialReturnedDetail对象", description = "退料明细")
@TableName("bu_material_returned_detail")
public class BuMaterialReturnedDetail extends Model<BuMaterialReturnedDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属退料单id")
    private String returnedId;

    @ApiModelProperty(value = "工单id")
    private String workOrderId;

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "工单物料id", notes = "此处保存领用明细id")
    private String orderMaterialId;

    @ApiModelProperty(value = "退回数量")
    private Double returnAmount;

    @ApiModelProperty(value = "物料存放位置WBS")
    private String locationWbs;

    @ApiModelProperty(value = "物料存放位置路径名称")
    private String locationName;

    @ApiModelProperty(value = "EBS 二级库编码，保存后，后端更新存储")
    private String ebsWhCode;

    @ApiModelProperty(value = "EBS 库位编码，保存后，后端更新存储")
    private String ebsWhChildCode;

    @ApiModelProperty(value = "批次号")
    private String tradeNo;


    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String materialTypeCode;

    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "规格型号")
    @TableField(exist = false)
    private String materialTypeSpec;

    @ApiModelProperty(value = "单位")
    @TableField(exist = false)
    private String materialTypeUnit;

    @ApiModelProperty(value = "仓库code")
    @TableField(exist = false)
    private String locationCode;

    @ApiModelProperty(value = "EBS系统中的库存组织")
    @TableField(exist = false)
    private String sysHouseCategory;

    @ApiModelProperty(value = "托盘名称")
    @TableField(exist = false)
    private String palletName;

    @ApiModelProperty(value = "库存价格 前端从所选库存记录带入")
    @TableField(exist = false)
    private BigDecimal stockPrice;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    @TableField(exist = false)
    private Integer useCategory;

    @ApiModelProperty(value = "物料单价")
    @TableField(exist = false)
    private BigDecimal materialUnitPrice;

    @ApiModelProperty(value = "消耗金额（总额）")
    @TableField(exist = false)
    private BigDecimal materialTotalPrice;

    @ApiModelProperty(value = "所属列计划id")
    @TableField(exist = false)
    private String planId;

    @ApiModelProperty(value = "所属线路id")
    @TableField(exist = false)
    private String lineId;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属车辆号")
    @TableField(exist = false)
    private String trainNo;

    @ApiModelProperty(value = "所属工单编号")
    @TableField(exist = false)
    private String orderCode;

    @ApiModelProperty(value = "退料确认时间 yyyy-MM-dd")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date confirmTime;

    @ApiModelProperty(value = "消耗工班名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "所属系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "车辆段名称")
    @TableField(exist = false)
    private String depotName;

    /**
     * 设备类型id，后端用于获取所属系统名称
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private String assetTypeId;

    @ApiModelProperty(value = "车辆段id")
    @TableField(exist = false)
    @JsonIgnore
    private String depotId;

    @ApiModelProperty(value = "车间id")
    @TableField(exist = false)
    @JsonIgnore
    private String workshopId;

    @ApiModelProperty(value = "车间名称")
    @TableField(exist = false)
    @JsonIgnore
    private String workshopName;

    @ApiModelProperty(value = "车辆id")
    @TableField(exist = false)
    @JsonIgnore
    private String trainId;

    @ApiModelProperty(value = "架修周期id")
    @TableField(exist = false)
    @JsonIgnore
    private String periodId;

    @ApiModelProperty(value = "架修序号")
    @TableField(exist = false)
    @JsonIgnore
    private Integer repairIndex;

    @ApiModelProperty(value = "修程id")
    @TableField(exist = false)
    @JsonIgnore
    private String programId;

    @ApiModelProperty(value = "所属系统id")
    @TableField(exist = false)
    @JsonIgnore
    private String systemId;

    @ApiModelProperty(value = "所属财务项目")
    @TableField(exist = false)
    private String fdProject;

    @ApiModelProperty(value = "所属财务项目编码")
    @TableField(exist = false)
    private String fdProjectCode;

    @ApiModelProperty(value = "所属财务项目名称")
    @TableField(exist = false)
    private String fdProjectName;

    @ApiModelProperty(value = "所属财务任务")
    @TableField(exist = false)
    private String fdTask;

    @ApiModelProperty(value = "所属财务任务编码")
    @TableField(exist = false)
    private String fdTaskCode;

    @ApiModelProperty(value = "所属财务任务名称")
    @TableField(exist = false)
    private String fdTaskName;

    @ApiModelProperty(value = "所属财务开支编码")
    @TableField(exist = false)
    private String fdCostType;

    /**
     * maximo数据同步中间表id，后端用于处理物料消耗后更改中间表状态
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private String transDataId;

    /**
     * maximo数据同步中间表transId，后端用于处理物料消耗后更改中间表状态
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private Long transId;

    /**
     * 同步到maximo时间，后端用于处理物料消耗后更改中间表状态
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date transTime;

    /**
     * 成功标志 0初始1成功2失败
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private Integer successStatus;

    /**
     * maximo处理成功时间
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date successTime;

    /**
     * 消息 用于记录maximo失败信息等
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private String message;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
