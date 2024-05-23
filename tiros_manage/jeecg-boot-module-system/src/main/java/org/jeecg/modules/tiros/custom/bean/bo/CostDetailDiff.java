package org.jeecg.modules.tiros.custom.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 物料消耗明细差异
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-31
 */
@Data
@Accessors(chain = true)
public class CostDetailDiff {

    private String materialTypeCode;
    private String materialTypeName;
    private String materialTypeSpec;
    private BigDecimal customerAmountSum;
    private String customerType;
    private BigDecimal programAmountSum;
    private String programType;

    private List<CostDetailDiffItem> customerList;
    private List<CostDetailDiffItem> programList;

    private String customerInfo;
    private String programInfo;

    private Boolean amountSumDiff;
    private Boolean systemWorkstationDiff;
    private Boolean useCategoryDiff;

    private String groupIds;
    private String orderMaterialIds;

}
