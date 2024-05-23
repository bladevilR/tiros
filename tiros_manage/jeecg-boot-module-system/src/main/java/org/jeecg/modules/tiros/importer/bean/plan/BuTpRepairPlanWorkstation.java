package org.jeecg.modules.tiros.importer.bean.plan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 任务工位
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuTpRepairPlanWorkstation {
    private static final long serialVersionUID = 1L;
    private String id;
    private String taskId;
    private String workstationId;

}
