package org.jeecg.modules.dispatch.serialplan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairReguDetail;
import org.jeecg.modules.dispatch.serialplan.bean.MustMaterialLack;
import org.jeecg.modules.dispatch.serialplan.bean.tp.BuTpRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanQueryVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanRelationVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanVOGantt;
import org.jeecg.modules.dispatch.serialplan.bean.vo.PlanProgressVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 列计划 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairPlanService extends IService<BuRepairPlan> {

    /**
     * 分页查询
     *
     * @param queryVO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<BuRepairPlan> page(BuRepairPlanQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据列计划id查询计划详情（含计划的任务）
     *
     * @param id 列计划id
     * @return 计划详情（含计划的任务）
     * @throws Exception 异常
     */
    BuRepairPlanVOGantt selectPlanAndTask(String id) throws Exception;

    /**
     * 新增列计划
     *
     * @param buRepairPlan
     * @return
     */
    boolean savePlan(BuRepairPlan buRepairPlan) throws Exception;

    /**
     * 编辑列计划
     *
     * @param buRepairPlan
     * @return
     */
    boolean editPlan(BuRepairPlan buRepairPlan) throws Exception;

    /**
     * 重新生成列计划任务及任务相关的信息
     *
     * @param planId
     * @return
     */
    boolean rebuild(String planId, Integer beginDayIndex);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 批量暂停列计划
     *
     * @param ids 列计划ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean suspendPlanBatch(String ids) throws Exception;

    /**
     * 批量激活列计划
     *
     * @param ids 列计划ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean activatePlanBatch(String ids) throws Exception;

    /**
     * 查询交接车记录
     *
     * @return
     */
    List<BuRepairExchang> selectRePairExchang(String lineId) throws Exception;

    /**
     * 根据接车对应的序号，自动带出在年计划中的计划开始日期
     *
     * @param trainIndex 车辆序号
     * @param lineId     线路ID
     * @return
     */
    BuRepairPlanYearDetail selectRepairYearStartDate(int trainIndex, String lineId) throws Exception;

    /**
     * 查询计划模板
     *
     * @return
     */
    List<BuTpRepairPlan> selectTpRepairPlan() throws Exception;


    /**
     * 计划关联的信息
     *
     * @return
     */
    BuRepairPlanRelationVO selectPlanRelevanceInfo(String planId) throws Exception;

    /**
     * 任务没有关联的规程数
     *
     * @param planId 列计划id
     * @return 规程数
     * @throws Exception 异常
     */
    int noRelevanceCount(String planId, String reguId) throws Exception;

    /**
     * 任务没有关联的规程详情
     *
     * @param planId 列计划id
     * @return 规程详情
     * @throws Exception 异常
     */
    List<BuRepairReguDetail> noRelevanceDetail(String planId, String reguId) throws Exception;

    /**
     * 根据id查询列计划未完成任务信息
     *
     * @param planId  列计划id
     * @param groupId 班组id
     * @param status  任务状态
     * @return 未完成任务信息
     * @throws Exception 异常
     */
    PlanProgressVO getPlanUnFinishTaskInfo(String planId, String groupId, Integer status) throws Exception;

    /**
     * 设置列计划任务为已完成
     *
     * @param planId      列计划id
     * @param planTaskIds 列计划任务ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean setPlanTaskFinish(String planId, String planTaskIds) throws Exception;

    /**
     * excel导入历史列计划和接车
     *
     * @param excelFile    excel文件
     * @param clearOldData 是否清空旧的历史数据
     * @return 导入结果
     * @throws Exception 异常信息
     */
    String importHistoryDataFromExcel(MultipartFile excelFile, Boolean clearOldData) throws Exception;

    /**
     * excel导入列计划历史成本数据
     *
     * @param excelFile excel文件
     * @param planId    列计划id
     * @return 导入结果
     * @throws Exception 异常信息
     */
    String importPlanHistoryCostDataFromExcel(MultipartFile excelFile, String planId) throws Exception;

    /**
     * 查询当前列计划缺料
     *
     * @return
     */
    List<MustMaterialLack> mustMaterialLack() throws Exception;

}
