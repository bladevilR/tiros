package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanForms;
import org.jeecg.modules.dispatch.planform.bean.BuWorkRecordDetail;
import org.jeecg.modules.dispatch.planform.bean.PlanFormInstance;
import org.jeecg.modules.dispatch.planform.bean.vo.BuRepairPlanFormsQueryVO;
import org.jeecg.modules.dispatch.planform.bean.vo.BuRepairPlanFormsInstQueryVO;

import java.util.List;

/**
 * <p>
 * 列计划表单，当列计划审批通过后，根据该配置表自动生成相关数据记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuRepairPlanFormsMapper extends BaseMapper<BuRepairPlanForms> {

    /**
     * 批量插入
     *
     * @param list 列计划表单列表
     */
    void insertList(@Param("list") List<BuRepairPlanForms> list);

    /**
     * 根据条件分页查询列计划表单
     *
     * @param queryVO 查询条件
     * @param page    分页信息
     * @return 列计划表单分页结果
     */
    IPage<BuRepairPlanForms> selectPageWithChoiceByCondition(Page<BuRepairPlanForms> page, @Param("queryVO") BuRepairPlanFormsQueryVO queryVO);

    /**
     * 根据条件查询列计划表单列表
     *
     * @param queryVO 查询条件
     * @return 列计划表单列表
     */
    List<BuRepairPlanForms> selectListWithChoiceByCondition(@Param("queryVO") BuRepairPlanFormsQueryVO queryVO);

    /**
     * 根据列计划id列表查询列计划表单列表
     *
     * @param planIdList 列计划id列表
     * @return 列计划表单列表
     */
    List<BuRepairPlanForms> selectPlanFormsListWithChoiceByPlanIdList(@Param("planIdList") List<String> planIdList);

    /**
     * 根据id查询列计划表单
     *
     * @param id 列计划表单id
     * @return 列计划表单
     */
    BuRepairPlanForms selectFormById(@Param("id") String id);

    /**
     * 根据作业记录表id查询作业记录明细
     *
     * @param workRecordId 作业记录表id
     * @return 作业记录明细列表
     */
    List<BuWorkRecordDetail> selectBuWorkRecordDetailByWorkRecordId(@Param("workRecordId") String workRecordId);

    String selectUserGroupId(@Param("userId") String userId);

    List<PlanFormInstance> selectPlanFormInstList(@Param("queryVO") BuRepairPlanFormsInstQueryVO queryVO);

}
