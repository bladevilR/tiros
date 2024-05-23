package org.jeecg.modules.dispatch.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCategory;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultCategoryQueryVO;

/**
 * <p>
 * 故障分类 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultCategoryService extends IService<BuFaultCategory> {

    /**
     * 根据条件分页查询故障分类
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuFaultCategory> pageFaultCategory(BuFaultCategoryQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
