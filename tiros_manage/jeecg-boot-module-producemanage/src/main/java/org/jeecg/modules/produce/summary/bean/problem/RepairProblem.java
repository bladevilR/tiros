package org.jeecg.modules.produce.summary.bean.problem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 维修过程中存在的问题及处理措施
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class RepairProblem {

    @ApiModelProperty(value = "问题")
    private String problem;

    @ApiModelProperty(value = "处理措施")
    private String solution;

}
