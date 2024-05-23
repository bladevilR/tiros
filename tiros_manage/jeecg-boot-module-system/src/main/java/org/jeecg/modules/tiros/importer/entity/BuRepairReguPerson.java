package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 规程人员需求
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuRepairReguPerson implements Serializable {
    private static final long serialVersionUID = 1L;


    private String id;

    private String reguDetailId;
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
