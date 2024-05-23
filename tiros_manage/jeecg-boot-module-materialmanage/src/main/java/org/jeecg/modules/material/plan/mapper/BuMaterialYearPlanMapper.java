package org.jeecg.modules.material.plan.mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.plan.bean.BuMaterialYearPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.material.plan.bean.vo.BuMaterialYearPlanQueryVO;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2021 -05-01
 */
public interface BuMaterialYearPlanMapper extends BaseMapper<BuMaterialYearPlan> {

    /**
     * Material year plan page page.
     *
     * @param page    the page
     * @param queryVO the query vo
     * @return the page
     */
    Page<BuMaterialYearPlan> materialYearPlanPage( Page<BuMaterialYearPlan> page,@Param("queryVO")BuMaterialYearPlanQueryVO queryVO);

    /**
     * Select by query vo list.
     *
     * @param queryVO the query vo
     * @return the list
     */
    List<BuMaterialYearPlan> selectBYQueryVO(@Param("queryVO") BuMaterialYearPlanQueryVO queryVO);

    /**
     * Select work group id string.
     *
     * @param orgCode the org code
     * @return the string
     */
    String selectWorkGroupId(String orgCode);
}
