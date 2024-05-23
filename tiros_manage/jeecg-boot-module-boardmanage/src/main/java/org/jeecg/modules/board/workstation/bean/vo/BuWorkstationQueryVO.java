package org.jeecg.modules.board.workstation.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工位看板（车间）--工位查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/18
 */
@Data
@Accessors(chain = true)
public class BuWorkstationQueryVO {

    @ApiModelProperty(value = "车辆段id")
    private String depotId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "过滤只显示已关联工班的工位")
    private Boolean filterGroupRelated;

//    @ApiModelProperty(value = "是否返回扩展信息(任务+必换件+故障+作业人数)")
//    private Boolean needData;

}
