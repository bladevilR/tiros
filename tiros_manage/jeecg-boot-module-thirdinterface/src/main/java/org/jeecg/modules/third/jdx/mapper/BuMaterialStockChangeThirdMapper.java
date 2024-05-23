package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialStockChange;

import java.util.List;

/**
 * <p>
 * 库存变动记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-10-23
 */
public interface BuMaterialStockChangeThirdMapper extends BaseMapper<BuMaterialStockChange> {

    /**
     * 批量插入
     *
     * @param list 库存变动记录列表
     */
    void insertList(@Param("list") List<BuMaterialStockChange> list);

}
