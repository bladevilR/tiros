package org.jeecg.modules.report.cost.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.report.cost.bean.BuRptPersonKpi;
import org.jeecg.modules.report.cost.bean.vo.BuKpiTimeItemVO;
import org.jeecg.modules.report.cost.bean.vo.KpiQueryVO;


/**
 * <p>
 * 个人绩效 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
public interface BuRptPersonKpiReportService extends IService<BuRptPersonKpi> {

    /**
     * 根据条件查询人工成本
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 人工成本列表
     * @throws Exception 异常
     */
    IPage<BuKpiTimeItemVO> pageTimeKpi(KpiQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据条件导出人工成本excel
     *
     * @param queryVO 查询条件
     * @return 人工成本excel
     * @throws Exception 异常
     */
    HSSFWorkbook exportTimeKpi(KpiQueryVO queryVO) throws Exception;

}
