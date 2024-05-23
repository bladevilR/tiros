package org.jeecg.modules.outsource.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
/**
 * @author yyg
 */
@Data
public class BuOutsourceAsset implements Serializable {
    @ApiModelProperty(value ="序号")
    private Integer  no;
    @ApiModelProperty(value = "部件名称")
    private String assetName;
    @ApiModelProperty(value = "送出数量")
    private Integer sendAmount;
    @ApiModelProperty(value = "返厂数量")
    private Integer returnAmount;
    @ApiModelProperty(value = "报废数量")
    private Integer failedAmount;
    @ApiModelProperty(value = "部件描述")
    private String  assetDesc;
    @ApiModelProperty(value = "所属系统")
    private String sysName;


}
