package org.jeecg.modules.outsource.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yuyougen
 * @title: ContractPrice
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/311:22
 */
@Data
public class ContractPrice {
    @ApiModelProperty("合同id")
    private  String id;
    @ApiModelProperty("合同编号")
    private  String contractNo;
    @ApiModelProperty("合同名称")
    private  String contractName;
    @ApiModelProperty("车辆列数")
    private Integer trainAmount;
    @ApiModelProperty("单列价格")
    private BigDecimal singlePrice;
    @ApiModelProperty("部件价格")
    private BigDecimal partPrice;
    @ApiModelProperty("合同价格")
    private  BigDecimal contractAmount;
    @ApiModelProperty("签订时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date signDate;
}
