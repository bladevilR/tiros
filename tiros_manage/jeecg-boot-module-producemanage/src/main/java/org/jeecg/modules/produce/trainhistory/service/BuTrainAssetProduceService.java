package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainAsset;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainAssetQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆设备 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
public interface BuTrainAssetProduceService extends IService<BuTrainAsset> {

    /**
     * 根据车辆号和车辆结构明细id查询子设备
     * 根据车辆号和车辆结构明细id找到对应的车辆设备，再找出此设备的下一层子设备列表
     *
     * @param trainNo           车辆号
     * @param structureDetailId 车辆结构明细id
     * @return 子设备列表
     * @throws Exception 异常信息
     */
    List<BuTrainAsset> listChildAsset(String trainNo, String structureDetailId) throws Exception;

    /**
     * 根据条件查询车辆设备
     *
     * @param queryVO 查询条件
     * @return 车辆设备列表
     * @throws Exception 异常信息
     */
    List<BuTrainAsset> queryAsset(BuTrainAssetQueryVO queryVO) throws Exception;

    /**
     * 根据设备id查询设备详情
     *
     * @param assetId 设备id
     * @return 车辆设备详情
     * @throws Exception 异常信息
     */
    BuTrainAsset getAssetById(String assetId) throws Exception;

    /**
     * 根据条件查询车辆设备及子设备id列表
     *
     * @param queryVO 查询条件
     * @return 车辆设备及子设备id列表
     * @throws Exception 异常信息
     */
    List<String> listAssetId(BuTrainAssetQueryVO queryVO) throws Exception;

    /**
     * 根据结构明细id查询设备id
     *
     * @param structDetailId 结构明细id
     * @return 设备id
     * @throws Exception 异常信息
     */
    String getAssetIdByTrainNoAndStructDetailId(String trainNo, String structDetailId) throws Exception;

}
