package org.jeecg.modules.material.stock.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 物资库存查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
@Data
public class BuMaterialStockQueryVO {

    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @ApiModelProperty(value = "物资编码或者名称")
    private String searchText;

    @ApiModelProperty(value = "物料分类 1必换件 2故障件(偶换件) 3耗材  字典bu_material_type")
    private Integer materialType;

    @ApiModelProperty(value = "物料属性 1紧固件 2备品备件 3车体 4车上电气 5辅助 6牵引 7制定 8转向架   字典bu_material_attr")
    private String materialAttr;

    @ApiModelProperty(value = "仓库wbs，用于查询下级仓库库存")
    private String wbs;


    @ApiModelProperty(value = "物资id列表，后端根据searchText求得")
    private List<String> searchMaterialTypeIdList;

}
