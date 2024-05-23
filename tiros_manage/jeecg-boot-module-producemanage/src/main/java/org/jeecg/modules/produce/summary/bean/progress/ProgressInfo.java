package org.jeecg.modules.produce.summary.bean.progress;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 生产进度控制情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class ProgressInfo {

    @ApiModelProperty(value = "本列车")
    private ProgressCurrent current;

    @ApiModelProperty(value = "近两列车")
    private List<ProgressLast> lastTwo;

}
