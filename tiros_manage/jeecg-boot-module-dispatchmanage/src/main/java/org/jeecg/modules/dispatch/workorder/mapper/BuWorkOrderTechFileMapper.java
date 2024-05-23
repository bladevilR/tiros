package org.jeecg.modules.dispatch.workorder.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskRegu;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTechFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 任务的工艺文件除了在这个表中找，还默认去列计划中取相应的工艺文件，合并后去重 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderTechFileMapper extends BaseMapper<BuWorkOrderTechFile> {

    /**
     * 批量插入
     *
     * @param list 工单工艺文件列表
     */
    void insertList(@Param("list") List<BuWorkOrderTechFile> list);

}
