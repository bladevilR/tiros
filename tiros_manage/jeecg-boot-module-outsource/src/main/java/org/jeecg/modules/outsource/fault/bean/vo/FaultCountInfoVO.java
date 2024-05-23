package org.jeecg.modules.outsource.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.util.List;

/**
 * <p>
 * 故障统计信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/22
 */
@Data
@Accessors(chain = true)
public class FaultCountInfoVO {

    @ApiModelProperty(value = "总数")
    private Integer total;

    @ApiModelProperty(value = "已处理数")
    private Integer handled;

    @ApiModelProperty(value = "未处理数")
    private Integer unhandled;

    @ApiModelProperty(value = "发现阶段 字典dictCode=bu_fault_phase")
    @Dict(dicCode = "bu_fault_phase")
    private Integer phase;

    @ApiModelProperty(value = "各系统的数量分布")
    private List<FaultCountSystemItemVO> systemItemList;

}
