package org.jeecg.modules.produce.summary.bean.progress;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.produce.summary.bean.PlanBase;

import java.util.List;

/**
 * <p>
 * 生产进度情况-近xx列车
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/5
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ProgressLast extends PlanBase {

    @ApiModelProperty(value = "开始日期提前天数")
    private Integer startTimeEarlierDays;

    @ApiModelProperty(value = "结束日期提前天数")
    private Integer endTimeEarlierDays;

    @ApiModelProperty(value = "正在进行的作业项目")
    private String workingItemName;

    @ApiModelProperty(value = "维修进度（完成的作业项目）")
    private List<WorkItem> finishItemList;

    //@ApiModelProperty(value = "维修进度（日期）")
   // private List<String> finishDayList;

    @ApiModelProperty(value = "完成情况")
    private String progressStatus;
}
