package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderAnnex;

import java.util.List;

/**
 * <p>
 * 工单附件 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-19
 */
public interface BuWorkOrderAnnexMapper extends BaseMapper<BuWorkOrderAnnex> {

    /**
     * 批量插入
     *
     * @param list 工单附件列表
     */
    void insertList(@Param("list") List<BuWorkOrderAnnex> list);

    /**
     * 根据工单任务id查询工单附件，
     * 显示关联到这个任务id的附件+这个工单未指定任务id的附件
     *
     * @param taskId 工单任务id
     * @return 工单附件
     */
    List<BuWorkOrderAnnex> selectListByTaskId(@Param("taskId") String taskId);

}
