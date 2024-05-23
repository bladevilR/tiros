package org.jeecg.modules.board.progress.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.board.progress.bean.BuRepairPlanYear;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;
import org.jeecg.modules.board.progress.bean.vo.BuPlanYearProgressProgramVO;

import java.util.List;

/**
 * <p>
 * 年计划 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-02
 */
public interface BuRepairPlanYearBoardService extends IService<BuRepairPlanYear> {

    /**
     * 查询架大修完成数据统计
     * 1. 查询出符合条件的年度架修、大修数量。
     * 2. 从列计划表中查询已完成的架修、大修数量。
     * 3. 计算架修、大修的完成比例，
     * 4. 查询出正在进行的架修、大修。
     * 5. 计算整体的完成比例。
     *
     * @param queryVO 查询条件
     * @return 完成数据统计
     * @throws Exception 异常信息
     */
    List<BuPlanYearProgressProgramVO> listPlanYearProgress(BuBoardProgressQueryVO queryVO) throws Exception;

    /**
     * 查询架大修完工质量比例
     * 1. 查询出当前年所有已完工的列计划。
     * 2. 计算所有完工计划中的，超前完工、延期完工、正常完工的各自数量。
     *
     * @param queryVO 查询条件
     * @return 完工数量和各种完工数量
     * @throws Exception 异常信息
     */
    List<PieChartVO> getFinishQuality(BuBoardProgressQueryVO queryVO) throws Exception;

    /**
     * 查询架大修年计划燃尽数据
     * 1. 查询出本年每个月应完成的架大修数量
     * 2. 查询出本年度每个月实际完成架大修数量（只到当前月份）。
     *
     * @param queryVO 查询条件
     * @return 年计划燃尽数据
     * @throws Exception 异常信息
     */
    List<LineChartVO> getBurnDownChartData(BuBoardProgressQueryVO queryVO) throws Exception;

}
