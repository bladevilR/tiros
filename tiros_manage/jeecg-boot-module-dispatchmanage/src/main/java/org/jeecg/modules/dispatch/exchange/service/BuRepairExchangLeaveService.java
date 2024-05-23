package org.jeecg.modules.dispatch.exchange.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangLeave;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 开口项（遗留问题） 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
public interface BuRepairExchangLeaveService extends IService<BuRepairExchangLeave> {

    /**
     * 分页查询交接车开口项记录
     *
     * @param exchangeId 交接车id
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuRepairExchangLeave> page(String exchangeId, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 批量删除交接车开口项记录
     *
     * @param ids 交接车开口项记录ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
