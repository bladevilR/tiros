package org.jeecg.modules.tiros.importer.service;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.dataisolation.bean.LineWorkshopCompany;
import org.jeecg.common.tiros.dataisolation.service.LineWorkshopCompanyService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.group.information.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.group.information.mapper.BuMtrWorkshopGroupMapper;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.tools.bean.BuMaterialTools;
import org.jeecg.modules.material.tools.mapper.BuMaterialToolsMapper;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yfy
 * @title: importMaterialTools
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/14 15:35
 */
@Repository
public class BuMaterialToolsImport {

    @Resource
    private ImporterSql importerSql;
    @Resource
    private BuMtrWorkshopGroupMapper buMtrWorkshopGroupMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private BuMaterialToolsMapper buMaterialToolsMapper;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private LineWorkshopCompanyService lineWorkshopCompanyService;


    @Transactional(rollbackFor = Exception.class)
    public void importMaterialTools(String fileName, InputStream inputStream, Integer type, String lineId, String workshopId) throws Exception {
        Map<String, LineWorkshopCompany> lineWorkshopCompanyMap = lineWorkshopCompanyService.mapLineWorkshopCompany();
        LineWorkshopCompany lineWorkshopCompany = lineWorkshopCompanyMap.get(lineId);
        String companyId = null;
        if (StringUtils.isBlank(workshopId)) {
            workshopId = lineWorkshopCompany.getWorkshopId();
            companyId = lineWorkshopCompany.getCompanyId();
        }

        Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        int lastCellNum = sheet.getRow(0).getLastCellNum();

        List<BuMtrWorkshopGroup> workshopGroupList = buMtrWorkshopGroupMapper.selectList(Wrappers.<BuMtrWorkshopGroup>lambdaQuery().select(BuMtrWorkshopGroup::getId, BuMtrWorkshopGroup::getGroupName));
        Map<String, String> groupMap = new HashMap<>();
        workshopGroupList.stream().forEach(item -> groupMap.put(item.getGroupName(), item.getId()));

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<BuMaterialTools> materialToolsList = new ArrayList<>();
        for (int i = 1; i < lastRowNum; i++) {
            Row row = sheet.getRow(i);
            BuMaterialTools materialTools = null;
            for (int j = 1; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                String cellValue = ExcelUtil.getCellValue(cell).trim();
                if (j == 1) {
                    if (StringUtils.isNotBlank(cellValue)) {
                        materialTools = new BuMaterialTools()
                                .setId(UUIDGenerator.generate())
                                .setMaterialTypeId(cellValue)
                                .setCode(cellValue)
                                .setToolType(type)
                                .setLineId(lineId)
                                .setWorkshopId(workshopId)
                                .setCompanyId(companyId)
                                .setCreateTime(new Date())
                                .setCreateBy(sysUser.getId());
                    } else {
                        break;
                    }
                }
                if (type == 4) {
                    createMaterialToolsGqj(groupMap, materialTools, j, i, cellValue);
                } else {
                    createMaterialToolsGz(groupMap, materialTools, j, cellValue);
                }

            }

            if (materialTools != null) {
                materialToolsList.add(materialTools);
            }
        }

        try {
            if (CollectionUtils.isNotEmpty(materialToolsList)) {
                Set<String> codeList = materialToolsList.stream().map(BuMaterialTools::getCode).filter(item -> StringUtils.isNotBlank(item)).collect(Collectors.toSet());
                Set<String> assetCodeList = materialToolsList.stream().map(BuMaterialTools::getAssetCode).filter(item -> StringUtils.isNotBlank(item)).collect(Collectors.toSet());
                Set<String> userNameList = materialToolsList.stream().map(BuMaterialTools::getUserId).filter(item -> StringUtils.isNotBlank(item)).collect(Collectors.toSet());

                List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectListByCode(codeList);
                Map<String, String> materialTypeMap = new HashMap<>(16);
                materialTypeList.stream().forEach(item -> materialTypeMap.put(item.getCode(), item.getId()));

                List<SysUser> userList = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().select(SysUser::getId, SysUser::getRealname)
                        .in(SysUser::getRealname, userNameList));
                Map<String, String> userMap = new HashMap<>();
                userList.stream().forEach(item -> userMap.put(item.getRealname(), item.getId()));

                List<BuMaterialTools> toolsList = buMaterialToolsMapper.selectListByAssetCode(assetCodeList);
                Map<String, String> toolMap = new HashMap<>();
                toolsList.stream().forEach(item -> toolMap.put(item.getAssetCode(), item.getId()));

                updateMaterialToolsInfo(materialToolsList, toolMap, materialTypeMap, userMap);
                List<List<BuMaterialTools>> batchSubList = DatabaseBatchSubUtil.batchSubList(materialToolsList);
                batchSubList.forEach(item -> buMaterialToolsMapper.insertBatch(item));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JeecgBootException("导入失败");
        }
    }

