package org.jeecg.modules.dispatch.workorder.bean;

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

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderTool对象", description = "")
@TableName("bu_work_order_tool")
public class BuWorkOrderTool extends Model<BuWorkOrderTool> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "来自作业表单实列(作业记录)id，标识这个器具属于哪个作业记录表，因为作业记录表中要显示用到哪些计量器具")
    private String workRecordInstId;

    @ApiModelProperty(value = "来自物资类型表id,字典bu_material_type,name,id")
    private String toolTypeId;

    @ApiModelProperty(value = "工器具id 自动根据工班、工器具类型获取")
    private String toolId;

    @ApiModelProperty(value = " 是否计量器具 0 否 1 是 ,字典 bu_state")
    @Dict(dicCode = "bu_state")
    private Integer measure;

    @ApiModelProperty(value = "是否缺少 0 否 1 是，字典 bu_state")
    @Dict(dicCode = "bu_state")
    private Integer lack;

    @ApiModelProperty(value = "需要数量")
    private Integer amount;


    @ApiModelProperty(value = "工具名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "工具分类")
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_kind")
    private Integer kind;

    @ApiModelProperty(value = "工具单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty(value = "工具编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "工具出厂编码")
    @TableField(exist = false)
    private String serialNo;

    @ApiModelProperty(value = "工具资产编码")
    @TableField(exist = false)
    private String assetCode;

    @ApiModelProperty(value = "单价")
    @TableField(exist = false)
    private Double price;

    @ApiModelProperty(value = "总价")
    @TableField(exist = false)
    private Double sumPrice;

    @ApiModelProperty(value = "工器具分类  字典dictCode=bu_tools_type")
    @Dict(dicCode = "bu_tools_type")
    @TableField(exist = false)
    private Integer category1;

    @ApiModelProperty(value = "物资分类2--属性 字典dictCode=bu_material_attr")
    @Dict(dicCode = "bu_material_attr")
    @TableField(exist = false)
    private String category2;

    @ApiModelProperty(value = "来自作业表单实列(作业记录)名称")
    @TableField(exist = false)
    private String workRecordInstName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
