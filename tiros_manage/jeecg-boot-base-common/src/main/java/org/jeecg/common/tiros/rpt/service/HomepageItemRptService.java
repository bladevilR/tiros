package org.jeecg.common.tiros.rpt.service;

/**
 * <p>
 * 生成首页统计数据项 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
public interface HomepageItemRptService {

    /**
     * 生成并重写首页统计数据区数据
     *
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean rewriteDataItem() throws RuntimeException;

    /**
     * 生成并重写首页统计预警区数据
     *
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean rewriteAlertItem() throws RuntimeException;

}
