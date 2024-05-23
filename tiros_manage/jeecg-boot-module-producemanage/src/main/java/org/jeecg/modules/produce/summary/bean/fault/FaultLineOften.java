package org.jeecg.modules.produce.summary.bean.fault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 线路惯性故障
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class FaultLineOften {

    @ApiModelProperty(value = "原因描述")
    private String reasonDesc;

    @ApiModelProperty(value = "处理措施描述")
    private String solutionDesc;

    @ApiModelProperty(value = "故障列表")
    private List<DetailFault> faultList;

    @ApiModelProperty(value = "涉及部件比例")
    private String faultAssetPercent;

}
