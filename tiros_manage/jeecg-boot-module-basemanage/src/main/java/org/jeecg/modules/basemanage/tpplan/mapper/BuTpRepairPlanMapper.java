package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguDetail;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlan;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanQueryVO;

import java.util.List;

/**
 * <p>
 * 维修计划模版 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanMapper extends BaseMapper<BuTpRepairPlan> {

    /**
     * 根据条件分页查询计划模板
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuTpRepairPlan> selectPageByCondition(IPage<BuTpRepairPlan> page, @Param("queryVO") BuTpRepairPlanQueryVO queryVO);

    /**
     * 根据计划模板id查询计划模版详情
     *
     * @param planId 计划模板id
     * @return 计划模版详情
     */
    BuTpRepairPlan selectPlanByPlanId(@Param("planId") String planId);

    /**
     * No relevance count int.
     *
     * @param planId the plan id
     * @param reguId the regu id
     * @return the int
     */
    int noRelevanceCount(@Param("planId") String planId, @Param("reguId") String reguId);

    /**
     * No relevance detail list.
     *
     * @param page   the page
     * @param planId the plan id
     * @param reguId the regu id
     * @return the list
     */
    List<BuRepairReguDetail> noRelevanceDetail(Page<BuRepairReguDetail> page, @Param("planId") String planId, @Param("reguId") String reguId);

    /**
     * Select max task no by parent id integer.
     *
     * @param parentId the parent id
     * @return the integer
     */
    Integer selectMaxTaskNoByParentId(@Param("parentId") String parentId);

}