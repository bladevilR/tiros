package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.util.List;

/**
 * <p>
 * 工单物料查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-28
 */
@Data
@Accessors(chain = true)
public class BuOrderMaterialQueryVO {

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "工单物料操作类型 字典dictCode=bu_order_material_op_type")
    @Dict(dicCode = "bu_order_material_op_type")
    private List<Integer> opTypes;

}
