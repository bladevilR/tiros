package org.jeecg.modules.basemanage.org.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 作业记录人员bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/3
 */
@Data
@Accessors(chain = true)
public class UserWorkRecordReduDetailBO {
    /**
     * 工单id
     */
    private String orderId;
    /**
     * 工单任务id
     */
    private String orderTaskId;
    /**
     * 工位id
     */
    private String workstationId;
    /**
     * 任务人员安排id
     */
    private String staffArrangeId;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 规程明细id
     */
    private String reguDetailId;

}
