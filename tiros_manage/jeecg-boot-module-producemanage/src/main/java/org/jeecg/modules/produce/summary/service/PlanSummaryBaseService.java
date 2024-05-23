package org.jeecg.modules.produce.summary.service;

import org.jeecg.modules.produce.summary.bean.PlanBase;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 列计划总结 基础 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
public interface PlanSummaryBaseService {

    /**
     * 获取列计划基本信息
     *
     * @param planId 列计划id
     * @return 列计划基本信息
     * @throws Exception 异常
     */
    PlanBase getPlanBase(String planId) throws Exception;

    /**
     * 获取该线路、修程下的列计划基本信息列表
     *
     * @param planBase 列计划基本信息
     * @return 列计划基本信息列表
     */
    List<PlanBase> listLinePlanBase(PlanBase planBase);

    /**
     * 查询该线路下车型的系统数据
     *
     * @param planBase 列计划基本信息
     * @return 系统数据
     */
    Map<String, String> getLineSystemIdNameMap(PlanBase planBase);

    /**
     * 获取列计划年份和月份列表
     *
     * @param planBase  列计划基本信息
     * @param yearList  年份
     * @param monthList 月份
     */
    void getYearAndMonthList(PlanBase planBase, List<Integer> yearList, List<Integer> monthList);

}
