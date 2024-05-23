package org.jeecg.modules.third.jdx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 物料消耗明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptMaterialUseRecord对象", description = "物料消耗明细")
@TableName("bu_rpt_material_use_record")
public class BuRptMaterialUseRecord extends Model<BuRptMaterialUseRecord> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "归档来源 0架大修自身数据 1检修系统同步 默认0")
    private Integer archiveType;

    @ApiModelProperty(value = "所属工单id")
    private String workOrderId;

    @ApiModelProperty(value = "所属工单名称")
    private String workOrderName;

    @ApiModelProperty(value = "所属工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "所属工单任务名称")
    private String orderTaskName;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "物资类型名称")
    private String materialTypeName;

    @ApiModelProperty(value = "物资类型编码")
    private String materialTypeCode;

    @ApiModelProperty(value = "物资类型型号规格")
    private String materialTypeModel;

    @ApiModelProperty(value = "物资类型描述")
    private String materialDesc;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "种类 字典dictCode=bu_material_kind", notes = "1物料 2工器具")
    private Integer kind;

    @ApiModelProperty(value = "物料分类  字典dictCode=bu_material_type")
    private String category1;

    @ApiModelProperty(value = "物料属性  字典dictCode=bu_material_attr")
    private String category2;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "消耗日期")
    private Date useDate;

    @ApiModelProperty(value = "资产编码")
    private String assetCode;

    @ApiModelProperty(value = "所属车辆段id 如果从检修系统来，则直接根据系统中的配置写入")
    private String depotId;

    @ApiModelProperty(value = "所属车辆段名称 如果从检修系统来，则直接根据系统中的配置写入")
    private String depotName;

    @ApiModelProperty(value = "所属车间id")
    private String workshopId;

    @ApiModelProperty(value = "所属车间名称")
    private String workshopName;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "所属线路名称")
    private String lineName;

    @ApiModelProperty(value = "所属车辆id")
    private String trainId;

    @ApiModelProperty(value = "所属车辆名称")
    private String trainName;

    @ApiModelProperty(value = "所属系统id 对应的车辆设备结构中的系统")
    private String tsystemId;

    @ApiModelProperty(value = "所属系统名称")
    private String systemName;

    @ApiModelProperty(value = "所属架修周期id 所属周期(来自架修周期表)")
    private String periodId;

    @ApiModelProperty(value = "作业班组id")
    private String groupId;

    @ApiModelProperty(value = "作业班组名称")
    private String groupName;

    @ApiModelProperty(value = "班长id")
    private String monitor;

    @ApiModelProperty(value = "班长名称")
    private String monitorName;

    @ApiModelProperty(value = "消耗工位id")
    private String stationId;

    @ApiModelProperty(value = "消耗工位号")
    private String stationNo;

    @ApiModelProperty(value = "消耗工位名称")
    private String stationName;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    private Integer useCategory;

    @ApiModelProperty(value = "备注")
    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
