package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainAsset;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainAssetQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆设备 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
public interface BuTrainAssetProduceMapper extends BaseMapper<BuTrainAsset> {

    /**
     * 根据条件查询车辆设备
     *
     * @param queryVO 查询条件
     * @return 车辆设备列表
     */
    List<BuTrainAsset> selectListByBuTrainAssetQueryVO(@Param("queryVO") BuTrainAssetQueryVO queryVO);

    /**
     * 根据条件统计查询车辆设备数量
     *
     * @param queryVO 查询条件
     * @return 车辆设备数量
     */
    List<String> selectIdListByBuTrainAssetQueryVO(@Param("queryVO") BuTrainAssetQueryVO queryVO);

    /**
     * 根据设备id查询设备详情
     *
     * @param assetId 设备id
     * @return 车辆设备详情
     */
    BuTrainAsset selectAssetById(@Param("assetId") String assetId);

    List<String> selectIdListByTrainNoAndStructDetailId(@Param("trainNo") String trainNo, @Param("structDetailId") String structDetailId);

//    /**
//     * 根据条件统计查询车辆设备数量
//     *
//     * @param queryVO 查询条件
//     * @return 车辆设备数量
//     */
//    Integer selectCountByBuTrainAssetQueryVO(@Param("queryVO") BuTrainAssetQueryVO queryVO);

//    /**
//     * 根据车辆结构明细id查询对应设备信息
//     *
//     * @param structureDetailId 车辆结构明细id
//     * @return 设备信息
//     */
//    BuTrainAsset selectByStructureDetailId(@Param("structureDetailId") String structureDetailId);

//    /**
//     * 根据车辆号和车辆结构明细id查询对应位置的车辆设备的下级子设备信息
//     *
//     * @param trainNo           车辆号
//     * @param structureDetailId 车辆结构明细id
//     * @return 下级子设备列表
//     */
//    List<BuTrainAsset> selectChildAssetListByTrainNoAndStructureDetailId(@Param("trainNo") String trainNo, @Param("structureDetailId") String structureDetailId);

}
