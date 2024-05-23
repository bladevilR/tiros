package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.tiros.task.service.TrainHistoryRecordCollectTaskService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.MapSizeUtil;
import org.jeecg.modules.produce.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryChange;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryFault;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryMeasure;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryWork;
import org.jeecg.modules.produce.trainhistory.bean.bo.IdNameBO;
import org.jeecg.modules.produce.trainhistory.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 归档车辆履历 定时任务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-04
 */
@Slf4j
@Service
public class TrainHistoryRecordCollectTaskServiceImpl implements TrainHistoryRecordCollectTaskService {

    @Resource
    private TrainHistoryCommonMapper trainHistoryCommonMapper;
    @Resource
    private BuTrainHistoryWorkMapper buTrainHistoryWorkMapper;
    @Resource
    private BuTrainHistoryFaultMapper buTrainHistoryFaultMapper;
    @Resource
    private BuTrainHistoryChangeMapper buTrainHistoryChangeMapper;
    @Resource
    private BuTrainHistoryMeasureMapper buTrainHistoryMeasureMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see TrainHistoryRecordCollectTaskService#generateTrainHistoryRecord()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean generateTrainHistoryRecord() throws RuntimeException {
        // 查询公共属性，用于id转name
        List<IdNameBO> departIdNameList = trainHistoryCommonMapper.selectDepartIdName();
        List<IdNameBO> depotIdNameList = trainHistoryCommonMapper.selectDepotIdName();
        List<IdNameBO> workshopIdNameList = trainHistoryCommonMapper.selectWorkshopIdName();
        List<IdNameBO> workstationIdNameList = trainHistoryCommonMapper.selectWorkstationIdName();
        List<IdNameBO> lineIdNameList = trainHistoryCommonMapper.selectLineIdName();
        List<IdNameBO> trainIdNameList = trainHistoryCommonMapper.selectTrainIdName();

