package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuMaterialApplyBO {
    private String materialTypeCode;
    private String workOrderCode;
    private Double price;
}
