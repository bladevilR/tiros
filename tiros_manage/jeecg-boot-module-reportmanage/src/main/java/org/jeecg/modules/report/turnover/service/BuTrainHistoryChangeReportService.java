package org.jeecg.modules.report.turnover.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.report.turnover.bean.BuTrainHistoryChange;
import org.jeecg.modules.report.turnover.bean.vo.TurnoverDetailQueryVO;
import org.jeecg.modules.report.turnover.bean.vo.TurnoverDetailResultVO;

/**
 * <p>
 * 车辆履历-更换记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryChangeReportService extends IService<BuTrainHistoryChange> {

    /**
     * 根据条件分页查询周转件更换记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<TurnoverDetailResultVO> pageTurnoverDetail(TurnoverDetailQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
