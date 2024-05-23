package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 检查确认公共vo
 * </p>
 *
 * @author lidafeng
 * @since 2021/03/04
 */
@Data
@Accessors(chain = true)
public class CheckCommonVO {

    @ApiModelProperty(value = "记录明细id，多个id逗号分隔", required = true)
    @NotBlank
    private String detailIds;

    @ApiModelProperty(value = "检查类型  字典dictCode=bu_work_order_record_check_type", required = true)
    @Dict(dicCode = "bu_work_order_record_check_type")
    @NotNull
    private Integer type;

    @ApiModelProperty(value = "检查结果  字典dicCode=bu_work_order_record_check_result", required = true)
    @Dict(dicCode = "bu_work_order_record_check_result")
    @NotNull
    private Integer result;

    @ApiModelProperty(value = "结果描述")
    private String resultDesc;

    @ApiModelProperty(value = "是否忽略填写 0否1是 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer isIgnore;

    @ApiModelProperty(value = "忽略原因")
    private String ignoreDesc;

    @ApiModelProperty(value = "同检人员ids")
    private String togetherCheckUserIds;

    @ApiModelProperty(value = "同检人员名称s")
    private String togetherCheckUserNames;

}
