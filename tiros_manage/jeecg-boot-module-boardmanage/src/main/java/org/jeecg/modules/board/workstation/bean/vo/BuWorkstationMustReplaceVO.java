package org.jeecg.modules.board.workstation.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工位必换件信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/18
 */
@Data
@Accessors(chain = true)
public class BuWorkstationMustReplaceVO {

    @ApiModelProperty(value = "必换件数")
    private Integer total;

    @ApiModelProperty(value = "已换必换件数")
    private Integer replaced;

    @ApiModelProperty(value = "未换必换件数")
    private Integer notReplaced;

}
