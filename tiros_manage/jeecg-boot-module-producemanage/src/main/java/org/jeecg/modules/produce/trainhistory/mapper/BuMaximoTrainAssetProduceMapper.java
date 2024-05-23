package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.trainhistory.bean.BuMaximoTrainAsset;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuMaximoTrainAssetChildrenQueryVO;

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
public interface BuMaximoTrainAssetProduceMapper extends BaseMapper<BuMaximoTrainAsset> {

    /**
     * 查询资产设备子节点
     *
     * @param queryVO 查询条件
     * @return 子节点列表
     */
    List<BuMaximoTrainAsset> selectChildrenByQueryVO(@Param("queryVO") BuMaximoTrainAssetChildrenQueryVO queryVO);

    /**
     * 根据id查询资产设备
     *
     * @param id 资产设备id
     * @return 资产设备
     */
    BuMaximoTrainAsset selectAssetById(@Param("id") String id);

    /**
     * 查询各车辆的车厢数
     *
     * @return 各车辆的车厢数
     */
    List<Map<String, Object>> selectTrainCarsNum();

}
