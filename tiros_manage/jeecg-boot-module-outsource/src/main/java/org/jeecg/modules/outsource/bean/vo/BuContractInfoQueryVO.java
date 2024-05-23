package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuContractInfoQueryVO implements Serializable {
    @ApiModelProperty(value = "合同id")
    private String id;
    @ApiModelProperty(value = "合同名称或者编号")
    private String searchText;
    @ApiModelProperty(value = "线路id，字典bu_mtr_line,line_name,line_id")
    private String lineId;
    @ApiModelProperty("厂商，字典bu_base_supplier,name,id")
    private String supplierId;
    @ApiModelProperty("状态，字典 bu_contract_status")
    private Integer status;
}
