package org.jeecg.modules.produce.summary.bean.progress;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 生产进度情况-本列车
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/5
 */
@Data
@Accessors(chain = true)
public class ProgressCurrent {

    @ApiModelProperty(value = "作业分类")
    private String categoryName;

    @ApiModelProperty(value = "作业项目")
    private List<String> itemNameList;

    @ApiModelProperty(value = "逾期天数 0=按计划完成 xx=延期xx天完成 -xx=提前xx天完成")
    private Integer delayDays;

    @ApiModelProperty(value = "计划和实际工作日对比")
    private List<WorkItem> dayCompareList;

}
