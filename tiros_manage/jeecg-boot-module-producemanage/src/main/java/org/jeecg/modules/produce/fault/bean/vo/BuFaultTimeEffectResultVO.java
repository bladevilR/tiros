package org.jeecg.modules.produce.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 架大修实效分析返回结果vo
 * 查询架修点前后故障数量对比
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-02
 */
@Data
@Accessors(chain = true)
public class BuFaultTimeEffectResultVO {

    @ApiModelProperty(value = "系统名称")
    private String name;

    @ApiModelProperty(value = "架修点")
    private Long jdx;

    @ApiModelProperty(value = "架修点前数据")
    private List<Long> before;

    @ApiModelProperty(value = "架修点后数据")
    private List<Long> after;

    @ApiModelProperty(value = "架修点对应时间点名称")
    private String jdxPeriodName;

    @ApiModelProperty(value = "架修点前数据对应时间点名称")
    private List<String> beforePeriodName;

    @ApiModelProperty(value = "架修点后数据对应时间点名称")
    private List<String> afterPeriodName;

}
