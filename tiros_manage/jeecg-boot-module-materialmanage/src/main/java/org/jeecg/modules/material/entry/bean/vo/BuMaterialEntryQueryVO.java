package org.jeecg.modules.material.entry.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 入库信息查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
@Data
public class BuMaterialEntryQueryVO {

    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @ApiModelProperty(value = "物资编码或者名称")
    private String searchText;

    @ApiModelProperty(value = "物料分类 字典bu_material_type")
    private String materialCategory;

    @ApiModelProperty(value = "是否新增 字典dictCode=bu_entryClass_type 1新增2已有 不传参表示所有 ")
    private Integer entryClass;

    @ApiModelProperty(value = "入库确认 字典dicCode=bu_state 0否1是")
    @Dict(dicCode = "bu_state")
    private Integer confirm;

}
