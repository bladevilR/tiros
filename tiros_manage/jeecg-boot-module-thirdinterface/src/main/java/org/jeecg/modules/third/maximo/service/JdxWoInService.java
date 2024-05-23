package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuWorkOrder;
import org.jeecg.modules.third.maximo.bean.JdxWoIn;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxWoInService extends IService<JdxWoIn> {

    /**
     * 单个插入
     *
     * @param maximoOrder 数据
     * @throws Exception 异常
     */
    boolean saveOne(JdxWoIn maximoOrder) throws Exception;

    /**
     * 单个插入
     *
     * @param maximoOrder 数据
     * @param inTrans     队列
     * @throws Exception 异常
     */
    boolean saveOneAndInTrans(JdxWoIn maximoOrder, MxinInterTrans inTrans) throws Exception;

    /**
     * 批量插入
     *
     * @param maximoOrderList 数据列表
     * @throws Exception 异常
     */
    boolean saveList(List<JdxWoIn> maximoOrderList) throws Exception;

    /**
     * 检查工单核实记录已存在
     *
     * @param orderCode 工单号
     * @return 是否存在
     * @throws Exception 异常
     */
    boolean checkVerifiedOrderExistAndNotClosed(String orderCode) throws Exception;

    /**
     * 读取工单的maximo工单id
     *
     * @param orderList 工单列表
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean getOrderMaximoWorkOrderId(List<BuWorkOrder> orderList) throws Exception;

    /**
     * 获取maximo工单号
     *
     * @return maximo工单号列表
     * @throws Exception 异常
     */
    List<String> listOrderCode() throws Exception;

}
