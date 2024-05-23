package org.jeecg.modules.material.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;

import java.util.List;

/**
 * <p>
 * 物资属性 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-07
 */
public interface BuMaterialTypeAttrMapper extends BaseMapper<BuMaterialTypeAttr> {

    void insertBatch(@Param("list") List<BuMaterialTypeAttr> saveTypeAttrs);

    void updateBatch(@Param("list") List<BuMaterialTypeAttr> materialTypeAttrList);

    /**
     * 根据物资id获取物资属性
     *
     * @param materialTypeId 物资id
     * @return 物资属性
     */
    BuMaterialTypeAttr selectByMaterialTypeId(@Param("materialTypeId") String materialTypeId);

    /**
     * 查询有效的安全库存量的物资属性
     *
     * @return 物资属性列表
     */
    List<BuMaterialTypeAttr> selectValidThresholdList();

}
