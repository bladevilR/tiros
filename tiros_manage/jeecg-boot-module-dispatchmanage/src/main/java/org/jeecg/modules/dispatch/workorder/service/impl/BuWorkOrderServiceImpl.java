package org.jeecg.modules.dispatch.workorder.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.BusinessException;
import org.jeecg.common.exception.ExceptionCode;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.tiros.holiday.bean.SpecialHoliday;
import org.jeecg.common.tiros.holiday.service.HolidayService;
import org.jeecg.common.tiros.pallet.PalletStatusCheckService;
import org.jeecg.common.tiros.rpt.service.*;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.stock.change.bean.BuMaterialStockChange;
import org.jeecg.common.tiros.stock.change.service.BuMaterialStockChangeService;
import org.jeecg.common.tiros.third.maximo.bean.BuMaximoTransData;
import org.jeecg.common.tiros.third.maximo.service.BuMaximoTransDataService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.LengthCheckUtil;
import org.jeecg.common.tiros.util.OrderStatusCheckUtil;
import org.jeecg.common.tiros.util.TirosUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.MD5Util;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.workflow.bean.WfBizStatus;
import org.jeecg.common.workflow.bean.vo.SignalVO;
import org.jeecg.common.workflow.bean.vo.StartVO;
import org.jeecg.common.workflow.constant.WfConstant;
import org.jeecg.common.workflow.service.WfBizStatusService;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.jeecg.modules.basemanage.productionnotice.service.IBuProductionNoticeService;
import org.jeecg.modules.dispatch.fault.bean.BuFaultInfo;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultInfoMapper;
import org.jeecg.modules.dispatch.planform.bean.*;
import org.jeecg.modules.dispatch.planform.bean.vo.BuPlanFormChoiceVO;
import org.jeecg.modules.dispatch.planform.mapper.*;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlanTask;
import org.jeecg.modules.dispatch.serialplan.bean.BuTrainAsset;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanMapper;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanTaskMapper;
import org.jeecg.modules.dispatch.serialplan.mapper.BuTrainAssetDispatchMapper;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.bean.bo.BuRepairReguDetailBO;
import org.jeecg.modules.dispatch.workorder.bean.bo.GroupStockUsed;
import org.jeecg.modules.dispatch.workorder.bean.bo.SysUserBO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuMaterialApplyOrderCreateVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderRecordCreateTaskQRCodeVO;
import org.jeecg.modules.dispatch.workorder.mapper.*;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Slf4j
@Service
public class BuWorkOrderServiceImpl extends ServiceImpl<BuWorkOrderMapper, BuWorkOrder> implements BuWorkOrderService {

    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;
    @Resource
    private BuWorkOrderTaskMapper buWorkOrderTaskMapper;
    @Resource
    private BuWorkOrderTaskWorkstationMapper buWorkOrderTaskWorkstationMapper;
    @Resource
    private BuWorkOrderMaterialMapper buWorkOrderMaterialMapper;
    @Resource
    private BuWorkOrderMaterialActsMapper buWorkOrderMaterialActsMapper;
    @Resource
    private BuWorkOrderToolMapper buWorkOrderToolMapper;
    @Resource
    private BuWorkOrderTechFileMapper buWorkOrderTechFileMapper;
    @Resource
    private BuWorkOrderBookStepMapper buWorkOrderBookStepMapper;
    @Resource
    private BuWorkOrderAnnexMapper buWorkOrderAnnexMapper;
    @Resource
    private BuWorkOrderTaskEquMapper buWorkOrderTaskEquMapper;
    @Resource
    private BuWorkOrderTaskFormInstMapper buWorkOrderTaskFormInstMapper;
    @Resource
    private BuRepairTaskStaffArrangeMapper buRepairTaskStaffArrangeMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;
    @Resource
    private BuRepairPlanFormsMapper buRepairPlanFormsMapper;
    @Resource
    private BuRepairPlanTaskFormsMapper buRepairPlanTaskFormsMapper;
    @Resource
    private BuMaterialApplyDispatchMapper buMaterialApplyDispatchMapper;
    @Resource
    private BuMaterialApplyDetailDispatchMapper buMaterialApplyDetailDispatchMapper;
    @Resource
    private BuMaterialAssignDetailDispatchMapper buMaterialAssignDetailDispatchMapper;
    @Resource
    private BuMaterialMustListDispatchMapper buMaterialMustListDispatchMapper;
    @Resource
    private BuMaterialReturnedDispatchMapper buMaterialReturnedDispatchMapper;
    @Resource
    private BuMaterialReturnedDetailDispatchMapper buMaterialReturnedDetailDispatchMapper;
    @Resource
    private BuMaterialGroupStockDispatchMapper buMaterialGroupStockDispatchMapper;
    @Resource
    private PalletStatusCheckService palletStatusCheckService;
    @Resource
    private BuRepairPlanMapper buRepairPlanMapper;
    @Resource
    private BuRepairPlanTaskMapper buRepairPlanTaskMapper;
    @Resource
    private BuFaultInfoMapper buFaultInfoMapper;
    @Resource
    private BuMaximoLocationDispatchMapper buMaximoLocationDispatchMapper;
    @Resource
    private BuMtrWorkshopGroupDispatchMapper buMtrWorkshopGroupDispatchMapper;
    @Resource
    private BuBaseScheduleDispatchMapper buBaseScheduleDispatchMapper;
    @Resource
    public RedisTemplate<String, String> redisTemplate;
    @Resource
    public ISysBaseAPI sysBaseAPI;
    @Resource
    private WorkflowForwardService workflowForwardService;
    @Resource
    private BuMaximoTransDataService buMaximoTransDataService;
    @Resource
    private HolidayService holidayService;
    @Resource
    private HomepageItemRptService homepageItemRptService;
    @Resource
    private MaterialRptService materialRptService;
    @Resource
    private KpiRptService kpiRptService;
    @Resource
    private PlanProgressUpdateService planProgressUpdateService;
    @Resource
    private AlertRecordService alertRecordService;
    @Resource
    private WfBizStatusService wfBizStatusService;
    @Resource
    private BuMaterialStockChangeService buMaterialStockChangeService;
    @Resource
    private IBuProductionNoticeService productionNoticeService;

    private static final List<Integer> MATERIAL_ORDER_TYPE = Arrays.asList(4, 5);


