package org.jeecg.modules.tiros.importer.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguDetail;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguInfo;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguMaterial;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTools;
import org.jeecg.modules.basemanage.regu.mapper.BuRepairReguDetailMapper;
import org.jeecg.modules.basemanage.regu.mapper.BuRepairReguMaterialMapper;
import org.jeecg.modules.basemanage.regu.mapper.BuRepairReguToolsMapper;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBook;
import org.jeecg.modules.basemanage.techbook.mapper.BuRepairTechBookMapper;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanTask;
import org.jeecg.modules.basemanage.tpplan.mapper.BuTpRepairPlanTaskMapper;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetTypeMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainStructureDetailMapper;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheckItem;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheckTechLink;
import org.jeecg.modules.basemanage.workcheck.service.BuWorkCheckItemService;
import org.jeecg.modules.basemanage.workcheck.service.BuWorkCheckTechLinkService;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordCategory;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordDetail;
import org.jeecg.modules.basemanage.workrecord.mapper.BuWorkRecordCategoryMapper;
import org.jeecg.modules.basemanage.workrecord.mapper.BuWorkRecordDetailMapper;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseWorkstation;
import org.jeecg.modules.basemanage.workstation.mapper.BuBaseWorkstationMapper;
import org.jeecg.modules.board.cost.bean.BuRptBoardTrainMaterial;
import org.jeecg.modules.board.cost.mapper.BuRptBoardTrainMaterialMapper;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairProgramMapper;
import org.jeecg.modules.group.information.bean.BuGroupWorkstation;
import org.jeecg.modules.group.information.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.group.information.mapper.BuGroupWorkstationMapper;
import org.jeecg.modules.group.information.mapper.BuMtrWorkshopGroupMapper;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryOrder;
import org.jeecg.modules.material.entry.mapper.BuMaterialEntryDetailMapper;
import org.jeecg.modules.material.entry.mapper.BuMaterialEntryOrderMapper;
import org.jeecg.modules.material.material.bean.BuMaterialCategory;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;
import org.jeecg.modules.material.material.mapper.BuMaterialCategoryMapper;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeAttrMapper;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.mapper.BuMaterialMustListMapper;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.mapper.BuMaterialGroupStockMapper;
import org.jeecg.modules.material.tools.bean.BuMaterialTools;
import org.jeecg.modules.material.tools.mapper.BuMaterialToolsMapper;
import org.jeecg.modules.material.sparepart.bean.BuMaterialSparePart;
import org.jeecg.modules.material.sparepart.mapper.BuMaterialSparePartMapper;
import org.jeecg.modules.report.cost.bean.BuRptBoardSysMaterial;
import org.jeecg.modules.report.cost.mapper.BuRptBoardSysMaterialMapper;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Importer sql.
 *
 * @author yuyougen
 * @title: ImporterSql
 * @projectName tiros -manage-parent
 * @description: TODO
 * @date 2021 /4/1510:01
 */
@Repository
public class ImporterSql {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private BuRepairReguDetailMapper reguDetailMapper;
    @Resource
    private BuRepairReguToolsMapper repairReguToolsMapper;
    @Resource
    private BuRepairReguMaterialMapper materialMapper;
    @Resource
    private BuMtrWorkshopGroupMapper workshopGroupMapper;
    @Resource
    private BuTpRepairPlanTaskMapper planTaskMapper;
    @Resource
    private BuWorkRecordCategoryMapper workRecordCategoryMapper;
    @Resource
    private BuWorkRecordDetailMapper workRecordDetailMapper;
    @Resource
    private BuMaterialTypeMapper materialTypeMapper;
    @Resource
    private BuMaterialTypeAttrMapper materialTypeAttrMapper;
    @Resource
    private BuTrainAssetTypeMapper trainAssetTypeMapper;
    @Resource
    private BuBaseWorkstationMapper workstationMapper;
    @Resource
    private BuTrainStructureDetailMapper structureDetailMapper;
    @Resource
    private BuMaterialMustListMapper materialMustListMapper;
    @Resource
    private BuMaterialEntryOrderMapper materialEntryOrderMapper;
    @Resource
    private BuMaterialEntryDetailMapper materialEntryDetailMapper;
    @Resource
    private BuMaterialCategoryMapper buMaterialCategoryMapper;
    @Resource
    private BuMaterialSparePartMapper materialSparePartMapper;
    @Resource
    private BuRptBoardSysMaterialMapper buRptBoardSysMaterialMapper;
    @Resource
    private BuRptBoardTrainMaterialMapper buRptBoardTrainMaterialMapper;
    @Resource
    private BuMaterialToolsMapper buMaterialToolsMapper;
    @Resource
    private BuRepairTechBookMapper techBookMapper;
    @Resource
    private BuWorkCheckItemService checkItemService;
    @Resource
    private BuWorkCheckTechLinkService checkTechLinkService;
    @Resource
    private BuMaterialGroupStockMapper materialGroupStockMapper;
    @Resource
    private BuRepairProgramMapper repairProgramMapper;
    @Resource
    private BuGroupWorkstationMapper groupWorkstationMapper;
    @Resource
    private SysUserMapper sysUserMapper;


