package org.jeecg.modules.dispatch.workorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.dispatch.serialplan.mapper.BuTrainAssetDispatchMapper;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOutsourceOutinQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOutsourcePerformQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.DelayReasonVO;
import org.jeecg.modules.dispatch.workorder.mapper.BuOutsourceOutinDetailMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuOutsourceOutinMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderMapper;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderTaskMapper;
import org.jeecg.modules.dispatch.workorder.service.BuOutsourceOutinService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 委外出入库单(交接) 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-14
 */
@Service
public class BuOutsourceOutinServiceImpl extends ServiceImpl<BuOutsourceOutinMapper, BuOutsourceOutin> implements BuOutsourceOutinService {

    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;
    @Resource
    private BuWorkOrderTaskMapper buWorkOrderTaskMapper;
    @Resource
    private BuTrainAssetDispatchMapper buTrainAssetDispatchMapper;
    @Resource
    private BuOutsourceOutinMapper buOutsourceOutinMapper;
    @Resource
    private BuOutsourceOutinDetailMapper buOutsourceOutinDetailMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;
    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see BuOutsourceOutinService#pageOutIn(BuOutsourceOutinQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuOutsourceOutin> pageOutIn(BuOutsourceOutinQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return buOutsourceOutinMapper.selectOutinPage(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuOutsourceOutinService#pageOutInDetail(BuOutsourceOutinQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuOutsourceOutinDetail> pageOutInDetail(BuOutsourceOutinQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return buOutsourceOutinMapper.selectOutinDetailPage(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuOutsourceOutinService#selectOutsourceOutinByTaskId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public BuOutsourceOutin selectOutsourceOutinByTaskId(String id) throws Exception {
        BuOutsourceOutin outsourceOutin = new BuOutsourceOutin();
        // 根据工单任务id查询委外交接单（一个工单任务只会有一个或没有）
        List<BuOutsourceOutin> outsourceOutinList = buOutsourceOutinMapper.selectListByOrderTaskId(id);
        if (CollectionUtils.isNotEmpty(outsourceOutinList)) {
            // 查到了返回查询到的数据
            outsourceOutin = outsourceOutinList.get(0);
            setUserName(outsourceOutin);
            List<BuOutsourceOutinDetail> outinDetailList = outsourceOutin.getOutinDetails();
            if (CollectionUtils.isNotEmpty(outinDetailList)) {
                Integer needDay = outsourceOutin.getNeedDay();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(outsourceOutin.getTransferDate());
                calendar.add(Calendar.DATE, null == needDay ? 0 : needDay);
                Date returnTime = calendar.getTime();

                for (BuOutsourceOutinDetail outinDetail : outinDetailList) {
                    outinDetail
                            .setLineId(outsourceOutin.getLineId())
                            .setLineName(outsourceOutin.getLineName())
                            .setTrainNo(outsourceOutin.getTrainNo())
                            .setReturnTime(returnTime);
                }
            }
        } else {
            // 查不到生成信息并返回
            BuWorkOrderTask workOrderTask = buWorkOrderTaskMapper.selectById(id);
            BuWorkOrder workOrder = buWorkOrderMapper.selectByTaskId(id);
//            String assetTypeId = workOrderTask.getAssetTypeId();
//            String structDetailId = workOrderTask.getStructDetailId();
//            String trainAssetId = workOrderTask.getTrainAssetId();

            // 类型：出库/入库
            Integer outTask = workOrderTask.getOutTask();
            int billType = 0;
            String billTypeName = "出库单";
            if (outTask == 2) {
                billType = 1;
                billTypeName = "入库单";
            }

            List<BuOutsourceOutinDetail> outinDetailList = new ArrayList<>();
            // 暂时不根据工单任务的设备信息设置对应的出入库明显
//            if (billType == 0) {
//                // 类型为出库时，车辆设备转化成出库明细
//                List<BuTrainAsset> trainAssetList = new ArrayList<>();
//                if (StringUtils.isNotBlank(trainAssetId)) {
//                    // 查询车辆设备
//                    BuTrainAsset trainAsset = buTrainAssetDispatchMapper.selectById(trainAssetId);
//                    if (trainAsset != null) {
//                        trainAssetList.add(trainAsset);
//                    }
//                } else {
//                    if (StringUtils.isNotBlank(structDetailId)) {
//                        // 查询该车辆结构明细的车辆设备
//                        trainAssetList = buTrainAssetDispatchMapper.selectAssetListByTrainNoAndStructDetailId(workOrder.getTrainNo(), structDetailId);
//                    } else if (StringUtils.isNotBlank(assetTypeId)) {
//                        // 查询该设备类型的车辆设备
//                        trainAssetList = buTrainAssetDispatchMapper.selectAssetListByTrainNoAndAssetTypeId(workOrder.getTrainNo(), assetTypeId);
//                    }
//                }
//
//                for (BuTrainAsset trainAsset : trainAssetList) {
//                    BuOutsourceOutinDetail outinDetail = new BuOutsourceOutinDetail()
//                            .setId(UUIDGenerator.generate())
//                            .setAssetNo(trainAsset.getAssetNo())
//                            .setAssetName(trainAsset.getAssetName())
//                            .setAmount(1)
//                            .setAssetTypeId(trainAsset.getAssetTypeId())
//                            .setTrainNo(workOrder.getTrainNo())
//                            .setFacadeStatus(0)
//                            .setMixtoolStatus(0)
//                            .setFault(0)
//                            .setReturnStatus(0)
//                            .setLineId(workOrder.getLineId())
//                            .setLineName(workOrder.getLineName())
//                            .setTrainNo(workOrder.getTrainNo())
//                            .setTurnoverAssetId(trainAsset.getId());
//                    outinDetailList.add(outinDetail);
//                }
//            } else {
//                // 类型为入库时，该车辆该设备类型的出库明细转化成入库明细
//                List<BuOutsourceOutinDetail> outDetailList = buOutsourceOutinDetailMapper.selectOutDetailListByTrainNoAndAssetTypeId(workOrder.getTrainNo(), assetTypeId, null);
//                outinDetailList = transformToInDetailList(outDetailList);
//            }

            String outTaskCode = serialNumberGenerate.generateSerialNumberByCode("OutTaskCode");
            // 交接单名默认为：车号+单据类型+日期
            Date date = null == workOrder.getStartTime() ? new Date() : workOrder.getStartTime();
            String outinName = workOrder.getTrainNo() + billTypeName + new SimpleDateFormat("yyyyMMdd").format(date);

            outsourceOutin
                    .setId(UUIDGenerator.generate())
                    .setOrderTaskId(workOrderTask.getId())
                    .setBillNo(outTaskCode)
                    .setBillType(billType)
                    .setOutinName(outinName)
                    .setLineId(workOrder.getLineId())
                    .setLineName(workOrder.getLineName())
                    .setTrainNo(workOrder.getTrainNo())
                    .setSendGroupId(workOrder.getGroupId())
                    .setSendGroupName(workOrder.getGroupName())
                    .setTransferDate(billType == 0 ? workOrder.getStartTime() : workOrder.getFinishTime())
                    .setWorkOrderId(workOrder.getId())
                    .setOrderTaskId(workOrderTask.getId())
                    .setOutinDetails(outinDetailList);
        }

