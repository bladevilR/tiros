package org.jeecg.modules.dispatch.planform.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 列计划在线表单(数据记录表)查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-18
 */
@Data
@Accessors(chain = true)
public class BuPlanOnlineFormQueryVO {

    @ApiModelProperty(value = "列计划在线表单(数据记录表)id", required = true)
    @NotBlank
    private String formInstId;

    @ApiModelProperty(value = "任务与表单实例的关联关系id")
    private String task2InstId;

    @ApiModelProperty(value = "是否需要数据值记录")
    private Boolean needValues;

}
