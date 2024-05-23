package org.jeecg.modules.basemanage.traininfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.traininfo.entity.BuMaximoTrainAsset;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuMaximoTrainAssetChildrenQueryVO;

import java.util.List;

/**
 * <p>
 * maximo资产设备 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
public interface BuMaximoTrainAssetMapper extends BaseMapper<BuMaximoTrainAsset> {

    /**
     * 批量插入
     *
     * @param list maximo资产设备列表
     */
    void insertList(@Param("list") List<BuMaximoTrainAsset> list);

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

}
