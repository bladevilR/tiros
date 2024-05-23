package org.jeecg.modules.board.workstation.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工位作业人数信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/18
 */
@Data
@Accessors(chain = true)
public class BuWorkstationWorkerVO {

    @ApiModelProperty(value = "作业人数")
    private Integer quantity;

    @ApiModelProperty(value = "已安排作业人数")
    private Integer arranged;

    @ApiModelProperty(value = "未安排作业人数")
    private Integer notArranged;

}
