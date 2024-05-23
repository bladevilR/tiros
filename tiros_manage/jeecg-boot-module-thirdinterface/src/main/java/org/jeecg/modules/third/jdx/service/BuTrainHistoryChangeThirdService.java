package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuTrainHistoryChange;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;
import org.jeecg.modules.third.maximo.bean.JdxRealassettransOut;

import java.util.List;

/**
 * <p>
 * 车辆履历-更换记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryChangeThirdService extends IService<BuTrainHistoryChange> {

    /**
     * 初始化消费maximo更换数据：所有更换
     *
     * @param maximoChangeList maximo更换数据
     * @param maximoAssetList  maximo资产数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean initConsumeMaximoChangeData(List<JdxRealassettransOut> maximoChangeList, List<JdxAssetOut> maximoAssetList) throws Exception;

    /**
     * 定时消费maximo更换数据：更换变动
     *
     * @param maximoChangeList maximo更换数据
     * @param maximoAssetList  maximo资产数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean taskConsumeMaximoChangeData(List<JdxRealassettransOut> maximoChangeList, List<JdxAssetOut> maximoAssetList) throws Exception;

}
