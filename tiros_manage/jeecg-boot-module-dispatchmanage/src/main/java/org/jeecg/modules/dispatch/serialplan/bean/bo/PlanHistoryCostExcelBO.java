package org.jeecg.modules.dispatch.serialplan.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 列计划历史成本excel导入bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-08-01
 */
@Data
@Accessors(chain = true)
public class PlanHistoryCostExcelBO {

    private String systemShortName;
    private String workstationNo;
    private String materialTypeCode;
    private String userCategoryStr;
    private String amountStr;
    private String unitPriceStr;
    private String remarkStr;

    private int sheetIndex;
    private int rowIndex;

}
