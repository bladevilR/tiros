package org.jeecg.modules.produce.kpi.bean.vo;

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
public class BuKpiFaultItemVO {

    @ApiModelProperty(value = "人员id")
    private String userId;

    @ApiModelProperty(value = "人员姓名")
    private String userName;

    @ApiModelProperty(value = "工号")
    private String workNo;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "故障次数总计 为发现故障数+处理故障数")
    private Integer totalHappen;

    @ApiModelProperty(value = "发现故障数")
    private Integer faultAmount;

    @ApiModelProperty(value = "处理故障数")
    private Integer handleAmount;

    @ApiModelProperty(value = "排名")
    private Integer sortNo;

}