        return outsourceOutin;
    }

    /**
     * @see BuOutsourceOutinService#saveOutsourceOutin(BuOutsourceOutin)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOutsourceOutin(BuOutsourceOutin outin) throws Exception {
        if (StringUtils.isBlank(outin.getBillNo())) {
            String outTaskCode = serialNumberGenerate.generateSerialNumberByCode("OutTaskCode");
            outin.setBillNo(outTaskCode);
        }
        if (StringUtils.isBlank(outin.getId())) {
            outin.setId(UUIDGenerator.generate());
        }
        String outinId = outin.getId();

        // 计算日期
        Date quality = null;
        Integer qualityDay = null;
        if (oConvertUtils.isNotEmpty(outin.getContractId())) {
            Map<String, Object> contract = buOutsourceOutinDetailMapper.selectByContractId(outin.getContractId());
            Object finishDate = contract.get("finishDate");
            if (finishDate == null) {
                finishDate = contract.get("FINISHDATE");
            }
            quality = DataTypeCastUtil.transDate(finishDate);
            Object expirationValue = contract.get("expiration");
            if (expirationValue == null) {
                expirationValue = contract.get("EXPIRATION");
            }
            BigDecimal expiration = DataTypeCastUtil.transNumber(expirationValue);
            qualityDay = null == expiration ? null : expiration.intValue() * 30;
        }

        // 删除委外出入库明细
        LambdaQueryWrapper<BuOutsourceOutinDetail> outinDetailDeleteWrapper = new LambdaQueryWrapper<>();
        outinDetailDeleteWrapper.eq(BuOutsourceOutinDetail::getOutinId, outinId);
        buOutsourceOutinDetailMapper.delete(outinDetailDeleteWrapper);
        // 删除委外单
        buOutsourceOutinMapper.deleteById(outinId);

        // 新增委外单
        buOutsourceOutinMapper.insert(outin);

        // 新增委外出入库明细
        List<BuOutsourceOutinDetail> outinDetailList = outin.getOutinDetails();
        for (BuOutsourceOutinDetail outinDetail : outinDetailList) {
            outinDetail.setOutinId(outinId);
            outinDetail.setQualityDate(quality);
            outinDetail.setQualityDay(qualityDay);
            buOutsourceOutinDetailMapper.insert(outinDetail);
        }

        return true;
    }

    /**
     * @see BuOutsourceOutinService#deleteOutinBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteOutinBatch(String ids) throws Exception {
        List<String> outInIdList = Arrays.asList(ids.split(","));
        if (CollectionUtils.isNotEmpty(outInIdList)) {
            List<List<String>> outInIdBatchSubList = DatabaseBatchSubUtil.batchSubList(outInIdList);
            for (List<String> outInIdBatchSub : outInIdBatchSubList) {
                // 删除委外出入库明细
                LambdaQueryWrapper<BuOutsourceOutinDetail> outinDetailWrapper = new LambdaQueryWrapper<BuOutsourceOutinDetail>()
                        .in(BuOutsourceOutinDetail::getOutinId, outInIdBatchSub);
                buOutsourceOutinDetailMapper.delete(outinDetailWrapper);
                // 删除委外单
                buOutsourceOutinMapper.deleteBatchIds(outInIdBatchSub);
            }
        }

        return true;
    }

    /**
     * @see BuOutsourceOutinService#deleteOutinDetailBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteOutinDetailBatch(String ids) throws Exception {
        List<String> outInDetailIdList = Arrays.asList(ids.split(","));
        if (CollectionUtils.isNotEmpty(outInDetailIdList)) {
            List<List<String>> outInDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(outInDetailIdList);
            for (List<String> outInDetailIdBatchSub : outInDetailIdBatchSubList) {
                // 删除委外出入库明细
                buOutsourceOutinDetailMapper.deleteBatchIds(outInDetailIdBatchSub);
            }
        }

        return true;
    }

    /**
     * @see BuOutsourceOutinService#listNotReturnAssetAsOutinDetail(String, String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuOutsourceOutinDetail> listNotReturnAssetAsOutinDetail(String trainNo, String assetTypeId, String assetName) throws Exception {
        // 查询出库明细和入库明细
        List<BuOutsourceOutinDetail> outDetailList = buOutsourceOutinDetailMapper.selectOutDetailListByTrainNoAndAssetTypeId(trainNo, assetTypeId, assetName);
        List<BuOutsourceOutinDetail> inDetailList = buOutsourceOutinDetailMapper.selectInDetailListByTrainNoAndAssetTypeId(trainNo, assetTypeId, assetName);

        // 过滤未生成入库明细的出库明细
        List<String> outDetailIdList = inDetailList.stream()
                .map(BuOutsourceOutinDetail::getOutDetailId)
                .collect(Collectors.toList());
        List<BuOutsourceOutinDetail> notInOutDetailList = outDetailList.stream()
                .filter(outDetail -> !outDetailIdList.contains(outDetail.getId()))
                .collect(Collectors.toList());

        return transformToInDetailList(notInOutDetailList);
    }

    /**
     * @see BuOutsourceOutinService#repairRecordPage(BuOutsourcePerformQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuOutsourceRepairRecord> repairRecordPage(BuOutsourcePerformQueryVO queryVO, Integer pageNo, Integer pageSize) {
        String contractIds = queryVO.getContractIds();
        if (StringUtils.isNotBlank(contractIds)) {
            String[] split = contractIds.split(",");
            queryVO.setContractIdList(Arrays.asList(split));
        }

        IPage<BuOutsourceRepairRecord> page = buOutsourceOutinMapper.repairRecordPage(new Page<>(pageNo, pageSize), queryVO);
        List<BuOutsourceRepairRecord> recordList = page.getRecords();
        if (oConvertUtils.listIsNotEmpty(recordList)) {
            recordList.forEach(r -> {
                Date returnTime = r.getReturnTime();
                Date actReturnTime = r.getActReturnTime();
                if (Objects.isNull(actReturnTime)) {
                    r.setStatus(2);
                } else {
                    r.setStatus(5);
                }
                if (Objects.nonNull(returnTime)) {
                    if (Objects.nonNull(actReturnTime)) {
                        if (actReturnTime.getTime() - returnTime.getTime() > 0) {
                            setDelayDays(r, actReturnTime, returnTime);
                        }
                    } else {
                        Date now = new Date();
                        if (now.getTime() - returnTime.getTime() > 0) {
                            setDelayDays(r, now, returnTime);
                        }
                    }
                }
            });
        }

        return page;
    }

    /**
     * @see BuOutsourceOutinService#settingDelayReason(DelayReasonVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean settingDelayReason(DelayReasonVO delayReason) {
        buOutsourceOutinMapper.settingDelayReason(delayReason);

        return true;
    }


    private void setDelayDays(BuOutsourceRepairRecord record, Date start, Date end) {
        int days = (int) ((end.getTime() - start.getTime()) / (1000 * 3600 * 24));
        record.setStatus(6);
        record.setDelayDays(Math.abs(days));
    }

    private void setDelayDays(BuOutsourceRepairRecord r, Date returnTime, Date nowTime, boolean flag) {
        int days = (int) ((nowTime.getTime() - returnTime.getTime()) / (1000 * 3600 * 24));
        if (days <= 0) {
            r.setDelayDays(0);
            if (flag) {
                r.setStatus(5);
            }
        } else {
            r.setDelayDays(days);
            r.setStatus(6);
        }
    }

    private void setUserName(BuOutsourceOutin outsourceOutin) {
        if (outsourceOutin == null) {
            return;
        }

        Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

        Integer billType = outsourceOutin.getBillType();
        if (billType == 0) {
            // 出库单接收人存的name
            outsourceOutin.setReceiveUserName(outsourceOutin.getReceiveUser());
            outsourceOutin.setTransferUserName(userIdNameMap.get(outsourceOutin.getTransferUserId()));
        } else if (billType == 1) {
            // 入库单移交人存的name
            outsourceOutin.setTransferUserName(outsourceOutin.getTransferUserId());
            outsourceOutin.setReceiveUserName(userIdNameMap.get(outsourceOutin.getReceiveUser()));
        }
        outsourceOutin.setEngineerName(userIdNameMap.get(outsourceOutin.getEngineerId()));
    }

    private List<BuOutsourceOutinDetail> transformToInDetailList(List<BuOutsourceOutinDetail> outDetailList) {
        List<BuOutsourceOutinDetail> inDetailList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(outDetailList)) {
            for (BuOutsourceOutinDetail outDetail : outDetailList) {
                Integer needDay = outDetail.getNeedDay();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(outDetail.getTransferDate());
                calendar.add(Calendar.DATE, null == needDay ? 0 : needDay);
                Date returnTime = calendar.getTime();
                outDetail.setReturnTime(returnTime);

                BuOutsourceOutinDetail inDetail = new BuOutsourceOutinDetail();
                BeanUtils.copyProperties(outDetail, inDetail);
                inDetail.setId(UUIDGenerator.generate())
                        .setFacadeStatus(0)
                        .setMixtoolStatus(0)
                        .setReturnStatus(0)
                        .setOutDetailId(outDetail.getId());
                inDetailList.add(inDetail);
            }
        }

        return inDetailList;
    }

}
