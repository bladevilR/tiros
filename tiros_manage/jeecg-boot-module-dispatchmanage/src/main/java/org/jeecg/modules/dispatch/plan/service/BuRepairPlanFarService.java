package org.jeecg.modules.dispatch.plan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFar;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanFarYearQueryVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearAmountVO;

/**
 * <p>
 * 远期规划 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
public interface BuRepairPlanFarService extends IService<BuRepairPlanFar> {

    /**
     * 根据条件分页查询远期规划
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuRepairPlanFar> page(BuRepairPlanFarYearQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询远期规划及明细
     *
     * @param id 远期规划id
     * @return 远期规划及明细
     * @throws Exception 异常信息
     */
    BuRepairPlanFar selectById(String id) throws Exception;

    /**
     * 保存远期规划和明细
     *
     * @param buRepairPlanFar 远期规划和明细
     * @return 是否保存成功
     * @throws Exception 异常信息
     */
    boolean savePlanFar(BuRepairPlanFar buRepairPlanFar) throws Exception;

    /**
     * 修改远期规划和明细
     *
     * @param buRepairPlanFar 远期规划和明细
     * @return 是否修改成功
     * @throws Exception 异常信息
     */
    boolean updatePlanFar(BuRepairPlanFar buRepairPlanFar) throws Exception;

    /**
     * 批量删除远期规划记录
     *
     * @param ids 远期规划记录ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 根据年份和车辆段获取修程数量
     *
     * @param year    年份
     * @param depotId 车辆段id
     * @return 修程数量信息
     * @throws Exception 异常信息
     */
    BuRepairPlanYearAmountVO getPlanAmountByYearAndDepotId(Integer year, String depotId) throws Exception;

}
