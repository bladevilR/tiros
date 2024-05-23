package org.jeecg.modules.dispatch.workorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTool;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderToolService extends IService<BuWorkOrderTool> {

    /**
     * 保存工单工器具
     *
     * @param tool 工单工器具
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveOrderTool(BuWorkOrderTool tool) throws Exception;

    /**
     * 删除工单工器具
     *
     * @param ids 工单工器具ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteOrderTool(String ids) throws Exception;

}
