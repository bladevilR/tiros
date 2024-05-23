package org.jeecg.modules.system.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 重复校验过滤条件字段vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-21
 */
@Data
@Accessors(chain = true)
@ApiModel("重复校验过滤条件字段")
public class DuplicateCheckFieldVO {

    /**
     * 字段名
     */
    @ApiModelProperty(value = "字段名", example = "dict_name")
    private String name;

    /**
     * 字段值
     */
    @ApiModelProperty(value = "字段值", example = "状态")
    private String val;

}
