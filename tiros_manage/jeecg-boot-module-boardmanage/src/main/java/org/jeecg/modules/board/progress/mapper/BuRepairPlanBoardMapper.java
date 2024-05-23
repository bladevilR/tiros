package org.jeecg.modules.board.progress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.progress.bean.BuRepairPlan;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;

import java.util.List;

/**
 * <p>
 * 列计划 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
public interface BuRepairPlanBoardMapper extends BaseMapper<BuRepairPlan> {

    /**
     * 查询所有未完成的列计划信息
     *
     * @return 所有列计划信息
     */
    List<BuRepairPlan> selectNotFinishRepairPlanList();

    /**
     * 查询所有未完成的列计划
     * @return
     */
    List<BuRepairPlan> selectNotFinishRepairPlanListNoTask();

    /**
     * 根据条件查询列计划进度信息
     *
     * @param year    年份
     * @param queryVO 查询条件
     * @return 列计划进度信息
     */
    List<BuRepairPlan> selectRepairPlanListByYearAndCondition(@Param("year") Integer year, @Param("queryVO") BuBoardProgressQueryVO queryVO);

}
