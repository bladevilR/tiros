package org.jeecg.modules.tiros.importer.bean.plan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 所需工器具
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuTpRepairPlanTool{
    private String id;
    private String taskId;
    /**
     * 工期具类型id
     */
    private String toolTypeId;
    private Double num;
    private String name;
    private Integer kind;
    private String unit;
    private String code;
}
