package org.jeecg.modules.tiros.custom.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
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
import org.jeecg.common.tiros.cache.warehouse.CacheWarehouseBO;
import org.jeecg.common.tiros.cache.warehouse.WarehouseCacheService;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.stock.change.bean.BuMaterialStockChange;
import org.jeecg.common.tiros.stock.change.service.BuMaterialStockChangeService;
import org.jeecg.common.tiros.stock.use.bean.BuMaterialStockUse;
import org.jeecg.common.tiros.stock.use.service.BuMaterialStockUseService;
import org.jeecg.common.tiros.third.maximo.bean.BuMaximoTransData;
import org.jeecg.common.tiros.third.maximo.service.BuMaximoTransDataService;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.TirosUtil;
import org.jeecg.common.tiros.util.UploadFileCheckUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.workflow.bean.WfBizStatus;
import org.jeecg.common.workflow.service.WfBizStatusService;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.mapper.BuMtrLineMapper;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanMaterial;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanMustReplace;
import org.jeecg.modules.basemanage.tpplan.mapper.BuTpRepairPlanMaterialMapper;
import org.jeecg.modules.basemanage.tpplan.mapper.BuTpRepairPlanMustReplaceMapper;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainInfo;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetTypeMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainInfoMapper;
import org.jeecg.modules.board.workstation.bean.BuMtrWorkstation;
import org.jeecg.modules.board.workstation.mapper.BuMtrWorkstationBoardMapper;
import org.jeecg.modules.dispatch.fault.bean.BuFaultInfo;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultInfoMapper;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanMapper;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.mapper.*;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderService;
import org.jeecg.modules.group.information.bean.BuGroupWorkstation;
import org.jeecg.modules.group.information.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.group.information.mapper.BuGroupWorkstationMapper;
import org.jeecg.modules.group.information.mapper.BuMtrWorkshopGroupMapper;
import org.jeecg.modules.material.apply.bean.BuMaterialApply;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;
import org.jeecg.modules.material.apply.mapper.BuMaterialApplyDetailMapper;
import org.jeecg.modules.material.apply.mapper.BuMaterialApplyMapper;
import org.jeecg.modules.material.apply.mapper.BuMaterialAssignDetailMapper;
import org.jeecg.modules.material.apply.mapper.BuWorkOrderForMaterialMapper;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.mapper.BuMaterialMustListMapper;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.mapper.BuMaterialGroupStockMapper;
import org.jeecg.modules.material.stock.mapper.BuMaterialStockMapper;
import org.jeecg.modules.material.stock.service.BuMaterialStockService;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.mapper.BuMtrWarehouseMapper;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryFault;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainHistoryFaultMapper;
import org.jeecg.modules.report.cost.bean.vo.*;
import org.jeecg.modules.report.cost.service.BuWorkOrderMaterialReportService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.jeecg.modules.tiros.custom.bean.bo.*;
import org.jeecg.modules.tiros.custom.service.CustomService;
import org.jeecg.modules.workflow.mapper.WfBizStatusMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 自定义 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-04
 */
@Slf4j
@Service
public class CustomServiceImpl implements CustomService {

    @Resource
    private BuMaterialStockMapper buMaterialStockMapper;
    @Resource
    private BuMaterialGroupStockDispatchMapper buMaterialGroupStockDispatchMapper;
    @Resource
    private BuMaterialGroupStockMapper buMaterialGroupStockMapper;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private BuMaterialAssignDetailMapper buMaterialAssignDetailMapper;
    @Resource
    private BuMaterialApplyDetailMapper buMaterialApplyDetailMapper;
    @Resource
    private BuMaterialApplyMapper buMaterialApplyMapper;
    @Resource
    private BuWorkOrderMaterialActsMapper buWorkOrderMaterialActsMapper;
    @Resource
    private BuWorkOrderMaterialMapper buWorkOrderMaterialMapper;
    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;
    @Resource
    private BuWorkOrderTaskMapper buWorkOrderTaskMapper;
    @Resource
    private BuTpRepairPlanMaterialMapper buTpRepairPlanMaterialMapper;
    @Resource
    private BuTpRepairPlanMustReplaceMapper buTpRepairPlanMustReplaceMapper;
    @Resource
    private BuRepairPlanMapper buRepairPlanMapper;
    @Resource
    private BuMaterialMustListMapper buMaterialMustListMapper;
    @Resource
    private BuGroupWorkstationMapper buGroupWorkstationMapper;
    @Resource
    private BuMtrWorkstationBoardMapper buMtrWorkstationBoardMapper;
    @Resource
    private BuMaximoTransDataService buMaximoTransDataService;
    @Resource
    private BuTrainAssetTypeMapper buTrainAssetTypeMapper;
    @Resource
    private BuMtrWarehouseMapper buMtrWarehouseMapper;
    @Resource
    private BuWorkOrderForMaterialMapper buWorkOrderForMaterialMapper;
    @Resource
    private BuMtrLineMapper buMtrLineMapper;
    @Resource
    private BuTrainInfoMapper buTrainInfoMapper;
    @Resource
    private BuMtrWorkshopGroupMapper buMtrWorkshopGroupMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private BuTrainHistoryFaultMapper buTrainHistoryFaultMapper;
    @Resource
    private BuFaultInfoMapper buFaultInfoMapper;
    @Resource
    private BuRepairTaskStaffArrangeMapper buRepairTaskStaffArrangeMapper;
    @Resource
    private WarehouseCacheService warehouseCacheService;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private BuWorkOrderMaterialReportService buWorkOrderMaterialReportService;
    @Resource
    private BuMaterialStockUseService buMaterialStockUseService;
    @Resource
    private BuMaterialStockChangeService buMaterialStockChangeService;
    @Resource
    private WfBizStatusMapper wfBizStatusMapper;
    @Resource
    private WorkflowForwardService workflowForwardService;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;


