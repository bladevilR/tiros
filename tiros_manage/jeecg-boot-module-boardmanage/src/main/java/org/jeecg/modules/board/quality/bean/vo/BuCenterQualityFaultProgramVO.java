package org.jeecg.modules.board.quality.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 质量看板故障统计--修程
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Data
@Accessors(chain = true)
public class BuCenterQualityFaultProgramVO {

    @ApiModelProperty(value = "车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "修程名称")
    private String programName;

    @ApiModelProperty(value = "架修期故障数")
    private Integer sum1;

    @ApiModelProperty(value = "架修期AB故障")
    private Integer sum2;

    @ApiModelProperty(value = "质保期故障")
    private Integer sum3;

    @ApiModelProperty(value = "质保期正线故障")
    private Integer sum4;

    @ApiModelProperty(value = "质保期AB故障")
    private Integer sum5;

    @ApiModelProperty(value = "质保期有责故障")
    private Integer sum6;

    @ApiModelProperty(value = "出保期故障")
    private Integer sum7;

}
