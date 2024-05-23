package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTaskEqu;

import java.util.List;

/**
 * <p>
 * 工单任务目标设备 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
public interface BuWorkOrderTaskEquMapper extends BaseMapper<BuWorkOrderTaskEqu> {

    /**
     * 批量插入
     *
     * @param list 工单任务目标设备列表
     */
    void insertList(@Param("list") List<BuWorkOrderTaskEqu> list);

    /**
     * 根据工单id查询目标设备列表，按车辆设备id去重
     *
     * @param orderId 工单id
     * @return 目标设备列表
     */
    List<BuWorkOrderTaskEqu> selectListByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单任务id查询目标设备列表
     *
     * @param taskId 工单任务id
     * @return 目标设备列表
     */
    List<BuWorkOrderTaskEqu> selectListByTaskId(@Param("taskId") String taskId);

}
