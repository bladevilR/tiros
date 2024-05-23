package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaximoTrainAsset;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * maximo资产设备 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
public interface BuMaximoTrainAssetThirdMapper extends BaseMapper<BuMaximoTrainAsset> {

    /**
     * 批量插入
     *
     * @param list maximo资产设备列表
     */
    void insertList(@Param("list") List<BuMaximoTrainAsset> list);

    List<BuMaximoTrainAsset> selectListUsed();

    List<Map<String, Object>> selectFaultIdAssetIdList();
    List<Map<String, Object>> selectOrderTaskIdAssetIdList();
    List<Map<String, Object>> selectCheckInstIdAssetIdList();
    List<Map<String, Object>> selectDataInstIdAssetIdList();
    List<Map<String, Object>> selectWorkInstIdAssetIdList();

    void updateFaultAssetIdBatch(@Param("idNewAssetIdMapList") List<Map<String, Object>> idNewAssetIdMapList);
    void updateOrderTaskAssetIdBatch(@Param("idNewAssetIdMapList") List<Map<String, Object>> idNewAssetIdMapList);
    void updateCheckInstAssetIdBatch(@Param("idNewAssetIdMapList") List<Map<String, Object>> idNewAssetIdMapList);
    void updateDataInstAssetIdBatch(@Param("idNewAssetIdMapList") List<Map<String, Object>> idNewAssetIdMapList);
    void updateWorkInstAssetIdBatch(@Param("idNewAssetIdMapList") List<Map<String, Object>> idNewAssetIdMapList);

}
