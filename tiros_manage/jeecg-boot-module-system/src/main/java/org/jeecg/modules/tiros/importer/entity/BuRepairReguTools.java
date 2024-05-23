package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 规程额定工器具，包括：工具、器具、工装
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuRepairReguTools implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String reguDetailId;
    private String toolTypeId;
    private Double amount;

}
