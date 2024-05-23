package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuTrainHistoryFault;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;
import org.jeecg.modules.third.maximo.bean.JdxSrOut;

import java.util.List;

/**
 * <p>
 * 车辆履历-故障记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryFaultThirdService extends IService<BuTrainHistoryFault> {

    /**
     * 初始化消费maximo故障数据：所有故障
     *
     * @param maximoFaultList maximo故障数据
     * @param maximoAssetList maximo资产数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean initConsumeMaximoFaultData(List<JdxSrOut> maximoFaultList, List<JdxAssetOut> maximoAssetList) throws Exception;

    /**
     * 定时消费maximo故障数据：故障变动
     *
     * @param maximoFaultList maximo故障数据
     * @param maximoAssetList maximo资产数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean taskConsumeMaximoFaultData(List<JdxSrOut> maximoFaultList, List<JdxAssetOut> maximoAssetList) throws Exception;

}
