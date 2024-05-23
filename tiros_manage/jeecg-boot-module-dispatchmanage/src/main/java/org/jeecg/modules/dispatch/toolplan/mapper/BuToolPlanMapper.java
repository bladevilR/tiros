package org.jeecg.modules.dispatch.toolplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.toolplan.bean.BuToolPlan;
import org.jeecg.modules.dispatch.toolplan.bean.vo.BuToolPlanCheckVO;
import org.jeecg.modules.dispatch.toolplan.bean.vo.BuToolPlanQueryVO;

import java.util.List;

/**
 * <p>
 * 工具(装)运用/保养计划 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-09
 */
public interface BuToolPlanMapper extends BaseMapper<BuToolPlan> {

    /**
     * 根据条件分页查询工具(装)运用/保养计划
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuToolPlan> selectPageByCondition(IPage<BuToolPlan> page, @Param("queryVO") BuToolPlanQueryVO queryVO);

    /**
     * 查询冲突的工装运用/保养计划
     *
     * @param checkVO 查询条件
     * @return 查询冲突结果
     */
    List<BuToolPlan> selectConflictToolPlanList(@Param("checkVO") BuToolPlanCheckVO checkVO);

}
