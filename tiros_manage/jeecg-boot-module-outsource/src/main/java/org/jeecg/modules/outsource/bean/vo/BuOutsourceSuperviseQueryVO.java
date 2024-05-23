package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuOutsourceSuperviseQueryVO implements Serializable {

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "线路，字典 bu_mtr_line,line_name,line_id")
    private String lineId;

    @ApiModelProperty(value = "状态，字典 bu_supervise_status")
    private Integer status;

}
