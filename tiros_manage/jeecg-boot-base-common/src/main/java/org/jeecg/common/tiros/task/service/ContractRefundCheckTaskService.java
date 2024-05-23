package org.jeecg.common.tiros.task.service;

/**
 * <p>
 * 委外合同质保金退款检查 服务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/17
 */
public interface ContractRefundCheckTaskService {

    /**
     * 检查需要退款的委外合同
     *
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean checkContractRefund() throws RuntimeException;

}
