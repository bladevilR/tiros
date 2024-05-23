package org.jeecg.modules.board.progress.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.board.progress.bean.BuWorkOrder;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;
import org.jeecg.modules.board.progress.bean.vo.BuWorkGroupOrderInfoVO;
import org.jeecg.modules.board.progress.bean.vo.BuWorkOutsourceOrderVO;
import org.jeecg.modules.board.progress.bean.vo.BuWorkOutsourceTaskVO;

import java.util.List;

/**
 * <p>
 * 工单 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
public interface BuWorkOrderBoardService extends IService<BuWorkOrder> {

    /**
     * 根据工班id查询工单信息
     *
     * @param queryVO 查询条件，groupId必填
     * @return 工单信息
     * @throws Exception 异常信息
     */
    BuWorkGroupOrderInfoVO getWorkGroupOrderInfo(BuBoardProgressQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询委外工单进度
     *
     * @param queryVO 查询条件
     * @return 委外任务进度
     * @throws Exception 异常信息
     */
    List<BuWorkOutsourceOrderVO> listOutsourceOrderProgress(BuBoardProgressQueryVO queryVO) throws Exception;
    /**
     * 根据条件查询委外任务进度
     *
     * @param queryVO 查询条件
     * @return 委外任务进度
     * @throws Exception 异常信息
     */
    List<BuWorkOutsourceTaskVO> listOutsourceTaskVOProgress(BuBoardProgressQueryVO queryVO) throws Exception;
}
