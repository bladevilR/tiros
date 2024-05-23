package org.jeecg.modules.material.turnovernew.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 周转件查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-09-18
 */
@Data
public class BuMaterialTurnoverQueryVONew {

    @ApiModelProperty(value = "名称或编码")
    private String searchText;

    @ApiModelProperty(value = "所属线路id  字典dictCode=(bu_mtr_line,line_name,line_id)")
    private String lineId;

}
