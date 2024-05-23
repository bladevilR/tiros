package org.jeecg.modules.dispatch.workorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.workorder.bean.BuRepairTaskStaffArrange;

import java.util.List;

/**
 * <p>
 * 任务人员安排 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuRepairTaskStaffArrangeService extends IService<BuRepairTaskStaffArrange> {

    /**
     * 批量删除任务人员安排
     *
     * @param ids 任务人员安排ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 批量保存/更新任务人员安排
     *
     * @param staffArrangeList 任务人员安排列表
     * @return 任务人员安排
     * @throws Exception 异常
     */
    boolean saveStaffArrangeList(List<BuRepairTaskStaffArrange> staffArrangeList) throws Exception;

}
