package org.jeecg.modules.report.daily.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.report.daily.bean.BuRptDayReport;
import org.jeecg.modules.report.daily.bean.vo.BuDayReportQueryVO;

/**
 * <p>
 * 生产日报 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
public interface BuRptDayReportService extends IService<BuRptDayReport> {

    /**
     * 根据条件查询生产日报
     *
     * @param queryVO 查询条件
     * @return 生产日报
     * @throws Exception 异常信息
     */
    BuRptDayReport getDayReport(BuDayReportQueryVO queryVO) throws Exception;

    /**
     * 根据条件生成生产日报
     *
     * @param queryVO 生成条件
     * @return 生产日报
     * @throws Exception 异常信息
     */
    BuRptDayReport generateDayReport(BuDayReportQueryVO queryVO) throws Exception;

    /**
     * 保存生产日报
     *
     * @param dayReport 生产日报
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveDayReport(BuRptDayReport dayReport) throws Exception;

}