        Map<String, String> departIdNameMap = transformToIdNameMap(departIdNameList);
        Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();
        Map<String, String> depotIdNameMap = transformToIdNameMap(depotIdNameList);
        Map<String, String> workshopIdNameMap = transformToIdNameMap(workshopIdNameList);
        Map<String, String> workstationIdNameMap = transformToIdNameMap(workstationIdNameList);
        Map<String, String> lineIdNameMap = transformToIdNameMap(lineIdNameList);
        Map<String, String> trainIdNameMap = transformToIdNameMap(trainIdNameList);
        Map<String, String> trainNameIdMap = transformToNameIdMap(trainIdNameList);
        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = assetTypeCacheService.map(null);

//        // 获取上次更新时间
//        String configCode = "trainHistoryGenerateTime";
//        Date lastTime = sysConfigService.getScheduleTaskLastTime(configCode);
        //TODO-zhaiyantao 2021/4/29 18:17 增量更新有问题，先做全量更新，问题在于数据收集，如第一次查时间对提交状态不对，第二次查提交状态对时间不对，其他的统计定时任务类似
        LambdaQueryWrapper<BuTrainHistoryWork> deleteWorkWrapper = new LambdaQueryWrapper<BuTrainHistoryWork>()
                .eq(BuTrainHistoryWork::getArchiveType, 0);
        buTrainHistoryWorkMapper.delete(deleteWorkWrapper);
        LambdaQueryWrapper<BuTrainHistoryFault> deleteFaultWrapper = new LambdaQueryWrapper<BuTrainHistoryFault>()
                .eq(BuTrainHistoryFault::getArchiveType, 0);
        buTrainHistoryFaultMapper.delete(deleteFaultWrapper);
        LambdaQueryWrapper<BuTrainHistoryChange> deleteChangeWrapper = new LambdaQueryWrapper<BuTrainHistoryChange>()
                .eq(BuTrainHistoryChange::getArchiveType, 0);
        buTrainHistoryChangeMapper.delete(deleteChangeWrapper);
        LambdaQueryWrapper<BuTrainHistoryMeasure> deleteMeasureWrapper = new LambdaQueryWrapper<BuTrainHistoryMeasure>()
                .eq(BuTrainHistoryMeasure::getArchiveType, 0);
        buTrainHistoryMeasureMapper.delete(deleteMeasureWrapper);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1900);
        Date lastTime = calendar.getTime();
        String dateString = DateUtils.datetimeFormat.get().format(lastTime);
        // 本次更新时间
        Date now = new Date();

        // 作业记录：工单任务bu_work_order_task
        List<BuTrainHistoryWork> workList = buTrainHistoryWorkMapper.selectListNeedCollect(lastTime);
        if (CollectionUtils.isNotEmpty(workList)) {
            for (BuTrainHistoryWork work : workList) {
                work.setArchiveType(0)
                        .setGroupName(departIdNameMap.get(work.getGroupId()))
                        .setMonitorName(userIdNameMap.get(work.getMonitor()))
                        .setDepotName(depotIdNameMap.get(work.getDepotId()))
                        .setWorkshopName(workshopIdNameMap.get(work.getWorkshopId()))
                        .setLineName(lineIdNameMap.get(work.getLineId()))
                        .setTrainId(trainNameIdMap.get(work.getTrainName()));

                BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOMap.get(work.getAssetTypeId());
                if (null != assetTypeBO) {
                    work.setSysId(assetTypeBO.getSysId())
                            .setSysName(assetTypeBO.getSysName())
                            .setSubSysId(assetTypeBO.getSubSysId())
                            .setSubSysName(assetTypeBO.getSubSysName());
                }
            }
            List<List<BuTrainHistoryWork>> workBatchSubList = DatabaseBatchSubUtil.batchSubList(workList);
            for (List<BuTrainHistoryWork> workBatchSub : workBatchSubList) {
                buTrainHistoryWorkMapper.insertList(workBatchSub);
            }
        }
        log.info("归档车辆履历定时任务：新增了" + workList.size() + "条作业记录。时间=" + dateString);

        // 故障记录：故障信息bu_fault_info
        List<BuTrainHistoryFault> faultList = buTrainHistoryFaultMapper.selectListNeedCollect(lastTime);
        if (CollectionUtils.isNotEmpty(faultList)) {
            for (BuTrainHistoryFault fault : faultList) {
                fault.setArchiveType(0)
                        .setLineName(lineIdNameMap.get(fault.getLineId()))
                        .setTrainId(trainNameIdMap.get(fault.getTrainName()))
                        .setSysName(assetTypeIdBOMap.get(fault.getSysId()) == null ? null : assetTypeIdBOMap.get(fault.getSysId()).getName())
                        .setSubSysName(assetTypeIdBOMap.get(fault.getSubSysId()) == null ? null : assetTypeIdBOMap.get(fault.getSubSysId()).getName())
                        .setWorkStationId(workstationIdNameMap.get(fault.getWorkStationId()))
                        .setReportGroup(departIdNameMap.get(fault.getReportGroup()))
                        .setReportUserId(userIdNameMap.get(fault.getReportUserId()))
                        .setDutyEngineer(userIdNameMap.get(fault.getDutyEngineer()));

                List<BuFaultHandleRecord> handleRecordList = fault.getHandleRecordList();
                if (CollectionUtils.isNotEmpty(handleRecordList)) {
                    BuFaultHandleRecord lastHandleRecord = handleRecordList.get(0);
                    fault.setHandleTime(lastHandleRecord.getSolutionTime());

                    String solutionGroupNames = handleRecordList.stream()
                            .map(handle -> departIdNameMap.get(handle.getSolutionGroupId()))
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(","));
                    String solutionUserNames = handleRecordList.stream()
                            .map(handle -> userIdNameMap.get(handle.getSolutionUserId()))
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(","));
                    fault.setHandleGroup(solutionGroupNames)
                            .setHandleUser(solutionUserNames);
                }
            }
            List<List<BuTrainHistoryFault>> faultBatchSubList = DatabaseBatchSubUtil.batchSubList(faultList);
            for (List<BuTrainHistoryFault> faultBatchSub : faultBatchSubList) {
                buTrainHistoryFaultMapper.insertList(faultBatchSub);
            }
        }
        log.info("归档车辆履历定时任务：新增了" + faultList.size() + "条故障记录。时间=" + dateString);

        // 更换记录：周转件更换bu_work_turnover_change
        List<BuTrainHistoryChange> changeList = buTrainHistoryChangeMapper.selectListNeedCollect(lastTime);
        List<BuTrainHistoryChange> needInsertChangeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(changeList)) {
            Map<String, List<BuTrainHistoryChange>> orderIdChangeListMap = changeList.stream()
                    .collect(Collectors.groupingBy(BuTrainHistoryChange::getOrderId));

            for (Map.Entry<String, List<BuTrainHistoryChange>> orderIdChangeListEntry : orderIdChangeListMap.entrySet()) {
                List<BuTrainHistoryChange> innerChangeList = orderIdChangeListEntry.getValue();
                innerChangeList.sort(Comparator.comparing(BuTrainHistoryChange::getExchangeTime, Comparator.nullsLast(Comparator.naturalOrder())));

                AtomicInteger index = new AtomicInteger(1);
                for (BuTrainHistoryChange change : innerChangeList) {
                    change.setArchiveType(0)
                            .setDutyUser(userIdNameMap.get(change.getDutyUser()))
                            .setWorkUser(userIdNameMap.get(change.getWorkUser()))
                            .setExchangeNo(String.valueOf(index.getAndIncrement()))
                            .setTrainId(trainNameIdMap.get(change.getTrainName()));

                    BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOMap.get(change.getAssetTypeId());
                    if (null != assetTypeBO) {
                        change.setAssetTypeName(assetTypeBO.getName())
                                .setSysId(assetTypeBO.getSysId())
                                .setSysName(assetTypeBO.getSysName())
                                .setSubSysId(assetTypeBO.getSubSysId())
                                .setSubSysName(assetTypeBO.getSubSysName());
                    }
                    needInsertChangeList.add(change);
                }
            }
            List<List<BuTrainHistoryChange>> changeBatchSubList = DatabaseBatchSubUtil.batchSubList(needInsertChangeList);
            for (List<BuTrainHistoryChange> changeBatchSub : changeBatchSubList) {
                buTrainHistoryChangeMapper.insertList(changeBatchSub);
            }
        }
        log.info("归档车辆履历定时任务：新增了" + needInsertChangeList.size() + "条更换记录。时间=" + dateString);

        // 测量记录：记录表数据值记录bu_pl_data_record_value
        List<BuTrainHistoryMeasure> measureList = buTrainHistoryMeasureMapper.selectListNeedCollect(lastTime);
        if (CollectionUtils.isNotEmpty(measureList)) {
            for (BuTrainHistoryMeasure measure : measureList) {
                measure.setArchiveType(0)
                        .setTrainId(trainNameIdMap.get(measure.getTrainName()))
                        .setWorkGroup(departIdNameMap.get(measure.getWorkGroup()));
                if (null == measure.getIsAlert()) {
                    measure.setIsAlert(0);
                }

                BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOMap.get(measure.getAssetTypeId());
                if (null != assetTypeBO) {
                    measure.setSysId(assetTypeBO.getSysId())
                            .setSysName(assetTypeBO.getSysName())
                            .setSubSysId(assetTypeBO.getSubSysId())
                            .setSubSysName(assetTypeBO.getSubSysName());
                }

                List<String> workUserIdList = measure.getWorkUserIdList();
                if (CollectionUtils.isNotEmpty(workUserIdList)) {
                    String workUserNames = workUserIdList.stream()
                            .map(userIdNameMap::get)
                            .collect(Collectors.joining(","));
                    measure.setWorkUser(workUserNames);
                }
                List<String> toolIdList = measure.getToolIdList();
                if (CollectionUtils.isNotEmpty(toolIdList)) {
                    String toolIds = String.join(",", toolIdList);
                    measure.setTools(toolIds);
                }
            }
            List<List<BuTrainHistoryMeasure>> measureBatchSubList = DatabaseBatchSubUtil.batchSubList(measureList);
            for (List<BuTrainHistoryMeasure> measureBatchSub : measureBatchSubList) {
                buTrainHistoryMeasureMapper.insertList(measureBatchSub);
            }
        }
        log.info("归档车辆履历定时任务：新增了" + measureList.size() + "条测量记录。时间=" + dateString);

