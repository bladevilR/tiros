package org.jeecg.modules.material.stock.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * @author yyg
 */
@Data
public class GroupStockAtrrVO {
    @ApiModelProperty(value = "库存id，多个用逗号分隔")
    private String ids;

    @ApiModelProperty(value = "所属系统id")
    private String systemId;

    @ApiModelProperty(value = "工位id")
    private String workstationId;

    @ApiModelProperty(value = "类别")
    @Dict(dicCode = "bu_material_type")
    private Integer useCategory;

}
