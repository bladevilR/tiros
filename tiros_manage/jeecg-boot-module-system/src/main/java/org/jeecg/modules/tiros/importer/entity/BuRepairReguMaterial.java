package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 规程额定物料
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuRepairReguMaterial  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String reguDetailId;
    private String materialTypeId;
    private Double amount;

}
