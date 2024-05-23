package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuMaterialTools;
import org.jeecg.modules.third.jdx.bean.BuMaterialSparePart;
import org.jeecg.modules.third.jdx.bean.BuMaterialType;
import org.jeecg.modules.third.jdx.mapper.BuMaterialToolsThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuMaterialSparePartThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuMaterialTypeThirdMapper;
import org.jeecg.modules.third.jdx.mapper.ThirdCommonMapper;
import org.jeecg.modules.third.jdx.bean.BuMtrLine;
import org.jeecg.modules.third.jdx.bean.bo.LineWorkshopCompany;
import org.jeecg.modules.third.jdx.mapper.*;
import org.jeecg.modules.third.jdx.service.BuMaterialToolsThirdService;
import org.jeecg.modules.third.maximo.bean.JdxRealassetOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工具信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-09
 */
@Slf4j
@Service
public class BuMaterialToolsThirdServiceImpl extends ServiceImpl<BuMaterialToolsThirdMapper, BuMaterialTools> implements BuMaterialToolsThirdService {

    @Resource
    private BuMaterialToolsThirdMapper buMaterialToolsThirdMapper;
    @Resource
    private BuMaterialSparePartThirdMapper buMaterialSparePartThirdMapper;
    @Resource
    private BuMaterialTypeThirdMapper buMaterialTypeThirdMapper;
    @Resource
    private ThirdCommonMapper thirdCommonMapper;
    @Resource
    private BuTrainInfoThirdMapper buTrainInfoThirdMapper;


