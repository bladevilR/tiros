package org.jeecg.modules.group.toolequipment.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 工装信息/工器具信息查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
@Data
public class BuMaterialToolsQueryVO {

    @ApiModelProperty(value = "工装名称/工装编码")
    private String searchText;

    @ApiModelProperty(value = "状态 字典dictCode=bu_tools_status")
    private Integer status;

    @ApiModelProperty(value = "所属工班(班组)id",required = true)
    @NotBlank
    private String groupId;

}
