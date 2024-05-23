package org.jeecg.common.tiros.pallet;

/**
 * <p>
 * 托盘状态检查并设置 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-31
 */
public interface PalletStatusCheckService {

    /**
     * 检查并设置所有托盘状态
     * 检查所有的托盘是否存在于：未提交的工单的发放明细中，如果存在则为使用中，如果没有则为未使用
     *
     * @return 是否成功
     */
    boolean checkAndSetPalletStatus();

}
