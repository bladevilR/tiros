package org.jeecg.modules.board.group.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 班组排序条目
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
@Data
@Accessors(chain = true)
public class BuGroupRankItem {

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "数量")
    private Integer amount;

    @ApiModelProperty(value = "序号")
    private Integer sortNo;

}
