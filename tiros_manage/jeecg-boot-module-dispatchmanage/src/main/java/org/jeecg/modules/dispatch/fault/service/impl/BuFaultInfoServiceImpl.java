package org.jeecg.modules.dispatch.fault.service.impl;

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
import org.jeecg.common.aspect.annotation.ExcelImport;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.tiros.common.mapper.BaseCommonMapper;
import org.jeecg.common.tiros.rpt.service.AlertRecordService;
import org.jeecg.common.tiros.rpt.service.FaultRptService;
import org.jeecg.common.tiros.rpt.service.HomepageItemRptService;
import org.jeecg.common.tiros.rpt.service.KpiRptService;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.third.maximo.bean.BuMaximoTransData;
import org.jeecg.common.tiros.third.maximo.service.BuMaximoTransDataService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeNew;
import org.jeecg.modules.dispatch.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.dispatch.fault.bean.BuFaultInfo;
import org.jeecg.modules.dispatch.fault.bean.BuFaultInfoAnnex;
import org.jeecg.modules.dispatch.fault.bean.bo.FaultImportBO;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultInfoQueryVO;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultCodeNewMapper;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultHandleRecordMapper;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultInfoAnnexMapper;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultInfoMapper;
import org.jeecg.modules.dispatch.fault.service.BuFaultInfoService;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormValues;
import org.jeecg.modules.dispatch.planform.mapper.BuPlanFormValuesMapper;
import org.jeecg.modules.dispatch.serialplan.mapper.BuTrainAssetDispatchMapper;
import org.jeecg.modules.dispatch.workorder.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.dispatch.workorder.mapper.BuMtrWorkshopGroupDispatchMapper;
import org.jeecgframework.poi.util.ExcelUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Slf4j
@Service
public class BuFaultInfoServiceImpl extends ServiceImpl<BuFaultInfoMapper, BuFaultInfo> implements BuFaultInfoService {

    @Resource
    private BuFaultInfoMapper buFaultInfoMapper;
    @Resource
    private BuFaultHandleRecordMapper buFaultHandleRecordMapper;
    @Resource
    private BuFaultInfoAnnexMapper buFaultInfoAnnexMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;
    @Resource
    private BuTrainAssetDispatchMapper buTrainAssetDispatchMapper;
    @Resource
    private BuFaultCodeNewMapper buFaultCodeNewMapper;
    @Resource
    private BuPlanFormValuesMapper buPlanFormValuesMapper;
    @Resource
    private BuMtrWorkshopGroupDispatchMapper buMtrWorkshopGroupDispatchMapper;
    @Resource
    private BaseCommonMapper baseCommonMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private BuMaximoTransDataService buMaximoTransDataService;
    @Resource
    private HomepageItemRptService homepageItemRptService;
    @Resource
    private FaultRptService faultRptService;
    @Resource
    private KpiRptService kpiRptService;
    @Resource
    private AlertRecordService alertRecordService;
    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see BuFaultInfoService#page(BuFaultInfoQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFaultInfo> page(BuFaultInfoQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        queryVO.toStartEndDate();

