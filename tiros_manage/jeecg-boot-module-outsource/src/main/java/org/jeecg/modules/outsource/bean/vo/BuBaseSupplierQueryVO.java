package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuBaseSupplierQueryVO implements Serializable {
    @ApiModelProperty(value = "厂商id")
    private String id;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "类别 字典 bu_vendor_category")
    private Integer category;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "设备类型id")
    private String objTypeId;
    @ApiModelProperty(value = "线路id")
    private String lineId;
    @ApiModelProperty(value = "修程id")
    private String repairProgramId;


}
