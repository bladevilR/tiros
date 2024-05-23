package org.jeecg.modules.tiros.importer.bean.plan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 所需人员
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuTpRepairPlanPerson {
    private static final long serialVersionUID = 1L;

    private String id;
    private String taskId;

    private Integer amount;

    /**
     * 要求人员的岗位，可以多个岗位，逗号分隔
     */
    private String requirePostion;

    /**
     * 证书要求
     */
    private String requireCertificate;

    /**
     * 技能要求
     */
    private String requireTech;

}
