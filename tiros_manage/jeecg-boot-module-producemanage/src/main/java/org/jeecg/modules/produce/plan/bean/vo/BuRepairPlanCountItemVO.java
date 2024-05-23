package org.jeecg.modules.produce.plan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/10
 */
@Data
@Accessors(chain = true)
public class BuRepairPlanCountItemVO {

    @ApiModelProperty(value = "标题 必换件/委外件/工时/故障数...")
    private String title;

    @ApiModelProperty(value = "总数")
    private Double count;

    @ApiModelProperty(value = "已完成")
    private Double finish;

    @ApiModelProperty(value = "详情")
    private Object details;

}
