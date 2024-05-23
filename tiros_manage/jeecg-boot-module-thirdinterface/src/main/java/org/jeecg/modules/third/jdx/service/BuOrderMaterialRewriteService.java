package org.jeecg.modules.third.jdx.service;

import org.jeecg.modules.third.maximo.bean.JdxMatusetransIn;

import java.util.List;

/**
 * <p>
 * 物料消耗或退料，maximo回写状态的处理 服务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-31
 */
public interface BuOrderMaterialRewriteService {

    /**
     * 消费maximo工单物料消耗或退料回写数据
     *
     * @param maximoOrderMaterialList 工单物料消耗或退料回写数据
     * @return 处理结果
     * @throws Exception 异常
     */
    boolean consumeMaximoTransData(List<JdxMatusetransIn> maximoOrderMaterialList) throws Exception;

}
