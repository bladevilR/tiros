package org.jeecg.modules.produce.workshop.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/30
 */
@Data
@Accessors(chain = true)
public class BuWorkOrderTaskBO {
    private String orderId;
    private String groupId;
    private String taskId;
    private String taskContent;
    private Date taskStart;
    private String workstationId;
}
