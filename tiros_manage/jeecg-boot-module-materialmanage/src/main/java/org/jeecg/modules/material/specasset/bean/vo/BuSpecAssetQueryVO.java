package org.jeecg.modules.material.specasset.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 特种设备查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-02
 */
@Data
public class BuSpecAssetQueryVO {

    @ApiModelProperty(value = "名称/编码")
    private String searchText;

    @ApiModelProperty(value = "状态 字典dictCode=bu_tools_status")
    private Integer status;

    @ApiModelProperty(value = "所属工班id")
    private String groupId;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

}