    /**
     * 查询规程最大版本号
     *
     * @param lineId the line id
     * @return the integer
     */
    public Integer selectReguInfoVersion(String lineId) {
        Integer version = null;

        String sql = "select max(version) as version from bu_repair_regu_info where line_id = " + "'" + lineId + "'";
        version = jdbcTemplate.queryForObject(sql, Integer.class);
        return version;
    }

    /**
     * Select material type name id map map.
     *
     * @param name the name
     * @return the map
     */
    public Map<String, String> selectMaterialTypeNameIdMap(String name) {
        Map<String, String> materialTypeNameIdMap = new HashMap<>(16);
        List<BuMaterialType> materialTypeList = new BuMaterialType().selectList(Wrappers.<BuMaterialType>lambdaQuery().select(BuMaterialType::getId, BuMaterialType::getName).eq(BuMaterialType::getName, name));
        if (CollectionUtils.isNotEmpty(materialTypeList)) {
            BuMaterialType materialType = materialTypeList.get(0);
            materialTypeNameIdMap.put(materialType.getName(), materialType.getId());
        }


        return materialTypeNameIdMap;

    }


    /**
     * 查询物资类型
     *
     * @return the map
     */
    public Map<String, String> selectMaterialTypeNameIdMap() {
        Map<String, String> materialTypeNameIdMap = new HashMap<>(16);

        List<BuMaterialType> materialTypeList = new BuMaterialType().selectList(Wrappers.<BuMaterialType>lambdaQuery().select(BuMaterialType::getId, BuMaterialType::getName));
        if (CollectionUtils.isNotEmpty(materialTypeList)) {
            for (BuMaterialType materialType : materialTypeList) {
                materialTypeNameIdMap.put(materialType.getName(), materialType.getId());
            }
        }
        return materialTypeNameIdMap;
    }

