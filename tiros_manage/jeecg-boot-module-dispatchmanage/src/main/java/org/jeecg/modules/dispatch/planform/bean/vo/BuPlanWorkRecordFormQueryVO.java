package org.jeecg.modules.dispatch.planform.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 列计划作业记录表查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-18
 */
@Data
@Accessors(chain = true)
public class BuPlanWorkRecordFormQueryVO {

    @ApiModelProperty(value = "作业记录表实列id", required = true)
    @NotBlank
    private String formInstId;

    @ApiModelProperty(value = "任务与表单实例的关联关系id")
    private String task2InstId;

    @ApiModelProperty(value = "是否需要检查信息")
    private Boolean needChecks;

    @ApiModelProperty(value = "是否需要明细分类 true:作业明细分到categoryList下，false:作业明细在detailList")
    private Boolean needCategory;

    @ApiModelProperty(value = "工单任务id 传参会根据工单任务过滤明细信息")
    private String orderTaskId;
    
    @ApiModelProperty(value = "是否需要作业表单所有项")
    private Boolean allItems;

}
