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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderMaterial对象", description = "")
@TableName("bu_work_order_material")
public class BuWorkOrderMaterial extends Model<BuWorkOrderMaterial> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "作业任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "物资id.字典bu_material_type,name,id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "所需数量")
    private Double amount;

    @ApiModelProperty(value = "计划数量")
    private Double planAmount;

    @ApiModelProperty(value = "实际消耗数量")
    private Double actAmount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否需要发料 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer needDispatchin;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer useCategory;

    @ApiModelProperty(value = "是否核实  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isVerify;

    @ApiModelProperty(value = "工单物料操作类型 字典dictCode=bu_order_material_op_type")
    @Dict(dicCode = "bu_order_material_op_type")
    private Integer opType;

    @ApiModelProperty(value = "是否已生成单据 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isGenOrder;

    @ApiModelProperty(value = "系统id")
    private String systemId;

    @ApiModelProperty(value = "工位id")
    private String workstationId;


    @ApiModelProperty(value = "物料名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "物料单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty(value = "物料分类")
    @TableField(exist = false)
    private Integer kind;

    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "物料规格")
    @TableField(exist = false)
    private String spec;

    @ApiModelProperty(value = "单价")
    @TableField(exist = false)
    private Double price;

    @ApiModelProperty(value = "总价")
    @TableField(exist = false)
    private Double sumPrice;

    @ApiModelProperty(value = "物资分类2--属性 字典dictCode=bu_material_attr")
    @Dict(dicCode = "bu_material_attr")
    @TableField(exist = false)
    private String category2;

    @ApiModelProperty(value = "来源库位 多个时用逗号分隔")
    @TableField(exist = false)
    private String sourceLocationName;

    @ApiModelProperty(value = "存放托盘名称 多个时用逗号分隔")
    @TableField(exist = false)
    private String palletName;

    @ApiModelProperty(value = "来源库位+数量+托盘（|分割） 多个时用逗号分隔")
    @TableField(exist = false)
    private String sourceLocationAndPalletName;

    @ApiModelProperty(value = "必换件替换编码")
    @TableField(exist = false)
    private String canReplace;

    @ApiModelProperty(value = "必换件替换编码描述")
    @TableField(exist = false)
    private String canReplaceSpec;

    @ApiModelProperty(value = "车号")
    @TableField(exist = false)
    private String trainNo;

    @ApiModelProperty(value = "工单类型")
    @TableField(exist = false)
    @Dict(dicCode = "bu_order_type")
    private Integer orderType;

    @ApiModelProperty(value = "工单编码")
    @TableField(exist = false)
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String orderName;

    @ApiModelProperty(value = "工单状态 字典dicCode=bu_order_status")
    @Dict(dicCode = "bu_order_status")
    @TableField(exist = false)
    private Integer orderStatus;

    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "工位号")
    @TableField(exist = false)
    private String workstationNo;

    @ApiModelProperty(value = "工位名称")
    @TableField(exist = false)
    private String workstationName;

    @ApiModelProperty(value = "发放数量")
    @TableField(exist = false)
    private BigDecimal issueAmount;

    @ApiModelProperty(value = "分配数量")
    @TableField(exist = false)
    private BigDecimal assignAmount;

    @ApiModelProperty(value = "分配明细")
    @TableField(exist = false)
    private List<BuMaterialAssignDetail> assignDetailList;

    @ApiModelProperty(value = "实际库存记录")
    @TableField(exist = false)
    private List<BuWorkOrderMaterialActs> materialActsList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
