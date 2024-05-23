package org.jeecg.modules.workflow.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.QualityTransformationAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.rpt.service.HomepageItemRptService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.workflow.service.WorkflowCallbackService;
import org.jeecg.modules.basemanage.alertaccept.mapper.BuBaseAlertAcceptMapper;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainInfoService;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangLeave;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangRectify;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangMapper;
import org.jeecg.modules.dispatch.exchange.service.BuRepairExchangService;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFar;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYear;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanFarMapper;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanYearDetailMapper;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanYearMapper;
import org.jeecg.modules.dispatch.planform.service.BuRepairPlanFormsService;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanMapper;
import org.jeecg.modules.outsource.bean.BuContractInfo;
import org.jeecg.modules.outsource.bean.BuOutsourceOutinQuality;
import org.jeecg.modules.outsource.mapper.BuContractInfoMapper;
import org.jeecg.modules.outsource.mapper.BuOutsourceOutinQualityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * 流程回调 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-25
 */
@Slf4j
@Service
public class WorkflowCallbackServiceImpl implements WorkflowCallbackService {

    @Resource
    private BuRepairPlanFarMapper buRepairPlanFarMapper;
    @Resource
    private BuRepairPlanYearMapper buRepairPlanYearMapper;
    @Resource
    private BuRepairPlanYearDetailMapper buRepairPlanYearDetailMapper;
    @Resource
    private BuRepairPlanMapper buRepairPlanMapper;
    @Resource
    private BuRepairExchangMapper buRepairExchangMapper;
    @Resource
    private BuContractInfoMapper buContractInfoMapper;
    @Resource
    private BuBaseAlertAcceptMapper buBaseAlertAcceptMapper;
    @Resource
    private BuOutsourceOutinQualityMapper buOutsourceOutinQualityMapper;
    @Resource
    private BuRepairPlanFormsService buRepairPlanFormsService;
    @Resource
    private BuRepairExchangService buRepairExchangService;
    @Resource
    private HomepageItemRptService homepageItemRptService;
    @Resource
    private IBuTrainInfoService buTrainInfoService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private QualityTransformationAPI qualityTransformationAPI;


