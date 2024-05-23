package org.jeecg.modules.basemanage.traininfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.traininfo.entity.BuMaximoTrainAssetExt;

/**
 * <p>
 * maximo资产设备扩展信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-29
 */
public interface BuMaximoTrainAssetExtService extends IService<BuMaximoTrainAssetExt> {

    /**
     * 保存资产设备扩展信息
     *
     * @param assetExt 资产设备扩展信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveAssetExt(BuMaximoTrainAssetExt assetExt) throws Exception;

}
