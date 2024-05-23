package org.jeecg.modules.material.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeReplace;

import java.util.List;

/**
 * <p>
 * 可替换物资 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-07-21
 */
public interface BuMaterialTypeReplaceMapper extends BaseMapper<BuMaterialTypeReplace> {

    /**
     * 批量插入
     *
     * @param list 可替换物资列表
     */
    void insertList(@Param("list") List<BuMaterialTypeReplace> list);


}