//        // 更新本次更新时间
//        sysConfigService.updateScheduleTaskLastTime(configCode, "归档车辆履历", now);

        return true;
    }

    /**
     * @see TrainHistoryRecordCollectTaskService#reWriteTrainHistoryFaultRecord(List, int)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean reWriteTrainHistoryFaultRecord(List<String> faultIdList, int type) throws RuntimeException {
        if (CollectionUtils.isEmpty(faultIdList)) {
            return true;
        }

        buTrainHistoryFaultMapper.deleteBatchIds(faultIdList);
        if (1 == type) {
            log.info("归档车辆履历：删除了" + faultIdList.size() + "条故障记录。");
            return true;
        }

        List<BuTrainHistoryFault> faultList = buTrainHistoryFaultMapper.selectFaultNeedCollectByFaultIdList(faultIdList);
        if (CollectionUtils.isNotEmpty(faultList)) {
            // 查询公共属性，用于id转name
            List<IdNameBO> departIdNameList = trainHistoryCommonMapper.selectDepartIdName();

            Map<String, String> departIdNameMap = transformToIdNameMap(departIdNameList);
            Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

            for (BuTrainHistoryFault fault : faultList) {
                fault.setArchiveType(0)
                        .setReportGroup(departIdNameMap.get(fault.getReportGroup()))
                        .setReportUserId(userIdNameMap.get(fault.getReportUserId()))
                        .setDutyEngineer(userIdNameMap.get(fault.getDutyEngineer()));

                List<BuFaultHandleRecord> handleRecordList = fault.getHandleRecordList();
                if (CollectionUtils.isNotEmpty(handleRecordList)) {
                    BuFaultHandleRecord lastHandleRecord = handleRecordList.get(0);
                    fault.setHandleTime(lastHandleRecord.getSolutionTime());

                    String solutionGroupNames = handleRecordList.stream()
                            .map(handle -> departIdNameMap.get(handle.getSolutionGroupId()))
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(","));
                    String solutionUserNames = handleRecordList.stream()
                            .map(handle -> userIdNameMap.get(handle.getSolutionUserId()))
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(","));
                    fault.setHandleGroup(solutionGroupNames)
                            .setHandleUser(solutionUserNames);
                }
            }
            List<List<BuTrainHistoryFault>> faultBatchSubList = DatabaseBatchSubUtil.batchSubList(faultList);
            for (List<BuTrainHistoryFault> faultBatchSub : faultBatchSubList) {
                buTrainHistoryFaultMapper.insertList(faultBatchSub);
            }
            log.info("归档车辆履历：更新了" + faultList.size() + "条故障记录。");
        }
        return true;
    }


    private Map<String, String> transformToIdNameMap(List<IdNameBO> idNameList) {
        if (CollectionUtils.isEmpty(idNameList)) {
            return new HashMap<>(0);
        }

        int mapSize = MapSizeUtil.tableSizeFor(idNameList.size());
        Map<String, String> idNameMap = new HashMap<>(mapSize);

        idNameList.forEach(idNameBO -> idNameMap.put(idNameBO.getId(), idNameBO.getName()));

        return idNameMap;
    }

    private Map<String, String> transformToNameIdMap(List<IdNameBO> idNameList) {
        if (CollectionUtils.isEmpty(idNameList)) {
            return new HashMap<>(0);
        }

        int mapSize = MapSizeUtil.tableSizeFor(idNameList.size());
        Map<String, String> nameIdMap = new HashMap<>(mapSize);

        idNameList.forEach(idNameBO -> nameIdMap.put(idNameBO.getName(), idNameBO.getId()));

        return nameIdMap;
    }

}
