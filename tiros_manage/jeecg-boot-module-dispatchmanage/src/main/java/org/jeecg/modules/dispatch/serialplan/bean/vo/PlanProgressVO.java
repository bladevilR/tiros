package org.jeecg.modules.dispatch.serialplan.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 列计划进度vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/16
 */
@Data
@Accessors(chain = true)
public class PlanProgressVO {

    @ApiModelProperty(value = "所有任务数")
    private Integer total;

    @ApiModelProperty(value = "未完成任务数")
    private Integer finished;

    @ApiModelProperty(value = "未完成任务数")
    private Integer unFinished;

    @ApiModelProperty(value = "未完成任务集合")
    private List<PlanProgressTaskVO> unFinishedList;

}
