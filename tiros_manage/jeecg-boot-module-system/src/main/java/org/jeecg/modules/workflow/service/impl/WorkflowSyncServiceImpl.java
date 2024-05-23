package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.workflow.bean.WorkFlowGroup;
import org.jeecg.common.workflow.bean.WorkFlowUser;
import org.jeecg.modules.workflow.mapper.WorkFlowSyncMapper;
import org.jeecg.common.workflow.service.WorkflowSyncService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 工作流 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/28
 */
@Slf4j
@Service
public class WorkflowSyncServiceImpl implements WorkflowSyncService {

    @Resource
    private WorkFlowSyncMapper workFlowSyncMapper;

    /**
     * @see WorkflowSyncService#syncDepartInfoToWorkflow()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public WorkFlowGroup syncDepartInfoToWorkflow() throws RuntimeException {
        WorkFlowGroup topGroup = new WorkFlowGroup()
                .setGroupName("部门列表")
                .setGroupCategory(1);

        List<WorkFlowGroup> groupList = workFlowSyncMapper.selectAllTopDepartAndUserRecursion();
        if (null != groupList) {
            sortGroup(groupList);
            if (groupList.size() == 1) {
                topGroup = groupList.get(0);
            } else {
                topGroup.setChildren(groupList);
            }
        }

//        sendRequestToWorkflow(topGroup);

        return topGroup;
    }

    /**
     * @see WorkflowSyncService#syncRoleInfoToWorkflow()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public WorkFlowGroup syncRoleInfoToWorkflow() throws RuntimeException {
        WorkFlowGroup topGroup = new WorkFlowGroup()
                .setGroupName("角色列表")
                .setGroupCategory(2);

        List<WorkFlowGroup> groupList = workFlowSyncMapper.selectAllRoleAndUserRecursion();
        if (null != groupList) {
            sortGroup(groupList);
            if (groupList.size() == 1) {
                topGroup = groupList.get(0);
            } else {
                topGroup.setChildren(groupList);
            }
        }

//        sendRequestToWorkflow(topGroup);

        return topGroup;
    }


    private void sortGroup(List<WorkFlowGroup> groupList) {
        if (null == groupList) {
            groupList = new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(groupList)) {
            return;
        }

        AtomicInteger index = new AtomicInteger(1);
        for (WorkFlowGroup group : groupList) {
            group.setSortBy(String.valueOf(index.getAndIncrement()));
            sortGroup(group.getChildren());
            sortUser(group.getUsers());
        }
    }

    private void sortUser(List<WorkFlowUser> userList) {
        if (null == userList) {
            userList = new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        AtomicInteger index = new AtomicInteger(1);
        for (WorkFlowUser user : userList) {
            user.setSortBy(String.valueOf(index.getAndIncrement()));
        }
    }
}
