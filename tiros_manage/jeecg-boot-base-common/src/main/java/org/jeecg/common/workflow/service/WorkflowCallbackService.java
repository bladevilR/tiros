package org.jeecg.common.workflow.service;

import java.util.Map;

/**
 * <p>
 * 流程回调 服务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-25
 */
public interface WorkflowCallbackService {

    /**
     * 回调接口：流程结束改变对应业务状态及处理相关逻辑
     *
     * @param requestParam 请求参数
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean completeChangeStatus(Map<String, Object> requestParam) throws Exception;

}
