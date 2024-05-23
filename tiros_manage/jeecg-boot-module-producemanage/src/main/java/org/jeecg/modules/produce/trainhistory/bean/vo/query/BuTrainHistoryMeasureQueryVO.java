package org.jeecg.modules.produce.trainhistory.bean.vo.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 车辆履历-测量记录 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuTrainHistoryMeasureQueryVO extends HistoryRecordsQueryVO {

    @ApiModelProperty(value = "测点名称")
    private String measureName;

}
