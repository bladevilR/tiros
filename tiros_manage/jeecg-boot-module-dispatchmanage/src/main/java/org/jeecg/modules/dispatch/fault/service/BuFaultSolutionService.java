package org.jeecg.modules.dispatch.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.fault.bean.BuFaultSolution;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultSolutionQueryVO;

/**
 * <p>
 * 故障处理措施 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultSolutionService extends IService<BuFaultSolution> {

    /**
     * 根据条件分页查询故障处理措施
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuFaultSolution> pageFaultSolution(BuFaultSolutionQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
