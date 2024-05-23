package org.jeecg.modules.basemanage.layout.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 界面布局查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
@Data
@Accessors(chain = true)
public class BuSysLayoutsQueryVO {

    @ApiModelProperty(value = "布局名称")
    private String layoutName;

    @ApiModelProperty(value = "所属人员id")
    private String byUserId;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

}
