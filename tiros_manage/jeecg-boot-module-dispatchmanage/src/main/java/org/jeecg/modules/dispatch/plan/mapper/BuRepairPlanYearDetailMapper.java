package org.jeecg.modules.dispatch.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.plan.bean.bo.BuTpRepairPlanBO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 年计划明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
public interface BuRepairPlanYearDetailMapper extends BaseMapper<BuRepairPlanYearDetail> {

    /**
     * 根据年计划id查询年计划明细列表
     *
     * @param planYearId 年计划id
     * @return 年计划明细列表
     */
    List<BuRepairPlanYearDetail> selectListByPlanYearId(String planYearId);

    /**
     * 根据计划模板id获取计划模板部分信息
     *
     * @param tpRepairPlanId 计划模板id
     * @return 计划模板部分信息
     */
    BuTpRepairPlanBO selectBuTpRepairPlanBOByTpRepairPlanId(@Param("tpRepairPlanId") String tpRepairPlanId);

    /**
     * 根据计划模板id获取计划模板工期序号和对应作业班组
     *
     * @param tpRepairPlanId tpRepairPlanId
     * @return 计划模板工期序号和对应作业班组
     */
    List<Map<String, Object>> selectTpRepairPlanDayIndexAndGroupByTpRepairPlanId(@Param("tpRepairPlanId") String tpRepairPlanId);

    /**
     * 根据车间id获取车间能力
     *
     * @param workshopId 车间id
     * @return 车间能力
     */
    Integer selectWorkshopAbilityByWorkshopId(@Param("workshopId") String workshopId);

    /**
     * 根据年份查询年计划明细
     *
     * @param year   年份
     * @param status 年计划明细状态
     * @return 年计划明细
     */
    List<BuRepairPlanYearDetail> selectListByYearAndStatus(@Param("year") Integer year, @Param("status") Integer status);

    /**
     * 根据线路、车辆序号、年份查询年计划明细
     *
     * @param lineId     线路id
     * @param trainIndex 车辆序号
     * @param year       年份
     * @return 年计划明细
     */
    List<BuRepairPlanYearDetail> selectListByLineAndTrainIndexAndYear(@Param("lineId") String lineId, @Param("trainIndex") Integer trainIndex, @Param("year") Integer year);

    /**
     * 根据年份和修程id获取年计划中该年份和修程的维修数量
     *
     * @param year      年份
     * @param programId 修程id
     * @return 维修数量
     */
    Integer selectAmountSumByYearAndProgramId(@Param("year") Integer year, @Param("programId") String programId);

}
