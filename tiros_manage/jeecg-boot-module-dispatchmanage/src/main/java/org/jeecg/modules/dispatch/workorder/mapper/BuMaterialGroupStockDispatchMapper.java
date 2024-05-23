package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialGroupStock;

import java.util.List;

/**
 * <p>
 * 班组库存 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
public interface BuMaterialGroupStockDispatchMapper extends BaseMapper<BuMaterialGroupStock> {

    /**
     * 批量更新库存量
     *
     * @param list 班组库存列表
     */
    void updateListAmount(@Param("list") List<BuMaterialGroupStock> list);

}
