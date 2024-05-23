package org.jeecg.modules.third.common.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 故障差异bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-03-26
 */
@Data
@Accessors(chain = true)
public class FaultCompareBO {

    private String jdxFaultSn;
    private String jdxFaultStatus;
    private String jdxFaultStatusText;
    private String jdxFaultFromType;
    private String jdxFaultFromTypeText;

    private String maximoFaultSn;
    private String maximoFaultStatus;
    private String maximoFaultStatusText;

    private Integer same;
    private String sameText;
    private Integer jdxExist;
    private Integer maximoExist;

}