    /**
     * 保存规程信息
     *
     * @param reguInfo the regu info
     * @return int int
     * @throws Exception the exception
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveReguInfo(BuRepairReguInfo reguInfo) throws Exception {
        int row = 0;
        String sql = "insert into bu_repair_regu_info (id,code,name,repair_pro_id,line_id,train_type_id,workshop_id,version,status,remark) values (?,?,?,?,?,?,?,?,?,?)";

        row = jdbcTemplate.update(sql, new Object[]{reguInfo.getId(),
                reguInfo.getCode(), reguInfo.getName(), reguInfo.getRepairProId(),
                reguInfo.getLineId(), reguInfo.getTrainTypeId(), reguInfo.getWorkshopId(), reguInfo.getVersion(), reguInfo.getStatus(), reguInfo.getRemark()});

        return row;
    }

    /**
     * 保存规程详细信息
     *
     * @param reguDetail the regu detail
     * @throws Exception the exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveReguDetail(List<BuRepairReguDetail> reguDetail) throws Exception {
        if (CollectionUtils.isEmpty(reguDetail)) {
            return;
        }

        for (BuRepairReguDetail detail : reguDetail) {
            if (null == detail.getMeasure()) {
                detail.setMeasure(0);
            }
            if (null == detail.getMustReplace()) {
                detail.setMustReplace(0);
            }
            if (null == detail.getOutsource()) {
                detail.setOutsource(0);
            }
            if (null == detail.getImportant()) {
                detail.setImportant(0);
            }
        }
        reguDetailMapper.insertList(reguDetail);

    }

    /**
     * 保存工器具
     *
     * @param tools the tools
     * @return the int
     * @throws Exception the exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveMaterialTools(List<BuRepairReguTools> tools) throws Exception {
        if (CollectionUtils.isEmpty(tools)) {
            return;
        }
        for (BuRepairReguTools tool : tools) {
            if (null == tool.getAmount()) {
                tool.setAmount(0D);
            }
        }
        repairReguToolsMapper.insertList(tools);
    }

    /**
     * 保存物资
     *
     * @param material the material
     * @throws Exception the exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveMaterial(List<BuRepairReguMaterial> material) throws Exception {
        if (CollectionUtils.isEmpty(material)) {
            return;
        }
        for (BuRepairReguMaterial reguMaterial : material) {
            if (null == reguMaterial.getAmount()) {
                reguMaterial.setAmount(0D);
            }
            if (null == reguMaterial.getUseCategory()) {
                reguMaterial.setUseCategory(-1);
            }
        }
        materialMapper.insertList(material);
    }

    /**
     * 查询工班名称
     *
     * @param groupNamesString the group names string
     * @return the map
     */
    public Map<String, String> selectWorkGroupNameIdMap(List<String> groupNamesString, String workshopId) {
        Map<String, String> nameIdMap = new HashMap<>(16);
        List<BuMtrWorkshopGroup> workshopGroups = workshopGroupMapper.selectList(
                Wrappers.<BuMtrWorkshopGroup>lambdaQuery()
                        .select(BuMtrWorkshopGroup::getId, BuMtrWorkshopGroup::getGroupName)
                        .eq(BuMtrWorkshopGroup::getWorkshopId, workshopId)
                        .in(BuMtrWorkshopGroup::getGroupName, groupNamesString));

        if (CollectionUtils.isNotEmpty(workshopGroups)) {
            workshopGroups.forEach(group -> {
                String id = group.getId();
                String groupName = group.getGroupName();
                nameIdMap.put(groupName, id);
            });
        }

        return nameIdMap;
    }

    /**
     * 查询所有工班
     *
     * @return the map
     */
    public Map<String, String> selectWorkGroupNameMap() {
        List<BuMtrWorkshopGroup> workshopGroups = workshopGroupMapper.selectList(Wrappers.<BuMtrWorkshopGroup>lambdaQuery().select(BuMtrWorkshopGroup::getGroupName, BuMtrWorkshopGroup::getId));
        if (CollectionUtils.isNotEmpty(workshopGroups)) {
            return workshopGroups.stream().collect(Collectors.toMap(BuMtrWorkshopGroup::getGroupName, BuMtrWorkshopGroup::getId));
        }
        return null;
    }

    /**
     * 保存计划模板任务
     *
     * @param planTasks the plan tasks
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveRepairPlanTask(List<BuTpRepairPlanTask> planTasks) {
        if (CollectionUtils.isEmpty(planTasks)) {
            return;
        }
        for (BuTpRepairPlanTask planTask : planTasks) {
            if (null == planTask.getGenOrder()) {
                planTask.setGenOrder(1);
            }
            if (null == planTask.getMethod()) {
                planTask.setMethod(0);
            }
            if (null == planTask.getOutsource()) {
                planTask.setOutsource(0);
            }
            if (null == planTask.getImportant()) {
                planTask.setImportant(0);
            }
        }

        planTaskMapper.insertList(planTasks);

    }

    /**
     * Save work record category.
     *
     * @param workRecordCategoryList the work record category list
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWorkRecordCategory(List<BuWorkRecordCategory> workRecordCategoryList) {
        workRecordCategoryList.forEach(item -> workRecordCategoryMapper.insert(item));
    }

    /**
     * Save work record detail.
     *
     * @param workRecordDetailList the work record detail list
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWorkRecordDetail(List<BuWorkRecordDetail> workRecordDetailList) {
        workRecordDetailList.forEach(item -> workRecordDetailMapper.insert(item));
    }

    /**
     * 删除作业记录表相关的信息
     *
     * @param id the id
     */
    @Transactional(rollbackFor = Exception.class)
    public void delWorkRecord(String id) {
        List<String> workRecordIdList = Arrays.asList(id);

        // 删除作业记录表明细
        List<String> detailIdList = workRecordDetailMapper.selectIdListByWorkRecordIdList(workRecordIdList);
        if (CollectionUtils.isNotEmpty(detailIdList)) {
            workRecordDetailMapper.deleteBatchIds(detailIdList);
        }
        // 删除作业记录表明细分项
        List<String> categoryIdList = workRecordCategoryMapper.selectIdListByWorkRecordIdList(workRecordIdList);
        if (CollectionUtils.isNotEmpty(categoryIdList)) {
            workRecordCategoryMapper.deleteBatchIds(categoryIdList);
        }
    }

