package org.jeecg.modules.tiros.custom.bean.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 修正物料消耗bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-13
 */
@Data
@Accessors(chain = true)
public class MaterialConsumeCorrect {

    @ApiModelProperty(value = "工班id", required = true)
    private String groupId;

    @ApiModelProperty(value = "工单号", required = true)
    private String orderCode;

    @ApiModelProperty(value = "物资类型id", required = true)
    private String materialTypeId;

    @ApiModelProperty(value = "物资使用类型")
    private Integer useCategory;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "指定批次号")
    private String assignTradeNo;

    @ApiModelProperty(value = "处理类型：1新增 2修改 3删除", required = true)
    private Integer type;


    /**
     * 序号，excel导入生成时用，用于对应生成的sql位置
     */
    @ApiModelProperty(hidden = true)
    private Integer index;
    /**
     * 生成的sql
     */
    @ApiModelProperty(hidden = true)
    private String correctSql;

}
