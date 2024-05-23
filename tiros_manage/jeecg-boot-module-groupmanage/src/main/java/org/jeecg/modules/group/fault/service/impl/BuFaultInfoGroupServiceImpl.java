package org.jeecg.modules.group.fault.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.rpt.service.AlertRecordService;
import org.jeecg.common.tiros.rpt.service.FaultRptService;
import org.jeecg.common.tiros.rpt.service.HomepageItemRptService;
import org.jeecg.common.tiros.rpt.service.KpiRptService;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.third.maximo.bean.BuMaximoTransData;
import org.jeecg.common.tiros.third.maximo.service.BuMaximoTransDataService;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.workflow.bean.vo.StartVO;
import org.jeecg.common.workflow.constant.WfConstant;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.jeecg.modules.group.fault.bean.BuFaultCodeNew;
import org.jeecg.modules.group.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.group.fault.bean.BuFaultInfo;
import org.jeecg.modules.group.fault.bean.BuFaultInfoAnnex;
import org.jeecg.modules.group.fault.bean.vo.BuFaultInfoQueryVO;
import org.jeecg.modules.group.fault.mapper.*;
import org.jeecg.modules.group.fault.service.BuFaultInfoGroupService;
import org.jeecg.modules.group.measurealert.bean.BuPlanFormValues;
import org.jeecg.modules.group.measurealert.mapper.BuPlanFormValuesGroupMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/12
 */
@Slf4j
@Service
public class BuFaultInfoGroupServiceImpl extends ServiceImpl<BuFaultInfoGroupMapper, BuFaultInfo> implements BuFaultInfoGroupService {

    @Resource
    private BuFaultInfoGroupMapper buFaultInfoGroupMapper;
    @Resource
    private BuFaultHandleRecordGroupMapper buFaultHandleRecordGroupMapper;
    @Resource
    private BuFaultInfoAnnexGroupMapper buFaultInfoAnnexGroupMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;
    @Resource
    private BuTrainAssetGroupMapper buTrainAssetGroupMapper;
    @Resource
    private BuFaultCodeNewGroupMapper buFaultCodeNewGroupMapper;
    @Resource
    private BuPlanFormValuesGroupMapper buPlanFormValuesGroupMapper;
    //    @Resource
//    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private HomepageItemRptService homepageItemRptService;
    @Resource
    private AlertRecordService alertRecordService;
    @Resource
    private WorkflowForwardService workflowForwardService;
    @Resource
    private BuMaximoTransDataService buMaximoTransDataService;
    @Resource
    private FaultRptService faultRptService;


