package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQuery;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuOutsourceAssetQueryVO extends TimeQuery implements Serializable {
    @ApiModelProperty(value = "线路id")
    private String lineId;
    @ApiModelProperty(value = "车辆")
    private String trainNo;
    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;
}
