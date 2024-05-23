package org.jeecg.modules.board.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 物资预警查看VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/17
 */
@Data
@Accessors(chain = true)
public class BuMaterialAlertVO {

    @ApiModelProperty("序号")
    private Integer order;

    @ApiModelProperty("物资id")
    private String materialId;

    @ApiModelProperty("物资编码")
    private String materialCode;

    @ApiModelProperty("物资名称")
    private String materialName;

    @ApiModelProperty("安全库存")
    private Double alertStock;

    @ApiModelProperty("当前库存")
    private Double currentStock;

    @ApiModelProperty("偏差")
    private Double diffStock;

}