    /**
     * @see BuFaultInfoGroupService#page(BuFaultInfoQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public IPage<BuFaultInfo> page(BuFaultInfoQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuFaultInfo> page = buFaultInfoGroupMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

        List<BuFaultInfo> faultInfoList = page.getRecords();
        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            for (BuFaultInfo faultInfo : faultInfoList) {
                faultInfo.setReportUserName(userIdNameMap.get(faultInfo.getReportUserId()));
                faultInfo.setDutyEngineerName(userIdNameMap.get(faultInfo.getDutyEngineer()));
                faultInfo.setCloseUserName(userIdNameMap.get(faultInfo.getCloseUserId()));
            }
        }

        return page;
    }

    /**
     * @see BuFaultInfoGroupService#getFaultById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuFaultInfo getFaultById(String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoGroupMapper.selectFaultById(id);

        Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

        if (null != faultInfo) {
            faultInfo.setReportUserName(userIdNameMap.get(faultInfo.getReportUserId()));
            faultInfo.setDutyEngineerName(userIdNameMap.get(faultInfo.getDutyEngineer()));
            faultInfo.setCloseUserName(userIdNameMap.get(faultInfo.getCloseUserId()));

            List<BuFaultHandleRecord> handleRecordList = faultInfo.getHandleRecordList();
            if (CollectionUtils.isNotEmpty(handleRecordList)) {
                handleRecordList.sort(Comparator.comparing(BuFaultHandleRecord::getSolutionTime, Comparator.nullsLast(Comparator.naturalOrder())));
                faultInfo.setHandleRecordList(handleRecordList.subList(0, 1));
            }
        }

        return faultInfo;
    }

    /**
     * @see BuFaultInfoGroupService#listHandleRecordById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultHandleRecord> listHandleRecordById(String id) throws Exception {
        return buFaultInfoGroupMapper.selectHandleRecordListByFaultId(id);
    }

    /**
     * @see BuFaultInfoGroupService#saveAndSubmitFaultInfo(BuFaultInfo)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveAndSubmitFaultInfo(BuFaultInfo buFaultInfo) throws Exception {
        // 生成故障编码
        buFaultInfo.setFaultSn(serialNumberGenerate.generateSerialNumberByCode("JDXGZ"));
        // 保存故障信息
        buFaultInfo.setReportTime(new Date());
        // 提交状态设置为1已提交
        buFaultInfo.setSubmitStatus(1);
        buFaultInfoGroupMapper.insert(buFaultInfo);
        String faultInfoId = buFaultInfo.getId();

        // 保存处理信息
        List<BuFaultHandleRecord> handleRecordList = buFaultInfo.getHandleRecordList();
        if (CollectionUtils.isNotEmpty(handleRecordList)) {
            for (BuFaultHandleRecord buFaultHandleRecord : handleRecordList) {
                buFaultHandleRecord.setFaultInfoId(faultInfoId);
                buFaultHandleRecordGroupMapper.insert(buFaultHandleRecord);
            }
        }
        // 保存附件
        List<BuFaultInfoAnnex> annexList = buFaultInfo.getAnnexList();
        if (CollectionUtils.isNotEmpty(annexList)) {
            for (BuFaultInfoAnnex buFaultInfoAnnex : annexList) {
                buFaultInfoAnnex.setFaultId(faultInfoId);
                buFaultInfoAnnexGroupMapper.insert(buFaultInfoAnnex);
            }
        }

        // 新增故障时，如果字段form_value_id有值，则需要将bu_pl_data_record_value表中id为该值的记录状态改成2已转故障
        if (StringUtils.isNotBlank(buFaultInfo.getFormValueId())) {
            String formValueId = buFaultInfo.getFormValueId();
            BuPlanFormValues formValues = new BuPlanFormValues().setId(formValueId).setStatus(2);
            buPlanFormValuesGroupMapper.updateById(formValues);
        }

        // 提交故障后启动故障处理流程
        startFaultWorkflow(Collections.singletonList(buFaultInfo));

        return true;
    }

    /**
     * @see BuFaultInfoGroupService#updateFaultInfo(BuFaultInfo)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateFaultInfo(BuFaultInfo fault) throws Exception {
        String faultInfoId = fault.getId();

        // 查询故障，已提交的不能修改
        BuFaultInfo dbBuFaultInfo = buFaultInfoGroupMapper.selectById(faultInfoId);
        if (null == dbBuFaultInfo) {
            throw new JeecgBootException("故障不存在");
        }
        if (dbBuFaultInfo.getSubmitStatus() == 1) {
            throw new JeecgBootException("已提交的故障不能修改，故障编号=" + dbBuFaultInfo.getFaultSn());
        }

        // 删除原来的处理信息
        LambdaQueryWrapper<BuFaultHandleRecord> handleRecordWrapper = new LambdaQueryWrapper<>();
        handleRecordWrapper.eq(BuFaultHandleRecord::getFaultInfoId, faultInfoId);
        buFaultHandleRecordGroupMapper.delete(handleRecordWrapper);
        // 删除原来的附件
        LambdaQueryWrapper<BuFaultInfoAnnex> annexWrapper = new LambdaQueryWrapper<>();
        annexWrapper.eq(BuFaultInfoAnnex::getFaultId, faultInfoId);
        buFaultInfoAnnexGroupMapper.delete(annexWrapper);

        // 更新故障信息
//        setSystemByAsset(buFaultInfo, false);
        // 使用新资产结构作为部件，系统根据故障代码来设置
        setSystemByFaultCode(fault);
        buFaultInfoGroupMapper.updateById(fault);

        // 保存处理信息
        List<BuFaultHandleRecord> handleRecordList = fault.getHandleRecordList();
        for (BuFaultHandleRecord buFaultHandleRecord : handleRecordList) {
            buFaultHandleRecord.setFaultInfoId(faultInfoId);
            buFaultHandleRecordGroupMapper.insert(buFaultHandleRecord);
        }
        // 保存附件
        List<BuFaultInfoAnnex> annexList = fault.getAnnexList();
        for (BuFaultInfoAnnex buFaultInfoAnnex : annexList) {
            buFaultInfoAnnex.setFaultId(faultInfoId);
            buFaultInfoAnnexGroupMapper.insert(buFaultInfoAnnex);
        }

        // 重新统计故障统计数据
        faultRptService.rebuildFaultRpt(null == fault.getTrainNo() ? null : Collections.singletonList(fault.getTrainNo()));

        return true;
    }

    /**
     * @see BuFaultInfoGroupService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> faultIdList = Arrays.asList(ids.split(","));

        // 查询故障，已提交/已处理/已关闭的不能删除
        List<BuFaultInfo> faultList = buFaultInfoGroupMapper.selectBatchIds(faultIdList);
        for (BuFaultInfo buFaultInfo : faultList) {
            if (buFaultInfo.getSubmitStatus() == 1) {
                throw new JeecgBootException("已提交的故障不能删除，故障编号=" + buFaultInfo.getFaultSn());
            }
            if (buFaultInfo.getStatus() == 1) {
                throw new JeecgBootException("已处理的故障不能删除，故障编号=" + buFaultInfo.getFaultSn());
            }
            if (buFaultInfo.getStatus() == 2) {
                throw new JeecgBootException("已关闭的故障不能删除，故障编号=" + buFaultInfo.getFaultSn());
            }
        }

        // 删除处理信息
        LambdaQueryWrapper<BuFaultHandleRecord> handleRecordWrapper = new LambdaQueryWrapper<>();
        handleRecordWrapper.in(BuFaultHandleRecord::getFaultInfoId, faultIdList);
        buFaultHandleRecordGroupMapper.delete(handleRecordWrapper);
        // 删除附件
        LambdaQueryWrapper<BuFaultInfoAnnex> annexWrapper = new LambdaQueryWrapper<>();
        annexWrapper.in(BuFaultInfoAnnex::getFaultId, faultIdList);
        buFaultInfoAnnexGroupMapper.delete(annexWrapper);

        // 删除故障信息
        buFaultInfoGroupMapper.deleteBatchIds(faultIdList);

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        // 删除故障处理预警
        alertRecordService.deleteAlertRecord(8, faultIdList);

        // 重新统计故障统计数据
        List<String> trainNoList = faultList.stream()
                .map(BuFaultInfo::getTrainNo)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        faultRptService.rebuildFaultRpt(trainNoList);

        return true;
    }

    /**
     * @see BuFaultInfoGroupService#cancelBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelBatch(String ids) throws Exception {
        List<String> faultIdList = Arrays.asList(ids.split(","));

        // 查询故障，已处理/已关闭的不能取消
        List<BuFaultInfo> faultList = buFaultInfoGroupMapper.selectBatchIds(faultIdList);
        for (BuFaultInfo buFaultInfo : faultList) {
            if (buFaultInfo.getSubmitStatus() == 1) {
                throw new JeecgBootException("已提交的故障不能取消，故障编号=" + buFaultInfo.getFaultSn());
            }
            if (buFaultInfo.getStatus() == 1) {
                throw new JeecgBootException("已处理的故障不能取消，故障编号=" + buFaultInfo.getFaultSn());
            }
            if (buFaultInfo.getStatus() == 2) {
                throw new JeecgBootException("已关闭的故障不能取消，故障编号=" + buFaultInfo.getFaultSn());
            }
        }

        // 取消故障信息
        for (BuFaultInfo buFaultInfo : faultList) {
            buFaultInfo.setSubmitStatus(2);
            buFaultInfoGroupMapper.updateById(buFaultInfo);
        }

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        // 删除故障处理预警
        alertRecordService.deleteAlertRecord(8, faultIdList);

        // 重新统计故障统计数据
        List<String> trainNoList = faultList.stream()
                .map(BuFaultInfo::getTrainNo)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        faultRptService.rebuildFaultRpt(trainNoList);

        return true;
    }

    /**
     * @see BuFaultInfoGroupService#submitFault(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitFault(String ids) throws Exception {
        Date now = new Date();

        List<String> faultIdList = Arrays.asList(ids.split(","));

        // 查询故障，已提交的不能再次提交
        List<BuFaultInfo> faultList = buFaultInfoGroupMapper.selectBatchIds(faultIdList);
        for (BuFaultInfo dbBuFaultInfo : faultList) {
            if (dbBuFaultInfo.getSubmitStatus() == 1) {
                throw new JeecgBootException("已提交的故障不能再次提交，故障编号=" + dbBuFaultInfo.getFaultSn());
            }

            dbBuFaultInfo.setSubmitStatus(1);
            buFaultInfoGroupMapper.updateById(dbBuFaultInfo);
        }

        // 工班的故障信息，提交后，发送消息通知给调度角色的人员
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sendMessageByNewTread(faultList, sysUser);

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        // 故障关闭才统计信息
//        for (String faultId : faultIdList) {
//            // 故障统计对应数量+1
//            faultRptService.increaseFaultRpt(faultId);
//            // 故障数绩效统计对应数量+1
//            kpiRptService.increaseFaultKpiByFault(faultId);
//        }

        // 提交故障后启动故障处理流程
        startFaultWorkflow(faultList);

        // 添加数据到maximo同步中间表
        addMaximoTransDataOfAddFault(faultIdList, now);

        // 重新统计故障统计数据
        List<String> trainNoList = faultList.stream()
                .map(BuFaultInfo::getTrainNo)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        faultRptService.rebuildFaultRpt(trainNoList);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveFaultInfo(BuFaultInfo buFaultInfo) throws Exception {
        // 生成故障编码
        buFaultInfo.setFaultSn(serialNumberGenerate.generateSerialNumberByCode("JDXGZ"));
        // 设置故障来源
        buFaultInfo.setFromType(1);
        // 设置系统子系统
        setSystemByFaultCode(buFaultInfo);
        // 保存故障信息
        buFaultInfoGroupMapper.insert(buFaultInfo);
        String faultInfoId = buFaultInfo.getId();

        // 保存处理信息
        List<BuFaultHandleRecord> handleRecordList = buFaultInfo.getHandleRecordList();
        if (CollectionUtils.isNotEmpty(handleRecordList)) {
            for (BuFaultHandleRecord buFaultHandleRecord : handleRecordList) {
                buFaultHandleRecord.setFaultInfoId(faultInfoId);
                buFaultHandleRecordGroupMapper.insert(buFaultHandleRecord);
            }
        }
        // 保存附件
        List<BuFaultInfoAnnex> annexList = buFaultInfo.getAnnexList();
        if (CollectionUtils.isNotEmpty(annexList)) {
            for (BuFaultInfoAnnex buFaultInfoAnnex : annexList) {
                buFaultInfoAnnex.setFaultId(faultInfoId);
                buFaultInfoAnnexGroupMapper.insert(buFaultInfoAnnex);
            }
        }

        // 新增故障时，如果字段form_value_id有值，则需要将bu_pl_data_record_value表中id为该值的记录状态改成2已转故障
        if (StringUtils.isNotBlank(buFaultInfo.getFormValueId())) {
            String formValueId = buFaultInfo.getFormValueId();
            BuPlanFormValues formValues = new BuPlanFormValues().setId(formValueId).setStatus(2);
            buPlanFormValuesGroupMapper.updateById(formValues);
        }

        return true;
    }


//    private void setSystemByAsset(BuFaultInfo faultInfo, boolean isAdd) {
//        if (StringUtils.isBlank(faultInfo.getFaultAssetId())) {
//            return;
//        }
//
//        BuTrainAsset asset = buTrainAssetGroupMapper.selectById(faultInfo.getFaultAssetId());
//        if (null == asset) {
//            return;
//        }
//
//        // 设置系统、子系统信息
//        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = assetTypeCacheService.map(null);
//        BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOMap.get(asset.getAssetTypeId());
//        if (null != assetTypeBO) {
//            faultInfo.setSysId(assetTypeBO.getSysId())
//                    .setSysName(assetTypeBO.getSysName())
//                    .setSubSysId(assetTypeBO.getSubSysId())
//                    .setSubSysName(assetTypeBO.getSubSysName());
//        }
//    }

    private void setSystemByFaultCode(BuFaultInfo faultInfo) {
        if (StringUtils.isBlank(faultInfo.getFaultCodeId())) {
            throw new JeecgBootException("请选择故障代码");
        }

        BuFaultCodeNew faultCodeNew = buFaultCodeNewGroupMapper.selectById(faultInfo.getFaultCodeId());
        if (null == faultCodeNew) {
            throw new JeecgBootException("故障代码不存在");
        }

        String fltCode = faultCodeNew.getFltCode();
        // 这里的故障代码就等于部件编码
        // 例如 011401007 按2/2/2/3拆分为01 14 01 007：第一个01表示专业（01=电客车），第二个14表示系统，第三个01表示子系统，第四个007表示部件
        // 专业、系统、子系统、部件
//        String fltMajorCode = fltCode.substring(0, 2);
        String sysLevelCode = fltCode.substring(2, 4);
        String subSysLevelCode = fltCode.substring(4, 6);
//        String assetLevelCode = fltCode.substring(6);

        String sysAssetTypeCode = "0" + sysLevelCode;
        String subSysAssetTypeCode = sysAssetTypeCode + "0" + subSysLevelCode;
//        String assetAssetTypeCode = subSysAssetTypeCode + assetLevelCode;

        // 设置系统、子系统id
        String sysId = buTrainAssetGroupMapper.selectAssetTypeIdByCode(sysAssetTypeCode);
        if (StringUtils.isNotBlank(sysId)) {
            faultInfo.setSysId(sysId);
        }
        String subSysId = buTrainAssetGroupMapper.selectAssetTypeIdByCode(subSysAssetTypeCode);
        if (StringUtils.isNotBlank(subSysId)) {
            faultInfo.setSubSysId(subSysId);
        }
    }

    private void sendMessageByNewTread(List<BuFaultInfo> faultInfoList, LoginUser sysUser) {
        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                List<String> userNameList = sysBaseAPI.listUsernameByTypeAndParam(2, Collections.singletonList("jxdd"));
                if (CollectionUtils.isNotEmpty(userNameList)) {
                    String usernames = String.join(",", userNameList);
                    for (BuFaultInfo faultInfo : faultInfoList) {
                        String title = "新的故障：" + faultInfo.getFaultSn();
                        String content = "故障描述：" + faultInfo.getFaultDesc();

                        sysBaseAPI.sendSysAnnouncement(sysUser.getUsername(), usernames, title, content);
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

    private void startFaultWorkflow(List<BuFaultInfo> faultList) throws Exception {
        if (CollectionUtils.isEmpty(faultList)) {
            return;
        }

        for (BuFaultInfo fault : faultList) {
            Map<String, Object> variables = new HashMap<>(2);
            variables.put("groupId", fault.getReportGroupId());
            variables.put("businessCode", fault.getFaultSn());

            StartVO startVO = new StartVO()
                    .setBusinessKey(fault.getId())
                    .setSolutionCode(WfConstant.SOLUTION_CODE_FAULT)
                    .setTitle("故障：" + fault.getFaultDesc())
                    .setVariables(variables);
            workflowForwardService.startSolution(startVO);
        }
    }

    private void addMaximoTransDataOfAddFault(List<String> faultIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(faultIdList)) {
            return;
        }

        List<BuFaultInfo> faultList = buFaultInfoGroupMapper.selectListForMaximoByIdList(faultIdList);

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
    }

}
