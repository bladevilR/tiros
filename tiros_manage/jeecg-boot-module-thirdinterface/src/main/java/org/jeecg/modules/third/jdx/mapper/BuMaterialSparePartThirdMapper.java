package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialSparePart;

import java.util.List;

/**
 * <p>
 * 列管备件 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-07-22
 */
public interface BuMaterialSparePartThirdMapper extends BaseMapper<BuMaterialSparePart> {

    /**
     * 批量插入
     *
     * @param list 列管备件列表
     */
    void insertList(@Param("list") List<BuMaterialSparePart> list);

    void updateList(@Param("list") List<BuMaterialSparePart> list);

    /**
     * 查询已存在的工器具id
     *
     * @return 工器具id列表
     */
    List<String> selectIdList();

}
