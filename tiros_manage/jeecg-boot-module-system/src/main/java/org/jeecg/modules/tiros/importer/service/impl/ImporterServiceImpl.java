package org.jeecg.modules.tiros.importer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguInfo;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlan;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheck;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecord;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.mapper.BuMaterialMustListMapper;
import org.jeecg.modules.tiros.importer.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuyougen
 * @title: ImporterServiceImpl
 * @projectName tiros-manage-parent
 * @description: TODO
 * @date 2021/4/1514:16
 */
@Service
public class ImporterServiceImpl implements ImporterService {

    @Resource
    private ReguImporter reguImporter;
    @Resource
    private RepairPlanImport repairPlanImport;
    @Resource
    private RepairPlanRelationImport planRelationImport;
    @Resource
    private ReguWorkRecord reguWorkRecord;
    @Resource
    private MaterialMustImporter materialMustImporter;
//    @Resource
//    private MaterialEntryOrderImporter materialEntryOrderImporter;
    @Resource
    private MaterialTypeImport materialTypeImport;
    @Resource
    private MaterialSparePartImport materialSparePartImport;
    @Resource
    private RptBoardImport rptBoardImport;
    @Resource
    private BuMaterialToolsImport buMaterialToolsImport;
    @Resource
    private WorkCheckImporter workCheckImporter;
    @Resource
    private BuMaterialMustListMapper materialMustListMapper;
    @Resource
    private GroupStockImport groupStockImport;
    @Resource
    private BuMaterialApplyImport materialApplyImport;
    @Resource
    private MaterialCostBySystemImport materialCostBySystemImport;
    @Resource
    private FaultHistoryImporter faultHistoryImporter;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importRegu(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        BuRepairReguInfo reguInfo = new BuRepairReguInfo();
        reguInfo.setLineId(request.getParameter("lineId"));
        reguInfo.setRepairProId(request.getParameter("repairProId"));
        reguInfo.setWorkshopId(request.getParameter("workshopId"));
        reguInfo.setTrainTypeId(request.getParameter("trainTypeId"));
        reguInfo.setName(request.getParameter("name"));
        reguInfo.setCode(request.getParameter("code"));
        reguInfo.setRemark(request.getParameter("remark"));
        reguInfo.setId(request.getParameter("id"));
        reguInfo.setVersion(request.getParameter("version"));
        reguImporter.importRegu(fileName, file.getInputStream(), reguInfo);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importPlan(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        BuTpRepairPlan plan = new BuTpRepairPlan();
        plan.setBaseDate(DateUtil.parse(request.getParameter("baseDate"), "yyyy-MM-dd"));
        plan.setGroupQuantity(Integer.valueOf(request.getParameter("groupQuantity")));
        plan.setLineId(request.getParameter("lineId"));
        plan.setTrainTypeId(request.getParameter("trainTypeId"));
        plan.setRepairProgramId(request.getParameter("repairProgramId"));
        plan.setStatus(Integer.valueOf(request.getParameter("status")));
        plan.setReguId(request.getParameter("reguId"));
        plan.setCode(request.getParameter("code"));
        plan.setTpName(request.getParameter("tpName"));
        plan.setDuration(Integer.valueOf(request.getParameter("duration")));
        plan.setFdProject(request.getParameter("fdProject"));
        plan.setFdTask(request.getParameter("fdTask"));
        plan.setFdCostType(request.getParameter("fdCostType"));
        plan.setRemark(request.getParameter("remark"));
        plan.setId(request.getParameter("id"));

        Map<String, Map<String, List<String>>> content = repairPlanImport.ImportRepairPlan(fileName, file.getInputStream());
        planRelationImport.ImportPlanTask(fileName, content, plan);

    }

    @Override
    public void importWorkRecord(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        BuWorkRecord workRecord = new BuWorkRecord();
        workRecord.setId(request.getParameter("id"));
        workRecord.setCode(request.getParameter("code"));
        workRecord.setTitle(request.getParameter("title"));
        workRecord.setLineId(request.getParameter("lineId"));
        workRecord.setRepairProId(request.getParameter("repairProId"));
        workRecord.setAssetTypeId(request.getParameter("assetTypeId"));
        workRecord.setUpdown(Integer.valueOf(request.getParameter("updown")));
        workRecord.setStatus(Integer.valueOf(request.getParameter("status")));
        workRecord.setRemark(request.getParameter("remark"));
        workRecord.setCreateGroupId(request.getParameter("createGroupId"));
        workRecord.setReguId(request.getParameter("reguId"));
        reguWorkRecord.importReguWorkRecord(file.getInputStream(), workRecord, fileName);
    }

    @Override
    public void importMaterialMust(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        String lineId = request.getParameter("lineId");
        String repairProgramId = request.getParameter("repairProgramId");
        Boolean clear = Boolean.valueOf(request.getParameter("clear"));
        if (clear) {
            deleteMustList(lineId, repairProgramId);
        }
        materialMustImporter.importer(fileName, file.getInputStream(), lineId, repairProgramId);
        materialMustListMapper.setGroupId();
    }

    private void deleteMustList(String lineId, String repairProgramId) {
        List<BuMaterialMustList> materialMustList = materialMustListMapper.selectList(Wrappers.<BuMaterialMustList>lambdaQuery().select(BuMaterialMustList::getId).eq(BuMaterialMustList::getLineId, lineId)
                .eq(BuMaterialMustList::getRepairProgramId, repairProgramId));
        if (CollectionUtils.isNotEmpty(materialMustList)) {
            List<String> ids = materialMustList.stream().map(BuMaterialMustList::getId).collect(Collectors.toList());
            materialMustListMapper.deleteBatchIds(ids);
        }
    }

//    @Override
//    public void importMaterialEntryOrder(MultipartFile file, HttpServletRequest request) throws Exception {
//        String fileName = file.getOriginalFilename();
//        materialEntryOrderImporter.importMaterialEntryOrder(fileName, file.getInputStream());
//    }

    @Override
    public Boolean importMaterialType(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        boolean flag = materialTypeImport.importMaterialType(fileName, file.getInputStream());
        return flag;

    }

    @Override
    public void importMaterialSparePart(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        materialSparePartImport.importMaterialSparePart(fileName, file.getInputStream());

    }

    @Override
    public void importRptBoard(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        rptBoardImport.importRptBoard(fileName, file.getInputStream());
    }

    @Override
    public void importMaterialTools(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        Integer type = Integer.valueOf(request.getParameter("type"));
        String lineId = request.getParameter("lineId");
        String workshopId = request.getParameter("workshopId");
        // buMaterialToolsImport.importMaterialTools(fileName, file.getInputStream());
        buMaterialToolsImport.importMaterialTools(fileName, file.getInputStream(), type, lineId, workshopId);
    }

    @Override
    public void importWorkCheck(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        BuWorkCheck workCheck = new BuWorkCheck()
                .setId(UUIDGenerator.generate())
                .setLineId(request.getParameter("lineId"))
                .setRepairProId(request.getParameter("repairProId"))
                .setTitle(fileName.substring(0, fileName.indexOf(".") + 1))
                .setCode("JCB" + RandomUtil.randomNumbers(5))
                .setStatus(1);

        workCheckImporter.importerWorkCheck(file.getInputStream(), workCheck, fileName);

    }

    @Override
    public void importGroupStock(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        Boolean cleanup = Boolean.valueOf(request.getParameter("cleanup"));
        groupStockImport.execute(fileName, file.getInputStream(), cleanup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importMaterialCostBySystem(List<MultipartFile> files) throws Exception {
        materialCostBySystemImport.execute(files);
    }

    @Override
    public void importMaterialApplyUsingSyncPrice(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        materialApplyImport.execute(fileName, file.getInputStream());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importFaultHistory(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        Integer type = Integer.valueOf(request.getParameter("type"));
        return faultHistoryImporter.execute(fileName, file.getInputStream(), type);
    }

}
