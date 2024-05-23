package org.jeecg.modules.produce.trainhistory.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainStructureDetail;

import java.util.List;

/**
 * <p>
 * 车辆履历树--车辆
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/3
 */
@Data
@Accessors(chain = true)
public class BuTrainHistoryTreeTrainVO {

    @ApiModelProperty(value = "车辆id")
    private String trainId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "结构明细树")
    private List<BuTrainStructureDetailTreeVO> structureDetailTree;

    @ApiModelProperty(value = "结构明细树")
    private List<BuTrainHistoryTreeAssetVO> assets;

}