    public void updateMaterialToolsInfo(List<BuMaterialTools> materialToolsList, Map<String, String> toolMap, Map<String, String> materialTypeMap, Map<String, String> userMap) {
        List<BuMaterialType> materialTypes = new ArrayList<>();
        List<BuMaterialTypeAttr> saveTypeAttrs = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        List<BuMaterialTools> delToolsList = new ArrayList<>();
        Map<String, BuMaterialTools> noMaterialTypeMap = new HashMap<>(16);
        Map<String, String> assetNullMap = new HashMap<>(16);
        materialToolsList.stream().forEach(item -> {
            String materialTypeId = materialTypeMap.get(item.getMaterialTypeId());
            item.setMaterialTypeId(materialTypeId);
            item.setUserId(userMap.get(item.getUserId()));
            item.setSync(0);
            if (StringUtils.isBlank(materialTypeId)) {
                item.setMaterialTypeId(item.getCode());
                noMaterialTypeMap.put(item.getCode(), item);
            }
            String localId = toolMap.get(item.getAssetCode());
            if (StringUtils.isNotBlank(localId)) {
                item.setId(localId);
                delList.add(localId);
            }
            if (StringUtils.isBlank(item.getAssetCode())) {
                if (!assetNullMap.containsKey(item.getAssetCode())) {
                    assetNullMap.put(item.getCode(), "-1");
                } else {
                    delToolsList.add(item);
                }
            }
        });
        materialToolsList.removeAll(delToolsList);
        noMaterialTypeMap.keySet().stream().forEach((item) -> {
            BuMaterialTools tools = noMaterialTypeMap.get(item);
            BuMaterialType materialType = new BuMaterialType()
                    .setId(tools.getCode())
                    .setCode(tools.getCode())
                    .setName(tools.getName())
                    .setSpec(tools.getModel())
                    .setUnit(tools.getUnit())
                    .setPrice(tools.getPrice() == null ? BigDecimal.ZERO : tools.getPrice())
                    .setStatus(1)
                    .setKind(2)
                    .setCategory1(tools.getToolType())
                    .setTheshold(BigDecimal.valueOf(-1))
                    .setIsAsset(tools.getIsFixedAsset())
                    .setIsConsume(2)
                    .setRemark("导入工器具工装时创建")
                    .setCreateTime(new Date());
            materialTypes.add(materialType);
            BuMaterialTypeAttr buMaterialTypeAttr = new BuMaterialTypeAttr()
                    .setId(UUIDGenerator.generate())
                    .setMaterialTypeId(tools.getCode())
                    .setTheshold(-1D);
            saveTypeAttrs.add(buMaterialTypeAttr);
        });
        if (CollectionUtils.isNotEmpty(materialTypes)) {
            buMaterialTypeMapper.insertList(materialTypes);
            importerSql.saveMaterialTypeAttr(saveTypeAttrs);
        }
        if (CollectionUtils.isNotEmpty(delList)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(delList);
            batchSubList.stream().forEach(item -> buMaterialToolsMapper.deleteBatchIds(item));
        }
    }

