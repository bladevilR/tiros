package org.jeecg.modules.material.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 列计划物料核实查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/15
 */
@Data
@Accessors(chain = true)
public class CheckQueryVO {

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "工单号")
    private String orderCode;

    @ApiModelProperty(value = "物资编码或名称")
    private String materialSearchText;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer useCategory;

}
