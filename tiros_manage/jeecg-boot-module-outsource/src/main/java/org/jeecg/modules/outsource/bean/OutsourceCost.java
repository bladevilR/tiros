package org.jeecg.modules.outsource.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author yyg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "委外成本对象")
public class OutsourceCost {

    @ApiModelProperty("线路id")
    private String lineId;

    @ApiModelProperty("线路名称")
    private String lineName;

    @ApiModelProperty("修程id")
    private String repairProgramId;

    @ApiModelProperty("修程名称")
    private String repairProgramName;

    @ApiModelProperty("委外成本项")
    private List<OutsourcrCostItem> costItemList;

}
