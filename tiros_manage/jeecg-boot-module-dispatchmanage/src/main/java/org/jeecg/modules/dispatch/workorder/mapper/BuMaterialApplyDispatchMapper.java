package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialApply;

import java.util.List;

/**
 * <p>
 * 物料申请(领用) Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
public interface BuMaterialApplyDispatchMapper extends BaseMapper<BuMaterialApply> {

    /**
     * 根据工单id删除领用单
     *
     * @param orderId 工单id
     */
    void deleteByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单id和领用类型查询物料领用单列表
     *
     * @param orderId   工单id
     * @param applyType 领用类型
     * @return 物料领用单列表
     */
    List<BuMaterialApply> selectListByOrderIdAndType(@Param("orderId") String orderId, @Param("applyType") Integer applyType);

}
