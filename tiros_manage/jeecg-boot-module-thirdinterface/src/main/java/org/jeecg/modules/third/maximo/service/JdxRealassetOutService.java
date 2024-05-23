package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxRealassetOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-01
 */
public interface JdxRealassetOutService extends IService<JdxRealassetOut> {

    /**
     * 获取工器具总数
     *
     * @return 工器具总数
     */
    int countTotal();

    /**
     * 分页获取工器具
     *
     * @param pageNo   页码
     * @param PageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<JdxRealassetOut> pageTool(Integer pageNo, Integer PageSize) throws Exception;

    /**
     * 根据trans列表获取工器具数据列表
     *
     * @param outTransBakList trans列表
     * @return 工器具数据列表
     * @throws Exception 异常
     */
    List<JdxRealassetOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception;

}
