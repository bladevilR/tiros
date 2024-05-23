package org.jeecg.modules.produce.workshop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;
import org.jeecg.modules.produce.workshop.bean.BuMtrWorkshop;
import org.jeecg.modules.produce.workshop.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.produce.workshop.bean.BuMtrWorkstation;
import org.jeecg.modules.produce.workshop.bean.bo.BuWorkOrderTaskBO;
import org.jeecg.modules.produce.workshop.bean.vo.BuWorkstationQueryVO;
import org.jeecg.modules.produce.workshop.bean.vo.BuWorkstationWorkVO;
import org.jeecg.modules.produce.workshop.mapper.BuMtrWorkshopGroupProduceMapper;
import org.jeecg.modules.produce.workshop.mapper.BuMtrWorkshopProduceMapper;
import org.jeecg.modules.produce.workshop.mapper.BuMtrWorkstationProduceMapper;
import org.jeecg.modules.produce.workshop.service.BuWorkshopMonitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 架修车间 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-30
 */
@Slf4j
@Service
public class BuWorkshopMonitorServiceImpl extends ServiceImpl<BuMtrWorkshopProduceMapper, BuMtrWorkshop> implements BuWorkshopMonitorService {

    @Resource
    private BuMtrWorkshopProduceMapper buMtrWorkshopProduceMapper;
    @Resource
    private BuMtrWorkstationProduceMapper buMtrWorkstationProduceMapper;
    @Resource
    private BuMtrWorkshopGroupProduceMapper buMtrWorkshopGroupProduceMapper;


