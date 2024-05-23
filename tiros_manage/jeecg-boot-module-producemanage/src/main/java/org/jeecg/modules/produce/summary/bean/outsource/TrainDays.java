package org.jeecg.modules.produce.summary.bean.outsource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 各车辆维修天数
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class TrainDays {

    @ApiModelProperty(value = "序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "天数")
    private Integer days;

    @ApiModelProperty(value = "项目",hidden = true)
    @JsonIgnore
    private String itemName;

}
