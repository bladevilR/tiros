package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuMaterialType;
import org.jeecg.modules.third.maximo.bean.JdxInvbalancesOut;
import org.jeecg.modules.third.maximo.bean.JdxInvcostOut;

import java.util.List;

/**
 * <p>
 * 第三方接口 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-09
 */
public interface BuMaterialTypeThirdService extends IService<BuMaterialType> {

    /**
     * 初始化消费maximo库存数据：获取所有库存
     *
     * @param maximoStockList maximo库存数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean initConsumeMaximoStockData(List<JdxInvbalancesOut> maximoStockList) throws Exception;

    /**
     * 初始化消费maximo库存数据：获取所有库存、指定库房
     *
     * @param maximoStockList  maximo库存数据
     * @param topWarehouseCode 库房 如JDX01,JDX04...
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean initConsumeMaximoStockDataByTopWarehouse(List<JdxInvbalancesOut> maximoStockList, String topWarehouseCode) throws Exception;

    /**
     * 定时消费maximo库存数据：处理库存变动
     *
     * @param maximoStockList maximo库存数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean taskConsumeMaximoStockData(List<JdxInvbalancesOut> maximoStockList) throws Exception;

    /**
     * 定时消费maximo库存数据：处理库存成本
     *
     * @param maximoStockPriceList maximo库存成本数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean taskConsumeMaximoStockPriceData(List<JdxInvcostOut> maximoStockPriceList) throws Exception;

}
