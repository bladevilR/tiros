package org.jeecg.modules.dispatch.specassetplan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.specassetplan.bean.BuSpecAssetPlan;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetMonthUsageQueryVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanCheckVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanQueryVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanVO;

import java.util.LinkedHashMap;


/**
 * <p>
 * 特种设备运用/保养计划 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-03
 */
public interface BuSpecAssetPlanService extends IService<BuSpecAssetPlan> {

    /**
     * 根据条件分页查询特种设备运用/保养计划
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuSpecAssetPlan> page(BuSpecAssetPlanQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询特种设备运用/保养计划
     *
     * @param id 特种设备运用/保养计划id
     * @return 特种设备运用/保养计划
     * @throws Exception 异常
     */
    BuSpecAssetPlan getSpecAssetPlanById(String id) throws Exception;

    /**
     * 特种设备运用/保养计划冲突检查
     *
     * @param checkVO 检查条件
     * @return 冲突检查结果 true表示没有冲突，false表示有冲突，有冲突时会抛出异常信息
     * @throws Exception 异常
     */
    boolean checkConflict(BuSpecAssetPlanCheckVO checkVO) throws Exception;

    /**
     * 新增特种设备运用/保养计划
     *
     * @param specAssetPlan 特种设备运用/保养计划
     * @return 是否新增成功
     * @throws Exception 异常
     */
    boolean addSpecAssetPlan(BuSpecAssetPlan specAssetPlan) throws Exception;

    /**
     * 修改特种设备运用/保养计划
     *
     * @param specAssetPlan 特种设备运用/保养计划
     * @return 是否修改成功
     * @throws Exception 异常
     */
    boolean updateSpecAssetPlan(BuSpecAssetPlan specAssetPlan) throws Exception;

    /**
     * 批量删除特种设备运用/保养计划
     *
     * @param ids 特种设备运用/保养计划ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 获取特种设备月份使用情况
     *
     * @param usageQueryVO 特种设备id+年月
     * @return 月份使用情况
     * @throws Exception 异常
     */
    LinkedHashMap<String, BuSpecAssetPlanVO> getSpecAssetMonthUsage(BuSpecAssetMonthUsageQueryVO usageQueryVO) throws Exception;

}
