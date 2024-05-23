package org.jeecg.common.tiros.rpt.service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 更新列计划进度 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
public interface PlanProgressUpdateService {

    /**
     * 更新列计划任务--开始
     *
     * @param planTaskIdList 列计划任务id列表 不传参不更新
     * @param now            当前时间，不传在方法内new获取
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean updatePlanTaskStart(List<String> planTaskIdList, Date now) throws RuntimeException;

    /**
     * 更新列计划任务--结束
     *
     * @param planTaskIdList 列计划任务id列表 不传参不更新
     * @param now            当前时间，不传在方法内new获取
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean updatePlanTaskFinish(List<String> planTaskIdList, Date now) throws RuntimeException;

    /**
     * 更新列计划开始--进度状态和实际开始时间
     *
     * @param planIdList 列计划id列表 不传参不更新
     * @param now        当前时间，不传在方法内new获取
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean updatePlanStart(List<String> planIdList, Date now) throws RuntimeException;

    /**
     * 更新列计划进度及状态
     *
     * @param planIdList 列计划id列表 不传参更新所有
     * @param now        当前时间，不传在方法内new获取
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean updatePlanProgressAndStatus(List<String> planIdList, Date now) throws RuntimeException;

}