    /**
     * @see CustomService#updatePlanGenerateOrderTaskTaskObjIdIfNull(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updatePlanGenerateOrderTaskTaskObjIdIfNull(String planId) throws Exception {
        // 查询没有关联列计划任务的工单的任务
        List<Map<String, Object>> orderTaskIdPlanTaskIdMapList = buWorkOrderTaskMapper.selectPlanGenerateOrderTaskAndTaskObjIdNullByPlanId(planId);
        List<BuWorkOrderTask> taskList = new ArrayList<>();
        for (Map<String, Object> orderTaskIdPlanTaskIdMap : orderTaskIdPlanTaskIdMapList) {
            String orderTaskId = (String) orderTaskIdPlanTaskIdMap.get("orderTaskId");
            String planTaskId = (String) orderTaskIdPlanTaskIdMap.get("planTaskId");

            if (StringUtils.isNotEmpty(orderTaskId) && StringUtils.isNotBlank(planTaskId)) {
                BuWorkOrderTask task = new BuWorkOrderTask()
                        .setId(orderTaskId)
                        .setTaskObjId(planTaskId);
                taskList.add(task);
            }
        }
        if (CollectionUtils.isNotEmpty(taskList)) {
            List<List<BuWorkOrderTask>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskList);
            for (List<BuWorkOrderTask> batchSub : batchSubList) {
                buWorkOrderTaskMapper.updateListTaskObjId(batchSub);
            }
        }

        return String.format("查询到由列计划生成的、任务对象id为空、的工单任务%s条，更新了%s条的任务对象id；", orderTaskIdPlanTaskIdMapList.size(), taskList.size());
    }

    /**
     * @see CustomService#updateMaterialFromTpPlanToOrder(String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateMaterialFromTpPlanToOrder(String planId, String groupId) throws Exception {
        // 查询没有物料的计划工单任务及计划模板信息
        List<Map<String, Object>> idMapList = buWorkOrderTaskMapper.selectNullMaterialOrderTaskAndTpPlanTask(planId, groupId);
        Set<String> orderIdSet = new HashSet<>();
        Set<String> tpPlanTaskIdSet = new HashSet<>();
        for (Map<String, Object> idMap : idMapList) {
            String orderId = (String) idMap.get("orderId");
            if (StringUtils.isNotBlank(orderId)) {
                orderIdSet.add(orderId);
            }
            String tpPlanTaskId = (String) idMap.get("tpPlanTaskId");
            if (StringUtils.isNotBlank(tpPlanTaskId)) {
                tpPlanTaskIdSet.add(tpPlanTaskId);
            }
        }
        if (CollectionUtils.isEmpty(orderIdSet)) {
            return "无可更新的工单信息；";
        }
        if (CollectionUtils.isEmpty(tpPlanTaskIdSet)) {
            return orderIdSet.size() + "条工单无物料，" + "找不到对应的计划模板任务；";
        }

        // 查询计划模板任务的物料和必换件
        List<BuTpRepairPlanMaterial> allMaterialList = new ArrayList<>();
        List<BuTpRepairPlanMustReplace> allMustReplaceList = new ArrayList<>();
        List<List<String>> tpPlanTaskIdBatchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(tpPlanTaskIdSet));
        for (List<String> tpPlanTaskIdBatchSub : tpPlanTaskIdBatchSubList) {
            LambdaQueryWrapper<BuTpRepairPlanMaterial> materialWrapper = new LambdaQueryWrapper<BuTpRepairPlanMaterial>()
                    .in(BuTpRepairPlanMaterial::getTaskId, tpPlanTaskIdBatchSub);
            List<BuTpRepairPlanMaterial> subMaterialList = buTpRepairPlanMaterialMapper.selectList(materialWrapper);
            allMaterialList.addAll(subMaterialList);

            LambdaQueryWrapper<BuTpRepairPlanMustReplace> mustReplaceWrapper = new LambdaQueryWrapper<BuTpRepairPlanMustReplace>()
                    .in(BuTpRepairPlanMustReplace::getTaskId, tpPlanTaskIdBatchSub);
            List<BuTpRepairPlanMustReplace> subMustReplaceList = buTpRepairPlanMustReplaceMapper.selectList(mustReplaceWrapper);
            allMustReplaceList.addAll(subMustReplaceList);
        }

        if (CollectionUtils.isEmpty(allMaterialList) && CollectionUtils.isEmpty(allMustReplaceList)) {
            return orderIdSet.size() + "条工单无物料，" + "对应的计划模板任务无可更新的物料信息；";
        }

        // 查询物资类型
        Map<String, Integer> materialTypeIdCategory1Map = new HashMap<>();
        Set<String> materialTypeIdSet = allMaterialList.stream()
                .map(BuTpRepairPlanMaterial::getMaterialTypeId)
                .collect(Collectors.toSet());
        List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(materialTypeIdSet));
        for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
            LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                    .in(BuMaterialType::getId, materialTypeIdBatchSub)
                    .select(BuMaterialType::getId, BuMaterialType::getCategory1);
            List<BuMaterialType> subMaterialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);
            subMaterialTypeList.forEach(materialType -> materialTypeIdCategory1Map.put(materialType.getId(), materialType.getCategory1()));
        }

        List<BuWorkOrderMaterial> orderMaterialList = new ArrayList<>();
        for (Map<String, Object> idMap : idMapList) {
            String orderId = (String) idMap.get("orderId");
            String orderTaskId = (String) idMap.get("orderTaskId");
            String tpPlanTaskId = (String) idMap.get("tpPlanTaskId");

            List<BuTpRepairPlanMaterial> matchMaterialList = allMaterialList.stream()
                    .filter(material -> tpPlanTaskId.equals(material.getTaskId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(matchMaterialList)) {
                Map<String, List<BuTpRepairPlanMaterial>> materialTypeIdMaterialListMap = matchMaterialList.stream()
                        .collect(Collectors.groupingBy(BuTpRepairPlanMaterial::getMaterialTypeId));
                for (Map.Entry<String, List<BuTpRepairPlanMaterial>> materialTypeIdMaterialListEntry : materialTypeIdMaterialListMap.entrySet()) {
                    String materialTypeId = materialTypeIdMaterialListEntry.getKey();
                    List<BuTpRepairPlanMaterial> materialList = materialTypeIdMaterialListEntry.getValue();
                    double amountSum = materialList.stream()
                            .mapToDouble(BuTpRepairPlanMaterial::getAmount)
                            .sum();
                    String materialIds = materialList.stream()
                            .map(BuTpRepairPlanMaterial::getId)
                            .collect(Collectors.joining(","));
                    Integer category1 = materialTypeIdCategory1Map.getOrDefault(materialTypeId, -1);

                    BuWorkOrderMaterial orderMaterial = new BuWorkOrderMaterial()
                            .setId(UUIDGenerator.generate())
                            .setOrderId(orderId)
                            .setOrderTaskId(orderTaskId)
                            .setMaterialTypeId(materialTypeId)
                            .setAmount(amountSum)
                            .setPlanAmount(amountSum)
                            .setActAmount(0D)
                            .setRemark("由“更新计划模板的物料必换件到工单（如果工单物料为空）”方法生成，计划模板物料id=" + materialIds)
                            .setNeedDispatchin(0)
                            .setUseCategory(category1)
                            .setIsVerify(0)
                            .setOpType(1)
                            .setIsGenOrder(0);
                    orderMaterialList.add(orderMaterial);
                }
            }

            List<BuTpRepairPlanMustReplace> matchMustReplaceList = allMustReplaceList.stream()
                    .filter(mustReplace -> tpPlanTaskId.equals(mustReplace.getTaskId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(matchMustReplaceList)) {
                Map<String, List<BuTpRepairPlanMustReplace>> materialTypeIdMustReplaceListMap = matchMustReplaceList.stream()
                        .collect(Collectors.groupingBy(BuTpRepairPlanMustReplace::getMaterialTypeId));
                for (Map.Entry<String, List<BuTpRepairPlanMustReplace>> materialTypeIdMustReplaceListEntry : materialTypeIdMustReplaceListMap.entrySet()) {
                    String materialTypeId = materialTypeIdMustReplaceListEntry.getKey();
                    List<BuTpRepairPlanMustReplace> mustReplaceList = materialTypeIdMustReplaceListEntry.getValue();
                    double amountSum = mustReplaceList.stream()
                            .mapToDouble(BuTpRepairPlanMustReplace::getAmount)
                            .sum();
                    String mustReplaceIds = mustReplaceList.stream()
                            .map(BuTpRepairPlanMustReplace::getId)
                            .collect(Collectors.joining(","));

                    BuWorkOrderMaterial orderMaterial = new BuWorkOrderMaterial()
                            .setId(UUIDGenerator.generate())
                            .setOrderId(orderId)
                            .setOrderTaskId(orderTaskId)
                            .setMaterialTypeId(materialTypeId)
                            .setAmount(amountSum)
                            .setPlanAmount(amountSum)
                            .setActAmount(0D)
                            .setRemark("由“更新计划模板的物料必换件到工单（如果工单物料为空）”方法生成，计划模板必换件id=" + mustReplaceIds)
                            .setNeedDispatchin(0)
                            .setUseCategory(1)
                            .setIsVerify(0)
                            .setOpType(1)
                            .setIsGenOrder(0);
                    orderMaterialList.add(orderMaterial);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            List<List<BuWorkOrderMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderMaterialList);
            for (List<BuWorkOrderMaterial> batchSub : batchSubList) {
                buWorkOrderMaterialMapper.insertList(batchSub);
            }
        }

        return String.format("%s条工单无物料，根据对应的计划模板任务物料和必换件生成了工单物料%s条；", orderIdSet.size(), orderMaterialList.size());
    }

    /**
     * @see CustomService#generateMaterialConsumeCorrectSql(MaterialConsumeCorrect)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String generateMaterialConsumeCorrectSql(MaterialConsumeCorrect correct) throws Exception {
        Date now = new Date();

        // 查询工单和任务id
        LambdaQueryWrapper<BuWorkOrder> orderWrapper = new LambdaQueryWrapper<BuWorkOrder>()
                .eq(BuWorkOrder::getOrderCode, correct.getOrderCode());
        BuWorkOrder order = buWorkOrderMapper.selectOne(orderWrapper);
        if (null == order) {
            throw new JeecgBootException("工单[" + correct.getOrderCode() + "]不存在");
        }
        LambdaQueryWrapper<BuWorkOrderTask> orderTaskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                .eq(BuWorkOrderTask::getOrderId, order.getId());
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskMapper.selectList(orderTaskWrapper);
        if (CollectionUtils.isEmpty(orderTaskList)) {
            throw new JeecgBootException("工单[" + correct.getOrderCode() + "]下没有任务");
        }
        if (orderTaskList.size() > 1) {
            throw new JeecgBootException("工单[" + correct.getOrderCode() + "]下有" + orderTaskList.size() + "个任务，请确认");
        }
        BuWorkOrderTask orderTask = orderTaskList.get(0);

        // 查询物资类型
        BuMaterialType materialType = buMaterialTypeMapper.selectById(correct.getMaterialTypeId());
        if (null == materialType) {
            throw new JeecgBootException("物资类型[" + correct.getMaterialTypeId() + "]不存在");
        }

        // 查询班组库存
        LambdaQueryWrapper<BuMaterialGroupStock> groupStockWrapper = new LambdaQueryWrapper<BuMaterialGroupStock>()
                .eq(BuMaterialGroupStock::getGroupId, correct.getGroupId())
                .eq(BuMaterialGroupStock::getMaterialTypeId, correct.getMaterialTypeId());
        List<BuMaterialGroupStock> groupStockList = buMaterialGroupStockMapper.selectList(groupStockWrapper);

        if ("ldgb".equals(correct.getGroupId()) && "010111080067".equals(correct.getMaterialTypeId())) {
            log.debug("debug");
        }

        // 生成结果
        Integer type = correct.getType();// 1新增 2修改 3删除
        if (!Arrays.asList(1, 2, 3).contains(type)) {
            throw new JeecgBootException("请选择输入正确的处理类型：1新增 2修改 3删除");
        }
        if (1 == type) {
            // 验证班组库存
            groupStockList.removeIf(groupStock -> null == groupStock.getAmount() || groupStock.getAmount().compareTo(BigDecimal.ZERO) <= 0);
            if (CollectionUtils.isEmpty(groupStockList)) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]没有物资[" + correct.getMaterialTypeId() + "]的库存");
            }
            if (StringUtils.isNotBlank(correct.getAssignTradeNo())) {
                groupStockList.removeIf(groupStock -> !correct.getAssignTradeNo().equals(groupStock.getTradeNo()));
            }
            if (groupStockList.size() > 1) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]有" + groupStockList.size() + "个库存记录，请确认");
            }
            BuMaterialGroupStock groupStock = groupStockList.get(0);

            // 1新增
            if (-1 == materialType.getCategory1() && null == correct.getUseCategory()) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]使用类型不确定，请输入");
            }

            BigDecimal amount = correct.getAmount();
            if (groupStock.getAmount().compareTo(amount) < 0) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]库存不足" + amount + "，请确认");
            }

            Integer useCategory = null == correct.getUseCategory() ? materialType.getCategory1() : correct.getUseCategory();
            String remark = "手动修正物料实际消耗时增加数据";
            String orderMaterialId = UUIDGenerator.generate();
            String orderMaterialActId = UUIDGenerator.generate();

            String orderMaterialSql = String.format(
                    "INSERT INTO BU_WORK_ORDER_MATERIAL " +
                            "(id, order_id, order_task_id, material_type_id, amount, act_amount, remark, use_category, op_type, is_gen_order, is_verify, plan_amount, need_dispatchin, system_id, workstation_id) " +
                            "VALUES " +
                            "('%s','%s','%s','%s',%s,%s,'%s',%s,1,0,0,%s,0,'%s','%s');",
                    orderMaterialId,
                    order.getId(),
                    orderTask.getId(),
                    correct.getMaterialTypeId(),
                    amount,
                    amount,
                    remark,
                    useCategory,
                    amount,
                    "",
                    ""
            );
            String orderMaterialActSql = String.format(
                    "INSERT INTO BU_WORK_ORDER_MATERIAL_ACTS " +
                            "(id, order_material_id, group_stock_id, apply_id, apply_detail_id, assign_detail_id, trade_no, act_amount, create_time, create_by, update_time, update_by, price) " +
                            "VALUES " +
                            "('%s','%s','%s','%s','%s','%s','%s',%s,%s,'admin',%s,'admin',%s);",
                    orderMaterialActId,
                    orderMaterialId,
                    groupStock.getId(),
                    groupStock.getApplyId(),
                    groupStock.getApplyDetailId(),
                    groupStock.getAssignDetailId(),
                    groupStock.getTradeNo(),
                    amount,
                    "TO_DATE('" + DateUtils.datetimeFormat.get().format(now) + "', 'yyyy-mm-dd hh24:mi:ss')",
                    "TO_DATE('" + DateUtils.datetimeFormat.get().format(now) + "', 'yyyy-mm-dd hh24:mi:ss')",
                    groupStock.getPrice()
            );
            BigDecimal oldGroupStockAmount = groupStock.getAmount();
            BigDecimal newGroupStockAmount = oldGroupStockAmount.subtract(correct.getAmount());
            String groupStockDecreaseSql = String.format(
                    "update BU_MATERIAL_GROUP_STOCK set AMOUNT = %s where id = '%s'; ",
                    newGroupStockAmount,
                    groupStock.getId()
            );

            String orderMaterialSqlNote = String.format("-- 班组[%s]工单[%s]物料[%s]实际消耗增加%s个的工单物料sql:",
                    correct.getGroupId(), correct.getOrderCode(), correct.getMaterialTypeId(), correct.getAmount());
            String orderMaterialActSqlNote = String.format("-- 班组[%s]工单[%s]物料[%s]实际消耗增加%s个的工单物料实际消耗sql:",
                    correct.getGroupId(), correct.getOrderCode(), correct.getMaterialTypeId(), correct.getAmount());
            String groupStockDecreaseSqlNote = String.format("-- 班组[%s]物料[%s]库存减少%s个的sql:",
                    correct.getGroupId(), correct.getMaterialTypeId(), correct.getAmount());

            return orderMaterialSqlNote
                    + System.lineSeparator()
                    + orderMaterialSql
                    + System.lineSeparator()
                    + orderMaterialActSqlNote
                    + System.lineSeparator()
                    + orderMaterialActSql
                    + System.lineSeparator()
                    + groupStockDecreaseSqlNote
                    + System.lineSeparator()
                    + groupStockDecreaseSql;
        } else if (2 == type) {
            // 2修改
            LambdaQueryWrapper<BuWorkOrderMaterial> orderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                    .eq(BuWorkOrderMaterial::getOrderId, order.getId())
                    .eq(BuWorkOrderMaterial::getOrderTaskId, orderTask.getId())
                    .eq(BuWorkOrderMaterial::getMaterialTypeId, correct.getMaterialTypeId());
            List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMapper.selectList(orderMaterialWrapper);
            if (CollectionUtils.isEmpty(orderMaterialList)) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]的工单物料不存在");
            }
            if (orderMaterialList.size() > 1) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]的工单物料有" + orderMaterialList.size() + "个记录，请确认");
            }

            BuWorkOrderMaterial orderMaterial = orderMaterialList.get(0);
            LambdaQueryWrapper<BuWorkOrderMaterialActs> orderMaterialActsWrapper = new LambdaQueryWrapper<BuWorkOrderMaterialActs>()
                    .eq(BuWorkOrderMaterialActs::getOrderMaterialId, orderMaterial.getId());
            List<BuWorkOrderMaterialActs> orderMaterialActsList = buWorkOrderMaterialActsMapper.selectList(orderMaterialActsWrapper);
            if (CollectionUtils.isEmpty(orderMaterialActsList)) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]的工单物料实际消耗不存在");
            }
            if (orderMaterialActsList.size() > 1) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]的工单物料实际消耗有" + orderMaterialActsList.size() + "个记录，请确认");
            }
            BuWorkOrderMaterialActs orderMaterialActs = orderMaterialActsList.get(0);

            BigDecimal amount = correct.getAmount();
            String remark = "手动修正物料实际消耗" + orderMaterial.getActAmount() + "" + amount + "时修改数据";

            String orderMaterialSql = String.format(
                    "UPDATE BU_WORK_ORDER_MATERIAL set act_amount = %s, remark = '%s' WHERE id = %s;",
                    amount,
                    remark,
                    "(SELECT id from BU_WORK_ORDER_MATERIAL WHERE order_id = (select id from BU_WORK_ORDER WHERE order_code = '" + correct.getOrderCode() + "') and material_type_id = '" + correct.getMaterialTypeId() + "')"
            );
            String orderMaterialActSql = String.format(
                    "UPDATE BU_WORK_ORDER_MATERIAL_ACTS set act_amount = %s WHERE order_material_id = %s;",
                    amount,
                    "(SELECT id from BU_WORK_ORDER_MATERIAL WHERE order_id = (select id from BU_WORK_ORDER WHERE order_code = '" + correct.getOrderCode() + "') and material_type_id = '" + correct.getMaterialTypeId() + "')"
            );

            String orderMaterialSqlNote = String.format("-- 班组[%s]工单[%s]物料[%s]实际消耗由%s修改为%s个的工单物料sql:",
                    correct.getGroupId(), correct.getOrderCode(), correct.getMaterialTypeId(), orderMaterial.getActAmount(), correct.getAmount());
            String orderMaterialActSqlNote = String.format("-- 班组[%s]工单[%s]物料[%s]实际消耗由%s修改为%s个的工单物料实际消耗sql:",
                    correct.getGroupId(), correct.getOrderCode(), correct.getMaterialTypeId(), orderMaterialActs.getActAmount(), correct.getAmount());

            return orderMaterialSqlNote
                    + System.lineSeparator()
                    + orderMaterialSql
                    + System.lineSeparator()
                    + orderMaterialActSqlNote
                    + System.lineSeparator()
                    + orderMaterialActSql;
        }
        if (3 == type) {
            // 3删除
            LambdaQueryWrapper<BuWorkOrderMaterial> orderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                    .eq(BuWorkOrderMaterial::getOrderId, order.getId())
                    .eq(BuWorkOrderMaterial::getOrderTaskId, orderTask.getId())
                    .eq(BuWorkOrderMaterial::getMaterialTypeId, correct.getMaterialTypeId());
            List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMapper.selectList(orderMaterialWrapper);
            if (CollectionUtils.isEmpty(orderMaterialList)) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]的工单物料不存在");
            }
            if (orderMaterialList.size() > 1) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]的工单物料有" + orderMaterialList.size() + "个记录，请确认");
            }

            BuWorkOrderMaterial orderMaterial = orderMaterialList.get(0);
            LambdaQueryWrapper<BuWorkOrderMaterialActs> orderMaterialActsWrapper = new LambdaQueryWrapper<BuWorkOrderMaterialActs>()
                    .eq(BuWorkOrderMaterialActs::getOrderMaterialId, orderMaterial.getId());
            List<BuWorkOrderMaterialActs> orderMaterialActsList = buWorkOrderMaterialActsMapper.selectList(orderMaterialActsWrapper);
            if (CollectionUtils.isEmpty(orderMaterialActsList)) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]的工单物料实际消耗不存在");
            }
            if (orderMaterialActsList.size() > 1) {
                throw new JeecgBootException("班组[" + correct.getGroupId() + "]物资[" + correct.getMaterialTypeId() + "]的工单物料实际消耗有" + orderMaterialActsList.size() + "个记录，请确认");
            }
//            BuWorkOrderMaterialActs orderMaterialActs = orderMaterialActsList.get(0);

            String orderMaterialActSql = String.format(
                    "DELETE from BU_WORK_ORDER_MATERIAL_ACTS WHERE order_material_id = %s;",
                    "(SELECT id from BU_WORK_ORDER_MATERIAL WHERE order_id = (select id from BU_WORK_ORDER WHERE order_code = '" + correct.getOrderCode() + "') and material_type_id = '" + correct.getMaterialTypeId() + "')"
            );

            String orderMaterialSql = String.format(
                    "DELETE from BU_WORK_ORDER_MATERIAL WHERE id = %s;",
                    "(SELECT id from BU_WORK_ORDER_MATERIAL WHERE order_id = (select id from BU_WORK_ORDER WHERE order_code = '" + correct.getOrderCode() + "') and material_type_id = '" + correct.getMaterialTypeId() + "')"
            );

            String orderMaterialActSqlNote = String.format("-- 班组[%s]工单[%s]物料[%s]实际消耗删除的工单物料实际消耗sql:",
                    correct.getGroupId(), correct.getOrderCode(), correct.getMaterialTypeId());
            String orderMaterialSqlNote = String.format("-- 班组[%s]工单[%s]物料[%s]实际消耗删除的工单物料sql:",
                    correct.getGroupId(), correct.getOrderCode(), correct.getMaterialTypeId());

            return orderMaterialActSqlNote
                    + System.lineSeparator()
                    + orderMaterialActSql
                    + System.lineSeparator()
                    + orderMaterialSqlNote
                    + System.lineSeparator()
                    + orderMaterialSql;
        }

        return "未成功生成";
    }

    /**
     * @see CustomService#generateMaterialConsumeCorrectSqlByExcel(MultipartFile, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String generateMaterialConsumeCorrectSqlByExcel(MultipartFile excelFile, String exportDisk) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(excelFile, null, Arrays.asList(".xls", ".xlsx"));

        // 获取表单数据
        if (excelFile.isEmpty()) {
            throw new JeecgBootException("文件为空");
        }
        String filename = excelFile.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new JeecgBootException("文件名为空");
        }
        InputStream inputStream = excelFile.getInputStream();
        Workbook inWorkbook;
        String extName = filename.substring(filename.lastIndexOf(".") + 1);
        if (StringUtils.equals("xls", extName)) {
            inWorkbook = new HSSFWorkbook(inputStream);
        } else if (StringUtils.equals("xlsx", extName)) {
            inWorkbook = new XSSFWorkbook(inputStream);
        } else {
            throw new JeecgBootException("错误的excel文件");
        }
        inputStream.close();

        Sheet sheet = inWorkbook.getSheetAt(0);
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        // 获取数据单元格的对应位置
        Integer indexCellNum = null;
        Integer groupIdCellNum = null;
        Integer orderCodeCellNum = null;
        Integer materialTypeIdCellNum = null;
        Integer userCategoryCellNum = null;
        Integer amountCellNum = null;
        Integer typeCellNum = null;
        Integer assignTradeNoCellNum = null;
        Row row = sheet.getRow(firstRowNum);
        if (null != row) {
            short firstCellNum = row.getFirstCellNum();
            short lastCellNum = row.getLastCellNum();
            for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                Cell cell = row.getCell(cellNum);
                if (null != cell) {
                    String value = getCellValue(cell);
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }
                    if (value.equals("序号")) {
                        indexCellNum = cellNum;
                    } else if (value.equals("工班")) {
                        groupIdCellNum = cellNum;
                    } else if (value.equals("工单号")) {
                        orderCodeCellNum = cellNum;
                    } else if (value.contains("物资类型")) {
                        materialTypeIdCellNum = cellNum;
                    } else if (value.contains("处理类型")) {
                        typeCellNum = cellNum;
                    } else if (value.contains("使用类型")) {
                        userCategoryCellNum = cellNum;
                    } else if (value.contains("数量")) {
                        amountCellNum = cellNum;
                    } else if (value.contains("指定批次号")) {
                        assignTradeNoCellNum = cellNum;
                    }
                }
            }
        }

        List<MaterialConsumeCorrect> correctList = new ArrayList<>();
        int rowCount = 0;
        // 操作每行数据
        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
            String index = getCellValue(sheet.getRow(rowNum).getCell(indexCellNum));
            String groupId = getCellValue(sheet.getRow(rowNum).getCell(groupIdCellNum));
            String orderCode = getCellValue(sheet.getRow(rowNum).getCell(orderCodeCellNum));
            String materialTypeId = getCellValue(sheet.getRow(rowNum).getCell(materialTypeIdCellNum));
            String userCategory = getCellValue(sheet.getRow(rowNum).getCell(userCategoryCellNum));
            String amount = getCellValue(sheet.getRow(rowNum).getCell(amountCellNum));
            String type = getCellValue(sheet.getRow(rowNum).getCell(typeCellNum));
            String assignTradeNo = getCellValue(sheet.getRow(rowNum).getCell(assignTradeNoCellNum));

            if ("ldgb".equals(groupId) && "010111080067".equals(materialTypeId)) {
                log.debug("debug");
            }

            // 序号
            int indexInt;
            if (StringUtils.isBlank(index)) {
                throw new JeecgBootException("请输入正确序号，用于对应sql语句到缺料信息");
            }
            indexInt = Integer.parseInt(index);

            // 处理类型：1新增 2修改 3删除
            int typeInt = 0;
            if ("新增".equals(type)) {
                typeInt = 1;
            } else if ("修改".equals(type)) {
                typeInt = 2;
            } else if ("删除".equals(type)) {
                typeInt = 3;
            }

            // 使用类型：1必换件 2偶换件 3耗材
            Integer useCategoryInt;
            if (StringUtils.isBlank(userCategory)) {
                useCategoryInt = null;
            } else if ("必换件".equals(userCategory)) {
                useCategoryInt = 1;
            } else if ("偶换件".equals(userCategory)) {
                useCategoryInt = 2;
            } else if ("耗材".equals(userCategory)) {
                useCategoryInt = 3;
            } else {
                throw new JeecgBootException("请输入正确的使用类型，或者为空");
            }

            // 数量
            BigDecimal amountBigDecimal = BigDecimal.ZERO;
            if (StringUtils.isNotBlank(amount)) {
                amountBigDecimal = new BigDecimal(amount);
            }

            correctList.add(
                    new MaterialConsumeCorrect()
                            .setIndex(indexInt)
                            .setGroupId(groupId)
                            .setOrderCode(orderCode)
                            .setMaterialTypeId(materialTypeId)
                            .setUseCategory(useCategoryInt)
                            .setAmount(amountBigDecimal)
                            .setType(typeInt)
                            .setAssignTradeNo(assignTradeNo)
            );

            rowCount++;
        }
        log.info("总共行数" + rowCount);

        if (CollectionUtils.isNotEmpty(correctList)) {
            for (MaterialConsumeCorrect correct : correctList) {
                String correctSql;
                if (StringUtils.isBlank(correct.getGroupId()) || StringUtils.isBlank(correct.getOrderCode())) {
                    correctSql = "-- 无";
                } else {
                    correctSql = generateMaterialConsumeCorrectSql(correct);
                }
                correct.setCorrectSql(correctSql);
            }
        }

        String resultString = "sql已导出到文件：";
        // 是否导出sql语句到excel
        if (CollectionUtils.isNotEmpty(correctList)) {
            HSSFWorkbook correctSqlWorkbook = getExcelOfCorrectSql(correctList);

            try {
                String fileName = String.format("手动修正物料实际消耗sql-%s", correctSqlWorkbook.getSheetAt(0).getSheetName());

                // 输出Excel文件
                if (StringUtils.isNotBlank(exportDisk)) {
                    exportDisk = "D";
                }
                String outFilePathName = exportDisk + ":\\" + fileName + ".xls";
                FileOutputStream outputStream = new FileOutputStream(outFilePathName);
                correctSqlWorkbook.write(outputStream);
                outputStream.flush();
                outputStream.close();

                resultString = resultString + outFilePathName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    /**
     * @see CustomService#updateOrderMaterialSystemAndWorkstation(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateOrderMaterialSystemAndWorkstation(String planId) throws Exception {
        if (StringUtils.isBlank(planId)) {
            throw new JeecgBootException("请选择列计划");
        }
        BuRepairPlan plan = buRepairPlanMapper.selectById(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }
        String lineId = plan.getLineId();
        String repairProgramId = plan.getRepairProgramId();

        // 查询系统或工位无效的工单物料
        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMapper.selectSystemOrWorkstationInvalidList(planId);
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return "没有系统或工位无效的工单物料";
        }
        String var1 = String.format("系统或工位无效的工单物料共有%s条；", orderMaterialList.size());

        // 查询物资类型id和code
        Map<String, String> materialTypeCodeIdMap = new HashMap<>();
//        Map<String, String> materialTypeIdCodeMap = new HashMap<>();
        LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                .select(BuMaterialType::getId, BuMaterialType::getCode);
        List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);
        for (BuMaterialType materialType : materialTypeList) {
            materialTypeCodeIdMap.put(materialType.getCode(), materialType.getId());
//            materialTypeIdCodeMap.put(materialType.getId(), materialType.getCode());
        }

        // 查询所有必换件
        List<BuMaterialMustList> allMustList = buMaterialMustListMapper.selectList(Wrappers.emptyWrapper());
        // 转成物资类型id-必换件Map
        Map<String, List<BuMaterialMustList>> materialTypeIdMustListMap = new HashMap<>();
        for (BuMaterialMustList must : allMustList) {
            List<BuMaterialMustList> innerList = materialTypeIdMustListMap.getOrDefault(must.getMaterialTypeId(), new ArrayList<>());
            if (!innerList.contains(must)) {
                innerList.add(must);
            }
            materialTypeIdMustListMap.put(must.getMaterialTypeId(), innerList);

            // 可替换物资
            if (StringUtils.isNotBlank(must.getCanReplace())) {
                String canReplace = must.getCanReplace();
                String[] codes = canReplace.split(",");
                for (String code : codes) {
                    String materialTypeId = materialTypeCodeIdMap.get(code);

                    List<BuMaterialMustList> innerList1 = materialTypeIdMustListMap.getOrDefault(materialTypeId, new ArrayList<>());
                    if (!innerList1.contains(must)) {
                        innerList1.add(must);
                    }
                    materialTypeIdMustListMap.put(materialTypeId, innerList1);
                }
            }
        }

//        int allMatchCount = 0;
//        int lineRepairProgramMatchCount = 0;
//        int lineMatchCount = 0;
//        int onlyMaterialMatchCount = 0;
//        int allNotMatchCount = 0;
        int matchCount = 0;
        int notMatchCount = 0;
//        Set<String> notInMustMaterialCodeSet = new HashSet<>();
        List<BuWorkOrderMaterial> needUpdateOrderMaterialList = new ArrayList<>();
        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            String materialTypeId = orderMaterial.getMaterialTypeId();

            // 先根据物质类型id找必换件
            List<BuMaterialMustList> mustList = materialTypeIdMustListMap.getOrDefault(materialTypeId, new ArrayList<>());

            // 过滤符合线路、修程、班组
            List<BuMaterialMustList> matchMustList = mustList.stream()
                    .filter(must -> lineId.equals(must.getLineId())
                            && repairProgramId.equals(must.getRepairProgramId())
                            && orderMaterial.getGroupId().equals(must.getGroupId()))
                    .collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(matchMustList)) {
//                allMatchCount++;
//            } else {
//                // 找不到，只过滤线路、修程
//                matchMustList = mustList.stream()
//                        .filter(must -> lineId.equals(must.getLineId())
//                                && repairProgramId.equals(must.getRepairProgramId()))
//                        .collect(Collectors.toList());
//                if (CollectionUtils.isNotEmpty(matchMustList)) {
//                    lineRepairProgramMatchCount++;
//                } else {
//                    // 再找不到，只过滤线路
//                    matchMustList = mustList.stream()
//                            .filter(must -> lineId.equals(must.getLineId()))
//                            .collect(Collectors.toList());
//                    if (CollectionUtils.isNotEmpty(matchMustList)) {
//                        lineMatchCount++;
//                    } else {
//                        // 再找不到，不过滤
//                        if (CollectionUtils.isEmpty(matchMustList)) {
//                            matchMustList = mustList;
//                        }
//                        if (CollectionUtils.isNotEmpty(matchMustList)) {
//                            onlyMaterialMatchCount++;
//                        }
//                    }
//                }
//            }

            if (CollectionUtils.isNotEmpty(matchMustList)) {
                matchCount++;

                BuMaterialMustList must = matchMustList.get(0);
                orderMaterial.setSystemId(must.getSysId())
                        .setWorkstationId(must.getWorkstationId());
                needUpdateOrderMaterialList.add(orderMaterial);
            } else {
                notMatchCount++;
//                allNotMatchCount++;
//                String materialTypeCode = materialTypeIdCodeMap.get(orderMaterial.getMaterialTypeId());
//                if (StringUtils.isNotBlank(materialTypeCode)) {
//                    notInMustMaterialCodeSet.add(materialTypeCode);
//                }
            }
        }
//        String var2 = String.format("【线路、修程、班组匹配】的有%s条；" +
//                        "【线路、修程匹配】的有%s条；" +
//                        "【线路匹配】的有%s条；" +
//                        "【全部不匹配、仅能匹配物资类型】的有%s条；" +
//                        "【全部不匹配、且物资类型在必换件中不存在】的有%s条，物资编码为（%s）；",
//                allMatchCount,
//                lineRepairProgramMatchCount,
//                lineMatchCount,
//                onlyMaterialMatchCount,
//                allNotMatchCount, String.join(",", notInMustMaterialCodeSet));
        String var2 = String.format("【线路、修程、班组、物资匹配】的有%s条；" +
                        "【全部不匹配】的有%s条；",
                matchCount,
                notMatchCount);

        // 更新数据
        String var3 = "";
        if (CollectionUtils.isNotEmpty(needUpdateOrderMaterialList)) {
            List<List<BuWorkOrderMaterial>> orderMaterialBatchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateOrderMaterialList);
            for (List<BuWorkOrderMaterial> orderMaterialBatchSub : orderMaterialBatchSubList) {
                buWorkOrderMaterialMapper.updateListSystemWorkstation(orderMaterialBatchSub);
            }
            var3 = String.format("更新了%s条工单物料的系统和工位；", needUpdateOrderMaterialList.size());
        }

        return var1 + var2 + var3;
    }

    /**
     * @see CustomService#updateMaterialApplyErrorStatus(String, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateMaterialApplyErrorStatus(String planId, Boolean updateApplyStatusToReceived) throws Exception {
        // 查询列计划下的领用单和明细的状态信息
        List<Map<String, Object>> applyAndDetailStatusList = buMaterialApplyMapper.selectApplyAndDetailStatusListOfCloseOrder(planId);
        if (CollectionUtils.isEmpty(applyAndDetailStatusList)) {
            return "该列计划没有领用单和明细信息";
        }

        // 转化
        List<BuMaterialApply> applyList = new ArrayList<>();
        for (Map<String, Object> applyAndDetailStatus : applyAndDetailStatusList) {
            String applyId = (String) applyAndDetailStatus.get("applyId");
            Integer applyStatus = DataTypeCastUtil.transNumber(applyAndDetailStatus.get("applyStatus")).intValue();
            String applyDetailId = (String) applyAndDetailStatus.get("applyDetailId");
            Integer applyDetailStatus = DataTypeCastUtil.transNumber(applyAndDetailStatus.get("applyDetailStatus")).intValue();

            List<BuMaterialApply> matchApplyList = applyList.stream()
                    .filter(apply -> applyId.equals(apply.getId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(matchApplyList)) {
                BuMaterialApplyDetail applyDetail = new BuMaterialApplyDetail()
                        .setId(applyDetailId)
                        .setStatus(applyDetailStatus);
                List<BuMaterialApplyDetail> applyDetailList = new ArrayList<>();
                applyDetailList.add(applyDetail);

                BuMaterialApply apply = new BuMaterialApply()
                        .setId(applyId)
                        .setStatus(applyStatus)
                        .setDetailList(applyDetailList);
                applyList.add(apply);
            } else {
                BuMaterialApply apply = matchApplyList.get(0);
                List<BuMaterialApplyDetail> applyDetailList = apply.getDetailList();
                BuMaterialApplyDetail applyDetail = new BuMaterialApplyDetail()
                        .setId(applyDetailId)
                        .setStatus(applyDetailStatus);
                applyDetailList.add(applyDetail);
            }
        }

        String resultString = "共有领用单" + applyList.size() + "个；";

        // 领用明细为空的
        List<BuMaterialApply> detailIsEmptyApplyList = new ArrayList<>();
        // 状态不应该为2已领用但为2已领用的
        List<BuMaterialApply> needNotReceivedApplyList = new ArrayList<>();
        // 状态应该为2已领用但不为2已领用的
        List<BuMaterialApply> needReceivedApplyList = new ArrayList<>();
        for (BuMaterialApply apply : applyList) {
            if (CollectionUtils.isEmpty(apply.getDetailList())) {
                detailIsEmptyApplyList.add(apply);
                continue;
            }

            boolean allReceived = whetherApplyDetailAllReceived(apply.getDetailList());
            if (2 == apply.getStatus()) {
                if (!allReceived) {
                    needNotReceivedApplyList.add(apply);
                }
            } else {
                if (allReceived) {
                    needReceivedApplyList.add(apply);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(detailIsEmptyApplyList)) {
            String applyIds = detailIsEmptyApplyList.stream()
                    .map(BuMaterialApply::getId)
                    .collect(Collectors.joining(","));
            resultString = resultString + detailIsEmptyApplyList.size() + "个领用单没有领用明细，领用明细ids=" + applyIds + "；";
        }
        if (CollectionUtils.isNotEmpty(needNotReceivedApplyList)) {
            String applyIds = needNotReceivedApplyList.stream()
                    .map(BuMaterialApply::getId)
                    .collect(Collectors.joining(","));
            resultString = resultString + needNotReceivedApplyList.size() + "个领用单状态不应该为2已领用但为2已领用，领用明细ids=" + applyIds + "；";
        }
        if (CollectionUtils.isNotEmpty(needReceivedApplyList)) {
            String applyIds = needReceivedApplyList.stream()
                    .map(BuMaterialApply::getId)
                    .collect(Collectors.joining(","));

            if (null != updateApplyStatusToReceived && updateApplyStatusToReceived) {
                needReceivedApplyList.forEach(apply -> apply.setStatus(2));
                buMaterialApplyMapper.updateListStatus(needReceivedApplyList);
                resultString = resultString + needReceivedApplyList.size() + "个领用单状态应该为2已领用但不为2已领用（已更新为2已领用），领用明细ids=" + applyIds + "；";
            } else {
                resultString = resultString + needReceivedApplyList.size() + "个领用单状态应该为2已领用但不为2已领用，领用明细ids=" + applyIds + "；";
            }
        }

        return resultString;
    }

    /**
     * @see CustomService#handleRepeatMaterialType(Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handleRepeatMaterialType(Boolean deleteRepeatData) throws Exception {
        Date now = new Date();

        // 查询重复的物资类型
        List<BuMaterialType> allRepeatMaterialTypeList = buMaterialTypeMapper.selectRepeatMaterialTypeList();
        if (CollectionUtils.isEmpty(allRepeatMaterialTypeList)) {
            return "没有重复的物资类型";
        }

        List<BuMaterialType> newMaterialTypeList = new ArrayList<>();
        List<Map<String, Object>> oldMaterialTypeIdsNewMaterialTypeIdMapList = new ArrayList<>();
        // 物资类型合并
        Map<String, List<BuMaterialType>> codeMaterialTypeListMap = allRepeatMaterialTypeList.stream()
                .collect(Collectors.groupingBy(BuMaterialType::getCode));
        StringBuilder var1Builder = new StringBuilder("重复的物资类型有" + codeMaterialTypeListMap.size() + "种：");
        for (Map.Entry<String, List<BuMaterialType>> codeMaterialTypeListEntry : codeMaterialTypeListMap.entrySet()) {
            String code = codeMaterialTypeListEntry.getKey();
            List<BuMaterialType> materialTypeList = codeMaterialTypeListEntry.getValue();
            // 记录重复数据
            var1Builder.append(code).append("(").append(materialTypeList.size()).append(")、");

            materialTypeList.sort(Comparator.comparing(BuMaterialType::getCode, Comparator.nullsLast(Comparator.naturalOrder())));
            BuMaterialType item0 = materialTypeList.get(0);
            // 合并
            BuMaterialType newMaterialType = new BuMaterialType()
                    .setId(code)
                    .setCode(code)
                    .setName(item0.getName())
                    .setSpec(item0.getSpec())
                    .setUnit(item0.getUnit())
                    .setPrice(item0.getPrice())
                    .setStatus(item0.getStatus())
                    .setKind(item0.getKind())
                    .setCategory(item0.getCategory())
                    .setCategory1(item0.getCategory1())
                    .setCategory2(item0.getCategory2())
                    .setCategory3(item0.getCategory3())
                    .setTheshold(item0.getTheshold())
                    .setSubject(item0.getSubject())
                    .setIsAsset(item0.getIsAsset())
                    .setFromHead(item0.getFromHead())
                    .setIsConsume(item0.getIsConsume())
                    .setRemark("按编码去重时重新生成")
                    .setCreateTime(now)
                    .setCreateBy("admin");

            for (BuMaterialType materialType : materialTypeList) {
                if (null == newMaterialType.getPrice() || (null != materialType.getPrice() && newMaterialType.getPrice().compareTo(materialType.getPrice()) < 0)) {
                    newMaterialType.setPrice(materialType.getPrice());
                }
                if (1 == materialType.getStatus()) {
                    newMaterialType.setStatus(1);
                }
                if (StringUtils.isBlank(newMaterialType.getCategory())) {
                    newMaterialType.setCategory(materialType.getCategory());
                }
                if (null == newMaterialType.getCategory1() || (null != materialType.getCategory1() && newMaterialType.getCategory1().compareTo(materialType.getCategory1()) < 0)) {
                    newMaterialType.setCategory1(materialType.getCategory1());
                }
                if (StringUtils.isBlank(newMaterialType.getCategory2())) {
                    newMaterialType.setCategory2(materialType.getCategory2());
                }
                if (StringUtils.isBlank(newMaterialType.getCategory3())) {
                    newMaterialType.setCategory3(materialType.getCategory3());
                }
                if (null == newMaterialType.getTheshold() || (null != materialType.getTheshold() && newMaterialType.getTheshold().compareTo(materialType.getTheshold()) < 0)) {
                    newMaterialType.setTheshold(materialType.getTheshold());
                }
                if (StringUtils.isBlank(newMaterialType.getSubject())) {
                    newMaterialType.setSubject(materialType.getSubject());
                }
                if (null == newMaterialType.getIsAsset() || (null != materialType.getIsAsset() && newMaterialType.getIsAsset().compareTo(materialType.getIsAsset()) < 0)) {
                    newMaterialType.setIsAsset(materialType.getIsAsset());
                }
                if (null == newMaterialType.getFromHead() || (null != materialType.getFromHead() && newMaterialType.getFromHead().compareTo(materialType.getFromHead()) < 0)) {
                    newMaterialType.setFromHead(materialType.getFromHead());
                }
                if (null == newMaterialType.getIsConsume() || (null != materialType.getIsConsume() && newMaterialType.getIsConsume().compareTo(materialType.getIsConsume()) < 0)) {
                    newMaterialType.setIsConsume(materialType.getIsConsume());
                }
            }

            newMaterialTypeList.add(newMaterialType);

            List<String> oldMaterialTypeIdList = materialTypeList.stream()
                    .map(BuMaterialType::getId)
                    .collect(Collectors.toList());
            Map<String, Object> oldMaterialTypeIdsNewMaterialTypeIdMap = new HashMap<>();
            oldMaterialTypeIdsNewMaterialTypeIdMap.put("oldIds", oldMaterialTypeIdList);
            oldMaterialTypeIdsNewMaterialTypeIdMap.put("newId", code);
            oldMaterialTypeIdsNewMaterialTypeIdMapList.add(oldMaterialTypeIdsNewMaterialTypeIdMap);
        }
        String var1 = null;
        if (var1Builder.length() > 0) {
            var1 = var1Builder.deleteCharAt(var1Builder.length() - 1).toString();
        }

        String var2 = "";
        if (null != deleteRepeatData && deleteRepeatData) {
            // 更新物资类型重复的业务数据表数据
            if (CollectionUtils.isNotEmpty(oldMaterialTypeIdsNewMaterialTypeIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(oldMaterialTypeIdsNewMaterialTypeIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaterialTypeMapper.updateOldNewMaterialTypeIdBusinessTableData(batchSub);
                }
                var2 = var2 + "更新了" + oldMaterialTypeIdsNewMaterialTypeIdMapList.size() + "种的物资类型重复的业务数据表数据；";
            }
            // 删除旧的物资类型
            List<String> needDeleteMaterialTypeIdList = allRepeatMaterialTypeList.stream()
                    .map(BuMaterialType::getId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(needDeleteMaterialTypeIdList)) {
                List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(needDeleteMaterialTypeIdList);
                for (List<String> batchSub : batchSubList) {
                    buMaterialTypeMapper.deleteBatchIds(batchSub);
                }
                var2 = var2 + "删除了" + needDeleteMaterialTypeIdList.size() + "条旧的物资类型；";
            }
            // 插入新的物资类型
            if (CollectionUtils.isNotEmpty(newMaterialTypeList)) {
                List<List<BuMaterialType>> batchSubList = DatabaseBatchSubUtil.batchSubList(newMaterialTypeList);
                for (List<BuMaterialType> batchSub : batchSubList) {
                    buMaterialTypeMapper.insertList(batchSub);
                }
                var2 = var2 + "插入了" + newMaterialTypeList.size() + "条新的物资类型；";
            }
        }

        return var1 + "；" + var2;
    }

    /**
     * @see CustomService#handleCodeIdNotEqualsMaterialType()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handleCodeIdNotEqualsMaterialType() throws Exception {
        Date now = new Date();

        // 查询id不等于编码的物资类型
        List<BuMaterialType> allRepeatMaterialTypeList = buMaterialTypeMapper.selectCodeIdNotEqualsMaterialTypeList();
        if (CollectionUtils.isEmpty(allRepeatMaterialTypeList)) {
            return "没有id不等于编码的物资类型";
        }
        String var1 = "id不等于编码的物资类型有" + allRepeatMaterialTypeList.size() + "%s条；";

        List<Map<String, Object>> oldMaterialTypeIdsNewMaterialTypeIdMapList = new ArrayList<>();
        for (BuMaterialType materialType : allRepeatMaterialTypeList) {
            Map<String, Object> oldMaterialTypeIdsNewMaterialTypeIdMap = new HashMap<>();
            oldMaterialTypeIdsNewMaterialTypeIdMap.put("oldIds", Collections.singletonList(materialType.getId()));
            oldMaterialTypeIdsNewMaterialTypeIdMap.put("newId", materialType.getCode());
            oldMaterialTypeIdsNewMaterialTypeIdMapList.add(oldMaterialTypeIdsNewMaterialTypeIdMap);
        }

        String var2 = "";
        // 更新物资类型id不等于编码的业务数据表数据
        if (CollectionUtils.isNotEmpty(oldMaterialTypeIdsNewMaterialTypeIdMapList)) {
            List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(oldMaterialTypeIdsNewMaterialTypeIdMapList);
            for (List<Map<String, Object>> batchSub : batchSubList) {
                buMaterialTypeMapper.updateOldNewMaterialTypeIdBusinessTableData(batchSub);
            }
            var2 = var2 + "更新了" + oldMaterialTypeIdsNewMaterialTypeIdMapList.size() + "种的物资类型id不等于编码的业务数据表数据；";
        }
        // 更新物资类型设置id等于code
        if (CollectionUtils.isNotEmpty(allRepeatMaterialTypeList)) {
            List<List<BuMaterialType>> batchSubList = DatabaseBatchSubUtil.batchSubList(allRepeatMaterialTypeList);
            for (List<BuMaterialType> batchSub : batchSubList) {
                buMaterialTypeMapper.updateIdEqualsCodeBatch(batchSub);
            }
            var2 = var2 + "更新了" + allRepeatMaterialTypeList.size() + "条新的物资类型id等于code；";
        }

        return var1.toString() + var2;
    }

    /**
     * @see CustomService#updateOrderMaterialSystemAndWorkstationByCustomerData(String, MultipartFile)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateOrderMaterialSystemAndWorkstationByCustomerData(String planId, MultipartFile excelFile) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(excelFile, null, Arrays.asList(".xls", ".xlsx"));

        if (StringUtils.isBlank(planId)) {
            throw new JeecgBootException("请选择列计划");
        }
        BuRepairPlan plan = buRepairPlanMapper.selectById(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }

        // 查询系统或工位无效的工单物料
        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMapper.selectSystemOrWorkstationInvalidList(planId);
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return "没有系统或工位无效的工单物料";
        }
        String var1 = String.format("系统或工位无效的工单物料共有%s条；", orderMaterialList.size());

        // 查询工位
        List<BuMtrWorkstation> workstationList = buMtrWorkstationBoardMapper.selectList(Wrappers.emptyWrapper());
        List<BuGroupWorkstation> groupWorkstationList = buGroupWorkstationMapper.selectList(Wrappers.emptyWrapper());
        Map<String, String> workstationNoIdMap = new HashMap<>();
        Map<String, String> workstationNoGroupIdMap = new HashMap<>();
        for (BuMtrWorkstation workstation : workstationList) {
            String workstationNo = workstation.getStationNo();
            String workstationId = workstation.getId();
            workstationNoIdMap.put(workstationNo, workstationId);

            List<BuGroupWorkstation> matchGroupWorkstationList = groupWorkstationList.stream()
                    .filter(groupWorkstation -> workstationId.equals(groupWorkstation.getWorkstationId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(matchGroupWorkstationList)) {
                String groupId = matchGroupWorkstationList.get(0).getGroupId();
                workstationNoGroupIdMap.put(workstationNo, groupId);
            }
        }
        // 查询系统
        List<BuTrainAssetType> systemAssetTypeList = buTrainAssetTypeMapper.selectTopSystemListByTrainNo(plan.getTrainNo(), null);
        Map<String, String> systemShortNameIdMap = new HashMap<>();
        systemAssetTypeList.forEach(systemAssetType -> systemShortNameIdMap.put(systemAssetType.getShortName(), systemAssetType.getId()));

        // 提取excel车间消耗明细数据
        List<CustomerCostDetailSystemWorkstation> costSystemWorkstationList = getCostDetailSystemWorkstationsFormExcelData(excelFile, systemShortNameIdMap, workstationNoGroupIdMap, workstationNoIdMap);

        // 查询物资类型id和code
//        Map<String, String> materialTypeCodeIdMap = new HashMap<>();
        Map<String, String> materialTypeIdCodeMap = new HashMap<>();
        LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                .select(BuMaterialType::getId, BuMaterialType::getCode);
        List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);
        for (BuMaterialType materialType : materialTypeList) {
//            materialTypeCodeIdMap.put(materialType.getCode(), materialType.getId());
            materialTypeIdCodeMap.put(materialType.getId(), materialType.getCode());
        }

        int matchCount = 0;
        int notMatchCount = 0;
        List<BuWorkOrderMaterial> needUpdateOrderMaterialList = new ArrayList<>();
        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            String groupId = orderMaterial.getGroupId();
            String materialTypeCode = materialTypeIdCodeMap.get(orderMaterial.getMaterialTypeId());

            // 从excel数据中，通过工位对应班组，找系统和工位
            List<CustomerCostDetailSystemWorkstation> matchCostDetailList = new ArrayList<>();
            for (CustomerCostDetailSystemWorkstation customerCostDetailSystemWorkstation : costSystemWorkstationList) {
                boolean sameMaterial = materialTypeCode.equals(customerCostDetailSystemWorkstation.getMaterialTypeCode());
                boolean sameGroup = groupId.equals(customerCostDetailSystemWorkstation.getGroupId());
                if (sameMaterial && sameGroup) {
                    matchCostDetailList.add(customerCostDetailSystemWorkstation);
                }
            }

            if (CollectionUtils.isNotEmpty(matchCostDetailList)) {
                matchCount++;

                CustomerCostDetailSystemWorkstation customerCostDetailSystemWorkstation = matchCostDetailList.get(0);
                // 系统id
                String systemId = customerCostDetailSystemWorkstation.getSystemId();
                // 工位id
                String workstationId = customerCostDetailSystemWorkstation.getWorkstationId();

                if (StringUtils.isNotBlank(systemId) && StringUtils.isNotBlank(workstationId)) {
                    orderMaterial.setSystemId(systemId)
                            .setWorkstationId(workstationId);
                    needUpdateOrderMaterialList.add(orderMaterial);
                }
            } else {
                notMatchCount++;
            }
        }

        String var2 = String.format("【根据工位对应班组和物资能匹配】的有%s条；" +
                        "【不匹配】的有%s条；",
                matchCount,
                notMatchCount);

        // 更新数据
        String var3 = "";
        if (CollectionUtils.isNotEmpty(needUpdateOrderMaterialList)) {
            List<List<BuWorkOrderMaterial>> orderMaterialBatchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateOrderMaterialList);
            for (List<BuWorkOrderMaterial> orderMaterialBatchSub : orderMaterialBatchSubList) {
                buWorkOrderMaterialMapper.updateListSystemWorkstation(orderMaterialBatchSub);
            }
            var3 = String.format("更新了%s条工单物料的系统和工位；", needUpdateOrderMaterialList.size());
        }

        return var1 + var2 + var3;
    }

    /**
     * @see CustomService#updateOrderMaterialPriceByCustomerData(String, MultipartFile)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateOrderMaterialPriceByCustomerData(String planId, MultipartFile excelFile) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(excelFile, null, Arrays.asList(".xls", ".xlsx"));

        if (StringUtils.isBlank(planId)) {
            throw new JeecgBootException("请选择列计划");
        }
        BuRepairPlan plan = buRepairPlanMapper.selectById(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }

        // 查询工位
        List<BuMtrWorkstation> workstationList = buMtrWorkstationBoardMapper.selectList(Wrappers.emptyWrapper());
        List<BuGroupWorkstation> groupWorkstationList = buGroupWorkstationMapper.selectList(Wrappers.emptyWrapper());
        Map<String, String> workstationNoIdMap = new HashMap<>();
        Map<String, String> workstationNoGroupIdMap = new HashMap<>();
        for (BuMtrWorkstation workstation : workstationList) {
            String workstationNo = workstation.getStationNo();
            String workstationId = workstation.getId();
            workstationNoIdMap.put(workstationNo, workstationId);

            List<BuGroupWorkstation> matchGroupWorkstationList = groupWorkstationList.stream()
                    .filter(groupWorkstation -> workstationId.equals(groupWorkstation.getWorkstationId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(matchGroupWorkstationList)) {
                String groupId = matchGroupWorkstationList.get(0).getGroupId();
                workstationNoGroupIdMap.put(workstationNo, groupId);
            }
        }
        // 查询系统
        List<BuTrainAssetType> systemAssetTypeList = buTrainAssetTypeMapper.selectTopSystemListByTrainNo(plan.getTrainNo(), null);
        Map<String, String> systemShortNameIdMap = new HashMap<>();
        systemAssetTypeList.forEach(systemAssetType -> systemShortNameIdMap.put(systemAssetType.getShortName(), systemAssetType.getId()));

        // 提取excel车间消耗明细数据
        List<CustomerCostDetailSystemWorkstation> costSystemWorkstationList = getCostDetailSystemWorkstationsFormExcelData(excelFile, systemShortNameIdMap, workstationNoGroupIdMap, workstationNoIdMap);

        // 查询物资类型id和code
        Map<String, String> materialTypeCodeIdMap = new HashMap<>();
//        Map<String, String> materialTypeIdCodeMap = new HashMap<>();
        LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                .select(BuMaterialType::getId, BuMaterialType::getCode);
        List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);
        for (BuMaterialType materialType : materialTypeList) {
            materialTypeCodeIdMap.put(materialType.getCode(), materialType.getId());
//            materialTypeIdCodeMap.put(materialType.getId(), materialType.getCode());
        }

        // 物资价格
        Map<String, List<BigDecimal>> materialTypeIdPriceListMap = new HashMap<>();
        for (CustomerCostDetailSystemWorkstation customerCostDetailSystemWorkstation : costSystemWorkstationList) {
            String materialTypeId = materialTypeCodeIdMap.get(customerCostDetailSystemWorkstation.getMaterialTypeCode());
            BigDecimal materialTypePrice = customerCostDetailSystemWorkstation.getMaterialTypePrice();

            List<BigDecimal> priceList = materialTypeIdPriceListMap.getOrDefault(materialTypeId, new ArrayList<>());
            if (!priceList.contains(materialTypePrice)) {
                priceList.add(materialTypePrice);
            }
            materialTypeIdPriceListMap.put(materialTypeId, priceList);
        }

        String var1 = "请【手动处理】有多个价格的物资：";
        List<Map<String, Object>> materialTypeIdPriceMapList = new ArrayList<>();
        for (Map.Entry<String, List<BigDecimal>> materialTypeIdPriceListEntry : materialTypeIdPriceListMap.entrySet()) {
            String materialTypeId = materialTypeIdPriceListEntry.getKey();
            List<BigDecimal> priceList = materialTypeIdPriceListEntry.getValue();
            if (priceList.size() > 1) {
                List<String> priceStringList = priceList.stream()
                        .map(BigDecimal::toPlainString)
                        .collect(Collectors.toList());
                var1 = var1 + materialTypeId + "有" + priceList.size() + "个价格（" + String.join("/", priceStringList) + "）；";

                priceList.sort(Comparator.comparing(BigDecimal::doubleValue));
            }

            Map<String, Object> materialTypeIdPriceMap = new HashMap<>();
            materialTypeIdPriceMap.put("id", materialTypeId);
            materialTypeIdPriceMap.put("price", priceList.get(0));
            materialTypeIdPriceMapList.add(materialTypeIdPriceMap);
        }

        // 更新数据
        String var2 = "";
        if (CollectionUtils.isNotEmpty(materialTypeIdPriceMapList)) {
            List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeIdPriceMapList);
            for (List<Map<String, Object>> batchSub : batchSubList) {
                buMaterialTypeMapper.updateMaterialTypePriceBusinessTableData(batchSub);
            }
            var2 = var2 + "更新了" + materialTypeIdPriceMapList.size() + "条新的物资类型价格到相关业务表；";
        }

        return var1 + var2;
    }

    /**
     * @see CustomService#compareDifferentMaterialCostDetail(String, MultipartFile, String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String compareDifferentMaterialCostDetail(String planId, MultipartFile excelFile, String exportDisk, String debugMaterialCode) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(excelFile, null, Arrays.asList(".xls", ".xlsx"));

        if (StringUtils.isBlank(planId)) {
            throw new JeecgBootException("请选择列计划");
        }
        BuRepairPlan plan = buRepairPlanMapper.selectById(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }

        // 查询工位
        List<BuMtrWorkstation> workstationList = buMtrWorkstationBoardMapper.selectList(Wrappers.emptyWrapper());
        List<BuGroupWorkstation> groupWorkstationList = buGroupWorkstationMapper.selectList(Wrappers.emptyWrapper());
        Map<String, String> workstationNoIdMap = new HashMap<>();
        Map<String, String> workstationNoGroupIdMap = new HashMap<>();
        for (BuMtrWorkstation workstation : workstationList) {
            String workstationNo = workstation.getStationNo();
            String workstationId = workstation.getId();
            workstationNoIdMap.put(workstationNo, workstationId);

            List<BuGroupWorkstation> matchGroupWorkstationList = groupWorkstationList.stream()
                    .filter(groupWorkstation -> workstationId.equals(groupWorkstation.getWorkstationId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(matchGroupWorkstationList)) {
                String groupId = matchGroupWorkstationList.get(0).getGroupId();
                workstationNoGroupIdMap.put(workstationNo, groupId);
            }
        }
        // 查询系统
        List<BuTrainAssetType> systemAssetTypeList = buTrainAssetTypeMapper.selectTopSystemListByTrainNo(plan.getTrainNo(), null);
        Map<String, String> systemShortNameIdMap = new HashMap<>();
        systemAssetTypeList.forEach(systemAssetType -> systemShortNameIdMap.put(systemAssetType.getShortName(), systemAssetType.getId()));

        // 提取excel车间消耗明细数据
        List<CustomerCostDetailSystemWorkstation> costSystemWorkstationList = getCostDetailSystemWorkstationsFormExcelData(excelFile, systemShortNameIdMap, workstationNoGroupIdMap, workstationNoIdMap);

        // 消耗明细
        BuReportCostDetailQueryVO costDetailQueryVO = new BuReportCostDetailQueryVO()
                .setLineId(plan.getLineId())
                .setTrainNo(plan.getTrainNo())
                .setUseCategoryList(Arrays.asList(1, 2, 3));
        BuCostDetailTotalVO costDetailStatistic = buWorkOrderMaterialReportService.getCostDetailStatistic(costDetailQueryVO);
        List<BuCostDetailMaterialVO> costDetailMaterialVOList = new ArrayList<>();
        List<BuCostDetailSystemVO> systemVOList = costDetailStatistic.getSystemList();
        if (CollectionUtils.isNotEmpty(systemVOList)) {
            for (BuCostDetailSystemVO systemVO : systemVOList) {
                List<BuCostDetailWorkstationVO> workstationVOList = systemVO.getWorkstationList();
                if (CollectionUtils.isNotEmpty(workstationVOList)) {
                    for (BuCostDetailWorkstationVO workstationVO : workstationVOList) {
                        List<BuCostDetailMaterialVO> materialVOList = workstationVO.getMaterialList();
                        if (CollectionUtils.isNotEmpty(materialVOList)) {
                            for (BuCostDetailMaterialVO materialVO : materialVOList) {
                                materialVO.setSystemId(systemVO.getSysId())
                                        .setSystemName(systemVO.getSysName().trim())
                                        .setWorkstationId(workstationVO.getWorkstationId())
                                        .setWorkstationNo(workstationVO.getWorkstationNo().trim())
                                        .setWorkstationName(workstationVO.getWorkstationName().trim())
                                        .setGroupId(workstationNoGroupIdMap.get(workstationVO.getWorkstationNo().trim()));
                                costDetailMaterialVOList.add(materialVO);
                            }
                        }
                    }
                }
            }
        }

        // 计算差别
        List<CostDetailDiff> diffList = new ArrayList<>();

        Set<String> materialTypeCodeSet1 = costSystemWorkstationList.stream()
                .map(CustomerCostDetailSystemWorkstation::getMaterialTypeCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Set<String> materialTypeCodeSet2 = costDetailMaterialVOList.stream()
                .map(BuCostDetailMaterialVO::getMaterialTypeCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        materialTypeCodeSet1.addAll(materialTypeCodeSet2);

        if (StringUtils.isNotBlank(debugMaterialCode)) {
            if (materialTypeCodeSet1.contains(debugMaterialCode)) {
                System.out.println("debug point");
            }
        }

        // 查询物质
        Map<String, BuMaterialType> codeMaterialTypeMap = new HashMap<>();
        List<List<String>> materialTypeCodeBatchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(materialTypeCodeSet1));
        for (List<String> materialTypeCodeBatchSub : materialTypeCodeBatchSubList) {
            LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                    .in(BuMaterialType::getCode, materialTypeCodeBatchSub);
            List<BuMaterialType> subMaterialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);
            subMaterialTypeList.forEach(materialType -> codeMaterialTypeMap.put(materialType.getCode(), materialType));
        }

        for (String materialTypeCode : materialTypeCodeSet1) {
            if (StringUtils.isNotBlank(debugMaterialCode)) {
                if (debugMaterialCode.equals(materialTypeCode)) {
                    System.out.println("debug point");
                }
            }

            // 客户的
            List<CustomerCostDetailSystemWorkstation> customerDataList = costSystemWorkstationList.stream()
                    .filter(item -> materialTypeCode.equals(item.getMaterialTypeCode()))
                    .collect(Collectors.toList());
            // 程序的
            List<BuCostDetailMaterialVO> programDataList = costDetailMaterialVOList.stream()
                    .filter(item -> materialTypeCode.equals(item.getMaterialTypeCode()))
                    .collect(Collectors.toList());

            boolean isDiff = false;
            boolean amountSumDiff = false;
            boolean systemWorkstationDiff = false;
            boolean useCategoryDiff = false;

            BigDecimal customerAmountSum = customerDataList.stream()
                    .map(CustomerCostDetailSystemWorkstation::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal programAmountSum = programDataList.stream()
                    .map(BuCostDetailMaterialVO::getConsumeAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (customerAmountSum.compareTo(programAmountSum) != 0) {
                isDiff = true;
                amountSumDiff = true;
            } else {
                // 根据系统-工位为key，数量为value分组，再比较
                Map<String, BigDecimal> customerAmountMap = new HashMap<>();
                Set<Integer> customerUseCategorySet = new HashSet<>();
                for (CustomerCostDetailSystemWorkstation customerData : customerDataList) {
                    String systemId = customerData.getSystemId();
                    String workstationId = customerData.getWorkstationId();
                    BigDecimal amount = customerData.getAmount();

                    String key = systemId + "-" + workstationId;
                    BigDecimal value = customerAmountMap.getOrDefault(key, BigDecimal.ZERO);
                    customerAmountMap.put(key, value.add(amount));
                    customerUseCategorySet.add(customerData.getUseCategory());
                }
                Map<String, BigDecimal> programAmountMap = new HashMap<>();
                Set<Integer> programUseCategorySet = new HashSet<>();
                for (BuCostDetailMaterialVO programData : programDataList) {
                    String systemId = programData.getSystemId();
                    String workstationId = programData.getWorkstationId();
                    BigDecimal amount = programData.getConsumeAmount();

                    String key = systemId + "-" + workstationId;
                    BigDecimal value = programAmountMap.getOrDefault(key, BigDecimal.ZERO);
                    programAmountMap.put(key, value.add(amount));
                    programUseCategorySet.add(programData.getUseCategory());
                }

                // 数量一致不
                for (Map.Entry<String, BigDecimal> customerAmountEntry : customerAmountMap.entrySet()) {
                    String key = customerAmountEntry.getKey();
                    BigDecimal value = customerAmountEntry.getValue();
                    if (value.compareTo(BigDecimal.ZERO) == 0) {
                        continue;
                    }

                    BigDecimal programValue = programAmountMap.get(key);
                    if (null == programValue || programValue.compareTo(value) != 0) {
                        isDiff = true;
                        systemWorkstationDiff = true;
                        break;
                    }
                }
                for (Map.Entry<String, BigDecimal> programAmountEntry : programAmountMap.entrySet()) {
                    String key = programAmountEntry.getKey();
                    BigDecimal value = programAmountEntry.getValue();
                    if (value.compareTo(BigDecimal.ZERO) == 0) {
                        continue;
                    }

                    BigDecimal customerValue = customerAmountMap.get(key);
                    if (null == customerValue || customerValue.compareTo(value) != 0) {
                        isDiff = true;
                        systemWorkstationDiff = true;
                        break;
                    }
                }

                // 消耗类型一致不
                for (Integer useCategory : customerUseCategorySet) {
                    if (!programUseCategorySet.contains(useCategory)) {
                        isDiff = true;
                        useCategoryDiff = true;
                        break;
                    }
                }
                for (Integer useCategory : programUseCategorySet) {
                    if (!customerUseCategorySet.contains(useCategory)) {
                        isDiff = true;
                        useCategoryDiff = true;
                        break;
                    }
                }
            }

            if (isDiff) {
                List<CostDetailDiffItem> customerList = new ArrayList<>();
                for (CustomerCostDetailSystemWorkstation customerData : customerDataList) {
                    CostDetailDiffItem item = new CostDetailDiffItem()
                            .setSystemName(customerData.getSystemShortName())
                            .setWorkstationNo(customerData.getWorkstationNo())
                            .setWorkstationName(customerData.getWorkstationName())
                            .setAmount(customerData.getAmount())
                            .setUseCategory(customerData.getUseCategory())
                            .setGroupId(customerData.getGroupId());
                    customerList.add(item);
                }
                List<CostDetailDiffItem> programList = new ArrayList<>();
                for (BuCostDetailMaterialVO programData : programDataList) {
                    CostDetailDiffItem item = new CostDetailDiffItem()
                            .setSystemName(programData.getSystemName())
                            .setWorkstationNo(programData.getWorkstationNo())
                            .setWorkstationName(programData.getWorkstationName())
                            .setAmount(programData.getConsumeAmount())
                            .setUseCategory(programData.getUseCategory())
                            .setGroupId(programData.getGroupId())
                            .setOrderMaterialIdSet(programData.getOrderMaterialIdSet());
                    programList.add(item);
                }

                Set<String> groupIdSet = new HashSet<>();

                String customerInfo = "";
                Integer customerUseCategory = null;
                if (CollectionUtils.isNotEmpty(customerList)) {
                    customerUseCategory = customerList.get(0).getUseCategory();
                    customerList.sort(Comparator.comparing(CostDetailDiffItem::getSystemName)
                            .thenComparing(CostDetailDiffItem::getWorkstationNo));
                    for (CostDetailDiffItem item : customerList) {
                        String itemInfo = item.getSystemName() + "-" + item.getWorkstationNo() + "(" + item.getWorkstationName() + "-" + item.getGroupId() + ")" + "-" + item.getAmount().stripTrailingZeros().toPlainString();
                        customerInfo = customerInfo + itemInfo + "；" + System.lineSeparator();

                        groupIdSet.add(item.getGroupId());
                    }
                }

                Set<String> orderMaterialIdSet = new HashSet<>();
                String programInfo = "";
                Integer programUseCategory = null;
                if (CollectionUtils.isNotEmpty(programList)) {
                    programUseCategory = programList.get(0).getUseCategory();
                    programList.sort(Comparator.comparing(CostDetailDiffItem::getSystemName)
                            .thenComparing(CostDetailDiffItem::getWorkstationNo));
                    for (CostDetailDiffItem item : programList) {
                        String itemInfo = item.getSystemName() + "-" + item.getWorkstationNo() + "(" + item.getWorkstationName() + "-" + item.getGroupId() + ")" + "-" + item.getAmount().stripTrailingZeros().toPlainString();
                        programInfo = programInfo + itemInfo + "；" + System.lineSeparator();

                        groupIdSet.add(item.getGroupId());
                        orderMaterialIdSet.addAll(item.getOrderMaterialIdSet());
                    }
                }

                CostDetailDiff diff = new CostDetailDiff()
                        .setMaterialTypeCode(materialTypeCode)
                        .setCustomerAmountSum(customerAmountSum)
                        .setCustomerType(getUseCategoryString(customerUseCategory))
                        .setProgramAmountSum(programAmountSum)
                        .setProgramType(getUseCategoryString(programUseCategory))
                        .setCustomerInfo(customerInfo)
                        .setProgramInfo(programInfo)
                        .setAmountSumDiff(amountSumDiff)
                        .setSystemWorkstationDiff(systemWorkstationDiff)
                        .setUseCategoryDiff(useCategoryDiff)
                        .setGroupIds(String.join(",", groupIdSet))
                        .setOrderMaterialIds(String.join(",", orderMaterialIdSet));
                BuMaterialType materialType = codeMaterialTypeMap.get(materialTypeCode);
                if (null != materialType) {
                    diff.setMaterialTypeName(materialType.getName())
                            .setMaterialTypeSpec(materialType.getSpec());
                }

                diffList.add(diff);
            }
        }

        String resultString = null;
        // 是否导出sql语句到excel
        if (CollectionUtils.isNotEmpty(diffList)) {
            diffList.sort(Comparator.comparing(CostDetailDiff::getMaterialTypeCode));
            HSSFWorkbook correctSqlWorkbook = getExcelOfCostDetailDiff(diffList);

            try {
                String fileName = String.format("消耗明细系统工位差异-%s", correctSqlWorkbook.getSheetAt(0).getSheetName());

                // 输出Excel文件
                if (StringUtils.isNotBlank(exportDisk)) {
                    exportDisk = "D";
                }
                String outFilePathName = exportDisk + ":\\" + fileName + ".xls";
                FileOutputStream outputStream = new FileOutputStream(outFilePathName);
                correctSqlWorkbook.write(outputStream);
                outputStream.flush();
                outputStream.close();

                resultString = "差异信息已导出到文件：" + outFilePathName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            resultString = "数据全匹配，无差异信息";
        }

        return resultString;
    }

    /**
     * @see CustomService#flushIssueOrderMaterialToGroupStock(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String flushIssueOrderMaterialToGroupStock(String orderId) throws Exception {
        Date now = new Date();

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        // 查询工单
        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        if (4 != order.getOrderStatus()) {
            throw new JeecgBootException("工单状态不是已关闭，请确认要刷新的工单");
        }
        boolean needAddGroupStock = order.getOrderType() == 4;
        if (!needAddGroupStock) {
            throw new JeecgBootException("该工单不是发料工单，不能添加班组库存");
        }

        String groupId = order.getGroupId();
        String trainNo = order.getTrainNo();

        // 查询工单的分配明细
        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListByOrderId(orderId);

        if (CollectionUtils.isNotEmpty(assignDetailList)) {
            // 查询班组库存
            LambdaQueryWrapper<BuMaterialGroupStock> groupStockWrapper = new LambdaQueryWrapper<BuMaterialGroupStock>()
                    .eq(BuMaterialGroupStock::getGroupId, groupId);
            List<BuMaterialGroupStock> groupStockList = buMaterialGroupStockMapper.selectList(groupStockWrapper);
            for (BuMaterialGroupStock groupStock : groupStockList) {
                groupStock.setOldAmount(groupStock.getAmount())
                        .setRelativeAssignDetailIdSet(new HashSet<>());
            }

            // 处理数据，新增或增加班组库存
            Set<String> needUpdateGroupStockIdSet = new HashSet<>();
            Set<String> needAddGroupStockIdSet = new HashSet<>();
            for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                String materialTypeId = assignDetail.getMaterialTypeId();
                String tradeNo = assignDetail.getTradeNo();

                // 查找匹配对应当前库存
                List<BuMaterialGroupStock> matchGroupStockList = new ArrayList<>();
                for (BuMaterialGroupStock groupStock : groupStockList) {
                    boolean sameMaterial = materialTypeId.equals(groupStock.getMaterialTypeId());
                    boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(groupStock.getTradeNo()) : tradeNo.equals(groupStock.getTradeNo());
                    boolean sameTrainNo = StringUtils.isBlank(trainNo) ? StringUtils.isBlank(groupStock.getTrainNo()) : trainNo.equals(groupStock.getTrainNo());

                    if (sameMaterial && sameTradeNo && sameTrainNo) {
                        matchGroupStockList.add(groupStock);
                    }
                }
                if (CollectionUtils.isEmpty(matchGroupStockList)) {
                    // 没有新增
                    BuMaterialType materialType = buMaterialTypeMapper.selectById(materialTypeId);
                    Map<String, BuMaterialMustList> materialMustListMap = buMaterialMustListMapper.selectList(Wrappers.<BuMaterialMustList>lambdaQuery()
                            .select(BuMaterialMustList::getMaterialTypeId, BuMaterialMustList::getWorkstationId, BuMaterialMustList::getSysId, BuMaterialMustList::getGroupId)
                            .eq(BuMaterialMustList::getMaterialTypeId, materialTypeId)
                            .eq(BuMaterialMustList::getGroupId, groupId))
                            .stream().collect(Collectors.toMap((item) -> item.getMaterialTypeId() + item.getGroupId(), (item) -> item, (k1, k2) -> k2));
                    String systemId = "";
                    String workstationId = "";
                    Integer category = null;
                    if (materialMustListMap.size() > 0) {
                        BuMaterialMustList materialMustList = materialMustListMap.get(materialTypeId + groupId);
                        if (materialMustList != null) {
                            systemId = materialMustList.getSysId();
                            workstationId = materialMustList.getWorkstationId();
                            category = 1;
                        }
                    }

                    BuMaterialGroupStock groupStock = new BuMaterialGroupStock()
                            .setId(UUIDGenerator.generate())
                            .setMaterialTypeId(materialTypeId)
                            .setAmount(BigDecimal.valueOf(assignDetail.getAmount()))
                            .setPrice(assignDetail.getPrice())
                            .setApplyId(assignDetail.getApplyId())
                            .setApplyDetailId(assignDetail.getApplyDetailId())
                            .setGroupId(groupId)
                            .setAssignDetailId(assignDetail.getId())
                            .setTradeNo(tradeNo)
                            .setCompanyId(order.getCompanyId())
                            .setWorkshopId(order.getWorkshopId())
                            .setLineId(order.getLineId())
                            .setSystemId(systemId)
                            .setWorkstationId(workstationId)
                            .setUseCategory(category != null ? category : materialType.getCategory1())
                            .setTrainNo(trainNo)
                            .setOldAmount(BigDecimal.ZERO)
                            .setRelativeAssignDetailIdSet(new HashSet<>());
                    groupStock.getRelativeAssignDetailIdSet().add(assignDetail.getId());
                    groupStockList.add(groupStock);
                    needAddGroupStockIdSet.add(groupStock.getId());
                } else {
                    // 有更新
                    BuMaterialGroupStock groupStock = matchGroupStockList.get(0);
                    BigDecimal newAmount = groupStock.getAmount().add(BigDecimal.valueOf(assignDetail.getAmount()));
                    groupStock.setAmount(newAmount)
                            .setPrice(assignDetail.getPrice())
                            .setApplyId(assignDetail.getApplyId())
                            .setApplyDetailId(assignDetail.getApplyDetailId())
                            .setAssignDetailId(assignDetail.getId())
                            .setTradeNo(tradeNo)
                            .setTrainNo(trainNo);
                    groupStock.getRelativeAssignDetailIdSet().add(assignDetail.getId());
                    if (!needAddGroupStockIdSet.contains(groupStock.getId())) {
                        needUpdateGroupStockIdSet.add(groupStock.getId());
                    }
                }
            }

            // 新增或更新班组库存
            updateGroupStockByIdSet(groupStockList, needAddGroupStockIdSet, needUpdateGroupStockIdSet, order, now, userId);

            // 新增或更新班组库存
            return String.format("新增了%s条班组库存记录，更新了%s条班组库存记录；", needAddGroupStockIdSet.size(), needUpdateGroupStockIdSet.size());
        } else {
            return "未查询到分配明细，没有修改班组库存";
        }
    }

    /**
     * @see CustomService#updateIssueOrderMaterialApplyStatusOfDeliver1(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateIssueOrderMaterialApplyStatusOfDeliver1(String orderId) throws Exception {
        // 查询工单
        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        if (2 != order.getOrderStatus()) {
            throw new JeecgBootException("工单状态不是已核实，请确认要刷新的工单");
        }
        List<BuMaterialApplyDetail> applyDetailList = buMaterialApplyDetailMapper.selectListByOrderId(orderId, Collections.singletonList(0));
        if (CollectionUtils.isEmpty(applyDetailList)) {
            return "该工单没有状态为初始0的领用明细";
        }

        // 查询仓库
        Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();
        Map<String, CacheWarehouseBO> codeWarehouseBOMap = new HashMap<>();
        Map<String, CacheWarehouseBO> wbsWarehouseBOMap = new HashMap<>();
        for (Map.Entry<String, CacheWarehouseBO> warehouseBOEntry : warehouseBOMap.entrySet()) {
            CacheWarehouseBO warehouseBO = warehouseBOEntry.getValue();
            codeWarehouseBOMap.put(warehouseBO.getCode(), warehouseBO);
            wbsWarehouseBOMap.put(warehouseBO.getWbs(), warehouseBO);
        }

        Date now = new Date();
        Set<String> materialTypeIdSet = new HashSet<>();
        List<BuMaterialApplyDetail> needDealApplyDetailList = new ArrayList<>();
        Set<String> needDealApplyIdSet = new HashSet<>();
        List<BuMaterialAssignDetail> needAddAssignDetailList = new ArrayList<>();
        List<String> zeroAmountAssignDetailIdList = new ArrayList<>();
        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            needDealApplyIdSet.add(applyDetail.getApplyId());
            needDealApplyDetailList.add(applyDetail);
            // 修改领用明细状态为已发料1
            applyDetail.setStatus(1);
            buMaterialApplyDetailMapper.updateById(applyDetail);

            // 查询分配明细
            List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListByApplyDetailId(applyDetail.getId());
            if (CollectionUtils.isNotEmpty(assignDetailList)) {
                // 设置分配明细属性
                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    if (0D == assignDetail.getAmount()) {
                        zeroAmountAssignDetailIdList.add(assignDetail.getId());
                    } else {
                        setDetailEbsInfo(assignDetail, codeWarehouseBOMap);
                        needAddAssignDetailList.add(assignDetail);
                    }
                }
            }

            if (StringUtils.isNotBlank(applyDetail.getMaterialTypeId())) {
                materialTypeIdSet.add(applyDetail.getMaterialTypeId());
            }
            if (null != applyDetail.getSendTime() && applyDetail.getSendTime().before(now)) {
                now = applyDetail.getSendTime();
            }
        }

        // 查询物资
        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialTypeIdSet)) {
            List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectBatchIds(materialTypeIdSet);
            materialTypeList.forEach(materialType -> idMaterialTypeMap.put(materialType.getId(), materialType));
        }

        // 验证库存
        if (CollectionUtils.isNotEmpty(needAddAssignDetailList)) {
            checkStockAmountEnough(materialTypeIdSet, warehouseBOMap, wbsWarehouseBOMap, idMaterialTypeMap, needAddAssignDetailList);
        }

        if (CollectionUtils.isNotEmpty(needDealApplyIdSet)) {
            // 查询领用单下所有明细
            LambdaQueryWrapper<BuMaterialApplyDetail> applyDetailWrapper = new LambdaQueryWrapper<BuMaterialApplyDetail>()
                    .in(BuMaterialApplyDetail::getApplyId, needDealApplyIdSet);
            List<BuMaterialApplyDetail> allApplyDetailList = buMaterialApplyDetailMapper.selectList(applyDetailWrapper);
            Map<String, List<BuMaterialApplyDetail>> applyIdApplyDetailListMap = allApplyDetailList.stream()
                    .collect(Collectors.groupingBy(BuMaterialApplyDetail::getApplyId));
            for (Map.Entry<String, List<BuMaterialApplyDetail>> applyIdApplyDetailListEntry : applyIdApplyDetailListMap.entrySet()) {
                String applyId = applyIdApplyDetailListEntry.getKey();
                List<BuMaterialApplyDetail> detailList = applyIdApplyDetailListEntry.getValue();

                // 领用明细全部已经备料确认，修改领用单为已备料
                boolean isAllReady = checkApplyDetailAllReady(detailList);
                if (isAllReady) {
                    BuMaterialApply apply = buMaterialApplyMapper.selectById(applyId);
                    apply.setId(applyId)
                            .setReadyStatus(1);
                    buMaterialApplyMapper.updateById(apply);

                    // 修改工单状态为6已发料
                    buWorkOrderForMaterialMapper.updateById(new org.jeecg.modules.material.apply.bean.BuWorkOrder().setId(apply.getWorkOrderId()).setOrderStatus(6));
                }
            }
        }

        return String.format("更新了%s条领用明细，%s条领用单；", applyDetailList.size(), needDealApplyIdSet.size());
    }

    /**
     * @see CustomService#updateIssueOrderMaterialApplyStatusOfReceive(String, Date, Boolean, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateIssueOrderMaterialApplyStatusOfReceive(String orderId, Date confirmTime, Boolean transConsumeToMaximo, Boolean toGroupStock) throws Exception {
        Date now = new Date();

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        String departId = buMaterialApplyMapper.selectDepartIdByOrgCode(sysUser.getOrgCode());

        // 查询工单
        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        if (4 != order.getOrderStatus()) {
            throw new JeecgBootException("工单状态不是已关闭，请确认要刷新的工单");
        }
        if (!departId.equals(order.getGroupId())) {
            throw new JeecgBootException("当前人员部门等于工单所属班组，请确认");
        }
        boolean needAddGroupStock = order.getOrderType() == 4;

        List<BuMaterialApplyDetail> applyDetailList = buMaterialApplyDetailMapper.selectListByOrderId(orderId, Arrays.asList(0, 1));
        if (CollectionUtils.isEmpty(applyDetailList)) {
            return "该工单没有状态为初始0或已发放1的领用明细";
        }

        // 查询数据库原领用单
        Set<String> applyIdSet = applyDetailList.stream()
                .map(BuMaterialApplyDetail::getApplyId)
                .collect(Collectors.toSet());
        List<BuMaterialApply> dbApplyList = buMaterialApplyMapper.selectBatchIds(applyIdSet);
        Map<String, BuMaterialApply> idDbApplyMap = new HashMap<>();
        dbApplyList.forEach(apply -> idDbApplyMap.put(apply.getId(), apply));

        // 查询分配明细
        List<BuMaterialAssignDetail> assignDetailList = new ArrayList<>();
        Set<String> applyDetailIdSet = applyDetailList.stream()
                .map(BuMaterialApplyDetail::getId)
                .collect(Collectors.toSet());
        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(applyDetailIdSet));
        for (List<String> batchSub : batchSubList) {
            List<BuMaterialAssignDetail> subAssignDetailList = buMaterialAssignDetailMapper.selectListByApplyDetailIdList(batchSub);
            assignDetailList.addAll(subAssignDetailList);
        }

        // 查询仓库
        Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();
        Map<String, CacheWarehouseBO> wbsWarehouseBOMap = new HashMap<>();
        for (Map.Entry<String, CacheWarehouseBO> warehouseBOEntry : warehouseBOMap.entrySet()) {
            CacheWarehouseBO warehouseBO = warehouseBOEntry.getValue();
            wbsWarehouseBOMap.put(warehouseBO.getWbs(), warehouseBO);
        }

        // 查询物资
        Set<String> materialTypeIdSet = applyDetailList.stream()
                .map(BuMaterialApplyDetail::getMaterialTypeId)
                .collect(Collectors.toSet());
        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialTypeIdSet)) {
            List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectBatchIds(materialTypeIdSet);
            materialTypeList.forEach(materialType -> idMaterialTypeMap.put(materialType.getId(), materialType));
        }

        Set<String> needDealApplyIdSet = new HashSet<>();
        List<String> needTransToMaximoAssignDetailIdList = new ArrayList<>();
        List<BuMaterialAssignDetail> needTransToMaximoAssignDetailList = new ArrayList<>();
        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            if (null == applyDetail) {
                continue;
            }

            // 更新领用明细
            BuMaterialApply dbApply = idDbApplyMap.get(applyDetail.getApplyId());
            if (null == dbApply) {
                throw new JeecgBootException("物料[" + applyDetail.getMaterialTypeCode() + "]的领用明细对应领用单不存在");
            }
            if (2 != dbApply.getStatus()) {
                needDealApplyIdSet.add(applyDetail.getApplyId());

                BigDecimal totalAmount = BigDecimal.ZERO;
                BigDecimal totalPrice = BigDecimal.ZERO;

                List<BuMaterialAssignDetail> matchAssignDetailList = assignDetailList.stream()
                        .filter(assignDetail -> applyDetail.getId().equals(assignDetail.getApplyDetailId()))
                        .collect(Collectors.toList());
                for (BuMaterialAssignDetail assignDetail : matchAssignDetailList) {
                    if (!needTransToMaximoAssignDetailIdList.contains(assignDetail.getId())) {
                        needTransToMaximoAssignDetailIdList.add(assignDetail.getId());
                    }
                    if (!needTransToMaximoAssignDetailList.contains(assignDetail)) {
                        needTransToMaximoAssignDetailList.add(assignDetail);
                    }

                    totalAmount = totalAmount.add(BigDecimal.valueOf(assignDetail.getAmount()));
                    totalPrice = totalPrice.add(assignDetail.getPrice().multiply(BigDecimal.valueOf(assignDetail.getAmount())));
                }

                BigDecimal unitPrice = BigDecimal.ZERO;
                if (totalPrice.compareTo(BigDecimal.ZERO) > 0 && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    unitPrice = totalPrice.divide(totalAmount, 8, BigDecimal.ROUND_HALF_UP);
                }
                // 提交时，才修改领用明细状态为已领料2
                applyDetail.setStatus(2)
                        .setUnitPrice(unitPrice);
            }

            applyDetail.setConfirmUser(userId)
                    .setConfirmTime(confirmTime);
            buMaterialApplyDetailMapper.updateById(applyDetail);
        }

        // 更新领用单
        updateApplyStatus(new ArrayList<>(needDealApplyIdSet), userId, departId, confirmTime);

        // 验证库存
        if (CollectionUtils.isNotEmpty(needTransToMaximoAssignDetailIdList)) {
            checkStockAmountEnough(materialTypeIdSet, warehouseBOMap, wbsWarehouseBOMap, idMaterialTypeMap, needTransToMaximoAssignDetailList);
        }

        int transConsumeToMaximoCount = 0;
        if (null != transConsumeToMaximo && transConsumeToMaximo) {
            transConsumeToMaximoCount = needTransToMaximoAssignDetailIdList.size();
            // 添加数据到maximo同步中间表
            addMaximoTransDataAssignDetail(needTransToMaximoAssignDetailIdList, now);
        }

        if (null != toGroupStock && toGroupStock) {
            this.flushIssueOrderMaterialToGroupStock(orderId);
        }

        return String.format("更新了%s条领用明细，%s条领用单，同步%s条消耗到maximo；", applyDetailList.size(), needDealApplyIdSet.size(), transConsumeToMaximoCount);
    }

    /**
     * @see CustomService#updateInitStockUse(Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateInitStockUse(Boolean forceDeleteOldUse) throws Exception {
        Date now = new Date();

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = null == sysUser ? "admin" : sysUser.getId();

        String deleteInfo = "";
        if (null != forceDeleteOldUse && forceDeleteOldUse) {
            int clearAllStockUseSize = buMaterialStockUseService.clearAllStockUse();
            deleteInfo = "删除了" + clearAllStockUseSize + "条旧的库存占用；";
        }

        List<BuMaximoTransData> notTransferredList = buMaximoTransDataService.listAllNotTransferredTransDataByType(Arrays.asList(3, 4));
        List<BuMaximoTransData> notHandledList = buMaximoTransDataService.listAllTransferredNotHandledMaterialTransData();

        Set<String> assignDetailIdSet = new HashSet<>();
        for (BuMaximoTransData transData : notTransferredList) {
            if (transData.getType() == 3) {
                assignDetailIdSet.add(transData.getObjId());
            }
        }
        for (BuMaximoTransData transData : notHandledList) {
            if (transData.getType() == 3) {
                assignDetailIdSet.add(transData.getObjId());
            }
        }

        if (CollectionUtils.isNotEmpty(assignDetailIdSet)) {
            int clearAllStockUseSize = buMaterialStockUseService.clearAllStockUse();
            deleteInfo = "删除了" + clearAllStockUseSize + "条旧的库存占用；";

            List<List<String>> assignDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(assignDetailIdSet));
            List<BuMaterialAssignDetail> assignDetailList = new ArrayList<>();
            for (List<String> assignDetailIdBatchSub : assignDetailIdBatchSubList) {
                List<BuMaterialAssignDetail> subAssignDetailList = buMaterialAssignDetailMapper.selectListByIdList(assignDetailIdBatchSub);
                assignDetailList.addAll(subAssignDetailList);
            }
            Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();

            List<BuMaterialStockUse> stockUseList = getStockUseListByAssignDetailList(assignDetailList, warehouseBOMap, null, now, userId);
            buMaterialStockUseService.addStockUseList(stockUseList);

            return deleteInfo + String.format("根据%s条未成功处理消耗的分配明细，创建了%s条库存占用", assignDetailList.size(), stockUseList.size());
        } else {
            return deleteInfo + "没有需要库存占用的未成功处理消耗的分配明细";
        }
    }

    /**
     * @see CustomService#updateMaximoDataHandledAndDeleteUse(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateMaximoDataHandledAndDeleteUse(List<String> transDataIdList) throws Exception {
        List<BuMaximoTransData> transDataList = buMaximoTransDataService.listTransDataByIdList(transDataIdList);
        Set<String> assignDetailIdSet = new HashSet<>();
        Set<String> transDataIdSet = new HashSet<>();
        for (BuMaximoTransData transData : transDataList) {
            if (transData.getType() == 3) {
                String assignDetailId = transData.getObjId();
                assignDetailIdSet.add(assignDetailId);
                transDataIdSet.add(transData.getId());
            }
        }

        String transDataInfo = String.format("根据%s个transDataId，找到了对应的消耗同步数据%s条；", transDataIdList.size(), transDataList.size());
        if (CollectionUtils.isNotEmpty(assignDetailIdSet)) {
            int deleteSum = buMaterialStockUseService.deleteStockUseByAssignDetailIdList(new ArrayList<>(assignDetailIdSet));

            int updateConsumeSum = buMaximoTransDataService.updateConsumeTransDataHandled(new ArrayList<>(transDataIdSet));

            return String.format("找到了%s条分配明细，删除了%s条库存占用，更新了%s条消耗同步数据为已处理；", assignDetailIdSet.size(), deleteSum, updateConsumeSum);
        } else {
            return transDataInfo + "，找到了0条分配明细，没有删除库存占用；";
        }
    }

    /**
     * @see CustomService#updateAssignDetailEbsWhCode(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateAssignDetailEbsWhCode(String orderCodes) throws Exception {
        List<String> orderCodeList = Arrays.asList(orderCodes.split(","));
        if (CollectionUtils.isEmpty(orderCodeList)) {
            return "请输入工单号";
        }

        // 查询工单id
        LambdaQueryWrapper<BuWorkOrder> orderWrapper = new LambdaQueryWrapper<BuWorkOrder>()
                .in(BuWorkOrder::getOrderCode, orderCodeList);
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectList(orderWrapper);
        List<String> orderIdList = orderList.stream()
                .map(BuWorkOrder::getId)
                .distinct()
                .collect(Collectors.toList());
        // 查询仓库
        Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();
        Map<String, CacheWarehouseBO> codeWarehouseBOMap = new HashMap<>();
        for (Map.Entry<String, CacheWarehouseBO> warehouseBOEntry : warehouseBOMap.entrySet()) {
            CacheWarehouseBO warehouseBO = warehouseBOEntry.getValue();
            codeWarehouseBOMap.put(warehouseBO.getCode(), warehouseBO);
        }
        // 查询工单下的分配明细
        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListByOrderIdList(orderIdList);

        int totalSize = assignDetailList.size();
        int ebsWhCodeNullSize = 0;
        List<BuMaterialAssignDetail> updateEbsWhCodeList = new ArrayList<>();
        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            if (StringUtils.isBlank(assignDetail.getEbsWhCode()) || StringUtils.isBlank(assignDetail.getEbsWhChildCode())) {
                ebsWhCodeNullSize++;
                setDetailEbsInfo(assignDetail, codeWarehouseBOMap);
                updateEbsWhCodeList.add(assignDetail);
            }
        }
        if (CollectionUtils.isNotEmpty(updateEbsWhCodeList)) {
            List<List<BuMaterialAssignDetail>> batchSubList = DatabaseBatchSubUtil.batchSubList(updateEbsWhCodeList);
            for (List<BuMaterialAssignDetail> batchSub : batchSubList) {
                buMaterialAssignDetailMapper.updateListEbsWhCode(batchSub);
            }
        }

        return String.format("根据%s个工单找到了%s条分配明细，其中%s条没有ebs库位信息，更新了%s条的ebs库存信息；",
                orderList.size(), totalSize, ebsWhCodeNullSize, updateEbsWhCodeList.size());
    }

    /**
     * @see CustomService#addAssignDetailToMaximoTransDataTable(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addAssignDetailToMaximoTransDataTable(String orderCodes) throws Exception {
        List<String> orderCodeList = Arrays.asList(orderCodes.split(","));
        if (CollectionUtils.isEmpty(orderCodeList)) {
            return "请输入工单号";
        }

        Date now = new Date();

        // 查询工单id
        LambdaQueryWrapper<BuWorkOrder> orderWrapper = new LambdaQueryWrapper<BuWorkOrder>()
                .in(BuWorkOrder::getOrderCode, orderCodeList);
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectList(orderWrapper);
        List<String> orderIdList = orderList.stream()
                .map(BuWorkOrder::getId)
                .distinct()
                .collect(Collectors.toList());
        // 查询工单下的分配明细
        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListByOrderIdList(orderIdList);

        int totalSize = assignDetailList.size();
        int addToMaximoSize = 0;
        if (CollectionUtils.isNotEmpty(assignDetailList)) {
            List<String> assignDetailIdList = assignDetailList.stream()
                    .map(BuMaterialAssignDetail::getId)
                    .distinct()
                    .collect(Collectors.toList());
            addToMaximoSize = assignDetailIdList.size();
            // 添加数据到maximo同步中间表
            addMaximoTransData(assignDetailIdList, now);
        }

        return String.format("根据%s个工单找到了%s条分配明细，添加了%s条分配明细到maximo同步表；",
                orderList.size(), totalSize, addToMaximoSize);
    }

    /**
     * @see CustomService#testRegularWarehouseId()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Object testRegularWarehouseId() throws Exception {
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseMapper.selectList(Wrappers.emptyWrapper());
        List<BuMtrWarehouse> topList = warehouseList.stream()
                .filter(warehouse -> StringUtils.isBlank(warehouse.getParentId()))
                .collect(Collectors.toList());
        for (BuMtrWarehouse top : topList) {
            recurseAddChild(top, warehouseList);
        }
        topList.sort(Comparator.comparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())));

        Map<String, List<BuMtrWarehouse>> regularWarehouseIdListMap = new HashMap<>();
        setRegularWarehouseId(topList, null, regularWarehouseIdListMap);

        Map<String, List<BuMtrWarehouse>> repeatMap = new HashMap<>();
        for (Map.Entry<String, List<BuMtrWarehouse>> regularWarehouseIdListEntry : regularWarehouseIdListMap.entrySet()) {
            List<BuMtrWarehouse> list = regularWarehouseIdListEntry.getValue();
            if (list.size() >= 2) {
                repeatMap.put(regularWarehouseIdListEntry.getKey(), list);
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("仓库树", topList);
        resultMap.put("重复的", repeatMap);
        return resultMap;
    }

    /**
     * @see CustomService#updateOrderInfoToMaximo(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateOrderInfoToMaximo(String orderId) throws Exception {
        if (StringUtils.isBlank(orderId)) {
            throw new JeecgBootException("请输入工单id");
        }

        Date now = new Date();

        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        if (4 == order.getOrderStatus()) {
            throw new JeecgBootException("工单已关闭，不能修改");
        }

        addMaximoTransDataOrderReplace(Collections.singletonList(orderId), now);

        return "已添加工单" + order.getOrderCode() + "到maximo中间表，请确认传送到maximo是否成功";
    }

    /**
     * @see CustomService#updateOrderTaskTimeByOrderActTime()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateOrderTaskTimeByOrderActTime() throws Exception {
        // 查任务开始时间早于工单实际开始的 或者 任务结束时间晚于工单实际开始的
        List<BuWorkOrderTask> taskList = buWorkOrderTaskMapper.selectErrorTimeListOfCloseOrder();
        if (CollectionUtils.isEmpty(taskList)) {
            return "无异常数据";
        }

        SimpleDateFormat dateFormat = DateUtils.datetimeFormat.get();

        // 查工单
        Set<String> orderIdSet = taskList.stream()
                .map(BuWorkOrderTask::getOrderId)
                .collect(Collectors.toSet());
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectBatchIds(orderIdSet);
        Map<String, BuWorkOrder> idOrderMap = new HashMap<>();
        orderList.forEach(order -> idOrderMap.put(order.getId(), order));

        List<String> taskUpdateInfoList = new ArrayList<>();
        for (BuWorkOrderTask task : taskList) {
            String orderId = task.getOrderId();
            BuWorkOrder order = idOrderMap.get(orderId);

            Date taskStart = task.getTaskStart();
            Date taskFinish = task.getTaskFinish();
            Date orderActStart = order.getActStart();
            Date orderActFinish = order.getActFinish();

            boolean needUpdate = false;
            StringBuilder taskUpdateInfoBuilder = new StringBuilder(String.format("工单%s，任务%s，", order.getOrderCode(), task.getId()));
            if (taskStart.before(orderActStart)) {
                String oldTaskStart = dateFormat.format(taskStart);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(orderActStart);
                calendar.add(Calendar.SECOND, 10);
                taskStart = calendar.getTime();

                task.setTaskStart(taskStart);
                buWorkOrderTaskMapper.updateById(task);

                String newTaskStart = dateFormat.format(taskStart);
                taskUpdateInfoBuilder.append("更新了任务开始时间 ").append(oldTaskStart).append(" -> ").append(newTaskStart).append("，");

                needUpdate = true;
            }
            if (taskFinish.after(orderActFinish)) {
                String oldTaskFinish = dateFormat.format(taskFinish);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(orderActFinish);
                calendar.add(Calendar.SECOND, -10);
                taskFinish = calendar.getTime();

                task.setTaskFinish(taskFinish);
                buWorkOrderTaskMapper.updateById(task);

                String newTaskFinish = dateFormat.format(taskFinish);
                taskUpdateInfoBuilder.append("更新了任务结束时间 ").append(oldTaskFinish).append(" -> ").append(newTaskFinish).append("，");

                needUpdate = true;
            }

            if (needUpdate) {
                List<BuMaximoTransData> workTimeTransDataList = buMaximoTransDataService.listWorkTimeTransDataByOrderCode(order.getOrderCode());
                if (CollectionUtils.isNotEmpty(workTimeTransDataList)) {
                    for (BuMaximoTransData transData : workTimeTransDataList) {
                        String objJson = transData.getObjJson();
                        BuRepairTaskStaffArrange workTime = JSON.parseObject(objJson, BuRepairTaskStaffArrange.class);
                        workTime.setTaskStart(taskStart)
                                .setTaskFinish(taskFinish);
                        transData.setObjJson(JSON.toJSONString(workTime));
                    }

                    buMaximoTransDataService.updateTransDataList(workTimeTransDataList);
                    taskUpdateInfoBuilder.append("更新了").append(workTimeTransDataList.size()).append("条工时同步数据。");
                }

                taskUpdateInfoList.add(taskUpdateInfoBuilder.toString());
            }
        }

        return String.join("；", taskUpdateInfoList);
    }

    /**
     * @see CustomService#updateNotTransferredWorkTimeAndCloseOrderTransData()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateNotTransferredWorkTimeAndCloseOrderTransData() throws Exception {
        StringBuilder resultBuilder = new StringBuilder();

        // 查工单关闭
        List<BuMaximoTransData> closeOrderDataList = buMaximoTransDataService.listAllNotTransferredTransDataByType(Collections.singletonList(2));
        resultBuilder.append("关闭工单数据").append(closeOrderDataList.size()).append("条；");
        // 查工时
        List<BuMaximoTransData> allStaffArrangeDataList = buMaximoTransDataService.listAllNotTransferredTransDataByType(Collections.singletonList(6));
        resultBuilder.append("工时数据").append(allStaffArrangeDataList.size()).append("条；");

        Map<String, BuMaximoTransData> objIdStaffArrangeDataMap = new HashMap<>();
        List<BuRepairTaskStaffArrange> allStaffArrangeList = new ArrayList<>();
        for (BuMaximoTransData staffArrangeData : allStaffArrangeDataList) {
            objIdStaffArrangeDataMap.put(staffArrangeData.getObjId(), staffArrangeData);
            String objJson = staffArrangeData.getObjJson();
            BuRepairTaskStaffArrange staffArrange = JSON.parseObject(objJson, BuRepairTaskStaffArrange.class);
            allStaffArrangeList.add(staffArrange);
        }

        int updateCloseOrderCount = 0;
        int notUpdateCloseOrderCount = 0;
        int updateWorkTimeCount = 0;
        int addReplaceOrderCount = 0;
        List<BuMaximoTransData> needUpdateList = new ArrayList<>();
        List<BuMaximoTransData> needAddList = new ArrayList<>();
        for (BuMaximoTransData closeOrderData : closeOrderDataList) {
            boolean needUpdateCloseOrderData = false;

            String objJson = closeOrderData.getObjJson();
            BuWorkOrder order = JSON.parseObject(objJson, BuWorkOrder.class);
            Date closeOrderCreateTime = closeOrderData.getCreateTime();
            Calendar closeOrderCreateTimeCalendar = Calendar.getInstance();
            closeOrderCreateTimeCalendar.setTime(closeOrderCreateTime);
            closeOrderCreateTimeCalendar.add(Calendar.SECOND, 5);
            Date closeOrderCreateTimeBefore5 = closeOrderCreateTimeCalendar.getTime();
            closeOrderCreateTimeCalendar.add(Calendar.SECOND, 5);
            Date closeOrderCreateTimeBefore10 = closeOrderCreateTimeCalendar.getTime();

            String orderId = order.getId();
            String orderCode = order.getOrderCode();

            List<BuRepairTaskStaffArrange> staffArrangeList = allStaffArrangeList.stream()
                    .filter(staffArrange -> orderCode.equals(staffArrange.getOrderCode()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(staffArrangeList)) {
                Date orderActStart = order.getActStart();
                Date orderActFinish = order.getActFinish();

                // 检验是否满足条件
                for (BuRepairTaskStaffArrange staffArrange : staffArrangeList) {
                    boolean needUpdateWorkTimeData = false;

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
                        needUpdateWorkTimeData = true;
                    }
                    // 工时结束时间（工时结束时间=工时开始时间+时长）不能晚于工单实际结束时间
                    Calendar workTimeCalendar = Calendar.getInstance();
                    workTimeCalendar.setTime(workTimeStart);
                    workTimeCalendar.add(Calendar.SECOND, workTimeSecond);
                    Date workTimeFinish = workTimeCalendar.getTime();
                    if (null == staffArrange.getTaskFinish() || workTimeFinish.after(staffArrange.getTaskFinish())) {
                        staffArrange.setTaskFinish(workTimeFinish);
                        needUpdateWorkTimeData = true;
                    }
                    if (workTimeFinish.after(orderActFinish)) {
                        workTimeCalendar.add(Calendar.SECOND, 10);
                        orderActFinish = workTimeCalendar.getTime();
                        order.setActFinish(orderActFinish);
                        needUpdateCloseOrderData = true;
                    }

                    // 工时同步数据
                    if (needUpdateWorkTimeData) {
                        String staffArrangeId = staffArrange.getId();
                        BuMaximoTransData staffArrangeData = objIdStaffArrangeDataMap.get(staffArrangeId);
                        if (null != staffArrangeData) {
                            staffArrangeData.setObjJson(JSON.toJSONString(staffArrange))
                                    .setCreateTime(closeOrderCreateTimeBefore5);
                            needUpdateList.add(staffArrangeData);
                            updateWorkTimeCount++;
                        }
                    }
                }
                if (needUpdateCloseOrderData) {
                    // 修改工单同步数据（工单状态=已核实）
                    BuWorkOrder updateOrder = new BuWorkOrder();
                    BeanUtils.copyProperties(order, updateOrder);
                    updateOrder.setOrderStatus(2);
                    BuMaximoTransData maximoTransData = new BuMaximoTransData()
                            .setId(UUIDGenerator.generate())
                            .setType(7)
                            .setObjId(orderId)
                            .setObjJson(JSON.toJSONString(updateOrder))
                            .setCreateTime(closeOrderCreateTimeBefore10)
                            .setSuccessStatus(0)
                            .setHandleStatus(0)
                            .setMessage(null);
                    needAddList.add(maximoTransData);
                    addReplaceOrderCount++;
                }
            }
            // 关闭工单同步数据
            if (needUpdateCloseOrderData) {
                closeOrderData.setObjJson(JSON.toJSONString(order));
                needUpdateList.add(closeOrderData);
                updateCloseOrderCount++;
            } else {
                notUpdateCloseOrderCount++;
            }
        }

        resultBuilder.append("更新关闭工单数据").append(updateCloseOrderCount).append("条；")
                .append("不更新关闭工单数据").append(notUpdateCloseOrderCount).append("条；")
                .append("新增修改工单数据").append(addReplaceOrderCount).append("条；")
                .append("更新工时数据").append(updateWorkTimeCount).append("条；");

        if (CollectionUtils.isNotEmpty(needAddList)) {
            buMaximoTransDataService.addTransDataList(needAddList);
        }
        if (CollectionUtils.isNotEmpty(needUpdateList)) {
            buMaximoTransDataService.updateTransDataList(needUpdateList);
        }
        resultBuilder.append("总新增同步数据").append(needAddList.size()).append("条；")
                .append("总更新同步数据").append(needUpdateList.size()).append("条；");

        return resultBuilder.toString();
    }

    /**
     * @see CustomService#importWorkshopOwnSystemFault1(MultipartFile, Integer)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importWorkshopOwnSystemFault1(MultipartFile excelFile, Integer debugRowNum) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(excelFile, null, Arrays.asList(".xls", ".xlsx"));

        if (excelFile.isEmpty()) {
            throw new JeecgBootException("文件为空");
        }
        String filename = excelFile.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new JeecgBootException("文件名为空");
        }

        // 线路
        List<BuMtrLine> lineList = buMtrLineMapper.selectList(Wrappers.emptyWrapper());
        Map<String, String> lineNameIdMap = lineList.stream()
                .collect(Collectors.toMap(BuMtrLine::getLineName, BuMtrLine::getLineId));
        // 车辆
        List<BuTrainInfo> trainInfoList = buTrainInfoMapper.selectList(Wrappers.emptyWrapper());
        Map<String, String> trainNoIdMap = trainInfoList.stream()
                .collect(Collectors.toMap(BuTrainInfo::getTrainNo, BuTrainInfo::getId));
        // 系统
        Map<String, BuTrainAssetTypeBO> idSysBOMap = assetTypeCacheService.mapSys(null);
        Map<String, String> systemNameIdMap = new HashMap<>();
        Map<String, String> systemShortNameIdMap = new HashMap<>();
        for (Map.Entry<String, BuTrainAssetTypeBO> idSysBOEntry : idSysBOMap.entrySet()) {
            BuTrainAssetTypeBO sysBO = idSysBOEntry.getValue();
            systemNameIdMap.put(sysBO.getName(), sysBO.getId());
            systemShortNameIdMap.put(sysBO.getShortName(), sysBO.getId());
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

        Date now = new Date();
        int numberOfSheets = workbook.getNumberOfSheets();
        boolean hasMatchingForms = false;
        List<BuTrainHistoryFault> historyFaultList = new ArrayList<>();
        int rowCount = 0;
        // 车间系统导入检修故障
        String idPrefix = "cjxtdrjxgz";
        // 操作每个表单
        for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer serialNumberStringCellNum = null;
            Integer faultSnCellNum = null;
            Integer faultDescCellNum = null;
            Integer lineNameCellNum = null;
            Integer trainNameCellNum = null;
            Integer sysNameCellNum = null;
            Integer faultAssetNameCellNum = null;
            Integer happenDateStringCellNum = null;
            Integer happenTimeStringCellNum = null;
            Integer faultLevelStringCellNum = null;
            Integer reportUserIdCellNum = null;
            Integer reportTimeStringCellNum = null;
            Integer statusStringCellNum = null;
            Integer handleDateStringCellNum = null;
            Integer handleTimeStringCellNum = null;
            Integer handleUserCellNum = null;
            Integer remarkCellNum = null;
            Integer exFaultDetailCellNum = null;
            Integer exFaultTypeCellNum = null;
            Integer exEffectCellNum = null;
            Integer exTypicalStringCellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = getCellValue(cell);
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }

                        switch (value) {
                            case "序号":
                                serialNumberStringCellNum = cellNum;
                                break;
                            case "故障单号":
                                faultSnCellNum = cellNum;
                                break;
                            case "故障描述":
                                faultDescCellNum = cellNum;
                                break;
                            case "线别":
                                lineNameCellNum = cellNum;
                                break;
                            case "列车号":
                                trainNameCellNum = cellNum;
                                break;
                            case "系统":
                                sysNameCellNum = cellNum;
                                break;
                            case "设备类别":
                                faultAssetNameCellNum = cellNum;
                                break;
                            case "日期":
                                happenDateStringCellNum = cellNum;
                                break;
                            case "发生时间":
                                happenTimeStringCellNum = cellNum;
                                break;
                            case "故障等级":
                                faultLevelStringCellNum = cellNum;
                                break;
                            case "报告人":
                                reportUserIdCellNum = cellNum;
                                break;
                            case "接报时间":
                                reportTimeStringCellNum = cellNum;
                                break;
                            case "处理结果":
                                statusStringCellNum = cellNum;
                                break;
                            case "修复日期":
                                handleDateStringCellNum = cellNum;
                                break;
                            case "修复时间":
                                handleTimeStringCellNum = cellNum;
                                break;
                            case "处理人":
                                handleUserCellNum = cellNum;
                                break;
                            case "备注":
                                remarkCellNum = cellNum;
                                break;
                            case "对应的故障描述条款":
                                exFaultDetailCellNum = cellNum;
                                break;
                            case "类别":
                                exFaultTypeCellNum = cellNum;
                                break;
                            case "故障影响":
                                exEffectCellNum = cellNum;
                                break;
                            case "是否为重要故障":
                                exTypicalStringCellNum = cellNum;
                                break;
                        }
                    }
                }
            }

            if (null == serialNumberStringCellNum || null == faultSnCellNum || null == faultDescCellNum
                    || null == lineNameCellNum || null == trainNameCellNum || null == sysNameCellNum
                    || null == faultAssetNameCellNum || null == happenDateStringCellNum
                    || null == happenTimeStringCellNum || null == faultLevelStringCellNum || null == reportUserIdCellNum
                    || null == reportTimeStringCellNum || null == statusStringCellNum || null == handleDateStringCellNum
                    || null == handleTimeStringCellNum || null == handleUserCellNum || null == remarkCellNum
                    || null == exFaultDetailCellNum || null == exFaultTypeCellNum || null == exEffectCellNum
                    || null == exTypicalStringCellNum) {
                throw new JeecgBootException(String.format("第%s个表单%s，没有正确获取到故障信息单元格，不进行处理", sheetNum + 1, sheet.getSheetName()));
            } else {
                hasMatchingForms = true;
                // 操作每行数据
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    if (null != debugRowNum && debugRowNum == rowNum) {
                        log.debug("调试第" + debugRowNum + "行");
                    }

                    String serialNumberString = getCellValue(sheet.getRow(rowNum).getCell(serialNumberStringCellNum));
                    String faultSn = getCellValue(sheet.getRow(rowNum).getCell(faultSnCellNum));
                    String faultDesc = getCellValue(sheet.getRow(rowNum).getCell(faultDescCellNum));
                    String lineName = getCellValue(sheet.getRow(rowNum).getCell(lineNameCellNum));
                    String trainName = getCellValue(sheet.getRow(rowNum).getCell(trainNameCellNum));
                    String sysName = getCellValue(sheet.getRow(rowNum).getCell(sysNameCellNum));
                    String faultAssetName = getCellValue(sheet.getRow(rowNum).getCell(faultAssetNameCellNum));
                    String happenDateString = getCellValue(sheet.getRow(rowNum).getCell(happenDateStringCellNum));
                    String happenTimeString = getCellValue(sheet.getRow(rowNum).getCell(happenTimeStringCellNum));
                    String faultLevelString = getCellValue(sheet.getRow(rowNum).getCell(faultLevelStringCellNum));
                    String reportUserId = getCellValue(sheet.getRow(rowNum).getCell(reportUserIdCellNum));
                    String reportTimeString = getCellValue(sheet.getRow(rowNum).getCell(reportTimeStringCellNum));
                    String statusString = getCellValue(sheet.getRow(rowNum).getCell(statusStringCellNum));
                    String handleDateString = getCellValue(sheet.getRow(rowNum).getCell(handleDateStringCellNum));
                    String handleTimeString = getCellValue(sheet.getRow(rowNum).getCell(handleTimeStringCellNum));
                    String handleUser = getCellValue(sheet.getRow(rowNum).getCell(handleUserCellNum));
                    String remark = getCellValue(sheet.getRow(rowNum).getCell(remarkCellNum));
                    String exFaultDetail = getCellValue(sheet.getRow(rowNum).getCell(exFaultDetailCellNum));
                    String exFaultType = getCellValue(sheet.getRow(rowNum).getCell(exFaultTypeCellNum));
                    String exEffect = getCellValue(sheet.getRow(rowNum).getCell(exEffectCellNum));
                    String exTypicalString = getCellValue(sheet.getRow(rowNum).getCell(exTypicalStringCellNum));

                    // 数据处理
                    // 序号
                    int serialNumberInt = Double.valueOf(serialNumberString).intValue();
                    // 多个车号取第一个
                    if (trainName.contains("、")) {
                        trainName = trainName.split("、")[0];
                    }
                    // 车号不为空且长度=3，前面补0。用于处理数据如：103 -> 0103
                    if (StringUtils.isNotBlank(trainName) && !"ALL".equals(trainName) && trainName.length() == 3) {
                        trainName = "0" + trainName;
                    }
                    // 系统id根据系统名称获取
                    String sysId = systemNameIdMap.get(sysName);
                    if (StringUtils.isBlank(sysId)) {
                        sysId = systemShortNameIdMap.get(sysName);
                    }
                    // 时间=年月日+时分秒
                    Date happenDate = null;
                    Date happenTime = null;
                    Date reportTime = null;
                    if (StringUtils.isNotBlank(happenDateString)) {
                        happenDate = getRealDate(now, happenDateString, rowNum);
                        happenTime = getRealTime(happenDate, happenTimeString, rowNum);
                        reportTime = getRealTime(happenDate, reportTimeString, rowNum);
                    }
                    Date handleTime = null;
                    if (StringUtils.isNotBlank(handleDateString)) {
                        Date handleDate = getRealDate((null == happenDate ? now : happenDate), handleDateString, rowNum);
                        handleTime = getRealTime(handleDate, handleTimeString, rowNum);
                    }
                    // 级别
                    Integer levelInt = getLevelInt(faultLevelString);
                    // 正线
                    Integer faultOnlineInt = (StringUtils.isNotBlank(exFaultType) && exFaultType.contains("正线")) ? 1 : 0;
                    // 委外
                    Integer outsourceInt = (StringUtils.isNotBlank(handleUser) && handleUser.contains("委外")) ? 1 : 0;
                    // 状态：默认已处理
                    Integer statusInt = getStatusInt(statusString);
                    if (0 == statusInt) {
                        statusInt = 1;
                    }
                    // 处理状态：默认修复
                    int handleStatusInt = 2;

                    BuTrainHistoryFault historyFault = new BuTrainHistoryFault()
                            .setId(idPrefix + serialNumberInt)
                            .setFaultSn(StringUtils.isBlank(faultSn) ? "-" : faultSn)
                            .setFaultDesc(StringUtils.isBlank(faultDesc) ? "-" : faultDesc)
                            .setCategoryId(null)
                            .setCategoryName(null)
                            .setFaultCodeId(null)
                            .setFaultCodeName(null)
                            .setLineId(lineNameIdMap.getOrDefault(lineName, lineName.replaceAll("号线", "")))
                            .setLineName(lineName)
                            .setTrainId(trainNoIdMap.get(trainName))
                            .setTrainName(trainName)
                            .setSysId(sysId)
                            .setSysName(sysName)
                            .setSubSysId(null)
                            .setSubSysName(null)
                            .setFaultAssetId(null)
                            .setFaultAssetName(faultAssetName)
                            .setHappenTime(happenTime)
                            .setWorkOrderId(null)
                            .setWorkOrderName(null)
                            .setOrderTaskId(null)
                            .setOrderTaskName(null)
                            .setWorkStationId(null)
                            .setPhase(1)
                            .setFaultLevel(levelInt)
                            .setFaultOnline(faultOnlineInt)
                            .setHasDuty(0)
                            .setOutsource(outsourceInt)
                            .setReportGroup(exFaultType)
                            .setReportUserId(reportUserId)
                            .setReportTime(reportTime)
                            .setDutyEngineer(null)
                            .setStatus(statusInt)
                            .setHandleStatus(handleStatusInt)
                            .setHandleTime(handleTime)
                            .setHandleGroup(null)
                            .setHandleUser(handleUser)
                            .setRemark(remark)
                            .setExLocation(null)
                            .setExFaultDetail(exFaultDetail)
                            .setExFaultType(exFaultType)
                            .setExEffect(exEffect)
                            .setExTypical(exTypicalString)
                            .setArchiveType(1);
                    historyFaultList.add(historyFault);

                    rowCount++;
                }
            }
        }

        // 如果没有匹配到模板表单，返回异常
        if (!hasMatchingForms) {
            return "表单无效，请核对";
        } else {
            if (CollectionUtils.isNotEmpty(historyFaultList)) {
                // 删除旧的
                //TODO-zhaiyantao 2021/12/28 18:10 如果要通过故障编码删除，需修改导入时的故障编码要求唯一且不能重复
                int deleteCount = 0;
                List<String> idList = historyFaultList.stream()
                        .map(BuTrainHistoryFault::getId)
                        .collect(Collectors.toList());
                List<List<String>> idBatchSubList = DatabaseBatchSubUtil.batchSubList(idList);
                for (List<String> idBatchSub : idBatchSubList) {
                    int delete = buTrainHistoryFaultMapper.deleteBatchIds(idBatchSub);
                    deleteCount = deleteCount + delete;
                }
                // 插入新的
                List<List<BuTrainHistoryFault>> historyFaultBatchSubList = DatabaseBatchSubUtil.batchSubList(historyFaultList);
                for (List<BuTrainHistoryFault> historyFaultBatchSub : historyFaultBatchSubList) {
                    buTrainHistoryFaultMapper.insertList(historyFaultBatchSub);
                }

                return String.format("表单中有效故障信息有%s条，生成了%s条故障履历并导入（%s条旧的被删除后重新导入）",
                        rowCount, historyFaultList.size(), deleteCount);
            } else {
                return "表单中无故障数据，请核对";
            }
        }
    }

    /**
     * @see CustomService#importWorkshopOwnSystemFault2(MultipartFile, Integer)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importWorkshopOwnSystemFault2(MultipartFile excelFile, Integer debugRowNum) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(excelFile, null, Arrays.asList(".xls", ".xlsx"));

        if (excelFile.isEmpty()) {
            throw new JeecgBootException("文件为空");
        }
        String filename = excelFile.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new JeecgBootException("文件名为空");
        }

        // 线路
        List<BuMtrLine> lineList = buMtrLineMapper.selectList(Wrappers.emptyWrapper());
        Map<String, String> lineNameIdMap = lineList.stream()
                .collect(Collectors.toMap(BuMtrLine::getLineName, BuMtrLine::getLineId));
        // 车辆
        List<BuTrainInfo> trainInfoList = buTrainInfoMapper.selectList(Wrappers.emptyWrapper());
        Map<String, String> trainNoIdMap = trainInfoList.stream()
                .collect(Collectors.toMap(BuTrainInfo::getTrainNo, BuTrainInfo::getId));
        // 系统
        Map<String, BuTrainAssetTypeBO> idSysBOMap = assetTypeCacheService.mapSys(null);
        Map<String, String> systemNameIdMap = new HashMap<>();
        Map<String, String> systemShortNameIdMap = new HashMap<>();
        for (Map.Entry<String, BuTrainAssetTypeBO> idSysBOEntry : idSysBOMap.entrySet()) {
            BuTrainAssetTypeBO sysBO = idSysBOEntry.getValue();
            systemNameIdMap.put(sysBO.getName(), sysBO.getId());
            systemShortNameIdMap.put(sysBO.getShortName(), sysBO.getId());
        }
        // 班组
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupMapper.selectAllGroupInfo();
        Map<String, String> groupNameIdMap = groupList.stream()
                .collect(Collectors.toMap(BuMtrWorkshopGroup::getGroupName, BuMtrWorkshopGroup::getId));
        // 人员
        List<SysUser> userList = sysUserMapper.selectList(Wrappers.emptyWrapper());
        Map<String, String> userRealNameIdMap = userList.stream()
                .collect(Collectors.toMap(SysUser::getRealname, SysUser::getId, (value1, value2) -> value1));
        // 工位
        List<BuMtrWorkstation> workstationList = buMtrWorkstationBoardMapper.selectList(Wrappers.emptyWrapper());
        Map<String, String> workstationNameIdMap = workstationList.stream()
                .collect(Collectors.toMap(BuMtrWorkstation::getName, BuMtrWorkstation::getId));
        // 故障
        List<BuFaultInfo> faultList = buFaultInfoMapper.selectList(Wrappers.emptyWrapper());
//        // 故障原因
//        List<BuFaultReason> faultReasonList = buFaultReasonMapper.selectList(Wrappers.emptyWrapper());
//        Map<String, String> faultReasonDescIdMap = faultReasonList.stream()
//                .collect(Collectors.toMap(BuFaultReason::getReasonDesc, BuFaultReason::getId));
//        // 故障处理措施
//        List<BuFaultSolution> faultSolutionList = buFaultSolutionMapper.selectList(Wrappers.emptyWrapper());
//        Map<String, String> faultSolutionDescIdMap = faultSolutionList.stream()
//                .collect(Collectors.toMap(BuFaultSolution::getSolutionDesc, BuFaultSolution::getId));

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

        Date now = new Date();
        int numberOfSheets = workbook.getNumberOfSheets();
        boolean hasMatchingForms = false;
        List<BuFaultInfo> needAddFaultList = new ArrayList<>();
        int rowCount = 0;
        int deletedRowCount = 0;
        int existFaultCount = 0;
        // 车间系统导入架大修故障
        String idPrefix = "cjxtdrjdxgz";
        // 操作每个表单
        for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer deletedFlagCellNum = null;
            Integer serialNumberStringCellNum = null;
            Integer faultDescCellNum = null;
            Integer sysNameCellNum = null;
            Integer happenDateStringCellNum = null;
            Integer happenTimeStringCellNum = null;
            Integer lineNameCellNum = null;
            Integer trainNameCellNum = null;
            Integer carriageNameCellNum = null;
            Integer phaseStringCellNum = null;
            Integer faultLevelStringCellNum = null;
            Integer hasDutyStringCellNum = null;
            Integer reportGroupNameCellNum = null;
            Integer reportUserNameCellNum = null;
            Integer dutyEngineerNameCellNum = null;
            Integer statusStringCellNum = null;
            Integer closeDateStringCellNum = null;
            Integer closeTimeStringCellNum = null;
            Integer workstationNameCellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = getCellValue(cell);
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }

                        switch (value) {
                            case "故障保存状态":
                                deletedFlagCellNum = cellNum;
                                break;
                            case "序号":
                                serialNumberStringCellNum = cellNum;
                                break;
                            case "车辆故障详细描述":
                                faultDescCellNum = cellNum;
                                break;
                            case "故障所属系统":
                                sysNameCellNum = cellNum;
                                break;
                            case "发生日期":
                                happenDateStringCellNum = cellNum;
                                break;
                            case "发生时间":
                                happenTimeStringCellNum = cellNum;
                                break;
                            case "线别":
                                lineNameCellNum = cellNum;
                                break;
                            case "列车号":
                                trainNameCellNum = cellNum;
                                break;
                            case "车辆号":
                                carriageNameCellNum = cellNum;
                                break;
                            case "架修期分类":
                                phaseStringCellNum = cellNum;
                                break;
                            case "故障等级":
                                faultLevelStringCellNum = cellNum;
                                break;
                            case "是否有责":
                                hasDutyStringCellNum = cellNum;
                                break;
                            case "责任主体":
                                reportGroupNameCellNum = cellNum;
                                break;
                            case "报告人":
                                reportUserNameCellNum = cellNum;
                                break;
                            case "工程师确认":
                                dutyEngineerNameCellNum = cellNum;
                                break;
                            case "完成情况":
                                statusStringCellNum = cellNum;
                                break;
                            case "关闭日期":
                                closeDateStringCellNum = cellNum;
                                break;
                            case "关闭时间":
                                closeTimeStringCellNum = cellNum;
                                break;
                            case "工位":
                                workstationNameCellNum = cellNum;
                                break;
                        }
                    }
                }
            }

            if (null == serialNumberStringCellNum || null == faultDescCellNum || null == sysNameCellNum
                    || null == happenDateStringCellNum || null == happenTimeStringCellNum || null == lineNameCellNum
                    || null == trainNameCellNum || null == carriageNameCellNum || null == phaseStringCellNum
                    || null == faultLevelStringCellNum || null == hasDutyStringCellNum || null == reportGroupNameCellNum
                    || null == closeDateStringCellNum || null == closeTimeStringCellNum || null == workstationNameCellNum) {
                throw new JeecgBootException(String.format("第%s个表单%s，没有正确获取到故障信息单元格，不进行处理", sheetNum + 1, sheet.getSheetName()));
            } else {
                hasMatchingForms = true;
                // 操作每行数据
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    if (null != debugRowNum && debugRowNum == rowNum) {
                        log.debug("调试第" + debugRowNum + "行");
                    }

                    String deletedFlag = getCellValue(sheet.getRow(rowNum).getCell(deletedFlagCellNum));
                    if (StringUtils.isNotBlank(deletedFlag) && "已删除".equals(deletedFlag)) {
                        deletedRowCount++;
                    } else {
                        String serialNumberString = getCellValue(sheet.getRow(rowNum).getCell(serialNumberStringCellNum));
                        String faultDesc = getCellValue(sheet.getRow(rowNum).getCell(faultDescCellNum));
                        String sysName = getCellValue(sheet.getRow(rowNum).getCell(sysNameCellNum));
                        String happenDateString = getCellValue(sheet.getRow(rowNum).getCell(happenDateStringCellNum));
                        String happenTimeString = getCellValue(sheet.getRow(rowNum).getCell(happenTimeStringCellNum));
                        String lineName = getCellValue(sheet.getRow(rowNum).getCell(lineNameCellNum));
                        String trainName = getCellValue(sheet.getRow(rowNum).getCell(trainNameCellNum));
                        String carriageName = getCellValue(sheet.getRow(rowNum).getCell(carriageNameCellNum));
                        String phaseString = getCellValue(sheet.getRow(rowNum).getCell(phaseStringCellNum));
                        String faultLevelString = getCellValue(sheet.getRow(rowNum).getCell(faultLevelStringCellNum));
                        String hasDutyString = getCellValue(sheet.getRow(rowNum).getCell(hasDutyStringCellNum));
                        String reportGroupName = getCellValue(sheet.getRow(rowNum).getCell(reportGroupNameCellNum));
                        String reportUserName = getCellValue(sheet.getRow(rowNum).getCell(reportUserNameCellNum));
                        String dutyEngineerName = getCellValue(sheet.getRow(rowNum).getCell(dutyEngineerNameCellNum));
                        String statusString = getCellValue(sheet.getRow(rowNum).getCell(statusStringCellNum));
                        String closeDateString = getCellValue(sheet.getRow(rowNum).getCell(closeDateStringCellNum));
                        String closeTimeString = getCellValue(sheet.getRow(rowNum).getCell(closeTimeStringCellNum));
                        String workstationName = getCellValue(sheet.getRow(rowNum).getCell(workstationNameCellNum));

                        // 数据处理
                        // 序号
                        int serialNumberInt = Double.valueOf(serialNumberString).intValue();
                        // 多个车号取第一个
                        if (trainName.contains("、")) {
                            trainName = trainName.split("、")[0];
                        }
                        if (!trainNoIdMap.containsKey(trainName) && StringUtils.isNotBlank(carriageName) && trainNoIdMap.containsKey(carriageName)) {
                            trainName = carriageName;
                        }
                        // 车号不为空且长度=3，前面补0。用于处理数据如：103 -> 0103
                        if (StringUtils.isNotBlank(trainName) && !"ALL".equals(trainName) && trainName.length() == 3) {
                            trainName = "0" + trainName;
                        }
                        // 系统id根据系统名称获取
                        String sysId = systemNameIdMap.get(sysName);
                        if (StringUtils.isBlank(sysId)) {
                            sysId = systemShortNameIdMap.get(sysName);
                        }
                        // 时间=年月日+时分秒
                        // 时间=年月日+时分秒
                        Date happenDate;
                        Date happenTime;
                        if (StringUtils.isNotBlank(happenDateString)) {
                            happenDate = getRealDate(now, happenDateString, rowNum);
                            happenTime = getRealTime(happenDate, happenTimeString, rowNum);
                        } else {
                            throw new RuntimeException("第" + rowNum + "行数据有问题，发生日期为空");
                        }
                        Date reportTime = happenTime;
                        Date closeTime = null;
                        if (StringUtils.isNotBlank(closeDateString)) {
                            Date closeDate = getRealDate((null == happenDate ? now : happenDate), closeDateString, rowNum);
                            closeTime = getRealTime(closeDate, closeTimeString, rowNum);
                        }
                        // 发现阶段
                        Integer phaseInt = getPhaseInt(phaseString);
                        // 级别
                        Integer levelInt = getLevelInt(faultLevelString);
                        // 是否有责
                        Integer hasDutyInt = (StringUtils.isNotBlank(hasDutyString) && "是".equals(happenDateString)) ? 1 : 0;
                        // 状态：默认已处理
                        Integer statusInt = getStatusInt(statusString);
                        if (0 == statusInt) {
                            statusInt = 1;
                        }
                        // 处理状态：默认修复
                        int handleStatusInt = 2;

                        BuFaultInfo fault = new BuFaultInfo()
                                .setId(UUIDGenerator.generate())
                                .setCategoryId(null)
                                .setFaultCodeId(null)
                                .setFaultSn(null)
                                .setFaultDesc(StringUtils.isBlank(faultDesc) ? "-" : faultDesc)
                                .setSysId(StringUtils.isBlank(sysId) ? "-" : sysId)
                                .setSubSysId(null)
                                .setFaultAssetId(trainNoIdMap.get(trainName))
                                .setHappenTime(happenTime)
                                .setWorkOrderId(null)
                                .setOrderTaskId(null)
                                .setLineId(lineNameIdMap.getOrDefault(lineName, lineName.replaceAll("号线", "")))
                                .setTrainNo(trainName)
                                .setPhase(phaseInt)
                                .setFaultLevel(levelInt)
                                .setFaultOnline(0)
                                .setHasDuty(hasDutyInt)
                                .setOutsource(0)
                                .setReportGroupId(groupNameIdMap.getOrDefault(reportGroupName, "-"))
                                .setReportUserId(userRealNameIdMap.getOrDefault(reportUserName, "-"))
                                .setReportTime(reportTime)
                                .setDutyEngineer(userRealNameIdMap.getOrDefault(dutyEngineerName, "-"))
                                .setStatus(statusInt)
                                .setCloseUserId(null)
                                .setCloseTime(closeTime)
                                .setSubmitStatus(1)
                                .setWorkStationId(workstationNameIdMap.getOrDefault(workstationName, "-"))
                                .setCreateTime(now)
                                .setCreateBy("admin")
                                .setUpdateTime(null)
                                .setUpdateBy(null)
                                .setCreateType(1)
                                .setTrainStructureId(null)
                                .setHandleStatus(handleStatusInt)
                                .setHandleOrderId(null)
                                .setOutSupplierId(null)
                                .setFormValueId(null)
                                .setPlanId("-");
                        boolean exist = checkFaultExist(fault, faultList);
                        if (exist) {
                            existFaultCount++;
                        } else {
                            needAddFaultList.add(fault);
                            faultList.add(fault);
                        }
                    }
                    rowCount++;
                }
            }
        }

        // 如果没有匹配到模板表单，返回异常
        if (!hasMatchingForms) {
            return "表单格式不正确，请确认";
        } else {
            if (CollectionUtils.isNotEmpty(needAddFaultList)) {
                needAddFaultList.sort(Comparator.comparing(BuFaultInfo::getReportTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(BuFaultInfo::getHappenTime, Comparator.nullsLast(Comparator.naturalOrder())));
                // 生成故障编号
                for (BuFaultInfo fault : needAddFaultList) {
                    String faultSn = serialNumberGenerate.generateSerialNumberByCode("JDXGZ");
                    fault.setFaultSn(faultSn + "LS");
                }

                // 删除旧的
                // 通过故障编码删除，导入时的故障编码要求唯一且不能重复
                int deleteCount = 0;
                List<String> faultSnList = needAddFaultList.stream()
                        .map(BuFaultInfo::getFaultSn)
                        .collect(Collectors.toList());
                List<List<String>> faultSnBatchSubList = DatabaseBatchSubUtil.batchSubList(faultSnList);
                for (List<String> faultSnBatchSub : faultSnBatchSubList) {
                    LambdaQueryWrapper<BuFaultInfo> deleteFaultWrapper = new LambdaQueryWrapper<BuFaultInfo>()
                            .in(BuFaultInfo::getFaultSn, faultSnBatchSub);
                    int delete = buFaultInfoMapper.delete(deleteFaultWrapper);
                    deleteCount = deleteCount + delete;
                }
                // 插入新的
                List<List<BuFaultInfo>> faultBatchSubList = DatabaseBatchSubUtil.batchSubList(needAddFaultList);
                for (List<BuFaultInfo> faultBatchSub : faultBatchSubList) {
                    buFaultInfoMapper.insertList(faultBatchSub);
                }

                return String.format("表单中有效故障信息有%s条（其中有%s条标记已删除的、%s条已存在的），生成了%s条故障记录并导入（%s条旧的被删除后重新导入）",
                        rowCount, deletedRowCount, existFaultCount, needAddFaultList.size(), deleteCount);
            } else {
                return "表单中没有需要导入的故障数据";
            }
        }
    }

    /**
     * @see CustomService#transFaultToMaximo(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String transFaultToMaximo(String faultSns) throws Exception {
        Date now = new Date();

        List<String> faultSnList = Arrays.asList(faultSns.split(","));
        if (CollectionUtils.isEmpty(faultSnList)) {
            return "请输入要同步的故障编号";
        }

        List<String> faultIdList = new ArrayList<>();
        List<List<String>> faultSnBatchSubList = DatabaseBatchSubUtil.batchSubList(faultSnList);
        for (List<String> faultSnBatchSub : faultSnBatchSubList) {
            LambdaQueryWrapper<BuFaultInfo> wrapper = new LambdaQueryWrapper<BuFaultInfo>()
                    .in(BuFaultInfo::getFaultSn, faultSnBatchSub);
            List<BuFaultInfo> subFaultList = buFaultInfoMapper.selectList(wrapper);
            subFaultList.forEach(fault -> faultIdList.add(fault.getId()));
        }

        // 查故障
        List<BuFaultInfo> faultList = buFaultInfoMapper.selectListForMaximoByIdList(faultIdList);

        // 处理同步
        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuFaultInfo fault : faultList) {
            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(5)
                    .setObjId(fault.getId())
                    .setObjJson(JSON.toJSONString(fault))
                    .setCreateTime(now)
                    .setSuccessStatus(0)
                    .setHandleStatus(0)
                    .setMessage(null);
            dataList.add(maximoTransData);
        }

        buMaximoTransDataService.addTransDataList(dataList);

        return String.format("根据%s个故障编号找到%s个故障，同步到maximo%s个故障",
                faultSnList.size(), faultList.size(), dataList.size());
    }

    /**
     * @see CustomService#transOrderToMaximo(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String transOrderToMaximo(String orderCodes) throws Exception {
        Date now = new Date();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(now);
        nowCalendar.add(Calendar.SECOND, 5);
        Date nowAfter5 = nowCalendar.getTime();
        nowCalendar.add(Calendar.SECOND, 5);
        Date nowAfter10 = nowCalendar.getTime();

        List<String> orderCodeList = Arrays.asList(orderCodes.split(","));
        if (CollectionUtils.isEmpty(orderCodeList)) {
            return "请输入要同步的工单号";
        }

        List<String> orderIdList = new ArrayList<>();
        List<List<String>> orderCodeBatchSubList = DatabaseBatchSubUtil.batchSubList(orderCodeList);
        for (List<String> orderCodeBatchSub : orderCodeBatchSubList) {
            LambdaQueryWrapper<BuWorkOrder> wrapper = new LambdaQueryWrapper<BuWorkOrder>()
                    .in(BuWorkOrder::getOrderCode, orderCodeBatchSub);
            List<BuWorkOrder> subOrderList = buWorkOrderMapper.selectList(wrapper);
            subOrderList.forEach(order -> orderIdList.add(order.getId()));
        }

        // 查工单
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectOrderForMaximoByOrderIdList(orderIdList);
        // 包含发料工单，给出提示
        List<String> orderType4OrderCodeList = orderList.stream()
                .filter(order -> 4 == order.getOrderType())
                .map(BuWorkOrder::getOrderCode)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(orderType4OrderCodeList)) {
            throw new JeecgBootException("包含发料工单" + String.join(",", orderType4OrderCodeList) + "，请核对后再处理");
        }
        List<String> orderType5OrderCodeList = orderList.stream()
                .filter(order -> 5 == order.getOrderType())
                .map(BuWorkOrder::getOrderCode)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(orderType5OrderCodeList)) {
            throw new JeecgBootException("包含车间消耗工单" + String.join(",", orderType5OrderCodeList) + "，请核对后再处理");
        }

        // 查工时
        List<BuRepairTaskStaffArrange> allStaffArrangeList = buRepairTaskStaffArrangeMapper.selectListForMaximoByOrderIdList(orderIdList);
        // 过滤掉没有工号、或者没工单任务开始时间的
        allStaffArrangeList.removeIf(staffArrange -> StringUtils.isBlank(staffArrange.getWorkNo()) || null == staffArrange.getTaskStart());
        // 过滤掉实际作业工时小于0.01的
        allStaffArrangeList.removeIf(staffArrange -> null == staffArrange.getWorkTime() || BigDecimal.valueOf(0.01D).compareTo(staffArrange.getWorkTime()) > 0);

        // 处理同步
        List<BuMaximoTransData> dataList = new ArrayList<>();
        int transOrderCount = 0;
        int transWorkTimeCount = 0;
        for (BuWorkOrder order : orderList) {
            String orderId = order.getId();
            String orderCode = order.getOrderCode();
            Integer orderStatus = order.getOrderStatus();
            // 设置工单资产设备编码
            List<String> trainAssetCodeList = buWorkOrderTaskMapper.selectTrainAssetCodeByOrderId(orderId);
            if (CollectionUtils.isNotEmpty(trainAssetCodeList)) {
                order.setAssetCode(trainAssetCodeList.get(0));
            }

            List<BuRepairTaskStaffArrange> staffArrangeList = allStaffArrangeList.stream()
                    .filter(staffArrange -> orderCode.equals(staffArrange.getOrderCode()))
                    .collect(Collectors.toList());
            if (4 == orderStatus) {
                if (CollectionUtils.isNotEmpty(staffArrangeList)) {
                    // 如果工单已关闭，且有工时，同步数据：新增工单->人员工时->关闭工单
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
                        transWorkTimeCount++;
                    }
                    // 新增工单同步数据（工单状态=已核实）
                    BuWorkOrder updateOrder = new BuWorkOrder();
                    BeanUtils.copyProperties(order, updateOrder);
                    updateOrder.setOrderStatus(2);
                    BuMaximoTransData addOrderMaximoTransData = new BuMaximoTransData()
                            .setId(UUIDGenerator.generate())
                            .setType(1)
                            .setObjId(orderId)
                            .setObjJson(JSON.toJSONString(updateOrder))
                            .setCreateTime(now)
                            .setSuccessStatus(0)
                            .setHandleStatus(0)
                            .setMessage(null);
                    dataList.add(addOrderMaximoTransData);
                    // 关闭工单同步数据
                    BuMaximoTransData closeOrderMaximoTransData = new BuMaximoTransData()
                            .setId(UUIDGenerator.generate())
                            .setType(2)
                            .setObjId(orderId)
                            .setObjJson(JSON.toJSONString(order))
                            .setCreateTime(nowAfter10)
                            .setSuccessStatus(0)
                            .setHandleStatus(0)
                            .setMessage(null);
                    dataList.add(closeOrderMaximoTransData);
                } else {
                    // 如果工单已关闭，且没有工时，同步数据：新增工单->关闭工单
                    // 新增工单同步数据（工单状态=已核实）
                    BuWorkOrder updateOrder = new BuWorkOrder();
                    BeanUtils.copyProperties(order, updateOrder);
                    updateOrder.setOrderStatus(2);
                    BuMaximoTransData addOrderMaximoTransData = new BuMaximoTransData()
                            .setId(UUIDGenerator.generate())
                            .setType(1)
                            .setObjId(orderId)
                            .setObjJson(JSON.toJSONString(updateOrder))
                            .setCreateTime(now)
                            .setSuccessStatus(0)
                            .setHandleStatus(0)
                            .setMessage(null);
                    dataList.add(addOrderMaximoTransData);
                    // 关闭工单同步数据
                    BuMaximoTransData closeOrderMaximoTransData = new BuMaximoTransData()
                            .setId(UUIDGenerator.generate())
                            .setType(2)
                            .setObjId(orderId)
                            .setObjJson(JSON.toJSONString(order))
                            .setCreateTime(nowAfter10)
                            .setSuccessStatus(0)
                            .setHandleStatus(0)
                            .setMessage(null);
                    dataList.add(closeOrderMaximoTransData);
                }
            } else {
                // 如果工单未关闭，同步数据：新增工单
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
            transOrderCount++;
        }
        buMaximoTransDataService.addTransDataList(dataList);

        return String.format("根据%s个工单号找到%s个工单，同步到maximo%s个工单，%s个人员工时",
                orderCodeList.size(), orderList.size(), transOrderCount, transWorkTimeCount);
    }

    /**
     * @see CustomService#closeOrderDecreaseMaterialGroupStock(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String closeOrderDecreaseMaterialGroupStock(String orderCodes) throws Exception {
        Date now = new Date();

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        List<String> orderCodeList = Arrays.asList(orderCodes.split(","));
        if (CollectionUtils.isEmpty(orderCodeList)) {
            return "请输入要同步的工单号";
        }

        // 查工单
        List<BuWorkOrder> orderList = new ArrayList<>();
        List<List<String>> orderCodeBatchSubList = DatabaseBatchSubUtil.batchSubList(orderCodeList);
        for (List<String> orderCodeBatchSub : orderCodeBatchSubList) {
            LambdaQueryWrapper<BuWorkOrder> wrapper = new LambdaQueryWrapper<BuWorkOrder>()
                    .in(BuWorkOrder::getOrderCode, orderCodeBatchSub);
            List<BuWorkOrder> subOrderList = buWorkOrderMapper.selectList(wrapper);
            orderList.addAll(subOrderList);
        }

        if (CollectionUtils.isEmpty(orderList)) {
            return "无法查询到工单";
        }

        StringBuilder resultStringBuilder = new StringBuilder();
        for (BuWorkOrder order : orderList) {
            // 查询工单物料实际消耗
            List<BuWorkOrderMaterialActs> orderMaterialActsList = buWorkOrderMaterialActsMapper.selectListByOrderId(order.getId());
            // 无需要消耗的工单物料实际消耗返回
            if (CollectionUtils.isEmpty(orderMaterialActsList)) {
                continue;
            }

            // 查询班组库存
            List<String> groupStockIdList = orderMaterialActsList.stream()
                    .map(BuWorkOrderMaterialActs::getGroupStockId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<String, org.jeecg.modules.dispatch.workorder.bean.BuMaterialGroupStock> idGroupStockMap = new HashMap<>();
            List<List<String>> groupStockIdBatchSubList = DatabaseBatchSubUtil.batchSubList(groupStockIdList);
            for (List<String> groupStockIdBatchSub : groupStockIdBatchSubList) {
                List<org.jeecg.modules.dispatch.workorder.bean.BuMaterialGroupStock> subGroupStockList = buMaterialGroupStockDispatchMapper.selectBatchIds(groupStockIdBatchSub);
                for (org.jeecg.modules.dispatch.workorder.bean.BuMaterialGroupStock groupStock : subGroupStockList) {
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

                org.jeecg.modules.dispatch.workorder.bean.BuMaterialGroupStock matchGroupStock = idGroupStockMap.get(groupStockId);
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
            List<org.jeecg.modules.dispatch.workorder.bean.BuMaterialGroupStock> needUpdateGroupStockList = idGroupStockMap.values().stream()
                    .filter(groupStock -> null != groupStock.getNeedUpdate() && groupStock.getNeedUpdate())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(needUpdateGroupStockList)) {
                List<List<org.jeecg.modules.dispatch.workorder.bean.BuMaterialGroupStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateGroupStockList);
                for (List<org.jeecg.modules.dispatch.workorder.bean.BuMaterialGroupStock> batchSub : batchSubList) {
                    buMaterialGroupStockDispatchMapper.updateListAmount(batchSub);
                }

                List<BuMaterialStockChange> changeList = new ArrayList<>();
                for (org.jeecg.modules.dispatch.workorder.bean.BuMaterialGroupStock groupStock : needUpdateGroupStockList) {
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

                String changeIds = changeList.stream()
                        .map(BuMaterialStockChange::getId)
                        .collect(Collectors.joining(","));
                resultStringBuilder.append("工单").append(order.getOrderCode())
                        .append("扣减了").append(changeList.size()).append("条班组库存（库存变动id=").append(changeIds).append("）")
                        .append("；");
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

        return resultStringBuilder.toString();
    }

    /**
     * @see CustomService#updateTpPLanMustToMaterial(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateTpPLanMustToMaterial(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        List<BuTpRepairPlanMustReplace> mustReplaceList = buTpRepairPlanMustReplaceMapper.selectBatchIds(idList);

        List<BuTpRepairPlanMaterial> materialList = new ArrayList<>();
        for (BuTpRepairPlanMustReplace mustReplace : mustReplaceList) {
            BuTpRepairPlanMaterial material = new BuTpRepairPlanMaterial()
                    .setId(UUIDGenerator.generate())
                    .setTaskId(mustReplace.getTaskId())
                    .setMaterialTypeId(mustReplace.getMaterialTypeId())
                    .setAmount(mustReplace.getAmount())
                    .setRemark(null)
                    .setUseCategory(1);
            materialList.add(material);
        }

        if (CollectionUtils.isNotEmpty(materialList)) {
            List<List<BuTpRepairPlanMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(materialList);
            for (List<BuTpRepairPlanMaterial> batchSub : batchSubList) {
                buTpRepairPlanMaterialMapper.insertList(batchSub);
            }
        }

        return String.format("由%s个必换件清单，生成了%s个物料", mustReplaceList.size(), materialList.size());
    }

    /**
     * @see CustomService#getErrorStockLevel4()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<Map<String, String>> getErrorStockLevel4() throws Exception {
        // 查询库存
        List<BuMaterialStock> stockList = buMaterialStockMapper.selectList(Wrappers.emptyWrapper());
        // 查询仓库
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseMapper.selectList(Wrappers.emptyWrapper());
        Map<String, BuMtrWarehouse> idWarehouseMap = new HashMap<>();
        for (BuMtrWarehouse warehouse : warehouseList) {
            idWarehouseMap.put(warehouse.getId(), warehouse);
        }

        List<Map<String, String>> errorStockList = new ArrayList<>();
        for (BuMaterialStock stock : stockList) {
            String warehouseId = stock.getWarehouseId();
            String materialTypeId = stock.getMaterialTypeId();
            BigDecimal amount = stock.getAmount();

            BuMtrWarehouse warehouse = idWarehouseMap.get(warehouseId);
            if (warehouse.getWhLevel() == 4) {
                // 3级库，获取下属4级库的
                Set<String> childWarehouseIdSet = warehouseList.stream()
                        .filter(item -> warehouseId.equals(item.getParentId()))
                        .map(BuMtrWarehouse::getId)
                        .collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(childWarehouseIdSet)) {
                    List<BuMaterialStock> childStockList = stockList.stream()
                            .filter(item -> childWarehouseIdSet.contains(item.getWarehouseId())
                                    && materialTypeId.equals(item.getMaterialTypeId()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(childStockList)) {
                        BigDecimal childStockSum = childStockList.stream()
                                .map(BuMaterialStock::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (childStockSum.compareTo(amount) > 0) {
                            // 下属4级库的库存量大于3级库的库存量
                            Map<String, String> errorStock = new HashMap<>();
                            errorStock.put("material", materialTypeId);
                            errorStock.put("level3", amount.stripTrailingZeros().toPlainString());
                            errorStock.put("level4", childStockSum.stripTrailingZeros().toPlainString());
                            errorStock.put("diff", amount.subtract(childStockSum).stripTrailingZeros().toPlainString());
                            errorStockList.add(errorStock);
                        }
                    }
                }
            }
        }

        return errorStockList;
    }

    /**
     * @see CustomService#deleteOrderAndWFAndRegenerate(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String deleteOrderAndWFAndRegenerate(String orderCodes) {
        List<String> orderCodeList = Arrays.asList(orderCodes.split(","));
        if (CollectionUtils.isEmpty(orderCodeList)) {
            throw new JeecgBootException("工单号为空：" + orderCodes);
        }
        if (orderCodeList.size() > 10) {
            throw new JeecgBootException("接口运行慢，请小批量运行，一次10个工单。当前工单号数量：" + orderCodeList.size());
        }

        // 查询工单
        LambdaQueryWrapper<BuWorkOrder> orderWrapper = new LambdaQueryWrapper<BuWorkOrder>()
                .in(BuWorkOrder::getOrderCode, orderCodeList);
        List<BuWorkOrder> orderList = buWorkOrderMapper.selectList(orderWrapper);
        if (orderList.size() != orderCodeList.size()) {
            List<String> codeList = orderList.stream()
                    .map(BuWorkOrder::getOrderCode)
                    .collect(Collectors.toList());
            ArrayList<String> allCodeList = new ArrayList<>(orderCodeList);
            allCodeList.removeAll(codeList);
            throw new JeecgBootException("工单未找到，工单号：" + String.join(",", allCodeList));
        }
        List<String> status1List = orderList.stream()
                .filter(item -> item.getOrderStatus() != 1)
                .map(BuWorkOrder::getOrderCode)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(status1List)) {
            throw new JeecgBootException("仅已下发(未核实)状态的工单可以使用该接口修改，不符合条件的工单：" + String.join(",", status1List));
        }

        int count = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (BuWorkOrder order : orderList) {
            String orderId = order.getId();
            // 查询业务流程实列状态表
            WfBizStatus wfBizStatus = wfBizStatusMapper.selectById(orderId);
            String processInstanceId = wfBizStatus.getProcessInstanceId();
            try {
                // 删除流程实例
                workflowForwardService.deleteInstance(processInstanceId);
            } catch (Exception ex) {
                stringBuilder.append("工单").append(order.getOrderCode())
                        .append("流程删除异常[")
                        .append(ex.getMessage())
                        .append("]，")
                        .append("忽略继续处理、");
            }
            // 删除业务流程实列状态表
            wfBizStatusMapper.deleteById(orderId);
            // 修改工单为未下发
            buWorkOrderMapper.updateUnIssuing(orderId);
            count++;
        }

        return String.format("传入工单号%s个，成功处理%s个。处理过程信息：%s", orderCodeList.size(), count, stringBuilder.toString());
    }

    /**
     * @see CustomService#setFaultInfoWorkshopId(Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String setFaultInfoWorkshopId(Boolean needUpdate) {
        // 查询故障
        LambdaQueryWrapper<BuFaultInfo> faultInfoWrapper = new LambdaQueryWrapper<BuFaultInfo>()
                .isNull(BuFaultInfo::getWorkshopId);
        List<BuFaultInfo> faultInfoList = buFaultInfoMapper.selectList(faultInfoWrapper);
        if (CollectionUtils.isEmpty(faultInfoList)) {
            return "没有车间id为空的故障数据";
        }
        // 班组
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupMapper.selectAllGroupInfo();
        Map<String, BuMtrWorkshopGroup> idGroupMap = groupList.stream()
                .collect(Collectors.toMap(BuMtrWorkshopGroup::getId, Function.identity()));

        int updateCount = 0;
        int canUpdateCount = 0;
        for (BuFaultInfo faultInfo : faultInfoList) {
            // 设置车间id
            if (StringUtils.isBlank(faultInfo.getWorkshopId())) {
                BuMtrWorkshopGroup group = idGroupMap.get(faultInfo.getReportGroupId());
                if (null != group) {
                    if (null != needUpdate && needUpdate) {
                        faultInfo.setWorkshopId(group.getWorkshopId())
                                .setCompanyId(group.getCompanyId());
                        buFaultInfoMapper.updateById(faultInfo);
                        updateCount++;
                    } else {
                        canUpdateCount++;
                    }
                }
            }
        }

        if (null != needUpdate && needUpdate) {
            return String.format("车间id为空的故障有%s条，更新了%s条。", faultInfoList.size(), updateCount);
        } else {
            return String.format("车间id为空的故障有%s条，%s条可更新。", faultInfoList.size(), canUpdateCount);
        }
    }

    private List<CustomerCostDetailSystemWorkstation> getCostDetailSystemWorkstationsFormExcelData(MultipartFile excelFile,
                                                                                                   Map<String, String> systemShortNameIdMap,
                                                                                                   Map<String, String> workstationNoGroupIdMap,
                                                                                                   Map<String, String> workstationNoIdMap) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(excelFile, null, Arrays.asList(".xls", ".xlsx"));

        // 获取表单数据
        if (excelFile.isEmpty()) {
            throw new JeecgBootException("文件为空");
        }
        String filename = excelFile.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new JeecgBootException("文件名为空");
        }
        InputStream inputStream = excelFile.getInputStream();
        Workbook inWorkbook;
        String extName = filename.substring(filename.lastIndexOf(".") + 1);
        if (StringUtils.equals("xls", extName)) {
            inWorkbook = new HSSFWorkbook(inputStream);
        } else if (StringUtils.equals("xlsx", extName)) {
            inWorkbook = new XSSFWorkbook(inputStream);
        } else {
            throw new JeecgBootException("错误的excel文件");
        }
        inputStream.close();

        List<CustomerCostDetailSystemWorkstation> costSystemWorkstationList = new ArrayList<>();
        // 第一个表单备件
        Sheet sheet1 = inWorkbook.getSheetAt(0);
        if (null != sheet1) {
            int firstRowNum = sheet1.getFirstRowNum();
            int lastRowNum = sheet1.getLastRowNum();

            String lastSystemShortName = null;
            String lastWorkstationNo = null;
            String lastWorkstationName = null;
            // 操作每行数据
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet1.getRow(rowNum);
                String materialTypeCode = getCellValue(row.getCell(3));
                if (StringUtils.isBlank(materialTypeCode)) {
                    continue;
                }

                String systemShortName = getCellValue(row.getCell(0));
                if ("车上电气".equals(systemShortName)) {
                    // 特殊转换
                    systemShortName = "整车电气";
                }
                String workstationNo = getCellValue(row.getCell(1));
                if (StringUtils.isNotBlank(workstationNo)) {
                    workstationNo = workstationNo.replaceAll("\\.0", "");
                }
                String workstationName = getCellValue(row.getCell(2));
                String typeString = getCellValue(row.getCell(7));
                Integer useCategory = getUseCategory(typeString);
                String amount = getCellValue(row.getCell(8));
                String price = getCellValue(row.getCell(9));
                BigDecimal priceBigDecimal = BigDecimal.valueOf(Double.parseDouble(price));

                if (StringUtils.isNotBlank(systemShortName)) {
                    lastSystemShortName = systemShortName;
                }
                if (StringUtils.isNotBlank(workstationNo)) {
                    lastWorkstationNo = workstationNo;
                }
                if (StringUtils.isNotBlank(workstationName)) {
                    lastWorkstationName = workstationName;
                }

                // 系统
                String systemId = systemShortNameIdMap.get(lastSystemShortName);
                // 工位班组
                String groupId = workstationNoGroupIdMap.get(lastWorkstationNo);
                // 工位id
                String workstationId = workstationNoIdMap.get(lastWorkstationNo);

                CustomerCostDetailSystemWorkstation costSystemWorkstation = new CustomerCostDetailSystemWorkstation()
                        .setSystemShortName(lastSystemShortName)
                        .setWorkstationNo(lastWorkstationNo)
                        .setWorkstationName(lastWorkstationName)
                        .setMaterialTypeCode(materialTypeCode)
                        .setMaterialTypePrice(priceBigDecimal)
                        .setAmount(BigDecimal.valueOf(Double.parseDouble(amount)))
                        .setUseCategory(useCategory)
                        .setSystemId(systemId)
                        .setGroupId(groupId)
                        .setWorkstationId(workstationId);
                costSystemWorkstationList.add(costSystemWorkstation);
            }
        }
        // 第二个表单耗材
        Sheet sheet2 = inWorkbook.getSheetAt(1);
        if (null != sheet2) {
            int firstRowNum = sheet2.getFirstRowNum();
            int lastRowNum = sheet2.getLastRowNum();

            String lastSystemShortName = null;
            String lastWorkstationNo = null;
            String lastWorkstationName = null;
            // 操作每行数据
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet2.getRow(rowNum);
                String materialTypeCode = getCellValue(row.getCell(3));
                if (StringUtils.isBlank(materialTypeCode)) {
                    continue;
                }

                String systemShortName = getCellValue(row.getCell(0));
                if ("车上电气".equals(systemShortName)) {
                    // 特殊转换
                    systemShortName = "整车电气";
                }
                String workstationNo = getCellValue(row.getCell(1));
                if (StringUtils.isNotBlank(workstationNo)) {
                    workstationNo = workstationNo.replaceAll("\\.0", "");
                }
                String workstationName = getCellValue(row.getCell(2));

                String typeString = getCellValue(row.getCell(6));
                Integer useCategory = getUseCategory(typeString);
                String amount = getCellValue(row.getCell(7));
                String price = getCellValue(row.getCell(8));
                BigDecimal priceBigDecimal = BigDecimal.valueOf(Double.parseDouble(price));

                if (StringUtils.isNotBlank(systemShortName)) {
                    lastSystemShortName = systemShortName;
                }
                if (StringUtils.isNotBlank(workstationNo)) {
                    lastWorkstationNo = workstationNo;
                }
                if (StringUtils.isNotBlank(workstationName)) {
                    lastWorkstationName = workstationName;
                }

                // 系统
                String systemId = systemShortNameIdMap.get(lastSystemShortName);
                // 工位班组
                String groupId = workstationNoGroupIdMap.get(lastWorkstationNo);
                // 工位id
                String workstationId = workstationNoIdMap.get(lastWorkstationNo);

                CustomerCostDetailSystemWorkstation costSystemWorkstation = new CustomerCostDetailSystemWorkstation()
                        .setSystemShortName(lastSystemShortName)
                        .setWorkstationNo(lastWorkstationNo)
                        .setWorkstationName(lastWorkstationName)
                        .setMaterialTypeCode(materialTypeCode)
                        .setMaterialTypePrice(priceBigDecimal)
                        .setAmount(BigDecimal.valueOf(Double.parseDouble(amount)))
                        .setUseCategory(useCategory)
                        .setSystemId(systemId)
                        .setGroupId(groupId)
                        .setWorkstationId(workstationId);
                costSystemWorkstationList.add(costSystemWorkstation);
            }
        }

        return costSystemWorkstationList;
    }

    private HSSFWorkbook getExcelOfGroupLack(List<ActConsumeGroupLack> groupLackList) {
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String[] excelHeader = {
                "班组",
                "工单号",
                "工单名称",
                "工单状态",
                "物资",
                "类型",
                "数量",
                "价格（大概）"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
        HSSFRow sheetRow = sheet.createRow(0);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 单元格格式，水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell.setCellValue(header);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        AtomicInteger index = new AtomicInteger(1);
        for (ActConsumeGroupLack groupLack : groupLackList) {
            String groupId = groupLack.getGroupId();
            List<ActConsumeOrderLack> orderLackList = groupLack.getOrderLackList();
            if (CollectionUtils.isNotEmpty(orderLackList)) {
                for (ActConsumeOrderLack orderLack : orderLackList) {
                    String orderCode = orderLack.getOrderCode();
                    String orderName = orderLack.getOrderName();
                    Integer orderStatus = orderLack.getOrderStatus();
                    List<ActConsumeMaterialLack> materialLackList = orderLack.getMaterialLackList();
                    if (CollectionUtils.isNotEmpty(materialLackList)) {
                        for (ActConsumeMaterialLack materialLack : materialLackList) {
                            HSSFRow row = sheet.createRow(index.getAndIncrement());
                            row.createCell(0).setCellValue(groupId);
                            row.createCell(1).setCellValue(orderCode);
                            row.createCell(2).setCellValue(orderName);
                            row.createCell(3).setCellValue(orderStatus);
                            row.createCell(4).setCellValue(materialLack.getMaterialTypeId());
                            row.createCell(5).setCellValue(materialLack.getType());
                            row.createCell(6).setCellValue(materialLack.getLack().toPlainString());
                            row.createCell(7).setCellValue(materialLack.getPrice().toPlainString());
                        }
                    }
                }
            }
        }

        // 设置自动列宽
        //  ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
        ExcelUtil.setColumnWidth(sheet, excelHeaderList.size());

        return workbook;
    }

    private HSSFWorkbook getExcelOfCorrectSql(List<MaterialConsumeCorrect> correctList) {
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String[] excelHeader = {
                "序号",
                "sql语句"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
        HSSFRow sheetRow = sheet.createRow(0);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 单元格格式，水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell.setCellValue(header);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        AtomicInteger rowIndex = new AtomicInteger(1);
        for (MaterialConsumeCorrect correct : correctList) {
            HSSFRow row = sheet.createRow(rowIndex.getAndIncrement());
            row.createCell(0).setCellValue(correct.getIndex());
            row.createCell(1).setCellValue(correct.getCorrectSql());
        }

        // 设置自动列宽
        //  ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
        ExcelUtil.setColumnWidth(sheet, excelHeaderList.size());

        return workbook;
    }

    private HSSFWorkbook getExcelOfCostDetailDiff(List<CostDetailDiff> diffList) {
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String[] excelHeader = {
                "物料编码",
                "物料名称",
                "物料规格",
                "客户提供总数量",
                "客户提供物料类型",
                "程序中总数量",
                "程序中物料类型",
                "客户消耗信息",
                "程序中消耗信息",
                "数量是否一致",
                "系统工位是否一致",
                "消耗类型是否一致",
                "班组",
                "工单物料ids"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
        HSSFRow sheetRow = sheet.createRow(0);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 单元格格式，水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell.setCellValue(header);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        AtomicInteger index = new AtomicInteger(1);
        for (CostDetailDiff diff : diffList) {
            HSSFRow row = sheet.createRow(index.getAndIncrement());
            row.createCell(0).setCellValue(diff.getMaterialTypeCode());
            row.createCell(1).setCellValue(diff.getMaterialTypeName());
            row.createCell(2).setCellValue(diff.getMaterialTypeSpec());
            row.createCell(3).setCellValue(diff.getCustomerAmountSum().stripTrailingZeros().toPlainString());
            row.createCell(4).setCellValue(diff.getCustomerType());
            row.createCell(5).setCellValue(diff.getProgramAmountSum().stripTrailingZeros().toPlainString());
            row.createCell(6).setCellValue(diff.getProgramType());
            row.createCell(7).setCellValue(diff.getCustomerInfo());
            row.createCell(8).setCellValue(diff.getProgramInfo());
            row.createCell(9).setCellValue(diff.getAmountSumDiff() ? "否" : "是");
            row.createCell(10).setCellValue(diff.getSystemWorkstationDiff() ? "否" : "是");
            row.createCell(11).setCellValue(diff.getUseCategoryDiff() ? "否" : "是");
            row.createCell(12).setCellValue(diff.getGroupIds());
            row.createCell(13).setCellValue(diff.getOrderMaterialIds());
        }

        // 设置自动列宽
        //  ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
        ExcelUtil.setColumnWidth(sheet, excelHeaderList.size());

        return workbook;
    }

    private String getUseCategoryString(Integer useCategory) {
        if (null == useCategory) {
            return null;
        }
        if (1 == useCategory) {
            return "必换件";
        }
        if (2 == useCategory) {
            return "故障件";
        }
        if (3 == useCategory) {
            return "耗材";
        }
        return null;
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

    private String getCellValue(Cell cell) {
        String value = "";
        if (null == cell) {
            return value;
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
                break;
        }

        return value.trim();
    }

    private boolean whetherApplyDetailAllReceived(List<BuMaterialApplyDetail> detailList) {
        for (BuMaterialApplyDetail applyDetail : detailList) {
            if (null == applyDetail.getStatus() || 0 == applyDetail.getStatus() || 1 == applyDetail.getStatus()) {
                return false;
            }
        }
        return true;
    }

    private void updateGroupStockByIdSet(List<BuMaterialGroupStock> groupStockList,
                                         Set<String> needAddGroupStockIdSet,
                                         Set<String> needUpdateGroupStockIdSet,
                                         BuWorkOrder order,
                                         Date now,
                                         String userId) {
        List<BuMaterialStockChange> changeList = new ArrayList<>();

        // 新增的
        List<BuMaterialGroupStock> needAddGroupStockList = groupStockList.stream()
                .filter(groupStock -> needAddGroupStockIdSet.contains(groupStock.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needAddGroupStockList)) {
            List<List<BuMaterialGroupStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddGroupStockList);
            for (List<BuMaterialGroupStock> batchSub : batchSubList) {
                buMaterialGroupStockMapper.insertList(batchSub);
            }

            for (BuMaterialGroupStock groupStock : needAddGroupStockList) {
                boolean noteInRemark = groupStock.getRelativeAssignDetailIdSet().size() > 1;
                String assignDetailIds = String.join(",", groupStock.getRelativeAssignDetailIdSet());

                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(4)
                        .setStockId(groupStock.getId())
                        .setWarehouseId(groupStock.getWarehouseId())
                        .setMaterialTypeId(groupStock.getMaterialTypeId())
                        .setTradeNo(groupStock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason("确认领料时，新增班组库存量")
                        .setChangeType(1)
                        .setOldValue(0D)
                        .setNewValue(groupStock.getAmount().doubleValue())
                        .setTrainNo(order.getTrainNo())
                        .setOrderCode(order.getOrderCode())
                        .setAssignDetailId(null)
                        .setReturnedDetailId(null)
                        .setOrderMaterialActId(null)
                        .setOperationUser(userId)
                        .setRemark(null);
                if (noteInRemark) {
                    change.setAssignDetailId("见备忘")
                            .setRemark("新增班组库存量，assignDetailIds=" + assignDetailIds);
                } else {
                    change.setAssignDetailId(assignDetailIds)
                            .setRemark("新增班组库存量");
                }
                changeList.add(change);
            }
        }
        // 更新的
        List<BuMaterialGroupStock> needUpdateGroupStockList = groupStockList.stream()
                .filter(groupStock -> needUpdateGroupStockIdSet.contains(groupStock.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needUpdateGroupStockList)) {
            List<List<BuMaterialGroupStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateGroupStockList);
            for (List<BuMaterialGroupStock> batchSub : batchSubList) {
                buMaterialGroupStockMapper.updateList(batchSub);
            }

            for (BuMaterialGroupStock groupStock : needUpdateGroupStockList) {
                boolean noteInRemark = groupStock.getRelativeAssignDetailIdSet().size() > 1;
                String assignDetailIds = String.join(",", groupStock.getRelativeAssignDetailIdSet());

                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(4)
                        .setStockId(groupStock.getId())
                        .setWarehouseId(groupStock.getWarehouseId())
                        .setMaterialTypeId(groupStock.getMaterialTypeId())
                        .setTradeNo(groupStock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason("确认领料时，更新班组库存量")
                        .setChangeType(2)
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
                    change.setAssignDetailId("见备忘")
                            .setRemark("更新班组库存量，assignDetailIds=" + assignDetailIds);
                } else {
                    change.setAssignDetailId(assignDetailIds)
                            .setRemark("更新班组库存量");
                }
                changeList.add(change);
            }
        }

        // 保存库存变动记录
        if (CollectionUtils.isNotEmpty(changeList)) {
            buMaterialStockChangeService.addChangeList(changeList);
        }
    }

    private void addMaximoTransDataOrderReplace(List<String> orderIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (String orderId : orderIdList) {
            BuWorkOrder orderForTransData = buWorkOrderMapper.selectOrderById(orderId);

            // 设置工单资产设备编码
            List<String> trainAssetCodeList = buWorkOrderTaskMapper.selectTrainAssetCodeByOrderId(orderId);
            if (CollectionUtils.isNotEmpty(trainAssetCodeList)) {
                orderForTransData.setAssetCode(trainAssetCodeList.get(0));
            }

            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(7)
                    .setObjId(orderId)
                    .setObjJson(JSON.toJSONString(orderForTransData))
                    .setCreateTime(now)
                    .setSuccessStatus(0)
                    .setHandleStatus(0)
                    .setMessage(null);
            dataList.add(maximoTransData);
        }

        buMaximoTransDataService.addTransDataList(dataList);
    }

    private void checkStockAmountEnough(Set<String> materialTypeIdSet,
                                        Map<String, CacheWarehouseBO> warehouseBOMap,
                                        Map<String, CacheWarehouseBO> wbsWarehouseBOMap,
                                        Map<String, BuMaterialType> idMaterialTypeMap,
                                        List<BuMaterialAssignDetail> assignDetailList) {
        // 查询物资库存
        List<BuMaterialStock> stockList = new ArrayList<>();
        List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(materialTypeIdSet));
        for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
            List<BuMaterialStock> subStockList = buMaterialStockMapper.selectListByMaterialTypeIdList(materialTypeIdBatchSub);
            stockList.addAll(subStockList);
        }

        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            if (null == assignDetail.getAmount() || BigDecimal.ZERO.compareTo(BigDecimal.valueOf(assignDetail.getAmount())) >= 0) {
                continue;
            }

            String materialTypeId = assignDetail.getMaterialTypeId();
            String locationWbs = assignDetail.getLocationWbs();
            String locationName = assignDetail.getLocationName();
            String tradeNo = assignDetail.getTradeNo();

            String realStockWarehouseWbs = locationWbs;
            CacheWarehouseBO warehouseBO = wbsWarehouseBOMap.get(locationWbs);
            if (null != warehouseBO) {
                Integer locationLevel = warehouseBO.getWhLevel();
                if (null != locationLevel && locationLevel >= 5 && StringUtils.isNotBlank(tradeNo)) {
                    // 4级库位以上且有批次，对应到3级库位：因为4级库位没有批次，这个批次号是从上级3级库位选择的
                    String level3WarehouseId = getLevel3WarehouseId(warehouseBO.getId(), warehouseBOMap);
                    if (StringUtils.isBlank(level3WarehouseId)) {
                        throw new JeecgBootException("仓库[" + locationName + "]物资[" + materialTypeId + "]批次[" + (StringUtils.isBlank(tradeNo) ? "" : tradeNo) + "]" + "对应的3级库位不存在，请确认");
                    } else {
                        CacheWarehouseBO level3Warehouse = warehouseBOMap.get(level3WarehouseId);
                        realStockWarehouseWbs = level3Warehouse.getWbs();
                    }
                }
            }

            // 查找匹配对应当前库存
            List<BuMaterialStock> matchStockList = new ArrayList<>();
            for (BuMaterialStock stock : stockList) {
                boolean sameMaterial = materialTypeId.equals(stock.getMaterialTypeId());
                boolean sameWarehouse = realStockWarehouseWbs.equals(stock.getWarehouseWbs());
                boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(stock.getTradeNo()) : tradeNo.equals(stock.getTradeNo());

                if (sameMaterial && sameWarehouse && sameTradeNo) {
                    matchStockList.add(stock);
                }
            }
            BigDecimal stockAmount = matchStockList.stream()
                    .map(BuMaterialStock::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (BigDecimal.valueOf(assignDetail.getAmount()).compareTo(stockAmount) > 0) {
                BuMaterialType materialType = idMaterialTypeMap.get(materialTypeId);
                String lackMaterial = materialType.getName() + "(" + materialType.getCode() + ")";
                throw new JeecgBootException("仓库[" + locationName + "]物资[" + lackMaterial + "]批次[" + (StringUtils.isBlank(tradeNo) ? "" : tradeNo) + "]" + "现有库存量" + stockAmount + "，不足" + assignDetail.getAmount());
            }
        }
    }

    private void addMaximoTransDataAssignDetail(List<String> assignDetailIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(assignDetailIdList)) {
            return;
        }

        List<org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListForMaximoByIdList(assignDetailIdList);

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail assignDetail : assignDetailList) {
            Double amount = assignDetail.getAmount();
            if (null == amount || amount <= 0D) {
                continue;
            }

            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(3)
                    .setObjId(assignDetail.getId())
                    .setObjJson(JSON.toJSONString(assignDetail))
                    .setCreateTime(now)
                    .setSuccessStatus(0)
                    .setHandleStatus(0)
                    .setMessage(null);
            dataList.add(maximoTransData);
        }

        buMaximoTransDataService.addTransDataList(dataList);
    }

    private void updateApplyStatus(List<String> applyIdList,
                                   String userId,
                                   String departId,
                                   Date confirmTime) {
        if (CollectionUtils.isEmpty(applyIdList)) {
            return;
        }

        List<BuMaterialApply> applyList = buMaterialApplyMapper.selectListByApplyIdList(applyIdList);
        for (BuMaterialApply apply : applyList) {
            List<BuMaterialApplyDetail> detailList = apply.getDetailList();

            apply.setReadyStatus(1)
                    .setStatus(2)
                    .setReceiveGroupId(departId)
                    .setReceiveUserId(userId)
                    .setReceiveTime(confirmTime);
            buMaterialApplyMapper.updateById(apply);
        }
    }

    private void setDetailEbsInfo(BuMaterialAssignDetail assignDetail, Map<String, CacheWarehouseBO> codeWarehouseBOMap) {
        String locationWbs = assignDetail.getLocationWbs();
        if (StringUtils.isBlank(locationWbs)) {
            return;
        }

        // 硬编码：
        // 仓库wbs格式如：1.2.JDX01.xxxxx.xxxx...
        if (locationWbs.length() <= 4) {
            return;
        }
        // 1.2为固定的2个上级编码，去掉
        String var1 = locationWbs.substring(4);
        // 逗号分割
        String[] codes = var1.split("\\.");
        int length = codes.length;
        // 根据第一个编码，获取EBS二级库编码ebsWhCode
        if (length < 1) {
            return;
        }
        CacheWarehouseBO ebsWhWarehouse = codeWarehouseBOMap.get(codes[0]);
        if (null != ebsWhWarehouse) {
            assignDetail.setEbsWhCode(ebsWhWarehouse.getSysHouseCode());
        }
        // 根据第二个编码，获取EBS库位编码ebsWhChildCode
        if (length < 2) {
            return;
        }
        CacheWarehouseBO ebsWhChildWarehouse = codeWarehouseBOMap.get(codes[1]);
        if (null != ebsWhChildWarehouse) {
            assignDetail.setEbsWhChildCode(ebsWhChildWarehouse.getSysHouseCode());
        }
    }

    private Integer getWarehouseLevel(String warehouseWbs, List<BuMtrWarehouse> warehouseList) {
        if (StringUtils.isBlank(warehouseWbs) || CollectionUtils.isEmpty(warehouseList)) {
            return null;
        }
        List<BuMtrWarehouse> matchList = warehouseList.stream()
                .filter(warehouse -> warehouseWbs.equals(warehouse.getWbs()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchList)) {
            return null;
        } else {
            return matchList.get(0).getWhLevel();
        }
    }

    private String getWarehouseId(String warehouseWbs, List<BuMtrWarehouse> warehouseList) {
        if (StringUtils.isBlank(warehouseWbs) || CollectionUtils.isEmpty(warehouseList)) {
            return null;
        }
        List<BuMtrWarehouse> matchList = warehouseList.stream()
                .filter(warehouse -> warehouseWbs.equals(warehouse.getWbs()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchList)) {
            return null;
        } else {
            return matchList.get(0).getId();
        }
    }

    private String getWarehouseWbs(String warehouseId, List<BuMtrWarehouse> warehouseList) {
        if (StringUtils.isBlank(warehouseId) || CollectionUtils.isEmpty(warehouseList)) {
            return null;
        }
        List<BuMtrWarehouse> matchList = warehouseList.stream()
                .filter(warehouse -> warehouseId.equals(warehouse.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchList)) {
            return null;
        } else {
            return matchList.get(0).getWbs();
        }
    }

    private String getLevel3WarehouseId(String warehouseId, Map<String, CacheWarehouseBO> warehouseBOMap) {
        CacheWarehouseBO warehouse = warehouseBOMap.get(warehouseId);
        if (null == warehouse) {
            return null;
        }
        if (warehouse.getWhLevel() <= 4) {
            return warehouse.getId();
        } else {
            String parentId = warehouse.getParentId();
            return getLevel3WarehouseId(parentId, warehouseBOMap);
        }
    }

    private boolean checkApplyDetailAllReady(List<BuMaterialApplyDetail> detailList) {
        // 暂时不验证，只要提交就认为全部已发料
        return true;
//        if (CollectionUtils.isEmpty(detailList)) {
//            return true;
//        }
//
//        // 根据领用明细id查分配明细
//        Set<String> applyDetailIdSet = detailList.stream()
//                .map(BuMaterialApplyDetail::getId)
//                .collect(Collectors.toSet());
//        LambdaQueryWrapper<BuMaterialAssignDetail> assignDetailWrapper = new LambdaQueryWrapper<BuMaterialAssignDetail>()
//                .in(BuMaterialAssignDetail::getApplyDetailId, applyDetailIdSet);
//        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectList(assignDetailWrapper);
//        Set<String> assignDetailApplyDetailIdSet = assignDetailList.stream()
//                .map(BuMaterialAssignDetail::getApplyDetailId)
//                .collect(Collectors.toSet());
//
//        // 所有领用明细都已分配，表示领用明细全部备料完成
//        return applyDetailIdSet.size() == assignDetailApplyDetailIdSet.size();
    }

    private List<BuMaterialStockUse> getStockUseListByAssignDetailList(List<BuMaterialAssignDetail> assignDetailList,
                                                                       Map<String, CacheWarehouseBO> warehouseBOMap,
                                                                       BuWorkOrder order,
                                                                       Date now,
                                                                       String userId) {
        if (CollectionUtils.isEmpty(assignDetailList)) {
            return new ArrayList<>();
        }

        Map<String, CacheWarehouseBO> wbsWarehouseMap = new HashMap<>();
        for (Map.Entry<String, CacheWarehouseBO> warehouseBOEntry : warehouseBOMap.entrySet()) {
            CacheWarehouseBO warehouseBO = warehouseBOEntry.getValue();
            wbsWarehouseMap.put(warehouseBO.getWbs(), warehouseBO);
        }

        List<BuMaterialStockUse> stockUseList = new ArrayList<>();
        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            CacheWarehouseBO warehouse = wbsWarehouseMap.get(assignDetail.getLocationWbs());
            String warehouseId = warehouse.getId();

            BuMaterialStockUse stockUse = new BuMaterialStockUse()
                    .setId(UUIDGenerator.generate())
                    .setStockType(1)
                    .setStockId(null)
                    .setWarehouseId(warehouseId)
                    .setMaterialTypeId(assignDetail.getMaterialTypeId())
                    .setTradeNo(assignDetail.getTradeNo())
                    .setUseTime(now)
                    .setUseReason("初始化库存占用时分配明细占用库存量")
                    .setUseAmount(assignDetail.getAmount())
                    .setTrainNo(null == order ? null : order.getTrainNo())
                    .setOrderCode(null == order ? assignDetail.getOrderCode() : order.getOrderCode())
                    .setAssignDetailId(assignDetail.getId())
                    .setOrderMaterialActId(null)
                    .setOperationUser(userId)
                    .setRemark(null)
                    .setCompanyId(null == order ? null : order.getCompanyId())
                    .setWorkshopId(null == order ? null : order.getWorkshopId())
                    .setLineId(null == order ? null : order.getLineId());
            stockUseList.add(stockUse);

            // 如果所属仓库是4级库，同时设置3级库的占用
            if (warehouse.getWhLevel() >= 5) {
                String level3WarehouseId = getLevel3WarehouseId(warehouseId, warehouseBOMap);
                BuMaterialStockUse level3StockUse = new BuMaterialStockUse()
                        .setId(UUIDGenerator.generate())
                        .setStockType(1)
                        .setStockId(null)
                        .setWarehouseId(level3WarehouseId)
                        .setMaterialTypeId(assignDetail.getMaterialTypeId())
                        .setTradeNo(assignDetail.getTradeNo())
                        .setUseTime(now)
                        .setUseReason("初始化库存占用时分配明细占用库存量（分配4级库同时占用3级库）")
                        .setUseAmount(assignDetail.getAmount())
                        .setTrainNo(null == order ? null : order.getTrainNo())
                        .setOrderCode(null == order ? assignDetail.getOrderCode() : order.getOrderCode())
                        .setAssignDetailId(assignDetail.getId())
                        .setOrderMaterialActId(null)
                        .setOperationUser(userId)
                        .setRemark(null)
                        .setCompanyId(null == order ? null : order.getCompanyId())
                        .setWorkshopId(null == order ? null : order.getWorkshopId())
                        .setLineId(null == order ? null : order.getLineId());
                stockUseList.add(level3StockUse);
            }
        }
        return stockUseList;
    }

    private void addMaximoTransData(List<String> assignDetailIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(assignDetailIdList)) {
            return;
        }

        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListForMaximoByIdList(assignDetailIdList);

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            Double amount = assignDetail.getAmount();
            if (null == amount || amount <= 0D) {
                continue;
            }

            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(3)
                    .setObjId(assignDetail.getId())
                    .setObjJson(JSON.toJSONString(assignDetail))
                    .setCreateTime(now)
                    .setSuccessStatus(0)
                    .setHandleStatus(0)
                    .setMessage(null);
            dataList.add(maximoTransData);
        }

        buMaximoTransDataService.addTransDataList(dataList);
    }

    private void recurseAddChild(BuMtrWarehouse warehouse, List<BuMtrWarehouse> warehouseList) {
        if (null == warehouse) {
            return;
        }

        String id = warehouse.getId();
        List<BuMtrWarehouse> children = warehouseList.stream()
                .filter(detail -> StringUtils.isNotBlank(id) && id.equals(detail.getParentId()))
                .sorted(Comparator.comparing(BuMtrWarehouse::getHasChildren, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        warehouse.setChildren(children);
        if (CollectionUtils.isNotEmpty(children)) {
            for (BuMtrWarehouse child : children) {
                recurseAddChild(child, warehouseList);
            }
        }
    }

    private void setRegularWarehouseId(List<BuMtrWarehouse> warehouseList, String parentId, Map<String, List<BuMtrWarehouse>> regularWarehouseIdListMap) {
        if (null == parentId) {
            parentId = "";
        }
        for (BuMtrWarehouse warehouse : warehouseList) {
            String regularWarehouseId;
            Integer whLevel = warehouse.getWhLevel();
            if (whLevel <= 3) {
                regularWarehouseId = warehouse.getCode();
            } else {
                String name = warehouse.getName();
                regularWarehouseId = (parentId + name);
            }

            regularWarehouseId = regularWarehouseId
                    .replaceAll("-", "")
                    .replaceAll("\\.", "")
                    .replaceAll("_", "")
                    .replaceAll("/", "")
                    .replaceAll("&", "");
            if (regularWarehouseId.length() > 32) {
                regularWarehouseId = regularWarehouseId.substring(regularWarehouseId.length() - 32);
            }
            warehouse.setRegularWarehouseId(regularWarehouseId);

            List<BuMtrWarehouse> repeatList = regularWarehouseIdListMap.getOrDefault(regularWarehouseId, new ArrayList<>());
            repeatList.add(warehouse);
            regularWarehouseIdListMap.put(regularWarehouseId, repeatList);

            List<BuMtrWarehouse> children = warehouse.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                setRegularWarehouseId(children, regularWarehouseId, regularWarehouseIdListMap);
            }
        }
    }


    private Date getRealDate(Date defaultDate, String dateString, int rowNum) throws ParseException {
        if (StringUtils.isBlank(dateString)) {
            return null;
        }

        if (dateString.contains("：")) {
            dateString = dateString.replaceAll("：", ":").trim();
        }
        if (dateString.contains("::")) {
            dateString = dateString.replaceAll("::", ":").trim();
        }
        if (dateString.contains("..")) {
            dateString = dateString.replaceAll("\\.\\.", ".").trim();
        }
        if (dateString.contains(" ")) {
            dateString = dateString.replaceAll(" ", "").trim();
        }

        int addDay = 0;
        if (dateString.contains("上午")) {
            dateString = dateString.replaceAll("上午", "").trim();
        }
        if (dateString.contains("下午")) {
            dateString = dateString.replaceAll("下午", "").trim();
        }
        if (dateString.contains("次日")) {
            dateString = dateString.replaceAll("次日", "").trim();
            addDay = 1;
        }

        if (TirosUtil.isStringNumeric(dateString) && dateString.length() < 8) {
            int length = dateString.length();
            if (length == 5) {
                dateString = "201" + dateString;
            }
            if (length == 6) {
                dateString = "20" + dateString;
            }
            if (length == 7) {
                dateString = "2" + dateString;
            }
        }

        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException ex1) {
            try {
                dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                date = dateFormat.parse(dateString);
            } catch (ParseException ex2) {
                try {
                    dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    date = dateFormat.parse(dateString);
                } catch (ParseException ex3) {
                    try {
                        dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
                        date = dateFormat.parse(dateString);
                    } catch (ParseException ex4) {
                        try {
                            dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                            date = dateFormat.parse(dateString);
                        } catch (ParseException ex5) {
                            try {
                                dateFormat = new SimpleDateFormat("MM.dd");
                                date = dateFormat.parse(dateString);
                            } catch (ParseException ex6) {
                                try {
                                    dateFormat = new SimpleDateFormat("HH:mm");
                                    date = dateFormat.parse(dateString);
                                } catch (ParseException ex7) {
                                    try {
                                        dateFormat = new SimpleDateFormat("yyyy/MM.dd");
                                        date = dateFormat.parse(dateString);
                                    } catch (ParseException ex8) {
                                        throw new RuntimeException("第" + rowNum + "行数据有问题，dateString=" + dateString + "，获取到的date=" + date);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (null != date) {
            Calendar defaultCalendar = Calendar.getInstance();
            defaultCalendar.setTime(defaultDate);

            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            if (year < 100) {
                year = 2000 + year;
            }
            if (year == 1970 && month == 0 && dayOfMonth == 1) {
                year = defaultCalendar.get(Calendar.YEAR);
                month = defaultCalendar.get(Calendar.MONTH);
                dayOfMonth = defaultCalendar.get(Calendar.DAY_OF_MONTH);
            }
            if (year == 1970) {
                year = defaultCalendar.get(Calendar.YEAR);
            }

            if (year > currentYear + 1) {
                throw new RuntimeException("第" + rowNum + "行数据有问题，dateString=" + dateString + "，获取到的date=" + date);
            }

            Calendar resultCalendar = Calendar.getInstance();
            resultCalendar.set(Calendar.YEAR, year);
            resultCalendar.set(Calendar.MONTH, month);
            resultCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            resultCalendar.add(Calendar.DATE, addDay);
            return resultCalendar.getTime();
        } else {
            return defaultDate;
        }
    }

    private Date getRealTime(Date date, String happenTimeString, int rowNum) {
        if (null == date) {
            return null;
        }

        Integer hour = getRealHour(happenTimeString, rowNum);
        Integer minute = getRealMinute(happenTimeString, rowNum);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (null != hour) {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
        }
        if (null != minute) {
            calendar.set(Calendar.MINUTE, minute);
        }
        return calendar.getTime();
    }

    private static Integer getRealHour(String timeString, int rowNum) {
        String hour = null;
        try {
            if (StringUtils.isBlank(timeString)) {
                return null;
            }

            if (timeString.contains("：")) {
                timeString = timeString.replaceAll("：", ":").trim();
            }
            if (timeString.contains("::")) {
                timeString = timeString.replaceAll("::", ":").trim();
            }
            if (timeString.contains(".")) {
                timeString = timeString.replaceAll("\\.", "").trim();
            }
            if (timeString.contains(" ")) {
                timeString = timeString.replaceAll(" ", "").trim();
            }

            int addHour = 0;
            if (timeString.contains("上午")) {
                timeString = timeString.replaceAll("上午", "").trim();
            }
            if (timeString.contains("下午")) {
                timeString = timeString.replaceAll("下午", "").trim();
                addHour = 12;
            }
            if (timeString.contains("次日")) {
                timeString = timeString.replaceAll("次日", "").trim();
            }

            int index = timeString.indexOf(":");
            if (index == -1) {
                index = timeString.indexOf("：");
            }
            if (index == 1) {
                timeString = "0" + timeString;
            }

            index = timeString.indexOf(":");
            if (index != -1) {
                hour = timeString.substring(index - 2, index);
            } else {
                index = timeString.indexOf("：");
                if (index != -1) {
                    hour = timeString.substring(index - 2, index);
                }
            }

            return StringUtils.isBlank(hour) ? null : Integer.parseInt(hour) + addHour;
        } catch (NumberFormatException ex) {
            throw new RuntimeException("第" + rowNum + "行数据有问题，timeString=" + timeString + "，获取到的hour=" + hour);
        }
    }

    private static Integer getRealMinute(String timeString, int rowNum) {
        String minute = null;
        try {
            if (StringUtils.isBlank(timeString)) {
                return null;
            }

            if (timeString.contains("：")) {
                timeString = timeString.replaceAll("：", ":").trim();
            }
            if (timeString.contains("::")) {
                timeString = timeString.replaceAll("::", ":").trim();
            }
            if (timeString.contains(".")) {
                timeString = timeString.replaceAll("\\.", "").trim();
            }
            if (timeString.contains(" ")) {
                timeString = timeString.replaceAll(" ", "").trim();
            }

            if (timeString.contains("上午")) {
                timeString = timeString.replaceAll("上午", "").trim();
            }
            if (timeString.contains("下午")) {
                timeString = timeString.replaceAll("下午", "").trim();
            }
            if (timeString.contains("次日")) {
                timeString = timeString.replaceAll("次日", "").trim();
            }

            int index = timeString.indexOf(":");
            if (index == -1) {
                index = timeString.indexOf("：");
            }
            if (index == 1) {
                timeString = "0" + timeString;
            }

            index = timeString.indexOf(":");
            if (index != -1) {
                int minIndex = Math.min(index + 3, timeString.length() - 1);
                minute = timeString.substring(index + 1, minIndex);
            } else {
                index = timeString.indexOf("：");
                if (index != -1) {
                    int minIndex = Math.min(index + 3, timeString.length() - 1);
                    minute = timeString.substring(index + 1, minIndex);
                }
            }

            return StringUtils.isBlank(minute) ? null : Integer.parseInt(minute);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("第" + rowNum + "行数据有问题，timeString=" + timeString + "，获取到的minute=" + minute);
        }
    }

    private Integer getPhaseInt(String phaseString) {
        int levelInt = 1;

        if (StringUtils.isBlank(phaseString)) {
            return levelInt;
        }

        /**
         *  大修期
         *  架修期
         *  质保期
         */
        switch (phaseString) {
            case "大修期":
                levelInt = 4;
                break;
            case "架修期":
                levelInt = 1;
                break;
            case "质保期":
                levelInt = 2;
                break;
            default:
                break;
        }

