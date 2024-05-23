package org.jeecg.modules.basemanage.traininfo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * 车辆信息查询vo
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
@Data
@Accessors(chain = true)
public class BuTrainInfoQueryVO {

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "车辆编号")
    private String trainNo;

    @ApiModelProperty(value = "车辆状态  字典dictCode=bu_train_status  0未上线1已上线")
    @Dict(dicCode = "bu_train_status")
    private Integer status;

}
