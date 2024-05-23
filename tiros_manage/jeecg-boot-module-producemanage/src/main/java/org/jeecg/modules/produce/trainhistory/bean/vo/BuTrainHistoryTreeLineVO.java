package org.jeecg.modules.produce.trainhistory.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 车辆履历树--线路
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/3
 */
@Data
@Accessors(chain = true)
public class BuTrainHistoryTreeLineVO {

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "车辆")
    private List<BuTrainHistoryTreeTrainVO> trains;

}
