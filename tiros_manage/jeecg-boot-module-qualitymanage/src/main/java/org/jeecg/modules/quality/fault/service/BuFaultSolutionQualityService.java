package org.jeecg.modules.quality.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.fault.bean.BuFaultSolution;

import java.util.List;

/**
 * <p>
 *  故障处理措施 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultSolutionQualityService extends IService<BuFaultSolution> {

    /**
     * 查询故障处理措施
     *
     * @param categoryId  所属分类id
     * @param faultCodeId 所属故障代码id
     * @param faultReasonId 所属故障原因id
     * @return 故障原因列表
     * @throws Exception 异常信息
     */
    List<BuFaultSolution> list(String categoryId, String faultCodeId, String faultReasonId) throws Exception;

}
