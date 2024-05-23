package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuWorkOrder;
import org.jeecg.modules.third.jdx.bean.bo.PriceZero;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 工单 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
public interface BuWorkOrderThirdMapper extends BaseMapper<BuWorkOrder> {

    /**
     * 批量更新工单的maximo工单id
     *
     * @param list 工单列表
     */
    void updateListMaximoWorkOrderId(@Param("list") List<BuWorkOrder> list);

    void updateOrderMaterialActPrice(@Param("id") String id, @Param("price") BigDecimal price);

    void updateGroupStockPrice(@Param("id") String id, @Param("price") BigDecimal price);

    void updateAssignDetailPrice(@Param("id") String id, @Param("price") BigDecimal price);

    /**
     * 查询物料价格为0信息
     *
     * @return 物料价格为0信息
     */
    List<PriceZero> selectNeedCloseOrderMaterialPriceZero();

    BigDecimal selectOrderMaterialActPrice(@Param("id") String id);

    BigDecimal selectGroupStockPrice(@Param("id") String id);

    BigDecimal selectAssignDetailPrice(@Param("id") String id);

}
