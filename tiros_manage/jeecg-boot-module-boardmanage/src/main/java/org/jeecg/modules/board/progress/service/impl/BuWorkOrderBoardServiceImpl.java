package org.jeecg.modules.board.progress.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.board.progress.bean.BuWorkOrder;
import org.jeecg.modules.board.progress.bean.BuWorkOrderTask;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;
import org.jeecg.modules.board.progress.bean.vo.BuWorkGroupOrderInfoVO;
import org.jeecg.modules.board.progress.bean.vo.BuWorkOutsourceOrderVO;
import org.jeecg.modules.board.progress.bean.vo.BuWorkOutsourceTaskVO;
import org.jeecg.modules.board.progress.mapper.BuWorkOrderBoardMapper;
import org.jeecg.modules.board.progress.service.BuWorkOrderBoardService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 工单 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
@Slf4j
@Service
public class BuWorkOrderBoardServiceImpl extends ServiceImpl<BuWorkOrderBoardMapper, BuWorkOrder> implements BuWorkOrderBoardService {

    @Resource
    private BuWorkOrderBoardMapper buWorkOrderBoardMapper;
    @Resource
    private SysConfigService sysConfigService;


    /**
     * @see BuWorkOrderBoardService#getWorkGroupOrderInfo(BuBoardProgressQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkGroupOrderInfoVO getWorkGroupOrderInfo(BuBoardProgressQueryVO queryVO) throws Exception {
        if (StringUtils.isBlank(queryVO.getGroupId())) {
            throw new JeecgBootException("工班id不能为空");
        }
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }

        List<BuWorkOrder> workOrderList = buWorkOrderBoardMapper.listWorkOrderByCondition(queryVO);

        BuWorkGroupOrderInfoVO workGroupOrderInfoVO = new BuWorkGroupOrderInfoVO();

        if (CollectionUtils.isNotEmpty(workOrderList)) {
            setUnfinishedTaskCount(workOrderList);
            workGroupOrderInfoVO
                    .setGroupName(workOrderList.get(0).getGroupName())
                    .setOrderList(workOrderList)
                    .setTaskCount(workOrderList.stream().mapToInt(BuWorkOrder::getTaskCount).sum())
                    .setFinishedTaskCount(workOrderList.stream().mapToInt(BuWorkOrder::getFinishedTaskCount).sum())
                    .setUnfinishedTaskCount(workOrderList.stream().mapToInt(BuWorkOrder::getUnfinishedTaskCount).sum());

            List<PieChartVO> pieChartVOList = new ArrayList<>(2);
            pieChartVOList.add(new PieChartVO().setItem("已完成").setCount(workGroupOrderInfoVO.getFinishedTaskCount().doubleValue()));
            pieChartVOList.add(new PieChartVO().setItem("未完成").setCount(workGroupOrderInfoVO.getUnfinishedTaskCount().doubleValue()));
            workGroupOrderInfoVO.setPieChartVOList(pieChartVOList);
        }

        return workGroupOrderInfoVO;
    }

    /**
     * @see BuWorkOrderBoardService#listOutsourceOrderProgress(BuBoardProgressQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOutsourceOrderVO> listOutsourceOrderProgress(BuBoardProgressQueryVO queryVO) throws Exception {
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }
        List<BuWorkOrder> orderList = buWorkOrderBoardMapper.listOutsourceWorkOrderByCondition(queryVO);

        return transformToGantt(orderList);
    }

    @Override
    public List<BuWorkOutsourceTaskVO> listOutsourceTaskVOProgress(BuBoardProgressQueryVO queryVO) throws Exception {
        // 如果手动控制显示监控大屏显示数据，包括列计划进度、工班任务进度、委外任务进度
        String monitorScreenControlDataManual = sysConfigService.getConfigValueByCode("MonitorScreenControl_DataManual");
        boolean manualControl = StringUtils.isNotBlank(monitorScreenControlDataManual) && "1".equals(monitorScreenControlDataManual);
        if (manualControl) {
            String data = sysConfigService.getConfigValueByCode("MonitorScreenControl_OutData");
            if (StringUtils.isBlank(data)) {
                return new ArrayList<>();
            } else {
                return JSON.parseArray(data, BuWorkOutsourceTaskVO.class);
            }
        }

        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }
        List<BuWorkOrderTask> taskList = buWorkOrderBoardMapper.listOutsourceWorkOrderTaskByCondition(queryVO);

        return taskTransformToGantt(taskList);
    }


    private void setUnfinishedTaskCount(List<BuWorkOrder> workOrderList) {
        for (BuWorkOrder workOrder : workOrderList) {
            workOrder.setUnfinishedTaskCount(workOrder.getTaskCount() - workOrder.getFinishedTaskCount());
        }
    }

    private List<BuWorkOutsourceOrderVO> transformToGantt(List<BuWorkOrder> orderList) {
        List<BuWorkOutsourceOrderVO> resultList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(orderList)) {
            for (BuWorkOrder order : orderList) {
                resultList.add(transformToGantt(order));
            }
        }

        return resultList;
    }

    private List<BuWorkOutsourceTaskVO> taskTransformToGantt(List<BuWorkOrderTask> taskList) {
        List<BuWorkOutsourceTaskVO> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskList.forEach(task -> resultList.add(taskTransformToGantt(task)));

        }
        return resultList;
    }

    private BuWorkOutsourceTaskVO taskTransformToGantt(BuWorkOrderTask task) {
        Double progress = 0D;
        if (task.getTaskStatus() == 2) {
            progress = 100D;
        }

        BuWorkOutsourceTaskVO taskGantt = new BuWorkOutsourceTaskVO();
        BeanUtils.copyProperties(task, taskGantt);
        taskGantt.setUID(task.getId());
        taskGantt.setName(task.getTaskName());
        taskGantt.setCritical(1);
        taskGantt.setDuration(task.getWorkTime());
        taskGantt.setStart(task.getStartTime());
        taskGantt.setFinish(getGanttFinishTime(task.getFinishTime()));
        taskGantt.setActStart(task.getTaskStart());
        taskGantt.setActFinish(getGanttFinishTime(task.getFinishTime()));
        taskGantt.setNotes(task.getRemark());
        taskGantt.setOutlineLevel(1);
        taskGantt.setPercentComplete(progress);

        return taskGantt;
    }

    private BuWorkOutsourceOrderVO transformToGantt(BuWorkOrder order) {
        Double progress = PercentUtils.percent(order.getFinishedTaskCount().doubleValue(), order.getTaskCount().doubleValue());

        BuWorkOutsourceOrderVO orderGantt = new BuWorkOutsourceOrderVO();
        BeanUtils.copyProperties(order, orderGantt);

        orderGantt.setUID(order.getId());
        orderGantt.setName(order.getOrderName());
        orderGantt.setCritical(1);
        orderGantt.setDuration(order.getDuration());
        orderGantt.setStart(order.getStartTime());
        orderGantt.setFinish(getGanttFinishTime(order.getFinishTime()));
        orderGantt.setNotes(order.getRemark());
        orderGantt.setOutlineLevel(1);
        orderGantt.setPercentComplete(progress);

        return orderGantt;
    }

    private Date getGanttFinishTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (0 == calendar.get(Calendar.HOUR_OF_DAY)
                && 0 == calendar.get(Calendar.MINUTE)
                && 0 == calendar.get(Calendar.SECOND)) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        }

        return calendar.getTime();
    }

}
