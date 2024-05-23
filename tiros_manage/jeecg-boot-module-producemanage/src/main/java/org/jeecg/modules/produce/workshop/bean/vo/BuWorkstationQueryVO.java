package org.jeecg.modules.produce.workshop.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工位查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/30
 */
@Data
@Accessors(chain = true)
public class BuWorkstationQueryVO {

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "工位编号")
    private String workstationNo;

    @ApiModelProperty(value = "作业状态 1作业正常 2发现故障 3无作业")
    private Integer workStatus;

}
