package org.jeecg.modules.produce.kpi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.kpi.bean.BuRptPersonKpi;
import org.jeecg.modules.produce.kpi.bean.vo.BuKpiVO;
import org.jeecg.modules.produce.kpi.bean.vo.KpiQueryVO;

/**
 * <p>
 * 个人绩效 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
public interface BuRptPersonKpiService extends IService<BuRptPersonKpi> {

    /**
     * 查询个人贡献
     *
     * @param queryVO 查询条件
     * @return 个人贡献信息
     * @throws Exception 异常信息
     */
    BuKpiVO getUserKpi(KpiQueryVO queryVO) throws Exception;

    /**
     * 查询班组贡献
     *
     * @param queryVO 查询条件
     * @return 班组贡献信息
     * @throws Exception 异常信息
     */
    BuKpiVO getGroupKpi(KpiQueryVO queryVO) throws Exception;

}
