package org.jeecg.modules.basemanage.tpplan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.tiros.cache.dict.DictCacheService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.wbs.entity.WbsConf;
import org.jeecg.common.tiros.wbs.service.WbsService;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.mapper.BuMtrLineMapper;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguDetail;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguMaterial;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTechBookDetail;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTools;
import org.jeecg.modules.basemanage.regu.mapper.BuRepairReguMaterialMapper;
import org.jeecg.modules.basemanage.regu.mapper.BuRepairReguTechBookDetailMapper;
import org.jeecg.modules.basemanage.regu.mapper.BuRepairReguToolsMapper;
import org.jeecg.modules.basemanage.tpplan.bean.*;
import org.jeecg.modules.basemanage.tpplan.bean.vo.*;
import org.jeecg.modules.basemanage.tpplan.mapper.*;
import org.jeecg.modules.basemanage.tpplan.service.BuTpRepairPlanService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructureDetail;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainStructureDetailMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 维修计划模版 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Service
public class BuTpRepairPlanServiceImpl extends ServiceImpl<BuTpRepairPlanMapper, BuTpRepairPlan> implements BuTpRepairPlanService {

    @Resource
    private BuTpRepairPlanMapper buTpRepairPlanMapper;
    @Resource
    private BuMtrLineMapper buMtrLineMapper;
    @Resource
    private BuTpRepairPlanTaskMapper buTpRepairPlanTaskMapper;
    @Resource
    private BuTpRepairPlanReguInfoMapper buTpRepairPlanReguInfoMapper;
    @Resource
    private BuTpRepairPlanWorkstationMapper buTpRepairPlanWorkstationMapper;
    @Resource
    private BuTpRepairPlanMaterialMapper buTpRepairPlanMaterialMapper;
    @Resource
    private BuTpRepairPlanToolMapper buTpRepairPlanToolMapper;
    @Resource
    private BuTpRepairPlanPersonMapper buTpRepairPlanPersonMapper;
    @Resource
    private BuTpRepairPlanMustReplaceMapper buTpRepairPlanMustReplaceMapper;
    @Resource
    private BuTpRepairPlanBookStepMapper buTpRepairPlanBookStepMapper;
    @Resource
    private BuTpRepairPlanSpeEqMapper buTpRepairPlanSpeEqMapper;
    @Resource
    private BuTpRepairPlanTaskPreMapper buTpRepairPlanTaskPreMapper;
    @Resource
    private BuTpRepairPlanTrainParkMapper buTpRepairPlanTrainParkMapper;
    @Resource
    private BuRepairReguMaterialMapper buRepairReguMaterialMapper;
    @Resource
    private BuRepairReguToolsMapper buRepairReguToolsMapper;
    @Resource
    private BuRepairReguTechBookDetailMapper buRepairReguTechBookDetailMapper;
    @Resource
    private BuTpRepairPlanFormsMapper buTpRepairPlanFormsMapper;
    @Resource
    private BuTpRepairPlanTaskEquMapper buTpRepairPlanTaskEquMapper;
    @Resource
    private BuTrainStructureDetailMapper buTrainStructureDetailMapper;
    @Resource
    private WbsService wbsService;
    @Resource
    private DictCacheService dictCacheService;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private BuTpRepairPlanTaskServiceImpl buTpRepairPlanTaskService;


