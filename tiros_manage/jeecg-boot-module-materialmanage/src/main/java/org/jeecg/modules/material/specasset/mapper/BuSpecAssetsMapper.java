package org.jeecg.modules.material.specasset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.specasset.bean.BuSpecAssets;
import org.jeecg.modules.material.specasset.bean.vo.BuSpecAssetQueryVO;

/**
 * <p>
 * 特种设备 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-02
 */
public interface BuSpecAssetsMapper extends BaseMapper<BuSpecAssets> {

    /**
     * 根据条件分页查询特种设备
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuSpecAssets> selectPageByQueryVO(IPage<BuSpecAssets> page, @Param("queryVO") BuSpecAssetQueryVO queryVO);

    /**
     * 根据id查询特种设备
     *
     * @param id 特种设备id
     * @return 特种设备
     */
    BuSpecAssets selectSpecAssetById(@Param("id") String id);

}
