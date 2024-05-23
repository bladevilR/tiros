package org.jeecg.modules.tiros.custom.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Set;

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
public class CostDetailDiffItem {

    private String systemName;
    private BigDecimal amount;
    private String workstationName;
    private String workstationNo;
    private Integer useCategory;

    private String groupId;
    private Set<String> orderMaterialIdSet;

}
