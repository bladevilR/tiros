package org.jeecg.modules.material.entry.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 入库单 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
public interface BuMaterialEntryOrderMapper extends BaseMapper<BuMaterialEntryOrder> {

    /**
     * 批量插入
     *
     * @param list 入库单列表
     */
    void insertList(@Param("list") List<BuMaterialEntryOrder> list);

    /**
     * 根据入库单id查询入库单
     *
     * @param entryOrderId 入库单id
     * @return 入库单信息
     */
    BuMaterialEntryOrder selectEntryOrderById(@Param("entryOrderId") String entryOrderId);

}
