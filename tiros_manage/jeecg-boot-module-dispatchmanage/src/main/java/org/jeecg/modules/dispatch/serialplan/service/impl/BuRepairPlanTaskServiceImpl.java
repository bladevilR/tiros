package org.jeecg.modules.dispatch.serialplan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanTaskForms;
import org.jeecg.modules.dispatch.planform.mapper.BuRepairPlanTaskFormsMapper;
import org.jeecg.modules.dispatch.serialplan.bean.*;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanTaskNoUpdateVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanTaskVOGantt;
import org.jeecg.modules.dispatch.serialplan.mapper.*;
import org.jeecg.modules.dispatch.serialplan.service.BuRepairPlanTaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 列计划任务 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Service
public class BuRepairPlanTaskServiceImpl extends ServiceImpl<BuRepairPlanTaskMapper, BuRepairPlanTask> implements BuRepairPlanTaskService {

    @Resource
    private BuRepairPlanTaskMapper buRepairPlanTaskMapper;
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
    private BuRepairPlanTaskFormsMapper buRepairPlanTaskFormsMapper;
    @Resource
    private BuTrainStructureDetailDispatchMapper buTrainStructureDetailDispatchMapper;
    @Resource
    private WbsService wbsService;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuRepairPlanTaskService#selectTaskDetail(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanTaskVOGantt selectTaskDetail(String taskId) throws Exception {
        BuRepairPlanTask task = buRepairPlanTaskMapper.selectPlanTaskTaskId(taskId);
        if (null == task) {
            throw new JeecgBootException("列计划任务不存在");
        }

        setTaskRelation(task);

