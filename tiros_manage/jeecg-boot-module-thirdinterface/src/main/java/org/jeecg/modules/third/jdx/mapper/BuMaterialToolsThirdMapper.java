package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialTools;

import java.util.List;

/**
 * <p>
 * 工具信息，包括工器具、工装等信息，一种物资类型可能存在多条记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
public interface BuMaterialToolsThirdMapper extends BaseMapper<BuMaterialTools> {

    /**
     * 批量插入
     *
     * @param list 工具信息列表
     */
    void insertList(@Param("list") List<BuMaterialTools> list);

    /**
     * 批量更新
     *
     * @param list 工具信息列表
     */
    void updateList(@Param("list") List<BuMaterialTools> list);

    /**
     * 根据id查询工具详情
     *
     * @param id 工具id
     * @return 工具详情
     */
    BuMaterialTools selectToolById(String id);

    /**
     * 查询已存在的工器具id
     *
     * @return 工器具id列表
     */
    List<String> selectIdList();

}
