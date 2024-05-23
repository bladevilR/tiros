package org.jeecg.modules.material.material.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.UploadFileCheckUtil;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeReplace;
import org.jeecg.modules.material.material.bean.vo.MaterialReplaceSaveVO;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeReplaceMapper;
import org.jeecg.modules.material.material.service.BuMaterialTypeReplaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 可替换物资 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-07-21
 */
@Slf4j
@Service
public class BuMaterialTypeReplaceServiceImpl extends ServiceImpl<BuMaterialTypeReplaceMapper, BuMaterialTypeReplace> implements BuMaterialTypeReplaceService {

    @Resource
    private BuMaterialTypeReplaceMapper buMaterialTypeReplaceMapper;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;


    /**
     * @see BuMaterialTypeReplaceService#listReplace(String)
     */
    @Override
    public List<BuMaterialType> listReplace(String id) {
        List<String> materialTypeIdList = listCanReplaceMaterialTypeIdByMaterialTypeId(id);
        List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectBatchIds(materialTypeIdList);

        materialTypeList.sort(Comparator.comparing(BuMaterialType::getCode));

        return materialTypeList;
    }

    /**
     * @see BuMaterialTypeReplaceService#saveReplace(MaterialReplaceSaveVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveReplace(MaterialReplaceSaveVO saveVO) {
        String id = saveVO.getId();
        List<BuMaterialType> canReplaceList = saveVO.getCanReplaceList();
        if (null == canReplaceList) {
            canReplaceList = new ArrayList<>();
        }
        List<String> canReplaceMaterialTypeIdList = canReplaceList.stream()
                .map(BuMaterialType::getId)
                .collect(Collectors.toList());

        // 查询旧的
        List<String> materialTypeIdList = listCanReplaceMaterialTypeIdByMaterialTypeId(id);
        // 删除旧的本身
        buMaterialTypeReplaceMapper.deleteById(id);
        // 删除旧的可替换
        if (CollectionUtils.isNotEmpty(materialTypeIdList)) {
            buMaterialTypeReplaceMapper.deleteBatchIds(materialTypeIdList);
        }
        // 删除新的可替换
        if (CollectionUtils.isNotEmpty(canReplaceMaterialTypeIdList)) {
            buMaterialTypeReplaceMapper.deleteBatchIds(canReplaceMaterialTypeIdList);
        }

        if (CollectionUtils.isNotEmpty(canReplaceMaterialTypeIdList)) {
            // 构建新的可替换数据
            Set<String> replaceIdSet = new HashSet<>();
            replaceIdSet.add(id);
            replaceIdSet.addAll(canReplaceMaterialTypeIdList);
            Map<String, Set<String>> idReplaceIdSetMap = new HashMap<>();
            for (String replaceId : replaceIdSet) {
                Set<String> replaceSet = new HashSet<>(replaceIdSet);
                replaceSet.remove(replaceId);

                idReplaceIdSetMap.put(replaceId, replaceSet);
            }
            List<BuMaterialTypeReplace> replaceList = new ArrayList<>();
            for (Map.Entry<String, Set<String>> idReplaceIdSetEntry : idReplaceIdSetMap.entrySet()) {
                String materialTypeId = idReplaceIdSetEntry.getKey();
                List<String> replaceMaterialTypeIdList = new ArrayList<>(idReplaceIdSetEntry.getValue());
                replaceMaterialTypeIdList.sort(Comparator.comparing(String::toString));
                String canReplace = String.join(",", replaceMaterialTypeIdList);

                BuMaterialTypeReplace replace = new BuMaterialTypeReplace()
                        .setId(materialTypeId)
                        .setCanReplace(canReplace);
                replaceList.add(replace);
            }

            // 保存新的
            if (CollectionUtils.isNotEmpty(replaceList)) {
                buMaterialTypeReplaceMapper.insertList(replaceList);
            }
        }

        return true;
    }

    /**
     * @see BuMaterialTypeReplaceService#importMaterialReplace(MultipartFile)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean importMaterialReplace(MultipartFile file) throws IOException {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        if (file.isEmpty()) {
            throw new JeecgBootException("文件为空");
        }
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new JeecgBootException("文件名为空");
        }

        // 读取文件
        InputStream inputStream = file.getInputStream();
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

        List<String> headerList = new ArrayList<>(Arrays.asList("物资编码", "其它编码1", "其它编码2", "其它编码3", "其它编码4"));
        boolean hasMatchingForms = false;
        List<Set<String>> replaceSetList = new ArrayList<>();
        // 操作每个表单
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer materialCodeCellNum = null;
            Integer materialCode1CellNum = null;
            Integer materialCode2CellNum = null;
            Integer materialCode3CellNum = null;
            Integer materialCode4CellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = getCellValue(cell);
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }

                        if (value.equals(headerList.get(0))) {
                            materialCodeCellNum = cellNum;
                        } else if (value.equals(headerList.get(1))) {
                            materialCode1CellNum = cellNum;
                        } else if (value.equals(headerList.get(2))) {
                            materialCode2CellNum = cellNum;
                        } else if (value.equals(headerList.get(3))) {
                            materialCode3CellNum = cellNum;
                        } else if (value.equals(headerList.get(4))) {
                            materialCode4CellNum = cellNum;
                        }
                    }
                }
            }

            if (null == materialCodeCellNum) {
                log.error(String.format("第%s个表单%s,没有必要信息,不进行处理", sheetNum + 1, sheet.getSheetName()));
            } else {
                log.info(String.format("第%s个表单%s,有必要信息,开始导入", sheetNum + 1, sheet.getSheetName()));

                hasMatchingForms = true;
                int rowCount = 0;

                // 操作每行数据
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    String materialCode = getCellValue(sheet.getRow(rowNum).getCell(materialCodeCellNum));
                    String materialCode1 = getCellValue(sheet.getRow(rowNum).getCell(materialCode1CellNum));
                    String materialCode2 = getCellValue(sheet.getRow(rowNum).getCell(materialCode2CellNum));
                    String materialCode3 = getCellValue(sheet.getRow(rowNum).getCell(materialCode3CellNum));
                    String materialCode4 = getCellValue(sheet.getRow(rowNum).getCell(materialCode4CellNum));


                    addToReplaceSetList(replaceSetList, materialCode, materialCode1);
                    addToReplaceSetList(replaceSetList, materialCode, materialCode2);
                    addToReplaceSetList(replaceSetList, materialCode, materialCode3);
                    addToReplaceSetList(replaceSetList, materialCode, materialCode4);

                    rowCount++;
                }
                log.info("总共行数" + rowCount);
            }
        }

        // 如果没有匹配到模板表单，返回异常
        if (!hasMatchingForms) {
            log.error("文件【" + file.getOriginalFilename() + "】未匹配到配置导入模板表单");
            throw new JeecgBootException("请选择正确的模板表单，必须包含表头：" + headerList.get(0));
        } else {
            String saveConfigInfo = dealSaveByReplaceSetList(replaceSetList);
            log.info(saveConfigInfo);
            return true;
        }
    }

    /**
     * @see BuMaterialTypeReplaceService#listCanReplaceMaterialTypeIdByMaterialTypeId(String)
     */
    @Override
    public List<String> listCanReplaceMaterialTypeIdByMaterialTypeId(String materialTypeId) {
        BuMaterialTypeReplace replace = buMaterialTypeReplaceMapper.selectById(materialTypeId);
        if (null == replace || StringUtils.isBlank(replace.getCanReplace())) {
            return new ArrayList<>();
        }

        String canReplace = replace.getCanReplace();
        return new ArrayList<>(Arrays.asList(canReplace.split(",")));
    }

