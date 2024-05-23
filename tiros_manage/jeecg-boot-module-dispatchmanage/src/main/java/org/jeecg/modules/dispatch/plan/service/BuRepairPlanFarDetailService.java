package org.jeecg.modules.dispatch.plan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFarDetail;

import java.util.List;

/**
 * <p>
 * 远期规划明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
public interface BuRepairPlanFarDetailService extends IService<BuRepairPlanFarDetail> {

    /**
     * 根据远期规划id查询远期规划明细列表
     *
     * @param planFarId 远期规划id
     * @return 远期规划明细列表
     * @throws Exception 异常信息
     */
    List<BuRepairPlanFarDetail> listByPlanFarId(String planFarId) throws Exception;

}
