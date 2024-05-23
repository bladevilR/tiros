package org.jeecg.modules.board.progress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.progress.bean.BuRepairPlanTask;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;


import java.util.List;

/**
 * <p>
 * 列计划任务 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
public interface BuRepairPlanTaskBoardMapper extends BaseMapper<BuRepairPlanTask> {

    /**
     * 根据条件查询查询列计划任务
     *
     * @param queryVO 查询条件
     * @return 列计划任务
     */
    List<BuRepairPlanTask> listPlanTaskByCondition(@Param("queryVO") BuBoardProgressQueryVO queryVO);

}
