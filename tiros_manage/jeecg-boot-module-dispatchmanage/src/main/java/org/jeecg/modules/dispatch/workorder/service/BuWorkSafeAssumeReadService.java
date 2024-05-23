package org.jeecg.modules.dispatch.workorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkSafeAssumeRead;


/**
 * <p>
 * 安全预想阅读记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
public interface BuWorkSafeAssumeReadService extends IService<BuWorkSafeAssumeRead> {

    /**
     * 新增安全预想阅读记录
     *
     * @param safeAssumeId 安全预想id
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean addSafeAssumeRead(String safeAssumeId) throws Exception;

}
