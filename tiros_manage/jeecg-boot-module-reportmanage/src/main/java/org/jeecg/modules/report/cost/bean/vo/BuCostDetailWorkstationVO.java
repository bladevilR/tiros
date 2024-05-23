package org.jeecg.modules.report.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.report.cost.bean.BuWorkOrderMaterial;

import java.util.List;

/**
 * <p>
 * 物料消耗明细--工位
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/11
 */
@Data
@Accessors(chain = true)
public class BuCostDetailWorkstationVO {

    @ApiModelProperty(value = "工位id")
    private String workstationId;

    @ApiModelProperty(value = "工位号")
    private String workstationNo;

    @ApiModelProperty(value = "工位名称")
    private String workstationName;

    @ApiModelProperty(value = "物资消耗列表")
    private List<BuCostDetailMaterialVO> materialList;

}
