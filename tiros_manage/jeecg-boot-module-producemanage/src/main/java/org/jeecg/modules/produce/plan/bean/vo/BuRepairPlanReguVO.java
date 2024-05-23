package org.jeecg.modules.produce.plan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 计划监控-规程结构方式vo（规程作业项）
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-25
 */
@Data
@Accessors(chain = true)
public class BuRepairPlanReguVO {

    @ApiModelProperty(value = "序号")
    private String no;

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "完成进度 百分比 0~100的数字")
    private Double progress;

    @ApiModelProperty(value = "进度状态 字典dictCode=bu_progress_status")
    @Dict(dicCode = "bu_progress_status")
    private Integer progressStatus;

    @ApiModelProperty(value = "故障数量")
    private Integer faultCount;

}