    private void createMaterialToolsGqj(Map<String, String> groupMap, BuMaterialTools materialTools, int cellIndex, int rowIndex, String cellValue) {
        switch (cellIndex) {
            case 2:
                materialTools.setAssetCode(cellValue);
                break;
           /* case 3:
                materialTools.setRemark(cellValue);
                break;*/
            case 4:
                Map<String, String> nameAndSpec = parseName(cellValue, "");
                materialTools.setName(nameAndSpec.get("name"));
                materialTools.setModel(nameAndSpec.get("spec"));
                break;
           /* case 5:
                materialTools.setUnit(cellValue);
                break;*/
            /*case 6:
                if (StringUtils.isNotBlank(cellValue))
                    cellValue=cellValue.replace(",","");
                    materialTools.setPrice(BigDecimal.valueOf(Double.parseDouble(cellValue)));
                break;*/
            case 5:
                materialTools.setAmount(cellValue);
                break;
            case 18:
                int state = 0;
                if (StringUtils.isNotBlank(cellValue)) {
                    state = cellValue.equals("Y") ? 1 : 0;
                }
                materialTools.setIsFixedAsset(state);
                break;
            case 11:
                materialTools.setUserId(cellValue);
                break;
            case 12:
                String groupId = groupMap.get(cellValue);
                if (StringUtils.isBlank(groupId)) {
                    groupId = "CJ";
                }
                materialTools.setGroupId(groupId);
                break;
            case 14:
                materialTools.setStatus(parseStatus(cellValue));
                break;
            case 6:
                materialTools.setSerialNo(cellValue);
                break;
            case 13:
                if (StringUtils.isNotBlank(cellValue)) {
                    cellValue = cellValue.replace(".", "-");
                    cellValue = cellValue.replace("/", "-");
                    try {
                        materialTools.setEntryDate(DateUtils.parseDate(cellValue, "yyyy-MM-dd HH:mm"));
                    } catch (Exception e) {
                        throw new JeecgBootException("日期格式不正确，在第" + (cellIndex + 1) + "列" + "第" + (rowIndex + 1) + "行");
                    }
                }

                break;
            case 15:
                materialTools.setIsSelfCheck("是".equals(cellValue) ? 1 : 0);
                break;
            case 16:
                if (StringUtils.isNotBlank(cellValue)) {
                    cellValue = cellValue.replace(".", "-");
                    cellValue = cellValue.replace("/", "-");
                    try {
                        materialTools.setLastSelfCheckTime(DateUtils.parseDate(cellValue, "yyyy-MM-dd"));
                    } catch (Exception e) {
                        throw new JeecgBootException("日期格式不正确，在第" + (cellIndex + 1) + "列" + "第" + (rowIndex + 1) + "行");
                    }

                }
                break;
            case 8:
                if (StringUtils.isNotBlank(cellValue)) {
                    cellValue = cellValue.replace(".", "-");
                    cellValue = cellValue.replace("/", "-");
                    try {
                        materialTools.setNextCheckTime(DateUtils.parseDate(cellValue, "yyyy-MM-dd"));
                    } catch (Exception e) {
                        throw new JeecgBootException("日期格式不正确，在第" + (cellIndex + 1) + "列" + "第" + (rowIndex + 1) + "行");
                    }
                }
                break;
            case 17:
                if (StringUtils.isNotBlank(cellValue)) {
                    cellValue = cellValue.replace(".", "-");
                    cellValue = cellValue.replace("/", "-");
                    try {
                        materialTools.setExpirDate(DateUtils.parseDate(cellValue, "yyyy-MM-dd"));
                        materialTools.setIsElectric(1);
                    } catch (Exception e) {
                        throw new JeecgBootException("日期格式不正确，在第" + (cellIndex + 1) + "列" + "第" + (rowIndex + 1) + "行");
                    }
                } else {
                    materialTools.setIsElectric(0);
                }
                break;
            case 19:
                materialTools.setStorageLocation(cellValue);
                break;
            default:
                break;
        }
    }

    private void createMaterialToolsGz(Map<String, String> groupMap, BuMaterialTools materialTools, int j, String cellValue) throws ParseException {
        switch (j) {
            case 2:
                materialTools.setAssetCode(cellValue);
                materialTools.setIsSelfCheck(0);
                break;
            case 3:
                Map<String, String> nameAndSpec = parseName(cellValue, "");
                materialTools.setName(nameAndSpec.get("name"));
                materialTools.setModel(nameAndSpec.get("spec"));
                break;
            case 4:
                materialTools.setRemark(cellValue);
                break;

            case 5:
                materialTools.setUnit(cellValue);
                break;
            case 6:
                materialTools.setAmount(cellValue);
                break;
            case 7:
                int state = 0;
                if (StringUtils.isNotBlank(cellValue)) {
                    state = cellValue.equals("Y") ? 1 : 0;
                }
                materialTools.setIsFixedAsset(state);
                break;
            case 9:
                materialTools.setUserId(cellValue);
                break;
            case 10:
                String groupId = groupMap.get(cellValue);
                if (StringUtils.isBlank(groupId)) {
                    groupId = "CJ";
                }
                materialTools.setGroupId(groupId);
                break;
            case 11:
                materialTools.setStatus(parseStatus(cellValue));
                break;
            default:
                break;
        }
    }


    private Map<String, String> parseName(String name, String spec) {
        Map<String, String> map = new HashMap<>();
        String[] split = name.split("-\\[");
        if (split.length >= 2) {
            name = split[0];
            spec = split[1].trim();
            spec = spec.substring(0, spec.length() - 1);
            spec = spec.replaceFirst("规格：", "").replaceFirst("规格:", "").trim();
        }
        map.put("name", name);
        map.put("spec", spec);
        return map;
    }

    private int parseStatus(String text) {
        int status = 1;
        switch (text) {
            case "使用中":
                status = 1;
                break;
            case "送检中":
                status = 4;
                break;
            case "送修中":
                status = 2;
                break;
            case "停用":
                status = 3;
                break;
            default:
                break;
        }
        return status;
    }

}
