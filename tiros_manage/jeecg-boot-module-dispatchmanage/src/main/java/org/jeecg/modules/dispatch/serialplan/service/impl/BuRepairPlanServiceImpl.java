package org.jeecg.modules.dispatch.serialplan.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.tiros.cache.dict.DictCacheService;
import org.jeecg.common.tiros.cache.workstation.WorkstationBO;
import org.jeecg.common.tiros.cache.workstation.WorkstationCacheService;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.tiros.holiday.bean.SpecialHoliday;
import org.jeecg.common.tiros.holiday.service.HolidayService;
import org.jeecg.common.tiros.rpt.service.MaterialRptService;
import org.jeecg.common.tiros.rpt.service.PlanProgressUpdateService;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.common.util.MapSizeUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.workflow.bean.WfBizStatus;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangMapper;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanYearDetailMapper;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanForms;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanTaskForms;
import org.jeecg.modules.dispatch.planform.mapper.BuRepairPlanFormsMapper;
import org.jeecg.modules.dispatch.planform.mapper.BuRepairPlanTaskFormsMapper;
import org.jeecg.modules.dispatch.serialplan.bean.*;
import org.jeecg.modules.dispatch.serialplan.bean.bo.PlanHistoryCostExcelBO;
import org.jeecg.modules.dispatch.serialplan.bean.tp.*;
import org.jeecg.modules.dispatch.serialplan.bean.vo.*;
import org.jeecg.modules.dispatch.serialplan.mapper.*;
import org.jeecg.modules.dispatch.serialplan.service.BuRepairPlanService;
import org.jeecg.modules.dispatch.specassetplan.bean.BuSpecAssetPlan;
import org.jeecg.modules.dispatch.specassetplan.mapper.BuSpecAssetPlanMapper;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrder;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderMaterial;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderMaterialActs;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTask;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderMaterialActsMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderMaterialMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderTaskMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * <p>
 * 列计划 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Slf4j
@Service
public class BuRepairPlanServiceImpl extends ServiceImpl<BuRepairPlanMapper, BuRepairPlan> implements BuRepairPlanService {

    @Resource
    private BuRepairPlanMapper buRepairPlanMapper;
    @Resource
    private BuRepairPlanTaskMapper buRepairPlanTaskMapper;
    @Resource
    private BuRepairExchangMapper buRepairExchangMapper;
    @Resource
    private BuRepairPlanYearDetailMapper buRepairPlanYearDetailMapper;
    @Resource
    private BuTpRepairPlanTaskDispatchMapper buTpRepairPlanTaskDispatchMapper;
    @Resource
    private BuRepairTaskReguMapper buRepairTaskReguMapper;
    @Resource
    private BuRepairTaskBookStepMapper buRepairTaskBookStepMapper;
    @Resource
    private BuRepairPlanSpeEqMapper buRepairPlanSpeEqMapper;
    @Resource
    private BuRepairTaskWorkstationMapper buRepairTaskWorkstationMapper;
    @Resource
    private BuRepairTaskMaterialMapper buRepairTaskMaterialMapper;
    @Resource
    private BuRepairTaskToolMapper buRepairTaskToolMapper;
    @Resource
    private BuRepairTaskStaffRequireMapper buRepairTaskStaffRequireMapper;
    @Resource
    private BuRepairTaskMustReplaceMapper buRepairTaskMustReplaceMapper;
    @Resource
    private BuRepairPlanTaskPreMapper buRepairPlanTaskPreMapper;
    @Resource
    private BuRepairTaskTrackMapper buRepairTaskTrackMapper;
    @Resource
    private BuRepairPlanTaskEquMapper buRepairPlanTaskEquMapper;
    @Resource
    private BuRepairPlanFormsMapper buRepairPlanFormsMapper;
    @Resource
    private BuRepairPlanTaskFormsMapper buRepairPlanTaskFormsMapper;
    @Resource
    private BuSpecAssetPlanMapper buSpecAssetPlanMapper;
    @Resource
    private BuTrainStructureDetailDispatchMapper buTrainStructureDetailDispatchMapper;
    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;
    @Resource
    private BuWorkOrderMaterialMapper buWorkOrderMaterialMapper;
    @Resource
    private BuWorkOrderMaterialActsMapper buWorkOrderMaterialActsMapper;
    @Resource
    private BuWorkOrderTaskMapper buWorkOrderTaskMapper;
    @Resource
    private BuRepairProgramMapper buRepairProgramMapper;
    @Resource
    private DictCacheService dictCacheService;
    @Resource
    private HolidayService holidayService;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private WorkstationCacheService workstationCacheService;
    @Resource
    private PlanProgressUpdateService planProgressUpdateService;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;
    @Resource
    private MaterialRptService materialRptService;


    /**
     * @see BuRepairPlanService#page(BuRepairPlanQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuRepairPlan> page(BuRepairPlanQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        // 如果手动控制显示监控大屏显示数据，包括列计划进度、工班任务进度、委外任务进度
        String monitorScreenControlDataManual = sysConfigService.getConfigValueByCode("MonitorScreenControl_DataManual");
        boolean manualControl = StringUtils.isNotBlank(monitorScreenControlDataManual) && "1".equals(monitorScreenControlDataManual);
        boolean forMonitorScreen1 = null != queryVO.getProgressStatus() && 1 == queryVO.getProgressStatus() && 3 == pageSize;
        boolean forMonitorScreen2 = null != queryVO.getProgressStatus() && 2 == queryVO.getProgressStatus() && 1000 == pageSize;
        if (manualControl && forMonitorScreen1) {
            String data = sysConfigService.getConfigValueByCode("MonitorScreenControl_PlanData");
            List<BuRepairPlan> planList = new ArrayList<>();
            if (StringUtils.isNotBlank(data)) {
                planList = JSON.parseArray(data, BuRepairPlan.class);
            }

            return new Page<BuRepairPlan>()
                    .setCurrent(1)
                    .setSize(pageSize)
                    .setTotal(planList.size())
                    .setPages((planList.size() / pageSize) + 1)
                    .setRecords(planList);
        }
        if (manualControl && forMonitorScreen2) {
            return new Page<BuRepairPlan>()
                    .setCurrent(1)
                    .setSize(pageSize)
                    .setTotal(0)
                    .setPages(0)
                    .setRecords(new ArrayList<>());
        }

        return buRepairPlanMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuRepairPlanService#selectPlanAndTask(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanVOGantt selectPlanAndTask(String id) throws Exception {
        BuRepairPlan plan = buRepairPlanMapper.selectPlanByPlanId(id);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }

        List<BuRepairPlanTask> taskList = buRepairPlanTaskMapper.selectListByPlanId(id);
        if (CollectionUtils.isNotEmpty(taskList)) {
            // 设置工位信息
            List<BuRepairTaskWorkstation> workstationList = buRepairPlanMapper.selectPlanWorkstations(id);
            Map<String, List<BuRepairTaskWorkstation>> taskWorkstationListMap = workstationList.stream()
                    .collect(Collectors.groupingBy(BuRepairTaskWorkstation::getTaskId));
            for (BuRepairPlanTask task : taskList) {
                List<BuRepairTaskWorkstation> workstations = taskWorkstationListMap.get(task.getId());
                task.setWorkstations(workstations);
            }
        }
        plan.setTasks(taskList);

        return transformToGantt(plan);
    }

    /**
     * @see BuRepairPlanService#savePlan(BuRepairPlan)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean savePlan(BuRepairPlan plan) throws Exception {
        if (StringUtils.isBlank(plan.getId())) {
            plan.setId(UUIDGenerator.generate());
        }

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = null == sysUser ? "admin" : sysUser.getId();

        Date now = new Date();

        // 检查交接车是否已关联列计划
        checkExchangeUsed(plan);

        // 设置列计划属性
        setPlanProperties(plan);
        buRepairPlanMapper.insert(plan);

        // 如果选择了计划模板，根据计划模板的任务及任务关联信息生成列计划的对应信息
        if (StringUtils.isNotBlank(plan.getPlanTemplateId())) {
            Map<String, String> tpPlanFormIdPlanFormIdMap = saveFormsByTpPlanId(plan, userId);
            insertPlanTaskAndRelationByTpPlanId(plan, tpPlanFormIdPlanFormIdMap, userId, now);
        }

        // 更新列计划的结束时间和工期
        updatePlanFinishTimeAndDuration(plan);

        return true;
    }

    /**
     * @see BuRepairPlanService#editPlan(BuRepairPlan)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean editPlan(BuRepairPlan plan) throws Exception {
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = null == sysUser ? "admin" : sysUser.getId();

        Date now = new Date();

        // 检查交接车是否已关联列计划
        checkExchangeUsed(plan);

        // 查询数据库原有的列计划信息
        BuRepairPlan dbBuRepairPlan = buRepairPlanMapper.selectById(plan.getId());

        // 设置列计划属性
        setPlanProperties(plan);
        buRepairPlanMapper.updateById(plan);

        // 如果修改了计划模板、时间、车辆中的任何一项， 删除原有的所有任务和任务关联信息后，根据新的计划模板的任务及任务关联信息生成列计划的对应信息
        boolean planTemplateSame = plan.getPlanTemplateId().equals(dbBuRepairPlan.getPlanTemplateId());
        boolean trainNoSame = plan.getTrainNo().equals(dbBuRepairPlan.getTrainNo());
        boolean startDateSame = plan.getStartDate().equals(dbBuRepairPlan.getStartDate());
        boolean allSame = planTemplateSame && trainNoSame && startDateSame;
        if (!allSame) {
            deleteWfBizStatusByPlanIdList(Collections.singletonList(plan.getId()));
            deletePlanTaskAndRelationByPlanIdList(Collections.singletonList(plan.getId()));
            deletePlanFormsByPlanIdList(Collections.singletonList(plan.getId()));

            Map<String, String> tpPlanFormIdPlanFormIdMap = saveFormsByTpPlanId(plan, userId);
            insertPlanTaskAndRelationByTpPlanId(plan, tpPlanFormIdPlanFormIdMap, userId, now);
            updatePlanFinishTimeAndDuration(plan);
        }

        return true;
    }

    /**
     * 重新生成列计划任务及任务相关的信息
     *
     * @param planId
     * @return
     */
    @Override
    public boolean rebuild(String planId, Integer beginDayIndex) {
        BuRepairPlan plan = buRepairPlanMapper.selectById(planId);

        if (null == plan) {
            log.warn("重新生成列计划，列计划ID：{}， 不存在", planId);
            return false;
        }

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = null == sysUser ? "admin" : sysUser.getId();

        Date now = new Date();

        // 流程状态不删除
        // deleteWfBizStatusByPlanIdList(Collections.singletonList(planInfo.getId()));
        try {
            if (null == beginDayIndex) {
                deletePlanTaskAndRelationByPlanIdList(Collections.singletonList(plan.getId()));
                deletePlanFormsByPlanIdList(Collections.singletonList(plan.getId()));

                Map<String, String> tpPlanFormIdPlanFormIdMap = saveFormsByTpPlanId(plan, userId);
                insertPlanTaskAndRelationByTpPlanId(plan, tpPlanFormIdPlanFormIdMap, userId, now);
            } else {
                deletePlanTaskAndRelationByPlanIdList(Collections.singletonList(plan.getId()), beginDayIndex);
                // 表单不好删除啊
                // deletePlanFormsByPlanIdList(Collections.singletonList(planInfo.getId()));

                // 表单没有删除，所以别保存了
                // saveFormsByTpPlanId(planInfo);
                Map<String, String> tpPlanFormIdPlanFormIdMap = getTpPlanFormIdPlanFormIdMapByPlan(plan);
                insertPlanTaskAndRelationByTpPlanId(plan, tpPlanFormIdPlanFormIdMap, beginDayIndex, userId, now);
            }
            // 更新列计划的完成时间及工期
            updatePlanFinishTimeAndDuration(plan);

            return true;
        } catch (Exception ex) {
            log.error("重新生成{},列计划异常：", planId, ex);
            return false;
        }
    }

