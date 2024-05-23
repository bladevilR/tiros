package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuOutsourceQualityQueryVO implements Serializable {
    @ApiModelProperty(value = "部件编码或者名称")
    private String searchText;
    @ApiModelProperty(value = "线路")
    private String lineId;
    @ApiModelProperty(value = "车辆编号")
    private String trainNo;
    @ApiModelProperty(value = "设备id")
    private String assetTypeId;
}
