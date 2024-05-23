package org.jeecg.modules.dispatch.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCode;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeLevel;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultCodeQueryVO;

import java.util.List;

/**
 * <p>
 * 故障代码 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultCodeService extends IService<BuFaultCode> {

    /**
     * 根据条件分页查询故障代码
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuFaultCode> pageFaultCode(BuFaultCodeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
