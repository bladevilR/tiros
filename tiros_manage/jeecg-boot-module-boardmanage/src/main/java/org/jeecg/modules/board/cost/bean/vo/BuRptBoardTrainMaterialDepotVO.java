package org.jeecg.modules.board.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 物料消耗统计--车辆段
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/11
 */
@Data
@Accessors(chain = true)
public class BuRptBoardTrainMaterialDepotVO {

//    @ApiModelProperty(value = "车辆段id")
//    private String depotId;

    @ApiModelProperty(value = "车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "修程数据")
    private List<BuRptBoardTrainMaterialProgramVO> programs;

}
