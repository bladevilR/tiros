package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuTrainHistoryWork;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;
import org.jeecg.modules.third.maximo.bean.JdxLabtransOut;
import org.jeecg.modules.third.maximo.bean.JdxWoOut;

import java.util.List;


/**
 * <p>
 * 车辆履历-作业记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryWorkThirdService extends IService<BuTrainHistoryWork> {

    /**
     * 初始化消费maximo工单数据：所有工单
     *
     * @param maximoOrderList maximo工单数据
     * @param maximoAssetList maximo资产数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean initConsumeMaximoOrderData(List<JdxWoOut> maximoOrderList, List<JdxAssetOut> maximoAssetList) throws Exception;

    /**
     * 定时消费maximo工单数据：工单变动
     *
     * @param maximoOrderList maximo工单数据
     * @param maximoAssetList maximo资产数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean taskConsumeMaximoOrderData(List<JdxWoOut> maximoOrderList, List<JdxAssetOut> maximoAssetList) throws Exception;

    /**
     * 通过maximo工单人员数据插入作业记录人员
     *
     * @param maximoOrderUserList maximo人员物料数据
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean insertOrderUserFromMaximoData(List<JdxLabtransOut> maximoOrderUserList) throws Exception;

}
