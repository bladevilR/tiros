package org.jeecg.modules.dispatch.planform.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 检查记录表（实例）查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-18
 */
@Data
@Accessors(chain = true)
public class BuPlanCheckRecordFormQueryVO {

    @ApiModelProperty(value = "检查记录表实列id", required = true)
    @NotBlank
    private String formInstId;

    @ApiModelProperty(value = "任务与表单实例的关联关系id")
    private String task2InstId;

    @ApiModelProperty(value = "是否需要整改关联信息 默认是 整改关联信息在itemList下")
    private Boolean needRectify;

    @ApiModelProperty(value = "是否需要质量评定信息 默认是")
    private Boolean needJudge;

    @ApiModelProperty(value = "是否需要作业表单所有项")
    private Boolean allItems;

}
