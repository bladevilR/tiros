package org.jeecg.modules.material.material.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
public class BuMaterialTypeQueryVO implements Serializable {

    @ApiModelProperty(value = "物资编码或者名称")
    private String searchText;

    @ApiModelProperty(value = "物资类别/工器具类别 多个逗号分割")
    private String category1;

    @ApiModelProperty(value = "物资类型  字典dictCode=bu_material_kind  1物资 2工器具")
    private Integer type;

    @ApiModelProperty(value = "物资属性  字典dictCode=bu_material_attr")
    private Integer attr;

    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @ApiModelProperty(value = "物资编码列表")
    private List<String> materialTypeCodeList;

    /**
     * 物资类别/工器具类别 列表，由后端处理category1参数获得，用于查询
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private List<Integer> category1List;

    @ApiModelProperty(value = "物资id列表，后端根据searchText求得")
    private List<String> searchMaterialTypeIdList;

}
