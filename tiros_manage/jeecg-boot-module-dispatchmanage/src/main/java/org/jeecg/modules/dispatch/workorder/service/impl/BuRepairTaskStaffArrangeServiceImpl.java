package org.jeecg.modules.dispatch.workorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.dispatch.workorder.bean.BuRepairTaskStaffArrange;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTask;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTaskWorkstation;
import org.jeecg.modules.dispatch.workorder.mapper.BuRepairTaskStaffArrangeMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderTaskMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderTaskWorkstationMapper;
import org.jeecg.modules.dispatch.workorder.service.BuRepairTaskStaffArrangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务人员安排 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Service
public class BuRepairTaskStaffArrangeServiceImpl extends ServiceImpl<BuRepairTaskStaffArrangeMapper, BuRepairTaskStaffArrange> implements BuRepairTaskStaffArrangeService {

    @Resource
    private BuRepairTaskStaffArrangeMapper buRepairTaskStaffArrangeMapper;
    @Resource
    private BuWorkOrderTaskWorkstationMapper buWorkOrderTaskWorkstationMapper;
    @Resource
    private BuWorkOrderTaskMapper buWorkOrderTaskMapper;


    /**
     * @see BuRepairTaskStaffArrangeService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> staffArrangeIdList = Arrays.asList(ids.split(","));
        buRepairTaskStaffArrangeMapper.deleteBatchIds(staffArrangeIdList);

        return true;
    }

    /**
     * @see BuRepairTaskStaffArrangeService#saveStaffArrangeList(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveStaffArrangeList(List<BuRepairTaskStaffArrange> staffArrangeList) throws Exception {
        // 需新增的任务人员安排列表
        List<BuRepairTaskStaffArrange> needInsertList = new ArrayList<>();

        for (BuRepairTaskStaffArrange staffArrange : staffArrangeList) {
            if (StringUtils.isBlank(staffArrange.getId())) {
                // 没有id的加入需新增列表
                needInsertList.add(staffArrange);
                continue;
            }

            // 有id的更新
            buRepairTaskStaffArrangeMapper.updateById(staffArrange);
        }

        if (CollectionUtils.isNotEmpty(needInsertList)) {
            Set<String> orderTaskIdSet = needInsertList.stream()
                    .map(BuRepairTaskStaffArrange::getOrderTaskId)
                    .collect(Collectors.toSet());

            // 任务工位
            LambdaQueryWrapper<BuWorkOrderTaskWorkstation> taskWorkstationWrapper = new LambdaQueryWrapper<BuWorkOrderTaskWorkstation>()
                    .in(BuWorkOrderTaskWorkstation::getOrderTaskId, orderTaskIdSet);
            List<BuWorkOrderTaskWorkstation> workstationList = buWorkOrderTaskWorkstationMapper.selectList(taskWorkstationWrapper);
            // 任务
            List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskMapper.selectBatchIds(orderTaskIdSet);
            Map<String, String> taskIdOrderIdMap = new HashMap<>(4);
            orderTaskList.forEach(task -> taskIdOrderIdMap.put(task.getId(), task.getOrderId()));

            for (BuRepairTaskStaffArrange staffArrange : needInsertList) {
                String orderTaskId = staffArrange.getOrderTaskId();
                String workstationId = staffArrange.getWorkstationId();
                String orderId = taskIdOrderIdMap.get(orderTaskId);
                BuWorkOrderTaskWorkstation taskWorkstation;

                List<BuWorkOrderTaskWorkstation> matchWorkstationList = workstationList.stream()
                        .filter(workstation ->
                                orderTaskId.equals(workstation.getOrderTaskId())
                                        && workstationId.equals(workstation.getWorkstationId()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(matchWorkstationList)) {
                    // 有任务工位
                    taskWorkstation = matchWorkstationList.get(0);
                } else {
                    // 没有任务工位，新增任务工位
                    taskWorkstation = new BuWorkOrderTaskWorkstation()
                            .setOrderId(orderId)
                            .setOrderTaskId(orderTaskId)
                            .setWorkTime(null == staffArrange.getPlanWorkTime() ? 0 : staffArrange.getPlanWorkTime())
                            .setWorkstationId(staffArrange.getWorkstationId());
                    buWorkOrderTaskWorkstationMapper.insert(taskWorkstation);
                    workstationList.add(taskWorkstation);
                }

                // 新增任务人员安排
                staffArrange.setOrderId(orderId)
                        .setOrdTskWkStationId(taskWorkstation.getId());
                buRepairTaskStaffArrangeMapper.insert(staffArrange);
            }
        }

        return true;
    }

}
