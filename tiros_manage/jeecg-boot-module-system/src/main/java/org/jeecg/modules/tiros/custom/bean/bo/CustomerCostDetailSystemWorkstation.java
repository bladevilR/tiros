package org.jeecg.modules.tiros.custom.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 物料消耗明细系统工位
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-31
 */
@Data
@Accessors(chain = true)
public class CustomerCostDetailSystemWorkstation {

    private String systemShortName;
    private String workstationNo;
    private String workstationName;
    private String materialTypeCode;
    private BigDecimal materialTypePrice;
    private BigDecimal amount;
    private Integer useCategory;

    private String systemId;
    private String groupId;
    private String workstationId;

}
