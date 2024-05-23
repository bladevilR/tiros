package org.jeecg.modules.dispatch.workorder.bean;

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

/**
 * <p>
 * 工单任务目标设备
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_work_order_task_equ")
@ApiModel(value = "BuWorkOrderTaskEqu对象", description = "工单任务目标设备")
public class BuWorkOrderTaskEqu extends Model<BuWorkOrderTaskEqu> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "车辆结构明细id")
    private String structDetailId;

    @ApiModelProperty(value = "车辆设备id")
    private String trainAssetId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "设备类型编码")
    @TableField(exist = false)
    private String assetTypeCode;

    @ApiModelProperty(value = "车辆结构明细名称")
    @TableField(exist = false)
    private String structDetailName;

    @ApiModelProperty(value = "车辆结构明细编码")
    @TableField(exist = false)
    private String structDetailCode;

    @ApiModelProperty(value = "车辆设备名称")
    @TableField(exist = false)
    private String trainAssetName;

    @ApiModelProperty(value = "车辆设备编码")
    @TableField(exist = false)
    private String trainAssetNo;

    @ApiModelProperty(value = "车辆设备结构类型  字典dictCode=bu_train_asset_type")
    @TableField(exist = false)
    @Dict(dicCode = "bu_train_asset_type")
    private Integer trainAssetStructType;

    @ApiModelProperty(value = "位置编号")
    @TableField(exist = false)
    private String locationCode;

    @ApiModelProperty(value = "位置名称")
    @TableField(exist = false)
    private String locationName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
