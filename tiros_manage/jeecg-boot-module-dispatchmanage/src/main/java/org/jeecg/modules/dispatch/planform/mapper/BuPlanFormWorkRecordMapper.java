package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormWorkRecord;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTaskFormInst;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划表单实列(作业记录) Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuPlanFormWorkRecordMapper extends BaseMapper<BuPlanFormWorkRecord> {

    /**
     * 更新作业记录表实例
     *
     * @param id             id
     * @param structDetailId 车辆结构明细id
     * @param title          标题
     */
    int updateStructAndTitleById(@Param("id") String id, @Param("structDetailId") String structDetailId, @Param("title") String title);

    /**
     * 更新作业记录表实例
     *
     * @param idList     id列表
     * @param finishDate 结束日期
     */
    int updateFinishDateByIdList(@Param("idList") List<String> idList, @Param("finishDate") Date finishDate);

    /**
     * 根据工单任务与表单实例关联id查询作业记录信息及明细
     *
     * @param task2InstId 工单任务与表单实例关联id
     * @return 作业记录信息及明细
     */
    BuPlanFormWorkRecord selectWorkRecordWithDetailByTask2InstId(@Param("task2InstId") String task2InstId, @Param("allItems") Boolean allItems);

    /**
     * 根据工单任务与表单实例关联id查询作业记录信息及明细及检查
     *
     * @param task2InstId 工单任务与表单实例关联id
     * @return 作业记录信息及明细及检查
     */
    BuPlanFormWorkRecord selectWorkRecordWithDetailCheckByTask2InstId(@Param("task2InstId") String task2InstId, @Param("allItems") Boolean allItems);

    /**
     * 根据列计划表单实列(作业记录)id查询作业记录信息及明细
     *
     * @param instId 表单实例id
     * @return 作业记录信息及明细
     */
    List<BuPlanFormWorkRecord> selectWorkRecordWithDetailByInstId(@Param("instId") String instId, @Param("allItems") Boolean allItems);

    /**
     * 根据列计划表单实列(作业记录)id查询作业记录信息及明细及检查
     *
     * @param instId 表单实例id
     * @return 作业记录信息及明细及检查
     */
    List<BuPlanFormWorkRecord> selectWorkRecordWithDetailCheckByInstId(@Param("instId") String instId, @Param("allItems") Boolean allItems);

    /**
     * 根据工单id查询工单作业记录表列表
     *
     * @param orderId 工单id
     * @return 工单作业记录表列表
     */
    List<BuWorkOrderTaskFormInst> selectListByOrderId(@Param("orderId") String orderId);

    /**
     * 根据表单id获取datajson
     *
     * @param formTemplateId 表单id
     * @return 表单datajson
     */
    String selectFormTemplateDataJsonByFormTemplateId(@Param("formTemplateId") String formTemplateId);

}
