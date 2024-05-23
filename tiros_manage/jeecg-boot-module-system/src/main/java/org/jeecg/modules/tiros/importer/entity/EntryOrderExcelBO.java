package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 导入入库数据vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-10
 */
@Data
@Accessors(chain = true)
public class EntryOrderExcelBO {

    private String entryOrder;
    private Date entryDate;
    private String materialCode;
    private String materialDesc;
    private String amount;
    private String price;
    private String totalPrice;
    private String departName;
    private String ebsLevel2;
    private String ebsLevel3;
    private String category3;
    private String isAsset;
    private String isConsume;

}
