package org.jeecg.modules.report.fault.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 车辆故障统计
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/8
 */
@Data
@Accessors(chain = true)
public class FaultTrainCountVO {

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "架修期")
    private Integer repair;

    @ApiModelProperty(value = "质保期")
    private Integer warranty;

    @ApiModelProperty(value = "质保期正线")
    private Integer warrantyOnline;

    @ApiModelProperty(value = "质保期AB类")
    private Integer warrantyAB;

    @ApiModelProperty(value = "质保期B类以上")
    private Integer warrantyAboveB;

    @ApiModelProperty(value = "质保期有责故障")
    private Integer warrantyHasDuty;

    @ApiModelProperty(value = "出保期故障")
    private Integer expireWarranty;

    /**
     * 序号
     */
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String trainSort;

}
