package org.jeecg.modules.workflow.mapper;

import org.jeecg.common.workflow.bean.WorkFlowGroup;

import java.util.List;

/**
 * <p>
 * 工作流 Mapper接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/28
 */
public interface WorkFlowSyncMapper {

    /**
     * 获取所有部门信息(含子部门和人员)，作为工作流组
     *
     * @return 工作流组
     */
    List<WorkFlowGroup> selectAllTopDepartAndUserRecursion();

    /**
     * 获取所有角色信息(含人员)，作为工作流组
     *
     * @return 工作流组
     */
    List<WorkFlowGroup> selectAllRoleAndUserRecursion();

}