    /**
     * @see BuMaterialToolsThirdService#insertToolFromMaximoData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertToolFromMaximoData(List<JdxRealassetOut> allList) throws Exception {
        if (CollectionUtils.isEmpty(allList)) {
            return true;
        }

        String sourceScene = "初始化";

        // 查询物质类型
        Map<String, BuMaterialType> codeMaterialTypeMap = getCodeMaterialTypeMap(allList);
        // 查询部门
        Map<String, String> departMaximoCodeIdMap = getDepartMaximoCodeIdMap();
        // 查询人员
        Map<String, String> userWorkNoIdMap = getUserWorkNoIdMap();
        // 查询线路车间公司
        Map<String, LineWorkshopCompany> lineWorkshopCompanyMap = getLineWorkshopCompanyMap();

        // 根据类型区分是工器具还是列管备件，分开处理
        List<JdxRealassetOut> maximoToolList = new ArrayList<>();
        List<JdxRealassetOut> maximoSparePartList = new ArrayList<>();
        for (JdxRealassetOut maximoTool : allList) {
            String realassettype = maximoTool.getRealassettype();
            if ("TOOLS".equalsIgnoreCase(realassettype)) {
                maximoToolList.add(maximoTool);
            } else if ("SPAREPART".equalsIgnoreCase(realassettype)) {
                maximoSparePartList.add(maximoTool);
            }
        }
        // 工器具
        Set<String> validToolIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(maximoToolList)) {
            // 查询旧的工器具
            Map<String, BuMaterialTools> assetCodeToolMap = new HashMap<>();
            List<String> assetCodeList = maximoToolList.stream()
                    .map(JdxRealassetOut::getRealassetnum)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(assetCodeList)) {
                List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(assetCodeList);
                for (List<String> batchSub : batchSubList) {
                    LambdaQueryWrapper<BuMaterialTools> toolWrapper = new LambdaQueryWrapper<BuMaterialTools>()
                            .in(BuMaterialTools::getAssetCode, batchSub);
                    List<BuMaterialTools> subToolList = buMaterialToolsThirdMapper.selectList(toolWrapper);
                    subToolList.forEach(tool -> assetCodeToolMap.put(tool.getAssetCode(), tool));
                }
            }

            Map<String, BuMaterialTools> needAddIdToolMap = new HashMap<>();
            Map<String, BuMaterialTools> needUpdateIdToolMap = new HashMap<>();
            List<BuMaterialType> needAddMaterialTypeList = new ArrayList<>();
            List<BuMaterialType> needUpdateMaterialTypeList = new ArrayList<>();
            for (JdxRealassetOut maximoTool : maximoToolList) {
                String transAction = maximoTool.getTransAction();
                if ("Delete".equals(transAction)) {
                    // 删除
                    // 资产编码
                    String assetCode = maximoTool.getRealassetnum();
                    BuMaterialTools tool = assetCodeToolMap.get(assetCode);
                    if (null != tool) {
                        needAddIdToolMap.remove(tool.getId());
                        needUpdateIdToolMap.remove(tool.getId());
                    }
                } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                    // 新增或更新
                    // 获取物资类型（不存在新增、存在更新价格）
                    BuMaterialType materialType = getOrAddMaterialType(maximoTool, codeMaterialTypeMap, needAddMaterialTypeList, needUpdateMaterialTypeList);
                    // 资产编码
                    String assetCode = maximoTool.getRealassetnum();
                    BuMaterialTools tool = assetCodeToolMap.get(assetCode);
                    if (null == tool) {
                        // 工器具不存在新增工器具
                        tool = buildNewTool(maximoTool, materialType, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
                        // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                        needAddIdToolMap.put(tool.getId(), tool);

                        assetCodeToolMap.put(tool.getAssetCode(), tool);
                    } else {
                        // 工器具存在更新数据
                        updateOldTool(tool, maximoTool, materialType, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
                        // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                        needUpdateIdToolMap.put(tool.getId(), tool);
                    }
                }
            }

            // 保存数据
            saveMaterialTypeData(needAddMaterialTypeList, needUpdateMaterialTypeList, sourceScene);
            saveToolData(maximoToolList, needAddIdToolMap, needUpdateIdToolMap, sourceScene);

            // 新增或修改的工器具是有效的
            validToolIdSet.addAll(needAddIdToolMap.keySet());
            validToolIdSet.addAll(needUpdateIdToolMap.keySet());

            // 日志中记录本次处理信息
            log.info(String.format(sourceScene + "从maximo同步数据--工器具：处理工器具信息%s条（新增%s，更新%s）",
                    maximoToolList.size(),
                    needAddIdToolMap.size(),
                    needUpdateIdToolMap.size()));
        }
        // 删除工器具旧数据
        deleteInvalidTool(validToolIdSet, sourceScene);
        // 列管备件
        Set<String> validSparePartIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(maximoSparePartList)) {
            // 查询旧的列管备件
            Map<String, BuMaterialSparePart> assetCodeSparePartMap = new HashMap<>();
            List<String> assetCodeList = maximoSparePartList.stream()
                    .map(JdxRealassetOut::getRealassetnum)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(assetCodeList)) {
                List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(assetCodeList);
                for (List<String> batchSub : batchSubList) {
                    LambdaQueryWrapper<BuMaterialSparePart> sparePartWrapper = new LambdaQueryWrapper<BuMaterialSparePart>()
                            .in(BuMaterialSparePart::getAssetCode, batchSub);
                    List<BuMaterialSparePart> subSparePartList = buMaterialSparePartThirdMapper.selectList(sparePartWrapper);
                    subSparePartList.forEach(sparePart -> assetCodeSparePartMap.put(sparePart.getAssetCode(), sparePart));
                }
            }

            Map<String, BuMaterialSparePart> needAddIdSparePartMap = new HashMap<>();
            Map<String, BuMaterialSparePart> needUpdateIdSparePartMap = new HashMap<>();
            List<BuMaterialType> needAddMaterialTypeList = new ArrayList<>();
            List<BuMaterialType> needUpdateMaterialTypeList = new ArrayList<>();
            for (JdxRealassetOut maximoSparePart : maximoSparePartList) {
                String transAction = maximoSparePart.getTransAction();
                if ("Delete".equals(transAction)) {
                    // 删除
                    // 资产编码
                    String assetCode = maximoSparePart.getRealassetnum();
                    BuMaterialSparePart sparePart = assetCodeSparePartMap.get(assetCode);
                    if (null != sparePart) {
                        needAddIdSparePartMap.remove(sparePart.getId());
                        needUpdateIdSparePartMap.remove(sparePart.getId());
                    }
                } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                    // 新增或更新
                    // 获取物资类型（不存在新增、存在更新价格）
                    BuMaterialType materialType = getOrAddMaterialType(maximoSparePart, codeMaterialTypeMap, needAddMaterialTypeList, needUpdateMaterialTypeList);
                    // 资产编码
                    String assetCode = maximoSparePart.getRealassetnum();
                    BuMaterialSparePart sparePart = assetCodeSparePartMap.get(assetCode);
                    if (null == sparePart) {
                        // 列管备件不存在新增列管备件
                        sparePart = buildNewSparePart(maximoSparePart, materialType, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
                        // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                        needAddIdSparePartMap.put(sparePart.getId(), sparePart);

                        assetCodeSparePartMap.put(sparePart.getAssetCode(), sparePart);
                    } else {
                        // 列管备件存在更新数据
                        updateOldSparePart(sparePart, maximoSparePart, materialType, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
                        // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                        needUpdateIdSparePartMap.put(sparePart.getId(), sparePart);
                    }
                }
            }

            // 保存数据
            saveMaterialTypeData(needAddMaterialTypeList, needUpdateMaterialTypeList, sourceScene);
            saveSparePartData(maximoSparePartList, needAddIdSparePartMap, needUpdateIdSparePartMap, sourceScene);

            // 新增或修改的工器具是有效的
            validSparePartIdSet.addAll(needAddIdSparePartMap.keySet());
            validSparePartIdSet.addAll(needUpdateIdSparePartMap.keySet());

            // 日志中记录本次处理信息
            log.info(String.format(sourceScene + "从maximo同步数据--列管备件：处理列管备件信息%s条（新增%s，更新%s）",
                    maximoSparePartList.size(),
                    needAddIdSparePartMap.size(),
                    needUpdateIdSparePartMap.size()));
        }
        // 删除列管备件旧数据
        deleteInvalidSparePart(validSparePartIdSet, sourceScene);

        return true;
    }

    /**
     * @see BuMaterialToolsThirdService#consumeMaximoTransChangeData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean consumeMaximoTransChangeData(List<JdxRealassetOut> changeList) throws Exception {
        if (CollectionUtils.isEmpty(changeList)) {
            return true;
        }

        String sourceScene = "定时";

        // 查询物质类型
        Map<String, BuMaterialType> codeMaterialTypeMap = getCodeMaterialTypeMap(changeList);
        // 查询部门
        Map<String, String> departMaximoCodeIdMap = getDepartMaximoCodeIdMap();
        // 查询人员
        Map<String, String> userWorkNoIdMap = getUserWorkNoIdMap();
        // 查询线路车间公司
        Map<String, LineWorkshopCompany> lineWorkshopCompanyMap = getLineWorkshopCompanyMap();

        // 根据类型区分是工器具还是列管备件，分开处理
        List<JdxRealassetOut> maximoToolList = new ArrayList<>();
        List<JdxRealassetOut> maximoSparePartList = new ArrayList<>();
        for (JdxRealassetOut maximoTool : changeList) {
            String realassettype = maximoTool.getRealassettype();
            if ("TOOLS".equalsIgnoreCase(realassettype)) {
                maximoToolList.add(maximoTool);
            } else if ("SPAREPART".equalsIgnoreCase(realassettype)) {
                maximoSparePartList.add(maximoTool);
            }
        }
        // 工器具
        if (CollectionUtils.isNotEmpty(maximoToolList)) {
            // 查询旧的工器具
            Map<String, BuMaterialTools> assetCodeToolMap = new HashMap<>();
            List<String> assetCodeList = maximoToolList.stream()
                    .map(JdxRealassetOut::getRealassetnum)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(assetCodeList)) {
                List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(assetCodeList);
                for (List<String> batchSub : batchSubList) {
                    LambdaQueryWrapper<BuMaterialTools> toolWrapper = new LambdaQueryWrapper<BuMaterialTools>()
                            .in(BuMaterialTools::getAssetCode, batchSub);
                    List<BuMaterialTools> subToolList = buMaterialToolsThirdMapper.selectList(toolWrapper);
                    subToolList.forEach(tool -> assetCodeToolMap.put(tool.getAssetCode(), tool));
                }
            }

            Map<String, BuMaterialTools> needAddIdToolMap = new HashMap<>();
            Map<String, BuMaterialTools> needUpdateIdToolMap = new HashMap<>();
            List<BuMaterialType> needAddMaterialTypeList = new ArrayList<>();
            List<BuMaterialType> needUpdateMaterialTypeList = new ArrayList<>();
            for (JdxRealassetOut maximoTool : maximoToolList) {
                String transAction = maximoTool.getTransAction();
                if ("Delete".equals(transAction)) {
                    // 删除
                    // 资产编码
                    String assetCode = maximoTool.getRealassetnum();
                    BuMaterialTools tool = assetCodeToolMap.get(assetCode);
                    if (null != tool) {
                        needAddIdToolMap.remove(tool.getId());
                        needUpdateIdToolMap.remove(tool.getId());
                    }
                } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                    // 新增或更新
                    // 获取物资类型（不存在新增、存在更新价格）
                    BuMaterialType materialType = getOrAddMaterialType(maximoTool, codeMaterialTypeMap, needAddMaterialTypeList, needUpdateMaterialTypeList);
                    // 资产编码
                    String assetCode = maximoTool.getRealassetnum();
                    BuMaterialTools tool = assetCodeToolMap.get(assetCode);
                    if (null == tool) {
                        // 工器具不存在新增工器具
                        tool = buildNewTool(maximoTool, materialType, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
                        // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                        needAddIdToolMap.put(tool.getId(), tool);
                    } else {
                        // 工器具存在更新数据
                        updateOldTool(tool, maximoTool, materialType, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
                        // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                        needUpdateIdToolMap.put(tool.getId(), tool);
                    }
                }
            }

            // 保存数据
            saveMaterialTypeData(needAddMaterialTypeList, needUpdateMaterialTypeList, sourceScene);
            saveToolData(maximoToolList, needAddIdToolMap, needUpdateIdToolMap, sourceScene);

            // 日志中记录本次处理信息
            log.info(String.format(sourceScene + "从maximo同步数据--工器具：处理工器具信息%s条（新增%s，更新%s）",
                    maximoToolList.size(),
                    needAddIdToolMap.size(),
                    needUpdateIdToolMap.size()));
        }
        // 列管备件
        if (CollectionUtils.isNotEmpty(maximoSparePartList)) {
            // 查询旧的列管备件
            Map<String, BuMaterialSparePart> assetCodeSparePartMap = new HashMap<>();
            List<String> assetCodeList = maximoSparePartList.stream()
                    .map(JdxRealassetOut::getRealassetnum)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(assetCodeList)) {
                List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(assetCodeList);
                for (List<String> batchSub : batchSubList) {
                    LambdaQueryWrapper<BuMaterialSparePart> sparePartWrapper = new LambdaQueryWrapper<BuMaterialSparePart>()
                            .in(BuMaterialSparePart::getAssetCode, batchSub);
                    List<BuMaterialSparePart> subSparePartList = buMaterialSparePartThirdMapper.selectList(sparePartWrapper);
                    subSparePartList.forEach(sparePart -> assetCodeSparePartMap.put(sparePart.getAssetCode(), sparePart));
                }
            }

            Map<String, BuMaterialSparePart> needAddIdSparePartMap = new HashMap<>();
            Map<String, BuMaterialSparePart> needUpdateIdSparePartMap = new HashMap<>();
            List<BuMaterialType> needAddMaterialTypeList = new ArrayList<>();
            List<BuMaterialType> needUpdateMaterialTypeList = new ArrayList<>();
            for (JdxRealassetOut maximoSparePart : maximoSparePartList) {
                String transAction = maximoSparePart.getTransAction();
                if ("Delete".equals(transAction)) {
                    // 删除
                    // 资产编码
                    String assetCode = maximoSparePart.getRealassetnum();
                    BuMaterialSparePart sparePart = assetCodeSparePartMap.get(assetCode);
                    if (null != sparePart) {
                        needAddIdSparePartMap.remove(sparePart.getId());
                        needUpdateIdSparePartMap.remove(sparePart.getId());
                    }
                } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                    // 新增或更新
                    // 获取物资类型（不存在新增、存在更新价格）
                    BuMaterialType materialType = getOrAddMaterialType(maximoSparePart, codeMaterialTypeMap, needAddMaterialTypeList, needUpdateMaterialTypeList);
                    // 资产编码
                    String assetCode = maximoSparePart.getRealassetnum();
                    BuMaterialSparePart sparePart = assetCodeSparePartMap.get(assetCode);
                    if (null == sparePart) {
                        // 列管备件不存在新增列管备件
                        sparePart = buildNewSparePart(maximoSparePart, materialType, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
                        // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                        needAddIdSparePartMap.put(sparePart.getId(), sparePart);
                    } else {
                        // 列管备件存在更新数据
                        updateOldSparePart(sparePart, maximoSparePart, materialType, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
                        // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                        needUpdateIdSparePartMap.put(sparePart.getId(), sparePart);
                    }
                }
            }

            // 保存数据
            saveMaterialTypeData(needAddMaterialTypeList, needUpdateMaterialTypeList, sourceScene);
            saveSparePartData(maximoSparePartList, needAddIdSparePartMap, needUpdateIdSparePartMap, sourceScene);

            // 日志中记录本次处理信息
            log.info(String.format(sourceScene + "从maximo同步数据--列管备件：处理列管备件信息%s条（新增%s，更新%s）",
                    maximoSparePartList.size(),
                    needAddIdSparePartMap.size(),
                    needUpdateIdSparePartMap.size()));
        }

        return true;
    }


    private Map<String, BuMaterialType> getCodeMaterialTypeMap(List<JdxRealassetOut> jdxRealassetOutList) {
        Map<String, BuMaterialType> codeMaterialTypeMap = new HashMap<>();

        List<String> itemnumList = jdxRealassetOutList.stream()
                .map(JdxRealassetOut::getItemnum)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(itemnumList)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(itemnumList);
            for (List<String> batchSub : batchSubList) {
                LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                        .in(BuMaterialType::getCode, batchSub);
                List<BuMaterialType> subMaterialTypeList = buMaterialTypeThirdMapper.selectList(materialTypeWrapper);
                subMaterialTypeList.forEach(materialType -> codeMaterialTypeMap.put(materialType.getCode(), materialType));
            }
        }

        return codeMaterialTypeMap;
    }

    private Map<String, String> getUserWorkNoIdMap() {
        Map<String, String> userWorkNoIdMap = new HashMap<>();

        List<Map<String, Object>> userIdAndWorkNoList = thirdCommonMapper.selectUserIdAndWorkNo();
        if (CollectionUtils.isNotEmpty(userIdAndWorkNoList)) {
            for (Map<String, Object> userIdAndWorkNo : userIdAndWorkNoList) {
                Object id = userIdAndWorkNo.get("id");
                Object workNo = userIdAndWorkNo.get("workNo");

                if (null != id && null != workNo) {
                    userWorkNoIdMap.put((String) workNo, (String) id);
                }
            }
        }

        return userWorkNoIdMap;
    }

    private Map<String, LineWorkshopCompany> getLineWorkshopCompanyMap() {
        Map<String, LineWorkshopCompany> lineWorkshopCompanyMap = new HashMap<>();

        List<BuMtrLine> lineList = buTrainInfoThirdMapper.selectAllLine();
        for (BuMtrLine line : lineList) {
            String lineId = line.getLineId();
            String workshopId = line.getWorkshopId();
            String companyId = line.getCompanyId();
            String depotId = line.getDepotId();

            LineWorkshopCompany lineWorkshopCompany = new LineWorkshopCompany()
                    .setLineId(lineId)
                    .setWorkshopId(workshopId)
                    .setCompanyId(companyId)
                    .setDepotId(depotId);
            lineWorkshopCompanyMap.put(lineId, lineWorkshopCompany);
        }

        return lineWorkshopCompanyMap;
    }

    private Map<String, String> getDepartMaximoCodeIdMap() {
        Map<String, String> departMaximoCodeIdMap = new HashMap<>();

        List<Map<String, Object>> departIdAndMaximoCodeList = thirdCommonMapper.selectDepartIdAndMaximoCode();
        if (CollectionUtils.isNotEmpty(departIdAndMaximoCodeList)) {
            for (Map<String, Object> departIdAndMaximoCode : departIdAndMaximoCodeList) {
                Object id = departIdAndMaximoCode.get("id");
                Object maximoCode = departIdAndMaximoCode.get("maximoCode");

                if (null != id && null != maximoCode) {
                    departMaximoCodeIdMap.put((String) maximoCode, (String) id);
                }
            }
        }

        return departMaximoCodeIdMap;
    }

    private BuMaterialType getOrAddMaterialType(JdxRealassetOut maximoTool,
                                                Map<String, BuMaterialType> codeMaterialTypeMap,
                                                List<BuMaterialType> needAddMaterialTypeList,
                                                List<BuMaterialType> needUpdateMaterialTypeList) {
        // 物资编码
        String materialTypeCode = maximoTool.getItemnum();
        // 价格
        BigDecimal price = null == maximoTool.getPurchaseprice() ? BigDecimal.ZERO : BigDecimal.valueOf(maximoTool.getPurchaseprice());

        // 属性转换
        String spec = null;
        String name = maximoTool.getDescription();
        String[] split = name.split("-\\[");
        if (split.length >= 2) {
            name = split[0];
            spec = split[1].trim();
            spec = spec.substring(0, spec.length() - 1);
            spec = spec.replaceFirst("规格：", "").replaceFirst("规格:", "").trim();
        }

        BuMaterialType materialType = codeMaterialTypeMap.get(materialTypeCode);
        if (null == materialType) {
            // 物资数据不存在增加物资
            // 物资属性转换
            int statusInt = 1;// 有效
            int kindInt = 2;// 工器具
            int category1 = 4;// 工器具
            String category2 = null;
            int isConsumeInt = 2;// 列管物资

            materialType = new BuMaterialType()
                    .setId(materialTypeCode)
                    .setCode(materialTypeCode)
                    .setName(name)
                    .setSpec(spec)
                    .setUnit("个")
                    .setPrice(price)
                    .setStatus(statusInt)
                    .setKind(kindInt)
                    .setCategory1(category1)
                    .setCategory2(category2)
                    .setCategory3("工器具")
                    .setTheshold(new BigDecimal(-1))
                    .setIsConsume(isConsumeInt)
                    .setIsAsset(0)
                    .setFromHead(0)
                    .setRemark("maximo导入工器具时创建；");

            needAddMaterialTypeList.add(materialType);
            codeMaterialTypeMap.put(materialType.getCode(), materialType);
        } else {
            // 物资数据存在更新对应物资类型的价格：价格大于0才更新，且价格跟原价格不一致时
            BigDecimal oldPrice = null == materialType.getPrice() ? BigDecimal.ZERO : materialType.getPrice();
            if (BigDecimal.ZERO.compareTo(price) < 0 && oldPrice.compareTo(price) != 0) {
                BuMaterialType updateMaterialType = new BuMaterialType()
                        .setId(materialType.getId())
                        .setPrice(price);
                needUpdateMaterialTypeList.add(updateMaterialType);
            }
        }

        return materialType;
    }

    private BuMaterialTools buildNewTool(JdxRealassetOut maximoTool,
                                         BuMaterialType materialType,
                                         Map<String, String> departMaximoCodeIdMap,
                                         Map<String, String> userWorkNoIdMap,
                                         Map<String, LineWorkshopCompany> lineWorkshopCompanyMap) {
        // 资产编码
        String assetCode = maximoTool.getRealassetnum();

        // 工器具属性转换
        int statusInt = getStatusInt(maximoTool.getStatus());
        BuMaterialTools tool = new BuMaterialTools()
                .setId(assetCode)
                .setToolType(4)
                .setMaterialTypeId(materialType.getId())
                .setCode(materialType.getCode())
                .setAssetCode(assetCode)
                .setSerialNo(null)
                .setName(materialType.getName())
                .setModel(materialType.getSpec())
                .setSupplierId(null)
                .setEntryDate(maximoTool.getPurchasedate())
                .setLifetime(null)
                .setLeaveFactory(null)
                .setExpirDate(null)
                .setWarehouseId(null)
                .setStatus(statusInt)
                .setServiceInterval(null)
                .setLastCheckTime(maximoTool.getCallastdate())
                .setNextCheckTime(null)
                .setIsSelfCheck(0)
                .setRemark("maximo导入；")
                .setSync(1);
        // 设置线路、车间、班组、人员
        setLineOrgUserProperty(tool, maximoTool, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);

        return tool;
    }

    private void updateOldTool(BuMaterialTools tool,
                               JdxRealassetOut maximoTool,
                               BuMaterialType materialType,
                               Map<String, String> departMaximoCodeIdMap,
                               Map<String, String> userWorkNoIdMap,
                               Map<String, LineWorkshopCompany> lineWorkshopCompanyMap) {
        // 工器具属性转换
        int statusInt = getStatusInt(maximoTool.getStatus());

        tool.setName(materialType.getName())
                .setModel(materialType.getSpec())
                .setEntryDate(maximoTool.getPurchasedate())
                .setStatus(statusInt)
                .setLastCheckTime(maximoTool.getCallastdate())
                .setRemark("maximo更新同步修改；");
        // 设置线路、车间、班组、人员
        setLineOrgUserProperty(tool, maximoTool, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
    }

    private BuMaterialSparePart buildNewSparePart(JdxRealassetOut maximoSparePart,
                                                  BuMaterialType materialType,
                                                  Map<String, String> departMaximoCodeIdMap,
                                                  Map<String, String> userWorkNoIdMap,
                                                  Map<String, LineWorkshopCompany> lineWorkshopCompanyMap) {
        // 资产编码
        String assetCode = maximoSparePart.getRealassetnum();

        // 列管备件属性转换
        int statusInt = getStatusInt(maximoSparePart.getStatus());
        BuMaterialSparePart sparePart = new BuMaterialSparePart()
                .setId(assetCode)
                .setMaterialTypeId(materialType.getId())
                .setMaterialCode(materialType.getCode())
                .setAssetCode(assetCode)
                .setManufNo(null)
                .setName(materialType.getName())
                .setModel(materialType.getSpec())
                .setBrand(null)
                .setLeaveFactory(null)
                .setExpirDate(null)
                .setSupplierId(null)
                .setPrice(materialType.getPrice())
                .setSystemId(null)
                .setAssetTypeId(null)
                .setStatus(statusInt)
                .setWarehouseId(null)
                .setOutDate(maximoSparePart.getPurchasedate())
                .setOutOrderNo(null)
                .setCurrentLocation(null);
        // 设置线路、车间、班组、人员
        setLineOrgUserProperty(sparePart, maximoSparePart, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);

        return sparePart;
    }

    private void updateOldSparePart(BuMaterialSparePart sparePart,
                                    JdxRealassetOut maximoSparePart,
                                    BuMaterialType materialType,
                                    Map<String, String> departMaximoCodeIdMap,
                                    Map<String, String> userWorkNoIdMap,
                                    Map<String, LineWorkshopCompany> lineWorkshopCompanyMap) {
        // 工器具属性转换
        int statusInt = getStatusInt(maximoSparePart.getStatus());

        sparePart
                .setMaterialTypeId(materialType.getId())
                .setMaterialCode(materialType.getCode())
                .setName(materialType.getName())
                .setModel(materialType.getSpec())
                .setPrice(materialType.getPrice())
                .setStatus(statusInt)
                .setOutDate(maximoSparePart.getPurchasedate());
        // 设置线路、车间、班组、人员
        setLineOrgUserProperty(sparePart, maximoSparePart, departMaximoCodeIdMap, userWorkNoIdMap, lineWorkshopCompanyMap);
    }

    private Integer getStatusInt(String status) {
        int statusInt = 1;

        if (StringUtils.isBlank(status)) {
            return statusInt;
        }
        /**
         *使用中
         *封存
         *报废
         *待送修
         *待送检
         *已完成
         *报废审批中
         *已拆卸
         *待使用
         *盘亏审批中
         *已盘亏
         *送修中
         *送检中
         *退库审批中
         *遗失已赔偿
         *已报废
         *停用
         *等待批准
         *已退库
         *未分配
         */
        switch (status) {
            case "使用中":
                statusInt = 1;
                break;
            case "封存":
                statusInt = 2;
                break;
            case "待送修":
                statusInt = 3;
                break;
            case "待送检":
                statusInt = 4;
                break;
            case "已完成":
                statusInt = 5;
                break;
            case "报废审批中":
                statusInt = 6;
                break;
            case "已拆卸":
                statusInt = 7;
                break;
            case "待使用":
                statusInt = 8;
                break;
            case "盘亏审批中":
                statusInt = 9;
                break;
            case "已盘亏":
                statusInt = 10;
                break;
            case "送修中":
                statusInt = 11;
                break;
            case "送检中":
                statusInt = 12;
                break;
            case "退库审批中":
                statusInt = 13;
                break;
            case "遗失已赔偿":
                statusInt = 14;
                break;
            case "已报废":
                statusInt = 15;
                break;
            case "停用":
                statusInt = 16;
                break;
            case "等待批准":
                statusInt = 17;
                break;
            case "已退库":
                statusInt = 18;
                break;
            case "报废":
                statusInt = 19;
                break;
            case "未分配":
                statusInt = 20;
                break;
            default:
                break;
        }

