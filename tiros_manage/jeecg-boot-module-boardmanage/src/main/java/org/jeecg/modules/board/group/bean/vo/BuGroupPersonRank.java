package org.jeecg.modules.board.group.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 班组人员排名
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
@Data
@Accessors(chain = true)
public class BuGroupPersonRank {

    @ApiModelProperty(value = "序号")
    private Integer sortNo;

    @ApiModelProperty(value = "人员id")
    private String userId;

    @ApiModelProperty(value = "人员工号")
    private String workNo;

    @ApiModelProperty(value = "人员姓名")
    private String userRealName;

    @ApiModelProperty(value = "故障数")
    private Integer faultAmount;

    @ApiModelProperty(value = "工时")
    private BigDecimal workTime;

}
