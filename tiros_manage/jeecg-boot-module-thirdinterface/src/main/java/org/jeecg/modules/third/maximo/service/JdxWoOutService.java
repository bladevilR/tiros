package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxWoOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;

import java.util.List;

/**
 * <p>
 * 工单 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxWoOutService extends IService<JdxWoOut> {

    /**
     * 获取工单总数
     *
     * @return 工单总数
     */
    int countTotal();

    /**
     * 分页获取工单
     *
     * @param pageNo   页码
     * @param PageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<JdxWoOut> pageOrder(Integer pageNo, Integer PageSize) throws Exception;

    /**
     * 根据maximo输出队列备份列表列表获取工单数据列表
     *
     * @param outTransBakList maximo输出队列备份列表
     * @return 工单数据列表
     * @throws Exception 异常
     */
    List<JdxWoOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception;

}
