package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
public class BuOutsourcePerformQueryVO implements Serializable {
    @ApiModelProperty(value = "部件名或编号")
    private String searchText;
    @ApiModelProperty(value = "线路id")
    private String lineId;
    @ApiModelProperty(value = "车号")
    private String trainNo;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "合同id")
    private String contactId;
    @ApiModelProperty(value = "交接单详情id")
    private String id;

    @ApiModelProperty(value = "合同ids，多个逗号分割")
    private String contractIds;

    @ApiModelProperty(value = "合同id列表，contractIds转化来的，后端查询数据库时使用",hidden = true)
    private List<String> contractIdList;
     @ApiModelProperty(value = "厂商id")
    private String supplierId;

}
