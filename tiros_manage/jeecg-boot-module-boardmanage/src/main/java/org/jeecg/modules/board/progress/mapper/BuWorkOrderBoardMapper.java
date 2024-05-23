package org.jeecg.modules.board.progress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.progress.bean.BuWorkOrder;
import org.jeecg.modules.board.progress.bean.BuWorkOrderTask;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;


import java.util.List;

/**
 * <p>
 * 工单 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
public interface BuWorkOrderBoardMapper extends BaseMapper<BuWorkOrder> {

    /**
     * 根据条件查询查询所有工单
     *
     * @param queryVO 查询条件
     * @return 所有工单
     */
    List<BuWorkOrder> listWorkOrderByCondition(@Param("queryVO") BuBoardProgressQueryVO queryVO);

    /**
     * 根据条件查询查询所有工单
     *
     * @param queryVO 查询条件
     * @return 所有工单
     */
    List<BuWorkOrder> listOutsourceWorkOrderByCondition(@Param("queryVO") BuBoardProgressQueryVO queryVO);
    /**
     * 根据条件查询所有委外任务
     *
     * @param queryVO 查询条件
     * @return 委外任务
     */
    List<BuWorkOrderTask> listOutsourceWorkOrderTaskByCondition(@Param("queryVO")BuBoardProgressQueryVO queryVO);
}
