package org.jeecg.modules.dispatch.plan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYear;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanFarYearQueryVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearVOWithGantt;

/**
 * <p>
 * 年计划 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
public interface BuRepairPlanYearService extends IService<BuRepairPlanYear> {

    /**
     * 根据条件分页查询年计划
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuRepairPlanYear> pagePlanYear(BuRepairPlanFarYearQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询年计划和明细
     *
     * @param id 年计划id
     * @return 年计划和明细
     * @throws Exception 异常
     */
    BuRepairPlanYear getPlanYearById(String id) throws Exception;

    /**
     * 根据年计划id查询年计划和明细列表，返回甘特图对象
     *
     * @param id 年计划id
     * @return 年计划和明细列表，甘特图对象
     * @throws Exception 异常
     */
    BuRepairPlanYearVOWithGantt getYearGanttWithDetailGantt(String id) throws Exception;

    /**
     * 保存年计划和明细
     *
     * @param yearVOWithGantt 年计划和明细甘特图对象
     * @return 是否保存成功
     * @throws Exception 异常
     */
    boolean savePlanYear(BuRepairPlanYearVOWithGantt yearVOWithGantt) throws Exception;

    /**
     * 修改年计划和明细
     *
     * @param yearVOWithGantt 年计划和明细甘特图对象
     * @return 是否修改成功
     * @throws Exception 异常
     */
    boolean updatePlanYear(BuRepairPlanYearVOWithGantt yearVOWithGantt) throws Exception;

    /**
     * 批量删除年计划记录
     *
     * @param ids 年计划记录ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 根据年份和修程id获取年计划中该年份和修程的维修数量，该数量取自年计划明细
     *
     * @param year      年份
     * @param programId 修程id
     * @return 维修数量
     * @throws Exception 异常
     */
    int sumRepairPlanYearDetailAmountByYearAndProgramId(Integer year, String programId) throws Exception;

    /**
     * Export exportYearPlanExport hssf workbook.
     *
     * @param queryVO the query vo
     * @return the hssf workbook
     */
    HSSFWorkbook exportYearPlanExport(BuRepairPlanFarYearQueryVO queryVO) throws Exception;

}
