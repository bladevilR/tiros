package org.jeecg.modules.tiros.custom.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 实际消耗班组库存不足-工单
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-12
 */
@Data
@Accessors(chain = true)
public class ActConsumeOrderLack {

    private String orderCode;
    private String orderName;
    private Integer orderStatus;
    private List<ActConsumeMaterialLack> materialLackList;

}
