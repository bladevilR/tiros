package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormCheckRecord;

import java.util.List;

/**
 * <p>
 * 作业检查记录表（实例） Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
public interface BuPlanFormCheckRecordMapper extends BaseMapper<BuPlanFormCheckRecord> {

    /**
     * 更新数据记录表实例
     *
     * @param id             id
     * @param structDetailId 车辆结构明细id
     * @param title          标题
     */
    int updateStructAndTitleById(@Param("id") String id, @Param("structDetailId") String structDetailId, @Param("title") String title);

    /**
     * 根据工单任务与表单实例关联id查询作业检查记录表（实例）信息及检查项
     *
     * @param task2InstId 工单任务与表单实例关联id
     * @return 作业检查记录表（实例）信息及检查项
     */
    BuPlanFormCheckRecord selectCheckRecordWithItemByTask2InstId(@Param("task2InstId") String task2InstId, @Param("allItems") Boolean allItems);

    /**
     * 根据工单任务与表单实例关联id查询作业检查记录表（实例）信息及检查项及关联整改
     *
     * @param task2InstId 工单任务与表单实例关联id
     * @return 作业检查记录表（实例）信息及检查项及关联整改
     */
    BuPlanFormCheckRecord selectCheckRecordWithItemRectifyByTask2InstId(@Param("task2InstId") String task2InstId, @Param("allItems") Boolean allItems);

    /**
     * 根据工单任务与表单实例关联id查询作业检查记录表（实例）信息及检查项
     *
     * @param instId 作业检查记录表（实例）id
     * @return 作业检查记录表（实例）信息及检查项
     */
    List<BuPlanFormCheckRecord> selectCheckRecordWithItemByInstId(@Param("instId") String instId, @Param("allItems") Boolean allItems);

    /**
     * 根据工单任务与表单实例关联id查询作业检查记录表（实例）信息及检查项及关联整改
     *
     * @param instId 作业检查记录表（实例）id
     * @return 作业检查记录表（实例）信息及检查项及关联整改
     */
    List<BuPlanFormCheckRecord> selectCheckRecordWithItemRectifyByInstId(@Param("instId") String instId, @Param("allItems") Boolean allItems);

    String selectRecordIds(@Param("id") String id);

}
