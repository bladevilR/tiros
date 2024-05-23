package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialReturned;

import java.util.List;

/**
 * <p>
 * 退料 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
public interface BuMaterialReturnedDispatchMapper extends BaseMapper<BuMaterialReturned> {

    /**
     * 根据工单id获取工单退料单
     *
     * @param orderId 工单id
     * @return 工单退料单列表
     */
    List<BuMaterialReturned> selectListByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单id统计查询未确认的退料单数量
     *
     * @param orderId 工单id
     * @return 未确认的退料单数量
     */
    Integer countUnConfirmByOrderId(@Param("orderId") String orderId);

}
