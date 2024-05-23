package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanForms;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanFormsQueryVO;

import java.util.List;

/**
 * <p>
 * 列计划模板表单 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuTpRepairPlanFormsMapper extends BaseMapper<BuTpRepairPlanForms> {

    /**
     * 批量插入
     *
     * @param list 列计划模板表单列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanForms> list);

    /**
     * 根据条件分页查询计划模板表单
     *
     * @param queryVO 查询条件
     * @param page    分页信息
     * @return 计划模板表单分页结果
     */
    IPage<BuTpRepairPlanForms> selectPageByCondition(Page<BuTpRepairPlanForms> page, @Param("queryVO") BuTpRepairPlanFormsQueryVO queryVO);

    /**
     * 根据条件查询计划模板表单列表
     *
     * @param queryVO 查询条件
     * @return 计划模板表单列表
     */
    List<BuTpRepairPlanForms> selectListByCondition(@Param("queryVO") BuTpRepairPlanFormsQueryVO queryVO);

    /**
     * 根据id查询计划模板表单
     *
     * @param id 计划模板表单id
     * @return 计划模板表单
     */
    BuTpRepairPlanForms selectFormById(@Param("id") String id);

}
