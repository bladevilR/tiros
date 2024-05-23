package org.jeecg.modules.board.progress.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.board.progress.bean.BuRepairPlan;
import org.jeecg.modules.board.progress.bean.BuRptPlanGroupStatistic;
import org.jeecg.modules.board.progress.bean.vo.PlanGroupStatisticQueryVO;
import org.jeecg.modules.board.progress.bean.vo.PlanGroupStatisticVO;

/**
 * <p>
 * 列计划班组工单故障填写统计 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-01-04
 */
public interface BuRptPlanGroupStatisticService extends IService<BuRptPlanGroupStatistic> {

    /**
     * 查询最近一个作业中的列计划
     *
     * @return 最近一个作业中的列计划
     * @throws Exception 异常
     */
    BuRepairPlan getLastWorkingPlan() throws Exception;

    /**
     * 查询列计划班组工单故障填写统计
     *
     * @param queryVO 查询条件
     * @return 工单故障填写统计
     * @throws Exception 异常
     */
    PlanGroupStatisticVO listPlanGroupStatistic(PlanGroupStatisticQueryVO queryVO) throws Exception;

}