    /**
     * @see BuWorkOrderService#page(BuWorkOrderQueryVO, Integer, Integer, boolean)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuWorkOrder> page(BuWorkOrderQueryVO queryVO, Integer pageNo, Integer pageSize, boolean isWorkGroup) throws Exception {
        if (null != queryVO.getStartDate()) {
            queryVO.setStartDate(DateUtils.transToDayStart(queryVO.getStartDate()));
        }
        if (null != queryVO.getEndDate()) {
            queryVO.setEndDate(DateUtils.transToDayEnd(queryVO.getEndDate()));
        }
        // 如果手动控制显示监控大屏显示数据，包括列计划进度、工班任务进度、委外任务进度
        if (null != queryVO.getIsForMonitorScreenControl() && 1 == queryVO.getIsForMonitorScreenControl()) {
            queryVO.setOverdue(0);
        }

        IPage<BuWorkOrder> page;
        if (isWorkGroup) {
            page = buWorkOrderMapper.selectPageByConditionForGroup(new Page<>(pageNo, pageSize), queryVO);
        } else {
            page = buWorkOrderMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        }

        // 查询设置发料工单的同步属性
        setApplyOrderProperties(page.getRecords());

        return page;
    }

    /**
     * @see BuWorkOrderService#applyPage(BuWorkOrderQueryVO, Integer, Integer, boolean)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuWorkOrder> applyPage(BuWorkOrderQueryVO queryVO, Integer pageNo, Integer pageSize, boolean isWorkGroup) throws Exception {
        if (null != queryVO.getStartDate()) {
            queryVO.setStartDate(DateUtils.transToDayStart(queryVO.getStartDate()));
        }
        if (null != queryVO.getEndDate()) {
            queryVO.setEndDate(DateUtils.transToDayEnd(queryVO.getEndDate()));
        }

        IPage<BuWorkOrder> page;
        if (isWorkGroup) {
            page = buWorkOrderMapper.selectApplyPageByConditionForGroup(new Page<>(pageNo, pageSize), queryVO);
        } else {
            page = buWorkOrderMapper.selectApplyPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        }

        // 查询设置发料工单的同步属性
        setApplyOrderProperties(page.getRecords());

        return page;
    }

    /**
     * @see BuWorkOrderService#selectWorkOrderRelevanceInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public WorkOrderRelevanceInfo selectWorkOrderRelevanceInfo(String id) throws Exception {
        WorkOrderRelevanceInfo relevanceInfo = buWorkOrderMapper.selectWorkOrderRelevanceInfo(id);
        if (null != relevanceInfo) {
            setWorkstationStaffs(relevanceInfo);
            setAnnexFieldProperties(relevanceInfo);
            setMaterialCanReplace(relevanceInfo);
            setEquipmentToTask(relevanceInfo);

            // 安全提示、技术要求，去规程里找
            List<BuRepairReguDetailBO> reguDetailBOList = buWorkOrderTaskMapper.selectOrderRelativeReguDetailList(id);
            JobRequirement jobRequirement = joinJobRequirement(reguDetailBOList);
            // 安全预想，取工单任务所属工班的最后一条安全预想
            Map<String, String> safetyExpectation = buWorkOrderTaskMapper.selectLastSafeAssumeByGroupId(relevanceInfo.getGroupId());
            if (null != safetyExpectation) {
                jobRequirement.setSafetyExpectationId(safetyExpectation.get("Id"));
                jobRequirement.setSafetyExpectation(safetyExpectation.get("content"));
            }

            // 设置来源库位和托盘名称信息
            setLocationWarehouseNamesAndPalletNames(relevanceInfo.getMaterials());

            // 表单是检查记录表时，检查人显示为已检查/未检查
            List<BuWorkOrderTaskFormInst> forms = relevanceInfo.getForms();
            if (CollectionUtils.isNotEmpty(forms)) {
                for (BuWorkOrderTaskFormInst formInst : forms) {
                    if (null != formInst.getInstType() && 4 == formInst.getInstType()) {
                        if (null != formInst.getStatus() && 1 == formInst.getStatus()) {
                            formInst.setCheckUserName("已检查");
                        } else {
                            formInst.setCheckUserName("未检查");
                        }
                    }
                }
            }
        }

        return relevanceInfo;
    }

    /**
     * @see BuWorkOrderService#listOrderMaterialAssign(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialAssignDetail> listOrderMaterialAssign(String orderMaterialId) throws Exception {
        // 查工单物料
        BuWorkOrderMaterial orderMaterial = buWorkOrderMaterialMapper.selectById(orderMaterialId);
        if (null == orderMaterial) {
            throw new JeecgBootException("工单物料不存在");
        }
        BuWorkOrder order = buWorkOrderMapper.selectById(orderMaterial.getOrderId());
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }

        // 查领用明细及分配明细
        List<BuMaterialApplyDetail> applyDetailList;
        if (1 == orderMaterial.getUseCategory()) {
            // 是必换件，查属于同一列计划的该必换件的领用明细
            String planId = order.getPlanId();
            String groupId = order.getGroupId();
            String materialTypeId = orderMaterial.getMaterialTypeId();

            applyDetailList = buMaterialApplyDetailDispatchMapper.selectMaterialOrderApplyDetailListWithAssign(planId, groupId, materialTypeId);
        } else {
            // 不是必换件，查该工单物料的领用明细
            applyDetailList = buMaterialApplyDetailDispatchMapper.selectListWithAssignByOrderMaterialIdList(Collections.singletonList(orderMaterialId));
        }

        // 生成结果
        List<BuMaterialAssignDetail> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(applyDetailList)) {
            for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                List<BuMaterialAssignDetail> assignDetailList = applyDetail.getAssignDetailList();
                if (CollectionUtils.isEmpty(assignDetailList)) {
                    continue;
                }

                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    assignDetail.setSendUserName(applyDetail.getSendUserName());
                    assignDetail.setSourceLocationName(TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName()));
                    resultList.add(assignDetail);
                }
            }
        }

        return resultList;
    }

    /**
     * @see BuWorkOrderService#saveWorkOrder(BuWorkOrder)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveWorkOrder(BuWorkOrder order) throws Exception {
        validateProductionNoticeOrder(order);
        // 设置物资属性，班长和工期
        setOrderProperties(order);
        // 插入工单
        String orderId = UUIDGenerator.generate();
        String code = serialNumberGenerate.generateOrderCode(order.getLineId());
        order.setId(orderId)
                .setOrderCode(code)
                .setGenerate(0);
        if (null == order.getWorkStatus()) {
            order.setWorkStatus(0);
        }
        buWorkOrderMapper.insert(order);
        bindProductionNoticeOrder(order);

        // 插入关联信息
        insertWorkOrderRelevanceInfo(order, true);
        // 过滤列计划表单实例、与工单工位有关联的表单，建立列计划表单实例和工单任务的关联
        insertFormInstByPlanTaskAndWorkstationRef(Collections.singletonList(order), order.getTasks());
        // 插入表单关联
        insertWorkOrderFormsInst(order);

        // 如果工单中的任务类型是故障任务，更新故障信息中的处理工单id
        setFaultOrderFaultInfo(Collections.singletonList(order.getId()));

        // 启动流程，转移到controller层，不在同一事务中
//        // 启动工单流程
//        startOrderWorkflow(order);

        return true;
    }

    /**
     * @see BuWorkOrderService#addMaterialApplyOrder(BuMaterialApplyOrderCreateVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addMaterialApplyOrder(BuMaterialApplyOrderCreateVO createVO) throws Exception {
        BuRepairPlan plan = buRepairPlanMapper.selectById(createVO.getPlanId());
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }
        if (2 != plan.getStatus()) {
            throw new JeecgBootException("列计划[" + plan.getPlanName() + "]未完成审批，不能生成发料工单");
        }
        if (Arrays.asList(3, 4, 5).contains(plan.getProgressStatus())) {
            throw new JeecgBootException("列计划[" + plan.getPlanName() + "]已完成，不能生成发料工单");
        }
        List<String> groupIdList = createVO.getGroupIdList();
        if (CollectionUtils.isEmpty(groupIdList)) {
            throw new JeecgBootException("请选择班组");
        }

        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        // 查询班组
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupDispatchMapper.selectBatchIds(groupIdList);
        Map<String, BuMtrWorkshopGroup> idGroupMap = new HashMap<>();
        groupList.forEach(group -> idGroupMap.put(group.getId(), group));

        // 检查已生成过的发料工单
        LambdaQueryWrapper<BuWorkOrder> orderWrapper = new LambdaQueryWrapper<BuWorkOrder>()
                .eq(BuWorkOrder::getOrderType, 4)
                .eq(BuWorkOrder::getPlanId, plan.getId())
                .in(BuWorkOrder::getGroupId, groupIdList);
        List<BuWorkOrder> dbOrderList = buWorkOrderMapper.selectList(orderWrapper);
        if (CollectionUtils.isNotEmpty(dbOrderList)) {
            List<String> groupNameList = new ArrayList<>();
            for (BuWorkOrder dbOrder : dbOrderList) {
                BuMtrWorkshopGroup group = idGroupMap.get(dbOrder.getGroupId());
                if (null != group) {
                    groupNameList.add(group.getGroupName());
                }
            }
            String errorMessage = "列计划[" + plan.getPlanName() + "]已生成班组[" + String.join(",", groupNameList) + "]的发料工单，请勿重复生成";
            throw new JeecgBootException(errorMessage);
        }

        LambdaQueryWrapper<BuMaterialMustList> mustWrapper = new LambdaQueryWrapper<BuMaterialMustList>()
                .eq(BuMaterialMustList::getStatus, 1)
                .eq(BuMaterialMustList::getLineId, plan.getLineId())
                .eq(BuMaterialMustList::getRepairProgramId, plan.getRepairProgramId())
                .in(BuMaterialMustList::getGroupId, groupIdList);
        List<BuMaterialMustList> mustListList = buMaterialMustListDispatchMapper.selectList(mustWrapper);

        // 每个工班创建发料工单类型的工单
        List<BuWorkOrder> orderList = new ArrayList<>();
        List<BuWorkOrderMaterial> orderMaterialList = new ArrayList<>();
        for (BuMtrWorkshopGroup group : groupList) {
            List<BuMaterialMustList> groupMustListList = mustListList.stream()
                    .filter(must -> group.getId().equals(must.getGroupId()))
                    .collect(Collectors.toList());

            // 创建工单
            String orderId = UUIDGenerator.generate();
            String orderCode = serialNumberGenerate.generateOrderCode(plan.getLineId());
            String orderName = plan.getTrainNo() + "车" + group.getGroupName() + "必换件发料工单";
            BuWorkOrder order = new BuWorkOrder()
                    .setId(orderId)
                    .setOrderCode(orderCode)
                    .setOrderName(orderName)
                    .setOrderType(4)
                    .setGenerate(1)
                    .setPlanId(plan.getId())
                    .setStartTime(DateUtils.transToDayStart(now))
                    .setFinishTime(DateUtils.transToDayEnd(now))
                    .setDuration(1)
                    .setGroupId(group.getId())
                    .setMonitor(group.getMonitor())
                    .setWorkshopId(plan.getWorkshopId())
                    .setLineId(plan.getLineId())
                    .setTrainNo(plan.getTrainNo())
                    .setActStart(DateUtils.transToDayStart(now))
                    .setActFinish(null)
                    .setOrderStatus(0)
                    .setWorkStatus(0)
                    .setRemark("由列计划[" + plan.getPlanName() + "]生成的必换件发料工单")
                    .setIssuingTime(null)
                    .setCreateTime(now)
                    .setCreateBy(userId)
                    .setUpdateTime(null)
                    .setUpdateBy(null)
                    .setFdProject(plan.getFdProject())
                    .setFdTask(plan.getFdTask())
                    .setFdCostType(plan.getFdCostType());
            orderList.add(order);
            // 工单物料
            if (CollectionUtils.isNotEmpty(groupMustListList)) {
                // 必换件清单如果对应着一个物资类型，合并
                Map<String, List<BuMaterialMustList>> materialTypeIdMustListMap = groupMustListList.stream()
                        .filter(mustList -> StringUtils.isNotBlank(mustList.getMaterialTypeId()))
                        .collect(Collectors.groupingBy(BuMaterialMustList::getMaterialTypeId));
                for (Map.Entry<String, List<BuMaterialMustList>> materialTypeIdMustListEntry : materialTypeIdMustListMap.entrySet()) {
                    String materialTypeId = materialTypeIdMustListEntry.getKey();
                    List<BuMaterialMustList> mustList = materialTypeIdMustListEntry.getValue();

                    double needAmountSum = mustList.stream()
                            .mapToDouble(BuMaterialMustList::getNeedAmount)
                            .sum();

                    BuWorkOrderMaterial orderMaterial = new BuWorkOrderMaterial()
                            .setId(UUIDGenerator.generate())
                            .setOrderId(orderId)
                            .setMaterialTypeId(materialTypeId)
                            .setAmount(needAmountSum)
                            .setActAmount(0D)
                            .setRemark("由列计划[" + plan.getPlanName() + "]生成的必换件发料工单的物料")
                            .setUseCategory(1)
                            .setIsVerify(0)
                            .setOpType(1)
                            .setIsGenOrder(0)
                            .setPlanAmount(needAmountSum)
                            .setNeedDispatchin(1);
                    orderMaterialList.add(orderMaterial);
                }
            }
        }
        // 保存工单和物料
        if (CollectionUtils.isNotEmpty(orderList)) {
            buWorkOrderMapper.insertList(orderList);
        }
        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            buWorkOrderMaterialMapper.insertList(orderMaterialList);
        }

        return true;
    }

    /**
     * @see BuWorkOrderService#editWorkOrder(BuWorkOrder)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean editWorkOrder(BuWorkOrder order) throws Exception {
        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        BuWorkOrder dbOrder = buWorkOrderMapper.selectById(order.getId());
        Integer currentOrderStatus = dbOrder.getOrderStatus();
        validateProductionNoticeOrder(order);
        if (StringUtils.isNotBlank(dbOrder.getProductionNoticeId()) && !StringUtils.equals(dbOrder.getProductionNoticeId(), order.getProductionNoticeId())) {
            throw new JeecgBootException("已绑定生产通知单的工单不允许更换通知单");
        }

        // 设置物资属性，班长和工期
        setOrderProperties(order);
        // 更新工单
        buWorkOrderMapper.updateById(order);
        String orderId = order.getId();

        // 根据工单状态，判断此"修改工单"是执行什么操作
        Integer orderStatus = order.getOrderStatus();
        boolean isIssuing = null != orderStatus && (orderStatus == 1) && null != currentOrderStatus && (currentOrderStatus == 0);
        boolean isVerify = null != orderStatus && (orderStatus == 2);
        boolean isClose = null != orderStatus && (orderStatus == 4);

        // 删除关联信息
        deleteWorkOrderRelevanceInfo(Collections.singletonList(orderId));
        // 插入关联信息
        insertWorkOrderRelevanceInfo(order, isVerify);
        // 删除表单关联
        deleteWorkOrderFormsInst(Collections.singletonList(orderId));
        // 重新插入表单关联
        insertWorkOrderFormsInst(order);

        // 如果工单中的任务类型是故障任务，更新故障信息中的处理工单id
        setFaultOrderFaultInfo(Collections.singletonList(orderId));

        // 如果该修改操作是下发工单
        if (isIssuing) {
            // 检查工单状态
            OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), currentOrderStatus, 1);
        }
        // 如果该修改操作是核实工单
        if (isVerify) {
            // 检查工单状态
            OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), currentOrderStatus, 2);

//            boolean needApplyMaterial;
//            if (4 == order.getOrderType()) {
//                // 发料工单，判断是否有物料
//                LambdaQueryWrapper<BuWorkOrderMaterial> orderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
//                        .eq(BuWorkOrderMaterial::getOrderId, orderId);
//                Integer orderMaterialCount = buWorkOrderMaterialMapper.selectCount(orderMaterialWrapper);
//                needApplyMaterial = null != orderMaterialCount && orderMaterialCount > 0;
//            } else {
//                // 非发料工单，判断工单是否有非必换件的物料
//                LambdaQueryWrapper<BuWorkOrderMaterial> notMustOrderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
//                        .eq(BuWorkOrderMaterial::getOrderId, orderId)
//                        .ne(BuWorkOrderMaterial::getUseCategory, 1);
//                Integer notMustOrderMaterialCount = buWorkOrderMaterialMapper.selectCount(notMustOrderMaterialWrapper);
//                needApplyMaterial = null != notMustOrderMaterialCount && notMustOrderMaterialCount > 0;
//            }
            // 现在是否需要申请领料单，由needDispatchin属性决定
            LambdaQueryWrapper<BuWorkOrderMaterial> needDispatchInOrderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                    .eq(BuWorkOrderMaterial::getOrderId, orderId)
                    .eq(BuWorkOrderMaterial::getNeedDispatchin, 1);
            Integer needDispatchInOrderMaterialCount = buWorkOrderMaterialMapper.selectCount(needDispatchInOrderMaterialWrapper);
            boolean needApplyMaterial = null != needDispatchInOrderMaterialCount && needDispatchInOrderMaterialCount > 0;
            if (needApplyMaterial) {
                // 需要生成领料信息，根据工单id生成并保存工单物料领用信息
                // 同时，将对应物资类型的数据加到物资库存总量的领用量
                verifyOrderSubmitMaterialApply(orderId);
            } else {
                // 不需要生成领料信息，设置工单状态为8填报中
                order.setOrderStatus(8);
                buWorkOrderMapper.updateById(order);
                // 工单状态变成8填报中时，删除工单没有确认的退料单，并将对应的工单物料是否生成单据的状态改成未生成
                clearUnConfirmMaterialReturn(order.getId());
            }

            // 如果工单是自动生成的，更新列计划实际开始时间
            if (null != order.getGenerate() && 1 == order.getGenerate()) {
                planProgressUpdateService.updatePlanStart(Collections.singletonList(order.getPlanId()), now);
            }

            // 核实工单创建日程给对应人员安排
            addBaseScheduleToArrangeUser(Collections.singletonList(orderId));

            // 添加数据到maximo同步中间表：新增工单
            addMaximoTransDataOfApproveOrder(Collections.singletonList(orderId), now);
        }
        // 如果该修改操作是关闭工单
        if (isClose) {
            // 检查工单状态
            OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), currentOrderStatus, 4);

            // 检查是否有该工单的退料没有确认
            checkUnConfirmMaterialReturn(Collections.singletonList(orderId));

            // 如果是发料工单
            if (MATERIAL_ORDER_TYPE.contains(order.getOrderType())) {
                order = buWorkOrderMapper.selectById(orderId);
                // 作业状态改为作业完成、设置实际结束
                order.setWorkStatus(2)
                        .setActFinish(now);
                if (null == order.getActStart() || order.getActStart().after(now)) {
                    Date issuingTime = order.getIssuingTime();
                    order.setActStart(null != issuingTime ? issuingTime : new Date(now.getTime() - 60000L));
                }
                buWorkOrderMapper.updateById(order);

                productionNoticeService.refreshProgressByOrder(order.getId(), order.getTrainNo());
            }

            // 故障工单关闭后，处理对应故障处理流程
            LambdaQueryWrapper<BuFaultInfo> faultWrapper = new LambdaQueryWrapper<BuFaultInfo>()
                    .eq(BuFaultInfo::getHandleOrderId, orderId);
            List<BuFaultInfo> faultList = buFaultInfoMapper.selectList(faultWrapper);
            if (CollectionUtils.isNotEmpty(faultList)) {
                List<String> faultIdList = faultList.stream()
                        .map(BuFaultInfo::getId)
                        .collect(Collectors.toList());
                List<WfBizStatus> wfBizStatusList = wfBizStatusService.listByBusinessKeyListAndSolutionCode(faultIdList, WfConstant.SOLUTION_CODE_FAULT);

                for (WfBizStatus wfBizStatus : wfBizStatusList) {
                    String processInstanceId = wfBizStatus.getProcessInstanceId();
                    String activityId = "Order_Finish";
                    SignalVO signalVO = new SignalVO().setProcessInstanceId(processInstanceId)
                            .setActivityId(activityId);
                    workflowForwardService.signal(signalVO);
                }
            }

            // 关闭工单，减少班组库存
            closeOrderDecreaseMaterialGroupStock(Collections.singletonList(order), now, userId);

            // 关闭工单，添加数据到maximo同步中间表
            closeOrderAddMaximoTransData(Collections.singletonList(orderId), now);

            // 开线程重新统计车辆物料统计数据
            rebuildMaterialRptDataByNewThread(Collections.singletonList(order.getTrainNo()));
            // 开线程统计工时绩效
            rebuildKpiRptDataByNewThread(Collections.singletonList(orderId));

            productionNoticeService.refreshProgressByOrder(order.getId(), order.getTrainNo());
        }

        // 启动流程，转移到controller层，不在同一事务中
//        // 启动工单流程
//        startOrderWorkflow(order);

        if (StringUtils.isBlank(dbOrder.getProductionNoticeId())) {
            bindProductionNoticeOrder(order);
        }

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        return true;
    }

    /**
     * @see BuWorkOrderService#verifyOrder(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean verifyOrder(String orderId) throws Exception {
        Date now = new Date();

        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        if (!Arrays.asList(0, 1).contains(order.getOrderStatus())) {
            throw new JeecgBootException("工单不能核实，当前状态(" + order.getOrderStatus() + ")");
        }

        // 如果是发料工单，核实时设置工单实际开始时间
        if (MATERIAL_ORDER_TYPE.contains(order.getOrderType())) {
            if (null == order.getActStart() || order.getActStart().equals(order.getStartTime())) {
                order.setActStart(now);
            }
        }

        // 现在是否需要申请领料单，由needDispatchin属性决定
        LambdaQueryWrapper<BuWorkOrderMaterial> needDispatchInOrderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                .eq(BuWorkOrderMaterial::getOrderId, orderId)
                .eq(BuWorkOrderMaterial::getNeedDispatchin, 1);
        Integer needDispatchInOrderMaterialCount = buWorkOrderMaterialMapper.selectCount(needDispatchInOrderMaterialWrapper);
        boolean needApplyMaterial = null != needDispatchInOrderMaterialCount && needDispatchInOrderMaterialCount > 0;
        if (needApplyMaterial) {
            // 需要生成领料信息，根据工单id生成并保存工单物料领用信息
            // 同时，将对应物资类型的数据加到物资库存总量的领用量
            verifyOrderSubmitMaterialApply(orderId);
            // 更新工单为已核实
            order.setOrderStatus(2);
            buWorkOrderMapper.updateById(order);
        } else {
            // 不需要生成领料信息，设置工单状态为8填报中
            order.setOrderStatus(8);
            buWorkOrderMapper.updateById(order);
            // 工单状态变成8填报中时，删除工单没有确认的退料单，并将对应的工单物料是否生成单据的状态改成未生成
            clearUnConfirmMaterialReturn(order.getId());
        }

        // 如果工单是自动生成的，更新列计划实际开始时间
        if (null != order.getGenerate() && 1 == order.getGenerate()) {
            planProgressUpdateService.updatePlanStart(Collections.singletonList(order.getPlanId()), now);
        }

        // 核实工单创建日程给对应人员安排
        addBaseScheduleToArrangeUser(Collections.singletonList(orderId));

        // 添加数据到maximo同步中间表：新增工单
        addMaximoTransDataOfApproveOrder(Collections.singletonList(orderId), now);

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        return true;
    }

    /**
     * @see BuWorkOrderService#verifyOrderForApp(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean verifyOrderForApp(String orderId) throws Exception {
        Date now = new Date();

        // 查询工单
        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }

        // 检查工单状态
        OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), order.getOrderStatus(), 2);

        // 如果工单中的任务类型是故障任务，更新故障信息中的处理工单id
        setFaultOrderFaultInfo(Collections.singletonList(orderId));

        // 现在是否需要申请领料单，由needDispatchin属性决定
        LambdaQueryWrapper<BuWorkOrderMaterial> needDispatchInOrderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                .eq(BuWorkOrderMaterial::getOrderId, orderId)
                .eq(BuWorkOrderMaterial::getNeedDispatchin, 1);
        Integer needDispatchInOrderMaterialCount = buWorkOrderMaterialMapper.selectCount(needDispatchInOrderMaterialWrapper);
        boolean needApplyMaterial = null != needDispatchInOrderMaterialCount && needDispatchInOrderMaterialCount > 0;
        if (needApplyMaterial) {
            // 需要生成领料信息，根据工单id生成并保存工单物料领用信息
            // 同时，将对应物资类型的数据加到物资库存总量的领用量
            verifyOrderSubmitMaterialApply(orderId);
            //设置工单状态为已下发已核实
            order.setOrderStatus(2);
        } else {
            // 不需要生成领料信息，设置工单状态为8填报中
            order.setOrderStatus(8);
            // 工单状态变成8填报中时，删除工单没有确认的退料单，并将对应的工单物料是否生成单据的状态改成未生成
            clearUnConfirmMaterialReturn(order.getId());
        }

        // 如果工单是自动生成的，更新列计划实际开始时间
        if (null != order.getGenerate() && 1 == order.getGenerate()) {
            planProgressUpdateService.updatePlanStart(Collections.singletonList(order.getPlanId()), now);
        }

        // 核实工单创建日程给对应人员安排
        addBaseScheduleToArrangeUser(Collections.singletonList(orderId));

        // 添加数据到maximo同步中间表：新增工单
        addMaximoTransDataOfApproveOrder(Collections.singletonList(orderId), now);

        // 启动流程，转移到controller层，不在同一事务中
//        // 启动工单流程
//        startOrderWorkflow(order);
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 提交工单当前流程的第一个任务
        Map<String, Object> vars = new HashMap<>();
        vars.put("hasMaterial", needDispatchInOrderMaterialCount);

        if (4 == order.getOrderType()) {
            workflowForwardService.completeMaterialApplyFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "APP核实工单", vars);
        } else if (5 == order.getOrderType()) {
            workflowForwardService.completeWorkshopConsumeFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "APP核实工单", vars);
        } else {
            workflowForwardService.completeOrderFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "APP核实工单", vars);
        }
        //更新工单状态
        buWorkOrderMapper.updateById(order);
        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        return true;
    }

    /**
     * @see BuWorkOrderService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> orderIdList = Arrays.asList(ids.split(","));
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdList);

        if (CollectionUtils.isNotEmpty(orderList)) {
            List<String> idList = orderList.stream()
                    .filter(order -> order.getOrderStatus() == 0)
                    .map(BuWorkOrder::getId)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(idList)) {
                // 删除关联信息
                deleteWorkOrderRelevanceInfo(idList);
                // 删除表单实例和工单任务的关联
                deleteWorkOrderFormsInst(idList);

                for (String id : idList) {
                    productionNoticeService.unbindOrder(id);
                }

                buWorkOrderMapper.deleteBatchIds(idList);
            }
        }

        // 如果工单中的任务类型是故障任务，删除故障信息中的处理工单id
        deleteFaultOrderFaultInfo(orderIdList);

        return true;
    }

    /**
     * @see BuWorkOrderService#issuingWorkOrderBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean issuingWorkOrderBatch(String ids) throws Exception {
        List<String> orderIdList = Arrays.asList(ids.split(","));
        Date now = new Date();

        if (CollectionUtils.isNotEmpty(orderIdList)) {
            List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdList);

            for (BuWorkOrder order : orderList) {
                // 检查工单状态
                OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), order.getOrderStatus(), 1);
            }

            BuWorkOrder updateOrder = new BuWorkOrder()
                    .setOrderStatus(1)
                    .setIssuingTime(now);
            LambdaQueryWrapper<BuWorkOrder> wrapper = new LambdaQueryWrapper<BuWorkOrder>()
                    .in(BuWorkOrder::getId, orderIdList);
            buWorkOrderMapper.update(updateOrder, wrapper);

            // 如果工单是自动生成的，更新列计划状态为已开始
            Set<String> planIdSet = orderList.stream()
                    .filter(order -> null != order.getGenerate() && 1 == order.getGenerate())
                    .map(BuWorkOrder::getPlanId)
                    .collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(planIdSet)) {
                planProgressUpdateService.updatePlanStart(new ArrayList<>(planIdSet), now);
            }

            // 发送消息给工班长
            sendAnnouncementToMonitor(orderList);
            // 下发工单创建日程给对应工班长
            addBaseScheduleToMonitor(orderList);

            // 更新首页数据区数据
            homepageItemRptService.rewriteDataItem();

            // 暂时不需要，注释掉
//            // 同步maximo库存变动
//            transFromMaximoService.readMaterialStockChange();
        }

        return true;
    }

    /**
     * @see BuWorkOrderService#submitOrderToDispatcherBatch(String, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitOrderToDispatcherBatch(String ids, Boolean force) throws Exception {
        List<String> orderIdList = Arrays.asList(ids.split(","));
        if (force == null) {
            force = false;
        }
        Date now = new Date();

        List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdList);
        for (BuWorkOrder order : orderList) {
            // 检查工单状态
            OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), order.getOrderStatus(), 3);
        }

        // 查询工单任务
        LambdaQueryWrapper<BuWorkOrderTask> taskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                .in(BuWorkOrderTask::getOrderId, orderIdList);
        List<BuWorkOrderTask> taskList = buWorkOrderTaskMapper.selectList(taskWrapper);
        // 验证工单任务状态
        long unFinishTaskCount = taskList.stream().filter(task -> task.getTaskStatus() != 2).count();
        if (!force && unFinishTaskCount > 0) {
            throw new BusinessException(ExceptionCode.ORDER_TASK_NOT_FINISH);
        }

        // 工单任务状态不修改，工单修改为已提交
        LambdaQueryWrapper<BuWorkOrder> orderWrapper = new LambdaQueryWrapper<BuWorkOrder>()
                .in(BuWorkOrder::getId, orderIdList);
        BuWorkOrder order = new BuWorkOrder()
                .setOrderStatus(3)
                .setActFinish(now);
        buWorkOrderMapper.update(order, orderWrapper);

        // 检查并设置所有托盘状态
        palletStatusCheckService.checkAndSetPalletStatus();

        // 根据工单物料中退料物料，生成退料单
        for (String orderId : orderIdList) {
            submitOrderSubmitMaterialReturn(orderId);
        }

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        // 设置工单逾期预警为已处理
        alertRecordService.setAlertRecordHandled(7, orderIdList);

        return true;
    }

    /**
     * @see BuWorkOrderService#submitOrderToDispatcherForApp(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitOrderToDispatcherForApp(String orderId) throws Exception {
        Date now = new Date();

        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        // 检查工单状态
        OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), order.getOrderStatus(), 3);

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 查询工单任务
        LambdaQueryWrapper<BuWorkOrderTask> taskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                .eq(BuWorkOrderTask::getOrderId, orderId);
        List<BuWorkOrderTask> taskList = buWorkOrderTaskMapper.selectList(taskWrapper);
        // 验证工单任务状态
        long unFinishTaskCount = taskList.stream().filter(task -> task.getTaskStatus() != 2).count();
        if (unFinishTaskCount > 0) {
            throw new BusinessException(ExceptionCode.ORDER_TASK_NOT_FINISH);
        }

        // 工单任务状态不修改，工单修改为已提交
        buWorkOrderMapper.updateById(new BuWorkOrder()
                .setId(orderId)
                .setOrderStatus(3)
                .setActFinish(now));

        // 检查并设置所有托盘状态
        palletStatusCheckService.checkAndSetPalletStatus();

        // 根据工单物料中退料物料，生成退料单
        submitOrderSubmitMaterialReturn(orderId);

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        // 设置工单逾期预警为已处理
        alertRecordService.setAlertRecordHandled(7, Collections.singletonList(orderId));

        // 工单下任务都已完成，提交工单当前流程的第一个任务
        workflowForwardService.completeOrderFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "APP提交工单给调度", new HashMap<>());

        return true;
    }

    /**
     * @see BuWorkOrderService#closeOrderBatch(String)
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public boolean closeOrderBatch(String ids) throws Exception {
        Date now = new Date();

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        List<String> orderIdList = Arrays.asList(ids.split(","));
        if (CollectionUtils.isNotEmpty(orderIdList)) {
            // 检查是否有该工单的退料没有确认
            checkUnConfirmMaterialReturn(orderIdList);
            // 检查工单实际消耗价格是否为0
            checkOrderMaterialActPriceZero(orderIdList);

            List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdList);

            // 工单修改为已关闭
            for (BuWorkOrder order : orderList) {
                // 检查工单状态
                OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), order.getOrderStatus(), 4);

                order.setOrderStatus(4);
                // 如果是发料工单
                if (MATERIAL_ORDER_TYPE.contains(order.getOrderType())) {
                    // 作业状态改为作业完成、设置实际结束
                    order.setWorkStatus(2)
                            .setActFinish(now);
                    if (null == order.getActStart() || order.getActStart().after(now)) {
                        Date issuingTime = order.getIssuingTime();
                        order.setActStart(null != issuingTime ? issuingTime : new Date(now.getTime() - 60000L));
                    }
                }
                buWorkOrderMapper.updateById(order);
            }

            // 故障工单关闭后，处理对应故障处理流程
            LambdaQueryWrapper<BuFaultInfo> faultWrapper = new LambdaQueryWrapper<BuFaultInfo>().in(BuFaultInfo::getHandleOrderId, orderIdList);
            List<BuFaultInfo> faultList = buFaultInfoMapper.selectList(faultWrapper);
            if (CollectionUtils.isNotEmpty(faultList)) {
                List<String> faultIdList = faultList.stream()
                        .map(BuFaultInfo::getId)
                        .collect(Collectors.toList());
                List<WfBizStatus> wfBizStatusList = wfBizStatusService.listByBusinessKeyListAndSolutionCode(faultIdList, WfConstant.SOLUTION_CODE_FAULT);

                for (WfBizStatus wfBizStatus : wfBizStatusList) {
                    String processInstanceId = wfBizStatus.getProcessInstanceId();
                    String activityId = "Order_Finish";
                    SignalVO signalVO = new SignalVO().setProcessInstanceId(processInstanceId)
                            .setActivityId(activityId);
                    workflowForwardService.signal(signalVO);
                }
            }

            // 关闭工单，减少班组库存
            closeOrderDecreaseMaterialGroupStock(orderList, now, userId);

            // 关闭工单，添加数据到maximo同步中间表
            closeOrderAddMaximoTransData(orderIdList, now);

            // 开线程重新统计车辆物料统计数据
            List<String> trainNoList = orderList.stream()
                    .map(BuWorkOrder::getTrainNo)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());
            rebuildMaterialRptDataByNewThread(trainNoList);
            // 开线程统计工时绩效
            rebuildKpiRptDataByNewThread(orderIdList);

            // 更新首页数据区数据
            homepageItemRptService.rewriteDataItem();
        }

        return true;
    }

    /**
     * 审批通过关闭工单（批量）
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public boolean approveOrderBatch(String ids) throws Exception {
        // 1. 获取要关闭的工单的流程状态
        List<WfBizStatus> wfBizStatusList = wfBizStatusService.listByBusinessKeyListAndSolutionCode(Arrays.asList(ids.split(",").clone()), WfConstant.SOLUTION_CODE_ORDER);
        List<WfBizStatus> cans = wfBizStatusList.stream().filter(wf -> {
            if (StringUtils.isNotBlank(wf.getCurAttrs())) {
                Map<String, Object> attrs = wf.getAttrs();
                if (attrs.containsKey("isClose") && "1".equals(attrs.get("isClose"))) {
                    return true;
                }
                return false;
            }
            return false;
        }).collect(Collectors.toList());

        // 找到当前是关闭节点的工单
        if (CollectionUtils.isNotEmpty(cans)) {
            List<String> closeIds = cans.stream().map(w -> w.getBusinessKey()).collect(Collectors.toList());

            // 调用关闭工单方法
            this.closeOrderBatch(String.join(",", closeIds));

            List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(closeIds);

            // 获取登录人信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Map<String, Object> vars = new HashMap<>();
            vars.put("approved", true);

            // 提交工单当前流程的第一个任务
            for (BuWorkOrder order : orderList) {
                workflowForwardService.completeOrderFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "批量关闭工单", vars);
            }
        }
        return false;
    }

    /**
     * @see BuWorkOrderService#startOrderWorkflow(BuWorkOrder)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean startOrderWorkflow(BuWorkOrder order) throws Exception {
        Date now = new Date();

        Boolean startFlow = order.getStartFlow();
        if (null == startFlow) {
            startFlow = false;
        }
        if (startFlow) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("groupId", order.getGroupId());
            variables.put("businessCode", order.getOrderCode());

            String solutionCode = WfConstant.SOLUTION_CODE_ORDER;
            if (4 == order.getOrderType()) {
                solutionCode = WfConstant.SOLUTION_CODE_ORDER_MATERIAL_APPLY;
            }
            if (5 == order.getOrderType()) {
                solutionCode = WfConstant.SOLUTION_CODE_ORDER_WORKSHOP_CONSUME;
            }

            String titlePrefix = "工单：" + order.getTrainNo() + "车";
            if (order.getOrderType() == 4) {
                if (StringUtils.isBlank(order.getGroupName())) {
                    order = buWorkOrderMapper.selectOrderById(order.getId());
                }

                titlePrefix += order.getGroupName();
            }

            StartVO startVO = new StartVO()
                    .setBusinessKey(order.getId())
                    .setSolutionCode(solutionCode)
                    .setTitle(titlePrefix + order.getOrderName())
                    .setVariables(variables);
            workflowForwardService.startSolution(startVO);
        }

        return true;
    }

    /**
     * @see BuWorkOrderService#startOrderWorkflowBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean startOrderWorkflowBatch(String ids) throws Exception {
        List<String> orderIdList = Arrays.asList(ids.split(","));

        if (CollectionUtils.isNotEmpty(orderIdList)) {
            List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdList);

            for (BuWorkOrder order : orderList) {
                // 启动工单流程
                order.setStartFlow(true);
                startOrderWorkflow(order);
            }
        }

        return true;
    }

    /**
     * @see BuWorkOrderService#setOrderStatusWorking(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setOrderStatusWorking(String ids) throws Exception {
        Date now = new Date();

        List<String> orderIdList = Arrays.asList(ids.split(","));
        // 设置工单状态填报中、作业中
        LambdaQueryWrapper<BuWorkOrder> orderWrapper = new LambdaQueryWrapper<BuWorkOrder>()
                .in(BuWorkOrder::getId, orderIdList);
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectList(orderWrapper);
        for (BuWorkOrder order : orderList) {
            order.setOrderStatus(8)
                    .setWorkStatus(1);
            if (null == order.getActStart()) {
                order.setActStart(now);
            }
            buWorkOrderMapper.updateById(order);

            // 工单状态变成8填报中时，删除工单没有确认的退料单，并将对应的工单物料是否生成单据的状态改成未生成
            clearUnConfirmMaterialReturn(order.getId());
        }

        // 设置工单任务状态已开始（未开始的不设置）
        LambdaQueryWrapper<BuWorkOrderTask> orderTaskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                .in(BuWorkOrderTask::getOrderId, orderIdList)
                .ne(BuWorkOrderTask::getTaskStatus, 0);
        BuWorkOrderTask orderTask = new BuWorkOrderTask()
                .setTaskStatus(1);
        buWorkOrderTaskMapper.update(orderTask, orderTaskWrapper);

        return true;
    }

    /**
     * @see BuWorkOrderService#startOrder(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean startOrder(String orderId) throws Exception {
        Date now = new Date();

        BuWorkOrder order = new BuWorkOrder()
                .setId(orderId)
                .setWorkStatus(1)
                .setActStart(now);
        buWorkOrderMapper.updateById(order);

        return true;
    }

    /**
     * @see BuWorkOrderService#submitTempMaterialApply(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitTempMaterialApply(String orderId) throws Exception {
        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        BuWorkOrder order = buWorkOrderMapper.selectOrderById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }

        // 查询未生成单据的、临时领料的、工单物料
        List<BuWorkOrderMaterial> notGenOrderTempApplyOrderMaterialList = buWorkOrderMaterialMapper.selectNotGenOrderListByOrderIdAndOpType(orderId, 2);
        if (CollectionUtils.isEmpty(notGenOrderTempApplyOrderMaterialList)) {
            throw new JeecgBootException("没有可申请的物料信息，请添加后提交");
        }

        List<BuMaterialApplyDetail> needAddApplyDetailList = new ArrayList<>();
        // 临时新增
        BuMaterialApply apply = getOrNewApplyOrder(order, now, sysUser.getId(), 3);
        // 生成领用明细
        transToApplyDetailList(needAddApplyDetailList, notGenOrderTempApplyOrderMaterialList, apply.getId());
        // 保存领用明细
        if (CollectionUtils.isNotEmpty(needAddApplyDetailList)) {
            buMaterialApplyDetailDispatchMapper.insertList(needAddApplyDetailList);
        }

        // 设置工单物料为已生成单据
        Set<String> notGenOrderOrderMaterialIdSet = notGenOrderTempApplyOrderMaterialList.stream()
                .map(BuWorkOrderMaterial::getId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(notGenOrderOrderMaterialIdSet)) {
            LambdaQueryWrapper<BuWorkOrderMaterial> orderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                    .in(BuWorkOrderMaterial::getId, notGenOrderOrderMaterialIdSet);
            BuWorkOrderMaterial orderMaterial = new BuWorkOrderMaterial()
                    .setIsGenOrder(1);
            buWorkOrderMaterialMapper.update(orderMaterial, orderMaterialWrapper);
        }
        // 修改工单状态为已核实
        order.setOrderStatus(2);
        buWorkOrderMapper.updateById(order);

        // 提交工单当前流程的第一个任务
        Map<String, Object> vars = new HashMap<>();
        vars.put("writeSubmit", 2);
        workflowForwardService.completeOrderFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "临时领料提交", vars);

        return true;
    }

    /**
     * @see BuWorkOrderService#listOrderMaterialApply(String, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialApply> listOrderMaterialApply(String orderId, Integer applyType) throws Exception {
        return buMaterialApplyDispatchMapper.selectListByOrderIdAndType(orderId, applyType);
    }

    /**
     * @see BuWorkOrderService#listOrderMaterialReturn(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialReturned> listOrderMaterialReturn(String orderId) throws Exception {
        List<BuMaterialReturned> returnedList = buMaterialReturnedDispatchMapper.selectListByOrderId(orderId);
        if (CollectionUtils.isEmpty(returnedList)) {
            for (BuMaterialReturned returned : returnedList) {
                List<BuMaterialReturnedDetail> returnedDetailList = returned.getDetailList();
                if (CollectionUtils.isNotEmpty(returnedDetailList)) {
                    for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
                        returnedDetail.setSourceLocationName(TirosUtil.extractSourceLocationName(returnedDetail.getLocationWbs(), returnedDetail.getLocationName()));
                    }
                }
                returned.setDetailList(returnedDetailList);
            }
        }

        return returnedList;
    }

    /**
     * @see BuWorkOrderService#getGroupLastSafeAssume(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String getGroupLastSafeAssume(String groupId) throws Exception {
        Map<String, String> safeAssume = buWorkOrderTaskMapper.selectLastSafeAssumeByGroupId(groupId);
        if (null != safeAssume) {
            return safeAssume.get("content");
        } else {
            return "";
        }
    }

    /**
     * @see BuWorkOrderService#getTaskQRCode(BuWorkOrderRecordCreateTaskQRCodeVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, String> getTaskQRCode(BuWorkOrderRecordCreateTaskQRCodeVO workOrderRecordCreateTaskQRCodeVO) throws Exception {
        String key = createTaskQRCodeKey(workOrderRecordCreateTaskQRCodeVO);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        workOrderRecordCreateTaskQRCodeVO.setFormUserId(sysUser.getId());
        String voJsonStr = redisTemplate.opsForValue().get(CommonConstant.PREFIX_TASK_QRCODE + key);
        String uuid;
        if (voJsonStr != null) { //redis中有数据直接读取
            BuWorkOrderRecordCreateTaskQRCodeVO vo = JSON.parseObject(voJsonStr, BuWorkOrderRecordCreateTaskQRCodeVO.class);
            uuid = vo.getUUID();
        } else {
            uuid = UUIDGenerator.generate();
        }

        //生成二维码图片 {
        //  targetType: 'TASK',  //二维码表示的对象类型 TASK 作业记录表明细任务， PSON 个人二维码
        //  fromBy: 'PC', // 请求显示二维码的所属平台，PC 表示在浏览器WEB端显示， APP 表示来自手机APP展示
        //  id：'xxxxxxxxxx', // 二维码在后端的唯一标示
        //}

        // String qRCodeContent = String.format("{\"uuid\":\"%s\",\"type\":\"%s\",\"qrcodeType\":\"6\"}", key, workOrderRecordCreateTaskQRCodeVO.getCheckType());
        String qRCodeContent = String.format("{\"targetType\":\"%s\",\"fromBy\":\"%s\",\"id\":\"%s\"}", workOrderRecordCreateTaskQRCodeVO.getTargetType(), workOrderRecordCreateTaskQRCodeVO.getFromBy(), uuid);
        BufferedImage bufferedImage = QrCodeUtil.generate(qRCodeContent, 300, 300);

        //二维码图片转base64
        String res = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", outputStream);
            BASE64Encoder encoder = new BASE64Encoder();
            String base64Img = encoder.encodeBuffer(outputStream.toByteArray());
            res = "data:image/jpg;base64," + base64Img;
            res = res.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (voJsonStr == null) {
            workOrderRecordCreateTaskQRCodeVO.setUUID(uuid);
            workOrderRecordCreateTaskQRCodeVO.setIsScan(0);//未扫描

            // 缓存到redis，key为参数md5加密，值为各参数
            redisTemplate.opsForValue().set(CommonConstant.PREFIX_TASK_QRCODE + key, JSON.toJSONString(workOrderRecordCreateTaskQRCodeVO));
            redisTemplate.expire(CommonConstant.PREFIX_TASK_QRCODE + key, 5, TimeUnit.MINUTES);

            // 把key缓存到redis,用来轮询
            redisTemplate.opsForValue().set(CommonConstant.PREFIX_TASK_QRCODE + uuid, key);
            redisTemplate.expire(CommonConstant.PREFIX_TASK_QRCODE + uuid, 5, TimeUnit.MINUTES);
        }
        Map<String, String> map = Maps.newHashMap();
        map.put("base64", res);
        map.put("uuid", uuid);
        return map;
    }

    /**
     * @see BuWorkOrderService#getTaskQRCodeValid(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Boolean getTaskQRCodeValid(String uuid) {
        String key = redisTemplate.opsForValue().get(CommonConstant.PREFIX_TASK_QRCODE + uuid);
        return StringUtils.isNotBlank(key);
    }

    /**
     * @see BuWorkOrderService#getTaskQRCodeIsScan(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Boolean getTaskQRCodeIsScan(String uuid) {
        String key = redisTemplate.opsForValue().get(CommonConstant.PREFIX_TASK_QRCODE + uuid);

        if (StringUtils.isNotBlank(key)) {
            //判断是否被扫描
            return redisTemplate.opsForValue().get(CommonConstant.PREFIX_TASK_ISSCAN_QRCODE + key) != null;
        } else {
            throw new JeecgBootException("二维码已失效");
        }
    }

    /**
     * @see BuWorkOrderService#suspendOrderBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean suspendOrderBatch(String ids) {
        Date now = new Date();

        List<String> orderIdList = Arrays.asList(ids.split(","));
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdList);

        List<String> needDeleteMaterialActOrderIdList = new ArrayList<>();
        List<String> needSuspendPlanTaskOrderIdList = new ArrayList<>();
        for (BuWorkOrder order : orderList) {
            String orderId = order.getId();
            Integer orderStatus = order.getOrderStatus();

            if (orderStatus == 3) {
                throw new JeecgBootException("工单" + order.getOrderCode() + "已提交，不能暂停");
            }
            if (orderStatus == 4) {
                throw new JeecgBootException("工单" + order.getOrderCode() + "已关闭，不能暂停");
            }
            if (orderStatus == 5) {
                throw new JeecgBootException("工单" + order.getOrderCode() + "已暂停，不能暂停");
            }
            if (orderStatus == 9) {
                throw new JeecgBootException("工单" + order.getOrderCode() + "已取消，不能暂停");
            }

            // 暂停工单
            buWorkOrderMapper.updateSuspend(orderId, order.getOrderStatus(), now, true);
            // 暂停流程
            String processInstanceId = buWorkOrderMapper.selectProcessInstanceId(orderId);
            if (StringUtils.isNotBlank(processInstanceId)) {
                workflowForwardService.suspendInstance(processInstanceId);
            }

            if (!MATERIAL_ORDER_TYPE.contains(order.getOrderType())) {
                needDeleteMaterialActOrderIdList.add(orderId);
                needSuspendPlanTaskOrderIdList.add(orderId);
            }
        }

        // 清除已填写的实际物料消耗记录（有可能激活后，原来填写的批次已经没有了）
        if (CollectionUtils.isNotEmpty(needDeleteMaterialActOrderIdList)) {
            buWorkOrderMaterialActsMapper.deleteByOrderIdList(needDeleteMaterialActOrderIdList);
            LambdaQueryWrapper<BuWorkOrderMaterial> orderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                    .in(BuWorkOrderMaterial::getOrderId, needDeleteMaterialActOrderIdList);
            BuWorkOrderMaterial zeroActAmount = new BuWorkOrderMaterial()
                    .setActAmount(0D);
            buWorkOrderMaterialMapper.update(zeroActAmount, orderMaterialWrapper);
        }

        // 工单暂停时将相应的列计划任务暂停
        if (CollectionUtils.isNotEmpty(needSuspendPlanTaskOrderIdList)) {
            // 未开始的才需要暂停
            List<BuRepairPlanTask> planTaskList = buRepairPlanTaskMapper.selectNeedSuspendListByOrderIdListAndPlanTaskStatus(needSuspendPlanTaskOrderIdList, 1);
            if (CollectionUtils.isNotEmpty(planTaskList)) {
                for (BuRepairPlanTask planTask : planTaskList) {
                    Integer status = planTask.getStatus();
                    planTask.setStatus(3)
                            .setSuspendStatus(status)
                            .setSuspendTime(now);
                }
                buRepairPlanTaskMapper.updateListSuspendActivity(planTaskList);
            }
        }

        return true;
    }

    /**
     * @see BuWorkOrderService#activityOrderBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean activityOrderBatch(String ids) {
        Date now = new Date();

        List<String> orderIdList = Arrays.asList(ids.split(","));
        List<Integer> needCheckGroupStockOrderStatusList = Arrays.asList(2, 3, 6, 7, 8);

        // 查询班组
        Map<String, BuMtrWorkshopGroup> idGroupMap = new HashMap<>();
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupDispatchMapper.selectList(Wrappers.emptyWrapper());
        groupList.forEach(group -> idGroupMap.put(group.getId(), group));

        List<String> needActivityPlanTaskOrderIdList = new ArrayList<>();

        List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdList);
        Map<String, List<BuWorkOrder>> groupIdOrderListMap = orderList.stream()
                .collect(Collectors.groupingBy(BuWorkOrder::getGroupId));
        for (Map.Entry<String, List<BuWorkOrder>> groupIdOrderListEntry : groupIdOrderListMap.entrySet()) {
            String groupId = groupIdOrderListEntry.getKey();
            List<BuWorkOrder> groupOrderList = groupIdOrderListEntry.getValue();

            // 查询班组库存
            LambdaQueryWrapper<BuMaterialGroupStock> groupStockWrapper = new LambdaQueryWrapper<BuMaterialGroupStock>()
                    .eq(BuMaterialGroupStock::getGroupId, groupId);
            List<BuMaterialGroupStock> groupStockList = buMaterialGroupStockDispatchMapper.selectList(groupStockWrapper);
            // 班组库存量减去“已核实”到“未关闭”的工单中的物料数量
            // 暂停状态的工单不需要减去：暂停时有可能原始状态为未核实不需要减去；重新激活时会回归原始状态根据原始状态计算
            List<BuWorkOrderMaterial> needUsedOrderMaterialList = buWorkOrderMaterialMapper.selectNotMaterialApplyOrderListByGroupIdAndOrderStatus(groupId, TirosConstant.GROUP_STOCK_NEED_COUNT_USED_ORDER_STATUS);

            List<BuWorkOrder> needCheckGroupStockOrderList = new ArrayList<>();
            List<String> needIgnoreOrderCodeList = new ArrayList<>();
            for (BuWorkOrder order : groupOrderList) {
                String orderId = order.getId();
                Integer suspendStatus = order.getSuspendStatus();

                // 激活工单
                buWorkOrderMapper.updateSuspend(orderId, order.getSuspendStatus(), now, false);
                // 激活流程
                String processInstanceId = buWorkOrderMapper.selectProcessInstanceId(orderId);
                if (StringUtils.isNotBlank(processInstanceId)) {
                    workflowForwardService.activateInstance(processInstanceId);
                }

                if (!MATERIAL_ORDER_TYPE.contains(order.getOrderType()) && needCheckGroupStockOrderStatusList.contains(suspendStatus)) {
                    needCheckGroupStockOrderList.add(order);
                }
                if (!MATERIAL_ORDER_TYPE.contains(order.getOrderType())) {
                    needActivityPlanTaskOrderIdList.add(order.getId());
                }
                needIgnoreOrderCodeList.add(order.getOrderCode());
            }

            // 如果原状态已核实及之后，检查班组库存够不够，不够了提示让先领料
            if (CollectionUtils.isNotEmpty(needCheckGroupStockOrderList)) {
                // 获得班组库存可用量
                deleteGroupStockUsedAmount(groupStockList, groupId, needUsedOrderMaterialList, null, needIgnoreOrderCodeList);

                // 查询需检查班组库存量的工单下的工单物料
                List<String> needCheckGroupStockOrderIdList = needCheckGroupStockOrderList.stream()
                        .map(BuWorkOrder::getId)
                        .collect(Collectors.toList());
                List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMapper.selectListByOrderIdList(needCheckGroupStockOrderIdList);
                if (CollectionUtils.isNotEmpty(orderMaterialList)) {
                    Map<String, List<BuWorkOrderMaterial>> materialTypeIdListMap = orderMaterialList.stream()
                            .collect(Collectors.groupingBy(BuWorkOrderMaterial::getMaterialTypeId));
                    for (Map.Entry<String, List<BuWorkOrderMaterial>> materialTypeIdListEntry : materialTypeIdListMap.entrySet()) {
                        String materialTypeId = materialTypeIdListEntry.getKey();
                        List<BuWorkOrderMaterial> list = materialTypeIdListEntry.getValue();

                        double sumAmount = list.stream()
                                .mapToDouble(BuWorkOrderMaterial::getAmount)
                                .sum();
                        double groupStockUsableAmount = groupStockList.stream()
                                .filter(groupStock -> materialTypeId.equals(groupStock.getMaterialTypeId()))
                                .map(BuMaterialGroupStock::getUsableAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .doubleValue();
                        if (sumAmount > groupStockUsableAmount) {
                            String groupName = idGroupMap.get(groupId).getGroupName();
                            StringBuilder materialNeedDetailBuilder = new StringBuilder();
                            for (BuWorkOrderMaterial orderMaterial : list) {
                                Double amount = orderMaterial.getAmount();
                                if (null == amount || amount <= 0) {
                                    continue;
                                }
                                materialNeedDetailBuilder.append(orderMaterial.getOrderCode()).append("需求").append(amount).append("、");
                            }
                            String materialNeedDetail = "";
                            if (materialNeedDetailBuilder.length() > 0) {
                                materialNeedDetail = materialNeedDetailBuilder.deleteCharAt(materialNeedDetailBuilder.length() - 1).toString();
                            }
                            String lackMessage = String.format("激活工单失败：%s物料%s库存不足，请先领料：需求数量%s（%s），班组库存可用量%s",
                                    groupName, materialTypeId, sumAmount, materialNeedDetail, groupStockUsableAmount);
                            throw new JeecgBootException(lackMessage);
                        }
                    }
                }
            }
        }

        // 工单激活时将相应的列计划任务激活
        if (CollectionUtils.isNotEmpty(needActivityPlanTaskOrderIdList)) {
            // 已暂停的才需要激活
            List<BuRepairPlanTask> planTaskList = buRepairPlanTaskMapper.selectNeedSuspendListByOrderIdListAndPlanTaskStatus(needActivityPlanTaskOrderIdList, 3);
            if (CollectionUtils.isNotEmpty(planTaskList)) {
                for (BuRepairPlanTask planTask : planTaskList) {
                    Integer suspendStatus = planTask.getSuspendStatus();
                    planTask.setStatus(suspendStatus)
                            .setSuspendStatus(null)
                            .setActiveTime(now);
                }
                buRepairPlanTaskMapper.updateListSuspendActivity(planTaskList);
            }
        }

        return true;
    }

    /**
     * @see BuWorkOrderService#cancelOrderWorkflowBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelOrderWorkflowBatch(String ids) throws Exception {
        Date now = new Date();

        List<String> orderIdList = Arrays.asList(ids.split(","));
        for (String orderId : orderIdList) {
            BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
            if (null == order) {
                continue;
            }

            // 删除流程
            String processInstanceId = buWorkOrderMapper.selectProcessInstanceId(orderId);
            if (StringUtils.isNotBlank(processInstanceId)) {
                workflowForwardService.deleteInstance(processInstanceId);
            }

            Integer orderStatus = order.getOrderStatus();
            Integer orderType = order.getOrderType();

            if (orderStatus == 4) {
                throw new JeecgBootException("已关闭的工单无法取消");
            }
            if (orderStatus == 5) {
                throw new JeecgBootException("已暂停的工单无法取消");
            }
            if (orderStatus == 9) {
                throw new JeecgBootException("已取消的工单无法取消");
            }

            if (MATERIAL_ORDER_TYPE.contains(orderType)) {
                if (orderStatus == 7 || orderStatus == 8) {
                    throw new JeecgBootException("已领料的发料工单无法取消");
                }

                // 查询领用明细
                List<BuMaterialApplyDetail> applyDetailList = buMaterialApplyDetailDispatchMapper.selectListByOrderId(orderId);
                // 查询分配明细
                List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailDispatchMapper.selectListByOrderId(orderId);

                // 占用量
                Map<String, BigDecimal> materialTypeIdUseAmountMap = new HashMap<>();
                if (orderStatus == 2) {
                    // 已核实的工单，已生成领用明细并增加了占用量，根据领用明细的申请数量减去占用量
                    for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                        String materialTypeId = applyDetail.getMaterialTypeId();
                        BigDecimal amount = applyDetail.getAmount();
                        if (BigDecimal.ZERO.compareTo(amount) != 0) {
                            BigDecimal oldUseAmount = materialTypeIdUseAmountMap.getOrDefault(materialTypeId, BigDecimal.ZERO);
                            materialTypeIdUseAmountMap.put(materialTypeId, oldUseAmount.add(amount));
                        }
                    }
                }
                if (orderStatus == 6) {
                    // 已发料的工单，已生成分配明细并修改了占用量，根据分配明细的分配数量减去占用量
                    for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                        String materialTypeId = assignDetail.getMaterialTypeId();
                        BigDecimal amount = null == assignDetail.getAmount() ? BigDecimal.ZERO : BigDecimal.valueOf(assignDetail.getAmount());
                        if (BigDecimal.ZERO.compareTo(amount) != 0) {
                            BigDecimal oldUseAmount = materialTypeIdUseAmountMap.getOrDefault(materialTypeId, BigDecimal.ZERO);
                            materialTypeIdUseAmountMap.put(materialTypeId, oldUseAmount.add(amount));
                        }
                    }
                }

                // 删除分配明细、领用明细、领用单
                buMaterialAssignDetailDispatchMapper.deleteByOrderId(orderId);
                buMaterialApplyDetailDispatchMapper.deleteByOrderId(orderId);
                buMaterialApplyDispatchMapper.deleteByOrderId(orderId);
            }

            // 更新工单为取消状态
            buWorkOrderMapper.updateById(new BuWorkOrder().setId(orderId).setOrderStatus(9));
            productionNoticeService.refreshProgressByOrder(orderId, order.getTrainNo());
            // 删除流程状态
            wfBizStatusService.removeById(orderId);
        }

        // 删除工单物料、实际消耗、工时
        if (CollectionUtils.isNotEmpty(orderIdList)) {
            buWorkOrderMaterialActsMapper.deleteByOrderIdList(orderIdList);
            buWorkOrderMaterialMapper.deleteByOrderIdList(orderIdList);
            buRepairTaskStaffArrangeMapper.deleteByOrderIdList(orderIdList);
        }

        return true;
    }

    @Override
    public IPage<BuWorkOrder> editWorkOrderOperator(IPage<BuWorkOrder> page) {
        List<BuWorkOrder> records = page.getRecords();
        records.forEach(item -> {
            Integer operator = 0;
            if (item.getOrderStatus() == 8 && item.getWorkStatus() != 2) {
                operator = 7;
            } else {
                if (!item.getWfAttrKey().equals("-1")) {
                    switch (item.getWfAttrKey()) {
                        case "isVerify":
                            operator = 1;
                            break;
                        case "isMaterial":
                            operator = 2;
                            break;
                        case "isConfirm":
                            operator = 3;
                            break;
                        case "isSubmit":
                            operator = 4;
                            break;
                        case "isCheck":
                            operator = 6;
                            break;
                        case "isClose":
                            operator = 5;
                            break;
                        default:
                            break;
                    }
                }
            }
            item.setOperator(operator);
        });
        page.setRecords(records);

        return page;
    }


    private void setApplyOrderProperties(List<BuWorkOrder> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        List<BuWorkOrder> applyOrderList = orderList.stream()
                .filter(order -> MATERIAL_ORDER_TYPE.contains(order.getOrderType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(applyOrderList)) {
            return;
        }

        // 查询发料工单的对应领用单
        List<String> applyOrderIdList = applyOrderList.stream()
                .map(BuWorkOrder::getId)
                .collect(Collectors.toList());
        List<BuMaterialApply> applyList = new ArrayList<>();
        List<List<String>> applyOrderIdBatchSubList = DatabaseBatchSubUtil.batchSubList(applyOrderIdList);
        for (List<String> applyOrderIdBatchSub : applyOrderIdBatchSubList) {
            LambdaQueryWrapper<BuMaterialApply> applyWrapper = new LambdaQueryWrapper<BuMaterialApply>()
                    .in(BuMaterialApply::getWorkOrderId, applyOrderIdBatchSub);
            List<BuMaterialApply> subApplyList = buMaterialApplyDispatchMapper.selectList(applyWrapper);
            applyList.addAll(subApplyList);
        }

        for (BuWorkOrder order : applyOrderList) {
            String orderId = order.getId();
            List<BuMaterialApply> matchApplyList = applyList.stream()
                    .filter(apply -> orderId.equals(apply.getWorkOrderId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(matchApplyList)) {
                continue;
            }

            for (BuMaterialApply apply : matchApplyList) {
                Date syncTime = apply.getSyncTime();
                String syncResult = apply.getSyncResult();
                Date syncResultTime = apply.getSyncResultTime();

                order.setSyncTime(syncTime)
                        .setSyncResult(syncResult)
                        .setSyncResultTime(syncResultTime);
            }
        }
    }

    private void setWorkstationStaffs(WorkOrderRelevanceInfo relevanceInfo) {
        List<BuWorkOrderTaskWorkstation> workstationList = relevanceInfo.getWorkstations();
        if (CollectionUtils.isEmpty(workstationList)) {
            return;
        }

        String orderId = relevanceInfo.getId();
        List<BuRepairTaskStaffArrange> staffArrangeList = buRepairTaskStaffArrangeMapper.selectListByOrderId(orderId);
        if (CollectionUtils.isEmpty(staffArrangeList)) {
            return;
        }

        for (BuWorkOrderTaskWorkstation taskWorkstation : workstationList) {
            String taskWorkstationId = taskWorkstation.getId();
            List<BuRepairTaskStaffArrange> taskWorkstationStaffArrangeList = staffArrangeList.stream()
                    .filter(staffArrange -> taskWorkstationId.equals(staffArrange.getOrdTskWkStationId()))
                    .collect(Collectors.toList());
            taskWorkstation.setStaffArrangeList(taskWorkstationStaffArrangeList);

            List<String> staffUserIds = taskWorkstationStaffArrangeList.stream()
                    .map(BuRepairTaskStaffArrange::getUserId)
                    .collect(Collectors.toList());
            String personNames = taskWorkstationStaffArrangeList.stream()
                    .map(BuRepairTaskStaffArrange::getUserName)
                    .collect(Collectors.joining(","));
            taskWorkstation.setStaffUserIds(staffUserIds);
            taskWorkstation.setPersonNames(personNames);
        }
    }

    private void setAnnexFieldProperties(WorkOrderRelevanceInfo relevanceInfo) {
        if (null == relevanceInfo) {
            return;
        }
        List<BuWorkOrderAnnex> annexList = relevanceInfo.getAnnexList();
        if (CollectionUtils.isEmpty(annexList)) {
            return;
        }

        Map<String, String> taskIdNameMap = new HashMap<>();
        List<BuWorkOrderTask> taskList = relevanceInfo.getTasks();
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskList.forEach(task -> taskIdNameMap.put(task.getId(), task.getTaskName()));
        }

        String orderName = relevanceInfo.getOrderName();
        for (BuWorkOrderAnnex annex : annexList) {
            annex.setWorkOrderName(orderName)
                    .setTaskName(taskIdNameMap.get(annex.getTaskId()));
        }
    }


    private void setMaterialCanReplace(WorkOrderRelevanceInfo relevanceInfo) {
        List<BuWorkOrderMaterial> materials = relevanceInfo.getMaterials();
        if (CollectionUtils.isNotEmpty(materials)) {
            materials.forEach(item -> {
                List<BuMaterialMustList> materialMustList = buMaterialMustListDispatchMapper.selectList(Wrappers.<BuMaterialMustList>lambdaQuery()
                        .eq(BuMaterialMustList::getLineId, relevanceInfo.getLineId())
                        .eq(BuMaterialMustList::getGroupId, relevanceInfo.getGroupId())
                        .eq(BuMaterialMustList::getRepairProgramId, relevanceInfo.getRepairProgramId())
                        .eq(BuMaterialMustList::getMaterialTypeId, item.getMaterialTypeId()));
                if (CollectionUtils.isNotEmpty(materialMustList)) {
                    String canReplace = materialMustList.get(0).getCanReplace();
                    if (StringUtils.isNotBlank(canReplace)) {
                        item.setCanReplace(canReplace);
                        String spec = buMaterialMustListDispatchMapper.selectMaterialSpec(canReplace);
                        item.setCanReplaceSpec(spec);
                    }
                }
            });
        }
    }

    private void setEquipmentToTask(WorkOrderRelevanceInfo relevanceInfo) {
        List<BuWorkOrderTaskEqu> equipments = relevanceInfo.getEquipments();
        setEquipmentLocationName(equipments);
        List<BuWorkOrderTask> tasks = relevanceInfo.getTasks();
        if (CollectionUtils.isNotEmpty(equipments) && CollectionUtils.isNotEmpty(tasks)) {
            for (BuWorkOrderTask task : tasks) {
                String taskId = task.getId();
                List<BuWorkOrderTaskEqu> taskEquipmentList = equipments.stream()
                        .filter(equipment -> taskId.equals(equipment.getOrderTaskId()))
                        .collect(Collectors.toList());
                task.setEquipments(taskEquipmentList);
            }
        }
    }

    private void setEquipmentLocationName(List<BuWorkOrderTaskEqu> equipmentList) {
        if (CollectionUtils.isEmpty(equipmentList)) {
            return;
        }

        // 查询位置名称
        Map<String, String> locationCodeNameMap = new HashMap<>();
        List<String> locationCodeList = equipmentList.stream()
                .map(BuWorkOrderTaskEqu::getLocationCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(locationCodeList)) {
            List<List<String>> locationCodeBatchSubList = DatabaseBatchSubUtil.batchSubList(locationCodeList);
            for (List<String> locationCodeBatchSub : locationCodeBatchSubList) {
                LambdaQueryWrapper<BuMaximoLocation> locationWrapper = new LambdaQueryWrapper<BuMaximoLocation>()
                        .in(BuMaximoLocation::getCode, locationCodeBatchSub);
                List<BuMaximoLocation> subLocationList = buMaximoLocationDispatchMapper.selectList(locationWrapper);
                subLocationList.forEach(location -> locationCodeNameMap.put(location.getCode(), location.getName()));
            }
        }
        // 设置位置名称
        for (BuWorkOrderTaskEqu equipment : equipmentList) {
            equipment.setLocationName(locationCodeNameMap.get(equipment.getLocationCode()));
        }
    }

    private void setOrderProperties(BuWorkOrder order) {
        // 设置班长
        String groupId = order.getGroupId();
        BuMtrWorkshopGroup group = buMtrWorkshopGroupDispatchMapper.selectById(groupId);
        // 工单的班长字段默认为空
        String monitor = null;
        if (null != group) {
            if (StringUtils.isNotBlank(group.getMonitor())) {
                monitor = group.getMonitor();
            } else if (StringUtils.isNotBlank(group.getMonitor2())) {
                monitor = group.getMonitor2();
            }
        }
        order.setMonitor(monitor);

        // 设置工期
        Date startTime = order.getStartTime();
        Date finishTime = order.getFinishTime();
        if (DateUtils.isSameDay(startTime, finishTime)) {
            // 计划开始和计划结束在同一天，工期=1
            order.setDuration(1);
        } else {
            // 计划开始和计划结束不在同一天，工期=计算不是休息日的天数
            int orderYear = order.getStartTime().getYear() + 1900;
            SpecialHoliday specialHoliday = holidayService.getSpecialHoliday(orderYear);
            Set<Date> specialHolidayDaySet = specialHoliday.getSpecialHolidayDaySet();

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startTime);
            Calendar finishCalendar = Calendar.getInstance();
            finishCalendar.setTime(finishTime);

            int duration = 0;
            while (!finishCalendar.before(startCalendar)) {
                boolean isWeekend = DateUtils.isWeekend(startCalendar);
                boolean isSpecialHoliday = DateUtils.containsDay(specialHolidayDaySet, startCalendar.getTime());

                if (!isWeekend && !isSpecialHoliday) {
                    duration++;
                }
                startCalendar.add(Calendar.DATE, 1);
            }
            order.setDuration(duration);
        }
    }

    private void setLocationWarehouseNamesAndPalletNames(List<BuWorkOrderMaterial> orderMaterialList) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        Set<String> orderMaterialIdSet = orderMaterialList.stream()
                .map(BuWorkOrderMaterial::getId)
                .collect(Collectors.toSet());
        List<BuMaterialApplyDetail> allApplyDetailList = buMaterialApplyDetailDispatchMapper.selectListWithAssignByOrderMaterialIdList(new ArrayList<>(orderMaterialIdSet));
        if (CollectionUtils.isEmpty(allApplyDetailList)) {
            return;
        }

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            List<String> sourceLocationNameList = new ArrayList<>();
            List<String> palletNameList = new ArrayList<>();
            List<String> sourceLocationAndPalletNameList = new ArrayList<>();
            List<BuMaterialAssignDetail> allAssignDetailList = new ArrayList<>();

            List<BuMaterialApplyDetail> applyDetailList = allApplyDetailList.stream()
                    .filter(applyDetail -> orderMaterial.getId().equals(applyDetail.getOrderMaterialId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(applyDetailList)) {
                continue;
            }

            BigDecimal issueAmount = BigDecimal.ZERO;
            BigDecimal assignAmount = BigDecimal.ZERO;
            for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                issueAmount = issueAmount.add(applyDetail.getReceive());

                List<BuMaterialAssignDetail> assignDetailList = applyDetail.getAssignDetailList();
                if (CollectionUtils.isEmpty(assignDetailList)) {
                    continue;
                }

                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    assignAmount = assignAmount.add(BigDecimal.valueOf(assignDetail.getAmount()));

                    String sourceLocationName = TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName());
                    String palletName = assignDetail.getPalletName();
                    if (!sourceLocationNameList.contains(sourceLocationName)) {
                        sourceLocationNameList.add(sourceLocationName);
                    }
                    if (StringUtils.isNotBlank(palletName)) {
                        if (!palletNameList.contains(palletName)) {
                            palletNameList.add(palletName);
                        }
                    }
                    sourceLocationAndPalletNameList.add(sourceLocationName + " | " + assignDetail.getAmount() + " | " + (StringUtils.isNotBlank(palletName) ? palletName : ""));
                    allAssignDetailList.add(assignDetail);
                }
            }

            orderMaterial.setSourceLocationName(String.join(", ", sourceLocationNameList))
                    .setPalletName(String.join(", ", palletNameList))
                    .setSourceLocationAndPalletName(String.join(", ", sourceLocationAndPalletNameList))
                    .setIssueAmount(issueAmount)
                    .setAssignAmount(assignAmount)
                    .setAssignDetailList(allAssignDetailList);
        }
    }

    private void deleteWorkOrderRelevanceInfo(List<String> orderIdList) {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }

        // 删除任务关联信息
        buWorkOrderTechFileMapper.delete(new LambdaQueryWrapper<BuWorkOrderTechFile>()
                .in(BuWorkOrderTechFile::getOrderId, orderIdList));
        buWorkOrderBookStepMapper.delete(new LambdaQueryWrapper<BuWorkOrderBookStep>()
                .in(BuWorkOrderBookStep::getWorkOrderId, orderIdList));
        buWorkOrderToolMapper.delete(new LambdaQueryWrapper<BuWorkOrderTool>()
                .in(BuWorkOrderTool::getOrderId, orderIdList));
        buWorkOrderMaterialMapper.delete(new LambdaQueryWrapper<BuWorkOrderMaterial>()
                .in(BuWorkOrderMaterial::getOrderId, orderIdList));
        buWorkOrderTaskWorkstationMapper.delete(new LambdaQueryWrapper<BuWorkOrderTaskWorkstation>()
                .in(BuWorkOrderTaskWorkstation::getOrderId, orderIdList));
        buRepairTaskStaffArrangeMapper.delete(new LambdaQueryWrapper<BuRepairTaskStaffArrange>()
                .in(BuRepairTaskStaffArrange::getOrderId, orderIdList));
        buWorkOrderTaskEquMapper.delete(new LambdaQueryWrapper<BuWorkOrderTaskEqu>()
                .in(BuWorkOrderTaskEqu::getOrderId, orderIdList));

        // 删除任务
        buWorkOrderTaskMapper.delete(new LambdaQueryWrapper<BuWorkOrderTask>()
                .in(BuWorkOrderTask::getOrderId, orderIdList));

        // 删除工单附件
        buWorkOrderAnnexMapper.delete(new LambdaQueryWrapper<BuWorkOrderAnnex>()
                .in(BuWorkOrderAnnex::getWorkOrderId, orderIdList));
    }

    private void insertWorkOrderRelevanceInfo(BuWorkOrder order, boolean arrangeFromStaffUserIds) {
        String orderId = order.getId();
        Integer maxTaskNo = buWorkOrderMapper.selectMaxTaskNo(orderId);
        int taskNo;
        if (maxTaskNo == null) {
            taskNo = 1;
        } else {
            taskNo = maxTaskNo + 1;
        }

        // 工单任务工时
        Map<String, Integer> taskIdWorkTimeMap = new HashMap<>();
        List<BuWorkOrderTaskEqu> equipments = new ArrayList<>();

        List<BuWorkOrderTask> tasks = order.getTasks();
        if (CollectionUtils.isNotEmpty(tasks)) {
            Integer safeNoticeDataLength = buWorkOrderTaskMapper.selectSafeNoticeDataLength();

            for (BuWorkOrderTask task : tasks) {
                task.setTaskNo(taskNo);
                task.setOrderId(orderId);
                if (null == task.getTaskStatus()) {
                    task.setTaskStatus(0);
                }

                // 检查字段长度
                LengthCheckUtil.maxLength(task.getSafeNotice(), "安全预想", safeNoticeDataLength);

                buWorkOrderTaskMapper.insert(task);
                taskNo++;
                taskIdWorkTimeMap.put(task.getId(), task.getWorkTime());

                List<BuWorkOrderTaskEqu> taskEquipments = task.getEquipments();
                if (CollectionUtils.isNotEmpty(taskEquipments)) {
                    equipments.addAll(taskEquipments);
                }
            }

            // 如果是计划任务，设置对应的列计划任务为已生成工单
            List<String> planTaskIdList = tasks.stream()
                    .filter(task -> 1 == task.getTaskType())
                    .map(BuWorkOrderTask::getTaskObjId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(planTaskIdList)) {
                LambdaQueryWrapper<BuRepairPlanTask> planTaskWrapper = new LambdaQueryWrapper<BuRepairPlanTask>()
                        .in(BuRepairPlanTask::getId, planTaskIdList);
                BuRepairPlanTask planTask = new BuRepairPlanTask()
                        .setHasGen(1);
                buRepairPlanTaskMapper.update(planTask, planTaskWrapper);
            }
        }

        // 保存关联数据
        List<BuWorkOrderMaterial> materialList = order.getMaterials();
        if (CollectionUtils.isNotEmpty(materialList)) {
            for (BuWorkOrderMaterial material : materialList) {
                material.setOrderId(orderId);
                if (StringUtils.isBlank(material.getId())) {
                    material.setId(UUIDGenerator.generate());
                }
                if (null == material.getActAmount()) {
                    material.setActAmount(0D);
                }
                if (null == material.getIsVerify()) {
                    material.setIsVerify(0);
                }
                if (null == material.getOpType()) {
                    material.setOpType(1);
                }
                if (null == material.getIsGenOrder()) {
                    material.setIsGenOrder(0);
                }
            }
            buWorkOrderMaterialMapper.insertList(materialList);
        }

        List<BuWorkOrderTechFile> techFileList = order.getTechFiles();
        if (CollectionUtils.isNotEmpty(techFileList)) {
            for (BuWorkOrderTechFile techFile : techFileList) {
                techFile.setOrderId(orderId);
                if (StringUtils.isBlank(techFile.getId())) {
                    techFile.setId(UUIDGenerator.generate());
                }
            }
            buWorkOrderTechFileMapper.insertList(techFileList);
        }

        List<BuWorkOrderBookStep> bookSteps = order.getBookSteps();
        if (CollectionUtils.isNotEmpty(bookSteps)) {
            List<BuWorkOrderBookStep> orderTaskBookSteps = bookSteps.stream()
                    .filter(bookStep -> null != bookStep.getBelongOrderTask() && 1 == bookStep.getBelongOrderTask())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(orderTaskBookSteps)) {
                for (BuWorkOrderBookStep bookStep : orderTaskBookSteps) {
                    bookStep.setWorkOrderId(orderId);
                    if (StringUtils.isBlank(bookStep.getId())) {
                        bookStep.setId(UUIDGenerator.generate());
                    }
                }
                buWorkOrderBookStepMapper.insertList(orderTaskBookSteps);
            }
        }

        List<BuWorkOrderTool> toolList = order.getTools();
        if (CollectionUtils.isNotEmpty(toolList)) {
            for (BuWorkOrderTool tool : toolList) {
                tool.setOrderId(orderId);
                if (StringUtils.isBlank(tool.getId())) {
                    tool.setId(UUIDGenerator.generate());
                }
                if (StringUtils.isBlank(tool.getToolId())) {
                    tool.setToolId("-1");
                }
            }
            buWorkOrderToolMapper.insertList(toolList);
        }

        List<BuWorkOrderAnnex> annexList = order.getAnnexList();
        if (CollectionUtils.isNotEmpty(annexList)) {
            for (BuWorkOrderAnnex annex : annexList) {
                annex.setWorkOrderId(orderId);
                if (StringUtils.isBlank(annex.getId())) {
                    annex.setId(UUIDGenerator.generate());
                }
            }
            buWorkOrderAnnexMapper.insertList(annexList);
        }

        if (CollectionUtils.isNotEmpty(equipments)) {
            for (BuWorkOrderTaskEqu equipment : equipments) {
                equipment.setOrderId(orderId);
                if (StringUtils.isBlank(equipment.getId())) {
                    equipment.setId(UUIDGenerator.generate());
                }
            }
            buWorkOrderTaskEquMapper.insertList(equipments);
        }

        insertWorkstations(order.getWorkstations(), orderId, taskIdWorkTimeMap, arrangeFromStaffUserIds);
    }

    private void insertWorkstations(List<BuWorkOrderTaskWorkstation> workstations,
                                    String orderId,
                                    Map<String, Integer> taskIdWorkTimeMap,
                                    boolean arrangeFromStaffUserIds) {
        if (CollectionUtils.isEmpty(workstations)) {
            return;
        }

        List<BuWorkOrderTaskWorkstation> needAddWorkstationList = new ArrayList<>();
        List<BuRepairTaskStaffArrange> needAddStaffArrangeList = new ArrayList<>();
        for (BuWorkOrderTaskWorkstation taskWorkstation : workstations) {
            Integer taskWorkTime = taskIdWorkTimeMap.getOrDefault(taskWorkstation.getOrderTaskId(), 0);
            taskWorkstation.setOrderId(orderId);
            if (null == taskWorkstation.getWorkTime()) {
                taskWorkstation.setWorkTime(taskWorkTime);
            }
            if (StringUtils.isBlank(taskWorkstation.getId())) {
                taskWorkstation.setId(UUIDGenerator.generate());
            }
            needAddWorkstationList.add(taskWorkstation);

            if (arrangeFromStaffUserIds) {
                List<String> staffUserIds = taskWorkstation.getStaffUserIds();
                if (CollectionUtils.isEmpty(staffUserIds)) {
                    continue;
                }
                String taskWorkstationId = taskWorkstation.getId();
                for (String userId : staffUserIds) {
                    BuRepairTaskStaffArrange staffArrange = new BuRepairTaskStaffArrange()
                            .setId(UUIDGenerator.generate())
                            .setOrderId(orderId)
                            .setOrdTskWkStationId(taskWorkstationId)
                            .setUserId(userId)
                            .setWorkTime(BigDecimal.ZERO);
                    needAddStaffArrangeList.add(staffArrange);
                }
            } else {
                List<BuRepairTaskStaffArrange> staffArrangeList = taskWorkstation.getStaffArrangeList();
                if (CollectionUtils.isNotEmpty(staffArrangeList)) {
                    for (BuRepairTaskStaffArrange staffArrange : staffArrangeList) {
                        staffArrange.setOrderId(taskWorkstation.getOrderId())
                                .setOrdTskWkStationId(taskWorkstation.getId());

                        if (null == staffArrange.getWorkTime()) {
                            staffArrange.setWorkTime(BigDecimal.ZERO);
                        }
                        if (StringUtils.isBlank(staffArrange.getId())) {
                            staffArrange.setId(UUIDGenerator.generate());
                        }
                        needAddStaffArrangeList.add(staffArrange);
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(needAddWorkstationList)) {
            buWorkOrderTaskWorkstationMapper.insertList(needAddWorkstationList);
        }
        if (CollectionUtils.isNotEmpty(needAddStaffArrangeList)) {
            buRepairTaskStaffArrangeMapper.insertList(needAddStaffArrangeList);
        }
    }

    private void insertFormInstByPlanTaskAndWorkstationRef(List<BuWorkOrder> orderList, List<BuWorkOrderTask> orderTaskList) {
        if (CollectionUtils.isEmpty(orderTaskList)) {
            return;
        }

        List<String> orderIdList = new ArrayList<>();
        List<String> planIdList = new ArrayList<>();
        Map<String, BuWorkOrder> idOrderMap = new HashMap<>();
        for (BuWorkOrder order : orderList) {
            if (StringUtils.isNotBlank(order.getId()) && !orderIdList.contains(order.getId())) {
                orderIdList.add(order.getId());
            }
            if (StringUtils.isNotBlank(order.getPlanId()) && !planIdList.contains(order.getPlanId())) {
                planIdList.add(order.getPlanId());
            }
            idOrderMap.put(order.getId(), order);
        }

        // 查询列计划任务和表单关联
        List<String> planTaskIdList = orderTaskList.stream()
                .map(BuWorkOrderTask::getTaskObjId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        List<BuRepairPlanTaskForms> planTaskFormList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(planTaskIdList)) {
            LambdaQueryWrapper<BuRepairPlanTaskForms> planTaskFormWrapper = new LambdaQueryWrapper<BuRepairPlanTaskForms>()
                    .in(BuRepairPlanTaskForms::getTaskId, planTaskIdList);
            planTaskFormList = buRepairPlanTaskFormsMapper.selectList(planTaskFormWrapper);
        }
        // 查询工单任务关联工位及工位关联表单
        List<BuWorkOrderTaskWorkstation> workstationList = buWorkOrderTaskWorkstationMapper.selectListAndRefFormListByOrderIdList(orderIdList);
        // 查询属于列计划的表单
        List<BuRepairPlanForms> planFormList = buRepairPlanFormsMapper.selectPlanFormsListWithChoiceByPlanIdList(planIdList);
        Map<String, BuRepairPlanForms> idPlanFormMap = planFormList.stream()
                .collect(Collectors.toMap(BuRepairPlanForms::getId, Function.identity()));

        // 关联表单实例和任务
        List<BuWorkOrderTaskFormInst> formInstList = new ArrayList<>();
        for (BuWorkOrderTask orderTask : orderTaskList) {
            String taskId = orderTask.getId();
            String orderId = orderTask.getOrderId();
            BuWorkOrder order = idOrderMap.get(orderId);
            String planId = order.getPlanId();
            String planTaskId = orderTask.getTaskObjId();

            List<BuRepairPlanTaskForms> planTaskFormMatchList = planTaskFormList.stream()
                    .filter(planTaskForm -> planTaskId.equals(planTaskForm.getTaskId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(planTaskFormMatchList)) {
                // 如果有列计划任务和表单关联，根据“列计划任务和表单关联”关联实例
                for (BuRepairPlanTaskForms planTaskForm : planTaskFormMatchList) {
                    BuRepairPlanForms planForm = idPlanFormMap.get(planTaskForm.getPlanFormId());
                    List<BuWorkOrderTaskFormInst> orderTaskFormInstList = createOrderTaskFormInst(planForm, planId, orderId, taskId);
                    formInstList.addAll(orderTaskFormInstList);
                }
            } else {
                // 如果没有列计划任务和表单关联，根据“工单任务关联工位及工位关联表单”关联实例
                List<BuWorkOrderTaskWorkstation> orderTaskWorkstationList = workstationList.stream()
                        .filter(workstation -> taskId.equals(workstation.getOrderTaskId()))
                        .collect(Collectors.toList());
                for (BuWorkOrderTaskWorkstation workstation : orderTaskWorkstationList) {
                    List<String> refFormIdList = workstation.getRefFormIdList();
                    List<BuRepairPlanForms> workstationMatchPlanFormList = planFormList.stream()
                            .filter(planForm -> refFormIdList.contains(planForm.getObjId()))
                            .collect(Collectors.toList());
                    for (BuRepairPlanForms planForm : workstationMatchPlanFormList) {
                        List<BuWorkOrderTaskFormInst> orderTaskFormInstList = createOrderTaskFormInst(planForm, planId, orderId, taskId);
                        formInstList.addAll(orderTaskFormInstList);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(formInstList)) {
            List<List<BuWorkOrderTaskFormInst>> batchSubList = DatabaseBatchSubUtil.batchSubList(formInstList);
            for (List<BuWorkOrderTaskFormInst> batchSub : batchSubList) {
                buWorkOrderTaskFormInstMapper.insertList(batchSub);
            }
        }
    }

    private List<BuWorkOrderTaskFormInst> createOrderTaskFormInst(BuRepairPlanForms planForm, String planId, String orderId, String taskId) {
        if (null == planForm || CollectionUtils.isNotEmpty(planForm.getChoiceList())) {
            return new ArrayList<>();
        }
        List<BuWorkOrderTaskFormInst> formInstList = new ArrayList<>();
        for (BuPlanFormChoiceVO choiceVO : planForm.getChoiceList()) {
            BuWorkOrderTaskFormInst formInst = new BuWorkOrderTaskFormInst()
                    .setId(UUIDGenerator.generate())
                    .setInstType(choiceVO.getFormType())
                    .setFormInstId(choiceVO.getId())
                    .setPlanId(planId)
                    .setWorkOrderId(orderId)
                    .setWorkOrderTaskId(taskId);
            formInstList.add(formInst);
        }
        return formInstList;
    }

    private void insertWorkOrderFormsInst(BuWorkOrder order) {
        List<BuWorkOrderTaskFormInst> forms = order.getForms();
        if (CollectionUtils.isEmpty(forms)) {
            return;
        }

        for (BuWorkOrderTaskFormInst formInst : forms) {
            formInst.setWorkOrderId(order.getId());
            if (StringUtils.isBlank(formInst.getId())) {
                formInst.setId(UUIDGenerator.generate());
            }
        }
        // 批量保存
        if (CollectionUtils.isNotEmpty(forms)) {
            buWorkOrderTaskFormInstMapper.insertList(forms);
        }
    }

    private void deleteWorkOrderFormsInst(List<String> orderIdList) {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }

        LambdaQueryWrapper<BuWorkOrderTaskFormInst> formInstWrapper = new LambdaQueryWrapper<BuWorkOrderTaskFormInst>()
                .in(BuWorkOrderTaskFormInst::getWorkOrderId, orderIdList);
        buWorkOrderTaskFormInstMapper.delete(formInstWrapper);
    }

    private JobRequirement joinJobRequirement(List<BuRepairReguDetailBO> reguDetailList) {
        AtomicInteger order = new AtomicInteger(999);
        List<BuRepairReguDetailBO> flatReguDetailList = new ArrayList<>();
        recurseAddFlatReguDetail(reguDetailList, flatReguDetailList, order);
        flatReguDetailList = deduplicateReguDetail(flatReguDetailList);

        flatReguDetailList.sort(Comparator.comparing(BuRepairReguDetailBO::getOrder));
//        AtomicInteger safeNoticeOrder = new AtomicInteger(1);
//        AtomicInteger requirementOrder = new AtomicInteger(1);
        StringBuilder safeNoticeBuilder = new StringBuilder();
        StringBuilder requirementBuilder = new StringBuilder();
        for (BuRepairReguDetailBO reguDetailBO : flatReguDetailList) {
            String safeNotice = reguDetailBO.getSafeNotice();
            String requirement = reguDetailBO.getRequirement();
            if (StringUtils.isNotBlank(safeNotice)) {
//                safeNoticeBuilder.append(safeNoticeOrder.getAndIncrement()).append(". ").append(safeNotice).append(System.lineSeparator());
                safeNoticeBuilder.append(safeNotice).append(System.lineSeparator());
            }
            if (StringUtils.isNotBlank(requirement)) {
//                requirementBuilder.append(requirementOrder.getAndIncrement()).append(". ").append(requirement).append(System.lineSeparator());
                requirementBuilder.append(requirement).append(System.lineSeparator());
            }
        }

        return new JobRequirement()
                .setSafeNotice(safeNoticeBuilder.toString())
                .setRequirement(requirementBuilder.toString());
    }

    private void recurseAddFlatReguDetail(List<BuRepairReguDetailBO> reguDetailList, List<BuRepairReguDetailBO> flatReguDetailList, AtomicInteger order) {
        if (CollectionUtils.isNotEmpty(reguDetailList)) {
            for (BuRepairReguDetailBO reguDetailBO : reguDetailList) {
                reguDetailBO.setOrder(order.getAndDecrement());
                flatReguDetailList.add(reguDetailBO);
                recurseAddFlatReguDetail(reguDetailBO.getParentList(), flatReguDetailList, order);
            }
        }
    }

    private List<BuRepairReguDetailBO> deduplicateReguDetail(List<BuRepairReguDetailBO> reguDetailList) {
        if (CollectionUtils.isEmpty(reguDetailList)) {
            return new ArrayList<>();
        }

        List<BuRepairReguDetailBO> resultList = new ArrayList<>();
        Set<String> detailIdSet = reguDetailList.stream()
                .map(BuRepairReguDetailBO::getId)
                .collect(Collectors.toSet());

        for (String detailId : detailIdSet) {
            List<BuRepairReguDetailBO> duplicateList = reguDetailList.stream()
                    .filter(detail -> detailId.equals(detail.getId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(duplicateList)) {
                resultList.add(duplicateList.get(0));
            }
        }

        return resultList;
    }

    private void checkUnConfirmMaterialReturn(List<String> orderIdList) {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }

        List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdList);
        for (BuWorkOrder order : orderList) {
            Integer unConfirmCount = buMaterialReturnedDispatchMapper.countUnConfirmByOrderId(order.getId());
            if (null != unConfirmCount && unConfirmCount > 0) {
                throw new JeecgBootException("工单" + order.getOrderCode() + "有未处理的退料，请处理后再关闭");
            }
        }
    }

    private void checkOrderMaterialActPriceZero(List<String> orderIdList) {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }

        List<BuWorkOrderMaterialActs> actsList = buWorkOrderMaterialActsMapper.selectListByOrderIdList(orderIdList);
        List<BuWorkOrderMaterialActs> priceZeroList = actsList.stream()
                .filter(item -> null == item.getPrice() || BigDecimal.ZERO.equals(item.getPrice()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(priceZeroList)) {
            TreeMap<String, TreeSet<String>> orderCodeMaterialTypeIdSetMap = new TreeMap<>();
            for (BuWorkOrderMaterialActs acts : priceZeroList) {
                String orderCode = acts.getOrderCode();
                String materialTypeId = acts.getMaterialTypeId();

                TreeSet<String> materialTypeIdSet = orderCodeMaterialTypeIdSetMap.getOrDefault(orderCode, new TreeSet<>());
                materialTypeIdSet.add(materialTypeId);
                orderCodeMaterialTypeIdSetMap.put(orderCode, materialTypeIdSet);
            }

            StringBuilder priceZeroErrorInfo = new StringBuilder("工单物料实际消耗价格为0，请处理后再关闭：");
            for (Map.Entry<String, TreeSet<String>> orderCodeMaterialTypeIdSetEntry : orderCodeMaterialTypeIdSetMap.entrySet()) {
                String orderCode = orderCodeMaterialTypeIdSetEntry.getKey();
                TreeSet<String> materialTypeIdSet = orderCodeMaterialTypeIdSetEntry.getValue();

                priceZeroErrorInfo.append("工单").append(orderCode)
                        .append("(").append(String.join(",", materialTypeIdSet)).append(")")
                        .append("；");
            }
            throw new JeecgBootException(priceZeroErrorInfo.toString());
        }
    }

    private void clearUnConfirmMaterialReturn(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return;
        }

        // 查询工单物料中类型为退还物资、已生成单据的数据
        LambdaQueryWrapper<BuWorkOrderMaterial> orderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                .eq(BuWorkOrderMaterial::getOrderId, orderId)
                .eq(BuWorkOrderMaterial::getOpType, 3)
                .eq(BuWorkOrderMaterial::getIsGenOrder, 1);
        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMapper.selectList(orderMaterialWrapper);
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        List<String> orderMaterialIdList = orderMaterialList.stream()
                .map(BuWorkOrderMaterial::getId)
                .collect(Collectors.toList());
        // 根据这些工单物料查询未确认的领料单id、领料明细id+对应工单物料id
        List<BuMaterialReturnedDetail> unConfirmReturnedDetailList = buMaterialReturnedDetailDispatchMapper.selectUnConfirmListByOrderMaterialIdList(orderMaterialIdList);
        if (CollectionUtils.isEmpty(unConfirmReturnedDetailList)) {
            return;
        }

        Set<String> returnedDetailIdSet = new HashSet<>();
        Set<String> returnedIdSet = new HashSet<>();
        Set<String> needReWriteOrderMaterialIdSet = new HashSet<>();
        for (BuMaterialReturnedDetail returnedDetail : unConfirmReturnedDetailList) {
            returnedDetailIdSet.add(returnedDetail.getId());
            returnedIdSet.add(returnedDetail.getReturnedId());
            needReWriteOrderMaterialIdSet.add(returnedDetail.getWorkOrderMaterialId());
        }

        // 删除退料明细
        if (CollectionUtils.isNotEmpty(returnedDetailIdSet)) {
            buMaterialReturnedDetailDispatchMapper.deleteBatchIds(returnedDetailIdSet);
        }
        // 删除退料单
        if (CollectionUtils.isNotEmpty(returnedIdSet)) {
            buMaterialReturnedDispatchMapper.deleteBatchIds(returnedIdSet);
        }
        // 设置工单物料是否生成单据的状态=未生成
        if (CollectionUtils.isNotEmpty(needReWriteOrderMaterialIdSet)) {
            LambdaQueryWrapper<BuWorkOrderMaterial> needReWriteOrderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                    .in(BuWorkOrderMaterial::getId, needReWriteOrderMaterialIdSet);
            BuWorkOrderMaterial orderMaterial = new BuWorkOrderMaterial()
                    .setIsGenOrder(0);
            buWorkOrderMaterialMapper.update(orderMaterial, needReWriteOrderMaterialWrapper);
        }
    }

    private void setFaultOrderFaultInfo(List<String> orderIdList) {
        // 如果工单中的任务类型是故障任务，则设置故障信息中的处理工单id字段
        LambdaQueryWrapper<BuWorkOrderTask> orderTaskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                .in(BuWorkOrderTask::getOrderId, orderIdList);
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskMapper.selectList(orderTaskWrapper);

        for (BuWorkOrderTask orderTask : orderTaskList) {
            if (2 == orderTask.getTaskType()) {
                String faultId = orderTask.getTaskObjId();
                String orderId = orderTask.getOrderId();

                BuFaultInfo faultInfo = new BuFaultInfo().setId(faultId).setHandleOrderId(orderId);
                buFaultInfoMapper.updateById(faultInfo);
            }
        }
    }

    private void deleteFaultOrderFaultInfo(List<String> orderIdList) {
        // 如果工单中的任务类型是故障任务，则设置故障信息中的处理工单id字段
        LambdaQueryWrapper<BuWorkOrderTask> orderTaskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                .in(BuWorkOrderTask::getOrderId, orderIdList);
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskMapper.selectList(orderTaskWrapper);

        List<String> faultIdList = orderTaskList.stream()
                .filter(task -> 2 == task.getTaskType())
                .map(BuWorkOrderTask::getTaskObjId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(faultIdList)) {
            buFaultInfoMapper.updateHandleOrderIdNull(faultIdList);
        }
    }

    private void sendAnnouncementToMonitor(List<BuWorkOrder> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String fromUser = sysUser.getUsername();

        sendMessageByNewThread(orderList, fromUser);
    }

    private void sendMessageByNewThread(List<BuWorkOrder> orderList, String fromUser) {
        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                List<String> groupIdList = orderList.stream()
                        .map(BuWorkOrder::getGroupId)
                        .distinct()
                        .collect(Collectors.toList());
                List<SysUserBO> userBOList = buWorkOrderMapper.selectMonitorUserNameByGroupIdList(groupIdList);
                if (CollectionUtils.isNotEmpty(userBOList)) {
                    for (BuWorkOrder order : orderList) {
                        String groupId = order.getGroupId();
                        String usernames = userBOList.stream()
                                .filter(user -> groupId.equals(user.getGroupId()))
                                .map(SysUserBO::getUsername)
                                .distinct()
                                .collect(Collectors.joining(","));

                        String title = "新的工单：" + order.getTrainNo() + "车辆 " + order.getOrderCode();
                        String orderTypeName = sysBaseAPI.getDictItemTextByCodeAndValue("bu_order_type", null == order.getOrderType() ? null : order.getOrderType().toString());
                        String content = order.getTrainNo() + "车辆 " + order.getOrderName() + "，工单类型：" + orderTypeName;

                        sysBaseAPI.sendSysAnnouncement(fromUser, usernames, title, content);
                    }
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

    private void addBaseScheduleToMonitor(List<BuWorkOrder> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        // 查询班组
        List<String> groupIdList = orderList.stream()
                .map(BuWorkOrder::getGroupId)
                .distinct()
                .collect(Collectors.toList());
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupDispatchMapper.selectBatchIds(groupIdList);
        Map<String, BuMtrWorkshopGroup> idGroupMap = new HashMap<>();
        groupList.forEach(group -> idGroupMap.put(group.getId(), group));

        // 保存日程
        for (BuWorkOrder order : orderList) {
            BuMtrWorkshopGroup group = idGroupMap.get(order.getGroupId());
            if (null == group) {
                continue;
            }

            String monitor = group.getMonitor();
            if (StringUtils.isNotBlank(monitor)) {
                String title = "新下发的工单：" + order.getTrainNo() + "车辆 " + order.getOrderCode();
                String orderTypeName = sysBaseAPI.getDictItemTextByCodeAndValue("bu_order_type", null == order.getOrderType() ? null : order.getOrderType().toString());
                String content = order.getTrainNo() + "车辆 " + order.getOrderName() + "类型：" + orderTypeName;

                BuBaseSchedule baseSchedule = new BuBaseSchedule()
                        .setId(UUIDGenerator.generate())
                        .setTitle(title)
                        .setContent(content)
                        .setUserId(monitor)
                        .setScheduleType(1)
                        .setStartTime(order.getStartTime())
                        .setEndTime(order.getFinishTime())
                        .setFinished(0);
                buBaseScheduleDispatchMapper.insert(baseSchedule);
            }
        }
    }

    private void addBaseScheduleToArrangeUser(List<String> orderIdList) {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }

        // 查询工单任务分配人员
        LambdaQueryWrapper<BuRepairTaskStaffArrange> staffArrangeWrapper = new LambdaQueryWrapper<BuRepairTaskStaffArrange>()
                .in(BuRepairTaskStaffArrange::getOrderId, orderIdList);
        List<BuRepairTaskStaffArrange> staffArrangeList = buRepairTaskStaffArrangeMapper.selectList(staffArrangeWrapper);
        if (CollectionUtils.isEmpty(staffArrangeList)) {
            return;
        }
        // 查询工单
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdList);

        // 保存日程
        for (BuWorkOrder order : orderList) {
            // 过滤出该工单任务分配人员
            Set<String> arrangeUserIdSet = staffArrangeList.stream()
                    .filter(staffArrange -> order.getId().equals(staffArrange.getOrderId()))
                    .map(BuRepairTaskStaffArrange::getUserId)
                    .collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(arrangeUserIdSet)) {
                continue;
            }

            String title = "新的待作业工单：" + order.getTrainNo() + "车辆 " + order.getOrderCode();
            String orderTypeName = sysBaseAPI.getDictItemTextByCodeAndValue("bu_order_type", null == order.getOrderType() ? null : order.getOrderType().toString());
            String content = order.getTrainNo() + "车辆 " + order.getOrderName() + "类型：" + orderTypeName;

            List<BuBaseSchedule> baseScheduleList = new ArrayList<>();
            for (String arrangeUserId : arrangeUserIdSet) {
                BuBaseSchedule baseSchedule = new BuBaseSchedule()
                        .setId(UUIDGenerator.generate())
                        .setTitle(title)
                        .setContent(content)
                        .setUserId(arrangeUserId)
                        .setScheduleType(1)
                        .setStartTime(order.getStartTime())
                        .setEndTime(order.getFinishTime())
                        .setFinished(0);
                baseScheduleList.add(baseSchedule);
            }
            if (CollectionUtils.isNotEmpty(baseScheduleList)) {
                buBaseScheduleDispatchMapper.insertList(baseScheduleList);
            }
        }
    }

    private void verifyOrderSubmitMaterialApply(String orderId) {
        Date now = new Date();

        BuWorkOrder order = buWorkOrderMapper.selectOrderById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }

        // 查询未生成单据的、额定物料+临时领料的、工单物料
        List<BuWorkOrderMaterial> specifiedOrderMaterialList = buWorkOrderMaterialMapper.selectNotGenOrderListByOrderIdAndOpType(orderId, 1);
        List<BuWorkOrderMaterial> tempOrderMaterialList = buWorkOrderMaterialMapper.selectNotGenOrderListByOrderIdAndOpType(orderId, 2);
        List<BuWorkOrderMaterial> allOrderMaterialList = new ArrayList<>();
        allOrderMaterialList.addAll(specifiedOrderMaterialList);
        allOrderMaterialList.addAll(tempOrderMaterialList);

        List<BuWorkOrderMaterial> notGenOrderOrderMaterialList = new ArrayList<>();
        // 现在是否需要申请领料单，由needDispatchin属性决定
//        if (4 == order.getOrderType()) {
//            // 发料工单，不过滤判断
//            notGenOrderOrderMaterialList = allOrderMaterialList;
//        } else {
//            // 非发料工单，判断不是必换件
//            notGenOrderOrderMaterialList = allOrderMaterialList.stream()
//                    .filter(orderMaterial -> 1 != orderMaterial.getUseCategory())
//                    .collect(Collectors.toList());
//        }
        // 现在是否需要申请领料单，由needDispatchin属性决定
        for (BuWorkOrderMaterial orderMaterial : allOrderMaterialList) {
            Integer needDispatchin = orderMaterial.getNeedDispatchin();
            if (null != needDispatchin && 1 == needDispatchin && orderMaterial.getIsGenOrder() == 0) {
                notGenOrderOrderMaterialList.add(orderMaterial);
            }
        }
        if (CollectionUtils.isEmpty(notGenOrderOrderMaterialList)) {
            return;
        }

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        List<BuMaterialApplyDetail> needAddApplyDetailList = new ArrayList<>();
        // 生成并保存工单物料领用单
        BuMaterialApply apply = getOrNewApplyOrder(order, now, userId, 1);
        // 生成领用明细
        transToApplyDetailList(needAddApplyDetailList, notGenOrderOrderMaterialList, apply.getId());

        // 保存领用明细
        if (CollectionUtils.isNotEmpty(needAddApplyDetailList)) {
            List<List<BuMaterialApplyDetail>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddApplyDetailList);
            for (List<BuMaterialApplyDetail> batchSub : batchSubList) {
                buMaterialApplyDetailDispatchMapper.insertList(batchSub);
            }
        }
        // 设置工单物料为已生成单据
        Set<String> notGenOrderOrderMaterialIdSet = notGenOrderOrderMaterialList.stream()
                .map(BuWorkOrderMaterial::getId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(notGenOrderOrderMaterialIdSet)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(notGenOrderOrderMaterialIdSet));
            for (List<String> batchSub : batchSubList) {
                LambdaQueryWrapper<BuWorkOrderMaterial> orderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                        .in(BuWorkOrderMaterial::getId, batchSub);
                BuWorkOrderMaterial orderMaterial = new BuWorkOrderMaterial()
                        .setIsGenOrder(1);
                buWorkOrderMaterialMapper.update(orderMaterial, orderMaterialWrapper);
            }
        }
    }

    private void submitOrderSubmitMaterialReturn(String orderId) {
        Date now = new Date();

        BuWorkOrder order = buWorkOrderMapper.selectOrderById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }

        // 查询工单物料
        List<BuWorkOrderMaterial> allReturnMaterialList = buWorkOrderMaterialMapper.selectNotGenOrderListByOrderIdAndOpType(orderId, 3);
        // 过滤出未生成单据的工单物料
        List<BuWorkOrderMaterial> notGenReturnMaterialList = allReturnMaterialList.stream()
                .filter(orderMaterial -> 0 == orderMaterial.getIsGenOrder())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(notGenReturnMaterialList)) {
            return;
        }

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        List<BuMaterialReturnedDetail> needAddReturnedDetailList = new ArrayList<>();

        // 退还物资
        // 生成退料单并保存
        String returnedCode = serialNumberGenerate.generateSerialNumberByCode("RT");
        BuMaterialReturned returned = new BuMaterialReturned()
                .setId(UUIDGenerator.generate())
                .setBillCode(returnedCode)
                .setBillName(order.getOrderName() + "【退料】")
                .setBillDate(now)
                .setWorkOrderId(orderId)
                .setGroupId(order.getGroupId())
                .setHandleUserId(userId)
                .setStatus(0)
                .setRemark("由工单" + order.getOrderCode() + "自动生成的退料单");
        buMaterialReturnedDispatchMapper.insert(returned);

        // 生成退料明细
        for (BuWorkOrderMaterial orderMaterial : notGenReturnMaterialList) {
            String materialTypeId = orderMaterial.getMaterialTypeId();

            // 查询对应的领用明细
            List<BuMaterialApplyDetail> applyDetailList = buMaterialApplyDetailDispatchMapper.selectReceivedListWithAssignByOrderIdAndMaterialTypeId(orderId, materialTypeId);
            if (CollectionUtils.isEmpty(applyDetailList)) {
                log.error("根据退还物资的工单物料生成退料明细时，无法找到已领料的领用明细，工单物料=" + JSON.toJSONString(orderMaterial));
                continue;
            }

            double needReturnAmount = null == orderMaterial.getAmount() ? 0D : orderMaterial.getAmount();
            for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                List<BuMaterialAssignDetail> assignDetailList = applyDetail.getAssignDetailList();
                if (CollectionUtils.isEmpty(assignDetailList)) {
                    continue;
                }

                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    double assignAmount = null == assignDetail.getAmount() ? 0D : assignDetail.getAmount();
                    double returnAmount = Math.min(assignAmount, needReturnAmount);

                    BuMaterialReturnedDetail returnedDetail = new BuMaterialReturnedDetail()
                            .setId(UUIDGenerator.generate())
                            .setReturnedId(returned.getId())
                            .setWorkOrderId(orderId)
                            .setMaterialTypeId(materialTypeId)
                            .setOrderMaterialId(applyDetail.getId())
                            .setReturnAmount(returnAmount)
                            .setLocationWbs(assignDetail.getLocationWbs())
                            .setLocationName(assignDetail.getLocationName())
                            .setEbsWhCode(assignDetail.getEbsWhCode())
                            .setEbsWhChildCode(assignDetail.getEbsWhChildCode());
                    needAddReturnedDetailList.add(returnedDetail);

                    needReturnAmount = needReturnAmount - returnAmount;
                    if (needReturnAmount <= 0) {
                        break;
                    }
                }
            }
        }

        // 保存退料明细
        if (CollectionUtils.isNotEmpty(needAddReturnedDetailList)) {
            buMaterialReturnedDetailDispatchMapper.insertList(needAddReturnedDetailList);
        }
        // 设置工单物料为已生成单据
        Set<String> notGenOrderOrderMaterialIdSet = notGenReturnMaterialList.stream()
                .map(BuWorkOrderMaterial::getId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(notGenOrderOrderMaterialIdSet)) {
            LambdaQueryWrapper<BuWorkOrderMaterial> orderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                    .in(BuWorkOrderMaterial::getId, notGenOrderOrderMaterialIdSet);
            BuWorkOrderMaterial orderMaterial = new BuWorkOrderMaterial()
                    .setIsGenOrder(1);
            buWorkOrderMaterialMapper.update(orderMaterial, orderMaterialWrapper);
        }
    }

    private void closeOrderDecreaseMaterialGroupStock(List<BuWorkOrder> orderList, Date now, String userId) {
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        for (BuWorkOrder order : orderList) {
            // 查询工单物料实际消耗
            List<BuWorkOrderMaterialActs> orderMaterialActsList = buWorkOrderMaterialActsMapper.selectListByOrderId(order.getId());
            // 无需要消耗的工单物料实际消耗，不处理
            if (CollectionUtils.isEmpty(orderMaterialActsList)) {
                continue;
            }

            // 查询班组库存
            List<String> groupStockIdList = orderMaterialActsList.stream()
                    .map(BuWorkOrderMaterialActs::getGroupStockId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<String, BuMaterialGroupStock> idGroupStockMap = new HashMap<>();
            List<List<String>> groupStockIdBatchSubList = DatabaseBatchSubUtil.batchSubList(groupStockIdList);
            for (List<String> groupStockIdBatchSub : groupStockIdBatchSubList) {
                List<BuMaterialGroupStock> subGroupStockList = buMaterialGroupStockDispatchMapper.selectBatchIds(groupStockIdBatchSub);
                for (BuMaterialGroupStock groupStock : subGroupStockList) {
                    groupStock.setOldAmount(groupStock.getAmount())
                            .setRelativeOrderMaterialActIdSet(new HashSet<>());
                    idGroupStockMap.put(groupStock.getId(), groupStock);
                }
            }
            if (idGroupStockMap.isEmpty()) {
                StringBuilder lackInfoBuilder = new StringBuilder();
                for (BuWorkOrderMaterialActs orderMaterialActs : orderMaterialActsList) {
                    String lackItem = String.format("工单:%s的物资:%s(%s)库存不足%s", orderMaterialActs.getOrderCode(), orderMaterialActs.getMaterialTypeName(), orderMaterialActs.getMaterialTypeCode(), orderMaterialActs.getActAmount());
                    lackInfoBuilder.append(lackItem).append("；").append(System.lineSeparator());
                }
                String lackInfo = null;
                if (lackInfoBuilder.length() > 0) {
                    lackInfo = lackInfoBuilder.deleteCharAt(lackInfoBuilder.length() - 1).toString();
                }
                throw new JeecgBootException(lackInfo);
            }

            // 计算班组库存数据
//        Set<String> needConsumeApplyDetailIdSet = new HashSet<>();
            for (BuWorkOrderMaterialActs orderMaterialActs : orderMaterialActsList) {
                String groupStockId = orderMaterialActs.getGroupStockId();
                Double actAmount = orderMaterialActs.getActAmount();
                if (null == actAmount) {
                    continue;
                }

                BuMaterialGroupStock matchGroupStock = idGroupStockMap.get(groupStockId);
                if (null == matchGroupStock) {
                    String lackItem = String.format("工单:%s的物资:%s(%s)库存不足%s", orderMaterialActs.getOrderCode(), orderMaterialActs.getMaterialTypeName(), orderMaterialActs.getMaterialTypeCode(), orderMaterialActs.getActAmount());
                    throw new JeecgBootException(lackItem);
                }

                double groupStockAmount = null == matchGroupStock.getAmount() ? 0D : matchGroupStock.getAmount().doubleValue();
                double newAmount = groupStockAmount - actAmount;
                if (newAmount < 0) {
                    String lackItem = String.format("工单:%s的物资:%s(%s)库存不足%s", orderMaterialActs.getOrderCode(), orderMaterialActs.getMaterialTypeName(), orderMaterialActs.getMaterialTypeCode(), orderMaterialActs.getActAmount());
                    throw new JeecgBootException(lackItem);
                } else {
                    matchGroupStock.setAmount(new BigDecimal(newAmount))
                            .setNeedUpdate(true);
                    matchGroupStock.getRelativeOrderMaterialActIdSet().add(orderMaterialActs.getId());
                }
//            needConsumeApplyDetailIdSet.add(orderMaterialActs.getApplyDetailId());
            }

            // 减少班组库存记录
            List<BuMaterialGroupStock> needUpdateGroupStockList = idGroupStockMap.values().stream()
                    .filter(groupStock -> null != groupStock.getNeedUpdate() && groupStock.getNeedUpdate())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(needUpdateGroupStockList)) {
                List<List<BuMaterialGroupStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateGroupStockList);
                for (List<BuMaterialGroupStock> batchSub : batchSubList) {
                    buMaterialGroupStockDispatchMapper.updateListAmount(batchSub);
                }

                List<BuMaterialStockChange> changeList = new ArrayList<>();
                for (BuMaterialGroupStock groupStock : needUpdateGroupStockList) {
                    boolean noteInRemark = groupStock.getRelativeOrderMaterialActIdSet().size() > 1;
                    String orderMaterialActIds = String.join(",", groupStock.getRelativeOrderMaterialActIdSet());

                    // 记录库存变动
                    BuMaterialStockChange change = new BuMaterialStockChange()
                            .setId(UUIDGenerator.generate())
                            .setStockType(4)
                            .setStockId(groupStock.getId())
                            .setWarehouseId(groupStock.getWarehouseId())
                            .setMaterialTypeId(groupStock.getMaterialTypeId())
                            .setTradeNo(groupStock.getTradeNo())
                            .setChangeTime(now)
                            .setChangeReason("关闭工单时，减少班组库存量")
                            .setChangeType(3)
                            .setOldValue(groupStock.getOldAmount().doubleValue())
                            .setNewValue(groupStock.getAmount().doubleValue())
                            .setTrainNo(order.getTrainNo())
                            .setOrderCode(order.getOrderCode())
                            .setAssignDetailId(null)
                            .setReturnedDetailId(null)
                            .setOrderMaterialActId(null)
                            .setOperationUser(userId)
                            .setRemark(null);
                    if (noteInRemark) {
                        change.setOrderMaterialActId("见备忘")
                                .setRemark("减少班组库存量，orderMaterialActIds=" + orderMaterialActIds);
                    } else {
                        change.setOrderMaterialActId(orderMaterialActIds)
                                .setRemark("减少班组库存量");
                    }
                    changeList.add(change);
                }
                // 保存库存变动记录
                if (CollectionUtils.isNotEmpty(changeList)) {
                    buMaterialStockChangeService.addChangeList(changeList);
                }
            }

            //TODO-zhaiyantao 2021/8/20 18:45 这里可能有问题：每次确认领料时更新班组库存中的领用相关id，可能现在的领用明细id不是应该更新的那个发料的领用明细id，所有先不更新
//        // 设置领命明细为3已消耗
//        if (CollectionUtils.isNotEmpty(needConsumeApplyDetailIdSet)) {
//            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(needConsumeApplyDetailIdSet));
//            for (List<String> batchSub : batchSubList) {
//                LambdaQueryWrapper<BuMaterialApplyDetail> applyDetailWrapper = new LambdaQueryWrapper<BuMaterialApplyDetail>()
//                        .in(BuMaterialApplyDetail::getId, batchSub);
//                BuMaterialApplyDetail applyDetail = new BuMaterialApplyDetail()
//                        .setStatus(3);
//                buMaterialApplyDetailDispatchMapper.update(applyDetail, applyDetailWrapper);
//            }
//        }
        }
    }

    private void addMaximoTransDataOfApproveOrder(List<String> orderIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }
        // 查工单
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectOrderForMaximoByOrderIdList(orderIdList);
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuWorkOrder order : orderList) {
            String orderId = order.getId();
            // 设置工单资产设备编码
            List<String> trainAssetCodeList = buWorkOrderTaskMapper.selectTrainAssetCodeByOrderId(orderId);
            if (CollectionUtils.isNotEmpty(trainAssetCodeList)) {
                order.setAssetCode(trainAssetCodeList.get(0));
            }

            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(1)
                    .setObjId(orderId)
                    .setObjJson(JSON.toJSONString(order))
                    .setCreateTime(now)
                    .setSuccessStatus(0)
                    .setHandleStatus(0)
                    .setMessage(null);
            dataList.add(maximoTransData);
        }
        buMaximoTransDataService.addTransDataList(dataList);
    }

    private void closeOrderAddMaximoTransData(List<String> orderIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }
        // 查工单
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectOrderForMaximoByOrderIdList(orderIdList);
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(now);
        nowCalendar.add(Calendar.SECOND, 5);
        Date nowAfter5 = nowCalendar.getTime();
        nowCalendar.add(Calendar.SECOND, 5);
        Date nowAfter10 = nowCalendar.getTime();

        // 查工时
        List<BuRepairTaskStaffArrange> allStaffArrangeList = buRepairTaskStaffArrangeMapper.selectListForMaximoByOrderIdList(orderIdList);
        // 过滤掉没有工号、或者没工单任务开始时间的
        allStaffArrangeList.removeIf(staffArrange -> StringUtils.isBlank(staffArrange.getWorkNo()) || null == staffArrange.getTaskStart());
        // 过滤掉实际作业工时小于0.01的
        allStaffArrangeList.removeIf(staffArrange -> null == staffArrange.getWorkTime() || BigDecimal.valueOf(0.01D).compareTo(staffArrange.getWorkTime()) > 0);

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuWorkOrder order : orderList) {
            String orderId = order.getId();
            String orderCode = order.getOrderCode();
            // 设置工单资产设备编码
            List<String> trainAssetCodeList = buWorkOrderTaskMapper.selectTrainAssetCodeByOrderId(orderId);
            if (CollectionUtils.isNotEmpty(trainAssetCodeList)) {
                order.setAssetCode(trainAssetCodeList.get(0));
            }

            List<BuRepairTaskStaffArrange> staffArrangeList = allStaffArrangeList.stream()
                    .filter(staffArrange -> orderCode.equals(staffArrange.getOrderCode()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(staffArrangeList)) {
                Date orderActStart = order.getActStart();
                Date orderActFinish = order.getActFinish();

                // 检验是否满足条件
                for (BuRepairTaskStaffArrange staffArrange : staffArrangeList) {
                    // 验证并修改数据
                    Date workTimeStart = staffArrange.getTaskStart();
                    BigDecimal workTime = staffArrange.getWorkTime();
                    int workTimeSecond = workTime.multiply(BigDecimal.valueOf(60 * 60)).intValue();
                    // 工时开始时间（任务开始时间）不能早于工单实际开始时间
                    if (workTimeStart.before(orderActStart)) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(orderActStart);
                        calendar.add(Calendar.SECOND, 10);
                        workTimeStart = calendar.getTime();
                        staffArrange.setTaskStart(workTimeStart);
                    }
                    // 工时结束时间（工时结束时间=工时开始时间+时长）不能晚于工单实际结束时间
                    Calendar workTimeCalendar = Calendar.getInstance();
                    workTimeCalendar.setTime(workTimeStart);
                    workTimeCalendar.add(Calendar.SECOND, workTimeSecond);
                    Date workTimeFinish = workTimeCalendar.getTime();
                    staffArrange.setTaskFinish(workTimeFinish);
                    if (workTimeFinish.after(orderActFinish)) {
                        workTimeCalendar.add(Calendar.SECOND, 10);
                        orderActFinish = workTimeCalendar.getTime();
                        order.setActFinish(orderActFinish);
                    }

                    // 工时同步数据
                    BuMaximoTransData maximoTransData = new BuMaximoTransData()
                            .setId(UUIDGenerator.generate())
                            .setType(6)
                            .setObjId(staffArrange.getId())
                            .setObjJson(JSON.toJSONString(staffArrange))
                            .setCreateTime(nowAfter5)
                            .setSuccessStatus(0)
                            .setHandleStatus(0)
                            .setMessage(null);
                    dataList.add(maximoTransData);
                }
                // 修改工单同步数据（工单状态=已核实）
                BuWorkOrder updateOrder = new BuWorkOrder();
                BeanUtils.copyProperties(order, updateOrder);
                updateOrder.setOrderStatus(2);
                BuMaximoTransData maximoTransData = new BuMaximoTransData()
                        .setId(UUIDGenerator.generate())
                        .setType(7)
                        .setObjId(orderId)
                        .setObjJson(JSON.toJSONString(updateOrder))
                        .setCreateTime(now)
                        .setSuccessStatus(0)
                        .setHandleStatus(0)
                        .setMessage(null);
                dataList.add(maximoTransData);
            }
            // 关闭工单同步数据
            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(2)
                    .setObjId(orderId)
                    .setObjJson(JSON.toJSONString(order))
                    .setCreateTime(nowAfter10)
                    .setSuccessStatus(0)
                    .setHandleStatus(0)
                    .setMessage(null);
            dataList.add(maximoTransData);
        }
        buMaximoTransDataService.addTransDataList(dataList);
    }

    private BuMaterialApply getOrNewApplyOrder(BuWorkOrder order, Date now, String userId, int applyType) {
        if (null == order) {
            return null;
        }

        LambdaQueryWrapper<BuMaterialApply> applyWrapper = new LambdaQueryWrapper<BuMaterialApply>()
                .eq(BuMaterialApply::getWorkOrderId, order.getId())
                .eq(BuMaterialApply::getApplyType, applyType);
        List<BuMaterialApply> applyList = buMaterialApplyDispatchMapper.selectList(applyWrapper);
        if (CollectionUtils.isEmpty(applyList)) {
            //, "-物料领用", "自动生成的工单物料领用单"
            String nameSuffix = "";
            if (1 == applyType) {
                nameSuffix = "物料领用";
            } else if (3 == applyType) {
                nameSuffix = "缺料申请";
            } else if (2 == applyType) {
                nameSuffix = "故障申领";
            }

            // 如果没有额定物资领用单，则生成并保存
            String applyCode = serialNumberGenerate.generateSerialNumberByCode("MaterialApplyCode");
            BuMaterialApply apply = new BuMaterialApply()
                    .setId(UUIDGenerator.generate())
                    .setCode(applyCode)
                    .setTitle(order.getOrderName() + "-" + nameSuffix)
                    .setApplyType(applyType)
                    .setWorkOrderId(order.getId())
                    .setTrainNo(order.getTrainNo())
                    .setDeptId(StringUtils.isBlank(order.getGroupId()) ? order.getDepotId() : order.getGroupId())
                    .setUserId(userId)
                    .setApplyDate(now)
                    .setReadyStatus(0)
                    .setStatus(0)
                    .setDepotId(order.getDepotId())
                    .setLineId(order.getLineId())
                    .setWorkshopId(order.getWorkshopId())
                    .setRemark("由工单" + order.getOrderCode() + "自动生成的物料领用单(" + nameSuffix + ")");
            buMaterialApplyDispatchMapper.insert(apply);
            return apply;
        } else {
            return applyList.get(0);
        }
    }

    private void transToApplyDetailList(List<BuMaterialApplyDetail> needAddApplyDetailList, List<BuWorkOrderMaterial> orderMaterialList, String applyId) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            BigDecimal unitPrice = null == orderMaterial.getPrice() ? BigDecimal.ZERO : BigDecimal.valueOf(orderMaterial.getPrice());

            BuMaterialApplyDetail applyDetail = new BuMaterialApplyDetail()
                    .setId(UUIDGenerator.generate())
                    .setApplyId(applyId)
                    .setMaterialTypeId(orderMaterial.getMaterialTypeId())
                    .setAmount(null == orderMaterial.getAmount() ? BigDecimal.ZERO : BigDecimal.valueOf(orderMaterial.getAmount()))
                    .setReceive(BigDecimal.ZERO)
                    .setStatus(0)
                    .setUnitPrice(unitPrice)
                    .setUseCategory(orderMaterial.getUseCategory())
                    .setOrderMaterialId(orderMaterial.getId())
                    .setMaterialTypeCode(orderMaterial.getCode())
                    .setMaterialTypeName(orderMaterial.getName());
            needAddApplyDetailList.add(applyDetail);
        }
    }

    private String createTaskQRCodeKey(BuWorkOrderRecordCreateTaskQRCodeVO workOrderRecordCreateTaskQRCodeVO) {
        String str = workOrderRecordCreateTaskQRCodeVO.toString();
        return MD5Util.MD5Encode(str, "utf-8");
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
                log.error("关闭工单时，开线程重新统计车辆物料统计数据，执行失败，原因如下：" + ex.getMessage());
                StringWriter stringWriter = new StringWriter();
                try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                    ex.printStackTrace(printWriter);
                }
                log.error(stringWriter.toString());
            }
        });
        singleExecutor.shutdown();
    }

    private void rebuildKpiRptDataByNewThread(List<String> orderIdList) {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }

        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                for (String orderId : orderIdList) {
                    kpiRptService.increaseTimeKpiByOrder(orderId);
                }
            } catch (Exception ex) {
                log.error("关闭工单时，开线程重新统计车辆物料统计数据，执行失败，原因如下：" + ex.getMessage());
                StringWriter stringWriter = new StringWriter();
                try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                    ex.printStackTrace(printWriter);
                }
                log.error(stringWriter.toString());
            }
        });
        singleExecutor.shutdown();
    }

    public void deleteGroupStockUsedAmount(List<BuMaterialGroupStock> groupStockList, String groupId, List<BuWorkOrderMaterial> needUsedOrderMaterialList, String needIgnoreOrderMaterialId, List<String> needIgnoreOrderCodeList) {
        if (CollectionUtils.isEmpty(groupStockList)) {
            return;
        }
        for (BuMaterialGroupStock groupStock : groupStockList) {
            if (null == groupStock.getUsableAmount()) {
                groupStock.setUsableAmount(groupStock.getAmount());
            }
            if (StringUtils.isBlank(groupStock.getTradeNo())) {
                groupStock.setTradeNo(null);
            }
        }

        // 班组库存量减去“已核实”到“未关闭”的工单中的物料数量
        // 暂停状态的工单不需要减去：暂停时有可能原始状态为未核实不需要减去；重新激活时会回归原始状态根据原始状态计算
        if (CollectionUtils.isEmpty(needUsedOrderMaterialList)) {
            needUsedOrderMaterialList = buWorkOrderMaterialMapper.selectNotMaterialApplyOrderListByGroupIdAndOrderStatus(groupId, TirosConstant.GROUP_STOCK_NEED_COUNT_USED_ORDER_STATUS);
        }
        if (StringUtils.isNotBlank(needIgnoreOrderMaterialId)) {
            // 如果是工单物料本身再次填写，不能减去
            needUsedOrderMaterialList.removeIf(orderMaterial -> needIgnoreOrderMaterialId.equals(orderMaterial.getId()));
        }
        if (CollectionUtils.isNotEmpty(needIgnoreOrderCodeList)) {
            // 不减去需要忽略的工单的工单物料
            needUsedOrderMaterialList.removeIf(orderMaterial -> StringUtils.isNotBlank(orderMaterial.getOrderCode()) && needIgnoreOrderCodeList.contains(orderMaterial.getOrderCode()));
        }
        if (CollectionUtils.isNotEmpty(needUsedOrderMaterialList)) {
            // 查询实际消耗列表
            List<String> orderMaterialIdList = needUsedOrderMaterialList.stream()
                    .map(BuWorkOrderMaterial::getId)
                    .collect(Collectors.toList());
            List<BuWorkOrderMaterialActs> actList = new ArrayList<>();
            List<List<String>> orderMaterialIdBatchSubList = DatabaseBatchSubUtil.batchSubList(orderMaterialIdList);
            for (List<String> orderMaterialIdBatchSub : orderMaterialIdBatchSubList) {
                List<BuWorkOrderMaterialActs> subActList = buWorkOrderMaterialActsMapper.selectListByOrderMaterialIdList(orderMaterialIdBatchSub);
                actList.addAll(subActList);
            }
            for (BuWorkOrderMaterial orderMaterial : needUsedOrderMaterialList) {
                List<BuWorkOrderMaterialActs> matchActList = actList.stream()
                        .filter(act -> orderMaterial.getId().equals(act.getOrderMaterialId()))
                        .collect(Collectors.toList());
                matchActList.forEach(act -> act.setOrderCode(orderMaterial.getOrderCode()));
                orderMaterial.setMaterialActsList(matchActList);
            }

            // 获取各物资、各车号、各批次的占用量
            List<GroupStockUsed> usedList = getMaterialGroupStockUsedList(needUsedOrderMaterialList);

            // 计算：减去占用量
            // 物资分组
            Map<String, List<BuMaterialGroupStock>> materialTypeIdGroupStockListMap = groupStockList.stream()
                    .collect(Collectors.groupingBy(BuMaterialGroupStock::getMaterialTypeId));
            for (Map.Entry<String, List<BuMaterialGroupStock>> materialTypeIdStockListEntry : materialTypeIdGroupStockListMap.entrySet()) {
                String materialTypeId = materialTypeIdStockListEntry.getKey();
                List<BuMaterialGroupStock> materialTypeIdGroupStockList = materialTypeIdStockListEntry.getValue();
                // 车号分组
                Map<String, List<BuMaterialGroupStock>> trainNoGroupStockListMap = new HashMap<>();
                for (BuMaterialGroupStock groupStock : materialTypeIdGroupStockList) {
                    String trainNo = groupStock.getTrainNo();
                    List<BuMaterialGroupStock> trainNoGroupStockList = trainNoGroupStockListMap.getOrDefault(trainNo, new ArrayList<>());
                    trainNoGroupStockList.add(groupStock);
                    trainNoGroupStockListMap.put(trainNo, trainNoGroupStockList);
                }
                for (Map.Entry<String, List<BuMaterialGroupStock>> trainNoGroupStockListEntry : trainNoGroupStockListMap.entrySet()) {
                    String trainNo = trainNoGroupStockListEntry.getKey();
                    List<BuMaterialGroupStock> trainNoGroupStockList = trainNoGroupStockListEntry.getValue();
                    // 匹配物资、车号
                    List<GroupStockUsed> matchUsedList = new ArrayList<>();
                    for (GroupStockUsed groupStockUsed : usedList) {
                        boolean sameMaterial = materialTypeId.equals(groupStockUsed.getMaterialTypeId());
                        boolean sameTrainNo = StringUtils.isBlank(trainNo) ? StringUtils.isBlank(groupStockUsed.getTrainNo()) : trainNo.equals(groupStockUsed.getTrainNo());

                        if (sameMaterial && sameTrainNo) {
                            matchUsedList.add(groupStockUsed);
                        }
                    }
                    matchUsedList.sort(Comparator.comparing(GroupStockUsed::getOrderCode, Comparator.nullsLast(Comparator.naturalOrder())));
                    if (CollectionUtils.isNotEmpty(matchUsedList)) {
                        for (GroupStockUsed used : matchUsedList) {
                            String usedTradeNo = used.getTradeNo();
                            String usedOrderCode = used.getOrderCode();
                            BigDecimal usedAmount = used.getAmount();
                            Integer usedType = used.getUsedType();
                            // 匹配批次
                            if (StringUtils.isNotBlank(usedTradeNo)) {
                                // 占用中有批次号，查找批次号相同的对应班组库存减去占用量
                                for (BuMaterialGroupStock groupStock : trainNoGroupStockList) {
                                    if (usedTradeNo.equals(groupStock.getTradeNo())) {
                                        decreaseGroupStock(groupStock, usedOrderCode, usedAmount, usedType);
                                    }
                                }
                            } else {
                                // 占用中没有批次号，先找批次号为空的对应班组库存减去占用，没有批次号为空的对应班组库存则需要按批次号排序后一个个的减去所有占用量
                                trainNoGroupStockList.sort(Comparator.comparing(BuMaterialGroupStock::getTradeNo, Comparator.nullsFirst(Comparator.naturalOrder())));
                                double totalAmount = usedAmount.doubleValue();
                                for (BuMaterialGroupStock groupStock : trainNoGroupStockList) {
                                    double groupUsableAmount = groupStock.getUsableAmount().doubleValue();
                                    if (groupUsableAmount > 0D) {
                                        double amount = Math.min(groupUsableAmount, totalAmount);

                                        decreaseGroupStock(groupStock, usedOrderCode, BigDecimal.valueOf(amount), usedType);
                                        totalAmount = totalAmount - amount;
                                        if (totalAmount <= 0D) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<GroupStockUsed> getMaterialGroupStockUsedList(List<BuWorkOrderMaterial> allOrderMaterialList) {
        if (CollectionUtils.isEmpty(allOrderMaterialList)) {
            return new ArrayList<>();
        }

        List<GroupStockUsed> usedList = new ArrayList<>();

        Map<String, List<BuWorkOrderMaterial>> materialTypeIdOrderMaterialListMap = allOrderMaterialList.stream()
                .collect(Collectors.groupingBy(BuWorkOrderMaterial::getMaterialTypeId));
        for (Map.Entry<String, List<BuWorkOrderMaterial>> materialTypeIdOrderMaterialListEntry : materialTypeIdOrderMaterialListMap.entrySet()) {
            String materialTypeId = materialTypeIdOrderMaterialListEntry.getKey();
            List<BuWorkOrderMaterial> orderMaterialList = materialTypeIdOrderMaterialListEntry.getValue();

            List<String> needDispatchOrderMaterialIdList = new ArrayList<>();
            for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
                if (1 == orderMaterial.getNeedDispatchin()) {
                    // 需要发料的工单物料：已领料的应该减去、未领料的不应该减去
                    needDispatchOrderMaterialIdList.add(orderMaterial.getId());
                } else {
                    // 无需发料的工单物料：全部应该减去
                    List<BuWorkOrderMaterialActs> actList = orderMaterial.getMaterialActsList();
                    if (CollectionUtils.isNotEmpty(actList)) {
                        // 有实际消耗的，使用实际消耗列表
                        for (BuWorkOrderMaterialActs act : actList) {
                            addToUsedList(usedList, materialTypeId, act.getGroupStockTrainNo(), act.getTradeNo(), act.getOrderCode(), act.getActAmount(), 2);
                        }
                    } else {
                        // 工单已提交且没有实际消耗的工单物料，不需要减去，原因如下：
                        // 这个工单物料计划消耗但实际没消耗就提交了，此时说明物料不消耗
                        // 如果确实需要的消耗的话，工单提交后会被退回，工单状态变回填报中，此时再进行预占用
                        if (orderMaterial.getOrderStatus() != 3) {
                            // 没有实际消耗，使用计划数量
                            addToUsedList(usedList, materialTypeId, orderMaterial.getTrainNo(), null, orderMaterial.getOrderCode(), orderMaterial.getAmount(), 1);
                        }
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(needDispatchOrderMaterialIdList)) {
                List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailDispatchMapper.selectListForGroupStockCount(needDispatchOrderMaterialIdList);
                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    addToUsedList(usedList, materialTypeId, assignDetail.getTrainNo(), assignDetail.getTradeNo(), assignDetail.getSourceOrderCode(), assignDetail.getAmount(), 1);
                }
            }
        }

        return usedList;
    }

    private void addToUsedList(List<GroupStockUsed> usedList, String materialTypeId, String trainNo, String tradeNo, String orderCode, Double amount, Integer usedType) {
        if (null == usedList) {
            usedList = new ArrayList<>();
        }
        BigDecimal amountBigDecimal = null == amount ? BigDecimal.ZERO : BigDecimal.valueOf(amount);

        boolean matched = false;
        for (GroupStockUsed used : usedList) {
            boolean sameMaterial = materialTypeId.equals(used.getMaterialTypeId());
            boolean sameTrainNo = StringUtils.isBlank(trainNo) ? StringUtils.isBlank(used.getTrainNo()) : trainNo.equals(used.getTrainNo());
            boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(used.getTradeNo()) : tradeNo.equals(used.getTradeNo());
            boolean sameOrder = orderCode.equals(used.getOrderCode());

            if (sameMaterial && sameTrainNo && sameTradeNo && sameOrder) {
                BigDecimal usedAmount = used.getAmount();
                used.setAmount(usedAmount.add(amountBigDecimal));

                matched = true;
                break;
            }
        }
        if (!matched) {
            GroupStockUsed used = new GroupStockUsed()
                    .setMaterialTypeId(materialTypeId)
                    .setTrainNo(trainNo)
                    .setTradeNo(tradeNo)
                    .setOrderCode(orderCode)
                    .setAmount(amountBigDecimal)
                    .setUsedType(usedType);
            usedList.add(used);
        }
    }

    private void decreaseGroupStock(BuMaterialGroupStock groupStock, String usedOrderCode, BigDecimal usedAmount, Integer usedType) {
        if (null == groupStock) {
            return;
        }
        if (BigDecimal.ZERO.compareTo(usedAmount) >= 0) {
            return;
        }
        String usedTypeString = "";
        if (1 == usedType) {
            usedTypeString = "预";
        }

        BigDecimal usableAmount = groupStock.getUsableAmount();
        String usedDetailInfo = groupStock.getUsedDetailInfo();

        groupStock.setUsableAmount(usableAmount.subtract(usedAmount));
        if (StringUtils.isNotBlank(usedDetailInfo)) {
            groupStock.setUsedDetailInfo(usedDetailInfo + "，" + "工单" + usedOrderCode + usedTypeString + "占用" + usedAmount.stripTrailingZeros().toString());
        } else {
            groupStock.setUsedDetailInfo("工单" + usedOrderCode + usedTypeString + "占用" + usedAmount.stripTrailingZeros().toString());
        }
    }

    private void validateProductionNoticeOrder(BuWorkOrder order) {
        if (order == null) {
            return;
        }
        String noticeId = order.getProductionNoticeId();
        if (StringUtils.isBlank(noticeId)) {
            return;
        }
        if (order.getOrderType() == null || order.getOrderType() != 3) {
            throw new JeecgBootException("仅临时工单允许关联技术类生产通知单");
        }
        if (StringUtils.isBlank(order.getTrainNo())) {
            throw new JeecgBootException("关联生产通知单时必须选择车号");
        }
    }

    private void bindProductionNoticeOrder(BuWorkOrder order) {
        if (order == null || StringUtils.isBlank(order.getProductionNoticeId())) {
            return;
        }
        productionNoticeService.bindOrder(order.getProductionNoticeId(), order.getId(), order.getOrderCode(), order.getTrainNo());
    }

}
