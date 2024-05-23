package org.jeecg.modules.board.quality.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 质量看板故障统计--车辆段
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Data
@Accessors(chain = true)
public class BuCenterQualityFaultDepotVO {

    @ApiModelProperty(value = "车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "修程数据")
    private List<BuCenterQualityFaultProgramVO> programs;

}
