package org.jeecg.modules.tiros.stock.use.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.tiros.stock.use.bean.BuMaterialStockUse;

import java.util.List;

/**
 * <p>
 * 库存占用 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-11-25
 */
public interface BuMaterialStockUseMapper extends BaseMapper<BuMaterialStockUse> {

    /**
     * 批量插入
     *
     * @param list 库存占用记录列表
     */
    void insertList(@Param("list") List<BuMaterialStockUse> list);

    /**
     * 根据物资类型id查询库存占用
     *
     * @param materialTypeIdList 物资类型id
     * @return 库存占用列表
     */
    List<BuMaterialStockUse> selectListByMaterialTypeIdList(@Param("materialTypeIdList") List<String> materialTypeIdList);

    /**
     * 根据物资类型id查询库存占用
     *
     * @param materialTypeId 物资类型id
     * @return 库存占用列表
     */
    List<BuMaterialStockUse> selectListByMaterialTypeId(@Param("materialTypeId") String materialTypeId);

}
