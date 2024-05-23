package org.jeecg.modules.report.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 物料消耗明细--系统
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/11
 */
@Data
@Accessors(chain = true)
public class BuCostDetailSystemVO {

    @ApiModelProperty(value = "系统id")
    private String sysId;

    @ApiModelProperty(value = "系统编码")
    private String sysCode;

    @ApiModelProperty(value = "系统名称")
    private String sysName;

    @ApiModelProperty(value = "工位数据列表")
    private List<BuCostDetailWorkstationVO> workstationList;

    @ApiModelProperty(value = "系统小计")
    private BigDecimal sysTotalPrice;

}