        return transformToGantt(task);
    }

    /**
     * @see BuRepairPlanTaskService#saveOrUpdatePlanTask(BuRepairPlanTaskVOGantt)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdatePlanTask(BuRepairPlanTaskVOGantt taskVOWithGantt) throws Exception {
        BuRepairPlanTask planTask = transformFromGantt(taskVOWithGantt);

        // 检查字段长度
        Integer safeNoticeDataLength = buRepairPlanTaskMapper.selectSafeNoticeDataLength();
        LengthCheckUtil.maxLength(planTask.getSafeNotice(), "安全预想", safeNoticeDataLength);

        // 如果序号为空，设置序号
        if (null == planTask.getTaskNo()) {
            setTaskNo(planTask);
        }

        // 删除任务关联信息
        deleteTaskRelation(Collections.singletonList(planTask.getId()));
        // 删除任务
        buRepairPlanTaskMapper.deleteById(planTask.getId());

        // 插入任务
        buRepairPlanTaskMapper.insert(planTask);
        // 插入任务关联信息
        insertTaskRelation(planTask);

        return true;
    }

    /**
     * @see BuRepairPlanTaskService#updateTaskNoAndWbs(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTaskNoAndWbs(List<BuRepairPlanTaskNoUpdateVO> taskNoUpdateVOS) throws Exception {
        // 更新任务序号vo转计划任务
        List<BuRepairPlanTask> taskList = new ArrayList<>();
        for (BuRepairPlanTaskNoUpdateVO updateVO : taskNoUpdateVOS) {
            BuRepairPlanTask task = new BuRepairPlanTask()
                    .setId(updateVO.getUID())
                    .setTaskNo(updateVO.getID());
            taskList.add(task);
        }

        if (CollectionUtils.isNotEmpty(taskList)) {
            List<List<BuRepairPlanTask>> taskBatchSubList = DatabaseBatchSubUtil.batchSubList(taskList);
            for (List<BuRepairPlanTask> taskBatchSub : taskBatchSubList) {
                buRepairPlanTaskMapper.updateListTaskNo(taskBatchSub);
            }

            // 更新wbs
            WbsConf wbsConf = new WbsConf("bu_repair_plan_task")
                    .setCode("task_no")
                    .setWbs("task_wbs");
            Set<String> planIdSet = taskNoUpdateVOS.stream()
                    .map(BuRepairPlanTaskNoUpdateVO::getPlanId)
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
     * @see BuRepairPlanTaskService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        Set<String> planIdSet = new HashSet<>();

        // 查询任务下的子节点
        List<List<String>> idBatchSubList = DatabaseBatchSubUtil.batchSubList(idList);
        Set<String> taskIdSet = new HashSet<>(idList);
        for (List<String> idBatchSub : idBatchSubList) {
            List<BuRepairPlanTask> planTaskList = buRepairPlanTaskMapper.selectBatchIds(idBatchSub);
            for (BuRepairPlanTask planTask : planTaskList) {
                LambdaQueryWrapper<BuRepairPlanTask> planTaskChildrenWrapper = new LambdaQueryWrapper<BuRepairPlanTask>()
                        .eq(BuRepairPlanTask::getPlanId, planTask.getPlanId())
                        .likeRight(BuRepairPlanTask::getTaskWbs, planTask.getTaskWbs())
                        .select(BuRepairPlanTask::getId, BuRepairPlanTask::getPlanId);
                List<BuRepairPlanTask> children = buRepairPlanTaskMapper.selectList(planTaskChildrenWrapper);
                for (BuRepairPlanTask child : children) {
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
            buRepairPlanTaskMapper.deleteBatchIds(taskIdBatchSub);
        }

        // 更新wbs
        String planIdsString = "'" + String.join("','", planIdSet) + "'";
        WbsConf wbsConf = new WbsConf("bu_repair_plan_task")
                .setCode("task_no")
                .setWbs("task_wbs")
                .setFilter("plan_id in (" + planIdsString + ")");
        wbsService.updateWbs(wbsConf);

        return true;
    }


    private void setTaskRelation(BuRepairPlanTask task) {
        if (null == task) {
            return;
        }
        String taskId = task.getId();

        List<BuRepairTaskRegu> reguInfoList = buRepairTaskReguMapper.selectListByTaskId(taskId);
        List<BuRepairTaskWorkstation> workstationList = buRepairTaskWorkstationMapper.selectListByTaskId(taskId);
        List<BuRepairTaskMaterial> materialList = buRepairTaskMaterialMapper.selectListByTaskId(taskId);
        List<BuRepairTaskTool> toolList = buRepairTaskToolMapper.selectListByTaskId(taskId);
        List<BuRepairTaskStaffRequire> personList = buRepairTaskStaffRequireMapper.selectListByTaskId(taskId);
        List<BuRepairTaskMustReplace> mustReplaceList = buRepairTaskMustReplaceMapper.selectListByTaskId(taskId);
        List<BuRepairTaskBookStep> bookStepList = buRepairTaskBookStepMapper.selectListByTaskId(taskId);
        List<BuRepairPlanSpeEq> specAssetList = buRepairPlanSpeEqMapper.selectListByTaskId(taskId);
        List<BuRepairPlanTaskPre> taskPreList = buRepairPlanTaskPreMapper.selectListByTaskId(taskId);
        List<BuRepairPlanTaskEqu> equipmentList = buRepairPlanTaskEquMapper.selectListByTaskId(taskId);
        List<BuRepairPlanTaskForms> formList = buRepairPlanTaskFormsMapper.selectListByTaskId(taskId);
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
            LambdaQueryWrapper<BuRepairTaskRegu> taskReguWrapper = new LambdaQueryWrapper<BuRepairTaskRegu>()
                    .in(BuRepairTaskRegu::getTaskId, taskIdBatchSub);
            buRepairTaskReguMapper.delete(taskReguWrapper);
            LambdaQueryWrapper<BuRepairTaskBookStep> taskBookStepWrapper = new LambdaQueryWrapper<BuRepairTaskBookStep>()
                    .in(BuRepairTaskBookStep::getTaskId, taskIdBatchSub);
            buRepairTaskBookStepMapper.delete(taskBookStepWrapper);
            LambdaQueryWrapper<BuRepairPlanSpeEq> taskSpecAssetWrapper = new LambdaQueryWrapper<BuRepairPlanSpeEq>()
                    .in(BuRepairPlanSpeEq::getTaskId, taskIdBatchSub);
            buRepairPlanSpeEqMapper.delete(taskSpecAssetWrapper);
            LambdaQueryWrapper<BuRepairTaskWorkstation> workstationWrapper = new LambdaQueryWrapper<BuRepairTaskWorkstation>()
                    .in(BuRepairTaskWorkstation::getTaskId, taskIdBatchSub);
            buRepairTaskWorkstationMapper.delete(workstationWrapper);
            LambdaQueryWrapper<BuRepairTaskMaterial> materialWrapper = new LambdaQueryWrapper<BuRepairTaskMaterial>()
                    .in(BuRepairTaskMaterial::getTaskId, taskIdBatchSub);
            buRepairTaskMaterialMapper.delete(materialWrapper);
            LambdaQueryWrapper<BuRepairTaskTool> toolWrapper = new LambdaQueryWrapper<BuRepairTaskTool>()
                    .in(BuRepairTaskTool::getTaskId, taskIdBatchSub);
            buRepairTaskToolMapper.delete(toolWrapper);
            LambdaQueryWrapper<BuRepairTaskStaffRequire> personWrapper = new LambdaQueryWrapper<BuRepairTaskStaffRequire>()
                    .in(BuRepairTaskStaffRequire::getTaskId, taskIdBatchSub);
            buRepairTaskStaffRequireMapper.delete(personWrapper);
            LambdaQueryWrapper<BuRepairTaskMustReplace> mustReplaceWrapper = new LambdaQueryWrapper<BuRepairTaskMustReplace>()
                    .in(BuRepairTaskMustReplace::getTaskId, taskIdBatchSub);
            buRepairTaskMustReplaceMapper.delete(mustReplaceWrapper);
            LambdaQueryWrapper<BuRepairPlanTaskPre> taskPreWrapper = new LambdaQueryWrapper<BuRepairPlanTaskPre>()
                    .in(BuRepairPlanTaskPre::getTaskId, taskIdBatchSub);
            buRepairPlanTaskPreMapper.delete(taskPreWrapper);
            LambdaQueryWrapper<BuRepairTaskTrack> trackWrapper = new LambdaQueryWrapper<BuRepairTaskTrack>()
                    .in(BuRepairTaskTrack::getTaskId, taskIdBatchSub);
            buRepairTaskTrackMapper.delete(trackWrapper);
            LambdaQueryWrapper<BuRepairPlanTaskEqu> equipmentWrapper = new LambdaQueryWrapper<BuRepairPlanTaskEqu>()
                    .in(BuRepairPlanTaskEqu::getPlanTaskId, taskIdBatchSub);
            buRepairPlanTaskEquMapper.delete(equipmentWrapper);
            LambdaQueryWrapper<BuRepairPlanTaskForms> formWrapper = new LambdaQueryWrapper<BuRepairPlanTaskForms>()
                    .in(BuRepairPlanTaskForms::getTaskId, taskIdBatchSub);
            buRepairPlanTaskFormsMapper.delete(formWrapper);
        }
    }

    private void insertTaskRelation(BuRepairPlanTask planTask) {
        String taskId = planTask.getId();
        String planId = planTask.getPlanId();

        List<BuRepairTaskRegu> taskRegus = planTask.getRepairPlanReguInfo();
        if (CollectionUtils.isNotEmpty(taskRegus)) {
            taskRegus.forEach(taskRegu -> taskRegu.setTaskId(taskId));
            List<List<BuRepairTaskRegu>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskRegus);
            for (List<BuRepairTaskRegu> batchSub : batchSubList) {
                buRepairTaskReguMapper.insertList(batchSub);
            }
        }

        List<BuRepairTaskBookStep> bookSteps = planTask.getBookSteps();
        if (CollectionUtils.isNotEmpty(bookSteps)) {
            bookSteps.forEach(bookStep -> bookStep.setTaskId(taskId));
            List<List<BuRepairTaskBookStep>> batchSubList = DatabaseBatchSubUtil.batchSubList(bookSteps);
            for (List<BuRepairTaskBookStep> batchSub : batchSubList) {
                buRepairTaskBookStepMapper.insertList(batchSub);
            }
        }

        List<BuRepairPlanSpeEq> specAssets = planTask.getSpecAssets();
        if (CollectionUtils.isNotEmpty(specAssets)) {
            specAssets.forEach(specAsset -> specAsset.setTaskId(taskId));
            List<List<BuRepairPlanSpeEq>> batchSubList = DatabaseBatchSubUtil.batchSubList(specAssets);
            for (List<BuRepairPlanSpeEq> batchSub : batchSubList) {
                buRepairPlanSpeEqMapper.insertList(batchSub);
            }
        }

        List<BuRepairTaskWorkstation> workstations = planTask.getWorkstations();
        if (CollectionUtils.isNotEmpty(workstations)) {
            workstations.forEach(workstation -> workstation.setTaskId(taskId));
            List<List<BuRepairTaskWorkstation>> batchSubList = DatabaseBatchSubUtil.batchSubList(workstations);
            for (List<BuRepairTaskWorkstation> batchSub : batchSubList) {
                buRepairTaskWorkstationMapper.insertList(batchSub);
            }
        }

        List<BuRepairTaskMaterial> materials = planTask.getMaterials();
        if (CollectionUtils.isNotEmpty(materials)) {
            for (BuRepairTaskMaterial material : materials) {
                material.setTaskId(taskId);
                if (null == material.getAmount()) {
                    material.setAmount(0D);
                }
                if (null == material.getUseCategory()) {
                    material.setUseCategory(-1);
                }
            }
            List<List<BuRepairTaskMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(materials);
            for (List<BuRepairTaskMaterial> batchSub : batchSubList) {
                buRepairTaskMaterialMapper.insertList(batchSub);
            }
        }

        List<BuRepairTaskTool> tools = planTask.getTools();
        if (CollectionUtils.isNotEmpty(tools)) {
            for (BuRepairTaskTool tool : tools) {
                tool.setTaskId(taskId);
                if (null == tool.getAmount()) {
                    tool.setAmount(0D);
                }
            }
            List<List<BuRepairTaskTool>> batchSubList = DatabaseBatchSubUtil.batchSubList(tools);
            for (List<BuRepairTaskTool> batchSub : batchSubList) {
                buRepairTaskToolMapper.insertList(batchSub);
            }
        }

        List<BuRepairTaskStaffRequire> persons = planTask.getPersons();
        if (CollectionUtils.isNotEmpty(persons)) {
            for (BuRepairTaskStaffRequire person : persons) {
                person.setTaskId(taskId);
                if (null == person.getAmount()) {
                    person.setAmount(0);
                }
            }
            List<List<BuRepairTaskStaffRequire>> batchSubList = DatabaseBatchSubUtil.batchSubList(persons);
            for (List<BuRepairTaskStaffRequire> batchSub : batchSubList) {
                buRepairTaskStaffRequireMapper.insertList(batchSub);
            }
        }

        List<BuRepairTaskMustReplace> mustReplaces = planTask.getMustReplaces();
        if (CollectionUtils.isNotEmpty(mustReplaces)) {
            for (BuRepairTaskMustReplace mustReplace : mustReplaces) {
                mustReplace.setTaskId(taskId);
                mustReplace.setPlanId(planTask.getPlanId());
                if (null == mustReplace.getAmount()) {
                    mustReplace.setAmount(0D);
                }
            }
            List<List<BuRepairTaskMustReplace>> batchSubList = DatabaseBatchSubUtil.batchSubList(mustReplaces);
            for (List<BuRepairTaskMustReplace> batchSub : batchSubList) {
                buRepairTaskMustReplaceMapper.insertList(batchSub);
            }
        }

        List<BuRepairPlanTaskPre> taskPres = planTask.getTaskPres();
        if (CollectionUtils.isNotEmpty(taskPres)) {
            taskPres.forEach(taskPre -> taskPre.setTaskId(taskId));
            List<List<BuRepairPlanTaskPre>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskPres);
            for (List<BuRepairPlanTaskPre> batchSub : batchSubList) {
                buRepairPlanTaskPreMapper.insertList(batchSub);
            }
        }

        BuRepairTaskTrack trainPark = new BuRepairTaskTrack()
                .setTaskId(taskId)
                .setTrackId(planTask.getTrackId());
        buRepairTaskTrackMapper.insert(trainPark);

        List<BuRepairPlanTaskEqu> equipments = planTask.getEquipments();
        if (CollectionUtils.isNotEmpty(equipments)) {
            equipments.forEach(equipment -> equipment.setPlanTaskId(taskId));
            // 系统id、设备类型id为空时，应根据车辆结构id设置对应的系统id、设备类型id
            setTaskEquSystemAssetTypeIdIfNull(equipments);

            List<List<BuRepairPlanTaskEqu>> batchSubList = DatabaseBatchSubUtil.batchSubList(equipments);
            for (List<BuRepairPlanTaskEqu> batchSub : batchSubList) {
                buRepairPlanTaskEquMapper.insertList(batchSub);
            }
        }

        List<BuRepairPlanTaskForms> forms = planTask.getForms();
        if (CollectionUtils.isNotEmpty(forms)) {
            for (BuRepairPlanTaskForms form : forms) {
                form.setTaskId(taskId)
                        .setPlanId(planId);
                if (StringUtils.isBlank(form.getId())) {
                    form.setId(UUIDGenerator.generate());
                }
            }

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

        return taskGantt;
    }

    private int countPointSplit(String var) {
        if (StringUtils.isBlank(var)) {
            return 0;
        }
        String replaceAll = var.replaceAll("\\.", "");
        return var.length() - replaceAll.length();
    }

    private BuRepairPlanTask transformFromGantt(BuRepairPlanTaskVOGantt taskVOWithGantt) {
        if (null == taskVOWithGantt) {
            return null;
        }

        BuRepairPlanTask task = new BuRepairPlanTask();

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

    private void setTaskNo(BuRepairPlanTask planTask) {
        Integer maxTaskNo = buRepairPlanTaskMapper.selectMaxTaskNoByParentId(planTask.getParentId());
        if (null == maxTaskNo) {
            planTask.setTaskNo(1);
        } else {
            planTask.setTaskNo(maxTaskNo + 1);
        }
    }

}
