package org.jeecg.modules.dispatch.exchange.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangRectify;

/**
 * <p>
 * 交接车整改项 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
public interface BuRepairExchangRectifyService extends IService<BuRepairExchangRectify> {

    /**
     * 分页查询交接车整改项记录
     *
     * @param exchangeId 交接车id
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuRepairExchangRectify> page(String exchangeId, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 批量删除交接车整改项记录
     *
     * @param ids 交接车整改项记录ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
