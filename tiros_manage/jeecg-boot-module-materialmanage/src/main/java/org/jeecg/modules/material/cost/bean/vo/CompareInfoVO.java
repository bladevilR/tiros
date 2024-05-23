package org.jeecg.modules.material.cost.bean.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 物料消耗对比 结果vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/10/26
 */
@Data
@Accessors(chain = true)
public class CompareInfoVO {

    private String consumeOrderInfo;
    private String compareInfo;
    private List<CompareResultVO> compareResultList;

}
