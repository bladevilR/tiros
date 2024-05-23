package org.jeecg.modules.workflow.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.workflow.bean.WfBizStatus;
import org.jeecg.common.workflow.service.WfBizStatusService;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangMapper;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFar;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYear;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanFarMapper;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanYearMapper;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanMapper;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrder;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderMapper;
import org.jeecg.modules.outsource.bean.BuOutsourceSupervise;
import org.jeecg.modules.outsource.mapper.BuOutsourceSuperviseMapper;
import org.jeecg.modules.workflow.mapper.WfBizStatusMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * 业务流程实列状态表 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-27
 */
@Slf4j
@Service
public class WfBizStatusServiceImpl extends ServiceImpl<WfBizStatusMapper, WfBizStatus> implements WfBizStatusService {

    @Resource
    private WfBizStatusMapper wfBizStatusMapper;
    @Resource
    private BuRepairPlanFarMapper buRepairPlanFarMapper;
    @Resource
    private BuRepairPlanYearMapper buRepairPlanYearMapper;
    @Resource
    private BuRepairExchangMapper buRepairExchangMapper;
    @Resource
    private BuRepairPlanMapper buRepairPlanMapper;
    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;
    @Resource
    private BuOutsourceSuperviseMapper buOutsourceSuperviseMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see WfBizStatusService#listProcessStatusBySolutionCode(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<String> listProcessStatusBySolutionCode(String solutionCode) throws Exception {
        List<String> processStatusList = wfBizStatusMapper.selectDistinctProcessStatusListBySolutionCode(solutionCode);
        processStatusList.sort(Comparator.comparing(String::toString, Comparator.nullsLast(Comparator.naturalOrder())));
        if (!processStatusList.contains("未发起")) {
            processStatusList.add(0, "未发起");
        }
        return processStatusList;
    }

    /**
     * @see WfBizStatusService#changeWfBizStatus(Map)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changeWfBizStatus(Map<String, Object> requestParam) throws Exception {
        String solutionCode = requestParam.get("solutionCode").toString();
        String processDefinitionId = requestParam.get("processDefinitionId").toString();
        String processInstanceId = requestParam.get("processInstanceId").toString();
        String businessKey = requestParam.get("businessKey").toString();
        String candidates = "";
        if (requestParam.get("CANDIDATES") != null) {
            candidates = requestParam.get("CANDIDATES").toString();
        }
        String eventType = requestParam.get("EventType").toString();

        String curNodeId = getMapKeyValue(requestParam, "taskId");
        String curNodeCode = getMapKeyValue(requestParam, "taskDefinitionKey");
        String curNodeName = getMapKeyValue(requestParam, "taskName");
        String curVars = getMapKeyValue(requestParam, "variables");
        String curAttrs = getMapKeyValue(requestParam, "attributes");

        WfBizStatus bizStatus = wfBizStatusMapper.selectById(businessKey);

        if (bizStatus != null) {
            String status = "";
            if ("PROCESS_STARTED".equals(eventType)) {

                bizStatus.setSolutionCode(solutionCode);
                bizStatus.setProcessDefinitionId(processDefinitionId);
                bizStatus.setProcessInstanceId(processInstanceId);
                status = "已启动";
            }
            if ("PROCESS_COMPLETED".equals(eventType)) {
                //流程结束，将待处理人修改成空的
                bizStatus.setProcessCandidate("");
                status = "已结束";
            }
            if ("PROCESS_CANCELLED".equals(eventType)) {
                status = "已结束";
            }
            if ("TASK_CREATED".equals(eventType)) {
                // 创建的任务名就是接下来要审批的节点
                status = requestParam.get("taskName").toString();
                bizStatus.setProcessCandidate(candidates);

//                // 接收参数：CANDIDATE_IDS，如果有值，给对应人员发送消息
//                Object candidateIds = requestParam.get("CANDIDATE_IDS");
//                if (null != candidateIds) {
//                    // 获取消息发送人
//                    String fromUser = "admin";
//                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//                    if (null != sysUser) {
//                        fromUser = sysUser.getUsername();
//                    }
//                    sendMessageByNewThread(solutionCode, businessKey, candidates, status, (String) candidateIds, fromUser);
//                }
            }
            if ("TASK_COMPLETED".equals(eventType)) {
                // 创建的任务名就是接下来要审批的节点
                //status = requestParam.get("taskName").toString();
            }
            if (StringUtils.isNotBlank(status)) {
                bizStatus.setProcessStatus(status);
            }

            bizStatus.setCurNodeCode(curNodeCode);
            bizStatus.setCurNodeName(curNodeName);
            bizStatus.setCurVars(curVars);
            bizStatus.setCurAttrs(curAttrs);

            wfBizStatusMapper.updateById(bizStatus);
        } else {
            if (eventType.equals("PROCESS_STARTED")) {
                bizStatus = new WfBizStatus();
                bizStatus.setBusinessKey(businessKey);
                bizStatus.setSolutionCode(solutionCode);
                bizStatus.setProcessDefinitionId(processDefinitionId);
                bizStatus.setProcessInstanceId(processInstanceId);

                bizStatus.setCurNodeCode(curNodeCode);
                bizStatus.setCurNodeName(curNodeName);
                bizStatus.setCurVars(curVars);
                bizStatus.setCurAttrs(curAttrs);

                bizStatus.setProcessStatus("已发送");
                wfBizStatusMapper.insert(bizStatus);
            }
        }

        // 启动时，修改远期规划、年计划、列计划的审批状态为1审批中
        if ("PROCESS_STARTED".equals(eventType)) {
            if ("WF_LONG_PLAN".equals(solutionCode)) {
                // 远期规划状态更新
                BuRepairPlanFar planFar = buRepairPlanFarMapper.selectById(businessKey);
                if (null != planFar) {
                    planFar.setStatus(1);
                    buRepairPlanFarMapper.updateById(planFar);

                    log.info("流程启动，修改远期规划(id=" + planFar.getId() + ")状态为" + planFar.getStatus());
                }
            } else if ("WF_YEAR_PLAN".equals(solutionCode)) {
                // 年计划状态更新
                BuRepairPlanYear planYear = buRepairPlanYearMapper.selectById(businessKey);
                if (null != planYear) {
                    planYear.setStatus(1);
                    buRepairPlanYearMapper.updateById(planYear);

                    log.info("流程启动，修改年计划(id=" + planYear.getId() + ")状态为" + planYear.getStatus());
                }
            } else if ("TRAINPLAN_APPROVED".equals(solutionCode)) {
                // 列计划状态更新
                BuRepairPlan plan = buRepairPlanMapper.selectById(businessKey);
                if (null != plan) {
                    plan.setStatus(1);
                    buRepairPlanMapper.updateById(plan);

                    log.info("流程启动，修改列计划(id=" + plan.getId() + ")状态为" + plan.getStatus());
                }
            }
        }

        return true;
    }

    /**
     * @see WfBizStatusService#listByBusinessKeyListAndSolutionCode(List, String)
     */
    @Override
    public List<WfBizStatus> listByBusinessKeyListAndSolutionCode(List<String> businessKeyList, String solutionCode) throws Exception {
        if (CollectionUtils.isEmpty(businessKeyList)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<WfBizStatus> wrapper = new LambdaQueryWrapper<WfBizStatus>()
                .in(WfBizStatus::getBusinessKey, businessKeyList);
        if (StringUtils.isNotBlank(solutionCode)) {
            wrapper.eq(WfBizStatus::getSolutionCode, solutionCode);
        }

        return wfBizStatusMapper.selectList(wrapper);
    }


    private String getMapKeyValue(Map<String, Object> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key).toString();
        } else {
            return null;
        }
    }

    private void sendMessageByNewThread(String solutionCode, String businessKey, String candidates, String status, String candidateIds, String fromUser) {
        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                // 获取消息接收人
                List<String> userIdList = Arrays.asList(candidateIds.split(","));
                List<String> usernameList = sysBaseAPI.listUsernameByTypeAndParam(3, userIdList);
                if (CollectionUtils.isNotEmpty(usernameList)) {
                    String usernames = String.join(",", usernameList);
                    // 发消息
                    String title = "新的" + getTitleSuffix(solutionCode, businessKey);
                    String content = title + "；" +
                            "当前处理状态：" + status +
                            "，当前可处理人：" + candidates +
                            "，请及时处理";
                    sysBaseAPI.sendSysAnnouncement(fromUser, usernames, title, content);
                }
            } catch (Exception ex) {
                log.error("开线程发送消息，执行失败，原因如下：" + ex.getMessage());
                StringWriter stringWriter = new StringWriter();
                try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                    ex.printStackTrace(printWriter);
                }
                log.error(stringWriter.toString());
            }
        });
        singleExecutor.shutdown();
    }

    private String getTitleSuffix(String solutionCode, String businessKey) {
        String titleSuffix = "";

        if ("WF_LONG_PLAN".equals(solutionCode)) {
            // 远期规划
            BuRepairPlanFar planFar = buRepairPlanFarMapper.selectById(businessKey);
            if (null != planFar) {
                titleSuffix = "远期规划：" + planFar.getTitle();
            }
        } else if ("WF_YEAR_PLAN".equals(solutionCode)) {
            // 年计划
            BuRepairPlanYear planYear = buRepairPlanYearMapper.selectById(businessKey);
            if (null != planYear) {
                titleSuffix = "年计划：" + planYear.getTitle();
            }
        } else if ("WF_TRANSFER_TRAIN".equals(solutionCode) || "WF_RECEIVE_TRAIN".equals(solutionCode)) {
            // 交接车
            BuRepairExchang exchange = buRepairExchangMapper.selectById(businessKey);
            if (null != exchange) {
                String type = "";
                if (0 == exchange.getTradeType()) {
                    type = "接车";
                } else {
                    type = "交车";
                }
                titleSuffix = type + "：" + "第" + exchange.getTrainIndex() + "列" + exchange.getTrainNo();
            }
        } else if ("TRAINPLAN_APPROVED".equals(solutionCode)) {
            // 列计划
            BuRepairPlan plan = buRepairPlanMapper.selectById(businessKey);
            if (null != plan) {
                titleSuffix = "列计划：" + plan.getPlanName();
            }
        } else if ("WF_WORK_ORDER".equals(solutionCode)) {
            // 工单
            BuWorkOrder order = buWorkOrderMapper.selectById(businessKey);
            if (null != order) {
                titleSuffix = "工单：" + order.getTrainNo() + "车辆 " + order.getOrderCode();
            }
        } else if ("WF_OUT_SUPERVISE".equals(solutionCode)) {
            // 委外监修
            BuOutsourceSupervise supervise = buOutsourceSuperviseMapper.selectById(businessKey);
            if (null != supervise) {
                titleSuffix = "委外监修：" + supervise.getTrainNo() + "车辆" + supervise.getTaskContent();
            }
        }

        return titleSuffix;
    }

}
