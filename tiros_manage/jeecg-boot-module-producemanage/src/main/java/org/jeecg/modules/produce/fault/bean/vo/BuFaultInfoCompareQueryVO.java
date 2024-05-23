package org.jeecg.modules.produce.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 故障信息比较分析查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Data
public class BuFaultInfoCompareQueryVO {

    @ApiModelProperty(value = "是否进行比较分析（默认false） false时不进行比较分析，只根据查询条件1返回查询结果1")
    private Boolean doCompare;

    @ApiModelProperty(value = "查询条件1")
    private BuFaultInfoQueryVO query1;

    @ApiModelProperty(value = "查询条件2")
    private BuFaultInfoQueryVO query2;

}
