package org.jeecg.common.tiros.cache.workstation;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工位信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-07-31
 */
@Data
@Accessors(chain = true)
public class WorkstationBO {

    private String id;
    private String workstationNo;
    private String workstationName;
    private String workshopId;
    private String groupId;

    private String companyId;
    private String lineId;
    private String location;
    private String content;
    private Integer status;
    private String remark;

}
