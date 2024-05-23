package org.jeecg.modules.tiros.importer.bean.plan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 所需物料
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class BuTpRepairPlanMaterial{
    private static final long serialVersionUID = 1L;
    private String id;
    private String taskId;

    /**
     * 物料类型id
     */
    private String materialTypeId;
    private Double num;
    private String name;
    private Integer kind;
    private String unit;
    private String code;

}
