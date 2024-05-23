package org.jeecg.modules.produce.kpi.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 组织贡献度查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-09
 */
@Data
@Accessors(chain = true)
public class KpiQueryVO {

    @ApiModelProperty(value = "车辆段id")
    private String depotId;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "工班id")
    private String groupId;

}
