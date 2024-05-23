package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxInvcostOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;

import java.util.List;

/**
 * <p>
 * 物资库存成本 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-10-31
 */
public interface JdxInvcostOutService extends IService<JdxInvcostOut> {

    /**
     * 根据maximo输出队列备份列表获取物资库存成本数据列表
     *
     * @param outTransBakList maximo输出队列备份列表
     * @return 物资库存成本数据列表
     * @throws Exception 异常
     */
    List<JdxInvcostOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception;

}
