package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderBookStep;

import java.util.List;

/**
 * <p>
 * 工单任务的作业指导书 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-17
 */
public interface BuWorkOrderBookStepMapper extends BaseMapper<BuWorkOrderBookStep> {

    /**
     * 批量插入
     *
     * @param list 工单任务的作业指导书列表
     */
    void insertList(@Param("list") List<BuWorkOrderBookStep> list);

}
