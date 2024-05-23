package org.jeecg.modules.dispatch.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYear;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanFarYearQueryVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearVOWithGantt;

import java.util.List;

/**
 * <p>
 * 年计划 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
public interface BuRepairPlanYearMapper extends BaseMapper<BuRepairPlanYear> {

    /**
     * 根据条件分页查询年计划
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuRepairPlanYear> selectPageByCondition(IPage<BuRepairPlanYear> page, @Param("queryVO") BuRepairPlanFarYearQueryVO queryVO);

    /**
     * 根据id查询年计划和明细
     *
     * @param id 远期规划id
     * @return 年计划和明细
     */
    BuRepairPlanYear selectPlanYearWithDetailById(@Param("id") String id);

    /**
     * 根据条件查询年计划
     *
     * @param queryVO 查询条件
     * @return 年计划数组
     */
    List<BuRepairPlanYear> selectListByCondition(@Param("queryVO") BuRepairPlanFarYearQueryVO queryVO);

}