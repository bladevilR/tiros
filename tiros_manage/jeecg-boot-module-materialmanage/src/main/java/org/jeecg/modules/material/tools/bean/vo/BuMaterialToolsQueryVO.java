package org.jeecg.modules.material.tools.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Description: 工艺装备查询VO
 * @Author: ZhaiYanTao
 * @Date: 2020/9/15 10:58
 */
@Data
@Accessors(chain = true)
public class BuMaterialToolsQueryVO {

    @ApiModelProperty(value = "工装名称/工装编码")
    private String searchText;

    @ApiModelProperty(value = "类型 字典dictCode=bu_tools_type")
    private Integer toolType;

    @ApiModelProperty(value = "状态 字典dictCode=bu_tools_status")
    private Integer status;

    @ApiModelProperty(value = "所属工班(班组)id")
    private String groupId;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;
    @ApiModelProperty(hidden = true)
    private Date  nextCheckTime;

}
