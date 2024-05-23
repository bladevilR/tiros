package org.jeecg.modules.board.progress.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.modules.board.progress.bean.BuWorkOrderTask;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;
import org.jeecg.modules.board.progress.bean.vo.BuWorkGroupTaskProgressVO;

import java.util.List;

/**
 * <p>
 * 工单任务 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
public interface BuWorkOrderTaskBoardService extends IService<BuWorkOrderTask> {

    /**
     * 根据条件查询查询当月每天任务完成趋势
     *
     * @param queryVO 查询条件
     * @return 当月每天任务完成趋势
     * @throws Exception 异常信息
     */
    List<LineChartVO> listCurrentMonthTaskFinishTrend(BuBoardProgressQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询工班任务进度
     *
     * @param queryVO 查询条件
     * @return 工班任务进度
     * @throws Exception 异常信息
     */
    List<BuWorkGroupTaskProgressVO> listWorkGroupTaskProgress(BuBoardProgressQueryVO queryVO) throws Exception;

    /**
     * 根据工单id和任务状态查询工单任务
     *
     * @param orderId
     * @param taskStatus
     * @return
     * @throws Exception
     */
    List<BuWorkOrderTask> listOrderTask(String orderId, Integer taskStatus) throws Exception;

}
