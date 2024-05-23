package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 规程表单
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuRepairReguForm implements Serializable {

    private static final long serialVersionUID = 1L;


    private String id;

    private String reguDetailId;

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
