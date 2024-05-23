package org.jeecg.modules.basemanage.tpplan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.LengthCheckUtil;
import org.jeecg.common.tiros.wbs.entity.WbsConf;
import org.jeecg.common.tiros.wbs.service.WbsService;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.tpplan.bean.*;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanTaskNoUpdateVO;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanTaskVOGantt;
import org.jeecg.modules.basemanage.tpplan.mapper.*;
import org.jeecg.modules.basemanage.tpplan.service.BuTpRepairPlanTaskService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructureDetail;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainStructureDetailMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 计划任务明细 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Slf4j
@Service
public class BuTpRepairPlanTaskServiceImpl extends ServiceImpl<BuTpRepairPlanTaskMapper, BuTpRepairPlanTask> implements BuTpRepairPlanTaskService {

    @Resource
    private BuTpRepairPlanMapper buTpRepairPlanMapper;
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
    private BuTpRepairPlanTaskEquMapper buTpRepairPlanTaskEquMapper;
    @Resource
    private BuTpRepairPlanTaskFormsMapper buTpRepairPlanTaskFormsMapper;
    @Resource
    private BuTrainStructureDetailMapper buTrainStructureDetailMapper;
    @Resource
    private WbsService wbsService;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuTpRepairPlanTaskService#selectTaskDetail(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuTpRepairPlanTaskVOGantt selectTaskDetail(String taskId) throws Exception {
        BuTpRepairPlanTask task = buTpRepairPlanTaskMapper.selectTpPlanTaskById(taskId);
        if (null == task) {
            throw new JeecgBootException("计划模板任务不存在");
        }

        setTaskRelation(task);

