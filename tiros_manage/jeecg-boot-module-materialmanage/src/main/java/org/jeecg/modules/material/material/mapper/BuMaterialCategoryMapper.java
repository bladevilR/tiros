package org.jeecg.modules.material.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.material.bean.BuMaterialCategory;

import java.util.List;

/**
 * <p>
 * 物资分类 Mapper 接口
 * </p>
 *
 * @author yfy
 * @since 2021-05-08
 */
public interface BuMaterialCategoryMapper extends BaseMapper<BuMaterialCategory> {

    List<BuMaterialCategory> selectAll();

    List<BuMaterialCategory> selectCategoryByCode(@Param("code") String code);

    void saveMaterialCategory(@Param("list") List<BuMaterialCategory> categoryList);
}
