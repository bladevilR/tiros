package org.jeecg.modules.quality.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.fault.bean.BuFaultReason;

import java.util.List;

/**
 * <p>
 * 故障原因 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultReasonQualityService extends IService<BuFaultReason> {

    /**
     * 查询故障原因
     *
     * @param categoryId  所属分类id
     * @param faultCodeId 所属故障代码id
     * @return 故障原因列表
     * @throws Exception 异常信息
     */
    List<BuFaultReason> list(String categoryId, String faultCodeId) throws Exception;

}
