package org.jeecg.modules.material.apply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.apply.bean.BuWorkOrder;
import org.jeecg.modules.material.apply.bean.BuWorkOrderTask;

import java.util.List;

/**
 * <p>
 * 工单 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-19
 */
public interface BuWorkOrderForMaterialMapper extends BaseMapper<BuWorkOrder> {

    /**
     * 根据工单id列表查询工单
     *
     * @param orderIdList 工单id列表
     * @return 工单列表
     */
    List<BuWorkOrder> selectListByIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 根据工单id查询工单任务
     *
     * @param orderId 工单id
     * @return 工单任务列表
     */
    List<BuWorkOrderTask> selectOrderTaskListByOrderId(@Param("orderId") String orderId);

}