    /**
     * @see WorkflowCallbackService#completeChangeStatus(Map)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean completeChangeStatus(Map<String, Object> requestParam) throws Exception {
        String solutionCode = requestParam.get("solutionCode").toString();
        String businessKey = requestParam.get("businessKey").toString();
        String eventType = requestParam.get("EventType").toString();

        String sysUsername = "admin";
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (null != sysUser) {
                sysUsername = sysUser.getUsername();
            }
        } catch (Exception ignored) {
        }

        if (!"PROCESS_COMPLETED".equals(eventType)) {
            // 流程事件不是已完成，返回异常
            throw new JeecgBootException("EventType不等于PROCESS_COMPLETED，请检查配置");
        }

        if ("WF_LONG_PLAN".equals(solutionCode)) {
            // 远期规划状态更新
            BuRepairPlanFar planFar = buRepairPlanFarMapper.selectById(businessKey);
            if (null != planFar) {
                planFar.setStatus(2);
                buRepairPlanFarMapper.updateById(planFar);

                log.info("流程结束，改变状态回调，修改远期规划(id=" + planFar.getId() + ")状态为" + planFar.getStatus());
            }
        } else if ("WF_YEAR_PLAN".equals(solutionCode)) {
            // 年计划状态更新
            BuRepairPlanYear planYear = buRepairPlanYearMapper.selectById(businessKey);
            if (null != planYear) {
                planYear.setStatus(2);
                buRepairPlanYearMapper.updateById(planYear);

                log.info("流程结束，改变状态回调，修改年计划(id=" + planYear.getId() + ")状态为" + planYear.getStatus());
            }
        } else if ("WF_TRANSFER_TRAIN".equals(solutionCode) || "WF_RECEIVE_TRAIN".equals(solutionCode)) {
            // 交接车状态更新
            BuRepairExchang exchange = buRepairExchangService.getRepairExchangeWithRelationById(businessKey);
            if (null != exchange) {
                exchange.setStatus(1);
                // 更新架修周期记录表
                buRepairExchangService.updateRepairPeriodByExchangeId(businessKey);
                log.info("流程结束，改变状态回调，更新了架修周期记录");
                // 类型为接车时
                if (0 == exchange.getTradeType()) {
                    // 当前审批完成后要将接车填写的里程数量，写入车辆信息中
                    buTrainInfoService.updateTrainMileage(exchange.getTrainNo(), exchange.getAcceptMileage());

                    // 发送消息给调度，提示调度进行列计划的生成
                    sendMessageByNewThread(exchange, sysUsername);

                    // 修改对应的年计划明细为已接车
                    String yearDetailId = exchange.getYearDetailId();
                    BuRepairPlanYearDetail yearDetail = new BuRepairPlanYearDetail()
                            .setId(yearDetailId)
                            .setStatus(2);
                    buRepairPlanYearDetailMapper.updateById(yearDetail);

                    // 把开项和整改项保存到质量管理中对应的表
                    saveLeaveAndRectify(exchange);
                } else if (1 == exchange.getTradeType()) {
                    // 修改对应的年计划明细为已交车
                    String yearDetailId = exchange.getYearDetailId();
                    exchange.setSendDate(exchange.getPlanFinishDate());
                    BuRepairPlanYearDetail yearDetail = new BuRepairPlanYearDetail()
                            .setId(yearDetailId)
                            .setStatus(3);
                    buRepairPlanYearDetailMapper.updateById(yearDetail);

                    // 处理合同信息
                    handleContractInfo(exchange, sysUsername);
                }
                buRepairExchangService.updateById(exchange);
                log.info("流程结束，改变状态回调，修改交接车(id=" + exchange.getId() + ")状态为");
            }
        } else if ("TRAINPLAN_APPROVED".equals(solutionCode)) {
            // 列计划状态更新
            BuRepairPlan plan = buRepairPlanMapper.selectById(businessKey);
            if (StringUtils.isNotBlank(plan.getExchangeId())) {
                // 设置对应的接车为维修中
                BuRepairExchang exchange = buRepairExchangMapper.selectById(plan.getExchangeId());
                if (null != exchange) {
                    exchange.setStatus(2);
                    buRepairExchangMapper.updateById(exchange);
                    log.info("流程结束，改变状态回调，修改交接车(id=" + exchange.getId() + ")状态为" + exchange.getStatus());
                }
            }

            // 设置列计划状态为2已审批
            plan.setStatus(2);
            buRepairPlanMapper.updateById(plan);
            log.info("流程结束，改变状态回调，修改列计划(id=" + plan.getId() + ")状态为" + plan.getStatus());

            // 更新首页数据区数据
            homepageItemRptService.rewriteDataItem();

            // 生成列计划表单实列
            buRepairPlanFormsService.generateFormsRelationDataByPlanId(plan.getId());
            log.info("流程结束，改变状态回调，生成列计划(id=" + plan.getId() + ")表单实列");
        }

        return true;
    }


    private void sendMessageByNewThread(BuRepairExchang exchange, String fromUser) {
        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                List<String> userNameList = sysBaseAPI.listUsernameByTypeAndParam(2, Collections.singletonList("jxdd"));
                if (CollectionUtils.isNotEmpty(userNameList)) {
                    String usernames = String.join(",", userNameList);
                    String title = "新的接车：" + "第" + exchange.getTrainIndex() + "列" + exchange.getTrainNo();
                    String content = "请及时安排该车辆（" + "第" + exchange.getTrainIndex() + "列" + exchange.getTrainNo() + "）的列计划";
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

    private void sendMessageByNewThread(List<BuContractInfo> contractList, String fromUser, List<String> toUserList) {
        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                String toUser = String.join(",", toUserList);
                for (BuContractInfo contract : contractList) {
                    String title = String.format("委外合同付款：合同%s已完成%s列", contract.getContractName(), contract.getFinishAmount());
                    String content = String.format("线路：%s，合同编号：%s，合同名称：%s。该合同共%s列，已完成%s列，根据合同中的付款设置：从第%s列开始，每隔%s列支付一次，需要付款，请及时处理。",
                            contract.getLineName(), contract.getContractNo(), contract.getContractName(),
                            contract.getTrainAmount(), contract.getFinishAmount(),
                            contract.getPayBegin(), contract.getPayInterval());
                    sysBaseAPI.sendSysAnnouncement(fromUser, toUser, title, content);
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

    private void handleContractInfo(BuRepairExchang exchange, String sysUsername) {
        // 查询未完成合同信息
        List<BuContractInfo> unFinishContractList = buContractInfoMapper.selectUnFinishListByLineAndProgramAndDate(exchange.getLineId(), exchange.getProgramId(), exchange.getSendDate());
        if (CollectionUtils.isEmpty(unFinishContractList)) {
            return;
        }

        // 更新合同已完成数+1
        for (BuContractInfo contract : unFinishContractList) {
            contract.setFinishAmount(contract.getFinishAmount() + 1);
        }
        List<List<BuContractInfo>> unFinishContractBatchSubList = DatabaseBatchSubUtil.batchSubList(unFinishContractList);
        for (List<BuContractInfo> unFinishContractBatchSub : unFinishContractBatchSubList) {
            buContractInfoMapper.updateListFinishAmount(unFinishContractBatchSub);
        }

        // 过滤出需要付款的合同、完成的合同
        List<BuContractInfo> needPayList = new ArrayList<>();
        List<BuContractInfo> finishList = new ArrayList<>();
        for (BuContractInfo contract : unFinishContractList) {
            Integer finishAmount = contract.getFinishAmount();
            Integer trainAmount = contract.getTrainAmount();

            List<Integer> needPayAmountList = getNeedPayAmountList(contract.getPayBegin(), contract.getPayInterval(), contract.getTrainAmount());
            if (needPayAmountList.contains(finishAmount)) {
                needPayList.add(contract);
            }
            if (finishAmount.equals(trainAmount)) {
                finishList.add(contract);
            }
        }

        if (CollectionUtils.isNotEmpty(needPayList)) {
            // 查询预警接收人username列表
            List<String> usernameList = buBaseAlertAcceptMapper.selectUsernameListByAlertType(9);
            if (CollectionUtils.isNotEmpty(usernameList)) {
                // 发消息
                sendMessageByNewThread(needPayList, sysUsername, usernameList);
            }
        }
        if (CollectionUtils.isNotEmpty(finishList)) {
            // 更新委外部件质保期
            List<BuOutsourceOutinQuality> qualityList = new ArrayList<>();
            for (BuContractInfo contract : finishList) {
                int expiration = null == contract.getExpiration() ? 0 : contract.getExpiration();
                Date startDate = exchange.getSendDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.MONTH, expiration);
                Date endDate = calendar.getTime();
                int dayDiff = DateUtils.dayDiff(startDate, endDate);

                BuOutsourceOutinQuality quality = new BuOutsourceOutinQuality()
                        .setStartDate(startDate)
                        .setEndDate(endDate)
                        .setDayCount(dayDiff)
                        .setContractId(contract.getId());
                qualityList.add(quality);

                contract.setWarrantyStartDate(startDate)
                        .setWarrantyFinishDate(endDate);
            }
            buOutsourceOutinQualityMapper.updateListWarranty(qualityList);

            // 更新委外合同质保开始和结束时间
            List<List<BuContractInfo>> finishContractBatchSubList = DatabaseBatchSubUtil.batchSubList(finishList);
            for (List<BuContractInfo> finishContractBatchSub : finishContractBatchSubList) {
                buContractInfoMapper.updateListWarranty(finishContractBatchSub);
            }
        }
    }

    private static List<Integer> getNeedPayAmountList(Integer begin, Integer interval, Integer total) {
        if (null == total || total <= 0) {
            return new ArrayList<>();
        }
        if (null == begin || begin < 0) {
            return new ArrayList<>();
        }
        if (null == interval || interval < 0) {
            return new ArrayList<>();
        }
        if (interval == 0) {
            interval = 1;
        }

        List<Integer> resultList = new ArrayList<>();
        while (begin <= total) {
            resultList.add(begin);
            begin = begin + interval;
        }
        if (!resultList.contains(total)) {
            resultList.add(total);
        }
        resultList.removeIf(item -> 0 == item);

        return resultList;
    }

    private void saveLeaveAndRectify(BuRepairExchang exchange) {
        List<BuRepairExchangLeave> leaveList = exchange.getLeaveList();
        if (CollectionUtils.isNotEmpty(leaveList)) {
            leaveList.forEach(item -> qualityTransformationAPI.leaveRecordTransformation(item));
        }
        List<BuRepairExchangRectify> rectifyList = exchange.getRectifyList();
        if (CollectionUtils.isNotEmpty(rectifyList)) {
            rectifyList.forEach(item -> qualityTransformationAPI.rectifyTransformation(item));
        }
    }

}
