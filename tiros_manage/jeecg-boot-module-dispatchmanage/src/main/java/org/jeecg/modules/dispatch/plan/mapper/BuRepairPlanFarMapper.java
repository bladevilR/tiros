package org.jeecg.modules.dispatch.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFar;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanFarYearQueryVO;

import java.util.List;

/**
 * <p>
 * 远期规划 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
public interface BuRepairPlanFarMapper extends BaseMapper<BuRepairPlanFar> {

    /**
     * 根据条件分页查询远期规划
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuRepairPlanFar> selectPageByCondition(IPage<BuRepairPlanFar> page, @Param("queryVO") BuRepairPlanFarYearQueryVO queryVO);

    /**
     * 根据id查询远期规划和明细
     *
     * @param id 远期规划id
     * @return 远期规划和明细
     */
    BuRepairPlanFar selectById(@Param("id") String id);

    /**
     * 根据年份和车辆段查询远期规划
     *
     * @param year    年份
     * @param depotId 车辆段id
     * @return 远期规划列表
     */
    List<BuRepairPlanFar> selectListByYearAndDepotId(@Param("year") Integer year, @Param("depotId") String depotId);

}
