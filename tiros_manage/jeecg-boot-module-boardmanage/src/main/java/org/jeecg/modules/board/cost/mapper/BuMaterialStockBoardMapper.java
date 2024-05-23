package org.jeecg.modules.board.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.cost.bean.BuMaterialStock;

import java.util.List;

/**
 * <p>
 * 物资库存  一种物资类型可能存在多条，所以在查询库存时要注意汇总，汇总时价格用最新的，且只要显示 物资类型 的kind为 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
public interface BuMaterialStockBoardMapper extends BaseMapper<BuMaterialStock> {

    /**
     * 根据物资类型id列表查询物资库存
     *
     * @param materialTypeIdList 物资类型id列表
     * @return 物资库存列表
     */
    List<BuMaterialStock> selectListByMaterialTypeIdList(@Param("materialTypeIdList") List<String> materialTypeIdList);

}
