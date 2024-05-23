package org.jeecg.modules.report.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 绩效-个人/班组贡献vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
@Data
@Accessors(chain = true)
public class BuKpiTimeItemVO {

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "人员id")
    private String userId;

    @ApiModelProperty(value = "人员姓名")
    private String userName;

    @ApiModelProperty(value = "工号")
    private String workNo;

    @ApiModelProperty(value = "总工时")
    private Integer totalTime;

    @ApiModelProperty(value = "维修工时")
    private Integer repairTime;

    @ApiModelProperty(value = "故障工时")
    private Integer faultTime;

    @ApiModelProperty(value = "排名")
    private Integer realSortNo;

}
