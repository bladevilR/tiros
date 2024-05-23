package org.jeecg.modules.basemanage.tpplan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguDetail;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlan;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanCopyVO;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanQueryVO;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanRelationVO;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanVOGantt;

import java.io.IOException;

/**
 * <p>
 * 维修计划模版 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanService extends IService<BuTpRepairPlan> {

    /**
     * 根据条件分页查询计划模板
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuTpRepairPlan> pageTpPlan(BuTpRepairPlanQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 新增或修改计划模板
     *
     * @param tpRepairPlan 计划模板
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveOrUpdatePlan(BuTpRepairPlan tpRepairPlan) throws Exception;

    /**
     * 批量删除计划模板
     *
     * @param ids 计划模板ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteByIds(String ids) throws Exception;

    /**
     * 批量设置计划模板有效
     *
     * @param ids 计划模板ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean validPlan(String ids) throws Exception;

    /**
     * 批量设置计划模板无效
     *
     * @param ids 计划模板ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean invalidPlan(String ids) throws Exception;

    /**
     * 根据计划模板id查询详情，返回甘特图及任务信息
     *
     * @param id 计划模板id
     * @return 计划模板甘特图及任务信息
     */
    BuTpRepairPlanVOGantt selectPlanAndTask(String id) throws Exception;

    /**
     * 根据计划模板id查询关联信息
     *
     * @param tpPlanId 计划模板id
     * @return 计划模板关联信息
     */
    BuTpRepairPlanRelationVO selectPlanRelation(String tpPlanId) throws Exception;

    /**
     * 规程没有关联具体任务数
     *
     * @param planId 计划模板id
     * @param reguId 规程id
     * @return 没有关联具体任务数
     * @throws Exception 异常
     */
    int noRelevanceCount(String planId, String reguId) throws Exception;

    /**
     * 规程没有关联具体任务详情
     *
     * @param planId 计划模板id
     * @param reguId 规程id
     * @return 具体任务详情
     * @throws Exception 异常
     */
    Page<BuRepairReguDetail> noRelevanceDetail(Page<BuRepairReguDetail> page, String planId, String reguId) throws Exception;

    /**
     * 复制计划模板
     *
     * @param planCopyVO 复制信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean copyPlan(BuTpRepairPlanCopyVO planCopyVO) throws Exception;

    /**
     * 批量删除计划模板任务不删除模板
     *
     * @param ids 计划模板ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteTaskByIds(String ids) throws Exception;

    /**
     * 刷新规程的物料工器具到计划模板中
     *
     * @param tpPlanId 计划模板id
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean flushReguRelationToTpPlan(String tpPlanId) throws Exception;

    /**
     * @param tpPlanId 计划模板id
     * @return
     */
    HSSFWorkbook exportPlanInfo(String tpPlanId) throws IOException, Exception;

    /**
     * @return
     * @throws Exception
     */
    Boolean syncMustReplace() throws Exception;

}