    /**
     * @see BuTpRepairPlanService#pageTpPlan(BuTpRepairPlanQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuTpRepairPlan> pageTpPlan(BuTpRepairPlanQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buTpRepairPlanMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuTpRepairPlanService#saveOrUpdatePlan(BuTpRepairPlan)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdatePlan(BuTpRepairPlan tpRepairPlan) {
        BuMtrLine line = buMtrLineMapper.selectById(tpRepairPlan.getLineId());
        tpRepairPlan.setTrainTypeId(line.getTrainTypeId());

        // 检查有效的同线路、同修程的计划模板
        checkTpPlanRepeat(tpRepairPlan.getLineId(), tpRepairPlan.getRepairProgramId(), tpRepairPlan.getId());

        if (StringUtils.isBlank(tpRepairPlan.getId())) {
            tpRepairPlan.setId(UUIDGenerator.generate());
            buTpRepairPlanMapper.insert(tpRepairPlan);
        } else {
            BuTpRepairPlan dbTpPlan = buTpRepairPlanMapper.selectById(tpRepairPlan.getId());
            if (null == dbTpPlan) {
                buTpRepairPlanMapper.insert(tpRepairPlan);
            } else {
                buTpRepairPlanMapper.updateById(tpRepairPlan);
            }
        }

        return true;
    }

    /**
     * @see BuTpRepairPlanService#deleteByIds(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByIds(String ids) {
        List<String> idList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuTpRepairPlanTask> wrapper = new LambdaQueryWrapper<BuTpRepairPlanTask>()
                .in(BuTpRepairPlanTask::getPlanId, idList);
        List<BuTpRepairPlanTask> planTaskList = buTpRepairPlanTaskMapper.selectList(wrapper);
        List<String> taskIdList = planTaskList.stream()
                .map(BuTpRepairPlanTask::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(taskIdList)) {
            deleteTaskRelation(taskIdList);
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskIdList);
            for (List<String> batchSub : batchSubList) {
                buTpRepairPlanTaskMapper.deleteBatchIds(batchSub);
            }
        }

        buTpRepairPlanMapper.deleteBatchIds(idList);

        return true;
    }

    /**
     * @see BuTpRepairPlanService#validPlan(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean validPlan(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuTpRepairPlan> wrapper = new LambdaQueryWrapper<BuTpRepairPlan>()
                .in(BuTpRepairPlan::getId, idList);
        List<BuTpRepairPlan> tpPlanList = buTpRepairPlanMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(tpPlanList)) {
            for (BuTpRepairPlan tpPlan : tpPlanList) {
                // 检查有效的同线路、同修程的计划模板
                checkTpPlanRepeat(tpPlan.getLineId(), tpPlan.getRepairProgramId(), tpPlan.getId());
            }
        }
        BuTpRepairPlan plan = new BuTpRepairPlan()
                .setStatus(1);
        buTpRepairPlanMapper.update(plan, wrapper);

        return true;
    }

    /**
     * @see BuTpRepairPlanService#invalidPlan(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean invalidPlan(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuTpRepairPlan> wrapper = new LambdaQueryWrapper<BuTpRepairPlan>()
                .in(BuTpRepairPlan::getId, idList);
        BuTpRepairPlan plan = new BuTpRepairPlan()
                .setStatus(0);
        buTpRepairPlanMapper.update(plan, wrapper);

        return true;
    }

    /**
     * @see BuTpRepairPlanService#selectPlanAndTask(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuTpRepairPlanVOGantt selectPlanAndTask(String id) throws Exception {
        BuTpRepairPlan plan = buTpRepairPlanMapper.selectPlanByPlanId(id);
        if (null == plan) {
            throw new JeecgBootException("计划模板不存在");
        }

        List<BuTpRepairPlanTask> taskList = buTpRepairPlanTaskMapper.selectListByPlanId(id);
        if (CollectionUtils.isNotEmpty(taskList)) {
            // 设置工位信息
            List<BuTpRepairPlanWorkstation> workstationList = buTpRepairPlanWorkstationMapper.selectListByPlanId(id);
            Map<String, List<BuTpRepairPlanWorkstation>> taskIdWorkstationListMap = workstationList.stream()
                    .collect(Collectors.groupingBy(BuTpRepairPlanWorkstation::getTaskId));
            for (BuTpRepairPlanTask task : taskList) {
                task.setWorkstations(taskIdWorkstationListMap.getOrDefault(task.getId(), new ArrayList<>()));
            }
        }
        plan.setTasks(taskList);

        return transformToGantt(plan);
    }

    /**
     * @see BuTpRepairPlanService#selectPlanRelation(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuTpRepairPlanRelationVO selectPlanRelation(String tpPlanId) {
        BuTpRepairPlan tpPlan = buTpRepairPlanMapper.selectById(tpPlanId);
        if (null == tpPlan) {
            throw new JeecgBootException("计划模板不存在");
        }

        // 规程
        List<BuTpRepairPlanReguInfo> reguInfoList = buTpRepairPlanReguInfoMapper.selectListByPlanId(tpPlanId);
        List<BuTpRepairPlanReguInfo> groupedReguList = getGroupedReguList(reguInfoList);
        // 物料
        List<BuTpRepairPlanMaterial> materialList = buTpRepairPlanMaterialMapper.selectListByPlanId(tpPlanId);
        List<BuTpRepairPlanMaterial> groupedMaterialList = getGroupedMaterialList(materialList);
        // 工器具
        List<BuTpRepairPlanTool> toolList = buTpRepairPlanToolMapper.selectListByPlanId(tpPlanId);
        List<BuTpRepairPlanTool> groupedToolList = getGroupedToolList(toolList);
        // 表单：不查询，表单通过“关联表单”查看
        // 人员
        List<BuTpRepairPlanPerson> personList = buTpRepairPlanPersonMapper.selectListByPlanId(tpPlanId);
        // 作业指导书
        List<BuTpRepairPlanBookStep> bookStepList = buTpRepairPlanBookStepMapper.selectListByPlanId(tpPlanId);
        List<BuTpRepairPlanBookStep> groupedBookStepList = getGroupedBookStepList(bookStepList);
        // 特种设备
        List<BuTpRepairPlanSpeEq> specAssetList = buTpRepairPlanSpeEqMapper.selectListByPlanId(tpPlanId);
        List<BuTpRepairPlanSpeEq> groupedSpecAssetList = getGroupedSpecAssetList(specAssetList);
        // 目标设备
        List<BuTpRepairPlanTaskEqu> equipmentList = buTpRepairPlanTaskEquMapper.selectListByPlanId(tpPlanId);
        List<BuTpRepairPlanTaskEqu> groupedEquipmentList = getGroupedEquipmentList(equipmentList);

        return new BuTpRepairPlanRelationVO()
                .setId(tpPlanId)
                .setRepairPlanReguInfo(groupedReguList)
                .setMaterials(groupedMaterialList)
                .setTools(groupedToolList)
                .setPersons(personList)
                .setMustReplaces(new ArrayList<>())
                .setBookSteps(groupedBookStepList)
                .setSpecAssets(groupedSpecAssetList)
                .setEquipments(groupedEquipmentList);
    }

    /**
     * @see BuTpRepairPlanService#noRelevanceCount(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int noRelevanceCount(String planId, String reguId) {
        return buTpRepairPlanMapper.noRelevanceCount(planId, reguId);
    }

    /**
     * @see BuTpRepairPlanService#noRelevanceDetail(Page, String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Page<BuRepairReguDetail> noRelevanceDetail(Page<BuRepairReguDetail> page, String planId, String reguId) {
        return page.setRecords(buTpRepairPlanMapper.noRelevanceDetail(page, planId, reguId));
    }

    /**
     * @see BuTpRepairPlanService#copyPlan(BuTpRepairPlanCopyVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean copyPlan(BuTpRepairPlanCopyVO planCopyVO) throws Exception {
        String oldPlanId = planCopyVO.getId();
        String newPlanName = planCopyVO.getName();
        String newPlanId = UUIDGenerator.generate();

        // 复制计划模板
        BuTpRepairPlan dbPlan = buTpRepairPlanMapper.selectById(oldPlanId);
        if (null == dbPlan) {
            throw new JeecgBootException("要复制的计划模板不存在");
        }
        BuTpRepairPlan newPlan = new BuTpRepairPlan();
        BeanUtils.copyProperties(dbPlan, newPlan);
        newPlan.setId(newPlanId);
        newPlan.setCode(dbPlan.getCode() + "-copy");
        if (StringUtils.isNotBlank(newPlanName)) {
            newPlan.setTpName(newPlanName);
        } else {
            newPlan.setTpName(dbPlan.getTpName() + "-复制");
        }
        // 检查有效的同线路、同修程的计划模板
        checkTpPlanRepeat(newPlan.getLineId(), newPlan.getRepairProgramId(), newPlan.getId());
        buTpRepairPlanMapper.insert(newPlan);

        // 复制计划模板任务及关联信息
        LambdaQueryWrapper<BuTpRepairPlanTask> taskWrapper = new LambdaQueryWrapper<BuTpRepairPlanTask>()
                .eq(BuTpRepairPlanTask::getPlanId, oldPlanId);
        List<BuTpRepairPlanTask> oldTaskList = buTpRepairPlanTaskMapper.selectList(taskWrapper);
        if (CollectionUtils.isNotEmpty(oldTaskList)) {
            List<String> oldTaskIdList = oldTaskList.stream()
                    .map(BuTpRepairPlanTask::getId)
                    .collect(Collectors.toList());
            // 旧新id映射
            Map<String, String> oldNewIdMap = new HashMap<>();
            for (String oldTaskId : oldTaskIdList) {
                oldNewIdMap.put(oldTaskId, UUIDGenerator.generate());
            }

            // 转换计划模板任务
            List<BuTpRepairPlanTask> newTaskList = new ArrayList<>();
            for (BuTpRepairPlanTask oldTask : oldTaskList) {
                String oldId = oldTask.getId();
                String oldParentId = oldTask.getParentId();
                String newId = oldNewIdMap.get(oldId);
                String newParentId = StringUtils.isBlank(oldNewIdMap.get(oldParentId)) ? null : oldNewIdMap.get(oldParentId);

                BuTpRepairPlanTask newTask = new BuTpRepairPlanTask();
                BeanUtils.copyProperties(oldTask, newTask);
                newTask.setPlanId(newPlanId)
                        .setId(newId)
                        .setParentId(newParentId);
                newTaskList.add(newTask);
            }

            // 计划模板任务关联信息
            List<BuTpRepairPlanReguInfo> reguInfoList = new ArrayList<>();
            List<BuTpRepairPlanWorkstation> workstationList = new ArrayList<>();
            List<BuTpRepairPlanMaterial> materialList = new ArrayList<>();
            List<BuTpRepairPlanTool> toolList = new ArrayList<>();
            List<BuTpRepairPlanPerson> personList = new ArrayList<>();
            List<BuTpRepairPlanMustReplace> mustReplaceList = new ArrayList<>();
            List<BuTpRepairPlanBookStep> bookStepList = new ArrayList<>();
            List<BuTpRepairPlanSpeEq> specAssetList = new ArrayList<>();
            List<BuTpRepairPlanTaskPre> taskPreList = new ArrayList<>();
            List<BuTpRepairPlanTaskEqu> equipmentList = new ArrayList<>();
            List<List<String>> oldTaskIdBatchSubList = DatabaseBatchSubUtil.batchSubList(oldTaskIdList);
            for (List<String> oldTaskIdBatchSub : oldTaskIdBatchSubList) {
                LambdaQueryWrapper<BuTpRepairPlanReguInfo> reguInfoWrapper = new LambdaQueryWrapper<BuTpRepairPlanReguInfo>()
                        .in(BuTpRepairPlanReguInfo::getTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanReguInfo> subReguInfoList = buTpRepairPlanReguInfoMapper.selectList(reguInfoWrapper);
                reguInfoList.addAll(subReguInfoList);

                LambdaQueryWrapper<BuTpRepairPlanWorkstation> workstationWrapper = new LambdaQueryWrapper<BuTpRepairPlanWorkstation>()
                        .in(BuTpRepairPlanWorkstation::getTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanWorkstation> subWorkstationList = buTpRepairPlanWorkstationMapper.selectList(workstationWrapper);
                workstationList.addAll(subWorkstationList);

                LambdaQueryWrapper<BuTpRepairPlanMaterial> materialWrapper = new LambdaQueryWrapper<BuTpRepairPlanMaterial>()
                        .in(BuTpRepairPlanMaterial::getTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanMaterial> subMaterialList = buTpRepairPlanMaterialMapper.selectList(materialWrapper);
                materialList.addAll(subMaterialList);

                LambdaQueryWrapper<BuTpRepairPlanTool> toolWrapper = new LambdaQueryWrapper<BuTpRepairPlanTool>()
                        .in(BuTpRepairPlanTool::getTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanTool> subToolList = buTpRepairPlanToolMapper.selectList(toolWrapper);
                toolList.addAll(subToolList);

                LambdaQueryWrapper<BuTpRepairPlanPerson> personWrapper = new LambdaQueryWrapper<BuTpRepairPlanPerson>()
                        .in(BuTpRepairPlanPerson::getTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanPerson> subPersonList = buTpRepairPlanPersonMapper.selectList(personWrapper);
                personList.addAll(subPersonList);

                LambdaQueryWrapper<BuTpRepairPlanMustReplace> mustReplaceWrapper = new LambdaQueryWrapper<BuTpRepairPlanMustReplace>()
                        .in(BuTpRepairPlanMustReplace::getTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanMustReplace> subMustReplaceList = buTpRepairPlanMustReplaceMapper.selectList(mustReplaceWrapper);
                mustReplaceList.addAll(subMustReplaceList);

                LambdaQueryWrapper<BuTpRepairPlanBookStep> bookStepWrapper = new LambdaQueryWrapper<BuTpRepairPlanBookStep>()
                        .in(BuTpRepairPlanBookStep::getTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanBookStep> subBookStepList = buTpRepairPlanBookStepMapper.selectList(bookStepWrapper);
                bookStepList.addAll(subBookStepList);

                LambdaQueryWrapper<BuTpRepairPlanSpeEq> specAssetWrapper = new LambdaQueryWrapper<BuTpRepairPlanSpeEq>()
                        .in(BuTpRepairPlanSpeEq::getTpTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanSpeEq> subSpecAssetList = buTpRepairPlanSpeEqMapper.selectList(specAssetWrapper);
                specAssetList.addAll(subSpecAssetList);

                LambdaQueryWrapper<BuTpRepairPlanTaskPre> taskPreWrapper = new LambdaQueryWrapper<BuTpRepairPlanTaskPre>()
                        .in(BuTpRepairPlanTaskPre::getTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanTaskPre> subTaskPreList = buTpRepairPlanTaskPreMapper.selectList(taskPreWrapper);
                taskPreList.addAll(subTaskPreList);

                LambdaQueryWrapper<BuTpRepairPlanTaskEqu> equipmentWrapper = new LambdaQueryWrapper<BuTpRepairPlanTaskEqu>()
                        .in(BuTpRepairPlanTaskEqu::getPlanTaskId, oldTaskIdBatchSub);
                List<BuTpRepairPlanTaskEqu> subEquipmentList = buTpRepairPlanTaskEquMapper.selectList(equipmentWrapper);
                equipmentList.addAll(subEquipmentList);
            }

            // 转换计划模板任务关联信息
            List<BuTpRepairPlanReguInfo> newReguInfoList = transformReguInfo(oldNewIdMap, reguInfoList);
            List<BuTpRepairPlanWorkstation> newWorkstationList = transformWorkstation(oldNewIdMap, workstationList);
            List<BuTpRepairPlanMaterial> newMaterialList = transformMaterial(oldNewIdMap, materialList);
            List<BuTpRepairPlanTool> newToolList = transformTool(oldNewIdMap, toolList);
            List<BuTpRepairPlanPerson> newPersonList = transformPerson(oldNewIdMap, personList);
            List<BuTpRepairPlanMustReplace> newMustReplaceList = transformMustReplace(oldNewIdMap, mustReplaceList);
            List<BuTpRepairPlanBookStep> newBookStepList = transformBookStep(oldNewIdMap, bookStepList);
            List<BuTpRepairPlanSpeEq> newSpecAssetList = transformSpecAsset(oldNewIdMap, specAssetList);
            List<BuTpRepairPlanTaskPre> newTaskPreList = transformTaskPre(oldNewIdMap, taskPreList);
            List<BuTpRepairPlanTaskEqu> newEquipmentList = transformEquipment(oldNewIdMap, equipmentList);

            // 保存任务和关联信息
            if (CollectionUtils.isNotEmpty(newTaskList)) {
                List<List<BuTpRepairPlanTask>> batchSubList = DatabaseBatchSubUtil.batchSubList(newTaskList);
                for (List<BuTpRepairPlanTask> batchSub : batchSubList) {
                    buTpRepairPlanTaskMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newReguInfoList)) {
                List<List<BuTpRepairPlanReguInfo>> batchSubList = DatabaseBatchSubUtil.batchSubList(newReguInfoList);
                for (List<BuTpRepairPlanReguInfo> batchSub : batchSubList) {
                    buTpRepairPlanReguInfoMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newWorkstationList)) {
                List<List<BuTpRepairPlanWorkstation>> batchSubList = DatabaseBatchSubUtil.batchSubList(newWorkstationList);
                for (List<BuTpRepairPlanWorkstation> batchSub : batchSubList) {
                    buTpRepairPlanWorkstationMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newMaterialList)) {
                List<List<BuTpRepairPlanMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(newMaterialList);
                for (List<BuTpRepairPlanMaterial> batchSub : batchSubList) {
                    buTpRepairPlanMaterialMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newToolList)) {
                List<List<BuTpRepairPlanTool>> batchSubList = DatabaseBatchSubUtil.batchSubList(newToolList);
                for (List<BuTpRepairPlanTool> batchSub : batchSubList) {
                    buTpRepairPlanToolMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newPersonList)) {
                List<List<BuTpRepairPlanPerson>> batchSubList = DatabaseBatchSubUtil.batchSubList(newPersonList);
                for (List<BuTpRepairPlanPerson> batchSub : batchSubList) {
                    buTpRepairPlanPersonMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newMustReplaceList)) {
                List<List<BuTpRepairPlanMustReplace>> batchSubList = DatabaseBatchSubUtil.batchSubList(newMustReplaceList);
                for (List<BuTpRepairPlanMustReplace> batchSub : batchSubList) {
                    buTpRepairPlanMustReplaceMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newBookStepList)) {
                List<List<BuTpRepairPlanBookStep>> batchSubList = DatabaseBatchSubUtil.batchSubList(newBookStepList);
                for (List<BuTpRepairPlanBookStep> batchSub : batchSubList) {
                    buTpRepairPlanBookStepMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newSpecAssetList)) {
                List<List<BuTpRepairPlanSpeEq>> batchSubList = DatabaseBatchSubUtil.batchSubList(newSpecAssetList);
                for (List<BuTpRepairPlanSpeEq> batchSub : batchSubList) {
                    buTpRepairPlanSpeEqMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newTaskPreList)) {
                List<List<BuTpRepairPlanTaskPre>> batchSubList = DatabaseBatchSubUtil.batchSubList(newTaskPreList);
                for (List<BuTpRepairPlanTaskPre> batchSub : batchSubList) {
                    buTpRepairPlanTaskPreMapper.insertList(batchSub);
                }
            }
            if (CollectionUtils.isNotEmpty(newEquipmentList)) {
                // 系统id、设备类型id为空时，应根据车辆结构id设置对应的系统id、设备类型id
                setTaskEquSystemAssetTypeIdIfNull(newEquipmentList);

                List<List<BuTpRepairPlanTaskEqu>> batchSubList = DatabaseBatchSubUtil.batchSubList(newEquipmentList);
                for (List<BuTpRepairPlanTaskEqu> batchSub : batchSubList) {
                    buTpRepairPlanTaskEquMapper.insertList(batchSub);
                }
            }
        }
        // 复制关联表单
        LambdaQueryWrapper<BuTpRepairPlanForms> formWrapper = new LambdaQueryWrapper<BuTpRepairPlanForms>()
                .eq(BuTpRepairPlanForms::getTpPlanId, oldPlanId);
        List<BuTpRepairPlanForms> oldPlanFormList = buTpRepairPlanFormsMapper.selectList(formWrapper);
        if (CollectionUtils.isNotEmpty(oldPlanFormList)) {
            List<BuTpRepairPlanForms> newPlanFormList = new ArrayList<>();
            for (BuTpRepairPlanForms oldPlanForm : oldPlanFormList) {
                BuTpRepairPlanForms newPlanForm = new BuTpRepairPlanForms();
                BeanUtils.copyProperties(oldPlanForm, newPlanForm);
                newPlanForm.setId(UUIDGenerator.generate())
                        .setTpPlanId(newPlanId);
                newPlanFormList.add(newPlanForm);
            }
            List<List<BuTpRepairPlanForms>> batchSubList = DatabaseBatchSubUtil.batchSubList(newPlanFormList);
            for (List<BuTpRepairPlanForms> batchSub : batchSubList) {
                buTpRepairPlanFormsMapper.insertList(batchSub);
            }
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteTaskByIds(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuTpRepairPlanTask> wrapper = new LambdaQueryWrapper<BuTpRepairPlanTask>()
                .in(BuTpRepairPlanTask::getPlanId, idList);
        List<BuTpRepairPlanTask> planTaskList = buTpRepairPlanTaskMapper.selectList(wrapper);
        List<String> taskIdList = planTaskList.stream()
                .map(BuTpRepairPlanTask::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(taskIdList)) {
            deleteTaskRelation(taskIdList);
            buTpRepairPlanTaskMapper.deleteBatchIds(taskIdList);
        }

        // buTpRepairPlanMapper.deleteBatchIds(idList);

        // 更新wbs
        Set<String> planIdSet = planTaskList.stream()
                .map(BuTpRepairPlanTask::getPlanId)
                .collect(Collectors.toSet());
        String planIdsString = "'" + String.join("','", planIdSet) + "'";
        WbsConf wbsConf = new WbsConf("bu_tp_repair_plan_task")
                .setCode("task_no")
                .setWbs("task_wbs")
                .setFilter("plan_id in (" + planIdsString + ")");
        wbsService.updateWbs(wbsConf);

        return true;
    }

    /**
     * @see BuTpRepairPlanService#flushReguRelationToTpPlan(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean flushReguRelationToTpPlan(String tpPlanId) throws Exception {
        // 计划任务
        LambdaQueryWrapper<BuTpRepairPlanTask> taskWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(tpPlanId)) {
            taskWrapper.eq(BuTpRepairPlanTask::getPlanId, tpPlanId);
        }
        List<BuTpRepairPlanTask> taskList = buTpRepairPlanTaskMapper.selectList(taskWrapper);
        if (CollectionUtils.isEmpty(taskList)) {
            return true;
        }
        List<String> taskIdList = taskList.stream()
                .map(BuTpRepairPlanTask::getId)
                .collect(Collectors.toList());
        // 计划任务关联的规程明细
        List<BuTpRepairPlanReguInfo> reguInfoList = new ArrayList<>();
        List<List<String>> taskIdBatchSubList = DatabaseBatchSubUtil.batchSubList(taskIdList);
        for (List<String> taskIdBatchSub : taskIdBatchSubList) {
            LambdaQueryWrapper<BuTpRepairPlanReguInfo> reguWrapper = new LambdaQueryWrapper<BuTpRepairPlanReguInfo>()
                    .in(BuTpRepairPlanReguInfo::getTaskId, taskIdBatchSub);
            List<BuTpRepairPlanReguInfo> subReguInfoList = buTpRepairPlanReguInfoMapper.selectList(reguWrapper);
            reguInfoList.addAll(subReguInfoList);
        }
        List<String> reguDetailIdList = reguInfoList.stream()
                .map(BuTpRepairPlanReguInfo::getReguDetailId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(reguDetailIdList)) {
            return true;
        }
        Map<String, Set<String>> reguDetailIdTaskIdSetMap = new HashMap<>();
        for (BuTpRepairPlanReguInfo reguInfo : reguInfoList) {
            String reguDetailId = reguInfo.getReguDetailId();
            String taskId = reguInfo.getTaskId();

            Set<String> taskIdSet = reguDetailIdTaskIdSetMap.get(reguDetailId);
            if (null == taskIdSet) {
                taskIdSet = new HashSet<>();
            }
            taskIdSet.add(taskId);
            reguDetailIdTaskIdSetMap.put(reguDetailId, taskIdSet);
        }

        // 规程明细物料、工器具、作业指导书
        List<BuRepairReguMaterial> reguMaterialList = new ArrayList<>();
        List<BuRepairReguTools> reguToolList = new ArrayList<>();
        List<BuRepairReguTechBookDetail> reguTechBookDetailList = new ArrayList<>();
        List<List<String>> reguDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(reguDetailIdList);
        for (List<String> reguDetailIdBatchSub : reguDetailIdBatchSubList) {
            LambdaQueryWrapper<BuRepairReguMaterial> reguMaterialWrapper = new LambdaQueryWrapper<BuRepairReguMaterial>()
                    .in(BuRepairReguMaterial::getReguDetailId, reguDetailIdBatchSub);
            List<BuRepairReguMaterial> subReguMaterialList = buRepairReguMaterialMapper.selectList(reguMaterialWrapper);
            reguMaterialList.addAll(subReguMaterialList);

            LambdaQueryWrapper<BuRepairReguTools> reguToolWrapper = new LambdaQueryWrapper<BuRepairReguTools>()
                    .in(BuRepairReguTools::getReguDetailId, reguDetailIdBatchSub);
            List<BuRepairReguTools> subReguToolList = buRepairReguToolsMapper.selectList(reguToolWrapper);
            reguToolList.addAll(subReguToolList);

            LambdaQueryWrapper<BuRepairReguTechBookDetail> reguTechBookDetailWrapper = new LambdaQueryWrapper<BuRepairReguTechBookDetail>()
                    .in(BuRepairReguTechBookDetail::getReguDetailId, reguDetailIdBatchSub);
            List<BuRepairReguTechBookDetail> subReguTechBookDetailList = buRepairReguTechBookDetailMapper.selectList(reguTechBookDetailWrapper);
            reguTechBookDetailList.addAll(subReguTechBookDetailList);
        }

        // 计划任务物料和工器具
        List<BuTpRepairPlanMaterial> taskMaterialList = new ArrayList<>();
        List<BuTpRepairPlanTool> taskToolList = new ArrayList<>();
        List<BuTpRepairPlanBookStep> taskBookStepList = new ArrayList<>();
        for (List<String> taskIdBatchSub : taskIdBatchSubList) {
            LambdaQueryWrapper<BuTpRepairPlanMaterial> taskMaterialWrapper = new LambdaQueryWrapper<BuTpRepairPlanMaterial>()
                    .in(BuTpRepairPlanMaterial::getTaskId, taskIdBatchSub);
            List<BuTpRepairPlanMaterial> subTaskMaterialList = buTpRepairPlanMaterialMapper.selectList(taskMaterialWrapper);
            taskMaterialList.addAll(subTaskMaterialList);

            LambdaQueryWrapper<BuTpRepairPlanTool> taskToolWrapper = new LambdaQueryWrapper<BuTpRepairPlanTool>()
                    .in(BuTpRepairPlanTool::getTaskId, taskIdBatchSub);
            List<BuTpRepairPlanTool> subTaskToolList = buTpRepairPlanToolMapper.selectList(taskToolWrapper);
            taskToolList.addAll(subTaskToolList);

            LambdaQueryWrapper<BuTpRepairPlanBookStep> taskBookStepWrapper = new LambdaQueryWrapper<BuTpRepairPlanBookStep>()
                    .in(BuTpRepairPlanBookStep::getTaskId, taskIdBatchSub);
            List<BuTpRepairPlanBookStep> subTaskBookStepList = buTpRepairPlanBookStepMapper.selectList(taskBookStepWrapper);
            taskBookStepList.addAll(subTaskBookStepList);
        }

        // 比较，获取需新增的计划任务物料工器具
        List<BuTpRepairPlanMaterial> needAddTaskMaterialList = compareGetNeedAddTaskMaterial(taskMaterialList, reguMaterialList, reguDetailIdTaskIdSetMap);
        List<BuTpRepairPlanTool> needAddTaskToolList = compareGetNeedAddTaskTool(taskToolList, reguToolList, reguDetailIdTaskIdSetMap);
        List<BuTpRepairPlanBookStep> needAddTaskBookStepList = compareGetNeedAddTaskBookStep(taskBookStepList, reguTechBookDetailList, reguDetailIdTaskIdSetMap);
        if (CollectionUtils.isNotEmpty(needAddTaskMaterialList)) {
            List<List<BuTpRepairPlanMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddTaskMaterialList);
            for (List<BuTpRepairPlanMaterial> batchSub : batchSubList) {
                buTpRepairPlanMaterialMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(needAddTaskToolList)) {
            List<List<BuTpRepairPlanTool>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddTaskToolList);
            for (List<BuTpRepairPlanTool> batchSub : batchSubList) {
                buTpRepairPlanToolMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(needAddTaskBookStepList)) {
            List<List<BuTpRepairPlanBookStep>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddTaskBookStepList);
            for (List<BuTpRepairPlanBookStep> batchSub : batchSubList) {
                buTpRepairPlanBookStepMapper.insertList(batchSub);
            }
        }

        return true;
    }

    @Override
    public HSSFWorkbook exportPlanInfo(String tpPlanId) throws Exception {
        BuTpRepairPlan tpRepairPlan = buTpRepairPlanMapper.selectPlanByPlanId(tpPlanId);

        if (tpRepairPlan != null) {
            List<BuTpRepairPlanTask> taskList = buTpRepairPlanTaskMapper.selectTaskListNotIncludeDay(tpPlanId);
            //Comparator.comparingInt(this::parseKey)
            Map<String, List<BuTpRepairPlanTask>> taskMap = new TreeMap<>();
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (BuTpRepairPlanTask task : taskList) {
                    String key = task.getDayIndex() + ":" + task.getWorkGroupId();
                    List<BuTpRepairPlanTask> tempList = new ArrayList<>();
                    if (taskMap.containsKey(key)) {
                        List<BuTpRepairPlanTask> taskMapList = taskMap.get(key);
                        taskMapList.add(task);
                        taskMap.put(key, taskMapList);
                    } else {
                        tempList.add(task);
                        taskMap.put(key, tempList);
                    }
                }

                // 查询并设置任务关联信息
                List<BuTpRepairPlanReguInfo> reguInfoList = buTpRepairPlanReguInfoMapper.selectListByPlanId(tpPlanId);
//                List<BuTpRepairPlanWorkstation> workstationList = buTpRepairPlanWorkstationMapper.selectListByPlanId(tpPlanId);
                List<BuTpRepairPlanMaterial> materialList = buTpRepairPlanMaterialMapper.selectListByPlanId(tpPlanId);
                List<BuTpRepairPlanTool> toolList = buTpRepairPlanToolMapper.selectListByPlanId(tpPlanId);
//                List<BuTpRepairPlanPerson> personList = buTpRepairPlanPersonMapper.selectListByPlanId(tpPlanId);
//                List<BuTpRepairPlanMustReplace> mustReplaceList = buTpRepairPlanMustReplaceMapper.selectListByPlanId(tpPlanId);
                List<BuTpRepairPlanBookStep> bookStepList = buTpRepairPlanBookStepMapper.selectListByPlanId(tpPlanId);
//                List<BuTpRepairPlanSpeEq> specAssetList = buTpRepairPlanSpeEqMapper.selectListByPlanId(tpPlanId);
                List<BuTpRepairPlanTaskEqu> equipmentList = buTpRepairPlanTaskEquMapper.selectListByPlanId(tpPlanId);
                for (Map.Entry<String, List<BuTpRepairPlanTask>> entry : taskMap.entrySet()) {
                    List<BuTpRepairPlanTask> entryValue = entry.getValue();
                    if (CollectionUtils.isNotEmpty(entryValue)) {
                        for (BuTpRepairPlanTask task : entryValue) {
                            String taskId = task.getId();
                            List<BuTpRepairPlanReguInfo> taskReguInfoList = reguInfoList.stream()
                                    .filter(item -> taskId.equals(item.getTaskId()))
                                    .collect(Collectors.toList());
                            List<BuTpRepairPlanMaterial> taskMaterialList = materialList.stream()
                                    .filter(item -> taskId.equals(item.getTaskId()))
                                    .collect(Collectors.toList());
                            List<BuTpRepairPlanTool> taskToolList = toolList.stream()
                                    .filter(item -> taskId.equals(item.getTaskId()))
                                    .collect(Collectors.toList());
                            List<BuTpRepairPlanBookStep> taskBookStepList = bookStepList.stream()
                                    .filter(item -> taskId.equals(item.getTaskId()))
                                    .collect(Collectors.toList());
                            List<BuTpRepairPlanTaskEqu> taskEquipmentList = equipmentList.stream()
                                    .filter(item -> taskId.equals(item.getPlanTaskId()))
                                    .collect(Collectors.toList());

                            task.setRepairPlanReguInfo(taskReguInfoList)
                                    .setMaterials(taskMaterialList)
                                    .setTools(taskToolList)
                                    .setBookSteps(taskBookStepList)
                                    .setEquipments(taskEquipmentList);
                        }
                    }
                }
            }
            return getExcel(taskMap, tpRepairPlan);
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncMustReplace() throws Exception {
        List<BuTpRepairPlanMustReplace> mustReplaceList = buTpRepairPlanMustReplaceMapper.selectListAll();

        if (CollectionUtil.isNotEmpty(mustReplaceList)) {
            List<BuTpRepairPlanMaterial> saveMaterialList = new ArrayList<>();
            List<BuTpRepairPlanMaterial> materialList = buTpRepairPlanMaterialMapper.selectList(Wrappers.emptyWrapper());
            if (CollectionUtil.isNotEmpty(materialList)) {
                Map<String, List<BuTpRepairPlanMaterial>> materialMap = materialList.stream().collect(Collectors.groupingBy(item -> item.getTaskId() + item.getMaterialTypeId()));

                mustReplaceList.forEach(mustReplace -> {
                    String key = mustReplace.getTaskId() + mustReplace.getMaterialTypeId();
                    List<BuTpRepairPlanMaterial> materials = materialMap.get(key);
                    if (CollectionUtils.isEmpty(materials)) {
                        BuTpRepairPlanMaterial saveMaterial = new BuTpRepairPlanMaterial()
                                .setId(UUIDGenerator.generate())
                                .setMaterialTypeId(mustReplace.getMaterialTypeId())
                                .setAmount(mustReplace.getAmount())
                                .setTaskId(mustReplace.getTaskId())
                                .setUseCategory(1)
                                .setRemark(mustReplace.getRemark());
                        saveMaterialList.add(saveMaterial);
                    }
                });
            }


            if (CollectionUtils.isNotEmpty(saveMaterialList)) {
                List<List<BuTpRepairPlanMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(saveMaterialList);
                for (List<BuTpRepairPlanMaterial> batchSub : batchSubList) {
                    buTpRepairPlanMaterialMapper.insertList(batchSub);
                }
            }
        }
        return true;
    }

    private HSSFWorkbook getExcel(Map<String, List<BuTpRepairPlanTask>> taskMap, BuTpRepairPlan tpRepairPlan) throws IOException {
        String title = tpRepairPlan.getTpName() + "关联数据";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String[] excelHeader = {
                "天 数", "班 组", "任务名称", "物 料", "工器具", "规 程", "作业指导书", "作业对象"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nowTimeString);

        HSSFRow sheetRow = sheet.createRow(0);
        HSSFCell titleCell = sheetRow.createCell(0);
        titleCell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        // 设置标题字体样式
        HSSFFont columnTitleFont = workbook.createFont();
        columnTitleFont.setFontName("宋体");
        columnTitleFont.setFontHeightInPoints((short) 15);
        columnTitleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
        cellTitleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellTitleStyle.setFont(columnTitleFont);
        setBoardColor(cellTitleStyle);
        titleCell.setCellStyle(cellTitleStyle);


        // 设置表头字体样式
        HSSFFont columnHeadFont = workbook.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 12);
        columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);


        HSSFCellStyle cellHeadStyle = workbook.createCellStyle();
        // 单元格格式，水平居中
        cellHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellHeadStyle.setFont(columnHeadFont);
        setBoardColor(cellHeadStyle);


        HSSFFont columnFont = workbook.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 10);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(columnFont);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setWrapText(true);
        setBoardColor(cellStyle);

        HSSFCellStyle dayStyle = workbook.createCellStyle();
        dayStyle.setFont(columnFont);
        dayStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        dayStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        dayStyle.setWrapText(true);
        setBoardColor(dayStyle);


        HSSFRow row1 = sheet.createRow(1);
        for (String header : excelHeaderList) {
            HSSFCell cell = row1.createCell(excelHeaderList.indexOf(header));
            cell.setCellValue(header);
            cell.setCellStyle(cellHeadStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }
        Set<Integer> daySet = new HashSet<>();
        taskMap.keySet().forEach(item -> daySet.add(parseKey(item)));
        List<Integer> dayList = new ArrayList<>(daySet);

        AtomicInteger lastRow = new AtomicInteger();
        AtomicInteger firstRow = new AtomicInteger();
        dayList.forEach(day -> {
            firstRow.set(lastRow.get() == 0 ? day + 1 : lastRow.get() + 1);
            List<String> keyList = taskMap.keySet().stream().filter(item -> parseKey(item) == day).collect(Collectors.toList());
            HSSFRow row = sheet.createRow(firstRow.get());
            HSSFCell cell = row.createCell(0);
            cell.setCellStyle(dayStyle);
            cell.setCellValue(day);
            if (CollectionUtils.isNotEmpty(keyList)) {
                AtomicInteger taskSize = new AtomicInteger();
                AtomicInteger gbLastRow = new AtomicInteger();
                AtomicInteger gbFirstRow = new AtomicInteger();
                keyList.forEach(key -> {
                    List<BuTpRepairPlanTask> tasks = taskMap.get(key);
                    gbFirstRow.set(gbLastRow.get() == 0 ? firstRow.get() : gbLastRow.get());
                    if (tasks.size() > 1) {
                        taskSize.set(taskSize.get() + tasks.size());
                        gbLastRow.set(firstRow.get() + taskSize.get());
                        sheet.addMergedRegion(new CellRangeAddress(gbFirstRow.get(), gbLastRow.get() - 1, 1, 1));
                    } else {
                        taskSize.set(taskSize.get() + 1);
                        if (gbLastRow.get() != 0) {
                            gbLastRow.set(gbLastRow.get() + 1);
                        }
                    }

                });
                lastRow.set((firstRow.get() + taskSize.get() - 1));
                sheet.addMergedRegion(new CellRangeAddress(firstRow.get(), lastRow.get(), 0, 0));
            } else {
                lastRow.set(firstRow.get());
            }

            AtomicInteger rowNum = new AtomicInteger();
            keyList.forEach(key -> {
                List<BuTpRepairPlanTask> tasks = taskMap.get(key);
                if (CollectionUtils.isNotEmpty(tasks)) {
                    tasks.forEach(task -> {
                        HSSFRow subRow = null;
                        int index = tasks.indexOf(task);
                        if (index == 0 && keyList.indexOf(key) == 0) {
                            subRow = row;
                            rowNum.set(subRow.getRowNum());
                        } else {
                            rowNum.set(rowNum.get() + 1);
                            subRow = sheet.createRow(rowNum.get());

                        }
                        //  ExcelUtil.calcAndSetRowHeigt(subRow);
                        HSSFCell cell1 = subRow.createCell(1);
                        cell1.setCellStyle(cellStyle);
                        cell1.setCellValue(task.getWorkGroupName());

                        HSSFCell cell2 = subRow.createCell(2);
                        cell2.setCellStyle(cellStyle);
                        cell2.setCellValue(task.getTaskName());

                        //物 料
                        HSSFCell cell3 = subRow.createCell(3);
                        cell3.setCellStyle(cellStyle);
                        List<BuTpRepairPlanMaterial> materials = task.getMaterials();
                        StringBuilder materialStr = new StringBuilder();
                        if (CollectionUtils.isNotEmpty(materials)) {
                            materials.forEach(material -> {
                                materialStr.append("(")
                                        .append(material.getCode())
                                        .append(")")
                                        .append(material.getName())
                                        .append(material.getAmount())
                                        .append(material.getUnit())
                                        .append(",")
                                        .append("规格:")
                                        .append(material.getSpec())
                                        .append("\n");
                            });
                        }
                        cell3.setCellValue(new HSSFRichTextString(materialStr.toString()));

                        //工器具
                        HSSFCell cell4 = subRow.createCell(4);
                        cell4.setCellStyle(cellStyle);
                        List<BuTpRepairPlanTool> tools = task.getTools();
                        StringBuilder toolsStr = new StringBuilder();
                        if (CollectionUtils.isNotEmpty(tools)) {
                            tools.forEach(tool -> {
                                toolsStr.append("(")
                                        .append(tool.getCode())
                                        .append(")")
                                        .append(tool.getName())
                                        .append(tool.getAmount())
                                        .append(tool.getUnit())
                                        .append(",")
                                        .append("规格:")
                                        .append(tool.getSpec())
                                        .append("\n");
                            });
                        }
                        cell4.setCellValue(new HSSFRichTextString(toolsStr.toString()));

                        //规 程
                        HSSFCell cell5 = subRow.createCell(5);
                        cell5.setCellStyle(cellStyle);
                        List<BuTpRepairPlanReguInfo> reguInfos = task.getRepairPlanReguInfo();
                        StringBuilder reguInfoStr = new StringBuilder();
                        if (CollectionUtils.isNotEmpty(reguInfos)) {
                            reguInfos.forEach(reguInfo -> {
                                reguInfoStr.append(reguInfo.getTitle())
                                        .append("\n");
                            });
                        }
                        cell5.setCellValue(new HSSFRichTextString(reguInfoStr.toString()));

                        //作业指定书
                        HSSFCell cell6 = subRow.createCell(6);
                        cell6.setCellStyle(cellStyle);
                        List<BuTpRepairPlanBookStep> bookSteps = task.getBookSteps();
                        StringBuilder bookStepStr = new StringBuilder();
                        if (CollectionUtils.isNotEmpty(bookSteps)) {
                            bookSteps.forEach(bookStep -> {
                                bookStepStr.append(bookStep.getBookStepNo())
                                        .append(".")
                                        .append(bookStep.getBootStepTitle())
                                        .append("\n");
                            });
                        }
                        cell6.setCellValue(new HSSFRichTextString(bookStepStr.toString()));


                        // 目标设备
                        HSSFCell cell7 = subRow.createCell(7);
                        cell7.setCellStyle(cellStyle);
                        List<BuTpRepairPlanTaskEqu> equipments = task.getEquipments();
                        StringBuilder equipmentStr = new StringBuilder();
                        if (CollectionUtils.isNotEmpty(equipments)) {
                            equipments.forEach(equipment -> {
                                equipmentStr.append(equipment.getAssetTypeCode())
                                        .append(equipment.getAssetTypeName())
                                        .append("\n");
                            });
                        }
                        cell7.setCellValue(new HSSFRichTextString(equipmentStr.toString()));
                    });
                }
            });

        });


        // 设置自动列宽
        ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());


        //输出Excel文件
      /*  FileOutputStream output = new FileOutputStream("d:\\workbook.xls");
        workbook.write(output);
        output.flush();*/
        return workbook;
    }

    private static void setBoardColor(HSSFCellStyle cellTitleStyle) {
        cellTitleStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellTitleStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        cellTitleStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellTitleStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());

        cellTitleStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellTitleStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

        cellTitleStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellTitleStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
    }

    private int parseKey(String key) {
        return Integer.parseInt(key.substring(0, key.indexOf(":")));
    }

    private List<BuTpRepairPlanReguInfo> transformReguInfo(Map<String, String> oldNewIdMap,
                                                           List<BuTpRepairPlanReguInfo> oldReguInfoList) {
        if (CollectionUtils.isEmpty(oldReguInfoList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanReguInfo> newReguInfoList = new ArrayList<>();
        for (BuTpRepairPlanReguInfo oldReguInfo : oldReguInfoList) {
            BuTpRepairPlanReguInfo newReguInfo = new BuTpRepairPlanReguInfo();
            BeanUtils.copyProperties(oldReguInfo, newReguInfo);
            newReguInfo.setId(UUIDGenerator.generate())
                    .setTaskId(oldNewIdMap.get(oldReguInfo.getTaskId()));
            newReguInfoList.add(newReguInfo);
        }

        return newReguInfoList;
    }

    private List<BuTpRepairPlanWorkstation> transformWorkstation(Map<String, String> oldNewIdMap,
                                                                 List<BuTpRepairPlanWorkstation> oldWorkstationList) {
        if (CollectionUtils.isEmpty(oldWorkstationList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanWorkstation> newWorkstationList = new ArrayList<>();
        for (BuTpRepairPlanWorkstation oldWorkstation : oldWorkstationList) {
            BuTpRepairPlanWorkstation newWorkstation = new BuTpRepairPlanWorkstation();
            BeanUtils.copyProperties(oldWorkstation, newWorkstation);
            newWorkstation.setId(UUIDGenerator.generate())
                    .setTaskId(oldNewIdMap.get(oldWorkstation.getTaskId()));
            newWorkstationList.add(newWorkstation);
        }

        return newWorkstationList;
    }

    private List<BuTpRepairPlanMaterial> transformMaterial(Map<String, String> oldNewIdMap,
                                                           List<BuTpRepairPlanMaterial> oldMaterialList) {
        if (CollectionUtils.isEmpty(oldMaterialList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanMaterial> newMaterialList = new ArrayList<>();
        for (BuTpRepairPlanMaterial oldMaterial : oldMaterialList) {
            BuTpRepairPlanMaterial newMaterial = new BuTpRepairPlanMaterial();
            BeanUtils.copyProperties(oldMaterial, newMaterial);
            newMaterial.setId(UUIDGenerator.generate())
                    .setTaskId(oldNewIdMap.get(oldMaterial.getTaskId()));

            if (null == newMaterial.getAmount()) {
                newMaterial.setAmount(0D);
            }
            if (null == newMaterial.getUseCategory()) {
                newMaterial.setUseCategory(-1);
            }

            newMaterialList.add(newMaterial);
        }

        return newMaterialList;
    }

    private List<BuTpRepairPlanTool> transformTool(Map<String, String> oldNewIdMap,
                                                   List<BuTpRepairPlanTool> oldToolList) {
        if (CollectionUtils.isEmpty(oldToolList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanTool> newToolList = new ArrayList<>();
        for (BuTpRepairPlanTool oldTool : oldToolList) {
            BuTpRepairPlanTool newTool = new BuTpRepairPlanTool();
            BeanUtils.copyProperties(oldTool, newTool);
            newTool.setId(UUIDGenerator.generate())
                    .setTaskId(oldNewIdMap.get(oldTool.getTaskId()));
            if (null == newTool.getAmount()) {
                newTool.setAmount(0D);
            }
            newToolList.add(newTool);
        }

        return newToolList;
    }

    private List<BuTpRepairPlanPerson> transformPerson(Map<String, String> oldNewIdMap,
                                                       List<BuTpRepairPlanPerson> oldPersonList) {
        if (CollectionUtils.isEmpty(oldPersonList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanPerson> newPersonList = new ArrayList<>();
        for (BuTpRepairPlanPerson oldPerson : oldPersonList) {
            BuTpRepairPlanPerson newPerson = new BuTpRepairPlanPerson();
            BeanUtils.copyProperties(oldPerson, newPerson);
            newPerson.setId(UUIDGenerator.generate())
                    .setTaskId(oldNewIdMap.get(oldPerson.getTaskId()));

            if (null == newPerson.getAmount()) {
                newPerson.setAmount(0);
            }

            newPersonList.add(newPerson);
        }

        return newPersonList;
    }

    private List<BuTpRepairPlanMustReplace> transformMustReplace(Map<String, String> oldNewIdMap,
                                                                 List<BuTpRepairPlanMustReplace> oldMustReplaceList) {
        if (CollectionUtils.isEmpty(oldMustReplaceList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanMustReplace> newMustReplaceList = new ArrayList<>();
        for (BuTpRepairPlanMustReplace oldMustReplace : oldMustReplaceList) {
            BuTpRepairPlanMustReplace newMustReplace = new BuTpRepairPlanMustReplace();
            BeanUtils.copyProperties(oldMustReplace, newMustReplace);
            newMustReplace.setId(UUIDGenerator.generate())
                    .setTaskId(oldNewIdMap.get(oldMustReplace.getTaskId()));
            newMustReplaceList.add(newMustReplace);
        }

        return newMustReplaceList;
    }

    private List<BuTpRepairPlanBookStep> transformBookStep(Map<String, String> oldNewIdMap,
                                                           List<BuTpRepairPlanBookStep> oldBookStepList) {
        if (CollectionUtils.isEmpty(oldBookStepList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanBookStep> newBookStepList = new ArrayList<>();
        for (BuTpRepairPlanBookStep oldBookStep : oldBookStepList) {
            BuTpRepairPlanBookStep newBookStep = new BuTpRepairPlanBookStep();
            BeanUtils.copyProperties(oldBookStep, newBookStep);
            newBookStep.setId(UUIDGenerator.generate())
                    .setTaskId(oldNewIdMap.get(oldBookStep.getTaskId()));
            newBookStepList.add(newBookStep);
        }

        return newBookStepList;
    }

    private List<BuTpRepairPlanSpeEq> transformSpecAsset(Map<String, String> oldNewIdMap,
                                                         List<BuTpRepairPlanSpeEq> oldSpecAssetList) {
        if (CollectionUtils.isEmpty(oldSpecAssetList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanSpeEq> newSpecAssetList = new ArrayList<>();
        for (BuTpRepairPlanSpeEq oldSpecAsset : oldSpecAssetList) {
            BuTpRepairPlanSpeEq newSpecAsset = new BuTpRepairPlanSpeEq();
            BeanUtils.copyProperties(oldSpecAsset, newSpecAsset);
            newSpecAsset.setId(UUIDGenerator.generate())
                    .setTpTaskId(oldNewIdMap.get(oldSpecAsset.getTpTaskId()));
            newSpecAssetList.add(newSpecAsset);
        }

        return newSpecAssetList;
    }

    private List<BuTpRepairPlanTaskPre> transformTaskPre(Map<String, String> oldNewIdMap,
                                                         List<BuTpRepairPlanTaskPre> oldTaskPreList) {
        if (CollectionUtils.isEmpty(oldTaskPreList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanTaskPre> newTaskPreList = new ArrayList<>();
        for (BuTpRepairPlanTaskPre oldTaskPre : oldTaskPreList) {
            BuTpRepairPlanTaskPre newTaskPre = new BuTpRepairPlanTaskPre();
            BeanUtils.copyProperties(oldTaskPre, newTaskPre);
            newTaskPre.setId(UUIDGenerator.generate())
                    .setTaskId(oldNewIdMap.get(oldTaskPre.getTaskId()));
            newTaskPreList.add(newTaskPre);
        }

        return newTaskPreList;
    }

    private List<BuTpRepairPlanTaskEqu> transformEquipment(Map<String, String> oldNewIdMap,
                                                           List<BuTpRepairPlanTaskEqu> oldTaskEquipmentList) {
        if (CollectionUtils.isEmpty(oldTaskEquipmentList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanTaskEqu> newTaskEquipmentList = new ArrayList<>();
        for (BuTpRepairPlanTaskEqu oldTaskEquipment : oldTaskEquipmentList) {
            BuTpRepairPlanTaskEqu newTaskEquipment = new BuTpRepairPlanTaskEqu();
            BeanUtils.copyProperties(oldTaskEquipment, newTaskEquipment);
            newTaskEquipment.setId(UUIDGenerator.generate())
                    .setPlanTaskId(oldNewIdMap.get(oldTaskEquipment.getPlanTaskId()));
            newTaskEquipmentList.add(newTaskEquipment);
        }

        return newTaskEquipmentList;
    }

    private void deleteTaskRelation(List<String> taskIdList) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return;
        }

        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskIdList);
        for (List<String> batchSub : batchSubList) {
            LambdaQueryWrapper<BuTpRepairPlanReguInfo> reguInfoWrapper = new LambdaQueryWrapper<BuTpRepairPlanReguInfo>()
                    .in(BuTpRepairPlanReguInfo::getTaskId, batchSub);
            buTpRepairPlanReguInfoMapper.delete(reguInfoWrapper);
            LambdaQueryWrapper<BuTpRepairPlanWorkstation> workstationWrapper = new LambdaQueryWrapper<BuTpRepairPlanWorkstation>()
                    .in(BuTpRepairPlanWorkstation::getTaskId, batchSub);
            buTpRepairPlanWorkstationMapper.delete(workstationWrapper);
            LambdaQueryWrapper<BuTpRepairPlanMaterial> materialWrapper = new LambdaQueryWrapper<BuTpRepairPlanMaterial>()
                    .in(BuTpRepairPlanMaterial::getTaskId, batchSub);
            buTpRepairPlanMaterialMapper.delete(materialWrapper);
            LambdaQueryWrapper<BuTpRepairPlanTool> toolWrapper = new LambdaQueryWrapper<BuTpRepairPlanTool>()
                    .in(BuTpRepairPlanTool::getTaskId, batchSub);
            buTpRepairPlanToolMapper.delete(toolWrapper);
            LambdaQueryWrapper<BuTpRepairPlanPerson> personWrapper = new LambdaQueryWrapper<BuTpRepairPlanPerson>()
                    .in(BuTpRepairPlanPerson::getTaskId, batchSub);
            buTpRepairPlanPersonMapper.delete(personWrapper);
            LambdaQueryWrapper<BuTpRepairPlanMustReplace> mustReplaceWrapper = new LambdaQueryWrapper<BuTpRepairPlanMustReplace>()
                    .in(BuTpRepairPlanMustReplace::getTaskId, batchSub);
            buTpRepairPlanMustReplaceMapper.delete(mustReplaceWrapper);
            LambdaQueryWrapper<BuTpRepairPlanBookStep> bookStepWrapper = new LambdaQueryWrapper<BuTpRepairPlanBookStep>()
                    .in(BuTpRepairPlanBookStep::getTaskId, batchSub);
            buTpRepairPlanBookStepMapper.delete(bookStepWrapper);
            LambdaQueryWrapper<BuTpRepairPlanSpeEq> specAssetWrapper = new LambdaQueryWrapper<BuTpRepairPlanSpeEq>()
                    .in(BuTpRepairPlanSpeEq::getTpTaskId, batchSub);
            buTpRepairPlanSpeEqMapper.delete(specAssetWrapper);
            LambdaQueryWrapper<BuTpRepairPlanTaskEqu> equipmentWrapper = new LambdaQueryWrapper<BuTpRepairPlanTaskEqu>()
                    .in(BuTpRepairPlanTaskEqu::getPlanTaskId, batchSub);
            buTpRepairPlanTaskEquMapper.delete(equipmentWrapper);
            LambdaQueryWrapper<BuTpRepairPlanTaskPre> taskPreWrapper = new LambdaQueryWrapper<BuTpRepairPlanTaskPre>()
                    .in(BuTpRepairPlanTaskPre::getTaskId, batchSub);
            buTpRepairPlanTaskPreMapper.delete(taskPreWrapper);
            LambdaQueryWrapper<BuTpRepairPlanTrainPark> trainParkWrapper = new LambdaQueryWrapper<BuTpRepairPlanTrainPark>()
                    .in(BuTpRepairPlanTrainPark::getTaskId, batchSub);
            buTpRepairPlanTrainParkMapper.delete(trainParkWrapper);
        }
    }

    private void insertTaskRelation(BuTpRepairPlanTask task) {
        String taskId = task.getId();

        List<BuTpRepairPlanReguInfo> reguInfos = task.getRepairPlanReguInfo();
        if (CollectionUtils.isNotEmpty(reguInfos)) {
            reguInfos.forEach(reguInfo -> reguInfo.setTaskId(taskId));
            buTpRepairPlanReguInfoMapper.insertList(reguInfos);
        }

        List<BuTpRepairPlanWorkstation> workstations = task.getWorkstations();
        if (CollectionUtils.isNotEmpty(workstations)) {
            workstations.forEach(workstation -> workstation.setTaskId(taskId));
            buTpRepairPlanWorkstationMapper.insertList(workstations);
        }

        List<BuTpRepairPlanMaterial> materialList = task.getMaterials();
        if (CollectionUtils.isNotEmpty(materialList)) {
            for (BuTpRepairPlanMaterial material : materialList) {
                material.setTaskId(taskId);
                if (null == material.getAmount()) {
                    material.setAmount(0D);
                }
                if (null == material.getUseCategory()) {
                    material.setUseCategory(-1);
                }
            }
            buTpRepairPlanMaterialMapper.insertList(materialList);
        }

        List<BuTpRepairPlanTool> toolList = task.getTools();
        if (CollectionUtils.isNotEmpty(toolList)) {
            for (BuTpRepairPlanTool tool : toolList) {
                tool.setTaskId(taskId);
                if (null == tool.getAmount()) {
                    tool.setAmount(0D);
                }
            }
            buTpRepairPlanToolMapper.insertList(toolList);
        }

        List<BuTpRepairPlanPerson> persons = task.getPersons();
        if (CollectionUtils.isNotEmpty(persons)) {
            for (BuTpRepairPlanPerson person : persons) {
                person.setTaskId(taskId);
                if (null == person.getAmount()) {
                    person.setAmount(0);
                }
            }
            buTpRepairPlanPersonMapper.insertList(persons);
        }

        List<BuTpRepairPlanMustReplace> mustReplaces = task.getMustReplaces();
        if (CollectionUtils.isNotEmpty(mustReplaces)) {
            mustReplaces.forEach(mustReplace -> mustReplace.setTaskId(taskId));
            buTpRepairPlanMustReplaceMapper.insertList(mustReplaces);
        }

        List<BuTpRepairPlanBookStep> bookSteps = task.getBookSteps();
        if (CollectionUtils.isNotEmpty(bookSteps)) {
            bookSteps.forEach(bookStep -> bookStep.setTaskId(taskId));
            buTpRepairPlanBookStepMapper.insertList(bookSteps);
        }

        List<BuTpRepairPlanSpeEq> specAssets = task.getSpecAssets();
        if (CollectionUtils.isNotEmpty(specAssets)) {
            specAssets.forEach(specAsset -> specAsset.setTpTaskId(taskId));
            buTpRepairPlanSpeEqMapper.insertList(specAssets);
        }

        List<BuTpRepairPlanTaskPre> taskPres = task.getTaskPres();
        if (CollectionUtils.isNotEmpty(taskPres)) {
            taskPres.forEach(taskPre -> taskPre.setTaskId(taskId));
            buTpRepairPlanTaskPreMapper.insertList(taskPres);
        }

        BuTpRepairPlanTrainPark trainPark = new BuTpRepairPlanTrainPark()
                .setTaskId(taskId)
                .setTrackId(task.getTrackId());
        buTpRepairPlanTrainParkMapper.insert(trainPark);
    }

    private int countPointSplit(String var) {
        if (StringUtils.isBlank(var)) {
            return 0;
        }
        String replaceAll = var.replaceAll("\\.", "");
        return var.length() - replaceAll.length();
    }

    private BuTpRepairPlanVOGantt transformToGantt(BuTpRepairPlan buTpRepairPlan) {
        BuTpRepairPlanVOGantt planGantt = new BuTpRepairPlanVOGantt();
        BeanUtils.copyProperties(buTpRepairPlan, planGantt);

        List<BuTpRepairPlanTask> tasks = buTpRepairPlan.getTasks();
        List<BuTpRepairPlanTaskVOGantt> taskGantts = transformToGantt(tasks);

        Date startDate = null;
        Date finishDate = null;
        Optional<BuTpRepairPlanTaskVOGantt> startOptional = taskGantts.stream().filter(t -> t.getStart() != null).min(Comparator.comparing(BuTpRepairPlanTaskVOGantt::getStart));
        if (startOptional.isPresent()) {
            startDate = startOptional.get().getStart();
        }
        Optional<BuTpRepairPlanTaskVOGantt> finishOptional = taskGantts.stream().filter(t -> t.getFinish() != null).max(Comparator.comparing(BuTpRepairPlanTaskVOGantt::getFinish));
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
        planGantt.setName(planGantt.getTpName());
        planGantt.setStartDate(startDate);
        planGantt.setFinishDate(finishDate);
        planGantt.setTasks(taskGantts);

        setDictText(planGantt);
        return planGantt;
    }

    private void setDictText(BuTpRepairPlanVOGantt planGantt) {
        if (null == planGantt) {
            return;
        }

        Map<String, Map<String, String>> dictCodeItemMap = dictCacheService.mapAll();
        Map<String, String> validStatusValueTextMap = dictCodeItemMap.get("bu_valid_status");
        if (null != planGantt.getStatus()) {
            planGantt.setStatus_dictText(validStatusValueTextMap.get(String.valueOf(planGantt.getStatus())));
        }
        Map<String, String> stateValueTextMap = dictCodeItemMap.get("bu_state");
        if (null != planGantt.getDefaultTp()) {
            planGantt.setDefaultTp_dictText(stateValueTextMap.get(String.valueOf(planGantt.getDefaultTp())));
        }
    }

    private List<BuTpRepairPlanTaskVOGantt> transformToGantt(List<BuTpRepairPlanTask> tasks) {
        List<BuTpRepairPlanTaskVOGantt> taskGantts = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(tasks)) {
            for (BuTpRepairPlanTask task : tasks) {
                taskGantts.add(transformToGantt(task));
            }
        }

        return taskGantts;
    }

    private BuTpRepairPlanTaskVOGantt transformToGantt(BuTpRepairPlanTask task) {
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

        BuTpRepairPlanTaskVOGantt taskGantt = new BuTpRepairPlanTaskVOGantt();
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
        taskGantt.setWork(task.getWorkTime() != null ? task.getWorkTime().intValue() : null);
        taskGantt.setDuration(task.getDuration().intValue());
        taskGantt.setStart(task.getStartTime());
        taskGantt.setFinish(task.getFinishTime());
        taskGantt.setNotes(task.getRemark());
        taskGantt.setOutlineLevel(countPointSplit(task.getTaskWbs()) + 1);
        taskGantt.setDepartment(task.getWorkGroupName());

        taskGantt.setRepairPlanReguInfo(null == taskGantt.getRepairPlanReguInfo() ? new ArrayList<>() : taskGantt.getRepairPlanReguInfo());
        taskGantt.setWorkstations(null == taskGantt.getWorkstations() ? new ArrayList<>() : taskGantt.getWorkstations());
        taskGantt.setMaterials(null == taskGantt.getMaterials() ? new ArrayList<>() : taskGantt.getMaterials());
        taskGantt.setTools(null == taskGantt.getTools() ? new ArrayList<>() : taskGantt.getTools());
        taskGantt.setPersons(null == taskGantt.getPersons() ? new ArrayList<>() : taskGantt.getPersons());
        taskGantt.setMustReplaces(null == taskGantt.getMustReplaces() ? new ArrayList<>() : taskGantt.getMustReplaces());
        taskGantt.setBookSteps(null == taskGantt.getBookSteps() ? new ArrayList<>() : taskGantt.getBookSteps());
        taskGantt.setSpecAssets(null == taskGantt.getSpecAssets() ? new ArrayList<>() : taskGantt.getSpecAssets());
        taskGantt.setTaskPres(null == taskGantt.getTaskPres() ? new ArrayList<>() : taskGantt.getTaskPres());

        return taskGantt;
    }

    private BuTpRepairPlanTask transformFromGantt(BuTpRepairPlanTaskVOGantt taskVOWithGantt) {
        if (null == taskVOWithGantt) {
            return null;
        }

        BuTpRepairPlanTask task = new BuTpRepairPlanTask();

        BeanUtils.copyProperties(taskVOWithGantt, task);
        // 甘特图字段转业务表字段
        task.setId(taskVOWithGantt.getUID())
                .setParentId(taskVOWithGantt.getParentId())
                .setDuration(taskVOWithGantt.getTaskDuration());
//                .setTaskNo(taskVOWithGantt.getID())
//                .setTaskName(taskVOWithGantt.getName())l
//                .setImportant(taskVOWithGantt.getCritical())
//                .setWorkTime(taskVOWithGantt.getWork().doubleValue())
//                .setDuration(taskVOWithGantt.getDuration().doubleValue())
//                .setStartTime(taskVOWithGantt.getStart())
//                .setFinishTime(taskVOWithGantt.getFinish())
//                .setRemark(taskVOWithGantt.getNotes());

        return task;
    }

    private void setTaskNo(BuTpRepairPlanTask planTask) {
        Integer maxTaskNo = buTpRepairPlanMapper.selectMaxTaskNoByParentId(planTask.getParentId());
        if (null == maxTaskNo) {
            planTask.setTaskNo(1);
        } else {
            planTask.setTaskNo(maxTaskNo + 1);
        }
    }

    private void getAllTaskChildrenId(List<BuTpRepairPlanTask> taskList, Set<String> childrenIdSet) {
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }

        Set<String> planIdSet = taskList.stream()
                .map(BuTpRepairPlanTask::getPlanId)
                .collect(Collectors.toSet());
        LambdaQueryWrapper<BuTpRepairPlanTask> wrapper = new LambdaQueryWrapper<BuTpRepairPlanTask>()
                .in(BuTpRepairPlanTask::getPlanId, planIdSet);
        List<BuTpRepairPlanTask> allTaskList = buTpRepairPlanTaskMapper.selectList(wrapper);

        recursionAddChildrenId(taskList, allTaskList, childrenIdSet);
    }

    private void recursionAddChildrenId(List<BuTpRepairPlanTask> taskList, List<BuTpRepairPlanTask> allTaskList, Set<String> taskIdSet) {
        if (CollectionUtils.isNotEmpty(taskList)) {
            for (BuTpRepairPlanTask task : taskList) {
                String taskId = task.getId();
                taskIdSet.add(taskId);

                List<BuTpRepairPlanTask> children = allTaskList.stream()
                        .filter(taskItem -> taskId.equals(taskItem.getParentId()))
                        .collect(Collectors.toList());
                recursionAddChildrenId(children, allTaskList, taskIdSet);
            }
        }
    }

    private List<BuTpRepairPlanMaterial> compareGetNeedAddTaskMaterial(List<BuTpRepairPlanMaterial> taskMaterialList,
                                                                       List<BuRepairReguMaterial> reguMaterialList,
                                                                       Map<String, Set<String>> reguDetailIdTaskIdSetMap) {
        if (CollectionUtils.isEmpty(reguMaterialList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanMaterial> needAddTaskMaterialList = new ArrayList<>();

        Map<String, List<BuRepairReguMaterial>> reguDetailIdMaterialListMap = reguMaterialList.stream()
                .collect(Collectors.groupingBy(BuRepairReguMaterial::getReguDetailId));
        for (Map.Entry<String, List<BuRepairReguMaterial>> reguDetailIdMaterialListEntry : reguDetailIdMaterialListMap.entrySet()) {
            String reguDetailId = reguDetailIdMaterialListEntry.getKey();
            List<BuRepairReguMaterial> materialList = reguDetailIdMaterialListEntry.getValue();

            Set<String> taskIdSet = reguDetailIdTaskIdSetMap.get(reguDetailId);
            if (CollectionUtils.isEmpty(taskIdSet)) {
                continue;
            }
            if (CollectionUtils.isEmpty(materialList)) {
                continue;
            }

            for (String taskId : taskIdSet) {
                List<String> taskMaterialTypeIdList = taskMaterialList.stream()
                        .filter(taskMaterial -> taskId.equals(taskMaterial.getTaskId()))
                        .map(BuTpRepairPlanMaterial::getMaterialTypeId)
                        .collect(Collectors.toList());

                List<BuRepairReguMaterial> notExistList = materialList.stream()
                        .filter(reguMaterial -> !taskMaterialTypeIdList.contains(reguMaterial.getMaterialTypeId()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(notExistList)) {
                    continue;
                }

                for (BuRepairReguMaterial reguMaterial : notExistList) {
                    BuTpRepairPlanMaterial taskMaterial = new BuTpRepairPlanMaterial()
                            .setId(UUIDGenerator.generate())
                            .setTaskId(taskId)
                            .setMaterialTypeId(reguMaterial.getMaterialTypeId())
                            .setAmount(null == reguMaterial.getAmount() ? 0D : reguMaterial.getAmount())
                            .setUseCategory(null == reguMaterial.getUseCategory() ? -1 : reguMaterial.getUseCategory())
                            .setRemark("从规程复制");
                    needAddTaskMaterialList.add(taskMaterial);
                }
            }
        }

        return needAddTaskMaterialList;
    }

    private List<BuTpRepairPlanTool> compareGetNeedAddTaskTool(List<BuTpRepairPlanTool> taskToolList,
                                                               List<BuRepairReguTools> reguToolList,
                                                               Map<String, Set<String>> reguDetailIdTaskIdSetMap) {
        if (CollectionUtils.isEmpty(reguToolList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanTool> needAddTaskToolList = new ArrayList<>();

        Map<String, List<BuRepairReguTools>> reguDetailIdToolListMap = reguToolList.stream()
                .collect(Collectors.groupingBy(BuRepairReguTools::getReguDetailId));
        for (Map.Entry<String, List<BuRepairReguTools>> reguDetailIdToolListEntry : reguDetailIdToolListMap.entrySet()) {
            String reguDetailId = reguDetailIdToolListEntry.getKey();
            List<BuRepairReguTools> toolList = reguDetailIdToolListEntry.getValue();

            Set<String> taskIdSet = reguDetailIdTaskIdSetMap.get(reguDetailId);
            if (CollectionUtils.isEmpty(taskIdSet)) {
                continue;
            }
            if (CollectionUtils.isEmpty(toolList)) {
                continue;
            }

            for (String taskId : taskIdSet) {
                List<String> taskToolTypeIdList = taskToolList.stream()
                        .filter(taskTool -> taskId.equals(taskTool.getTaskId()))
                        .map(BuTpRepairPlanTool::getToolTypeId)
                        .collect(Collectors.toList());

                List<BuRepairReguTools> notExistList = toolList.stream()
                        .filter(reguTool -> !taskToolTypeIdList.contains(reguTool.getToolTypeId()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(notExistList)) {
                    continue;
                }

                for (BuRepairReguTools reguTool : notExistList) {
                    BuTpRepairPlanTool taskTool = new BuTpRepairPlanTool()
                            .setId(UUIDGenerator.generate())
                            .setTaskId(taskId)
                            .setToolTypeId(reguTool.getToolTypeId())
                            .setAmount(null == reguTool.getAmount() ? 0D : reguTool.getAmount())
                            .setRemark("从规程复制");
                    needAddTaskToolList.add(taskTool);
                }
            }
        }

        return needAddTaskToolList;
    }

    private List<BuTpRepairPlanBookStep> compareGetNeedAddTaskBookStep(List<BuTpRepairPlanBookStep> taskBookStepList,
                                                                       List<BuRepairReguTechBookDetail> reguTechBookDetailList,
                                                                       Map<String, Set<String>> reguDetailIdTaskIdSetMap) {
        if (CollectionUtils.isEmpty(reguTechBookDetailList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanBookStep> needAddTaskBookStepList = new ArrayList<>();

        Map<String, List<BuRepairReguTechBookDetail>> reguDetailIdTechBookDetailListMap = reguTechBookDetailList.stream()
                .collect(Collectors.groupingBy(BuRepairReguTechBookDetail::getReguDetailId));
        for (Map.Entry<String, List<BuRepairReguTechBookDetail>> reguDetailIdTechBookDetailListEntry : reguDetailIdTechBookDetailListMap.entrySet()) {
            String reguDetailId = reguDetailIdTechBookDetailListEntry.getKey();
            List<BuRepairReguTechBookDetail> techBookDetailList = reguDetailIdTechBookDetailListEntry.getValue();

            Set<String> taskIdSet = reguDetailIdTaskIdSetMap.get(reguDetailId);
            if (CollectionUtils.isEmpty(taskIdSet)) {
                continue;
            }
            if (CollectionUtils.isEmpty(techBookDetailList)) {
                continue;
            }

            for (String taskId : taskIdSet) {
                List<String> taskBookDetailIdList = taskBookStepList.stream()
                        .filter(taskBookStep -> taskId.equals(taskBookStep.getTaskId()))
                        .map(BuTpRepairPlanBookStep::getBookDetailId)
                        .collect(Collectors.toList());

                List<BuRepairReguTechBookDetail> notExistList = techBookDetailList.stream()
                        .filter(reguTechBookDetail -> !taskBookDetailIdList.contains(reguTechBookDetail.getBookDetailId()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(notExistList)) {
                    continue;
                }

                for (BuRepairReguTechBookDetail reguTechBookDetail : notExistList) {
                    BuTpRepairPlanBookStep taskBookStep = new BuTpRepairPlanBookStep()
                            .setId(UUIDGenerator.generate())
                            .setTaskId(taskId)
                            .setTechBookId(reguTechBookDetail.getTechBookId())
                            .setBookDetailId(reguTechBookDetail.getBookDetailId())
                            .setBookStepNo(reguTechBookDetail.getBookStepNo())
                            .setBootStepTitle(reguTechBookDetail.getBootStepTitle());
                    needAddTaskBookStepList.add(taskBookStep);
                }
            }
        }

        return needAddTaskBookStepList;
    }

    private void setTaskEquSystemAssetTypeIdIfNull(List<BuTpRepairPlanTaskEqu> equipments) {
        if (CollectionUtils.isEmpty(equipments)) {
            return;
        }

        // 过滤出系统id或设备类型id为空的
        List<BuTpRepairPlanTaskEqu> needUpdateList = equipments.stream()
                .filter(equipment -> StringUtils.isBlank(equipment.getSystemId()) || StringUtils.isBlank(equipment.getAssetTypeId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needUpdateList)) {
            // 查询车辆结构
            List<String> structDetailIdList = needUpdateList.stream()
                    .map(BuTpRepairPlanTaskEqu::getStructDetailId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<String, BuTrainStructureDetail> idStructureDetailMap = new HashMap<>();
            List<List<String>> structDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(structDetailIdList);
            for (List<String> structDetailIdBatchSub : structDetailIdBatchSubList) {
                List<BuTrainStructureDetail> subStructureDetailList = buTrainStructureDetailMapper.selectBatchIds(structDetailIdBatchSub);
                subStructureDetailList.forEach(structDetail -> idStructureDetailMap.put(structDetail.getId(), structDetail));
            }
            // 查询设备类型缓存
            Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = assetTypeCacheService.map(null);

            // 设置
            for (BuTpRepairPlanTaskEqu equipment : needUpdateList) {
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

    private List<BuTpRepairPlanReguInfo> getGroupedReguList(List<BuTpRepairPlanReguInfo> reguInfoList) {
        if (CollectionUtils.isEmpty(reguInfoList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanReguInfo> resultList = new ArrayList<>();

        Map<String, List<BuTpRepairPlanReguInfo>> groupedListMap = reguInfoList.stream()
                .collect(Collectors.groupingBy(item -> item.getWorkGroupId() + "," + item.getReguDetailId()));
        for (Map.Entry<String, List<BuTpRepairPlanReguInfo>> groupedListEntry : groupedListMap.entrySet()) {
            List<BuTpRepairPlanReguInfo> list = groupedListEntry.getValue();
            BuTpRepairPlanReguInfo reguInfo = list.get(0);

            BuTpRepairPlanReguInfo result = new BuTpRepairPlanReguInfo();
            BeanUtils.copyProperties(reguInfo, result);
            result.setId(null)
                    .setTaskId(null)
                    .setRemark(null);
            resultList.add(result);
        }

        resultList.sort(Comparator.comparing(BuTpRepairPlanReguInfo::getWorkGroupName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanReguInfo::getReguId, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanReguInfo::getNo, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }

    private List<BuTpRepairPlanMaterial> getGroupedMaterialList(List<BuTpRepairPlanMaterial> materialList) {
        if (CollectionUtils.isEmpty(materialList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanMaterial> resultList = new ArrayList<>();

        Map<String, List<BuTpRepairPlanMaterial>> groupedListMap = materialList.stream()
                .collect(Collectors.groupingBy(item -> item.getWorkGroupId() + "," + item.getMaterialTypeId() + "," + item.getUseCategory()));
        for (Map.Entry<String, List<BuTpRepairPlanMaterial>> groupedListEntry : groupedListMap.entrySet()) {
            List<BuTpRepairPlanMaterial> list = groupedListEntry.getValue();
            BuTpRepairPlanMaterial material = list.get(0);
            double amountSum = list.stream()
                    .mapToDouble(BuTpRepairPlanMaterial::getAmount)
                    .sum();

            BuTpRepairPlanMaterial result = new BuTpRepairPlanMaterial();
            BeanUtils.copyProperties(material, result);
            result.setId(null)
                    .setTaskId(null)
                    .setAmount(amountSum)
                    .setRemark(null);
            resultList.add(result);
        }

        resultList.sort(Comparator.comparing(BuTpRepairPlanMaterial::getWorkGroupName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanMaterial::getMaterialTypeId, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanMaterial::getUseCategory, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }

    private List<BuTpRepairPlanTool> getGroupedToolList(List<BuTpRepairPlanTool> toolList) {
        if (CollectionUtils.isEmpty(toolList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanTool> resultList = new ArrayList<>();

        Map<String, List<BuTpRepairPlanTool>> groupedListMap = toolList.stream()
                .collect(Collectors.groupingBy(item -> item.getWorkGroupId() + "," + item.getToolTypeId()));
        for (Map.Entry<String, List<BuTpRepairPlanTool>> groupedListEntry : groupedListMap.entrySet()) {
            List<BuTpRepairPlanTool> list = groupedListEntry.getValue();
            BuTpRepairPlanTool tool = list.get(0);
            double amountSum = list.stream()
                    .mapToDouble(BuTpRepairPlanTool::getAmount)
                    .sum();

            BuTpRepairPlanTool result = new BuTpRepairPlanTool();
            BeanUtils.copyProperties(tool, result);
            result.setId(null)
                    .setTaskId(null)
                    .setAmount(amountSum)
                    .setRemark(null);
            resultList.add(result);
        }

        resultList.sort(Comparator.comparing(BuTpRepairPlanTool::getWorkGroupName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanTool::getToolTypeId, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }

    private List<BuTpRepairPlanBookStep> getGroupedBookStepList(List<BuTpRepairPlanBookStep> bookStepList) {
        if (CollectionUtils.isEmpty(bookStepList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanBookStep> resultList = new ArrayList<>();

        Map<String, List<BuTpRepairPlanBookStep>> groupedListMap = bookStepList.stream()
                .collect(Collectors.groupingBy(item -> item.getWorkGroupId() + "," + item.getBookDetailId()));
        for (Map.Entry<String, List<BuTpRepairPlanBookStep>> groupedListEntry : groupedListMap.entrySet()) {
            List<BuTpRepairPlanBookStep> list = groupedListEntry.getValue();
            BuTpRepairPlanBookStep bookStep = list.get(0);

            BuTpRepairPlanBookStep result = new BuTpRepairPlanBookStep();
            BeanUtils.copyProperties(bookStep, result);
            result.setId(null)
                    .setTaskId(null);
            resultList.add(result);
        }

        resultList.sort(Comparator.comparing(BuTpRepairPlanBookStep::getWorkGroupName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanBookStep::getFileNo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanBookStep::getBookStepNo, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }

    private List<BuTpRepairPlanSpeEq> getGroupedSpecAssetList(List<BuTpRepairPlanSpeEq> specAssetList) {
        if (CollectionUtils.isEmpty(specAssetList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanSpeEq> resultList = new ArrayList<>();

        Map<String, List<BuTpRepairPlanSpeEq>> groupedListMap = specAssetList.stream()
                .collect(Collectors.groupingBy(item -> item.getWorkGroupId() + "," + item.getSpecAssetId()));
        for (Map.Entry<String, List<BuTpRepairPlanSpeEq>> groupedListEntry : groupedListMap.entrySet()) {
            List<BuTpRepairPlanSpeEq> list = groupedListEntry.getValue();
            BuTpRepairPlanSpeEq specAsset = list.get(0);

            BuTpRepairPlanSpeEq result = new BuTpRepairPlanSpeEq();
            BeanUtils.copyProperties(specAsset, result);
            result.setId(null)
                    .setTpTaskId(null)
                    .setStartTime(null)
                    .setEndTime(null)
                    .setTimeLen(null)
                    .setRemark(null);
            resultList.add(result);
        }

        resultList.sort(Comparator.comparing(BuTpRepairPlanSpeEq::getWorkGroupName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanSpeEq::getAssetCode, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }

    private List<BuTpRepairPlanTaskEqu> getGroupedEquipmentList(List<BuTpRepairPlanTaskEqu> equipmentList) {
        if (CollectionUtils.isEmpty(equipmentList)) {
            return new ArrayList<>();
        }

        List<BuTpRepairPlanTaskEqu> resultList = new ArrayList<>();

        Map<String, List<BuTpRepairPlanTaskEqu>> groupedListMap = equipmentList.stream()
                .collect(Collectors.groupingBy(item -> item.getWorkGroupId() + "," + item.getStructDetailId()));
        for (Map.Entry<String, List<BuTpRepairPlanTaskEqu>> groupedListEntry : groupedListMap.entrySet()) {
            List<BuTpRepairPlanTaskEqu> list = groupedListEntry.getValue();
            BuTpRepairPlanTaskEqu equipment = list.get(0);

            BuTpRepairPlanTaskEqu result = new BuTpRepairPlanTaskEqu();
            BeanUtils.copyProperties(equipment, result);
            result.setId(null)
                    .setPlanTaskId(null);
            resultList.add(result);
        }

        resultList.sort(Comparator.comparing(BuTpRepairPlanTaskEqu::getWorkGroupName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanTaskEqu::getSystemCode, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanTaskEqu::getAssetTypeCode, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTpRepairPlanTaskEqu::getStructDetailCode, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }

    private void checkTpPlanRepeat(String lineId, String repairProgramId, String tpPlanId) {
        LambdaQueryWrapper<BuTpRepairPlan> wrapper = new LambdaQueryWrapper<BuTpRepairPlan>()
                .eq(BuTpRepairPlan::getLineId, lineId)
                .eq(BuTpRepairPlan::getRepairProgramId, repairProgramId)
                .eq(BuTpRepairPlan::getStatus, 1);
        if (StringUtils.isNotBlank(tpPlanId)) {
            wrapper.ne(BuTpRepairPlan::getId, tpPlanId);
        }
        Integer count = buTpRepairPlanMapper.selectCount(wrapper);
        if (null != count && count > 0) {
            throw new JeecgBootException("已存在有效的同线路同修程计划模板，请先禁用相关计划模板。");
        }
    }

}
