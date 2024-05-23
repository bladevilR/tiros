package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialStock;

import java.util.List;

/**
 * <p>
 * 物资库存  一种物资类型可能存在多条，所以在查询库存时要注意汇总，汇总时价格用最新的，且只要显示 物资类型 的kind为 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
public interface BuMaterialStockThirdMapper extends BaseMapper<BuMaterialStock> {

    /**
     * 批量插入
     *
     * @param list 物资库存列表
     */
    void insertList(@Param("list") List<BuMaterialStock> list);

    /**
     * 批量物资库存
     *
     * @param list 物资库存列表
     */
    void updateListAmountPrice(@Param("list") List<BuMaterialStock> list);

    /**
     * 批量物资库存
     *
     * @param list 物资库存列表
     */
    void updateListAmount(@Param("list") List<BuMaterialStock> list);

    /**
     * 批量物资库存
     *
     * @param list 物资库存列表
     */
    void updateListPrice(@Param("list") List<BuMaterialStock> list);

//    /**
//     * 删除非4级库位的库存数据
//     */
//    void deleteAllNot4levelStock();

//    /**
//     * 清理物资库存表中的数量为0的数据
//     */
//    void deleteZeroAmount();

    /**
     * 查询库位级别在指定级别之下的库存
     *
     * @param level 级别
     * @return 库存列表
     */
    List<BuMaterialStock> selectListByMaxLevel(@Param("level") Integer level);

    /**
     * 查询库位级别在指定级别之下的库存
     *
     * @param level              级别
     * @param materialTypeIdList 物资类型id列表
     * @return 库存列表
     */
    List<BuMaterialStock> selectListByMaxLevelAndMaterialTypeIdList(@Param("level") Integer level, @Param("materialTypeIdList") List<String> materialTypeIdList);

}
