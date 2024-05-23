package org.jeecg.modules.tiros.importer.bean.plan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 作业表单
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuTpRepairPlanForm  {
    private static final long serialVersionUID = 1L;

    private String id;
    private String taskId;
    /**
     * 1 在线表单  2 文件表单
     */
    private Integer formType;

    /**
     * 根据表单类型文件id或在线表单id
     */
    private String objId;
    private String remark;
    private String formName;
}
