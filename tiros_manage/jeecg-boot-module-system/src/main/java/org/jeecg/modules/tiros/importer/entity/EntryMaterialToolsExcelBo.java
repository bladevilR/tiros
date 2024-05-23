package org.jeecg.modules.tiros.importer.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 导入入库数据vo
 * </p>
 *
 * @author yangfengyu
 * @since 2021-05-14
 */
@Data
@Accessors(chain = true)
public class EntryMaterialToolsExcelBo {

    private String code;

    private String assetCode;

    private String name;

    private String model;

    private Integer isAsset;

    private String desc;

    private Integer isConsume;

    private String workShopName;

    private String userName;

    private String workGroupName;

    private String price;

    private Integer amount;

    private String status;

    private Integer toolType;
}
