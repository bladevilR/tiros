package org.jeecg.modules.dispatch.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.fault.bean.BuFaultReason;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultReasonQueryVO;

/**
 * <p>
 * 故障原因 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultReasonService extends IService<BuFaultReason> {

    /**
     * 根据条件分页查询故障原因
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuFaultReason> pageFaultReason(BuFaultReasonQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
