package org.jeecg.modules.basemanage.tpplan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanForms;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanFormsQueryVO;

import java.util.List;

/**
 * <p>
 * 列计划模板表单 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuTpRepairPlanFormsService extends IService<BuTpRepairPlanForms> {

    /**
     * 根据条件分页查询计划模板表单
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 计划模板表单分页结果
     * @throws Exception 异常
     */
    IPage<BuTpRepairPlanForms> pageForms(BuTpRepairPlanFormsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据条件查询计划模板表单列表
     *
     * @param queryVO 查询条件
     * @return 计划模板表单列表
     * @throws Exception 异常
     */
    List<BuTpRepairPlanForms> listForms(BuTpRepairPlanFormsQueryVO queryVO) throws Exception;

    /**
     * 根据id查询计划模板表单
     *
     * @param id 计划模板表单id
     * @return 计划模板表单
     * @throws Exception 异常
     */
    BuTpRepairPlanForms getFormById(String id) throws Exception;

    /**
     * 删除计划模板表单(批量)
     *
     * @param ids 计划模板表单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 保存表单关联
     *
     * @param buTpRepairPlanForms 表单关联对象
     * @return 是否成功
     */
    boolean saveForm(BuTpRepairPlanForms buTpRepairPlanForms);

    /**
     * 更新表单关联
     *
     * @param buTpRepairPlanForms 表单关联对象
     * @return 是否成功
     */
    boolean updateFormById(BuTpRepairPlanForms buTpRepairPlanForms);

}
