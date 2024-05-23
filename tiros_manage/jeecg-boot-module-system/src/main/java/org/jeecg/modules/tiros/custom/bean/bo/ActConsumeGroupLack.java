package org.jeecg.modules.tiros.custom.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 实际消耗班组库存不足-工班
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-10
 */
@Data
@Accessors(chain = true)
public class ActConsumeGroupLack {

    private String groupId;
    private List<ActConsumeOrderLack> orderLackList;

}