        return transformToGantt(task);
    }

    /**
     * @see BuTpRepairPlanTaskService#saveOrUpdatePlanTask(BuTpRepairPlanTaskVOGantt)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdatePlanTask(BuTpRepairPlanTaskVOGantt taskVOWithGantt) throws Exception {
        BuTpRepairPlanTask planTask = transformFromGantt(taskVOWithGantt);

        // 检查字段长度
        Integer safeNoticeDataLength = buTpRepairPlanTaskMapper.selectSafeNoticeDataLength();
        LengthCheckUtil.maxLength(planTask.getSafeNotice(), "安全预想", safeNoticeDataLength);

        // 如果序号为空，设置序号
        if (null == planTask.getTaskNo()) {
            setTaskNo(planTask);
        }

        // 删除任务关联信息
        deleteTaskRelation(Collections.singletonList(planTask.getId()));
        // 删除任务
        buTpRepairPlanTaskMapper.deleteById(planTask.getId());

        // 插入任务
        buTpRepairPlanTaskMapper.insert(planTask);
        // 插入任务关联信息
        insertTaskRelation(planTask);

        return true;
    }

    /**
     * @see BuTpRepairPlanTaskService#updateTaskNoAndWbs(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTaskNoAndWbs(List<BuTpRepairPlanTaskNoUpdateVO> taskNoUpdateVOS) {
        // 更新任务序号vo转计划模板任务
        List<BuTpRepairPlanTask> taskList = new ArrayList<>();
        for (BuTpRepairPlanTaskNoUpdateVO updateVO : taskNoUpdateVOS) {
            BuTpRepairPlanTask task = new BuTpRepairPlanTask()
                    .setId(updateVO.getUID())
                    .setTaskNo(updateVO.getID());
            taskList.add(task);
        }

        if (CollectionUtils.isNotEmpty(taskList)) {
            List<List<BuTpRepairPlanTask>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskList);
            for (List<BuTpRepairPlanTask> batchSub : batchSubList) {
                buTpRepairPlanTaskMapper.updateListTaskNo(batchSub);
            }

            // 更新wbs
            WbsConf wbsConf = new WbsConf("bu_tp_repair_plan_task")
                    .setCode("task_no")
                    .setWbs("task_wbs");
            Set<String> planIdSet = taskNoUpdateVOS.stream()
                    .map(BuTpRepairPlanTaskNoUpdateVO::getPlanId)
                    .collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(planIdSet)) {
                String planIdsString = "'" + String.join("','", planIdSet) + "'";
                wbsConf.setFilter("plan_id in (" + planIdsString + ")");
            }

            wbsService.updateWbs(wbsConf);
        }

        return true;
    }

    /**
     * @see BuTpRepairPlanTaskService#deleteBatchPlanTaskByIds(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchPlanTaskByIds(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        Set<String> planIdSet = new HashSet<>();

        // 查询任务下的子节点
        List<List<String>> idBatchSubList = DatabaseBatchSubUtil.batchSubList(idList);
        Set<String> taskIdSet = new HashSet<>(idList);
        for (List<String> idBatchSub : idBatchSubList) {
            List<BuTpRepairPlanTask> planTaskList = buTpRepairPlanTaskMapper.selectBatchIds(idBatchSub);
            for (BuTpRepairPlanTask planTask : planTaskList) {
                LambdaQueryWrapper<BuTpRepairPlanTask> planTaskChildrenWrapper = new LambdaQueryWrapper<BuTpRepairPlanTask>()
                        .eq(BuTpRepairPlanTask::getPlanId, planTask.getPlanId())
                        .likeRight(BuTpRepairPlanTask::getTaskWbs, planTask.getTaskWbs())
                        .select(BuTpRepairPlanTask::getId, BuTpRepairPlanTask::getPlanId);
                List<BuTpRepairPlanTask> children = buTpRepairPlanTaskMapper.selectList(planTaskChildrenWrapper);
                for (BuTpRepairPlanTask child : children) {
                    taskIdSet.add(child.getId());
                    planIdSet.add(child.getPlanId());
                }
            }
        }
        taskIdSet.remove(null);
        planIdSet.remove(null);

        // 删除任务及关联信息
        List<String> taskIdList = new ArrayList<>(taskIdSet);
        deleteTaskRelation(taskIdList);
        List<List<String>> taskIdBatchSubList = DatabaseBatchSubUtil.batchSubList(taskIdList);
        for (List<String> taskIdBatchSub : taskIdBatchSubList) {
            buTpRepairPlanTaskMapper.deleteBatchIds(taskIdBatchSub);
        }

        // 更新wbs
        String planIdsString = "'" + String.join("','", planIdSet) + "'";
        WbsConf wbsConf = new WbsConf("bu_tp_repair_plan_task")
                .setCode("task_no")
                .setWbs("task_wbs")
                .setFilter("plan_id in (" + planIdsString + ")");
        wbsService.updateWbs(wbsConf);

        return true;
    }


    public void setTaskRelation(BuTpRepairPlanTask task) {
        if (null == task) {
            return;
        }
        String taskId = task.getId();

        List<BuTpRepairPlanReguInfo> reguInfoList = buTpRepairPlanReguInfoMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanWorkstation> workstationList = buTpRepairPlanWorkstationMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanMaterial> materialList = buTpRepairPlanMaterialMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanTool> toolList = buTpRepairPlanToolMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanPerson> personList = buTpRepairPlanPersonMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanMustReplace> mustReplaceList = buTpRepairPlanMustReplaceMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanBookStep> bookStepList = buTpRepairPlanBookStepMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanSpeEq> specAssetList = buTpRepairPlanSpeEqMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanTaskPre> taskPreList = buTpRepairPlanTaskPreMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanTaskEqu> equipmentList = buTpRepairPlanTaskEquMapper.selectListByTaskId(taskId);
        List<BuTpRepairPlanTaskForms> formList = buTpRepairPlanTaskFormsMapper.selectListByTaskId(taskId);
        task.setRepairPlanReguInfo(reguInfoList)
                .setWorkstations(workstationList)
                .setMaterials(materialList)
                .setTools(toolList)
                .setPersons(personList)
                .setMustReplaces(mustReplaceList)
                .setBookSteps(bookStepList)
                .setSpecAssets(specAssetList)
                .setTaskPres(taskPreList)
                .setEquipments(equipmentList)
                .setForms(formList);
    }

    private void deleteTaskRelation(List<String> taskIdList) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return;
        }

        List<List<String>> taskIdBatchSubList = DatabaseBatchSubUtil.batchSubList(taskIdList);
        for (List<String> taskIdBatchSub : taskIdBatchSubList) {
            LambdaQueryWrapper<BuTpRepairPlanReguInfo> reguInfoWrapper = new LambdaQueryWrapper<BuTpRepairPlanReguInfo>()
                    .in(BuTpRepairPlanReguInfo::getTaskId, taskIdBatchSub);
            buTpRepairPlanReguInfoMapper.delete(reguInfoWrapper);
            LambdaQueryWrapper<BuTpRepairPlanWorkstation> workstationWrapper = new LambdaQueryWrapper<BuTpRepairPlanWorkstation>()
                    .in(BuTpRepairPlanWorkstation::getTaskId, taskIdBatchSub);
            buTpRepairPlanWorkstationMapper.delete(workstationWrapper);
            LambdaQueryWrapper<BuTpRepairPlanMaterial> materialWrapper = new LambdaQueryWrapper<BuTpRepairPlanMaterial>()
                    .in(BuTpRepairPlanMaterial::getTaskId, taskIdBatchSub);
            buTpRepairPlanMaterialMapper.delete(materialWrapper);
            LambdaQueryWrapper<BuTpRepairPlanTool> toolWrapper = new LambdaQueryWrapper<BuTpRepairPlanTool>()
                    .in(BuTpRepairPlanTool::getTaskId, taskIdBatchSub);
            buTpRepairPlanToolMapper.delete(toolWrapper);
            LambdaQueryWrapper<BuTpRepairPlanPerson> personWrapper = new LambdaQueryWrapper<BuTpRepairPlanPerson>()
                    .in(BuTpRepairPlanPerson::getTaskId, taskIdBatchSub);
            buTpRepairPlanPersonMapper.delete(personWrapper);
            LambdaQueryWrapper<BuTpRepairPlanMustReplace> mustReplaceWrapper = new LambdaQueryWrapper<BuTpRepairPlanMustReplace>()
                    .in(BuTpRepairPlanMustReplace::getTaskId, taskIdBatchSub);
            buTpRepairPlanMustReplaceMapper.delete(mustReplaceWrapper);
            LambdaQueryWrapper<BuTpRepairPlanBookStep> bookStepWrapper = new LambdaQueryWrapper<BuTpRepairPlanBookStep>()
                    .in(BuTpRepairPlanBookStep::getTaskId, taskIdBatchSub);
            buTpRepairPlanBookStepMapper.delete(bookStepWrapper);
            LambdaQueryWrapper<BuTpRepairPlanSpeEq> specAssetWrapper = new LambdaQueryWrapper<BuTpRepairPlanSpeEq>()
                    .in(BuTpRepairPlanSpeEq::getTpTaskId, taskIdBatchSub);
            buTpRepairPlanSpeEqMapper.delete(specAssetWrapper);
            LambdaQueryWrapper<BuTpRepairPlanTaskPre> taskPreWrapper = new LambdaQueryWrapper<BuTpRepairPlanTaskPre>()
                    .in(BuTpRepairPlanTaskPre::getTaskId, taskIdBatchSub);
            buTpRepairPlanTaskPreMapper.delete(taskPreWrapper);
            LambdaQueryWrapper<BuTpRepairPlanTrainPark> trainParkWrapper = new LambdaQueryWrapper<BuTpRepairPlanTrainPark>()
                    .in(BuTpRepairPlanTrainPark::getTaskId, taskIdBatchSub);
            buTpRepairPlanTrainParkMapper.delete(trainParkWrapper);
            LambdaQueryWrapper<BuTpRepairPlanTaskEqu> equipmentWrapper = new LambdaQueryWrapper<BuTpRepairPlanTaskEqu>()
                    .in(BuTpRepairPlanTaskEqu::getPlanTaskId, taskIdBatchSub);
            buTpRepairPlanTaskEquMapper.delete(equipmentWrapper);
            LambdaQueryWrapper<BuTpRepairPlanTaskForms> formWrapper = new LambdaQueryWrapper<BuTpRepairPlanTaskForms>()
                    .in(BuTpRepairPlanTaskForms::getTaskId, taskIdBatchSub);
            buTpRepairPlanTaskFormsMapper.delete(formWrapper);
        }
    }

    private void insertTaskRelation(BuTpRepairPlanTask task) {
        String taskId = task.getId();
        String planId = task.getPlanId();

        List<BuTpRepairPlanReguInfo> reguInfos = task.getRepairPlanReguInfo();
        if (CollectionUtils.isNotEmpty(reguInfos)) {
            for (BuTpRepairPlanReguInfo reguInfo : reguInfos) {
                reguInfo.setTaskId(taskId);
                if (StringUtils.isBlank(reguInfo.getId())) {
                    reguInfo.setId(UUIDGenerator.generate());
                }
            }
            List<List<BuTpRepairPlanReguInfo>> batchSubList = DatabaseBatchSubUtil.batchSubList(reguInfos);
            for (List<BuTpRepairPlanReguInfo> batchSub : batchSubList) {
                buTpRepairPlanReguInfoMapper.insertList(batchSub);
            }
        }

        List<BuTpRepairPlanWorkstation> workstations = task.getWorkstations();
        if (CollectionUtils.isNotEmpty(workstations)) {
            for (BuTpRepairPlanWorkstation workstation : workstations) {
                workstation.setTaskId(taskId);
                if (StringUtils.isBlank(workstation.getId())) {
                    workstation.setId(UUIDGenerator.generate());
                }
            }
            List<List<BuTpRepairPlanWorkstation>> batchSubList = DatabaseBatchSubUtil.batchSubList(workstations);
            for (List<BuTpRepairPlanWorkstation> batchSub : batchSubList) {
                buTpRepairPlanWorkstationMapper.insertList(batchSub);
            }
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
                if (StringUtils.isBlank(material.getId())) {
                    material.setId(UUIDGenerator.generate());
                }
            }
            List<List<BuTpRepairPlanMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(materialList);
            for (List<BuTpRepairPlanMaterial> batchSub : batchSubList) {
                buTpRepairPlanMaterialMapper.insertList(batchSub);
            }
        }

        List<BuTpRepairPlanTool> toolList = task.getTools();
        if (CollectionUtils.isNotEmpty(toolList)) {
            for (BuTpRepairPlanTool tool : toolList) {
                tool.setTaskId(taskId);
                if (null == tool.getAmount()) {
                    tool.setAmount(0D);
                }
                if (StringUtils.isBlank(tool.getId())) {
                    tool.setId(UUIDGenerator.generate());
                }
            }
            List<List<BuTpRepairPlanTool>> batchSubList = DatabaseBatchSubUtil.batchSubList(toolList);
            for (List<BuTpRepairPlanTool> batchSub : batchSubList) {
                buTpRepairPlanToolMapper.insertList(batchSub);
            }
        }

        List<BuTpRepairPlanPerson> persons = task.getPersons();
        if (CollectionUtils.isNotEmpty(persons)) {
            for (BuTpRepairPlanPerson person : persons) {
                person.setTaskId(taskId);
                if (null == person.getAmount()) {
                    person.setAmount(0);
                }
                if (StringUtils.isBlank(person.getId())) {
                    person.setId(UUIDGenerator.generate());
                }
            }
            List<List<BuTpRepairPlanPerson>> batchSubList = DatabaseBatchSubUtil.batchSubList(persons);
            for (List<BuTpRepairPlanPerson> batchSub : batchSubList) {
                buTpRepairPlanPersonMapper.insertList(batchSub);
            }
        }

        List<BuTpRepairPlanMustReplace> mustReplaces = task.getMustReplaces();
        if (CollectionUtils.isNotEmpty(mustReplaces)) {
            for (BuTpRepairPlanMustReplace mustReplace : mustReplaces) {
                mustReplace.setTaskId(taskId);
                if (null == mustReplace.getAmount()) {
                    mustReplace.setAmount(0D);
                }
                if (StringUtils.isBlank(mustReplace.getId())) {
                    mustReplace.setId(UUIDGenerator.generate());
                }
            }
            List<List<BuTpRepairPlanMustReplace>> batchSubList = DatabaseBatchSubUtil.batchSubList(mustReplaces);
            for (List<BuTpRepairPlanMustReplace> batchSub : batchSubList) {
                buTpRepairPlanMustReplaceMapper.insertList(batchSub);
            }
        }

        List<BuTpRepairPlanBookStep> bookSteps = task.getBookSteps();
        if (CollectionUtils.isNotEmpty(bookSteps)) {
            for (BuTpRepairPlanBookStep bookStep : bookSteps) {
                bookStep.setTaskId(taskId);
                if (StringUtils.isBlank(bookStep.getId())) {
                    bookStep.setId(UUIDGenerator.generate());
                }
            }
            List<List<BuTpRepairPlanBookStep>> batchSubList = DatabaseBatchSubUtil.batchSubList(bookSteps);
            for (List<BuTpRepairPlanBookStep> batchSub : batchSubList) {
                buTpRepairPlanBookStepMapper.insertList(batchSub);
            }
        }

        List<BuTpRepairPlanSpeEq> specAssets = task.getSpecAssets();
        if (CollectionUtils.isNotEmpty(specAssets)) {
            for (BuTpRepairPlanSpeEq specAsset : specAssets) {
                specAsset.setTpTaskId(taskId);
                if (StringUtils.isBlank(specAsset.getId())) {
                    specAsset.setId(UUIDGenerator.generate());
                }
            }
            List<List<BuTpRepairPlanSpeEq>> batchSubList = DatabaseBatchSubUtil.batchSubList(specAssets);
            for (List<BuTpRepairPlanSpeEq> batchSub : batchSubList) {
                buTpRepairPlanSpeEqMapper.insertList(batchSub);
            }
        }

        List<BuTpRepairPlanTaskPre> taskPres = task.getTaskPres();
        if (CollectionUtils.isNotEmpty(taskPres)) {
            for (BuTpRepairPlanTaskPre taskPre : taskPres) {
                taskPre.setTaskId(taskId);
                if (StringUtils.isBlank(taskPre.getId())) {
                    taskPre.setId(UUIDGenerator.generate());
                }
            }
            List<List<BuTpRepairPlanTaskPre>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskPres);
            for (List<BuTpRepairPlanTaskPre> batchSub : batchSubList) {
                buTpRepairPlanTaskPreMapper.insertList(batchSub);
            }
        }

        BuTpRepairPlanTrainPark trainPark = new BuTpRepairPlanTrainPark()
                .setTaskId(taskId)
                .setTrackId(task.getTrackId());
        buTpRepairPlanTrainParkMapper.insert(trainPark);

        List<BuTpRepairPlanTaskEqu> equipments = task.getEquipments();
        if (CollectionUtils.isNotEmpty(equipments)) {
            for (BuTpRepairPlanTaskEqu equipment : equipments) {
                equipment.setPlanTaskId(taskId);
                if (StringUtils.isBlank(equipment.getId())) {
                    equipment.setId(UUIDGenerator.generate());
                }
            }
            // 系统id、设备类型id为空时，应根据车辆结构id设置对应的系统id、设备类型id
            setTaskEquSystemAssetTypeIdIfNull(equipments);

            List<List<BuTpRepairPlanTaskEqu>> batchSubList = DatabaseBatchSubUtil.batchSubList(equipments);
            for (List<BuTpRepairPlanTaskEqu> batchSub : batchSubList) {
                buTpRepairPlanTaskEquMapper.insertList(batchSub);
            }
        }

        List<BuTpRepairPlanTaskForms> forms = task.getForms();
        if (CollectionUtils.isNotEmpty(forms)) {
            for (BuTpRepairPlanTaskForms form : forms) {
                form.setTaskId(taskId)
                        .setPlanId(planId);
                if (StringUtils.isBlank(form.getId())) {
                    form.setId(UUIDGenerator.generate());
                }
            }

            List<List<BuTpRepairPlanTaskForms>> batchSubList = DatabaseBatchSubUtil.batchSubList(forms);
            for (List<BuTpRepairPlanTaskForms> batchSub : batchSubList) {
                buTpRepairPlanTaskFormsMapper.insertList(batchSub);
            }
        }
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

    private int countPointSplit(String var) {
        if (StringUtils.isBlank(var)) {
            return 0;
        }
        String replaceAll = var.replaceAll("\\.", "");
        return var.length() - replaceAll.length();
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

//        taskGantt.setRepairPlanReguInfo(null == taskGantt.getRepairPlanReguInfo() ? new ArrayList<>() : taskGantt.getRepairPlanReguInfo());
//        taskGantt.setWorkstations(null == taskGantt.getWorkstations() ? new ArrayList<>() : taskGantt.getWorkstations());
//        taskGantt.setMaterials(null == taskGantt.getMaterials() ? new ArrayList<>() : taskGantt.getMaterials());
//        taskGantt.setTools(null == taskGantt.getTools() ? new ArrayList<>() : taskGantt.getTools());
//        taskGantt.setPersons(null == taskGantt.getPersons() ? new ArrayList<>() : taskGantt.getPersons());
//        taskGantt.setMustReplaces(null == taskGantt.getMustReplaces() ? new ArrayList<>() : taskGantt.getMustReplaces());
//        taskGantt.setBookSteps(null == taskGantt.getBookSteps() ? new ArrayList<>() : taskGantt.getBookSteps());
//        taskGantt.setSpecAssets(null == taskGantt.getSpecAssets() ? new ArrayList<>() : taskGantt.getSpecAssets());
//        taskGantt.setTaskPres(null == taskGantt.getTaskPres() ? new ArrayList<>() : taskGantt.getTaskPres());

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

}
