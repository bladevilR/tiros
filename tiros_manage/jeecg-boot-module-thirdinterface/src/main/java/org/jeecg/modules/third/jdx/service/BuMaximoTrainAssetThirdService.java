package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuMaximoTrainAsset;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;

import java.util.List;

/**
 * <p>
 * maximo资产设备 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
public interface BuMaximoTrainAssetThirdService extends IService<BuMaximoTrainAsset> {

    /**
     * 同步所有资产设备
     *
     * @param maximoAssetList                maximo资产设备
     * @param needUpdateOldBusinessTableData 是否需要更新业务表中的旧资产设备id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean insertAllAssetFromMaximoData(List<JdxAssetOut> maximoAssetList, Boolean needUpdateOldBusinessTableData) throws Exception;

}
