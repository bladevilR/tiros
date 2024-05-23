package org.jeecg.modules.material.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 物料消耗对比 结果详情vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
@Data
@Accessors(chain = true)
public class CompareResultVO {

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "列计划名称")
    private String planName;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "物资id")
    private String materialId;

    @ApiModelProperty(value = "物资编码")
    private String materialCode;

    @ApiModelProperty(value = "物资名称")
    private String materialName;

    @ApiModelProperty(value = "物资描述")
    private String materialSpec;

    @ApiModelProperty(value = "是否必换件清单 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isMustList;

    @ApiModelProperty(value = "必换件清单数量")
    private BigDecimal mustListAmount;

    @ApiModelProperty(value = "额定系统id")
    private String requireSystemId;

    @ApiModelProperty(value = "额定系统名称")
    private String requireSystemName;

    @ApiModelProperty(value = "额定工位id")
    private String requireWorkstationId;

    @ApiModelProperty(value = "额定工位号")
    private String requireWorkstationNo;

    @ApiModelProperty(value = "额定工位名称")
    private String requireWorkstationName;

    @ApiModelProperty(value = "额定系统工位信息")
    private String requireSystemWorkstationInfo;

    @ApiModelProperty(value = "可替换物资")
    private String canReplace;

    @ApiModelProperty(value = "用途类型 字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private String useCategory;

//    @ApiModelProperty(value = "系统id")
//    private String systemId;
//
//    @ApiModelProperty(value = "系统名称")
//    private String systemName;
//
//    @ApiModelProperty(value = "工位id")
//    private String workstationId;
//
//    @ApiModelProperty(value = "工位号")
//    private String workstationNo;
//
//    @ApiModelProperty(value = "工位名称")
//    private String workstationName;

    @ApiModelProperty(value = "额定系统工位信息")
    private String consumeSystemWorkstationInfo;

    @ApiModelProperty(value = "额定数量")
    private BigDecimal needAmount;

    @ApiModelProperty(value = "领用数量")
    private BigDecimal receiveAmount;

    @ApiModelProperty(value = "实际消耗")
    private BigDecimal consumeAmount;

    @ApiModelProperty(value = "是否存在差异 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer amountIsDiff;

//    @ApiModelProperty(value = "是否核实  字典dictCode=bu_state")
//    @Dict(dicCode = "bu_state")
//    private Integer isVerify;

    @ApiModelProperty(value = "工单物料，用于查询核实明细和批量核实")
    private List<BuWorkOrderMaterial> orderMaterialList;

    @ApiModelProperty(value = "工单物料ids，用于查询核实明细和批量核实")
    private String orderMaterialIds;

    @ApiModelProperty(value = "领用明细，用于查询核实明细和批量核实")
    private List<BuMaterialApplyDetail> applyDetailList;

    @ApiModelProperty(value = "领用明细ids，用于查询核实明细和批量核实")
    private String applyDetailIds;


    @ApiModelProperty(value = "发料工单信息，用于导出核实明细")
    private String applyOrderInfo;

    @ApiModelProperty(value = "消耗工单信息，用于导出核实明细")
    private String consumeOrderInfo;

    @ApiModelProperty(value = "修程类型，用于导出核实明细")
    private String repairProNames;

    @ApiModelProperty(value = "系统，用于导出核实明细")
    private String systemNames;

    @ApiModelProperty(value = "使用类型，用于导出核实明细")
    private String useCategoryNames;

    @ApiModelProperty(value = "物资属性，用于导出核实明细")
    private String materialTypeCategory3;


//    @ApiModelProperty(value = "批次号，多个逗号分隔")
//    private String tradeNos;
//
//    @ApiModelProperty(value = "物资单价，多个逗号分隔")
//    private String unitPrices;

//    @ApiModelProperty(value = "领用日期")
//    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private Date receiveDate;
//
//    @ApiModelProperty(value = "修程名称")
//    private String repairProName;
//
//    @ApiModelProperty(value = "系统名称")
//    private String sysName;
//
//    @ApiModelProperty(value = "物资属性")
//    private String category;
//
//    @ApiModelProperty(value = "工单物料id")
//    private String orderMaterialId;
//
//    @ApiModelProperty(value = "领用明细id")
//    private String applyDetailId;
//
//    @ApiModelProperty(value = "工单id")
//    private String orderId;
//
//    @ApiModelProperty(value = "工单编码")
//    private String orderCode;
//
//    @ApiModelProperty(value = "工单名称")
//    private String orderName;

}