    /**
     * @see BuRepairPlanService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        deleteWfBizStatusByPlanIdList(idList);
        deletePlanTaskAndRelationByPlanIdList(idList);
        deletePlanFormsByPlanIdList(idList);

        buRepairPlanMapper.deleteBatchIds(idList);

        return true;
    }

    /**
     * @see BuRepairPlanService#suspendPlanBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean suspendPlanBatch(String ids) throws Exception {
        Date now = new Date();

        List<String> idList = Arrays.asList(ids.split(","));

        List<BuRepairPlan> planList = buRepairPlanMapper.selectPlanByPlanIdList(idList);
        if (CollectionUtils.isEmpty(planList)) {
            throw new JeecgBootException("请选择列计划");
        }

        List<Integer> finishProgressStatusList = Arrays.asList(3, 4, 5);
        for (BuRepairPlan plan : planList) {
            if (!"已结束".equals(plan.getWfStatus())) {
                throw new JeecgBootException("列计划[" + plan.getPlanName() + "]未审批完成，无法暂停");
            }

            Integer progressStatus = plan.getProgressStatus();
            if (finishProgressStatusList.contains(progressStatus)) {
                throw new JeecgBootException("列计划[" + plan.getPlanName() + "]已完成，无法暂停");
            }
            if (6 == progressStatus) {
                throw new JeecgBootException("列计划[" + plan.getPlanName() + "]已暂停，请勿重复暂停");
            }
            plan.setProgressStatus(6)
                    .setSuspendTime(now)
                    .setSuspendedProgressStatus(progressStatus);
            buRepairPlanMapper.updateById(plan);
        }

        return true;
    }

    /**
     * @see BuRepairPlanService#activatePlanBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean activatePlanBatch(String ids) throws Exception {
        Date now = new Date();

        List<String> idList = Arrays.asList(ids.split(","));

        List<BuRepairPlan> planList = buRepairPlanMapper.selectBatchIds(idList);
        if (CollectionUtils.isEmpty(planList)) {
            throw new JeecgBootException("请选择列计划");
        }

        for (BuRepairPlan plan : planList) {
            Integer progressStatus = plan.getProgressStatus();
            if (6 != progressStatus) {
                throw new JeecgBootException("列计划[" + plan.getPlanName() + "]未暂停，不可激活");
            }

            Integer suspendedProgressStatus = plan.getSuspendedProgressStatus();
            plan.setProgressStatus(suspendedProgressStatus)
                    .setActivateTime(now)
                    .setSuspendedProgressStatus(null);
            buRepairPlanMapper.updateById(plan);
        }

        return true;
    }

    /**
     * @see BuRepairPlanService#selectRePairExchang(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairExchang> selectRePairExchang(String lineId) throws Exception {
        LambdaQueryWrapper<BuRepairExchang> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(BuRepairExchang::getId, BuRepairExchang::getTrainNo, BuRepairExchang::getTrainIndex, BuRepairExchang::getLineId)
                .eq(BuRepairExchang::getStatus, 1)
                .eq(BuRepairExchang::getTradeType, 0);
        if (StringUtils.isNotBlank(lineId)) {
            wrapper.eq(BuRepairExchang::getLineId, lineId);
        }

        // 不需要这个过滤条件，列计划被审判完成后修改交接车状态，
//                .notInSql(BuRepairExchang::getId, "select exchange_id from bu_repair_plan"));
        return buRepairExchangMapper.selectList(wrapper);
    }

    /**
     * @see BuRepairPlanService#selectRepairYearStartDate(int, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanYearDetail selectRepairYearStartDate(int trainIndex, String lineId) throws Exception {
        BuRepairPlanYearDetail planYearDetail = buRepairPlanYearDetailMapper.selectOne(Wrappers.<BuRepairPlanYearDetail>lambdaQuery()
                .select(BuRepairPlanYearDetail::getStartDate)
                .eq(BuRepairPlanYearDetail::getTrainIndex, trainIndex)
                .eq(BuRepairPlanYearDetail::getLineId, lineId));
        return planYearDetail;
    }

    /**
     * @see BuRepairPlanService#selectTpRepairPlan()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTpRepairPlan> selectTpRepairPlan() throws Exception {
        return buRepairPlanMapper.selectTpRepairPlan();
    }

    /**
     * @see BuRepairPlanService#selectPlanRelevanceInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanRelationVO selectPlanRelevanceInfo(String planId) throws Exception {
        BuRepairPlan plan = buRepairPlanMapper.selectById(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }

        List<BuRepairTaskRegu> reguInfoList = buRepairTaskReguMapper.selectDistinctListByPlanId(planId);
        List<BuRepairTaskMaterial> materialList = buRepairTaskMaterialMapper.selectDistinctListByPlanId(planId);
        List<BuRepairTaskTool> toolList = buRepairTaskToolMapper.selectDistinctListByPlanId(planId);
        List<BuRepairTaskStaffRequire> personList = buRepairTaskStaffRequireMapper.selectListByPlanId(planId);
        List<BuRepairTaskMustReplace> mustReplaceList = buRepairTaskMustReplaceMapper.selectDistinctListByPlanId(planId);
        List<BuRepairPlanSpeEq> specAssetList = buRepairPlanSpeEqMapper.selectListByPlanId(planId);
        // distinct+clob会保存，所以查询出来后手动去重
        List<BuRepairTaskBookStep> bookStepList = buRepairTaskBookStepMapper.selectListByPlanId(planId);
        List<BuRepairTaskBookStep> distinctBookStepList = new ArrayList<>();
        Set<String> bookDetailIdSet = new HashSet<>();
        for (BuRepairTaskBookStep bookStep : bookStepList) {
            if (!bookDetailIdSet.contains(bookStep.getBookDetailId())) {
                distinctBookStepList.add(bookStep);
                bookDetailIdSet.add(bookStep.getBookDetailId());
            }
        }
        List<BuRepairPlanTaskEqu> equipmentList = buRepairPlanTaskEquMapper.selectDistinctListByPlanId(planId);

        return new BuRepairPlanRelationVO()
                .setId(planId)
                .setRepairPlanReguInfo(reguInfoList)
                .setMaterials(materialList)
                .setTools(toolList)
                .setPersons(personList)
                .setMustReplaces(mustReplaceList)
                .setBookSteps(distinctBookStepList)
                .setSpecAssets(specAssetList)
                .setEquipments(equipmentList);
    }

    /**
     * @see BuRepairPlanService#noRelevanceCount(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int noRelevanceCount(String planId, String reguId) throws Exception {
        return buRepairPlanMapper.noRelevanceCount(planId, reguId);
    }

    /**
     * @see BuRepairPlanService#noRelevanceDetail(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairReguDetail> noRelevanceDetail(String planId, String reguId) throws Exception {
        return buRepairPlanMapper.noRelevanceDetail(planId, reguId);
    }

    /**
     * @see BuRepairPlanService#getPlanUnFinishTaskInfo(String, String, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public PlanProgressVO getPlanUnFinishTaskInfo(String planId, String groupId, Integer status) throws Exception {
        BuRepairPlan plan = buRepairPlanMapper.selectById(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }

        PlanProgressVO progressVO = new PlanProgressVO()
                .setTotal(0)
                .setFinished(0)
                .setUnFinished(0)
                .setUnFinishedList(new ArrayList<>());

        List<BuRepairPlanTask> planTaskList = buRepairPlanTaskMapper.selectListByPlanId(plan.getId());
        // 过滤
        if (StringUtils.isNotBlank(groupId)) {
            planTaskList.removeIf(item -> !groupId.equals(item.getWorkGroupId()));
        }
        if (null != status) {
            planTaskList.removeIf(item -> !status.equals(item.getStatus()));
        }
//            // 去掉委外劳务班的列计划任务，这里为硬编码
//            planTaskList.removeIf(planTask -> "wwlwb".equals(planTask.getWorkGroupId()));
        if (CollectionUtils.isNotEmpty(planTaskList)) {
            // 获取用于生成工单的列计划任务：所有叶子节点任务
            List<BuRepairPlanTask> planTaskLeafNodeList = extractPlanTaskLeafNodeList(null, planTaskList);
            if (CollectionUtils.isNotEmpty(planTaskLeafNodeList)) {
                planTaskLeafNodeList.sort(Comparator.comparing(BuRepairPlanTask::getActFinish, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());

                int total = 0;
                int finished = 0;
                int unFinished = 0;
                List<PlanProgressTaskVO> unFinishedList = new ArrayList<>();

                List<BuRepairPlanTask> unFinishedLeafNodeList = new ArrayList<>();
                for (BuRepairPlanTask leafNode : planTaskLeafNodeList) {
                    // 工时合计
                    total++;
                    if (2 == leafNode.getStatus()) {
                        finished++;
                    } else {
                        unFinished++;
                        unFinishedLeafNodeList.add(leafNode);
                    }
                }

                if (CollectionUtils.isNotEmpty(unFinishedLeafNodeList)) {
                    // 查询未完成的列计划任务对应的工单和工单任务
                    Map<String, List<BuWorkOrderTask>> planTaskIdOrderTaskListMap = new HashMap<>();
                    Map<String, BuWorkOrder> idOrderMap = new HashMap<>();
                    List<String> planTaskIdList = unFinishedLeafNodeList.stream()
                            .map(BuRepairPlanTask::getId)
                            .collect(Collectors.toList());
                    List<List<String>> planTaskIdBatchSubList = DatabaseBatchSubUtil.batchSubList(planTaskIdList);
                    for (List<String> planTaskIdBatchSub : planTaskIdBatchSubList) {
                        LambdaQueryWrapper<BuWorkOrderTask> taskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                                .in(BuWorkOrderTask::getTaskObjId, planTaskIdBatchSub);
                        List<BuWorkOrderTask> subOrderTaskList = buWorkOrderTaskMapper.selectList(taskWrapper);
                        if (CollectionUtils.isNotEmpty(subOrderTaskList)) {
                            for (BuWorkOrderTask orderTask : subOrderTaskList) {
                                String taskObjId = orderTask.getTaskObjId();
                                if (StringUtils.isNotBlank(taskObjId)) {
                                    List<BuWorkOrderTask> list = planTaskIdOrderTaskListMap.getOrDefault(taskObjId, new ArrayList<>());
                                    list.add(orderTask);
                                    planTaskIdOrderTaskListMap.put(taskObjId, list);
                                }
                            }

                            List<String> orderIdList = subOrderTaskList.stream()
                                    .map(BuWorkOrderTask::getOrderId)
                                    .filter(StringUtils::isNotBlank)
                                    .distinct()
                                    .collect(Collectors.toList());
                            List<BuWorkOrder> subOrderList = buWorkOrderMapper.selectBatchIds(orderIdList);
                            subOrderList.forEach(order -> idOrderMap.put(order.getId(), order));
                        }
                    }

                    // 转化
                    for (BuRepairPlanTask planTask : unFinishedLeafNodeList) {
                        String planTaskId = planTask.getId();
                        // 列计划任务的属性
                        PlanProgressTaskVO progressTaskVO = new PlanProgressTaskVO();
                        BeanUtils.copyProperties(planTask, progressTaskVO);
                        // 工单和工单任务的属性
                        List<BuWorkOrderTask> orderTaskList = planTaskIdOrderTaskListMap.get(planTaskId);
                        if (CollectionUtils.isNotEmpty(orderTaskList)) {
                            orderTaskList.sort(Comparator.comparing(BuWorkOrderTask::getTaskStatus, Comparator.nullsLast(Comparator.naturalOrder())));
                            BuWorkOrderTask orderTask = orderTaskList.get(0);
                            progressTaskVO.setOrderTaskNo(orderTask.getTaskNo())
                                    .setOrderTaskName(orderTask.getTaskName())
                                    .setOrderTaskStatus(orderTask.getTaskStatus());
                            BuWorkOrder order = idOrderMap.get(orderTask.getOrderId());
                            if (null != order) {
                                BeanUtils.copyProperties(order, progressTaskVO);
                            }
                        }

                        progressTaskVO.setId(planTaskId);
                        unFinishedList.add(progressTaskVO);
                    }
                }

                progressVO.setTotal(total)
                        .setFinished(finished)
                        .setUnFinished(unFinished)
                        .setUnFinishedList(unFinishedList);
            }
        }

        return progressVO;
    }

    /**
     * @see BuRepairPlanService#setPlanTaskFinish(String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setPlanTaskFinish(String planId, String planTaskIds) throws Exception {
        Date now = new Date();

        BuRepairPlan plan = buRepairPlanMapper.selectById(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }
        if (0 == plan.getProgressStatus()) {
            throw new JeecgBootException("列计划未开始");
        }
        if (null != plan.getProgress() && 100 <= plan.getProgress()) {
            throw new JeecgBootException("列计划已完成");
        }
        if (6 == plan.getProgressStatus()) {
            throw new JeecgBootException("列计划已暂停");
        }

        List<String> planTaskIdList = Arrays.asList(planTaskIds.split(","));
        List<List<String>> planTaskIdBatchSubList = DatabaseBatchSubUtil.batchSubList(planTaskIdList);
        for (List<String> planTaskIdBatchSub : planTaskIdBatchSubList) {
            // 修改列计划任务任务状态=已完成2
            LambdaQueryWrapper<BuRepairPlanTask> planTaskWrapper = new LambdaQueryWrapper<BuRepairPlanTask>()
                    .in(BuRepairPlanTask::getId, planTaskIdBatchSub);
            BuRepairPlanTask planTask = new BuRepairPlanTask()
                    .setStatus(2)
                    .setActFinish(now)
                    .setProgress(100D);
            buRepairPlanTaskMapper.update(planTask, planTaskWrapper);

            // 如果工单任务对应的列计划任务有停放计划，则需要在任务完成后清空对应车辆的股道
            LambdaQueryWrapper<BuRepairTaskTrack> planTaskTrackWrapper = new LambdaQueryWrapper<BuRepairTaskTrack>()
                    .in(BuRepairTaskTrack::getTaskId, planTaskIdBatchSub);
            List<BuRepairTaskTrack> planTaskTrackList = buRepairTaskTrackMapper.selectList(planTaskTrackWrapper);
            List<String> trackIdList = planTaskTrackList.stream()
                    .map(BuRepairTaskTrack::getTrackId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(trackIdList)) {
                String trainNo = plan.getTrainNo();
                buRepairTaskTrackMapper.clearTrainTrackList(trainNo, trackIdList);
            }
        }

        // 修改列计划的进度和状态
        planProgressUpdateService.updatePlanProgressAndStatus(Collections.singletonList(planId), now);

        return true;
    }

    /**
     * @see BuRepairPlanService#importHistoryDataFromExcel(MultipartFile, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importHistoryDataFromExcel(MultipartFile excelFile, Boolean clearOldData) throws Exception {
        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (null == sysUser) {
            throw new JeecgBootException("无法获取到当前登录人信息，请先登录系统");
        }

        String userId = sysUser.getId();
        String userRealName = sysUser.getRealname();
        String workshopId = sysUser.getWorkshopId();
        if (StringUtils.isBlank(workshopId) || !Arrays.asList("CJ1", "CJ2").contains(workshopId)) {
            throw new JeecgBootException("当前登录人未明确归属车间，请使用其他账号操作");
        }

        if (excelFile.isEmpty()) {
            throw new JeecgBootException("文件为空");
        }
        String filename = excelFile.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new JeecgBootException("文件名为空");
        }

        InputStream inputStream = excelFile.getInputStream();
        Workbook workbook;

        String extName = filename.substring(filename.lastIndexOf(".") + 1);
        if (StringUtils.equals("xls", extName)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (StringUtils.equals("xlsx", extName)) {
            workbook = new XSSFWorkbook(inputStream);
        } else {
            throw new JeecgBootException("错误的excel文件");
        }
        inputStream.close();

        int numberOfSheets = workbook.getNumberOfSheets();
        log.info("导入excel历史列计划和接车开始");
        log.info("文件【" + excelFile.getOriginalFilename() + "】总共表单个数=" + numberOfSheets);

        // 查询修程
        Map<String, String> programNameIdMap = new HashMap<>();
        List<BuRepairProgram> programList = buRepairProgramMapper.selectList(Wrappers.emptyWrapper());
        programList.forEach(item -> programNameIdMap.put(item.getName(), item.getId()));
        // 查询车辆段
        Map<String, String> depotNameIdMap = new HashMap<>();
        List<Map<String, String>> depotNameIdList = buRepairPlanMapper.selectDepotNameId();
        depotNameIdList.forEach(item -> depotNameIdMap.put(item.get("name"), item.get("id")));

        boolean hasMatchingForms = false;
        List<BuRepairPlan> planList = new ArrayList<>();
        List<BuRepairExchang> receiveList = new ArrayList<>();
        List<BuRepairExchang> deliverList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String nowStr = DateUtils.datetimeFormat.get().format(now);
        String remark = String.format("%s由%s导入的历史数据", nowStr, userRealName);
        // 操作每个表单
        for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            log.info(String.format("第%s个表单【%s】", sheetNum + 1, sheet.getSheetName()));

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer indexNumCellNum = null;
            Integer trainNoCellNum = null;
            Integer mileageCellNum = null;
            Integer acceptTimeCellNum = null;
            Integer actAcceptTimeCellNum = null;
            Integer sendTimeCellNum = null;
            Integer actSendTimeCellNum = null;
            Integer officialSendTimeCellNum = null;
            Integer programCellNum = null;
            Integer depotCellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = ExcelUtil.getCellValueByCell(cell);
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }

                        if (value.equals("序号")) {
                            indexNumCellNum = cellNum;
                        } else if (value.equals("车号")) {
                            trainNoCellNum = cellNum;
                        } else if (value.contains("架修前公里数")) {
                            mileageCellNum = cellNum;
                        } else if (value.contains("计划接车时间")) {
                            acceptTimeCellNum = cellNum;
                        } else if (value.contains("实际接车时间")) {
                            actAcceptTimeCellNum = cellNum;
                        } else if (value.contains("计划交车时间")) {
                            sendTimeCellNum = cellNum;
                        } else if (value.contains("实际交车时间")) {
                            actSendTimeCellNum = cellNum;
                        } else if (value.contains("正式移交检修时间")) {
                            officialSendTimeCellNum = cellNum;
                        } else if (value.contains("修程")) {
                            programCellNum = cellNum;
                        } else if (value.contains("车辆段")) {
                            depotCellNum = cellNum;
                        }
                    }
                }
            }

            if (null == indexNumCellNum || null == trainNoCellNum || null == mileageCellNum || null == acceptTimeCellNum || null == actAcceptTimeCellNum
                    || null == sendTimeCellNum || null == actSendTimeCellNum || null == officialSendTimeCellNum || null == programCellNum || null == depotCellNum) {
                log.info(String.format("第%s个表单%s,没有必要信息(序号/车号/架修前公里数/计划接车时间/实际接车时间/计划交车时间/实际交车时间/正式移交检修时间/修程/车辆段),不进行处理", sheetNum + 1, sheet.getSheetName()));
            } else {
                log.info(String.format("第%s个表单%s,有历史列计划和接车信息,开始导入", sheetNum + 1, sheet.getSheetName()));

                hasMatchingForms = true;
                int rowCount = 0;

                // 操作每行数据
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    String indexNumStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(indexNumCellNum));
                    String trainNo = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(trainNoCellNum));
                    String mileageStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(mileageCellNum));
                    String acceptTimeStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(acceptTimeCellNum));
                    String actAcceptTimeStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(actAcceptTimeCellNum));
                    String sendTimeStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(sendTimeCellNum));
                    String actSendTimeStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(actSendTimeCellNum));
                    String officialSendTimeStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(officialSendTimeCellNum));
                    String programNameStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(programCellNum));
                    String depotNameStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(depotCellNum));

                    if (StringUtils.isBlank(indexNumStr) || StringUtils.isBlank(trainNo)) {
                        continue;
                    }

                    Integer indexNum = Integer.parseInt(indexNumStr.replaceAll("第", "").replaceAll("列", ""));
                    double mileage = Double.parseDouble(mileageStr);
                    Date acceptTime = dateFormat.parse(acceptTimeStr);
                    Date actAcceptTime = dateFormat.parse(actAcceptTimeStr);
                    Date sendTime = dateFormat.parse(sendTimeStr);
                    Date actSendTime = dateFormat.parse(actSendTimeStr);
                    Date officialSendTime = dateFormat.parse(officialSendTimeStr);
                    String programId = programNameIdMap.getOrDefault(programNameStr, "1");
                    String depotId = depotNameIdMap.getOrDefault(depotNameStr, "CLD1");

                    String planCode = String.format("%s-%s-%s-LS", trainNo, DateUtils.getYear(acceptTime), indexNum);
                    String planName = String.format("%s-%s车%s计划-历史", indexNumStr, trainNo, programNameStr);
                    String lineId = String.valueOf(trainNo.charAt(1));
                    int dayDiff = DateUtils.dayDiff(actAcceptTime, actSendTime, true);
//                    String workshopId = "CJ1";
//                    if ("CLD2".equals(depotId)) {
//                        workshopId = "CJ2";
//                    }

                    // 接车
                    BuRepairExchang receive = new BuRepairExchang()
                            .setId(UUIDGenerator.generate())
                            .setTradeType(0)
                            .setLineId(lineId)
                            .setTrainNo(trainNo)
                            .setProgramId(programId)
                            .setTrainIndex(indexNum)
                            .setSendDeptId(depotId)
                            .setSendUserId(userId)
                            .setSendDate(null)
                            .setAcceptDeptId(depotId)
                            .setAcceptUserId(userId)
                            .setAcceptDate(actAcceptTime)
                            .setAcceptMileage(mileage)
                            .setPlanFinishDate(sendTime)
                            .setRemark(remark)
                            .setStatus(3)
                            .setItemNo(indexNum)
                            .setTrainNumber(indexNum)
                            .setPlanTempId("-")
                            .setYearDetailId(null)
                            .setReceiveId(null)
                            .setWorkshopId(workshopId)
                            .setHistoryData(1);
                    receiveList.add(receive);
                    // 列计划
                    BuRepairPlan plan = new BuRepairPlan()
                            .setId(UUIDGenerator.generate())
                            .setCode(planCode)
                            .setPlanName(planName)
                            .setDepotId(depotId)
                            .setLineId(lineId)
                            .setRepairProgramId(programId)
                            .setExchangeId(receive.getId())
                            .setTrainNo(trainNo)
                            .setMileage(mileage)
                            .setPlanTemplateId("-")
                            .setStartDate(acceptTime)
                            .setFinishDate(sendTime)
                            .setActStart(actAcceptTime)
                            .setActFinish(actSendTime)
                            .setDuration(dayDiff)
                            .setStatus(2)
                            .setRemark(remark)
                            .setProgressStatus(3)
                            .setProgress(100)
                            .setActDuration(dayDiff)
                            .setTrainIndex(indexNum)
                            .setReguId(null)
                            .setFdProject(null)
                            .setFdTask(null)
                            .setFdCostType(null)
                            .setWorkshopId(workshopId)
                            .setSuspendTime(null)
                            .setActivateTime(null)
                            .setSuspendedProgressStatus(null)
                            .setHistoryData(1);
                    planList.add(plan);
                    // 交车
                    BuRepairExchang deliver = new BuRepairExchang()
                            .setId(UUIDGenerator.generate())
                            .setTradeType(1)
                            .setLineId(lineId)
                            .setTrainNo(trainNo)
                            .setProgramId(programId)
                            .setTrainIndex(indexNum)
                            .setSendDeptId(depotId)
                            .setSendUserId(userId)
                            .setSendDate(actSendTime)
                            .setAcceptDeptId(depotId)
                            .setAcceptUserId(userId)
                            .setAcceptDate(actAcceptTime)
                            .setAcceptMileage(mileage)
                            .setPlanFinishDate(sendTime)
                            .setRemark(remark)
                            .setStatus(3)
                            .setItemNo(indexNum)
                            .setTrainNumber(indexNum)
                            .setPlanTempId("-")
                            .setYearDetailId(null)
                            .setReceiveId(receive.getId())
                            .setWorkshopId(workshopId)
                            .setHistoryData(1);
                    deliverList.add(deliver);

                    rowCount++;
                }
                log.info("总共行数" + rowCount);
            }
        }

        // 如果没有匹配到模板表单，返回异常
        if (!hasMatchingForms) {
            log.info("文件【" + excelFile.getOriginalFilename() + "】未匹配到历史列计划和接车模板表单");
            throw new JeecgBootException("请选择正确的模板表单");
        } else {
            // 删除旧的
            int deletePlanCount = 0;
            int deleteReceiveCount = 0;
            int deleteDeliverCount = 0;
            if (null == clearOldData || clearOldData) {
                LambdaQueryWrapper<BuRepairPlan> planWrapper = new LambdaQueryWrapper<BuRepairPlan>()
                        .eq(BuRepairPlan::getWorkshopId, workshopId)
                        .eq(BuRepairPlan::getHistoryData, 1);
                deletePlanCount = buRepairPlanMapper.delete(planWrapper);
                LambdaQueryWrapper<BuRepairExchang> receiveWrapper = new LambdaQueryWrapper<BuRepairExchang>()
                        .eq(BuRepairExchang::getWorkshopId, workshopId)
                        .eq(BuRepairExchang::getHistoryData, 1)
                        .eq(BuRepairExchang::getTradeType, 0);
                deleteReceiveCount = buRepairExchangMapper.delete(receiveWrapper);
                LambdaQueryWrapper<BuRepairExchang> deliverWrapper = new LambdaQueryWrapper<BuRepairExchang>()
                        .eq(BuRepairExchang::getWorkshopId, workshopId)
                        .eq(BuRepairExchang::getHistoryData, 1)
                        .eq(BuRepairExchang::getTradeType, 1);
                deleteDeliverCount = buRepairExchangMapper.delete(deliverWrapper);
            }

            // 插入新的
            int savePlanCount = 0;
            if (CollectionUtils.isNotEmpty(planList)) {
                for (BuRepairPlan plan : planList) {
                    buRepairPlanMapper.insert(plan);
                    savePlanCount++;
                }
            }
            int saveReceiveCount = 0;
            if (CollectionUtils.isNotEmpty(receiveList)) {
                for (BuRepairExchang receive : receiveList) {
                    buRepairExchangMapper.insert(receive);
                    saveReceiveCount++;
                }
            }
            int saveDeliverCount = 0;
            if (CollectionUtils.isNotEmpty(deliverList)) {
                for (BuRepairExchang deliver : deliverList) {
                    buRepairExchangMapper.insert(deliver);
                    saveDeliverCount++;
                }
            }

            String importInfo = String.format("删除了%s条旧的历史列计划，插入了%s条新的历史列计划；删除了%s条旧的历史接车，插入了%s条新的历史接车；删除了%s条旧的历史交车，插入了%s条新的历史交车。",
                    deletePlanCount, savePlanCount, deleteReceiveCount, saveReceiveCount, deleteDeliverCount, saveDeliverCount);
            log.info(importInfo);

            return importInfo;
        }
    }

    /**
     * @see BuRepairPlanService#importPlanHistoryCostDataFromExcel(MultipartFile, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importPlanHistoryCostDataFromExcel(MultipartFile excelFile, String planId) throws Exception {
        if (StringUtils.isBlank(planId)) {
            throw new JeecgBootException("请输入列计划id");
        }
        BuRepairPlan plan = buRepairPlanMapper.selectPlanByPlanId(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在，id=" + planId);
        }

        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String username = null == sysUser ? "admin" : sysUser.getUsername();
        String userId = null == sysUser ? "admin" : sysUser.getId();
        String userRealName = null == sysUser ? "admin" : sysUser.getRealname();

        if (excelFile.isEmpty()) {
            throw new JeecgBootException("文件为空");
        }
        String filename = excelFile.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new JeecgBootException("文件名为空");
        }

        InputStream inputStream = excelFile.getInputStream();
        Workbook workbook;
        String extName = filename.substring(filename.lastIndexOf(".") + 1);
        if (StringUtils.equals("xls", extName)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (StringUtils.equals("xlsx", extName)) {
            workbook = new XSSFWorkbook(inputStream);
        } else {
            throw new JeecgBootException("错误的excel文件");
        }
        inputStream.close();

        int numberOfSheets = workbook.getNumberOfSheets();
        log.info("导入excel列计划历史成本数据开始");
        log.info("文件【" + excelFile.getOriginalFilename() + "】总共表单个数=" + numberOfSheets);
        boolean hasMatchingForms = false;
        // 提取excel数据
        // 操作每个表单
        // 第1个表单是备品备件，第2个表单是耗材
        List<PlanHistoryCostExcelBO> excelBOList = new ArrayList<>();
        for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            log.info(String.format("第%s个表单【%s】", sheetNum + 1, sheet.getSheetName()));

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer systemShortNameCellNum = null;
            Integer workstationNoCellNum = null;
            Integer materialTypeCodeCellNum = null;
            Integer userCategoryCellNum = null;
            Integer amountCellNum = null;
            Integer unitPriceCellNum = null;
            Integer remarkCellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = ExcelUtil.getCellValueByCell(cell);
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }

                        if (value.equals("系统")) {
                            systemShortNameCellNum = cellNum;
                        } else if (value.equals("工位号")) {
                            workstationNoCellNum = cellNum;
                        } else if (value.contains("物资编码")) {
                            materialTypeCodeCellNum = cellNum;
                        } else if (value.contains("属性（必换件/故障件/耗材）")) {
                            userCategoryCellNum = cellNum;
                        } else if (value.contains("数量")) {
                            amountCellNum = cellNum;
                        } else if (value.contains("单价（元）")) {
                            unitPriceCellNum = cellNum;
                        } else if (value.contains("备注")) {
                            remarkCellNum = cellNum;
                        }
                    }
                }
            }

            if (null == systemShortNameCellNum || null == workstationNoCellNum || null == materialTypeCodeCellNum ||
                    null == userCategoryCellNum || null == amountCellNum || null == unitPriceCellNum) {
                String format = String.format("第%s个表单%s,没有必要信息(系统/工位号/物资编码/属性（必换件/故障件/耗材）/数量/单价（元）),不进行处理", sheetNum + 1, sheet.getSheetName());
                log.info(format);
                throw new JeecgBootException(format);
            } else {
                log.info(String.format("第%s个表单%s,有列计划历史成本数据,开始导入", sheetNum + 1, sheet.getSheetName()));

                // 操作每行数据
                String lastSystemShortName = null;
                String lastWorkstationNo = null;
                hasMatchingForms = true;
                int rowCount = 0;
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    rowCount++;

                    String systemShortName = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(systemShortNameCellNum));
                    String workstationNo = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(workstationNoCellNum));
                    String materialTypeCode = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(materialTypeCodeCellNum));
                    String userCategoryStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(userCategoryCellNum));
                    String amountStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(amountCellNum));
                    String unitPriceStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(unitPriceCellNum));
                    String remarkStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(remarkCellNum));

                    if (StringUtils.isBlank(materialTypeCode)) {
                        log.warn(String.format("第%s个表单第%s行，物资编码为空，已跳过", sheetNum + 1, rowNum + 1));
                        continue;
                    }

                    if ("车上电气".equals(systemShortName)) {
                        systemShortName = "整车电气";
                    }
                    if (StringUtils.isNotBlank(systemShortName)) {
                        lastSystemShortName = systemShortName;
                    }
                    if (StringUtils.isNotBlank(workstationNo)) {
                        lastWorkstationNo = workstationNo;
                    }

                    PlanHistoryCostExcelBO excelBO = new PlanHistoryCostExcelBO()
                            .setSystemShortName(lastSystemShortName)
                            .setWorkstationNo(lastWorkstationNo)
                            .setMaterialTypeCode(materialTypeCode)
                            .setUserCategoryStr(userCategoryStr)
                            .setAmountStr(amountStr)
                            .setUnitPriceStr(unitPriceStr)
                            .setRemarkStr(remarkStr)
                            .setSheetIndex(sheetNum + 1)
                            .setRowIndex(rowNum + 1);
                    excelBOList.add(excelBO);
                }
                log.info("总共行数" + rowCount);
            }
        }
        if (CollectionUtils.isEmpty(excelBOList)) {
            throw new JeecgBootException("文件中未找到有效的导入数据，不进行操作");
        }

        Set<String> systemShortNameSet = excelBOList.stream()
                .map(PlanHistoryCostExcelBO::getSystemShortName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Set<String> workstationNoSet = excelBOList.stream()
                .map(PlanHistoryCostExcelBO::getWorkstationNo)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Set<String> materialTypeCodeSet = excelBOList.stream()
                .map(PlanHistoryCostExcelBO::getMaterialTypeCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        // 查询系统
        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = assetTypeCacheService.map(null);
        Map<String, String> systemShortNameIdMap = assetTypeIdBOMap.values().stream()
                .filter(item -> 1 == item.getStructType())
                .collect(Collectors.toMap(BuTrainAssetTypeBO::getShortName, BuTrainAssetTypeBO::getId));
        systemShortNameSet.removeAll(systemShortNameIdMap.keySet());
        if (CollectionUtils.isNotEmpty(systemShortNameSet)) {
            throw new JeecgBootException("无法找到对应系统短名称[" + String.join(",", systemShortNameSet) + "]，请核对文件数据后导入");
        }
        // 查询工位
        Map<String, WorkstationBO> workstationNoBOMap = workstationCacheService.mapKeyNo(plan.getWorkshopId());
        workstationNoSet.removeAll(workstationNoBOMap.keySet());
        if (CollectionUtils.isNotEmpty(workstationNoSet)) {
            throw new JeecgBootException("无法找到对应工位号[" + String.join(",", workstationNoSet) + "]，请核对文件数据后导入");
        }
        // 查询物资类型id和code
        Map<String, String> materialTypeCodeIdMap = new HashMap<>();
        List<Map<String, Object>> materialTypeIdCodeMapList = buWorkOrderMaterialMapper.selectMaterialTypeIdCodeByCodeList(new ArrayList<>(materialTypeCodeSet));
        for (Map<String, Object> materialTypeIdCodeMap : materialTypeIdCodeMapList) {
            String id = (String) materialTypeIdCodeMap.get("id");
            String code = (String) materialTypeIdCodeMap.get("code");
            materialTypeCodeIdMap.put(code, id);
        }
        materialTypeCodeSet.removeAll(materialTypeCodeIdMap.keySet());
        if (CollectionUtils.isNotEmpty(materialTypeCodeSet)) {
            throw new JeecgBootException("无法找到对应物料类型[" + String.join(",", materialTypeCodeSet) + "]，请核对文件数据后导入");
        }

        // 处理excel数据
        List<BuWorkOrderMaterial> orderMaterialList = new ArrayList<>();
        List<BuWorkOrderMaterialActs> orderMaterialActList = new ArrayList<>();
        for (PlanHistoryCostExcelBO excelBO : excelBOList) {
            String materialTypeId = materialTypeCodeIdMap.get(excelBO.getMaterialTypeCode());
            if (null == materialTypeId) {
                throw new JeecgBootException(String.format("第%s个表单第%s行，无法找到对应物资类型，物资编码=%s",
                        excelBO.getSheetIndex(), excelBO.getRowIndex(), excelBO.getMaterialTypeCode()));
            }

            // 必换件/故障件/耗材
            Integer useCategory = getUseCategory(excelBO.getUserCategoryStr());
            double amount = Double.parseDouble(excelBO.getAmountStr());
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble(excelBO.getUnitPriceStr()));

            // 系统id
            String systemId = systemShortNameIdMap.get(excelBO.getSystemShortName());
            if (StringUtils.isBlank(systemId)) {
                throw new JeecgBootException(String.format("第%s个表单第%s行，无法找到对应系統，物资编码=%s，系统=%s",
                        excelBO.getSheetIndex(), excelBO.getRowIndex(), excelBO.getMaterialTypeCode(), excelBO.getSystemShortName()));
            }
            // 工位id
            String workstationId = workstationNoBOMap.getOrDefault(excelBO.getWorkstationNo(), new WorkstationBO()).getId();
            if (StringUtils.isBlank(workstationId)) {
                throw new JeecgBootException(String.format("第%s个表单第%s行，无法找到对应工位，物资编码=%s，工位=%s",
                        excelBO.getSheetIndex(), excelBO.getRowIndex(), excelBO.getMaterialTypeCode(), excelBO.getWorkstationNo()));
            }

            // 工单物料
            BuWorkOrderMaterial orderMaterial = new BuWorkOrderMaterial()
                    .setId(UUIDGenerator.generate())
                    .setOrderId(null)
                    .setOrderTaskId(null)
                    .setMaterialTypeId(materialTypeId)
                    .setAmount(amount)
                    .setPlanAmount(amount)
                    .setActAmount(amount)
                    .setRemark(excelBO.getRemarkStr())
                    .setNeedDispatchin(0)
                    .setUseCategory(useCategory)
                    .setIsVerify(1)
                    .setOpType(1)
                    .setIsGenOrder(0)
                    .setSystemId(systemId)
                    .setWorkstationId(workstationId);
            orderMaterialList.add(orderMaterial);
            // 工单物料实际消耗
            BuWorkOrderMaterialActs orderMaterialAct = new BuWorkOrderMaterialActs()
                    .setId(UUIDGenerator.generate())
                    .setOrderMaterialId(orderMaterial.getId())
                    .setGroupStockId(null)
                    .setApplyId(null)
                    .setApplyDetailId(null)
                    .setAssignDetailId(null)
                    .setTradeNo(null)
                    .setActAmount(amount)
                    .setCreateTime(now)
                    .setCreateBy(username)
                    .setPrice(price);
            orderMaterialActList.add(orderMaterialAct);
        }

        // 如果没有匹配到模板表单，返回异常
        if (!hasMatchingForms) {
            log.info("文件【" + excelFile.getOriginalFilename() + "】未匹配到列计划历史成本数据模板表单");
            throw new JeecgBootException("请选择正确的模板表单");
        } else {
            if (CollectionUtils.isEmpty(orderMaterialList) && CollectionUtils.isEmpty(orderMaterialActList)) {
                log.info("文件【" + excelFile.getOriginalFilename() + "】匹配到列计划历史成本数据模板表单，但数据条数为0");
                throw new JeecgBootException("匹配到列计划历史成本数据模板表单，但数据条数为0，未导入数据");
            }

            String remark = String.format("%s由%s导入的历史消耗成本数据", DateUtils.datetimeFormat.get().format(now), userRealName);
            String orderCode = serialNumberGenerate.generateOrderCode(plan.getLineId());
            Date planStart = null != plan.getActStart() ? plan.getActStart() : plan.getStartDate();
            // 工单
            BuWorkOrder order = new BuWorkOrder()
                    .setId(UUIDGenerator.generate())
                    .setOrderCode(orderCode)
                    .setOrderName(plan.getPlanName() + "历史消耗工单")
                    .setOrderType(6)
                    .setGenerate(1)
                    .setPlanId(plan.getId())
                    .setStartTime(DateUtils.transToDayStart(planStart))
                    .setFinishTime(DateUtils.transToDayEnd(planStart))
                    .setDuration(1)
                    .setGroupId("-")
                    .setMonitor("-")
                    .setWorkshopId(plan.getWorkshopId())
                    .setLineId(plan.getLineId())
                    .setTrainNo(plan.getTrainNo())
                    .setActStart(DateUtils.transToDayStart(planStart))
                    .setActFinish(DateUtils.transToDayEnd(planStart))
                    .setOrderStatus(4)
                    .setWorkStatus(2)
                    .setRemark(remark)
                    .setCreateTime(now)
                    .setCreateBy(username)
                    .setIssuingTime(null)
                    .setFdProject(plan.getFdProject())
                    .setFdTask(plan.getFdTask())
                    .setFdCostType(plan.getFdCostType())
                    .setFromType(1)
                    .setSuspendStatus(null)
                    .setSuspendTime(null)
                    .setActiveTime(null)
                    .setMaximoWorkOrderId(null)
                    .setCompanyId(plan.getCompanyId());
            // 工单任务
            BuWorkOrderTask orderTask = new BuWorkOrderTask()
                    .setId(UUIDGenerator.generate())
                    .setOrderId(order.getId())
                    .setTaskType(1)
                    .setTaskObjId(null)
                    .setTaskNo(1)
                    .setTaskName(order.getOrderName())
                    .setTaskContent(order.getRemark())
                    .setWorkTime(0)
                    .setRemark(order.getRemark())
                    .setTaskStart(DateUtils.transToDayStart(now))
                    .setTaskFinish(DateUtils.transToDayEnd(now))
                    .setTaskTime(0)
                    .setReportTime(0)
                    .setTaskStatus(2)
                    .setOutTask(0)
                    .setAssetTypeId(null)
                    .setStructDetailId(null)
                    .setTrainAssetId(null)
                    .setImportant(0)
                    .setMethod(null)
                    .setSafeNotice(null);
            for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
                orderMaterial.setOrderId(order.getId())
                        .setOrderTaskId(orderTask.getId());
            }

            // 删除旧的
            // 查询历史成本工单
            LambdaQueryWrapper<BuWorkOrder> oldOrderWrapper = new LambdaQueryWrapper<BuWorkOrder>()
                    .eq(BuWorkOrder::getPlanId, planId)
                    .eq(BuWorkOrder::getOrderType, 6);
            List<BuWorkOrder> oldOrderList = buWorkOrderMapper.selectList(oldOrderWrapper);
            int deleteOrderMaterialActCount = 0;
            int deleteOrderMaterialCount = 0;
            int deleteOrderTaskCount = 0;
            int deleteOrderCount = 0;
            if (CollectionUtils.isNotEmpty(oldOrderList)) {
                List<String> oldOrderIdList = oldOrderList.stream()
                        .map(BuWorkOrder::getId)
                        .collect(Collectors.toList());
                deleteOrderMaterialActCount = buWorkOrderMaterialActsMapper.deleteByOrderIdList(oldOrderIdList);
                deleteOrderMaterialCount = buWorkOrderMaterialMapper.deleteByOrderIdList(oldOrderIdList);

                deleteOrderTaskCount = buWorkOrderTaskMapper.deleteByOrderIdList(oldOrderIdList);
                deleteOrderCount = buWorkOrderMapper.deleteBatchIds(oldOrderIdList);
            }

            // 插入新的
            buWorkOrderMapper.insert(order);
            buWorkOrderTaskMapper.insert(orderTask);
            int saveOrderMaterialCount = 0;
            if (CollectionUtils.isNotEmpty(orderMaterialList)) {
                buWorkOrderMaterialMapper.insertList(orderMaterialList);
                saveOrderMaterialCount = orderMaterialList.size();
            }
            int saveOrderMaterialActCount = 0;
            if (CollectionUtils.isNotEmpty(orderMaterialActList)) {
                buWorkOrderMaterialActsMapper.insertList(orderMaterialActList);
                saveOrderMaterialActCount = orderMaterialActList.size();
            }

            // 开线程重新统计车辆物料统计数据
            rebuildMaterialRptDataByNewThread(Collections.singletonList(plan.getTrainNo()));

            String importInfo = String.format("删除了旧的工单%s条、工单任务%s条、工单物料%s条、工单物料实际消耗%s条，" +
                            "插入了新的工单%s条、工单任务%s条、工单物料%s条、工单物料实际消耗%s条。",
                    deleteOrderCount, deleteOrderTaskCount, deleteOrderMaterialCount, deleteOrderMaterialActCount,
                    1, 1, saveOrderMaterialCount, saveOrderMaterialActCount);
            log.info(importInfo);

            return importInfo;
        }
    }

    @Override
    public List<MustMaterialLack> mustMaterialLack() throws Exception {
        List<MustMaterialLack> materialLacks = buRepairPlanMapper.mustMaterialLack();
        Map<String, MustMaterialLack> mustMaterialLackMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialLacks)) {
            materialLacks.forEach(lack -> {
                if (lack.getLack() > 0) {
                    String key = lack.getMaterialTypeId();
                    if (mustMaterialLackMap.containsKey(key)) {
                        MustMaterialLack mapValue = mustMaterialLackMap.get(key);
                        mapValue.setLack(mapValue.getLack() + lack.getLack())
                                .setReceive(mapValue.getReceive() + lack.getReceive())
                                .setNeedAmount(mapValue.getNeedAmount() + lack.getNeedAmount());
                        mustMaterialLackMap.put(key, mapValue);
                    } else {
                        mustMaterialLackMap.put(key, lack);
                    }
                }
            });
        }
        return new ArrayList<>(mustMaterialLackMap.values());
    }


    private void deleteWfBizStatusByPlanIdList(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }

        LambdaQueryWrapper<WfBizStatus> wrapper = new LambdaQueryWrapper<WfBizStatus>()
                .in(WfBizStatus::getBusinessKey, idList);
        new WfBizStatus().delete(wrapper);
    }

    private void deletePlanTaskAndRelationByPlanIdList(List<String> planIdList) {
        if (CollectionUtils.isEmpty(planIdList)) {
            return;
        }

        LambdaQueryWrapper<BuRepairPlanTask> planTaskWrapper = new LambdaQueryWrapper<BuRepairPlanTask>()
                .in(BuRepairPlanTask::getPlanId, planIdList);
        List<BuRepairPlanTask> planTasks = buRepairPlanTaskMapper.selectList(planTaskWrapper);
        List<String> taskIdList = planTasks.stream()
                .map(BuRepairPlanTask::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(taskIdList)) {
            deletePlanTaskRelation(taskIdList);
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskIdList);
            for (List<String> batchSub : batchSubList) {
                buRepairPlanTaskMapper.deleteBatchIds(batchSub);
            }
        }
    }

    private void deletePlanTaskAndRelationByPlanIdList(List<String> planIdList, Integer dayIndex) {
        if (CollectionUtils.isEmpty(planIdList)) {
            return;
        }

        LambdaQueryWrapper<BuRepairPlanTask> planTaskWrapper = new LambdaQueryWrapper<BuRepairPlanTask>()
                .in(BuRepairPlanTask::getPlanId, planIdList)
                .ge(BuRepairPlanTask::getDayIndex, dayIndex);
        List<BuRepairPlanTask> planTasks = buRepairPlanTaskMapper.selectList(planTaskWrapper);
        List<String> taskIdList = planTasks.stream()
                .map(BuRepairPlanTask::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(taskIdList)) {
            deletePlanTaskRelation(taskIdList);
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskIdList);
            for (List<String> batchSub : batchSubList) {
                buRepairPlanTaskMapper.deleteBatchIds(batchSub);
            }
        }
    }

    private void deletePlanFormsByPlanIdList(List<String> planIdList) {
        if (CollectionUtils.isNotEmpty(planIdList)) {
            LambdaQueryWrapper<BuRepairPlanForms> wrapper = new LambdaQueryWrapper<BuRepairPlanForms>()
                    .in(BuRepairPlanForms::getPlanId, planIdList);
            buRepairPlanFormsMapper.delete(wrapper);
        }
    }

    private void deletePlanTaskRelation(List<String> taskIdList) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return;
        }

        List<List<String>> taskIdBatchSubList = DatabaseBatchSubUtil.batchSubList(taskIdList);
        for (List<String> taskIdBatchSub : taskIdBatchSubList) {
            LambdaQueryWrapper<BuRepairTaskRegu> reguWrapper = new LambdaQueryWrapper<BuRepairTaskRegu>()
                    .in(BuRepairTaskRegu::getTaskId, taskIdBatchSub);
            buRepairTaskReguMapper.delete(reguWrapper);
            LambdaQueryWrapper<BuRepairTaskBookStep> bookStepWrapper = new LambdaQueryWrapper<BuRepairTaskBookStep>()
                    .in(BuRepairTaskBookStep::getTaskId, taskIdBatchSub);
            buRepairTaskBookStepMapper.delete(bookStepWrapper);
            LambdaQueryWrapper<BuRepairPlanSpeEq> specAssetWrapper = new LambdaQueryWrapper<BuRepairPlanSpeEq>()
                    .in(BuRepairPlanSpeEq::getTaskId, taskIdBatchSub);
            buRepairPlanSpeEqMapper.delete(specAssetWrapper);
            LambdaQueryWrapper<BuRepairTaskWorkstation> workstationWrapper = new LambdaQueryWrapper<BuRepairTaskWorkstation>()
                    .in(BuRepairTaskWorkstation::getTaskId, taskIdBatchSub);
            buRepairTaskWorkstationMapper.delete(workstationWrapper);
            LambdaQueryWrapper<BuRepairTaskMaterial> materialWrapper = new LambdaQueryWrapper<BuRepairTaskMaterial>()
                    .in(BuRepairTaskMaterial::getTaskId, taskIdBatchSub);
            buRepairTaskMaterialMapper.delete(materialWrapper);
            LambdaQueryWrapper<BuRepairTaskTool> toolWrapper = new LambdaQueryWrapper<BuRepairTaskTool>()
                    .in(BuRepairTaskTool::getTaskId, taskIdBatchSub);
            buRepairTaskToolMapper.delete(toolWrapper);
            LambdaQueryWrapper<BuRepairTaskStaffRequire> staffRequireWrapper = new LambdaQueryWrapper<BuRepairTaskStaffRequire>()
                    .in(BuRepairTaskStaffRequire::getTaskId, taskIdBatchSub);
            buRepairTaskStaffRequireMapper.delete(staffRequireWrapper);
            LambdaQueryWrapper<BuRepairTaskMustReplace> mustReplaceWrapper = new LambdaQueryWrapper<BuRepairTaskMustReplace>()
                    .in(BuRepairTaskMustReplace::getTaskId, taskIdBatchSub);
            buRepairTaskMustReplaceMapper.delete(mustReplaceWrapper);
            LambdaQueryWrapper<BuRepairPlanTaskPre> taskPreWrapper = new LambdaQueryWrapper<BuRepairPlanTaskPre>()
                    .in(BuRepairPlanTaskPre::getTaskId, taskIdBatchSub);
            buRepairPlanTaskPreMapper.delete(taskPreWrapper);
            LambdaQueryWrapper<BuRepairTaskTrack> taskTrackWrapper = new LambdaQueryWrapper<BuRepairTaskTrack>()
                    .in(BuRepairTaskTrack::getTaskId, taskIdBatchSub);
            buRepairTaskTrackMapper.delete(taskTrackWrapper);
            LambdaQueryWrapper<BuRepairPlanTaskEqu> equipmentWrapper = new LambdaQueryWrapper<BuRepairPlanTaskEqu>()
                    .in(BuRepairPlanTaskEqu::getPlanTaskId, taskIdBatchSub);
            buRepairPlanTaskEquMapper.delete(equipmentWrapper);
        }
    }

    private void checkExchangeUsed(BuRepairPlan plan) {
        String exchangeId = plan.getExchangeId();
        LambdaQueryWrapper<BuRepairPlan> planWrapper = new LambdaQueryWrapper<BuRepairPlan>()
                .eq(BuRepairPlan::getExchangeId, exchangeId)
                .ne(BuRepairPlan::getId, plan.getId());
        Integer count = buRepairPlanMapper.selectCount(planWrapper);
        if (null != count && count > 0) {
            BuRepairExchang exchange = buRepairExchangMapper.selectById(exchangeId);
            throw new JeecgBootException("接车[第" + exchange.getTrainIndex() + "列-" + exchange.getTrainNo() + "]已创建列计划，请先删除对应列计划后再次创建");
        }
    }

    private void setPlanProperties(BuRepairPlan buRepairPlan) throws Exception {
        // 车号
        BuRepairExchang exchange = buRepairExchangMapper.selectById(buRepairPlan.getExchangeId());
        buRepairPlan.setTrainNo(exchange.getTrainNo());

        // 设置车辆的里程数到列计划中
        Double mileage = buRepairPlanMapper.selectTrainMileageByTrainNo(exchange.getTrainNo());
        buRepairPlan.setMileage(mileage);

        // 编码
        if (StrUtil.isEmpty(buRepairPlan.getCode())) {
            Integer maxCode = buRepairPlanMapper.selectMaxCodeOfCurrentPlanData();
            if (Objects.isNull(maxCode)) {
                buRepairPlan.setCode("1");
            } else {
                buRepairPlan.setCode(maxCode + 1 + "");
            }
        }

        // 财务属性
        String planTemplateId = buRepairPlan.getPlanTemplateId();
        if (StringUtils.isNotBlank(planTemplateId)) {
            BuTpRepairPlan tpRepairPlan = buTpRepairPlanTaskDispatchMapper.selectTpPlanByTpPlanId(planTemplateId);
            if (null != tpRepairPlan) {
                if (StringUtils.isBlank(buRepairPlan.getFdProject())) {
                    buRepairPlan.setFdProject(tpRepairPlan.getFdProject());
                }
                if (StringUtils.isBlank(buRepairPlan.getFdTask())) {
                    buRepairPlan.setFdTask(tpRepairPlan.getFdTask());
                }
                if (StringUtils.isBlank(buRepairPlan.getFdCostType())) {
                    buRepairPlan.setFdCostType(tpRepairPlan.getFdCostType());
                }
            }
        }
    }

    private BuRepairPlanVOGantt transformToGantt(BuRepairPlan buRepairPlan) {
        BuRepairPlanVOGantt planGantt = new BuRepairPlanVOGantt();
        BeanUtils.copyProperties(buRepairPlan, planGantt);

        List<BuRepairPlanTask> tasks = buRepairPlan.getTasks();
        List<BuRepairPlanTaskVOGantt> taskGantts = transformToGantt(tasks);

        Date startDate = null;
        Date finishDate = null;
        Optional<BuRepairPlanTaskVOGantt> startOptional = taskGantts.stream()
                .filter(t -> t.getStart() != null)
                .min(Comparator.comparing(BuRepairPlanTaskVOGantt::getStart));
        if (startOptional.isPresent()) {
            startDate = startOptional.get().getStart();
        }
        Optional<BuRepairPlanTaskVOGantt> finishOptional = taskGantts.stream()
                .filter(t -> t.getFinish() != null)
                .max(Comparator.comparing(BuRepairPlanTaskVOGantt::getFinish));
        if (finishOptional.isPresent()) {
            finishDate = finishOptional.get().getFinish();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(finishDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            finishDate = calendar.getTime();
        }

        planGantt.setUID(planGantt.getId());
        planGantt.setName(planGantt.getPlanName());
        planGantt.setStartDate(startDate);
        planGantt.setFinishDate(finishDate);
        planGantt.setTasks(taskGantts);

        setDictText(planGantt);
        return planGantt;
    }

    private void setDictText(BuRepairPlanVOGantt planGantt) {
        if (null == planGantt) {
            return;
        }

        Map<String, Map<String, String>> dictCodeItemMap = dictCacheService.mapAll();
        Map<String, String> planStatusValueTextMap = dictCodeItemMap.get("bu_plan_status");
        if (null != planGantt.getStatus()) {
            planGantt.setStatus_dictText(planStatusValueTextMap.get(String.valueOf(planGantt.getStatus())));
        }
        Map<String, String> planProgressStatusValueTextMap = dictCodeItemMap.get("bu_progress_status");
        if (null != planGantt.getProgressStatus()) {
            planGantt.setProgressStatus_dictText(planProgressStatusValueTextMap.get(String.valueOf(planGantt.getProgressStatus())));
        }
    }

    private List<BuRepairPlanTaskVOGantt> transformToGantt(List<BuRepairPlanTask> tasks) {
        List<BuRepairPlanTaskVOGantt> taskGantts = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(tasks)) {
            for (BuRepairPlanTask task : tasks) {
                taskGantts.add(transformToGantt(task));
            }
        }

        return taskGantts;
    }

    private BuRepairPlanTaskVOGantt transformToGantt(BuRepairPlanTask task) {
        if (null == task) {
            return null;
        }

        if (null != task.getFinishTime()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(task.getFinishTime());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            task.setFinishTime(calendar.getTime());
        }

        BuRepairPlanTaskVOGantt taskGantt = new BuRepairPlanTaskVOGantt();
        BeanUtils.copyProperties(task, taskGantt);
        taskGantt.setTaskId(task.getId());
        taskGantt.setTaskParentId(task.getParentId());
        taskGantt.setTaskDuration(task.getDuration());

        taskGantt.setUID(task.getId());
        taskGantt.setParentId(task.getParentId());
        taskGantt.setID(task.getTaskNo());
        taskGantt.setOutlineNumber(task.getTaskWbs());
        taskGantt.setName(task.getTaskName());
        taskGantt.setCritical(task.getImportant());
        taskGantt.setWork(task.getWorkTime().intValue());
        taskGantt.setDuration(task.getDuration().intValue());
        taskGantt.setStart(task.getStartTime());
        taskGantt.setFinish(task.getFinishTime());
        taskGantt.setActualStart(task.getActStart());
        taskGantt.setActualFinish(task.getActFinish());
        taskGantt.setPercentComplete(task.getProgress());
        taskGantt.setNotes(task.getRemark());
        taskGantt.setOutlineLevel(countPointSplit(task.getTaskWbs()) + 1);
        taskGantt.setDepartment(task.getWorkGroupName());

        taskGantt.setRepairPlanReguInfo(null == taskGantt.getRepairPlanReguInfo() ? new ArrayList<>() : taskGantt.getRepairPlanReguInfo());
        taskGantt.setWorkstations(null == taskGantt.getWorkstations() ? new ArrayList<>() : taskGantt.getWorkstations());
        taskGantt.setMaterials(null == taskGantt.getMaterials() ? new ArrayList<>() : taskGantt.getMaterials());
        taskGantt.setTools(null == taskGantt.getTools() ? new ArrayList<>() : taskGantt.getTools());
        taskGantt.setPersons(null == taskGantt.getPersons() ? new ArrayList<>() : taskGantt.getPersons());
        taskGantt.setForms(null == taskGantt.getForms() ? new ArrayList<>() : taskGantt.getForms());
        taskGantt.setMustReplaces(null == taskGantt.getMustReplaces() ? new ArrayList<>() : taskGantt.getMustReplaces());
        taskGantt.setTaskPres(null == taskGantt.getTaskPres() ? new ArrayList<>() : taskGantt.getTaskPres());

        return taskGantt;
    }

    private int countPointSplit(String var) {
        if (StringUtils.isBlank(var)) {
            return 0;
        }
        String replaceAll = var.replaceAll("\\.", "");
        return var.length() - replaceAll.length();
    }

    private void insertPlanTaskAndRelationByTpPlanId(BuRepairPlan plan,
                                                     Map<String, String> tpPlanFormIdPlanFormIdMap,
                                                     String userId,
                                                     Date now) {
        // 生成列计划任务及关联信息
        List<BuRepairPlanTask> taskList = getPlanTaskListAndRelationByTpPlanId(plan, tpPlanFormIdPlanFormIdMap, userId, now);
        // 保存列计划任务及关联信息
        savePlanTaskListAndRelation(taskList);
        // 根据列计划任务特种设备需求，生成特种设备使用计划
        savePlanSpecAssetUsePlan(taskList);
    }

    private void insertPlanTaskAndRelationByTpPlanId(BuRepairPlan plan,
                                                     Map<String, String> tpPlanFormIdPlanFormIdMap,
                                                     Integer dayIndex,
                                                     String userId,
                                                     Date now) {
        // 生成列计划任务及关联信息
        List<BuRepairPlanTask> taskList = getPlanTaskListAndRelationByTpPlanId(plan, tpPlanFormIdPlanFormIdMap, dayIndex, userId, now);
        // 保存列计划任务及关联信息
        savePlanTaskListAndRelation(taskList);
        // 根据列计划任务特种设备需求，生成特种设备使用计划
        savePlanSpecAssetUsePlan(taskList);
    }

    private Map<String, String> saveFormsByTpPlanId(BuRepairPlan plan, String userId) {
        String tpPlanId = plan.getPlanTemplateId();
        List<BuTpRepairPlanForms> tpRepairPlanFormsList = buTpRepairPlanTaskDispatchMapper.selectTpPlanFormsByTpPlanId(tpPlanId);
        if (CollectionUtils.isEmpty(tpRepairPlanFormsList)) {
            return new HashMap<>();
        }

        String planId = plan.getId();
        List<BuRepairPlanForms> planFormsList = new ArrayList<>();
        Map<String, String> tpPlanFormIdPlanFormIdMap = new HashMap<>();
        for (BuTpRepairPlanForms tpRepairPlanForms : tpRepairPlanFormsList) {
            BuRepairPlanForms planForms = new BuRepairPlanForms()
                    .setId(UUIDGenerator.generate())
                    .setPlanId(planId)
                    .setFormType(tpRepairPlanForms.getFormType())
                    .setObjId(tpRepairPlanForms.getObjId())
                    .setFromBy(1)
                    .setAssetTypeId(tpRepairPlanForms.getAssetTypeId())
                    .setRemark(tpRepairPlanForms.getRemark())
                    .setCreateTime(new Date())
                    .setCreateBy(userId)
                    .setTitle(tpRepairPlanForms.getTitle())
                    .setTrainStructId(tpRepairPlanForms.getTrainStructId())
                    .setWorkRecordType(tpRepairPlanForms.getWorkRecordType());
            planFormsList.add(planForms);
            tpPlanFormIdPlanFormIdMap.put(tpRepairPlanForms.getId(), planForms.getId());
        }
        List<List<BuRepairPlanForms>> batchSubList = DatabaseBatchSubUtil.batchSubList(planFormsList);
        for (List<BuRepairPlanForms> batchSub : batchSubList) {
            buRepairPlanFormsMapper.insertList(batchSub);
        }

        return tpPlanFormIdPlanFormIdMap;
    }

    private Map<String, String> getTpPlanFormIdPlanFormIdMapByPlan(BuRepairPlan plan) {
        if (null == plan) {
            return new HashMap<>();
        }
        String planId = plan.getId();
        String tpPlanId = plan.getPlanTemplateId();
        if (StringUtils.isBlank(tpPlanId)) {
            return new HashMap<>();
        }

        // 列计划表单
        LambdaQueryWrapper<BuRepairPlanForms> planFormWrapper = new LambdaQueryWrapper<BuRepairPlanForms>()
                .eq(BuRepairPlanForms::getPlanId, planId);
        List<BuRepairPlanForms> planFormList = buRepairPlanFormsMapper.selectList(planFormWrapper);
        // 计划模板表单
        List<BuTpRepairPlanForms> tpPlanFormList = buTpRepairPlanTaskDispatchMapper.selectTpPlanFormsByTpPlanId(tpPlanId);

        Map<String, String> tpPlanFormIdPlanFormIdMap = new HashMap<>();
        for (BuTpRepairPlanForms tpPlanForm : tpPlanFormList) {
            Integer formType = tpPlanForm.getFormType();
            String objId = tpPlanForm.getObjId();

            BuRepairPlanForms matchPlanForm;
            List<BuRepairPlanForms> matchPlanFormList = planFormList.stream()
                    .filter(planForm -> formType.equals(planForm.getFormType()) && objId.equals(planForm.getObjId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(matchPlanFormList)) {
                matchPlanForm = matchPlanFormList.get(0);
            } else {
                matchPlanForm = new BuRepairPlanForms()
                        .setId(UUIDGenerator.generate())
                        .setPlanId(planId)
                        .setFormType(tpPlanForm.getFormType())
                        .setObjId(tpPlanForm.getObjId())
                        .setFromBy(1)
                        .setAssetTypeId(tpPlanForm.getAssetTypeId())
                        .setRemark(tpPlanForm.getRemark())
                        .setWorkRecordType(tpPlanForm.getWorkRecordType())
                        .setCreateTime(new Date())
                        .setUpdateTime(new Date())
                        .setWorkRecordType(tpPlanForm.getWorkRecordType());
                buRepairPlanFormsMapper.insert(matchPlanForm);
            }
            tpPlanFormIdPlanFormIdMap.put(tpPlanForm.getId(), matchPlanForm.getId());
        }
        return tpPlanFormIdPlanFormIdMap;
    }

    /**
     * 根据计划模板id，查询计划模板任务及任务关联信息，生成列计划任务及任务关联信息
     *
     * @param plan 列计划信息
     * @return 列计划任务及任务关联信息
     */
    private List<BuRepairPlanTask> getPlanTaskListAndRelationByTpPlanId(BuRepairPlan plan,
                                                                        Map<String, String> tpPlanFormIdPlanFormIdMap,
                                                                        String userId,
                                                                        Date now) {
        String tpPlanId = plan.getPlanTemplateId();
        String planId = plan.getId();
        int planYear = plan.getStartDate().getYear() + 1900;

        List<BuRepairPlanTask> planTaskList = new ArrayList<>();

        // 根据计划模板id获取计划模板任务及任务关联信息
        List<BuTpRepairPlanTask> tpRepairPlanTaskList = buTpRepairPlanTaskDispatchMapper.selectTpPlanTaskByTpPlanId(tpPlanId);

        if (CollectionUtils.isNotEmpty(tpRepairPlanTaskList)) {
            List<String> tpTaskIdList = tpRepairPlanTaskList.stream()
                    .map(BuTpRepairPlanTask::getId)
                    .collect(Collectors.toList());

            // 根据系统中节假日信息获取特殊日期
            SpecialHoliday specialHoliday = holidayService.getSpecialHoliday(planYear);

            // 获取列计划跳过节假日的实际开始时间
            Calendar planActStartCalendar = Calendar.getInstance();
            planActStartCalendar.setTime(plan.getStartDate());
            planActStartCalendar.add(Calendar.DATE, -1);
            planActStartCalendar = holidayService.getNextWorkDayCalendar(planActStartCalendar, specialHoliday);
            Date planActStartDate = planActStartCalendar.getTime();
            plan.setStartDate(planActStartDate);

            // 计划模板任务列表转map
            int mapSize = MapSizeUtil.tableSizeFor(tpRepairPlanTaskList.size());
            Map<String, BuTpRepairPlanTask> tpTaskIdTpTaskMap = new HashMap<>(mapSize);
            tpRepairPlanTaskList.forEach(tpTask -> tpTaskIdTpTaskMap.put(tpTask.getId(), tpTask));

            // 计划模板关联信息
            List<BuTpRepairPlanReguInfo> repairPlanReguInfo = buTpRepairPlanTaskDispatchMapper.selectTaskRepairPlanReguInfoByTpPlanId(tpPlanId);
            List<BuTpRepairPlanBookStep> bookSteps = buTpRepairPlanTaskDispatchMapper.selectTaskBookStepByTpPlanId(tpPlanId);
            List<BuTpRepairPlanSpeEq> specAssets = buTpRepairPlanTaskDispatchMapper.selectTaskSpecAssetByTpPlanId(tpPlanId);
            List<BuTpRepairPlanWorkstation> workstations = buTpRepairPlanTaskDispatchMapper.selectTaskWorkstationsByTpPlanId(tpPlanId);
            List<BuTpRepairPlanMaterial> materials = buTpRepairPlanTaskDispatchMapper.selectTaskMaterialsByTpPlanId(tpPlanId);
            List<BuTpRepairPlanTool> tools = buTpRepairPlanTaskDispatchMapper.selectTaskToolsByTpPlanId(tpPlanId);
            List<BuTpRepairPlanPerson> persons = buTpRepairPlanTaskDispatchMapper.selectTaskPersonsByTpPlanId(tpPlanId);
            List<BuTpRepairPlanMustReplace> mustReplaces = buTpRepairPlanTaskDispatchMapper.selectTaskMustReplacesByTpPlanId(tpPlanId);
            if (CollectionUtils.isNotEmpty(mustReplaces)) {
                mustReplaces.forEach(item -> item.setPlanId(planId));
            }
            List<BuTpRepairPlanTaskPre> taskPres = buTpRepairPlanTaskDispatchMapper.selectTaskTaskPresByTpPlanId(tpPlanId);
            List<BuTpRepairPlanTaskEqu> equipments = buTpRepairPlanTaskDispatchMapper.selectTaskEquipmentByTpPlanId(tpPlanId);
            List<BuTpRepairPlanTaskForms> forms = buTpRepairPlanTaskDispatchMapper.selectTaskFormByTpPlanId(tpPlanId);

            // 关联信息映射到计划模板任务
            Map<String, List<BuTpRepairPlanReguInfo>> repairPlanReguInfoMap = repairPlanReguInfo.stream().
                    collect(Collectors.groupingBy(BuTpRepairPlanReguInfo::getTaskId));
            Map<String, List<BuTpRepairPlanBookStep>> bookStepsMap = bookSteps.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanBookStep::getTaskId));
            Map<String, List<BuTpRepairPlanSpeEq>> specAssetsMap = specAssets.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanSpeEq::getTpTaskId));
            Map<String, List<BuTpRepairPlanWorkstation>> workstationsMap = workstations.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanWorkstation::getTaskId));
            Map<String, List<BuTpRepairPlanMaterial>> materialsMap = materials.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanMaterial::getTaskId));
            Map<String, List<BuTpRepairPlanTool>> toolsMap = tools.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanTool::getTaskId));
            Map<String, List<BuTpRepairPlanPerson>> personsMap = persons.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanPerson::getTaskId));
            Map<String, List<BuTpRepairPlanMustReplace>> mustReplacesMap = mustReplaces.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanMustReplace::getTaskId));
            Map<String, List<BuTpRepairPlanTaskPre>> taskPresMap = taskPres.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanTaskPre::getTaskId));
            Map<String, List<BuTpRepairPlanTaskEqu>> equipmentsMap = equipments.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanTaskEqu::getPlanTaskId));
            Map<String, List<BuTpRepairPlanTaskForms>> formsMap = forms.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanTaskForms::getTaskId));

            // 提前映射计划模板id和列计划任务id，用于父子关系的复制
            Map<String, String> tpTaskIdPlanTaskIdMap = new HashMap<>();
            for (String tpTaskId : tpTaskIdList) {
                tpTaskIdPlanTaskIdMap.put(tpTaskId, UUIDGenerator.generate());
            }

            // 计划任务顶级节点是按天分组的，所有顶级节点的工期为子节点的工期，子节点的开始时间和结束时间继承顶级节点就行
            List<BuTpRepairPlanTask> topTpTaskList = tpRepairPlanTaskList.stream()
                    .filter(tpTask -> StringUtils.isBlank(tpTask.getParentId()))
                    .sorted(Comparator.comparing(BuTpRepairPlanTask::getTaskNo))
                    .collect(Collectors.toList());
            Map<String, Date> topPlanTaskIdStartTimeMap = new HashMap<>();
            Map<String, Date> topPlanTaskIdFinishTimeMap = new HashMap<>();
            setTopPlanTaskStartAndFinishTime(plan.getStartDate(), topTpTaskList, specialHoliday, tpTaskIdPlanTaskIdMap, topPlanTaskIdStartTimeMap, topPlanTaskIdFinishTimeMap);

            for (BuTpRepairPlanTask tpTask : tpRepairPlanTaskList) {
                // 获取当前计划模板任务的关联关系
                tpTask.setRepairPlanReguInfo(repairPlanReguInfoMap.get(tpTask.getId()))
                        .setBookSteps(bookStepsMap.get(tpTask.getId()))
                        .setSpecAssets(specAssetsMap.get(tpTask.getId()))
                        .setWorkstations(workstationsMap.get(tpTask.getId()))
                        .setMaterials(materialsMap.get(tpTask.getId()))
                        .setTools(toolsMap.get(tpTask.getId()))
                        .setPersons(personsMap.get(tpTask.getId()))
                        .setMustReplaces(mustReplacesMap.get(tpTask.getId()))
                        .setTaskPres(taskPresMap.get(tpTask.getId()))
                        .setEquipments(equipmentsMap.get(tpTask.getId()))
                        .setForms(formsMap.get(tpTask.getId()));

                // 获取列计划任务id和上级id
                String taskId = tpTaskIdPlanTaskIdMap.get(tpTask.getId());
                String parentId = tpTaskIdPlanTaskIdMap.get(tpTask.getParentId());
                String topTpTaskId = getTopTpTaskId(tpTask, tpTaskIdTpTaskMap);
                String topPlanTaskId = tpTaskIdPlanTaskIdMap.get(topTpTaskId);

                // 生成列计划任务及关联信息
                BuRepairPlanTask task = generateTaskAndRelation(planId, tpTask, tpPlanFormIdPlanFormIdMap, taskId, parentId, userId, now);
                // 设置列计划任务开始和结束时间
                task.setStartTime(topPlanTaskIdStartTimeMap.get(topPlanTaskId));
                task.setFinishTime(topPlanTaskIdFinishTimeMap.get(topPlanTaskId));

                planTaskList.add(task);
            }
        }

        return planTaskList;
    }

    private List<BuRepairPlanTask> getPlanTaskListAndRelationByTpPlanId(BuRepairPlan plan,
                                                                        Map<String, String> tpPlanFormIdPlanFormIdMap,
                                                                        Integer dayIndex,
                                                                        String userId,
                                                                        Date now) {
        String tpPlanId = plan.getPlanTemplateId();
        String planId = plan.getId();
        int planYear = plan.getStartDate().getYear() + 1900;

        List<BuRepairPlanTask> planTaskList = new ArrayList<>();

        // 根据计划模板id获取计划模板任务及任务关联信息
        List<BuTpRepairPlanTask> tpRepairPlanTaskList = buTpRepairPlanTaskDispatchMapper.selectTpPlanTaskByTpPlanId(tpPlanId);

        if (CollectionUtils.isNotEmpty(tpRepairPlanTaskList)) {
            List<String> tpTaskIdList = tpRepairPlanTaskList.stream()
                    .map(BuTpRepairPlanTask::getId)
                    .collect(Collectors.toList());

            // 根据系统中节假日信息获取特殊日期
            SpecialHoliday specialHoliday = holidayService.getSpecialHoliday(planYear);

            // 获取列计划跳过节假日的实际开始时间
            Calendar planActStartCalendar = Calendar.getInstance();
            planActStartCalendar.setTime(plan.getStartDate());
            planActStartCalendar.add(Calendar.DATE, -1);
            planActStartCalendar = holidayService.getNextWorkDayCalendar(planActStartCalendar, specialHoliday);
            Date planActStartDate = planActStartCalendar.getTime();
            plan.setStartDate(planActStartDate);

            // 计划模板任务列表转map
            int mapSize = MapSizeUtil.tableSizeFor(tpRepairPlanTaskList.size());
            Map<String, BuTpRepairPlanTask> tpTaskIdTpTaskMap = new HashMap<>(mapSize);
            tpRepairPlanTaskList.forEach(tpTask -> tpTaskIdTpTaskMap.put(tpTask.getId(), tpTask));

            // 计划模板关联信息
            List<BuTpRepairPlanReguInfo> repairPlanReguInfo = buTpRepairPlanTaskDispatchMapper.selectTaskRepairPlanReguInfoByTpPlanId(tpPlanId);
            List<BuTpRepairPlanBookStep> bookSteps = buTpRepairPlanTaskDispatchMapper.selectTaskBookStepByTpPlanId(tpPlanId);
            List<BuTpRepairPlanSpeEq> specAssets = buTpRepairPlanTaskDispatchMapper.selectTaskSpecAssetByTpPlanId(tpPlanId);
            List<BuTpRepairPlanWorkstation> workstations = buTpRepairPlanTaskDispatchMapper.selectTaskWorkstationsByTpPlanId(tpPlanId);
            List<BuTpRepairPlanMaterial> materials = buTpRepairPlanTaskDispatchMapper.selectTaskMaterialsByTpPlanId(tpPlanId);
            List<BuTpRepairPlanTool> tools = buTpRepairPlanTaskDispatchMapper.selectTaskToolsByTpPlanId(tpPlanId);
            List<BuTpRepairPlanPerson> persons = buTpRepairPlanTaskDispatchMapper.selectTaskPersonsByTpPlanId(tpPlanId);
            List<BuTpRepairPlanMustReplace> mustReplaces = buTpRepairPlanTaskDispatchMapper.selectTaskMustReplacesByTpPlanId(tpPlanId);
            if (CollectionUtils.isNotEmpty(mustReplaces)) {
                mustReplaces.forEach(item -> item.setPlanId(planId));
            }
            List<BuTpRepairPlanTaskPre> taskPres = buTpRepairPlanTaskDispatchMapper.selectTaskTaskPresByTpPlanId(tpPlanId);
            List<BuTpRepairPlanTaskEqu> equipments = buTpRepairPlanTaskDispatchMapper.selectTaskEquipmentByTpPlanId(tpPlanId);
            List<BuTpRepairPlanTaskForms> forms = buTpRepairPlanTaskDispatchMapper.selectTaskFormByTpPlanId(tpPlanId);

            // 关联信息映射到计划模板任务
            Map<String, List<BuTpRepairPlanReguInfo>> repairPlanReguInfoMap = repairPlanReguInfo.stream().
                    collect(Collectors.groupingBy(BuTpRepairPlanReguInfo::getTaskId));
            Map<String, List<BuTpRepairPlanBookStep>> bookStepsMap = bookSteps.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanBookStep::getTaskId));
            Map<String, List<BuTpRepairPlanSpeEq>> specAssetsMap = specAssets.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanSpeEq::getTpTaskId));
            Map<String, List<BuTpRepairPlanWorkstation>> workstationsMap = workstations.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanWorkstation::getTaskId));
            Map<String, List<BuTpRepairPlanMaterial>> materialsMap = materials.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanMaterial::getTaskId));
            Map<String, List<BuTpRepairPlanTool>> toolsMap = tools.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanTool::getTaskId));
            Map<String, List<BuTpRepairPlanPerson>> personsMap = persons.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanPerson::getTaskId));
            Map<String, List<BuTpRepairPlanMustReplace>> mustReplacesMap = mustReplaces.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanMustReplace::getTaskId));
            Map<String, List<BuTpRepairPlanTaskPre>> taskPresMap = taskPres.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanTaskPre::getTaskId));
            Map<String, List<BuTpRepairPlanTaskEqu>> equipmentsMap = equipments.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanTaskEqu::getPlanTaskId));
            Map<String, List<BuTpRepairPlanTaskForms>> formsMap = forms.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanTaskForms::getTaskId));

            // 提前映射计划模板id和列计划任务id，用于父子关系的复制
            Map<String, String> tpTaskIdPlanTaskIdMap = new HashMap<>();
            for (String tpTaskId : tpTaskIdList) {
                tpTaskIdPlanTaskIdMap.put(tpTaskId, UUIDGenerator.generate());
            }

            // 计划任务顶级节点是按天分组的，所有顶级节点的工期为子节点的工期，子节点的开始时间和结束时间继承顶级节点就行
            List<BuTpRepairPlanTask> topTpTaskList = tpRepairPlanTaskList.stream()
                    .filter(tpTask -> StringUtils.isBlank(tpTask.getParentId()))
                    .sorted(Comparator.comparing(BuTpRepairPlanTask::getTaskNo))
                    .collect(Collectors.toList());
            Map<String, Date> topPlanTaskIdStartTimeMap = new HashMap<>();
            Map<String, Date> topPlanTaskIdFinishTimeMap = new HashMap<>();

            setTopPlanTaskStartAndFinishTime(plan.getStartDate(), topTpTaskList, specialHoliday, tpTaskIdPlanTaskIdMap, topPlanTaskIdStartTimeMap, topPlanTaskIdFinishTimeMap);

            for (BuTpRepairPlanTask tpTask : tpRepairPlanTaskList) {
                // 获取当前计划模板任务的关联关系
                tpTask.setRepairPlanReguInfo(repairPlanReguInfoMap.get(tpTask.getId()))
                        .setBookSteps(bookStepsMap.get(tpTask.getId()))
                        .setSpecAssets(specAssetsMap.get(tpTask.getId()))
                        .setWorkstations(workstationsMap.get(tpTask.getId()))
                        .setMaterials(materialsMap.get(tpTask.getId()))
                        .setTools(toolsMap.get(tpTask.getId()))
                        .setPersons(personsMap.get(tpTask.getId()))
                        .setMustReplaces(mustReplacesMap.get(tpTask.getId()))
                        .setTaskPres(taskPresMap.get(tpTask.getId()))
                        .setEquipments(equipmentsMap.get(tpTask.getId()))
                        .setForms(formsMap.get(tpTask.getId()));

                // 获取列计划任务id和上级id
                String taskId = tpTaskIdPlanTaskIdMap.get(tpTask.getId());
                String parentId = tpTaskIdPlanTaskIdMap.get(tpTask.getParentId());
                String topTpTaskId = getTopTpTaskId(tpTask, tpTaskIdTpTaskMap); // 找到该人的顶级任务id, 为了取顶级任务的开始和结束日期，如果子任务的工期不是1天呢？？
                String topPlanTaskId = tpTaskIdPlanTaskIdMap.get(topTpTaskId);

                // 生成列计划任务及关联信息
                BuRepairPlanTask task = generateTaskAndRelation(planId, tpTask, tpPlanFormIdPlanFormIdMap, taskId, parentId, userId, now);
                // 设置列计划任务开始和结束时间 TODO:  这里要单独计算，根据顶级任务的开始日期计算
                task.setStartTime(topPlanTaskIdStartTimeMap.get(topPlanTaskId));
                task.setFinishTime(topPlanTaskIdFinishTimeMap.get(topPlanTaskId));

                planTaskList.add(task);
            }
        }
        // 这个日期前的任务都不要保存
        planTaskList.removeIf(t -> t.getDayIndex() < dayIndex);

        return planTaskList;
    }

    private void setTopPlanTaskStartAndFinishTime(Date startDate,
                                                  List<BuTpRepairPlanTask> topTpTaskList,
                                                  SpecialHoliday specialHoliday,
                                                  Map<String, String> tpTaskIdPlanTaskIdMap,
                                                  Map<String, Date> topPlanTaskIdStartTimeMap,
                                                  Map<String, Date> topPlanTaskIdFinishTimeMap) {
        if (CollectionUtils.isEmpty(topTpTaskList)) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        for (BuTpRepairPlanTask topTpTask : topTpTaskList) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            String topPlanTaskId = tpTaskIdPlanTaskIdMap.get(topTpTask.getId());
            // 计划模板没有任务开始和结束时间，列计划需要有(根据工期计算，去除节假日)
            topPlanTaskIdStartTimeMap.put(topPlanTaskId, calendar.getTime());
            int workDays = 1;
            while (workDays < topTpTask.getDuration()) {
                calendar = holidayService.getNextWorkDayCalendar(calendar, specialHoliday);
                workDays++;
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.SECOND, -1);
            topPlanTaskIdFinishTimeMap.put(topPlanTaskId, calendar.getTime());
            calendar = holidayService.getNextWorkDayCalendar(calendar, specialHoliday);
        }
    }

    private String getTopTpTaskId(BuTpRepairPlanTask tpTask, Map<String, BuTpRepairPlanTask> tpTaskIdTpTaskMap) {
        String parentId = tpTask.getParentId();
        if (StringUtils.isBlank(parentId)) {
            return tpTask.getId();
        } else {
            BuTpRepairPlanTask parent = tpTaskIdTpTaskMap.get(parentId);
            if (null == parent) {
                return tpTask.getId();
            }
            return getTopTpTaskId(parent, tpTaskIdTpTaskMap);
        }
    }

    private BuRepairPlanTask generateTaskAndRelation(String planId,
                                                     BuTpRepairPlanTask tpTask,
                                                     Map<String, String> tpPlanFormIdPlanFormIdMap,
                                                     String taskId,
                                                     String parentId,
                                                     String userId,
                                                     Date now) {
        return new BuRepairPlanTask()
                .setId(taskId)
                .setPlanId(planId)
                .setParentId(parentId)
                .setTaskNo(tpTask.getTaskNo())
                .setTaskWbs(tpTask.getTaskWbs())
                .setTaskName(tpTask.getTaskName())
                .setImportant(null == tpTask.getImportant() ? 0 : tpTask.getImportant())
                .setOutsource(null == tpTask.getOutsource() ? 0 : tpTask.getOutsource())
                .setMethod(null == tpTask.getMethod() ? 1 : tpTask.getMethod())
                .setSystemId(tpTask.getSystemId())
                .setAssetTypeId(tpTask.getAssetTypeId())
                .setWorkTime(null == tpTask.getWorkTime() ? 0 : tpTask.getWorkTime())
                .setDuration(tpTask.getDuration())
                .setProgress(0D)
                .setDayIndex(tpTask.getDayIndex())
                .setWorkGroupId(tpTask.getWorkGroupId())
                .setStatus(1)
                .setRemark(tpTask.getRemark())
                .setGenOrder(null == tpTask.getGenOrder() ? 0 : tpTask.getGenOrder())
                .setHasGen(0)
                .setCreateTime(now)
                .setCreateBy(userId)
                .setUpdateTime(now)
                .setUpdateBy(userId)
                .setSuspendStatus(null)
                .setSuspendTime(null)
                .setActiveTime(null)
                .setSafeNotice(tpTask.getSafeNotice())
                .setRepairPlanReguInfo(transformTpReguWithId(tpTask.getRepairPlanReguInfo(), taskId))
                .setBookSteps(transformTpBookStepWithId(tpTask.getBookSteps(), taskId))
                .setSpecAssets(transformTpSpecAssetWithId(tpTask.getSpecAssets(), taskId))
                .setWorkstations(transformTpWorkstationWithId(tpTask.getWorkstations(), taskId))
                .setMaterials(transformTpMaterialWithId(tpTask.getMaterials(), taskId))
                .setTools(transformTpToolWithId(tpTask.getTools(), taskId))
                .setPersons(transformTpPersonWithId(tpTask.getPersons(), taskId))
                .setMustReplaces(transformTpMustReplaceWithId(tpTask.getMustReplaces(), taskId))
                .setTaskPres(transformTpTaskPreWithId(tpTask.getTaskPres(), taskId))
                .setEquipments(transformTpTaskEquipmentWithId(tpTask.getEquipments(), taskId, planId))
                .setForms(transformTpTaskFormWithId(tpTask.getForms(), tpPlanFormIdPlanFormIdMap, taskId, planId));
    }


    private List<BuRepairTaskRegu> transformTpReguWithId(List<BuTpRepairPlanReguInfo> repairPlanReguInfo, String taskId) {
        List<BuRepairTaskRegu> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(repairPlanReguInfo)) {
            for (BuTpRepairPlanReguInfo tpRegu : repairPlanReguInfo) {
                result.add(
                        new BuRepairTaskRegu()
                                .setId(UUIDGenerator.generate())
                                .setTaskId(taskId)
                                .setReguId(tpRegu.getReguId())
                                .setReguDetailId(tpRegu.getReguDetailId())
                                .setRemark(tpRegu.getRemark())
                );
            }
        }

        return result;
    }

    private List<BuRepairTaskBookStep> transformTpBookStepWithId(List<BuTpRepairPlanBookStep> bookSteps, String taskId) {
        List<BuRepairTaskBookStep> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(bookSteps)) {
            for (BuTpRepairPlanBookStep tpBookStep : bookSteps) {
                result.add(
                        new BuRepairTaskBookStep()
                                .setId(UUIDGenerator.generate())
                                .setTaskId(taskId)
                                .setTechBookId(tpBookStep.getTechBookId())
                                .setBookDetailId(tpBookStep.getBookDetailId())
                                .setBookStepNo(tpBookStep.getBookStepNo())
                                .setBootStepTitle(tpBookStep.getBootStepTitle())
                );
            }
        }

        return result;
    }

    private List<BuRepairPlanSpeEq> transformTpSpecAssetWithId(List<BuTpRepairPlanSpeEq> specAssets, String taskId) {
        List<BuRepairPlanSpeEq> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(specAssets)) {
            for (BuTpRepairPlanSpeEq tpSpecAsset : specAssets) {
                result.add(
                        new BuRepairPlanSpeEq()
                                .setId(UUIDGenerator.generate())
                                .setTaskId(tpSpecAsset.getTpTaskId())
                                .setSpecAssetId(tpSpecAsset.getSpecAssetId())
                                .setAssetName(tpSpecAsset.getAssetName())
                                .setAssetCode(tpSpecAsset.getAssetCode())
                                .setStartTime(tpSpecAsset.getStartTime())
                                .setEndTime(tpSpecAsset.getEndTime())
                                .setTimeLen(tpSpecAsset.getTimeLen())
                                .setRemark(tpSpecAsset.getRemark())
                                .setCreateTime(tpSpecAsset.getCreateTime())
                                .setCreateBy(tpSpecAsset.getCreateBy())
                                .setUpdateTime(tpSpecAsset.getUpdateTime())
                                .setUpdateBy(tpSpecAsset.getUpdateBy())
                );
            }
        }

        return result;
    }

    private List<BuRepairTaskWorkstation> transformTpWorkstationWithId(List<BuTpRepairPlanWorkstation> workstations, String taskId) {
        List<BuRepairTaskWorkstation> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(workstations)) {
            for (BuTpRepairPlanWorkstation tpWorkstation : workstations) {
                result.add(
                        new BuRepairTaskWorkstation()
                                .setId(UUIDGenerator.generate())
                                .setTaskId(taskId)
                                .setWorkstationId(tpWorkstation.getWorkstationId())
                );
            }
        }

        return result;
    }

    private List<BuRepairTaskMaterial> transformTpMaterialWithId(List<BuTpRepairPlanMaterial> materials, String taskId) {
        List<BuRepairTaskMaterial> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(materials)) {
            for (BuTpRepairPlanMaterial tpMaterial : materials) {
                result.add(
                        new BuRepairTaskMaterial()
                                .setId(UUIDGenerator.generate())
                                .setTaskId(taskId)
                                .setMaterialTypeId(tpMaterial.getMaterialTypeId())
                                .setAmount(tpMaterial.getAmount())
                                .setRemark(tpMaterial.getRemark())
                                .setUseCategory(null == tpMaterial.getUseCategory() ? -1 : tpMaterial.getUseCategory())
                );
            }
        }

        return result;
    }

    private List<BuRepairTaskTool> transformTpToolWithId(List<BuTpRepairPlanTool> tools, String taskId) {
        List<BuRepairTaskTool> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(tools)) {
            for (BuTpRepairPlanTool tpTool : tools) {
                result.add(
                        new BuRepairTaskTool()
                                .setId(UUIDGenerator.generate())
                                .setTaskId(taskId)
                                .setToolTypeId(tpTool.getToolTypeId())
                                .setAmount(tpTool.getAmount())
                                .setRemark(tpTool.getRemark())
                );
            }
        }

        return result;
    }

    private List<BuRepairTaskStaffRequire> transformTpPersonWithId(List<BuTpRepairPlanPerson> persons, String taskId) {
        List<BuRepairTaskStaffRequire> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(persons)) {
            for (BuTpRepairPlanPerson tpPerson : persons) {
                result.add(
                        new BuRepairTaskStaffRequire()
                                .setId(UUIDGenerator.generate())
                                .setTaskId(taskId)
                                .setAmount(tpPerson.getAmount())
                                .setRequirePostion(tpPerson.getRequirePostion())
                                .setRequireCertificate(tpPerson.getRequireCertificate())
                                .setRequireTech(tpPerson.getRequireTech())
                                .setRemark(tpPerson.getRemark())
                );
            }
        }

        return result;
    }

    private List<BuRepairTaskMustReplace> transformTpMustReplaceWithId(List<BuTpRepairPlanMustReplace> mustReplaces, String taskId) {
        List<BuRepairTaskMustReplace> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(mustReplaces)) {
            for (BuTpRepairPlanMustReplace tpMustReplace : mustReplaces) {
                result.add(
                        new BuRepairTaskMustReplace()
                                .setId(UUIDGenerator.generate())
                                .setTaskId(taskId)
                                .setAssetTypeId(tpMustReplace.getAssetTypeId())
                                .setChanged(0)
                                .setChangeId(null)
                                .setRemark(tpMustReplace.getRemark())
                                .setMustReplaceId(tpMustReplace.getMustReplaceId())
                                .setPlanId(tpMustReplace.getPlanId())
                                .setMaterialTypeId(tpMustReplace.getMaterialTypeId())
                                .setAmount(null == tpMustReplace.getAmount() ? 0 : tpMustReplace.getAmount())
                );
            }
        }

        return result;
    }

    private List<BuRepairPlanTaskPre> transformTpTaskPreWithId(List<BuTpRepairPlanTaskPre> taskPres, String taskId) {
        List<BuRepairPlanTaskPre> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(taskPres)) {
            for (BuTpRepairPlanTaskPre tpTaskPre : taskPres) {
                result.add(
                        new BuRepairPlanTaskPre()
                                .setId(UUIDGenerator.generate())
                                .setTaskId(taskId)
                                .setTaskNo(tpTaskPre.getTaskNo())
                                .setTaskName(tpTaskPre.getTaskName())
                                .setPreType(tpTaskPre.getPreType())
                                .setDelay(tpTaskPre.getDelay())
                );
            }
        }

        return result;
    }

    private List<BuRepairPlanTaskEqu> transformTpTaskEquipmentWithId(List<BuTpRepairPlanTaskEqu> equipments, String taskId, String planId) {
        List<BuRepairPlanTaskEqu> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(equipments)) {
            for (BuTpRepairPlanTaskEqu equipment : equipments) {
                result.add(
                        new BuRepairPlanTaskEqu()
                                .setId(UUIDGenerator.generate())
                                .setPlanId(planId)
                                .setPlanTaskId(taskId)
                                .setSystemId(equipment.getSystemId())
                                .setAssetTypeId(equipment.getAssetTypeId())
                                .setStructDetailId(equipment.getStructDetailId())
                );
            }
        }

        return result;
    }

    private List<BuRepairPlanTaskForms> transformTpTaskFormWithId(List<BuTpRepairPlanTaskForms> forms, Map<String, String> tpPlanFormIdPlanFormIdMap, String taskId, String planId) {
        List<BuRepairPlanTaskForms> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(forms)) {
            for (BuTpRepairPlanTaskForms form : forms) {
                String tpPlanFormId = form.getPlanFormId();
                String planFormId = tpPlanFormIdPlanFormIdMap.get(tpPlanFormId);
                result.add(
                        new BuRepairPlanTaskForms()
                                .setId(UUIDGenerator.generate())
                                .setPlanId(planId)
                                .setTaskId(taskId)
                                .setPlanFormId(planFormId)
                );
            }
        }

        return result;
    }

    /**
     * 保存列计划任务及任务关联信息
     *
     * @param taskList 列计划列表（含关联信息）
     */
    private void savePlanTaskListAndRelation(List<BuRepairPlanTask> taskList) {
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }

        // 先保存任务
        List<List<BuRepairPlanTask>> taskBatchSubList = DatabaseBatchSubUtil.batchSubList(taskList);
        for (List<BuRepairPlanTask> taskBatchSub : taskBatchSubList) {
            buRepairPlanTaskMapper.insertList(taskBatchSub);
        }
        // 关联信息
        List<BuRepairTaskRegu> repairPlanReguInfo = new ArrayList<>();
        List<BuRepairTaskBookStep> bookSteps = new ArrayList<>();
        List<BuRepairPlanSpeEq> specAssets = new ArrayList<>();
        List<BuRepairTaskWorkstation> workstations = new ArrayList<>();
        List<BuRepairTaskMaterial> materials = new ArrayList<>();
        List<BuRepairTaskTool> tools = new ArrayList<>();
        List<BuRepairTaskStaffRequire> persons = new ArrayList<>();
        List<BuRepairTaskMustReplace> mustReplaces = new ArrayList<>();
        List<BuRepairPlanTaskPre> taskPres = new ArrayList<>();
        List<BuRepairPlanTaskEqu> equipments = new ArrayList<>();
        List<BuRepairPlanTaskForms> forms = new ArrayList<>();
        for (BuRepairPlanTask task : taskList) {
            repairPlanReguInfo.addAll(task.getRepairPlanReguInfo());
            bookSteps.addAll(task.getBookSteps());
            specAssets.addAll(task.getSpecAssets());
            workstations.addAll(task.getWorkstations());
            materials.addAll(task.getMaterials());
            tools.addAll(task.getTools());
            persons.addAll(task.getPersons());
            mustReplaces.addAll(task.getMustReplaces());
            taskPres.addAll(task.getTaskPres());
            equipments.addAll(task.getEquipments());
            forms.addAll(task.getForms());
        }

        // 保存关联信息
        if (CollectionUtils.isNotEmpty(repairPlanReguInfo)) {
            List<List<BuRepairTaskRegu>> batchSubList = DatabaseBatchSubUtil.batchSubList(repairPlanReguInfo);
            for (List<BuRepairTaskRegu> batchSub : batchSubList) {
                buRepairTaskReguMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(bookSteps)) {
            List<List<BuRepairTaskBookStep>> batchSubList = DatabaseBatchSubUtil.batchSubList(bookSteps);
            for (List<BuRepairTaskBookStep> batchSub : batchSubList) {
                buRepairTaskBookStepMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(specAssets)) {
            List<List<BuRepairPlanSpeEq>> batchSubList = DatabaseBatchSubUtil.batchSubList(specAssets);
            for (List<BuRepairPlanSpeEq> batchSub : batchSubList) {
                buRepairPlanSpeEqMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(workstations)) {
            List<List<BuRepairTaskWorkstation>> batchSubList = DatabaseBatchSubUtil.batchSubList(workstations);
            for (List<BuRepairTaskWorkstation> batchSub : batchSubList) {
                buRepairTaskWorkstationMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(materials)) {
            List<List<BuRepairTaskMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(materials);
            for (List<BuRepairTaskMaterial> batchSub : batchSubList) {
                buRepairTaskMaterialMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(tools)) {
            List<List<BuRepairTaskTool>> batchSubList = DatabaseBatchSubUtil.batchSubList(tools);
            for (List<BuRepairTaskTool> batchSub : batchSubList) {
                buRepairTaskToolMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(persons)) {
            List<List<BuRepairTaskStaffRequire>> batchSubList = DatabaseBatchSubUtil.batchSubList(persons);
            for (List<BuRepairTaskStaffRequire> batchSub : batchSubList) {
                buRepairTaskStaffRequireMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(mustReplaces)) {
            List<List<BuRepairTaskMustReplace>> batchSubList = DatabaseBatchSubUtil.batchSubList(mustReplaces);
            for (List<BuRepairTaskMustReplace> batchSub : batchSubList) {
                buRepairTaskMustReplaceMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(taskPres)) {
            List<List<BuRepairPlanTaskPre>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskPres);
            for (List<BuRepairPlanTaskPre> batchSub : batchSubList) {
                buRepairPlanTaskPreMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(equipments)) {
            // 系统id、设备类型id为空时，应根据车辆结构id设置对应的系统id、设备类型id
            setTaskEquSystemAssetTypeIdIfNull(equipments);

            List<List<BuRepairPlanTaskEqu>> batchSubList = DatabaseBatchSubUtil.batchSubList(equipments);
            for (List<BuRepairPlanTaskEqu> batchSub : batchSubList) {
                buRepairPlanTaskEquMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(forms)) {
            List<List<BuRepairPlanTaskForms>> batchSubList = DatabaseBatchSubUtil.batchSubList(forms);
            for (List<BuRepairPlanTaskForms> batchSub : batchSubList) {
                buRepairPlanTaskFormsMapper.insertList(batchSub);
            }
        }
    }

    private void setTaskEquSystemAssetTypeIdIfNull(List<BuRepairPlanTaskEqu> equipments) {
        if (CollectionUtils.isEmpty(equipments)) {
            return;
        }

        // 过滤出系统id或设备类型id为空的
        List<BuRepairPlanTaskEqu> needUpdateList = equipments.stream()
                .filter(equipment -> StringUtils.isBlank(equipment.getSystemId()) || StringUtils.isBlank(equipment.getAssetTypeId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needUpdateList)) {
            // 查询车辆结构
            List<String> structDetailIdList = needUpdateList.stream()
                    .map(BuRepairPlanTaskEqu::getStructDetailId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<String, BuTrainStructureDetail> idStructureDetailMap = new HashMap<>();
            List<List<String>> structDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(structDetailIdList);
            for (List<String> structDetailIdBatchSub : structDetailIdBatchSubList) {
                List<BuTrainStructureDetail> subStructureDetailList = buTrainStructureDetailDispatchMapper.selectBatchIds(structDetailIdBatchSub);
                subStructureDetailList.forEach(structDetail -> idStructureDetailMap.put(structDetail.getId(), structDetail));
            }
            // 查询设备类型缓存
            Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = assetTypeCacheService.map(null);

            // 设置
            for (BuRepairPlanTaskEqu equipment : needUpdateList) {
                String structDetailId = equipment.getStructDetailId();
                // 设备类型id
                if (StringUtils.isBlank(equipment.getAssetTypeId()) && StringUtils.isNotBlank(structDetailId)) {
                    BuTrainStructureDetail structureDetail = idStructureDetailMap.get(structDetailId);
                    if (null != structureDetail) {
                        equipment.setAssetTypeId(structureDetail.getAssetTypeId());
                    }
                }
                String assetTypeId = equipment.getAssetTypeId();
                // 系统id
                if (StringUtils.isBlank(equipment.getSystemId()) && StringUtils.isNotBlank(assetTypeId)) {
                    BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOMap.get(assetTypeId);
                    if (null != assetTypeBO) {
                        equipment.setSystemId(assetTypeBO.getSysId());
                    }
                }
            }
        }
    }

    private void savePlanSpecAssetUsePlan(List<BuRepairPlanTask> taskList) {
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }

        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        List<BuSpecAssetPlan> specAssetUsePlanList = new ArrayList<>();
        for (BuRepairPlanTask task : taskList) {
            List<BuRepairPlanSpeEq> specAssets = task.getSpecAssets();
            if (CollectionUtils.isEmpty(specAssets)) {
                continue;
            }

            Date startTime = task.getStartTime();
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startTime);
            Date finishTime = task.getFinishTime();
            Calendar finishCalendar = Calendar.getInstance();
            finishCalendar.setTime(finishTime);
            while (startCalendar.before(finishCalendar)) {
                // 转换出使用计划
                Calendar useCalendar = Calendar.getInstance();
                useCalendar.setTime(startCalendar.getTime());

                for (BuRepairPlanSpeEq planSpeEq : specAssets) {
                    String useStartTime = planSpeEq.getStartTime();
                    String useEndTime = planSpeEq.getEndTime();

                    BuSpecAssetPlan specAssetPlan = new BuSpecAssetPlan()
                            .setId(UUIDGenerator.generate())
                            .setSpecAssetId(planSpeEq.getSpecAssetId())
                            .setPlanType(1)
                            .setStartTime(getPreciseTime(useCalendar, useStartTime, 0))
                            .setEndTime(getPreciseTime(useCalendar, useEndTime, 1))
                            .setRemark("生成列计划时自动生成运用计划")
                            .setCreateTime(now)
                            .setCreateBy(userId);
                    specAssetUsePlanList.add(specAssetPlan);
                }

                startCalendar.add(Calendar.DATE, 1);
            }
        }

        if (CollectionUtils.isNotEmpty(specAssetUsePlanList)) {
            List<List<BuSpecAssetPlan>> batchSubList = DatabaseBatchSubUtil.batchSubList(specAssetUsePlanList);
            for (List<BuSpecAssetPlan> batchSub : batchSubList) {
                buSpecAssetPlanMapper.insertList(batchSub);
            }
        }
    }

    private Date getPreciseTime(Calendar calendar, String time, int type) {
        if (StringUtils.isBlank(time)) {
            if (0 == type) {
                return DateUtils.transToDayStart(calendar).getTime();
            } else if (1 == type) {
                return DateUtils.transToDayEnd(calendar).getTime();
            }
        } else {
            String[] split = time.split(":");
            String hour = split[0];
            String minute = split[1];
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
            if (0 == type) {
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            } else if (1 == type) {
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 997);
            }

            return calendar.getTime();
        }

        return calendar.getTime();
    }

    private void updatePlanFinishTimeAndDuration(BuRepairPlan buRepairPlan) {
        Date latestFinishTime = buRepairPlanTaskMapper.getTaskLatestFinishTimeByPlanId(buRepairPlan.getId());
        Integer taskTotalDuration = buRepairPlanTaskMapper.getTaskTotalDurationByPlanId(buRepairPlan.getId());
        BuRepairPlan planForUpdate = new BuRepairPlan()
                .setId(buRepairPlan.getId())
                .setFinishDate(latestFinishTime)
                .setDuration(taskTotalDuration);
        buRepairPlanMapper.updateById(planForUpdate);
    }

    private List<BuRepairPlanTask> extractPlanTaskLeafNodeList(BuRepairPlanTask parent, List<BuRepairPlanTask> planTaskList) {
        List<BuRepairPlanTask> parentList;
        if (null == parent) {
            Set<String> planTaskIdSet = planTaskList.stream()
                    .map(BuRepairPlanTask::getId)
                    .collect(Collectors.toSet());
            // 父列计划任务=顶级列计划任务
            parentList = planTaskList.stream()
                    .filter(planTask -> !planTaskIdSet.contains(planTask.getParentId()))
                    .collect(Collectors.toList());
        } else {
            // 父列计划任务=指定列计划任务
            parentList = Collections.singletonList(parent);
        }

        Set<BuRepairPlanTask> resultSet = new HashSet<>();
        // 递归添加父列计划任务下的叶子节点
        for (BuRepairPlanTask parentTask : parentList) {
            addPlanTaskAllLeafNode(parentTask, planTaskList, resultSet);
        }

        List<BuRepairPlanTask> taskList = new ArrayList<>(resultSet);
        taskList.sort(Comparator.comparing(BuRepairPlanTask::getTaskNo, Comparator.nullsLast(Comparator.naturalOrder())));
        return taskList;
    }

    private void addPlanTaskAllLeafNode(BuRepairPlanTask parent, List<BuRepairPlanTask> planTaskList, Set<BuRepairPlanTask> resultSet) {
        String parentId = parent.getId();

        List<BuRepairPlanTask> children = planTaskList.stream()
                .filter(planTask -> parentId.equals(planTask.getParentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(children)) {
            resultSet.add(parent);
        } else {
            for (BuRepairPlanTask child : children) {
                addPlanTaskAllLeafNode(child, planTaskList, resultSet);
            }
        }
    }

    private Integer getUseCategory(String useCategory) {
        if (null == useCategory) {
            return null;
        }
        if ("必换件".equals(useCategory)) {
            return 1;
        }
        if ("故障件".equals(useCategory)) {
            return 2;
        }
        if ("耗材".equals(useCategory)) {
            return 3;
        }

        throw new JeecgBootException("物资使用类型不明确：" + useCategory);
    }

    private void rebuildMaterialRptDataByNewThread(List<String> trainNoList) {
        if (CollectionUtils.isEmpty(trainNoList)) {
            return;
        }

        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                for (String trainNo : trainNoList) {
                    materialRptService.reBuildRptMaterialByTrainNo(trainNo);
                }
            } catch (Exception ex) {
                log.error("导入历史成本工单时，开线程重新统计车辆物料统计数据，执行失败，原因如下：" + ex.getMessage());
                StringWriter stringWriter = new StringWriter();
                try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                    ex.printStackTrace(printWriter);
                }
                log.error(stringWriter.toString());
            }
        });
        singleExecutor.shutdown();
    }

}
