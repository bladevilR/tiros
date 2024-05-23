package org.jeecg.modules.basemanage.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.finance.bean.BuMaximoFinanceItem;

import java.util.List;

/**
 * <p>
 * 财务项目 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-27
 */
public interface BuMaximoFinanceItemService extends IService<BuMaximoFinanceItem> {

    /**
     * 查询项目列表
     *
     * @return 项目列表
     * @throws Exception 异常
     */
    List<BuMaximoFinanceItem> listProjectItem() throws Exception;

    /**
     * 根据项目id查询任务列表
     * @param projectId 项目id
     * @return 任务列表
     * @throws Exception 异常
     */
    List<BuMaximoFinanceItem> listTaskItemByProjectId(String projectId) throws Exception;

}
