package org.jeecg.modules.basemanage.tpplan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuTpRepairPlanQueryVO implements Serializable {
    @ApiModelProperty(value = "名称或者编码")
    private String searchContent;
    @ApiModelProperty(value = "0 无效  1 有效")
    private Integer status;
    @ApiModelProperty(value = "修程类型id")
    private String repairProgramId;
    @ApiModelProperty(value = "所属车型id")
    private String trainTypeId;
}
