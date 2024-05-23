package org.jeecg.modules.report.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.report.fault.bean.BuRptBoardSysFault;
import org.jeecg.modules.report.fault.bean.vo.FaultCountQueryVO;
import org.jeecg.modules.report.fault.bean.vo.FaultTrainCountSysVO;

import java.util.List;

/**
 * <p>
 * 故障汇总(单列) 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-15
 */
public interface BuFaultTrainCountReportService extends IService<BuRptBoardSysFault> {

    /**
     * 根据条件查询故障各系统统计(含架修期和质保期)
     *
     * @param queryVO 查询条件
     * @return 故障各系统统计(含架修期和质保期)
     * @throws Exception 异常信息
     */
    List<FaultTrainCountSysVO> countTrainSysFault(FaultCountQueryVO queryVO) throws Exception;

}
