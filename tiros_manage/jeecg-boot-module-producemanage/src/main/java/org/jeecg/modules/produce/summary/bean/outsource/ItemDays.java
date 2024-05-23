package org.jeecg.modules.produce.summary.bean.outsource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 各项目维修天数
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class ItemDays {

    @ApiModelProperty(value = "项目")
    private String itemName;

    @ApiModelProperty(value = "各车辆维修天数")
    private List<TrainDays> trainList;

}
