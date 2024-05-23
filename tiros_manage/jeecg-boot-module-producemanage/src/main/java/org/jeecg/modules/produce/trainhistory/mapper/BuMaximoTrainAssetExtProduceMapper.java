package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.trainhistory.bean.BuMaximoTrainAssetExt;

import java.util.List;

/**
 * <p>
 * maximo资产设备扩展信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-29
 */
public interface BuMaximoTrainAssetExtProduceMapper extends BaseMapper<BuMaximoTrainAssetExt> {

    /**
     * 根据资产设备id列表查询扩展信息
     *
     * @param assetIdList 资产设备id列表
     * @return 扩展信息列表
     */
    List<BuMaximoTrainAssetExt> selectListByAssetIdList(@Param("assetIdList") List<String> assetIdList);

}
