package org.jeecg.modules.report.cost.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Set;

/**
 * <p>
 * 物料消耗明细--物料
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-31
 */
@Data
@Accessors(chain = true)
public class BuCostDetailMaterialVO {

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "物资编码")
    private String materialTypeCode;

    @ApiModelProperty(value = "物资名称")
    private String materialTypeName;

    @ApiModelProperty(value = "规格型号")
    private String materialTypeSpec;

    @ApiModelProperty(value = "单位")
    private String materialTypeUnit;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer useCategory;

    @ApiModelProperty(value = "物资属性  字典dictCode=bu_material_attr")
    @Dict(dicCode = "bu_material_attr")
    private String category2;

    @ApiModelProperty(value = "用于统计数据的消耗数量")
    private BigDecimal consumeAmount;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "用于统计数据的消耗总金额")
    private BigDecimal consumeTotalPrice;

    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 工单物料ids
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String orderMaterialIds;

    /**
     * 系统名称
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String systemId;

    /**
     * 系统名称
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String systemName;

    /**
     * 工位id
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String workstationId;

    /**
     * 工位号
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String workstationNo;

    /**
     * 工位名称
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String workstationName;

    /**
     * 工班id
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String groupId;

    /**
     * 工单物料id集合
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Set<String> orderMaterialIdSet;

}