        return statusInt;
    }

    private void setLineOrgUserProperty(BuMaterialTools tool,
                                        JdxRealassetOut maximoTool,
                                        Map<String, String> departMaximoCodeIdMap,
                                        Map<String, String> userWorkNoIdMap,
                                        Map<String, LineWorkshopCompany> lineWorkshopCompanyMap) {
        // 线路，maximo为1、2、3、4，直接使用
        String lineId = null == maximoTool.getLinenum() ? "1" : maximoTool.getLinenum();
        // 车间，根据maximo编码找对应的车间
        String workshopId = departMaximoCodeIdMap.get(maximoTool.getWorkshop());
        // 公司
        String companyId = null;
        LineWorkshopCompany lineWorkshopCompany = lineWorkshopCompanyMap.get(lineId);
        if (null != lineWorkshopCompany) {
            companyId = lineWorkshopCompany.getCompanyId();
            if (StringUtils.isBlank(workshopId)) {
                workshopId = lineWorkshopCompany.getWorkshopId();
            }
        }
        if (StringUtils.isBlank(companyId)) {
            companyId = MaximoThirdConstant.DEFAULT_JDX_COMPANY;
        }
        if (StringUtils.isBlank(workshopId)) {
            workshopId = MaximoThirdConstant.JDX_WORKSHOP_1;
        }
        tool.setLineId(lineId);
        tool.setWorkshopId(workshopId);
        tool.setCompanyId(companyId);
        // 班组，根据maximo编码找对应的班组
        if (StringUtils.isNotBlank(maximoTool.getPersongroup())) {
            tool.setGroupId(departMaximoCodeIdMap.get(maximoTool.getPersongroup()));
        }
        // 人员，根据maximo编码找对应工号的人员
        if (StringUtils.isNotBlank(maximoTool.getCustodian())) {
            tool.setUserId(userWorkNoIdMap.get(maximoTool.getCustodian()));
        }
    }

    private void setLineOrgUserProperty(BuMaterialSparePart sparePart,
                                        JdxRealassetOut maximoSparePart,
                                        Map<String, String> departMaximoCodeIdMap,
                                        Map<String, String> userWorkNoIdMap,
                                        Map<String, LineWorkshopCompany> lineWorkshopCompanyMap) {
        // 线路，maximo为1、2、3、4，直接使用
        String lineId = null == maximoSparePart.getLinenum() ? "1" : maximoSparePart.getLinenum();
        // 车间，根据maximo编码找对应的车间
        String workshopId = departMaximoCodeIdMap.get(maximoSparePart.getWorkshop());
        // 公司
        String companyId = null;
        LineWorkshopCompany lineWorkshopCompany = lineWorkshopCompanyMap.get(lineId);
        if (null != lineWorkshopCompany) {
            companyId = lineWorkshopCompany.getCompanyId();
            if (StringUtils.isBlank(workshopId)) {
                workshopId = lineWorkshopCompany.getWorkshopId();
            }
        }
        if (StringUtils.isBlank(companyId)) {
            companyId = MaximoThirdConstant.DEFAULT_JDX_COMPANY;
        }
        if (StringUtils.isBlank(workshopId)) {
            workshopId = MaximoThirdConstant.JDX_WORKSHOP_1;
        }
        sparePart.setLineId(lineId);
        sparePart.setWorkshopId(workshopId);
        sparePart.setCompanyId(companyId);
        // 班组，根据maximo编码找对应的班组
        if (StringUtils.isNotBlank(maximoSparePart.getPersongroup())) {
            sparePart.setGroupId(departMaximoCodeIdMap.get(maximoSparePart.getPersongroup()));
        }
        // 人员，根据maximo编码找对应工号的人员
        if (StringUtils.isNotBlank(maximoSparePart.getCustodian())) {
            sparePart.setDutyUserId(userWorkNoIdMap.get(maximoSparePart.getCustodian()));
        }
    }

    private void saveMaterialTypeData(List<BuMaterialType> needAddMaterialTypeList,
                                      List<BuMaterialType> needUpdateMaterialTypeList,
                                      String sourceScene) {
        // 新增的
        if (CollectionUtils.isNotEmpty(needAddMaterialTypeList)) {
            List<List<BuMaterialType>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddMaterialTypeList);
            for (List<BuMaterialType> batchSub : batchSubList) {
                buMaterialTypeThirdMapper.insertList(batchSub);
            }
            log.info(sourceScene + "同步maximo工器具列管备件台账信息：新增了" + needAddMaterialTypeList.size() + "条物资类型；");
        }
        // 更新价格的
        if (CollectionUtils.isNotEmpty(needUpdateMaterialTypeList)) {
            List<List<BuMaterialType>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateMaterialTypeList);
            for (List<BuMaterialType> batchSub : batchSubList) {
                buMaterialTypeThirdMapper.updateListPrice(batchSub);
            }
            log.info(sourceScene + "同步maximo工器具列管备件台账信息：更新了" + needUpdateMaterialTypeList.size() + "条物资类型的价格；");
        }
    }

    private void saveToolData(List<JdxRealassetOut> maximoToolList,
                              Map<String, BuMaterialTools> needAddIdToolMap,
                              Map<String, BuMaterialTools> needUpdateIdToolMap,
                              String sourceScene) {
        log.info(sourceScene + "同步maximo工器具列管备件台账信息：传入了" + maximoToolList.size() + "条工器具记录（含更新的数据）；");
        if (!needAddIdToolMap.isEmpty()) {
            List<BuMaterialTools> needAddToolList = new ArrayList<>(needAddIdToolMap.values());
            List<List<BuMaterialTools>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddToolList);
            for (List<BuMaterialTools> batchSub : batchSubList) {
                buMaterialToolsThirdMapper.insertList(batchSub);
            }
            log.info(sourceScene + "同步maximo工器具列管备件台账信息：新增了" + needAddToolList.size() + "条工器具记录；");
        }
        if (!needUpdateIdToolMap.isEmpty()) {
            List<BuMaterialTools> needUpdateToolList = new ArrayList<>(needUpdateIdToolMap.values());
            List<List<BuMaterialTools>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateToolList);
            for (List<BuMaterialTools> batchSub : batchSubList) {
                buMaterialToolsThirdMapper.updateList(batchSub);
            }
            log.info(sourceScene + "同步maximo工器具列管备件台账信息：更新了" + needUpdateToolList.size() + "条工器具记录；");
        }
    }

    private void saveSparePartData(List<JdxRealassetOut> maximoSparePartList,
                                   Map<String, BuMaterialSparePart> needAddIdSparePartMap,
                                   Map<String, BuMaterialSparePart> needUpdateIdSparePartMap,
                                   String sourceScene) {
        log.info(sourceScene + "同步maximo工器具列管备件台账信息：传入了" + maximoSparePartList.size() + "条列管备件记录（含更新的数据）；");
        if (!needAddIdSparePartMap.isEmpty()) {
            List<BuMaterialSparePart> needAddSparePartList = new ArrayList<>(needAddIdSparePartMap.values());
            List<List<BuMaterialSparePart>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddSparePartList);
            for (List<BuMaterialSparePart> batchSub : batchSubList) {
                buMaterialSparePartThirdMapper.insertList(batchSub);
            }
            log.info(sourceScene + "同步maximo工器具列管备件台账信息：新增了" + needAddSparePartList.size() + "条列管备件记录；");
        }
        if (!needUpdateIdSparePartMap.isEmpty()) {
            List<BuMaterialSparePart> needUpdateSparePartList = new ArrayList<>(needUpdateIdSparePartMap.values());
            List<List<BuMaterialSparePart>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateSparePartList);
            for (List<BuMaterialSparePart> batchSub : batchSubList) {
                buMaterialSparePartThirdMapper.updateList(batchSub);
            }
            log.info(sourceScene + "同步maximo工器具列管备件台账信息：更新了" + needUpdateSparePartList.size() + "条列管备件记录；");
        }
    }

    private void deleteInvalidTool(Set<String> validToolIdSet, String sourceScene) {
        // 查询所有旧的id
        List<String> oldIdList = buMaterialToolsThirdMapper.selectIdList();
        // 过滤掉有效的
        if (CollectionUtils.isNotEmpty(validToolIdSet)) {
            oldIdList.removeAll(validToolIdSet);
        }
        // 删除无效的
        if (CollectionUtils.isNotEmpty(oldIdList)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(oldIdList);
            for (List<String> batchSub : batchSubList) {
                buMaterialToolsThirdMapper.deleteBatchIds(batchSub);
            }

            // 日志中记录本次处理信息
            log.info(String.format(sourceScene + "从maximo同步数据--工器具：处理工器具信息%s条（删除%s）",
                    oldIdList.size(),
                    oldIdList.size()));
        }
    }

    private void deleteInvalidSparePart(Set<String> validSparePartIdSet, String sourceScene) {
        // 查询所有旧的id
        List<String> oldIdList = buMaterialSparePartThirdMapper.selectIdList();
        // 过滤掉有效的
        if (CollectionUtils.isNotEmpty(validSparePartIdSet)) {
            oldIdList.removeAll(validSparePartIdSet);
        }
        // 删除无效的
        if (CollectionUtils.isNotEmpty(oldIdList)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(oldIdList);
            for (List<String> batchSub : batchSubList) {
                buMaterialSparePartThirdMapper.deleteBatchIds(batchSub);
            }

            // 日志中记录本次处理信息
            log.info(String.format(sourceScene + "从maximo同步数据--列管备件：处理列管备件信息%s条（删除%s）",
                    oldIdList.size(),
                    oldIdList.size()));
        }
    }

}