    /**
     * @see BuWorkshopMonitorService#getGraphAddressByWorkShopId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String getGraphAddressByWorkShopId(String workshopId) throws Exception {
        BuMtrWorkshop workshop = buMtrWorkshopProduceMapper.selectById(workshopId);
        if (null == workshop) {
            throw new JeecgBootException("车间不存在");
        }

        return workshop.getGraphAddress();
    }

    /**
     * @see BuWorkshopMonitorService#listWorkstation(BuWorkstationQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkstationWorkVO> listWorkstation(BuWorkstationQueryVO queryVO) throws Exception {
        List<BuMtrWorkstation> workstationList = buMtrWorkstationProduceMapper.selectListByWorkshopIdAndStationNo(queryVO.getWorkshopId(), queryVO.getWorkstationNo());

        List<BuWorkstationWorkVO> workVOList = transformToWorkVOList(workstationList);

        Integer workStatus = queryVO.getWorkStatus();
        if (null != workStatus) {
            workVOList = workVOList.stream()
                    .filter(vo -> vo.getWorkStatus().equals(workStatus))
                    .collect(Collectors.toList());
        }

        return workVOList;
    }

    /**
     * @see BuWorkshopMonitorService#getWorkstationWorkInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkstationWorkVO getWorkstationWorkInfo(String id) {
        BuMtrWorkstation workstation = buMtrWorkstationProduceMapper.selectWorkstationById(id);

        // 查询正在作业的工单任务
        List<BuWorkOrderTaskBO> workingOrderTaskList = buMtrWorkstationProduceMapper.selectWorkingOrderTaskListByWorkstationIdList(Collections.singletonList(id));
        // 查询未处理的故障
        List<BuFaultInfo> unHandleFaultList = buMtrWorkstationProduceMapper.selectUnHandleFaultListByWorkstationIdList(Collections.singletonList(id));
        // 查询班组信息
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupProduceMapper.selectAll();
        Map<String, BuMtrWorkshopGroup> idGroupMap = new HashMap<>(16);
        groupList.forEach(group -> idGroupMap.put(group.getId(), group));

        return transformToWorkVO(workstation, workingOrderTaskList, unHandleFaultList, idGroupMap);
    }


    private List<BuWorkstationWorkVO> transformToWorkVOList(List<BuMtrWorkstation> workstationList) {
        if (CollectionUtils.isEmpty(workstationList)) {
            return new ArrayList<>();
        }

        List<String> workstationIdList = workstationList.stream()
                .map(BuMtrWorkstation::getId)
                .distinct()
                .collect(Collectors.toList());
        // 查询正在作业的工单任务
        List<BuWorkOrderTaskBO> allWorkingOrderTaskList = buMtrWorkstationProduceMapper.selectWorkingOrderTaskListByWorkstationIdList(workstationIdList);
        // 查询未处理的故障
        List<BuFaultInfo> allUnHandleFaultList = buMtrWorkstationProduceMapper.selectUnHandleFaultListByWorkstationIdList(workstationIdList);
        // 查询班组信息
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupProduceMapper.selectAll();
        Map<String, BuMtrWorkshopGroup> idGroupMap = new HashMap<>(16);
        groupList.forEach(group -> idGroupMap.put(group.getId(), group));

        List<BuWorkstationWorkVO> workVOList = new ArrayList<>();
        for (BuMtrWorkstation workstation : workstationList) {
            String workstationId = workstation.getId();
            // 获取此工位的正在作业的工单任务
            List<BuWorkOrderTaskBO> workingOrderTaskList = allWorkingOrderTaskList.stream()
                    .filter(task -> workstationId.equals(task.getWorkstationId()))
                    .collect(Collectors.toList());

            // 获取此工位的未处理的故障
            List<BuFaultInfo> unHandleFaultList = allUnHandleFaultList.stream()
                    .filter(fault -> workstationId.equals(fault.getWorkStationId()))
                    .collect(Collectors.toList());

            BuWorkstationWorkVO workVO = transformToWorkVO(workstation, workingOrderTaskList, unHandleFaultList, idGroupMap);
            if (null != workVO) {
                workVOList.add(workVO);
            }
        }

        return workVOList;
    }

    private BuWorkstationWorkVO transformToWorkVO(BuMtrWorkstation workstation,
                                                  List<BuWorkOrderTaskBO> workingOrderTaskList,
                                                  List<BuFaultInfo> unHandleFaultList,
                                                  Map<String, BuMtrWorkshopGroup> idGroupMap) {
        if (null == workstation) {
            return null;
        }

        BuWorkstationWorkVO workVO = new BuWorkstationWorkVO()
                .setId(workstation.getId())
                .setLocation(workstation.getLocation())
                .setStationNo(workstation.getStationNo())
                .setName(workstation.getName());

        if (CollectionUtils.isEmpty(workingOrderTaskList)) {
            workVO.setWorkStatus(3);
        } else {
            workVO.setWorkStatus(1);

            StringBuilder groupName = new StringBuilder();
            StringBuilder content = new StringBuilder();
            Date startTime = null;
            StringBuilder monitor = new StringBuilder();
            for (BuWorkOrderTaskBO taskBO : workingOrderTaskList) {
                BuMtrWorkshopGroup group = idGroupMap.getOrDefault(taskBO.getGroupId(), new BuMtrWorkshopGroup());

                groupName.append(group.getGroupName());
                content.append(taskBO.getTaskContent());
                monitor.append(StringUtils.isBlank(group.getMonitorName()) ? group.getMonitor2Name() : group.getMonitorName());

                if (null != taskBO.getTaskStart()) {
                    if (null == startTime) {
                        startTime = taskBO.getTaskStart();
                    }
                    if (startTime.after(taskBO.getTaskStart())) {
                        startTime = taskBO.getTaskStart();
                    }
                }
            }

            workVO.setGroupName(groupName.toString())
                    .setContent(content.toString())
                    .setStartTime(startTime)
                    .setMonitor(monitor.toString());
        }

        if (CollectionUtils.isNotEmpty(unHandleFaultList)) {
            List<String> faultDescList = unHandleFaultList.stream()
                    .map(BuFaultInfo::getFaultDesc)
                    .collect(Collectors.toList());
            workVO.setWorkStatus(2)
                    .setContent(StringUtils.join(faultDescList, ","));
        }

        return workVO;
    }

}
