package org.jeecg.modules.produce.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 故障信息比较分析结果vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Data
@Accessors(chain = true)
public class BuFaultInfoCompareResultVO {

    @ApiModelProperty(value = "是否进行比较分析（默认false） false时不进行比较分析，只根据查询条件1返回查询结果1")
    private Boolean doCompare;

    @ApiModelProperty(value = "查询结果1标题")
    private String result1Title;

    @ApiModelProperty(value = "查询结果1")
    private List<FaultCountInfoVO> result1;

    @ApiModelProperty(value = "查询结果2标题")
    private String result2Title;

    @ApiModelProperty(value = "查询结果2")
    private List<FaultCountInfoVO> result2;

}
