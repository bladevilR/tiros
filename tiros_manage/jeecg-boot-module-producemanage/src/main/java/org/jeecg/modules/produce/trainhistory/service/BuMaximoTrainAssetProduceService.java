package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.trainhistory.bean.BuMaximoTrainAsset;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuMaximoTrainAssetChildrenQueryVO;

import java.util.List;

/**
 * <p>
 * maximo资产设备 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
public interface BuMaximoTrainAssetProduceService extends IService<BuMaximoTrainAsset> {

    /**
     * 查询资产设备子节点
     *
     * @param queryVO 查询条件
     * @return 子节点列表
     * @throws Exception 异常
     */
    List<BuMaximoTrainAsset> listMaximoTrainAssetChild(BuMaximoTrainAssetChildrenQueryVO queryVO) throws Exception;

    /**
     * 根据id查询资产设备和扩展信息
     *
     * @param id 资产设备id
     * @return 资产设备和扩展信息
     * @throws Exception 异常
     */
    BuMaximoTrainAsset getTrainAssetById(String id) throws Exception;

}