        return levelInt;
    }

    private Integer getLevelInt(String levelString) {
        int levelInt = 1;

        if (StringUtils.isBlank(levelString)) {
            return levelInt;
        }

        /**
         *  A
         *  B
         *  C
         */
        switch (levelString) {
            case "A":
                levelInt = 1;
                break;
            case "B":
                levelInt = 2;
                break;
            case "C":
                levelInt = 3;
                break;
            default:
                break;
        }

        return levelInt;
    }

    private Integer getStatusInt(String status) {
        int statusInt = 2;

        if (StringUtils.isBlank(status)) {
            return statusInt;
        }

        /**
         * 跟踪
         * 未处理
         * 已处理
         */
        switch (status) {
            case "跟踪":
                statusInt = 3;
                break;
            case "未处理":
                statusInt = 0;
                break;
            case "已处理":
                statusInt = 1;
                break;
            default:
                break;
        }

        return statusInt;
    }

    private boolean checkFaultExist(BuFaultInfo fault, List<BuFaultInfo> faultList) {
        if (null == fault) {
            return true;
        }
        if (CollectionUtils.isEmpty(faultList)) {
            return false;
        }

        // 判断存在的规则：车号、发生日期、描述一样
        String trainNo = fault.getTrainNo();
        Date happenTime = fault.getHappenTime();
        String faultDesc = fault.getFaultDesc();
        for (BuFaultInfo faultInfo : faultList) {
            boolean sameTrainNo = trainNo.equals(faultInfo.getTrainNo());
            boolean sameHappenDay = DateUtils.isSameDay(happenTime, faultInfo.getHappenTime());
            boolean sameFaultDesc = faultDesc.trim().equals(faultInfo.getFaultDesc().trim());
            if (sameTrainNo && sameHappenDay && sameFaultDesc) {
                return true;
            }
        }

        return false;
    }

}
