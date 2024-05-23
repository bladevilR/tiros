package org.jeecg.modules.dispatch.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 故障分类查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-29
 */
@Data
@Accessors(chain = true)
public class BuFaultCategoryQueryVO {

    @ApiModelProperty(value = "编码或描述")
    private String searchText;

}
