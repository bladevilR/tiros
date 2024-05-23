package org.jeecg.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.workflow.bean.WfBizStatus;

import java.util.List;

/**
 * <p>
 * 业务流程实列状态表 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-27
 */
public interface WfBizStatusMapper extends BaseMapper<WfBizStatus> {

    /**
     * 根据映射编码查询业务流程存在的任务节点名称
     *
     * @param solutionCode 映射编码
     * @return 任务节点名称列表
     */
    List<String> selectDistinctProcessStatusListBySolutionCode(@Param("solutionCode") String solutionCode);

}
