package org.jeecg.modules.dispatch.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFarDetail;

import java.util.List;

/**
 * <p>
 * 远期规划明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
public interface BuRepairPlanFarDetailMapper extends BaseMapper<BuRepairPlanFarDetail> {

    /**
     * 根据远期规划id查询远期规划明细列表
     *
     * @param planFarId 远期规划id
     * @return 远期规划明细列表
     */
    List<BuRepairPlanFarDetail> selectListByPlanFarId(@Param("planFarId") String planFarId);

}