    public String selectMaterialTypeByCode(String cellValue) {
        List<BuMaterialType> materialTypes = materialTypeMapper.selectList(Wrappers.<BuMaterialType>lambdaQuery().select(BuMaterialType::getId).eq(BuMaterialType::getCode, cellValue));
        if (CollectionUtils.isNotEmpty(materialTypes)) {
            return materialTypes.get(0).getId();
        }
        return "";
    }

    public Map<String, String> selectMaterialTypeMap() {
        List<BuMaterialType> materialTypes = materialTypeMapper.selectList(Wrappers.<BuMaterialType>lambdaQuery().select(BuMaterialType::getId, BuMaterialType::getCode));
        if (CollectionUtils.isNotEmpty(materialTypes)) {
            return materialTypes.stream().collect(Collectors.toMap(BuMaterialType::getCode, BuMaterialType::getId, (value1, value2) -> value2));
        }
        return null;
    }

    public Map<String, BuMaterialType> selectMaterialTypeOfObjectMap() {
        List<BuMaterialType> materialTypes = materialTypeMapper.selectList(Wrappers.<BuMaterialType>lambdaQuery().select(BuMaterialType::getId, BuMaterialType::getCode, BuMaterialType::getPrice));
        if (CollectionUtils.isNotEmpty(materialTypes)) {
            return materialTypes.stream().collect(Collectors.toMap(BuMaterialType::getCode, item -> item, (key1, key2) -> key2));
        }
        return null;
    }

    public Map<String, String> selectTrainSysShortNameIdMap() {
        List<BuTrainAssetType> trainAssetTypes = trainAssetTypeMapper.selectList(Wrappers.<BuTrainAssetType>lambdaQuery()
                .select(BuTrainAssetType::getId, BuTrainAssetType::getName, BuTrainAssetType::getShortName)
                .eq(BuTrainAssetType::getStructType, 1));

        if (CollectionUtils.isNotEmpty(trainAssetTypes)) {
            return trainAssetTypes.stream()
                    .collect(Collectors.toMap(BuTrainAssetType::getShortName, BuTrainAssetType::getId));
        }
        return new HashMap<>();
    }

    public Map<String, String> selectGroupIdMap() {
        List<BuGroupWorkstation> groupWorkstationList = groupWorkstationMapper.selectList(Wrappers.emptyWrapper());
        if (CollectionUtils.isNotEmpty(groupWorkstationList)) {
            return groupWorkstationList.stream().collect(Collectors.toMap(BuGroupWorkstation::getWorkstationId, BuGroupWorkstation::getGroupId, (k1, k2) -> k2));
        } else {
            throw new JeecgBootException("班组没有和工位关联!");
        }
    }

    public Map<String, String> selectWorkstationIdMap() {

        List<BuBaseWorkstation> buBaseWorkstations = workstationMapper.selectList(Wrappers.<BuBaseWorkstation>lambdaQuery().select(BuBaseWorkstation::getId, BuBaseWorkstation::getName));
        if (CollectionUtils.isNotEmpty(buBaseWorkstations)) {
            buBaseWorkstations.forEach(item -> item.setName(item.getName().trim()));
            return buBaseWorkstations.stream().collect(Collectors.toMap(BuBaseWorkstation::getName, BuBaseWorkstation::getId));
        }
        return new HashMap<>();
    }

