package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairReguDetail;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskWorkstation;
import org.jeecg.modules.dispatch.serialplan.bean.MustMaterialLack;
import org.jeecg.modules.dispatch.serialplan.bean.tp.BuTpRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanQueryVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanRelationVO;
import org.jeecg.modules.dispatch.workorder.bean.BuMtrWorkshopGroup;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 列计划 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairPlanMapper extends BaseMapper<BuRepairPlan> {
    /**
     * 查询编码最大值
     *
     * @return Integer
     */
    Integer selectMaxCodeOfCurrentPlanData();

    /**
     * 查询计划模板
     *
     * @return
     */
    List<BuTpRepairPlan> selectTpRepairPlan();

    IPage<BuRepairPlan> selectPageByCondition(Page<BuRepairPlan> page, @Param("plan") BuRepairPlanQueryVO plan);

    /**
     * 查询任务总数
     *
     * @param planId
     * @return
     */
    Integer selectTaskCount(@Param("planId") String planId);

    /**
     * 查询已完成的任务总数
     *
     * @param planId
     * @return
     */
    Integer selectTaskSuccessCount(@Param("planId") String planId);

    BuRepairPlanRelationVO selectPlanRelevanceInfo(@Param("id") String id);

    /**
     * 根据列计划id查询计划详情
     *
     * @param planId 列计划id
     * @return 计划详情
     */
    BuRepairPlan selectPlanByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询计划详情
     *
     * @param planIdList 列计划id列表
     * @return 计划详情列表
     */
    List<BuRepairPlan> selectPlanByPlanIdList(@Param("planIdList") List<String> planIdList);

    /**
     * 根据列计划id查询计划关联工班信息
     *
     * @param id 列计划id
     * @return 计划工班关联信息
     */
    List<BuRepairTaskWorkstation> selectPlanWorkstations(@Param("id") String id);

    int noRelevanceCount(@Param("planId") String planId, @Param("reguId") String reguId);

    List<BuRepairReguDetail> noRelevanceDetail(@Param("planId") String planId, @Param("reguId") String reguId);

    /**
     * 查询未完成的列计划
     *
     * @param planId 列计划id
     * @return 列计划列表
     */
    List<BuRepairPlan> selectApprovedNotFinishedPlanList(@Param("planId") String planId);

    /**
     * 根据列计划id获取列计划关联的规程明细id列表
     *
     * @param planId 列计划id
     * @return 列计划关联的规程明细id列表
     */
    List<String> selectRelationReguDetailIdListByPlanId(@Param("planId") String planId);

    /**
     * 查询所有班组信息
     *
     * @return 班组列表
     */
    List<BuMtrWorkshopGroup> selectAllWorkGroupList();

    Double selectTrainMileageByTrainNo(@Param("trainNo") String trainNo);

    /**
     * 查询当前列计划缺料
     *
     * @return
     */
    List<MustMaterialLack> mustMaterialLack();

    /**
     * 查询开始日期在指定年份的已完成的列计划
     *
     * @param year 年份
     * @return 已完成的列计划列表
     */
    List<BuRepairPlan> selectFinishedListByYear(@Param("year") Integer year);

    List<Map<String, String>> selectDepotNameId();

}
