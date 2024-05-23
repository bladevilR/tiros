package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuWorkOrder;
import org.jeecg.modules.third.jdx.bean.bo.PriceZero;
import org.jeecg.modules.third.jdx.bean.vo.OrderDiff;
import org.jeecg.modules.third.maximo.bean.JdxWoIn;

import java.util.List;

/**
 * <p>
 * 工单 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
public interface BuWorkOrderThirdService extends IService<BuWorkOrder> {

    /**
     * 根据工单获取需写入到maximo的工单
     *
     * @param order 工单
     * @return 需写入到maximo的工单
     * @throws Exception 异常
     */
    JdxWoIn getMaximoOrderNeedWriteByOrder(BuWorkOrder order) throws Exception;

    /**
     * 查询需要写入maximo工单id的工单
     *
     * @return 工单列表
     * @throws Exception 异常
     */
    List<BuWorkOrder> listOrderNeedMaximoWorkOrderId() throws Exception;

    /**
     * 设置工单的maximo工单id
     *
     * @param orderList 工单列表
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateOrderMaximoWorkOrderId(List<BuWorkOrder> orderList) throws Exception;

    /**
     * 获取架大修系统和maximo系统的不同工单
     *
     * @param maximoOrderCodeList maximo工单号列表
     * @return 工单差异
     * @throws Exception 异常
     */
    OrderDiff getOrderDiffOfJdxAndMaximo(List<String> maximoOrderCodeList) throws Exception;

    /**
     * 查询物料价格为0的数据
     *
     * @return 物料价格为0的数据
     * @throws Exception 异常
     */
    List<PriceZero> getNeedCloseOrderMaterialPriceZero() throws Exception;

    /**
     * 更新物料价格为0的数据
     *
     * @param priceZeroList 物料价格为0的数据
     * @return 更新后数据
     * @throws Exception 异常
     */
    List<PriceZero> rewriteNeedCloseOrderMaterialPriceZero(List<PriceZero> priceZeroList) throws Exception;

}
