package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuOutsourceOutinQueryVO implements Serializable {

    @ApiModelProperty(value = "部件名或者编号")
    private String assetSearchText;

    @ApiModelProperty(value = "交接单名称或者编号")
    private String billSearchText;

    @ApiModelProperty(value = "线路id，字典 bu_mtr_line,line_name,line_id")
    private String lineId;

    @ApiModelProperty(value = "车辆id，字典 bu_train_info,train_no,id")
    private String  trainId;

    private String id;

    @ApiModelProperty(value = "0出库单，1入库单")
    private Integer billType;

}
