package org.jeecg.modules.basemanage.track.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 股道信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuMtrTrackQueryVO implements Serializable {


    @ApiModelProperty(value = "所属车辆段ID")
    private String depotId;
    @ApiModelProperty(value = "股道编号")
    private String code;


}
