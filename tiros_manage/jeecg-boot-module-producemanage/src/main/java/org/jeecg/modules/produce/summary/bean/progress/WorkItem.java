package org.jeecg.modules.produce.summary.bean.progress;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 生产进度情况-作业项目
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/5
 */
@Data
@Accessors(chain = true)
public class WorkItem {

    @ApiModelProperty(value = "序号")
    private Integer sortNo;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "计划工作日")
    private Integer plannedDays;

    @ApiModelProperty(value = "实际工作日")
    private Integer actualDays;

}
