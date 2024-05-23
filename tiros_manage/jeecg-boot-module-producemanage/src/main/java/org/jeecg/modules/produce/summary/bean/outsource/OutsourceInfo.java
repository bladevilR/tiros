package org.jeecg.modules.produce.summary.bean.outsource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 委外维修部件维修进度控制情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class OutsourceInfo {

    @ApiModelProperty(value = "项目合同天数")
    private List<ContractDays> contractDaysList;

    @ApiModelProperty(value = "近三列车各项目维修天数")
    private List<ItemDays> itemList;

    @ApiModelProperty(value = "近三列车车辆项目维修详情")
    private List<TrainDetailGroup> trainItemDetailList;

    @ApiModelProperty(value = "委外部件验收问题")
    private InProblemInfo inProblemInfo;

}
