package org.jeecg.modules.material.plan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.material.plan.bean.BuMaterialYearPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.plan.bean.vo.BuMaterialYearPlanQueryVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author youGen
 * @since 2021 -05-01
 */
public interface BuMaterialYearPlanService extends IService<BuMaterialYearPlan> {

    /**
     * Material year plan page page.
     *
     * @param queryVO the query vo
     * @param page    the page
     * @param isAll   the is all
     * @return the page
     */
    Page<BuMaterialYearPlan> materialYearPlanPage(BuMaterialYearPlanQueryVO queryVO, Page<BuMaterialYearPlan> page);

    /**
     * Add material year plan boolean.
     *
     * @param materialYearPlan the material year plan
     * @return the boolean
     * @throws Exception the exception
     */
    boolean addMaterialYearPlan(BuMaterialYearPlan materialYearPlan) throws Exception;

    /**
     * Update material year plan boolean.
     *
     * @param materialYearPlan the material year plan
     * @return the boolean
     * @throws Exception the exception
     */
    boolean updateMaterialYearPlan(BuMaterialYearPlan materialYearPlan) throws Exception;

    /**
     * Delete batch boolean.
     *
     * @param ids the ids
     * @return the boolean
     * @throws Exception the exception
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * Export material year plan hssf workbook.
     *
     * @param queryVO the query vo
     * @return the hssf workbook
     * @throws Exception the exception
     */
    HSSFWorkbook exportMaterialYearPlan(BuMaterialYearPlanQueryVO queryVO) throws Exception;

    /**
     * 获取车辆数量
     * @return
     */
    Integer getMaterialYearTrain();
}