    /**
     * @see BuMaterialTypeReplaceService#listCanReplaceMaterialTypeIdByMaterialTypeText(String)
     */
    @Override
    public List<String> listCanReplaceMaterialTypeIdByMaterialTypeText(String materialTypeText) {
        if (StringUtils.isBlank(materialTypeText)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                .like(BuMaterialType::getCode, materialTypeText)
                .or()
                .like(BuMaterialType::getName, materialTypeText);
        List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);
        if (CollectionUtils.isNotEmpty(materialTypeList)) {
            Set<String> materialTypeIdSet = new HashSet<>();

            for (BuMaterialType materialType : materialTypeList) {
                String materialTypeId = materialType.getId();
                List<String> canReplaceMaterialTypeIdList = listCanReplaceMaterialTypeIdByMaterialTypeId(materialTypeId);

                materialTypeIdSet.add(materialTypeId);
                materialTypeIdSet.addAll(canReplaceMaterialTypeIdList);
            }

            List<String> materialTypeIdList = new ArrayList<>(materialTypeIdSet);
            materialTypeIdList.sort(Comparator.comparing(String::toString));
            return materialTypeIdList;
        }

        return new ArrayList<>();
    }

    /**
     * @see BuMaterialTypeReplaceService#listSelfMaterialTypeIdByMaterialTypeText(String)
     */
    @Override
    public List<String> listSelfMaterialTypeIdByMaterialTypeText(String materialTypeText) {
        if (StringUtils.isBlank(materialTypeText)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                .like(BuMaterialType::getCode, materialTypeText)
                .or()
                .like(BuMaterialType::getName, materialTypeText);
        List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);
        if (CollectionUtils.isNotEmpty(materialTypeList)) {
            return materialTypeList.stream()
                    .map(BuMaterialType::getId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .sorted(Comparator.comparing(String::toString))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    /**
     * @see BuMaterialTypeReplaceService#mapCanReplaceMaterialTypeIdListByMaterialTypeIdList(List)
     */
    @Override
    public Map<String, List<String>> mapCanReplaceMaterialTypeIdListByMaterialTypeIdList(List<String> materialTypeIdList) {
        if (CollectionUtils.isEmpty(materialTypeIdList)) {
            return new HashMap<>();
        }
        List<BuMaterialTypeReplace> replaceList = buMaterialTypeReplaceMapper.selectBatchIds(materialTypeIdList);

        Map<String, List<String>> materialTypeIdReplaceIdListMap = new HashMap<>();
        for (String materialTypeId : materialTypeIdList) {
            Set<String> replaceIdSet = new HashSet<>();

            for (BuMaterialTypeReplace replace : replaceList) {
                if (materialTypeId.equals(replace.getId()) && StringUtils.isNotBlank(replace.getCanReplace())) {
                    replaceIdSet.addAll(Arrays.asList(replace.getCanReplace().split(",")));
                }
            }

            List<String> replaceIdList = new ArrayList<>(replaceIdSet);
            replaceIdList.sort(Comparator.comparing(String::toString));
            materialTypeIdReplaceIdListMap.put(materialTypeId, replaceIdList);
        }
        return materialTypeIdReplaceIdListMap;
    }


    private String getCellValue(Cell cell) {
        String value = "";
        if (null == cell) {
            return value;
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
                break;
        }

        return value.trim();
    }

    private void addToReplaceSetList(List<Set<String>> replaceSetList, String materialTypeId, String replaceMaterialTypeId) {
        if (StringUtils.isBlank(materialTypeId) || StringUtils.isBlank(replaceMaterialTypeId)) {
            return;
        }

        boolean match = false;
        for (Set<String> replaceSet : replaceSetList) {
            if (replaceSet.contains(materialTypeId) || replaceSet.contains(replaceMaterialTypeId)) {
                replaceSet.add(materialTypeId);
                replaceSet.add(replaceMaterialTypeId);
                match = true;
            }
        }
        if (!match) {
            Set<String> replaceSet = new HashSet<>();
            replaceSet.add(materialTypeId);
            replaceSet.add(replaceMaterialTypeId);
            replaceSetList.add(replaceSet);
        }
    }

    private String dealSaveByReplaceSetList(List<Set<String>> replaceSetList) {
        if (CollectionUtils.isEmpty(replaceSetList)) {
            return "excel中没有需要导入的配置信息";
        }

        List<BuMaterialTypeReplace> replaceList = new ArrayList<>();
        Set<String> needDeleteIdSet = new HashSet<>();
        for (Set<String> replaceSet : replaceSetList) {
            for (String materialTypeId : replaceSet) {
                Set<String> copySet = new HashSet<>(replaceSet);
                copySet.remove(materialTypeId);

                List<String> replaceMaterialTypeIdList = new ArrayList<>(copySet);
                replaceMaterialTypeIdList.sort(Comparator.comparing(String::toString));
                String canReplace = String.join(",", replaceMaterialTypeIdList);

                BuMaterialTypeReplace replace = new BuMaterialTypeReplace()
                        .setId(materialTypeId)
                        .setCanReplace(canReplace);
                replaceList.add(replace);
            }

            needDeleteIdSet.addAll(replaceSet);
        }

        // 删除旧的
        int deleteCount = 0;
        if (CollectionUtils.isNotEmpty(needDeleteIdSet)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(needDeleteIdSet));
            for (List<String> batchSub : batchSubList) {
                int delete = buMaterialTypeReplaceMapper.deleteBatchIds(batchSub);
                deleteCount = deleteCount + delete;
            }
        }
        // 保存新的
        if (CollectionUtils.isNotEmpty(replaceList)) {
            Set<String> set = new HashSet<>();
            List<BuMaterialTypeReplace> list = new ArrayList<>();
            for (BuMaterialTypeReplace replace : replaceList) {
                String id = replace.getId();
                if (!set.contains(id)) {
                    set.add(id);
                } else {
                    list.add(replace);
                }
            }

            System.out.println("重复数量:" + list.size());
            System.out.println(JSON.toJSONString(list));

            buMaterialTypeReplaceMapper.insertList(replaceList);
        }

        return String.format("删除了%s条旧的可替换，保存了%s条新的可替换",
                deleteCount, replaceList.size());
    }

}
