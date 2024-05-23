package org.jeecg.modules.material.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 物料消耗对比查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
@Data
@Accessors(chain = true)
public class CompareQueryVO {

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "是否存在差异 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer amountIsDiff;

    @ApiModelProperty(value = "是否必换件清单 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isMustList;

    @ApiModelProperty(value = "物资编码或名称")
    private String materialSearchText;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer useCategory;

}
