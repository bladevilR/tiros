package org.jeecg.modules.dispatch.toolplan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.toolplan.bean.BuToolPlan;
import org.jeecg.modules.dispatch.toolplan.bean.vo.BuToolPlanCheckVO;
import org.jeecg.modules.dispatch.toolplan.bean.vo.BuToolPlanQueryVO;

/**
 * <p>
 * 工具(装)运用/保养计划 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-09
 */
public interface BuToolPlanService extends IService<BuToolPlan> {

    /**
     * 根据条件分页查询工具(装)运用/保养计划
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuToolPlan> page(BuToolPlanQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 工装运用/保养计划冲突检查
     *
     * @param checkVO 检查条件
     * @return 冲突检查结果 true表示没有冲突，false表示有冲突，有冲突时会抛出异常信息
     * @throws Exception 异常信息
     */
    boolean checkConflict(BuToolPlanCheckVO checkVO) throws Exception;

    /**
     * 新增工具(装)运用/保养计划
     *
     * @param buToolPlan 工具(装)运用/保养计划
     * @return 是否新增成功
     * @throws Exception 异常信息
     */
    boolean saveToolPlan(BuToolPlan buToolPlan) throws Exception;

    /**
     * 修改工具(装)运用/保养计划
     *
     * @param buToolPlan 工具(装)运用/保养计划
     * @return 是否修改成功
     * @throws Exception 异常信息
     */
    boolean updateToolPlan(BuToolPlan buToolPlan) throws Exception;

    /**
     * 批量删除工具(装)运用/保养计划
     *
     * @param ids 工具(装)运用/保养计划ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
