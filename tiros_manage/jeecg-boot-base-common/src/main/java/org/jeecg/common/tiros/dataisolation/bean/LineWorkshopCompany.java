package org.jeecg.common.tiros.dataisolation.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 线路车间公司信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/8/26
 */
@Data
@Accessors(chain = true)
public class LineWorkshopCompany {

    private String lineId;
    private String workshopId;
    private String companyId;
    private String depotId;

}