    public String selectWorkstationIdByName(String cellValue) {

        List<BuBaseWorkstation> buBaseWorkstations = workstationMapper.selectList(Wrappers.<BuBaseWorkstation>lambdaQuery().select(BuBaseWorkstation::getId).eq(BuBaseWorkstation::getName, cellValue));
        if (CollectionUtils.isNotEmpty(buBaseWorkstations)) {
            return buBaseWorkstations.get(0).getId();
        }
        return "";
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveMaterialType(List<BuMaterialType> materialTypeList) throws Exception {
        if (CollectionUtils.isEmpty(materialTypeList)) {
            return;
        }
        for (BuMaterialType materialType : materialTypeList) {
            if (null == materialType.getStatus()) {
                materialType.setStatus(1);
            }
            if (null == materialType.getKind()) {
                materialType.setKind(1);
            }
            if (null == materialType.getTheshold()) {
                materialType.setTheshold(BigDecimal.valueOf(-1));
            }
            if (null == materialType.getIsAsset()) {
                materialType.setIsAsset(0);
            }
            if (null == materialType.getFromHead()) {
                materialType.setFromHead(0);
            }
        }
        long start = System.currentTimeMillis();
        materialTypeMapper.insertList(materialTypeList);
        long end = System.currentTimeMillis();
        System.out.println("---------------" + (start - end) + "---------------");
    }

    public List<BuMaterialMustList> selectAllMaterialMust(String repairProgramId, String lineId) {
        return materialMustListMapper.selectList(Wrappers.<BuMaterialMustList>lambdaQuery().eq(BuMaterialMustList::getLineId, lineId).eq(BuMaterialMustList::getRepairProgramId, repairProgramId));
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveMaterialMust(List<BuMaterialMustList> materialMustLists) throws Exception {
        long start = System.currentTimeMillis();
        materialMustListMapper.insertBatch(materialMustLists);
        long end = System.currentTimeMillis();
        System.out.println("---------------" + (start - end) + "---------------");
    }


    /**
     * Save BuMaterialEntryOrder
     *
     * @param orderList
     */
    public void saveMaterialEntryOrder(List<BuMaterialEntryOrder> orderList) {
        orderList.forEach(item -> materialEntryOrderMapper.insert(item));
    }

    /**
     * Save save BuMaterialEntryDetail
     *
     * @param detailList
     */
    public void saveMaterialEntryDetail(List<BuMaterialEntryDetail> detailList) {
        detailList.forEach(item -> materialEntryDetailMapper.insert(item));
    }

    /**
     * Save save BuMaterialCategory
     *
     * @param categoryList
     */
    public void saveMaterialCategory(List<BuMaterialCategory> categoryList) {
        categoryList.forEach(item -> buMaterialCategoryMapper.insert(item));
    }

    /**
     * Save save BuMaterialType
     *
     * @param typeList
     */
    public void saveTypeList(List<BuMaterialType> typeList) {
        typeList.forEach(item -> materialTypeMapper.insert(item));
    }

    /**
     * Save save BuMaterialSparePart
     *
     * @param sparePartList
     */
    public void saveMaterialSparePart(List<BuMaterialSparePart> sparePartList) {
        sparePartList.forEach(item -> materialSparePartMapper.insert(item));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMaterialType(List<BuMaterialType> materialTypeUpdateList) {
        long start = System.currentTimeMillis();
        materialTypeMapper.updateBatch(materialTypeUpdateList);
        long end = System.currentTimeMillis();
        System.out.println("---------------" + (start - end) + "---------------");
    }

    /**
     * Save save RptBoardSysMaterial
     *
     * @param sysMaterialList
     */
    public void saveRptBoardSysMaterial(List<BuRptBoardSysMaterial> sysMaterialList) {
        sysMaterialList.forEach(item -> buRptBoardSysMaterialMapper.insert(item));
    }

    /**
     * Save save RptBoardTrainMaterial
     *
     * @param trainMaterialList
     */
    public void saveRptBoardTrainMaterial(List<BuRptBoardTrainMaterial> trainMaterialList) {
        trainMaterialList.forEach(item -> buRptBoardTrainMaterialMapper.insert(item));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMaterialTypeAttr(List<BuMaterialTypeAttr> materialTypeAttrList) {
        materialTypeAttrMapper.updateBatch(materialTypeAttrList);

    }

    public Map<String, String> selectMaterialTypeAttrList() {
        List<BuMaterialTypeAttr> materialTypeAttrList = materialTypeAttrMapper.selectList(Wrappers.<BuMaterialTypeAttr>lambdaQuery().select(BuMaterialTypeAttr::getMaterialTypeId, BuMaterialTypeAttr::getId));
        if (CollectionUtils.isNotEmpty(materialTypeAttrList)) {
            return materialTypeAttrList.stream().collect(Collectors.toMap(BuMaterialTypeAttr::getMaterialTypeId, BuMaterialTypeAttr::getId, (key1, key2) -> key2));
        }
        return null;

    }

    @Transactional(rollbackFor = Exception.class)
    public void saveMaterialTypeAttr(List<BuMaterialTypeAttr> saveTypeAttrs) {
        if (CollectionUtils.isEmpty(saveTypeAttrs)) {
            return;
        }
        for (BuMaterialTypeAttr saveTypeAttr : saveTypeAttrs) {
            if (null == saveTypeAttr.getTheshold()) {
                saveTypeAttr.setTheshold(-1D);
            }
        }
        materialTypeAttrMapper.insertBatch(saveTypeAttrs);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveToolsList(List<BuMaterialTools> toolsList) {
        toolsList.forEach(item -> buMaterialToolsMapper.insert(item));
    }

    public Map<String, String> selectTechBookMap() {
        List<BuRepairTechBook> techBookList = techBookMapper.selectList(Wrappers.<BuRepairTechBook>lambdaQuery().select(BuRepairTechBook::getId, BuRepairTechBook::getFileName));
        if (CollectionUtils.isNotEmpty(techBookList)) {
            return techBookList.stream().collect(Collectors.toMap(BuRepairTechBook::getFileName, BuRepairTechBook::getId, (key1, key2) -> key2));
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveCheckItemList(List<BuWorkCheckItem> checkItemList) {
        checkItemService.saveBatch(checkItemList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveCheckTechLink(List<BuWorkCheckTechLink> checkTechLinks) {
        checkTechLinkService.saveBatch(checkTechLinks);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatchGroupStock(List<String> groupIdList, List<BuMaterialGroupStock> groupStockList, Boolean cleanup) {
        if (cleanup) {
            materialGroupStockMapper.delete(Wrappers.<BuMaterialGroupStock>lambdaUpdate().in(BuMaterialGroupStock::getGroupId, groupIdList));
            materialGroupStockMapper.insertList(groupStockList);
        } else {
            List<BuMaterialGroupStock> updateGroupStockList = new ArrayList<>();
            List<BuMaterialGroupStock> addGroupStockList = new ArrayList<>();
            List<BuMaterialGroupStock> localGroupStockList = materialGroupStockMapper.selectList(Wrappers.<BuMaterialGroupStock>lambdaQuery()
                    .select(BuMaterialGroupStock::getGroupId, BuMaterialGroupStock::getId, BuMaterialGroupStock::getAmount, BuMaterialGroupStock::getMaterialTypeId)
                    .in(BuMaterialGroupStock::getGroupId, groupIdList));
            if (CollectionUtils.isNotEmpty(localGroupStockList)) {
                addGroupStockList.addAll(groupStockList);
                Map<String, BuMaterialGroupStock> localGroupStockMap = localGroupStockList.stream().collect(Collectors.toMap(BuMaterialGroupStock::getMaterialTypeId, item -> item, (k1, k2) -> k2));
                groupStockList.forEach(item -> {
                    BuMaterialGroupStock localGroupStock = localGroupStockMap.get(item.getMaterialTypeId());
                    if (localGroupStock != null && localGroupStock.getGroupId().equals(item.getGroupId())) {
                        localGroupStock.setAmount(localGroupStock.getAmount().add(item.getAmount()));
                        updateGroupStockList.add(localGroupStock);
                        addGroupStockList.remove(item);
                    }
                });
                if (CollectionUtils.isNotEmpty(addGroupStockList)) {
                    materialGroupStockMapper.insertList(addGroupStockList);
                }
                if (CollectionUtils.isNotEmpty(updateGroupStockList)) {
                    materialGroupStockMapper.updateList(updateGroupStockList);
                }
            } else {
                materialGroupStockMapper.insertList(groupStockList);
            }
        }
    }

    public String selectRepairProgramName(String repairProgramId) {
        return repairProgramMapper.selectById(repairProgramId).getName();
    }

    @Transactional(rollbackFor = Exception.class)
    public void delMaterialMust(List<String> delLocalMaterialMustId) {
        materialMustListMapper.deleteBatchIds(delLocalMaterialMustId);
    }

    public Map<String, String> selectUserNameMap() {
        return sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().select(SysUser::getId, SysUser::getRealname))
                .stream().collect(Collectors.toMap(SysUser::getRealname, SysUser::getId, (k1, k2) -> k2));
    }

}
