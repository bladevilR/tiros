package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuTrainAsset;

import java.util.List;

/**
 * <p>
 * 车辆设备，按车辆分区存放，如果后期设备太多，可以线路分表，设备更换时，如果这个设备对应的是周转件，从车上换下的设备需要在 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-29
 */
public interface BuTrainAssetDispatchMapper extends BaseMapper<BuTrainAsset> {

    /**
     * 根据车号和设备类型id列表查询车辆设备信息
     *
     * @param trainNo         车号
     * @param assetTypeIdList 设备类型id列表
     * @return 车辆设备信息
     */
    List<BuTrainAsset> selectAssetListByTrainNoAndAssetTypeIdList(@Param("trainNo") String trainNo, @Param("assetTypeIdList") List<String> assetTypeIdList);

    /**
     * 根据车号和设备类型id查询车辆设备信息
     *
     * @param trainNo     车号
     * @param assetTypeId 设备类型id
     * @return 车辆设备信息
     */
    List<BuTrainAsset> selectAssetListByTrainNoAndAssetTypeId(@Param("trainNo") String trainNo, @Param("assetTypeId") String assetTypeId);

    /**
     * 根据车号和设备类型id查询车辆设备信息
     *
     * @param trainNo        车号
     * @param structDetailId 设备类型id
     * @return 车辆设备信息
     */
    List<BuTrainAsset> selectAssetListByTrainNoAndStructDetailId(@Param("trainNo") String trainNo, @Param("structDetailId") String structDetailId);

    /**
     * 根据车号和设备类型id列表查询车辆设备信息
     *
     * @param trainNo            车号
     * @param structDetailIdList 设备类型id列表
     * @return 车辆设备信息
     */
    List<BuTrainAsset> selectAssetListByTrainNoAndStructDetailIdList(@Param("trainNo") String trainNo, @Param("structDetailIdList") List<String> structDetailIdList);

    /**
     * 根据设备类型code查询id
     *
     * @param assetTypeCode 设备类型code
     * @return 设备类型id
     */
    String selectAssetTypeIdByCode(@Param("assetTypeCode") String assetTypeCode);

}
