package org.jeecg.modules.dispatch.workorder.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskRegu;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTool;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderToolMapper extends BaseMapper<BuWorkOrderTool> {

    /**
     * 批量插入
     *
     * @param list 工单工器具列表
     */
    void insertList(@Param("list") List<BuWorkOrderTool> list);

    List<BuWorkOrderTool> selectWorkOrderTool(@Param("workOrderTaskId") String workOrderTaskId);

}
