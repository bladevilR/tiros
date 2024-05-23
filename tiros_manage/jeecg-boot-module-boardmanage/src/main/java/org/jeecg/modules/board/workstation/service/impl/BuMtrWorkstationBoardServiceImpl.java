package org.jeecg.modules.board.workstation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.board.progress.bean.BuWorkOrderTask;
import org.jeecg.modules.board.quality.bean.BuFaultInfo;
import org.jeecg.modules.board.workstation.bean.BuMtrWorkstation;
import org.jeecg.modules.board.workstation.bean.BuRepairTaskStaffRequire;
import org.jeecg.modules.board.workstation.bean.vo.*;
import org.jeecg.modules.board.workstation.mapper.BuMtrWorkstationBoardMapper;
import org.jeecg.modules.board.workstation.service.BuMtrWorkstationBoardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工位信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-18
 */
@Slf4j
@Service
public class BuMtrWorkstationBoardServiceImpl extends ServiceImpl<BuMtrWorkstationBoardMapper, BuMtrWorkstation> implements BuMtrWorkstationBoardService {

    @Resource
    private BuMtrWorkstationBoardMapper buMtrWorkstationBoardMapper;


    /**
     * @see BuMtrWorkstationBoardService#listWorkstation(BuWorkstationQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkstationVO> listWorkstation(BuWorkstationQueryVO queryVO) throws Exception {
        // 查询工位信息
        List<BuWorkstationVO> workstationBoardVOList = buMtrWorkstationBoardMapper.selectWorkstationVOListByCondition(queryVO);

        workstationBoardVOList.sort(Comparator.comparing(BuWorkstationVO::getGroupName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuWorkstationVO::getWorkstationName, Comparator.nullsLast(Comparator.naturalOrder())));

        //TODO-zhaiyantao 2021/3/12 19:16 暂时先在一个接口返回，后续改成先获取工位列表，再获取工位数据信息列表
//        for (BuWorkstationVO workstationVO : workstationBoardVOList) {
//            BuWorkstationDataQueryVO dataQueryVO = new BuWorkstationDataQueryVO()
//                    .setGroupId(workstationVO.getGroupId())
//                    .setWorkstationId(workstationVO.getWorkstationId());
//            BuWorkstationVO allData = getWorkstationAllData(dataQueryVO);
//            workstationVO.setTask(allData.getTask())
//                    .setMustReplace(allData.getMustReplace())
//                    .setFault(allData.getFault())
//                    .setWorker(allData.getWorker());
//        }

        return workstationBoardVOList;
    }

    /**
     * @see BuMtrWorkstationBoardService#getWorkstationAllData(BuWorkstationDataQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkstationVO getWorkstationAllData(BuWorkstationDataQueryVO queryVO) throws Exception {
        BuWorkstationVO workstationVO = buMtrWorkstationBoardMapper.selectWorkstationVOById(queryVO.getWorkstationId(), queryVO.getGroupId());
        if (null == workstationVO) {
            throw new JeecgBootException("工位信息不存在");
        }

        BuWorkstationTaskVO taskData = getWorkstationTaskData(queryVO);
        BuWorkstationMustReplaceVO mustReplaceData = getWorkstationMustReplaceData(queryVO);
        BuWorkstationFaultVO faultData = getWorkstationFaultData(queryVO);
        BuWorkstationWorkerVO workerData = getWorkstationWorkerData(queryVO);

        return workstationVO.setTask(taskData).setMustReplace(mustReplaceData).setFault(faultData).setWorker(workerData);
    }

    /**
     * @see BuMtrWorkstationBoardService#getWorkstationTaskData(BuWorkstationDataQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkstationTaskVO getWorkstationTaskData(BuWorkstationDataQueryVO queryVO) throws Exception {
        // 时间跨度包含当天的工单任务
        List<BuWorkOrderTask> orderTaskList = buMtrWorkstationBoardMapper.selectTodayTaskListByGroupIdAndWorkstationId(queryVO);

        Double progress = -1D;
        int total = 0;
        int finish = 0;
        if (CollectionUtils.isNotEmpty(orderTaskList)) {
            total = orderTaskList.stream()
                    .map(BuWorkOrderTask::getId)
                    .collect(Collectors.toSet())
                    .size();
            finish = orderTaskList.stream()
                    .filter(task -> 2 == task.getTaskStatus())
                    .map(BuWorkOrderTask::getId)
                    .collect(Collectors.toSet())
                    .size();
            progress = PercentUtils.percent(finish, total);
        }

        // 逾期任务
        List<BuWorkOrderTask> delayOrderTaskList = buMtrWorkstationBoardMapper.selectTodayDelayTaskListByGroupIdAndWorkstationId(queryVO);
        int delay = delayOrderTaskList.stream()
                .map(BuWorkOrderTask::getId)
                .collect(Collectors.toSet())
                .size();

        return new BuWorkstationTaskVO()
                .setProgress(progress)
                .setTotal(total)
                .setFinish(finish)
                .setDelay(delay);
    }

    /**
     * @see BuMtrWorkstationBoardService#getWorkstationMustReplaceData(BuWorkstationDataQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkstationMustReplaceVO getWorkstationMustReplaceData(BuWorkstationDataQueryVO queryVO) throws Exception {
        // 统计工单物料中的必换件物料和实际消耗数量
        List<Map<String, Object>> mustMaterialList = buMtrWorkstationBoardMapper.selectTodayMustMaterialListByGroupIdAndWorkstationId(queryVO);

        int total = 0;
        int replaced = 0;
        if (CollectionUtils.isNotEmpty(mustMaterialList)) {
            Set<String> orderMaterialIdSet = new HashSet<>();
            Set<String> orderMaterialActIdSet = new HashSet<>();
            for (Map<String, Object> mustMaterial : mustMaterialList) {
                String orderMaterialId = (String) mustMaterial.get("orderMaterialId");
                BigDecimal amount = (BigDecimal) mustMaterial.get("amount");
                String orderMaterialActId = (String) mustMaterial.get("orderMaterialActId");
                Float actAmount = (Float) mustMaterial.get("actAmount");

                if (!orderMaterialIdSet.contains(orderMaterialId)) {
                    total = total + (null == amount ? 0 : amount.intValue());
                    orderMaterialIdSet.add(orderMaterialId);
                }
                if (!orderMaterialActIdSet.contains(orderMaterialActId)) {
                    replaced = replaced + (null == actAmount ? 0 : actAmount.intValue());
                    orderMaterialActIdSet.add(orderMaterialActId);
                }
            }
        }

        return new BuWorkstationMustReplaceVO()
                .setTotal(total)
                .setReplaced(replaced)
                .setNotReplaced(total - replaced);
    }

    /**
     * @see BuMtrWorkstationBoardService#getWorkstationFaultData(BuWorkstationDataQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkstationFaultVO getWorkstationFaultData(BuWorkstationDataQueryVO queryVO) throws Exception {
        // 查询工位故障信息(当天)
        List<BuFaultInfo> faultInfoList = buMtrWorkstationBoardMapper.selectTodayFaultListByByGroupIdAndWorkstationId(queryVO);

        int total = faultInfoList.size();
        int unhandled = (int) faultInfoList.stream()
                .filter(fault -> 0 == fault.getStatus())
                .count();

        return new BuWorkstationFaultVO()
                .setTotal(total)
                .setHandled(total - unhandled)
                .setUnhandled(unhandled);
    }

    /**
     * @see BuMtrWorkstationBoardService#getWorkstationWorkerData(BuWorkstationDataQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkstationWorkerVO getWorkstationWorkerData(BuWorkstationDataQueryVO queryVO) throws Exception {
        // 查询工位作业人数需求--从列计划查
        List<BuRepairTaskStaffRequire> staffRequireList = buMtrWorkstationBoardMapper.selectTodayStaffRequireListByGroupIdAndWorkstationId(queryVO);
        int quantity = 0;
        Set<String> staffRequireIdSet = new HashSet<>();
        for (BuRepairTaskStaffRequire staffRequire : staffRequireList) {
            if (!staffRequireIdSet.contains(staffRequire.getId())) {
                quantity = quantity + staffRequire.getAmount();
                staffRequireIdSet.add(staffRequire.getId());
            }
        }

        // 查任务人员安排--从工单查
        Integer arranged = buMtrWorkstationBoardMapper.countTodayStaffArrangeByGroupIdAndWorkstationId(queryVO);
        int notArranged = quantity - arranged;

        return new BuWorkstationWorkerVO()
                .setQuantity(quantity)
                .setArranged(arranged)
                .setNotArranged(notArranged);
    }

}
