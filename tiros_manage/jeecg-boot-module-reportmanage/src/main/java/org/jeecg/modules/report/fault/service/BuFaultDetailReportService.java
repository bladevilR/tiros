package org.jeecg.modules.report.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.report.fault.bean.BuFaultInfo;
import org.jeecg.modules.report.fault.bean.vo.FaultDetailQueryVO;

import java.util.List;

/**
 * <p>
 * 故障信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultDetailReportService extends IService<BuFaultInfo> {

    /**
     * 根据条件查询故障明细
     *
     * @param queryVO  查询条件
     * @return 故障列表
     * @throws Exception 异常
     */
    List<BuFaultInfo> listFault(FaultDetailQueryVO queryVO) throws Exception;

    /**
     * 根据条件导出故障明细
     *
     * @param queryVO 查询条件
     * @return 故障明细excel
     * @throws Exception 异常
     */
    HSSFWorkbook exportFaultDetail(FaultDetailQueryVO queryVO) throws Exception;

}