        IPage<BuFaultInfo> page = buFaultInfoMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

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
     * @see BuFaultInfoService#getFaultById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuFaultInfo getFaultById(String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoMapper.selectFaultById(id);

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
     * @see BuFaultInfoService#listHandleRecordById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultHandleRecord> listHandleRecordById(String id) throws Exception {
        return buFaultInfoMapper.selectHandleRecordListByFaultId(id);
    }

    /**
     * @see BuFaultInfoService#saveFaultInfo(BuFaultInfo)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveFaultInfo(BuFaultInfo fault) throws Exception {
        Date now = new Date();

        // 生成故障编码
        fault.setFaultSn(serialNumberGenerate.generateSerialNumberByCode("JDXGZ"));
        // 设置故障来源
        fault.setFromType(1);
        // 保存故障信息
        // buFaultInfo.setReportTime(new Date());
//        // 此处不需要设置提交状态了，因为前端已经做过判断：调度途径新增的，提交状态为已提交；工班途径新增的，提交状态为未提交
//        buFaultInfo.setSubmitStatus(1);
//        setSystemByAsset(buFaultInfo, true);
        // 使用新资产结构作为部件，系统根据故障代码来设置
        setSystemByFaultCode(fault);
        // 设置车间id
        if (StringUtils.isBlank(fault.getWorkshopId())) {
            BuMtrWorkshopGroup group = buMtrWorkshopGroupDispatchMapper.selectWorkshopGroupById(fault.getReportGroupId());
            if (null != group) {
                fault.setWorkshopId(group.getWorkshopId())
                        .setCompanyId(group.getCompanyId());
            }
        }
        buFaultInfoMapper.insert(fault);

        String faultInfoId = fault.getId();

        // 保存处理信息
        List<BuFaultHandleRecord> handleRecordList = fault.getHandleRecordList();
        if (CollectionUtils.isNotEmpty(handleRecordList)) {
            for (BuFaultHandleRecord buFaultHandleRecord : handleRecordList) {
                buFaultHandleRecord.setFaultInfoId(faultInfoId);
                buFaultHandleRecordMapper.insert(buFaultHandleRecord);
            }
        }
        // 保存附件
        List<BuFaultInfoAnnex> annexList = fault.getAnnexList();
        if (CollectionUtils.isNotEmpty(annexList)) {
            for (BuFaultInfoAnnex buFaultInfoAnnex : annexList) {
                buFaultInfoAnnex.setFaultId(faultInfoId);
                buFaultInfoAnnexMapper.insert(buFaultInfoAnnex);
            }
        }

        // 新增故障时，如果字段form_value_id有值，则需要将bu_pl_data_record_value表中id为该值的记录状态改成2已转故障
        if (StringUtils.isNotBlank(fault.getFormValueId())) {
            String formValueId = fault.getFormValueId();
            BuPlanFormValues formValues = new BuPlanFormValues().setId(formValueId).setStatus(2);
            buPlanFormValuesMapper.updateById(formValues);
        }

        if (null != fault.getSubmitStatus() && 1 == fault.getSubmitStatus()) {
            // 添加数据到maximo同步中间表
            addMaximoTransDataOfAddFault(Collections.singletonList(fault.getId()), now);
        }
        // 故障关闭才统计信息
//        // 只有已提交的故障才增加对应数量
//        if (null != buFaultInfo.getSubmitStatus() && 1 == buFaultInfo.getSubmitStatus()) {
//            // 更新首页数据区数据
//            homepageItemRptService.rewriteDataItem();
//            // 故障统计对应数量+1
//            faultRptService.increaseFaultRpt(faultInfoId);
//            // 故障数绩效统计对应数量+1
//            kpiRptService.increaseFaultKpiByFault(faultInfoId);
//        }

        // 重新统计故障统计数据
        faultRptService.rebuildFaultRpt(null == fault.getTrainNo() ? null : Collections.singletonList(fault.getTrainNo()));

        return true;
    }

    /**
     * @see BuFaultInfoService#updateFaultInfo(BuFaultInfo)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateFaultInfo(BuFaultInfo fault) throws Exception {
        String faultInfoId = fault.getId();

        // 故障关闭才统计信息
//        // 只有已提交的故障才增加对应数量
//        if (null != buFaultInfo.getSubmitStatus() && 1 == buFaultInfo.getSubmitStatus()) {
//            // 故障统计对应数量-1
//            faultRptService.decreaseFaultRpt(faultInfoId);
//            // 绩效统计对应数量-1
//            kpiRptService.decreaseFaultKpiByFault(faultInfoId);
//        }

        // 删除原来的处理信息
        LambdaQueryWrapper<BuFaultHandleRecord> handleRecordWrapper = new LambdaQueryWrapper<>();
        handleRecordWrapper.eq(BuFaultHandleRecord::getFaultInfoId, faultInfoId);
        buFaultHandleRecordMapper.delete(handleRecordWrapper);
        // 删除原来的附件
        LambdaQueryWrapper<BuFaultInfoAnnex> annexWrapper = new LambdaQueryWrapper<>();
        annexWrapper.eq(BuFaultInfoAnnex::getFaultId, faultInfoId);
        buFaultInfoAnnexMapper.delete(annexWrapper);

        // 更新故障信息
//        setSystemByAsset(buFaultInfo, false);
        // 使用新资产结构作为部件，系统根据故障代码来设置
        setSystemByFaultCode(fault);
        buFaultInfoMapper.updateById(fault);

        // 保存处理信息
        List<BuFaultHandleRecord> handleRecordList = fault.getHandleRecordList();
        for (BuFaultHandleRecord buFaultHandleRecord : handleRecordList) {
            buFaultHandleRecord.setFaultInfoId(faultInfoId);
            buFaultHandleRecordMapper.insert(buFaultHandleRecord);
        }
        // 保存附件
        List<BuFaultInfoAnnex> annexList = fault.getAnnexList();
        for (BuFaultInfoAnnex buFaultInfoAnnex : annexList) {
            buFaultInfoAnnex.setFaultId(faultInfoId);
            buFaultInfoAnnexMapper.insert(buFaultInfoAnnex);
        }

        // 故障关闭才统计信息
//        // 只有已提交的故障才增加对应数量
//        if (null != buFaultInfo.getSubmitStatus() && 1 == buFaultInfo.getSubmitStatus()) {
//            // 故障统计对应数量+1
//            faultRptService.increaseFaultRpt(faultInfoId);
//            // 故障数绩效统计对应数量+1
//            kpiRptService.increaseFaultKpiByFault(faultInfoId);
//        }

        // 重新统计故障统计数据
        faultRptService.rebuildFaultRpt(null == fault.getTrainNo() ? null : Collections.singletonList(fault.getTrainNo()));

        return true;
    }

    /**
     * @see BuFaultInfoService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        Date now = new Date();

        List<String> faultIdList = Arrays.asList(ids.split(","));

        // 查询故障，已处理/已关闭的不能删除
        List<BuFaultInfo> faultList = buFaultInfoMapper.selectBatchIds(faultIdList);
        for (BuFaultInfo fault : faultList) {
            if (fault.getStatus() == 1) {
                throw new JeecgBootException("已处理的故障不能删除，故障编号=" + fault.getFaultSn());
            }
            if (fault.getStatus() == 2) {
                throw new JeecgBootException("已关闭的故障不能删除，故障编号=" + fault.getFaultSn());
            }

            // 故障关闭才统计信息
//            // 故障统计对应数量-1
//            faultRptService.decreaseFaultRpt(fault.getId());
//            // 绩效统计对应数量-1
//            kpiRptService.decreaseFaultKpiByFault(fault.getId());
        }

        // 添加数据到maximo同步中间表
        addMaximoTransDataOfDeleteFault(faultIdList, now);

        // 删除处理信息
        LambdaQueryWrapper<BuFaultHandleRecord> handleRecordWrapper = new LambdaQueryWrapper<>();
        handleRecordWrapper.in(BuFaultHandleRecord::getFaultInfoId, faultIdList);
        buFaultHandleRecordMapper.delete(handleRecordWrapper);
        // 删除附件
        LambdaQueryWrapper<BuFaultInfoAnnex> annexWrapper = new LambdaQueryWrapper<>();
        annexWrapper.in(BuFaultInfoAnnex::getFaultId, faultIdList);
        buFaultInfoAnnexMapper.delete(annexWrapper);

        // 删除故障信息
        buFaultInfoMapper.deleteBatchIds(faultIdList);

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
     * @see BuFaultInfoService#cancelBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelBatch(String ids) throws Exception {
        Date now = new Date();

        List<String> faultIdList = Arrays.asList(ids.split(","));

        // 查询故障，已处理/已关闭的不能取消
        List<BuFaultInfo> faultList = buFaultInfoMapper.selectBatchIds(faultIdList);
        for (BuFaultInfo fault : faultList) {
            if (fault.getStatus() == 1) {
                throw new JeecgBootException("已处理的故障不能取消，故障编号=" + fault.getFaultSn());
            }
            if (fault.getStatus() == 2) {
                throw new JeecgBootException("已关闭的故障不能取消，故障编号=" + fault.getFaultSn());
            }

            // 故障关闭才统计信息
//            // 故障统计对应数量-1
//            faultRptService.decreaseFaultRpt(fault.getId());
//            // 绩效统计对应数量-1
//            kpiRptService.decreaseFaultKpiByFault(fault.getId());
        }

        // 取消故障信息
        for (BuFaultInfo buFaultInfo : faultList) {
            buFaultInfo.setSubmitStatus(2);
            buFaultInfoMapper.updateById(buFaultInfo);
        }

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        // 删除故障处理预警
        alertRecordService.deleteAlertRecord(8, faultIdList);

        // 添加数据到maximo同步中间表
        addMaximoTransDataOfReplaceFault(faultIdList, now);

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
     * @see BuFaultInfoService#closeBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean closeBatch(String ids) throws Exception {
        Date now = new Date();

        List<String> faultIdList = Arrays.asList(ids.split(","));

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        List<BuFaultInfo> faultList = buFaultInfoMapper.selectBatchIds(faultIdList);

        // 更新故障状态
//        BuFaultInfo closeFault = new BuFaultInfo()
//                .setCloseTime(now)
//                .setCloseUserId(userId)
//                .setStatus(2);
//        LambdaQueryWrapper<BuFaultInfo> closeWrapper = new LambdaQueryWrapper<BuFaultInfo>()
//                .in(BuFaultInfo::getId, faultIdList);
//        buFaultInfoMapper.update(closeFault, closeWrapper);
        for (BuFaultInfo faultInfo : faultList) {
            if (faultInfo.getFaultSn().contains("LS")) {
                if (null == faultInfo.getCloseTime()) {
                    Date reportTime = faultInfo.getReportTime();
                    // 设置为提报时间2小时后
                    faultInfo.setCloseTime(new Date(reportTime.getTime() + 7200000));
                }
                if (StringUtils.isBlank(faultInfo.getCloseUserId()) || "-".equals(faultInfo.getCloseUserId())) {
                    faultInfo.setCloseUserId(userId);
                }
                faultInfo.setStatus(2);
            } else {
                faultInfo.setCloseTime(now)
                        .setCloseUserId(userId)
                        .setStatus(2);
            }
        }
        buFaultInfoMapper.updateListCloseInfo(faultList);

        // 添加数据到maximo同步中间表
        addMaximoTransDataOfReplaceFault(faultIdList, now);

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        // 故障关闭才统计绩效
        for (String faultId : faultIdList) {
//            // 故障统计对应数量+1
//            faultRptService.increaseFaultRpt(faultId);
            // 故障数绩效统计对应数量+1
            kpiRptService.increaseFaultKpiByFault(faultId);
        }
        // 重新统计故障统计数据
        List<String> trainNoList = faultList.stream()
                .map(BuFaultInfo::getTrainNo)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        faultRptService.rebuildFaultRpt(trainNoList);

        // 设置故障处理预警为已处理
        alertRecordService.setAlertRecordHandled(8, faultIdList);

        return true;
    }

    /**
     * @see BuFaultInfoService#importFaultHistory(MultipartFile, int)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importFaultHistory(MultipartFile excelFile, int type) throws Exception {
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

        // 线路，根据文件名获取
        String lineId = filename.substring(0, filename.indexOf("号"));
        // 查询设备类型系统
        Map<String, BuTrainAssetTypeBO> idSysBOMap = assetTypeCacheService.mapSys(null);
        Map<String, String> systemNameIdMap = new HashMap<>();
        Map<String, String> systemShortNameIdMap = new HashMap<>();
        for (Map.Entry<String, BuTrainAssetTypeBO> idSysBOEntry : idSysBOMap.entrySet()) {
            BuTrainAssetTypeBO sysBO = idSysBOEntry.getValue();
            systemNameIdMap.put(sysBO.getName(), sysBO.getId());
            systemShortNameIdMap.put(sysBO.getShortName(), sysBO.getId());
        }
        // 查询班组信息
        List<Map<String, Object>> groupIdAndNameMapList = baseCommonMapper.selectGroupIdAndName();
        Map<String, String> groupNameIdMap = toPropertyMap(groupIdAndNameMapList, "groupName", "id");
        // 查询人员信息
        List<Map<String, Object>> userIdAndRealNameMapList = baseCommonMapper.selectUserIdAndRealName();
        Map<String, String> userRealNameIdMap = toPropertyMap(userIdAndRealNameMapList, "realname", "id");
        List<Map<String, Object>> userIdAndWorkNoMapList = baseCommonMapper.selectUserIdAndWorkNo();
        Map<String, String> userWorkNoIdMap = toPropertyMap(userIdAndWorkNoMapList, "workNo", "id");
        // 查询工位信息
        List<Map<String, Object>> workstationIdAndNameMapList = baseCommonMapper.selectWorkstationIdAndName();
        Map<String, String> workstationNameIdMap = toPropertyMap(workstationIdAndNameMapList, "name", "id");
        // 查询班组
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupDispatchMapper.selectAllGroupInfo();
        Map<String, BuMtrWorkshopGroup> idGroupMap = groupList.stream()
                .collect(Collectors.toMap(BuMtrWorkshopGroup::getId, Function.identity()));
        // 查询已有故障
        List<BuFaultInfo> faultList = buFaultInfoMapper.selectList(Wrappers.emptyWrapper());

        int numberOfSheets = workbook.getNumberOfSheets();
        log.info("导入excel库位信息开始");
        log.info("文件【" + excelFile.getOriginalFilename() + "】总共表单个数=" + numberOfSheets);

        boolean hasMatchingForms = false;
        List<BuFaultInfo> needAddFaultList = new ArrayList<>();
        int rowCount = 0;
        int rowValidCount = 0;
        int existFaultCount = 0;
        // 操作每个表单
        for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            log.info(String.format("第%s个表单【%s】", sheetNum + 1, sheet.getSheetName()));

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
//            List<String> headerList = Arrays.asList("序号", "故障分类", "发生日期", "车号", "所属系统", "所属工位", "故障位置", "车辆故障详细描述", "车辆故障详细处理措施", "故障等级", "故障影响", "完成情况", "故障类别", "关闭日期", "处理人", "更换部件名称", "是否有责", "是否委外件故障", "规程内要求情况");
            Map<String, String> headerFieldMap = new HashMap<>();
            Set<String> headerMustSet = new HashSet<>();
            toHeaderFieldMap(FaultImportBO.class, headerFieldMap, headerMustSet);
            Map<String, Integer> headerCellIndexMap = new HashMap<>();

            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = ExcelUtil.getCellValue(cell).trim();
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }

                        if (headerFieldMap.containsKey(value)) {
                            headerCellIndexMap.put(value, cellNum);
                            headerMustSet.remove(value);
                        }
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(headerMustSet)) {
                log.info(String.format("第%s个表单%s，未包含必要的表头：%s", sheetNum + 1, sheet.getSheetName(), String.join(",", headerMustSet)));
            } else {
                log.info(String.format("第%s个表单%s，包含必要的表头，开始导入", sheetNum + 1, sheet.getSheetName()));

                hasMatchingForms = true;

                // 操作每行数据
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    Row sheetRow = sheet.getRow(rowNum);
                    FaultImportBO importBO = toFaultImportBO(sheetRow, headerFieldMap, headerCellIndexMap);
                    BuFaultInfo faultInfo = toFaultInfo(importBO, lineId, rowNum, systemNameIdMap, systemShortNameIdMap, workstationNameIdMap, groupNameIdMap, idGroupMap, userRealNameIdMap);

                    if (faultInfo != null) {
                        boolean exist = checkFaultExist(faultInfo, faultList);
                        if (exist) {
                            existFaultCount++;
                        } else {
                            needAddFaultList.add(faultInfo);
                            faultList.add(faultInfo);
                        }
                        rowValidCount++;
                    }

                    rowCount++;
                }
                log.info("总共行数" + rowCount + "，有效行数" + rowValidCount);
            }
        }

        // 如果没有匹配到模板表单，返回异常
        if (!hasMatchingForms) {
            log.info("文件【" + excelFile.getOriginalFilename() + "】未匹配到导入历史故障模板表单");
            throw new JeecgBootException("请选择正确的模板表单");
        } else {
            if (CollectionUtils.isNotEmpty(needAddFaultList)) {
                needAddFaultList.sort(Comparator.comparing(BuFaultInfo::getReportTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(BuFaultInfo::getHappenTime, Comparator.nullsLast(Comparator.naturalOrder())));
                // 生成故障编号
                for (BuFaultInfo fault : needAddFaultList) {
                    String faultSn = serialNumberGenerate.generateSerialNumberByCode("JDXGZ");
                    fault.setFaultSn(faultSn + "LS");
                    // 设置故障来源
                    fault.setFromType(2);
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
                    List<BuFaultInfo> deleteFaultList = buFaultInfoMapper.selectList(deleteFaultWrapper);
                    List<String> deleteFaultIdList = deleteFaultList.stream()
                            .map(BuFaultInfo::getId)
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(deleteFaultIdList)) {
                        LambdaQueryWrapper<BuFaultHandleRecord> deleteFaultHandleRecordWrapper = new LambdaQueryWrapper<BuFaultHandleRecord>()
                                .in(BuFaultHandleRecord::getFaultInfoId, deleteFaultIdList);
                        buFaultHandleRecordMapper.delete(deleteFaultHandleRecordWrapper);
                    }

                    int delete = buFaultInfoMapper.delete(deleteFaultWrapper);
                    deleteCount = deleteCount + delete;
                }
                // 插入新的
                List<List<BuFaultInfo>> faultBatchSubList = DatabaseBatchSubUtil.batchSubList(needAddFaultList);
                for (List<BuFaultInfo> faultBatchSub : faultBatchSubList) {
                    buFaultInfoMapper.insertList(faultBatchSub);

                    List<BuFaultHandleRecord> handleRecordList = new ArrayList<>();
                    faultBatchSub.forEach(item -> handleRecordList.addAll(item.getHandleRecordList()));
                    buFaultHandleRecordMapper.insertList(handleRecordList);
                }

                return String.format("表单中有效故障信息有%s条（其中有%s条已存在的），生成了%s条故障记录并导入（%s条旧的被删除后重新导入）",
                        rowValidCount, existFaultCount, needAddFaultList.size(), deleteCount);
            } else {
                return "表单中没有需要导入的故障数据";
            }
        }
    }


    //    private void setSystemByAsset(BuFaultInfo faultInfo, boolean isAdd) {
//        if (StringUtils.isBlank(faultInfo.getFaultAssetId())) {
//            return;
//        }
//
//        BuTrainAsset asset = buTrainAssetDispatchMapper.selectById(faultInfo.getFaultAssetId());
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
            return;
        }

        BuFaultCodeNew faultCodeNew = buFaultCodeNewMapper.selectById(faultInfo.getFaultCodeId());
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
        String sysId = buTrainAssetDispatchMapper.selectAssetTypeIdByCode(sysAssetTypeCode);
        if (StringUtils.isNotBlank(sysId)) {
            faultInfo.setSysId(sysId);
        }
        String subSysId = buTrainAssetDispatchMapper.selectAssetTypeIdByCode(subSysAssetTypeCode);
        if (StringUtils.isNotBlank(subSysId)) {
            faultInfo.setSubSysId(subSysId);
        }
    }

    private void addMaximoTransDataOfAddFault(List<String> faultIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(faultIdList)) {
            return;
        }

        List<BuFaultInfo> faultList = buFaultInfoMapper.selectListForMaximoByIdList(faultIdList);

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuFaultInfo fault : faultList) {
            if (fault.getFromType() != 1) {
                // 1手动创建的故障才同步到maximo
                continue;
            }

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

    private void addMaximoTransDataOfReplaceFault(List<String> faultIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(faultIdList)) {
            return;
        }

        List<BuFaultInfo> faultList = buFaultInfoMapper.selectListForMaximoByIdList(faultIdList);

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuFaultInfo fault : faultList) {
            if (fault.getFromType() != 1) {
                // 1手动创建的故障才同步到maximo
                continue;
            }

            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(8)
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

    private void addMaximoTransDataOfDeleteFault(List<String> faultIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(faultIdList)) {
            return;
        }

        List<BuFaultInfo> faultList = buFaultInfoMapper.selectListForMaximoByIdList(faultIdList);

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuFaultInfo fault : faultList) {
            if (fault.getFromType() != 1) {
                // 1手动创建的故障才同步到maximo
                continue;
            }

            String nowString = DateUtils.datetimeFormat.get().format(now);
            fault.setFaultDesc(fault.getFaultDesc() + "（" + nowString + "删除）");

            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(9)
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

    private Map<String, String> toPropertyMap(List<Map<String, Object>> mapList, String keyStr, String valueStr) {
        Map<String, String> propertyMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map<String, Object> map : mapList) {
                Object key = map.get(keyStr);
                Object value = map.get(valueStr);

                if (null != value && null != key) {
                    propertyMap.put((String) key, (String) value);
                }
            }
        }
        return propertyMap;
    }

    private void toHeaderFieldMap(Class<?> clazz, Map<String, String> headerFieldMap, Set<String> headerMustSet) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            ExcelImport excelImportAnnotation = field.getAnnotation(ExcelImport.class);
            if (null == excelImportAnnotation) {
                continue;
            }

            String name = excelImportAnnotation.name().trim();
            if (StringUtils.isNotBlank(name)) {
                headerFieldMap.put(name, field.getName());
            }
            if (excelImportAnnotation.mustRequire()) {
                headerMustSet.add(name);
            }
        }
    }

    private FaultImportBO toFaultImportBO(Row sheetRow, Map<String, String> headerFieldMap, Map<String, Integer> headerCellIndexMap) throws IllegalAccessException {
        FaultImportBO importBO = new FaultImportBO();
        Field[] fields = importBO.getClass().getDeclaredFields();
        for (Field field : fields) {
            ExcelImport excelImportAnnotation = field.getAnnotation(ExcelImport.class);
            if (null == excelImportAnnotation) {
                continue;
            }

            String name = excelImportAnnotation.name().trim();
            if (StringUtils.isNotBlank(name) && headerCellIndexMap.containsKey(name)) {
                Integer cellIndex = headerCellIndexMap.get(name);
                String value = ExcelUtil.getCellValue(sheetRow.getCell(cellIndex)).trim();

                field.setAccessible(true);
                field.set(importBO, value);
                field.setAccessible(false);
            }
        }
        return importBO;
    }

    private BuFaultInfo toFaultInfo(FaultImportBO importBO,
                                    String lineId,
                                    int rowIndex,
                                    Map<String, String> systemNameIdMap,
                                    Map<String, String> systemShortNameIdMap,
                                    Map<String, String> workstationNameIdMap,
                                    Map<String, String> groupNameIdMap,
                                    Map<String, BuMtrWorkshopGroup> idGroupMap,
                                    Map<String, String> userRealNameIdMap) {
        if (null == importBO) {
            return null;
        }

        // 故障
        BuFaultInfo fault = new BuFaultInfo()
                .setId(UUIDGenerator.generate())
                .setFaultSn(null)
                .setPlanId("-1")
                .setLineId(lineId)
                .setHasDuty(0)
                .setFaultOnline(0)
                .setOutsource(0)
                .setStatus(2)
                .setSubmitStatus(1);
        // 导入的数据
        fault.setPhase(parsePhase(importBO.getPhase()));
        fault.setHappenTime(parseDate(importBO.getHappenTime(), rowIndex));
        fault.setReportTime(fault.getHappenTime());
        fault.setTrainNo(importBO.getTrainNo());
        // 系统id根据系统名称获取
        String sysId = systemNameIdMap.get(importBO.getSysName());
        if (org.apache.commons.lang3.StringUtils.isBlank(sysId)) {
            sysId = systemShortNameIdMap.get(importBO.getSysName());
        }
        fault.setSysId(org.apache.commons.lang3.StringUtils.isBlank(sysId) ? "-" : sysId);
        fault.setWorkStationId(workstationNameIdMap.get(importBO.getWorkStationName()));
        checkFaultDescLength(importBO.getFaultDesc(), rowIndex);
        fault.setFaultDesc(importBO.getFaultDesc());
        fault.setFaultLevel(parseFaultLevel(importBO.getFaultLevel()));
        // 班组
        String groupId = groupNameIdMap.get(importBO.getReportGroupName());
        BuMtrWorkshopGroup group = idGroupMap.get(groupId);
        if (StringUtils.isNotBlank(groupId) && null != group) {
            fault.setReportGroupId(groupId)
                    .setWorkshopId(group.getWorkshopId())
                    .setCompanyId(group.getCompanyId());
        } else {
            log.error(String.format("【第%s行】故障类别对应的班组找不到，不导入此数据", rowIndex + 1));
            return null;
        }
        fault.setCloseTime(parseDate(importBO.getCloseTime(), rowIndex));
        // 提报人员
        String reportUserId = userRealNameIdMap.get(importBO.getReportUserName());
        if (StringUtils.isBlank(reportUserId)) {
            reportUserId = "-1";
        }
        fault.setReportUserId(reportUserId);
        fault.setCloseUserId(fault.getReportUserId());
        fault.setHasDuty("无".equals(importBO.getHasDuty()) ? 0 : 1);
        fault.setOutsource("是".equals(importBO.getOutsource()) ? 1 : 0);

        // 故障处理记录
        checkFaultSolutionDescLength(importBO.getSolutionDesc(), rowIndex);
        BuFaultHandleRecord faultHandleRecord = new BuFaultHandleRecord()
                .setId(UUIDGenerator.generate())
                .setFaultInfoId(fault.getId())
                .setFaultReasonId(null)
                .setReasonDesc("导入的历史故障")
                .setFaultSolutionId(null)
                .setSolutionDesc(StringUtils.isBlank(importBO.getSolutionDesc()) ? "-" : importBO.getSolutionDesc())
                .setSolutionTime(fault.getCloseTime())
                .setSolutionGroupId(fault.getReportGroupId())
                .setSolutionUserId(fault.getReportUserId())
                .setResult(1);
        fault.setHandleRecordList(Collections.singletonList(faultHandleRecord));

        return fault;
    }

    private int parsePhase(String cellValue) {
        int phase = 1;
        if (org.apache.commons.lang.StringUtils.isNotBlank(cellValue)) {
            switch (cellValue) {
                case "架修期":
                    phase = 1;
                    break;
                case "大修期":
                    phase = 4;
                    break;
                case "质保期":
                    phase = 2;
                    break;
                case "出保期":
                    phase = 3;
                    break;
                default:
                    break;
            }

        }
        return phase;
    }

    private int parseFaultLevel(String cellValue) {
        int level = 0;
        if (org.apache.commons.lang.StringUtils.isNotBlank(cellValue)) {
            switch (cellValue) {
                case "A":
                    level = 1;
                    break;
                case "B":
                    level = 2;
                    break;
                case "C":
                    level = 3;
                    break;
                case "D":
                    level = 4;
                    break;
                default:
                    break;
            }
        }
        return level;
    }

    private Date parseDate(String value, int rowIndex) {
        if (org.apache.commons.lang.StringUtils.isNotBlank(value)) {
            try {
                try {
                    Calendar calendar = new GregorianCalendar(1900, Calendar.JANUARY, -1);
                    Date date = calendar.getTime();
                    return org.apache.commons.lang.time.DateUtils.addDays(date, Double.valueOf(value).intValue());
                } catch (NumberFormatException ex) {
                    try {
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        return dateFormat1.parse(value);
                    } catch (ParseException e) {
                        try {
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
                            return dateFormat2.parse(value);
                        } catch (ParseException parseException) {
                            SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyyMMdd");
                            return dateFormat3.parse(value);
                        }
                    }
                }
            } catch (ParseException ex) {
                throw new JeecgBootException(String.format("【第%s行】日期格式不正确，请使用推荐格式（yyyy-MM-dd或yyyy/MM/dd或yyyyMMdd），当前输入：%s",
                        rowIndex + 1, value));
            }
        }
        return new Date();
    }

    private void checkFaultDescLength(String value, int rowIndex) {
        if (value.length() > 332) {
            throw new JeecgBootException(String.format("【第%s行车辆故障详细描述】过长【当前长度%s】，请修改为332以内（包含汉字和标点符号等<=332），当前输入：%s",
                    rowIndex + 1, value.length(), value));
        }
    }

    private void checkFaultSolutionDescLength(String value, int rowIndex) {
        if (value.length() > 166) {
            throw new JeecgBootException(String.format("【第%s行车辆故障详细处理措施】过长【当前长度%s】，请修改为166以内（包含汉字和标点符号等<=166），当前输入：%s",
                    rowIndex + 1, value.length(), value));
        }
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
            boolean sameHappenDay = org.jeecg.common.util.DateUtils.isSameDay(happenTime, faultInfo.getHappenTime());
            boolean sameFaultDesc = faultDesc.trim().equals(faultInfo.getFaultDesc().trim());
            if (sameTrainNo && sameHappenDay && sameFaultDesc) {
                return true;
            }
        }

        return false;
    }

}
