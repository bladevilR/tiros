package org.jeecg.modules.board.progress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.progress.bean.BuRptPlanGroupStatistic;
import org.jeecg.modules.board.progress.bean.vo.PlanGroupStatisticQueryVO;

import java.util.List;

/**
 * <p>
 * 列计划班组工单故障填写统计 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-01-04
 */
public interface BuRptPlanGroupStatisticMapper extends BaseMapper<BuRptPlanGroupStatistic> {

    /**
     * 批量插入
     *
     * @param list 列计划班组工单故障填写统计列表
     */
    void insertList(@Param("list") List<BuRptPlanGroupStatistic> list);

    /**
     * 查询列计划班组工单故障填写统计
     *
     * @param queryVO 查询条件
     * @return 工单故障填写统计
     */
    List<BuRptPlanGroupStatistic> selectListByQueryVO(@Param("queryVO") PlanGroupStatisticQueryVO queryVO);

}
