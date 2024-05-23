package org.jeecg.modules.produce.summary.bean.outsource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 各车辆维修详情-车辆分组
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class TrainDetailGroup {

    @ApiModelProperty(value = "序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "各项目详情")
    private List<TrainDetailItem> itemList;

}
