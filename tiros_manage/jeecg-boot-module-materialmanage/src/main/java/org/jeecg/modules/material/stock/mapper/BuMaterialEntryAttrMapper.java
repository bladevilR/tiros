package org.jeecg.modules.material.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.stock.bean.BuMaterialEntryAttr;

import java.util.List;

/**
 * <p>
 * 物资批次属性 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-11-30
 */
public interface BuMaterialEntryAttrMapper extends BaseMapper<BuMaterialEntryAttr> {

    /**
     * 查询物资批次属性
     *
     * @param materialTypeId 物资id
     * @param tradeNo        批次
     * @return 物资批次属性
     */
    List<BuMaterialEntryAttr> selectListByMaterialAndTrade(@Param("materialTypeId") String materialTypeId, @Param("tradeNo") String tradeNo);

}
