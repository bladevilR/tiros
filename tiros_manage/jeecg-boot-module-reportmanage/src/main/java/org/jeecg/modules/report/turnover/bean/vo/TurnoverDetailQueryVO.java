package org.jeecg.modules.report.turnover.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQuery;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 周转件明细查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/3/31
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TurnoverDetailQueryVO extends TimeQuery {

    @ApiModelProperty(value = "线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "车辆号", required = true)
    @NotBlank
    private String trainNo;

}
