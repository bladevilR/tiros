package org.jeecg.modules.basemanage.layout.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 桌面部件查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
@Data
@Accessors(chain = true)
public class BuSysWidgetsQueryVO {

    @ApiModelProperty(value = "所属分类id 来自系统分类表sys_category")
    private String widgetCategory;

    @ApiModelProperty(value = "部件名称")
    private String widgetName;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

}
