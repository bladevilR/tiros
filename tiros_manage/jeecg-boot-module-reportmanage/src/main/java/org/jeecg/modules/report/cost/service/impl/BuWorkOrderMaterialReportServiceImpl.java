package org.jeecg.modules.report.cost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.report.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.report.cost.bean.vo.*;
import org.jeecg.modules.report.cost.mapper.BuWorkOrderMaterialReportMapper;
import org.jeecg.modules.report.cost.service.BuWorkOrderMaterialReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单物料 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
@Slf4j
@Service
public class BuWorkOrderMaterialReportServiceImpl extends ServiceImpl<BuWorkOrderMaterialReportMapper, BuWorkOrderMaterial> implements BuWorkOrderMaterialReportService {

    @Resource
    private BuWorkOrderMaterialReportMapper buWorkOrderMaterialReportMapper;
//    @Resource
//    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuWorkOrderMaterialReportService#getCostDetailStatistic(BuReportCostDetailQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuCostDetailTotalVO getCostDetailStatistic(BuReportCostDetailQueryVO queryVO) throws Exception {
        queryVO.toStartEndDate();
        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialReportMapper.selectListByCondition(queryVO);
        // 设置消耗数量价格
        setConsumeAmountAndPrice(orderMaterialList);

        return transToBuCostDetailTotalVO(orderMaterialList);
    }


    private void setConsumeAmountAndPrice(List<BuWorkOrderMaterial> orderMaterialList) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            if (null == orderMaterial.getConsumeAmount()) {
                orderMaterial.setConsumeAmount(BigDecimal.ZERO);
            }
            if (null == orderMaterial.getConsumeTotalPrice()) {
                orderMaterial.setConsumeTotalPrice(BigDecimal.ZERO);
            }
            if (BigDecimal.ZERO.compareTo(orderMaterial.getConsumeAmount()) < 0) {
                orderMaterial.setUnitPrice(orderMaterial.getConsumeTotalPrice().divide(orderMaterial.getConsumeAmount(), 8, BigDecimal.ROUND_HALF_UP));
            } else {
                orderMaterial.setUnitPrice(BigDecimal.ZERO);
            }
        }

        orderMaterialList.removeIf(orderMaterial ->
                BigDecimal.ZERO.compareTo(orderMaterial.getConsumeAmount()) >= 0
                        && BigDecimal.ZERO.compareTo(orderMaterial.getConsumeTotalPrice()) >= 0
                        && BigDecimal.ZERO.compareTo(orderMaterial.getUnitPrice()) >= 0);
    }

    private BuCostDetailTotalVO transToBuCostDetailTotalVO(List<BuWorkOrderMaterial> orderMaterialList) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return new BuCostDetailTotalVO().setTotalCost(BigDecimal.ZERO).setSystemList(new ArrayList<>());
        }

//        // 获取设备类型缓存
//        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = assetTypeCacheService.map(null);

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            // 规格拼接
            String description;
            if (StringUtils.isNotBlank(orderMaterial.getMaterialTypeSpec())) {
                description = orderMaterial.getMaterialTypeName() + "-[规格：" + orderMaterial.getMaterialTypeSpec() + "]";
            } else {
                description = orderMaterial.getMaterialTypeName();
            }
            orderMaterial.setMaterialTypeSpec(description);

            if (StringUtils.isBlank(orderMaterial.getSystemName())) {
                orderMaterial.setSystemName("无");
            }
            if (StringUtils.isBlank(orderMaterial.getWorkstationNo())) {
                orderMaterial.setWorkstationNo("无");
            }
            if (StringUtils.isBlank(orderMaterial.getWorkstationName())) {
                orderMaterial.setWorkstationName("无");
            }

//            String assetTypeId = orderMaterial.getAssetTypeId();
//            BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOMap.get(assetTypeId);
//            String sysName = "其他(无对应系统)";
//            if (null != assetTypeBO) {
//                sysName = assetTypeBO.getSysName();
//            }
//            BuCostDetailSystemVO systemVO = getSystemVO(systemVOList, sysName);
//
//            List<BuCostDetailWorkstationVO> workstationVOList = systemVO.getWorkstationList();
//            List<BuMtrWorkstation> workstationList = orderMaterial.getWorkstationList();
//            if (CollectionUtils.isEmpty(workstationList)) {
//                workstationList = new ArrayList<>();
//                workstationList.add(new BuMtrWorkstation().setId("").setStationNo("").setName("其他(无对应工位)"));
//            }
//            for (BuMtrWorkstation workstation : workstationList) {
//                BuCostDetailWorkstationVO workstationVO = getWorkstationVO(workstation, workstationVOList);
//                addOrderMaterialToWorkstationVO(orderMaterial, workstationVO);
//            }
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        List<BuCostDetailSystemVO> systemVOList = new ArrayList<>();
        // 按系统分组
        Map<String, List<BuWorkOrderMaterial>> sysNameListMap = orderMaterialList.stream()
                .collect(Collectors.groupingBy(BuWorkOrderMaterial::getSystemName));
        for (Map.Entry<String, List<BuWorkOrderMaterial>> sysNameListEntry : sysNameListMap.entrySet()) {
            String sysName = sysNameListEntry.getKey();
            List<BuWorkOrderMaterial> sysNameList = sysNameListEntry.getValue();
            if (CollectionUtils.isEmpty(sysNameList)) {
                continue;
            }
            String systemId = sysNameList.get(0).getSystemId();
            String systemCode = sysNameList.get(0).getSystemCode();

            BigDecimal sysCost = BigDecimal.ZERO;

            List<BuCostDetailWorkstationVO> workstationVOList = new ArrayList<>();
            // 按工位分组
            Map<String, List<BuWorkOrderMaterial>> workstationNameListMap = sysNameList.stream()
                    .collect(Collectors.groupingBy(BuWorkOrderMaterial::getWorkstationName));
            for (Map.Entry<String, List<BuWorkOrderMaterial>> workstationNameListEntry : workstationNameListMap.entrySet()) {
                List<BuWorkOrderMaterial> workstationNameList = workstationNameListEntry.getValue();
                if (CollectionUtils.isEmpty(workstationNameList)) {
                    continue;
                }

                String workstationId = workstationNameList.get(0).getWorkstationId();
                String workstationNo = workstationNameList.get(0).getWorkstationNo();
                String workstationName = workstationNameList.get(0).getWorkstationName();

                List<BuCostDetailMaterialVO> materialVOList = new ArrayList<>();
                // 按物资分组
                Map<String, List<BuWorkOrderMaterial>> materialTypeIdListMap = workstationNameList.stream()
                        .collect(Collectors.groupingBy(BuWorkOrderMaterial::getMaterialTypeId));
                for (Map.Entry<String, List<BuWorkOrderMaterial>> materialTypeIdListEntry : materialTypeIdListMap.entrySet()) {
                    String materialTypeId = materialTypeIdListEntry.getKey();
                    List<BuWorkOrderMaterial> materialTypeIdList = materialTypeIdListEntry.getValue();
                    // 按使用类型分组
                    Map<Integer, List<BuWorkOrderMaterial>> useCategoryListMap = materialTypeIdList.stream()
                            .collect(Collectors.groupingBy(BuWorkOrderMaterial::getUseCategory));
                    for (Map.Entry<Integer, List<BuWorkOrderMaterial>> useCategoryListEntry : useCategoryListMap.entrySet()) {
                        Integer useCategory = useCategoryListEntry.getKey();
                        List<BuWorkOrderMaterial> useCategoryList = useCategoryListEntry.getValue();
                        // 按单价分组
                        Map<BigDecimal, List<BuWorkOrderMaterial>> unitPriceListMap = useCategoryList.stream()
                                .collect(Collectors.groupingBy(BuWorkOrderMaterial::getUnitPrice));
                        for (Map.Entry<BigDecimal, List<BuWorkOrderMaterial>> unitPriceListEntry : unitPriceListMap.entrySet()) {
                            BigDecimal unitPrice = unitPriceListEntry.getKey();
                            List<BuWorkOrderMaterial> unitPriceList = unitPriceListEntry.getValue();

                            // 数量
                            BigDecimal materialAmount = unitPriceList.stream()
                                    .map(BuWorkOrderMaterial::getConsumeAmount)
                                    .filter(Objects::nonNull)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            if (BigDecimal.ZERO.compareTo(materialAmount) == 0) {
                                // 数量为0不处理
                                continue;
                            }

                            String materialTypeCode = unitPriceList.get(0).getMaterialTypeCode();
                            String materialTypeName = unitPriceList.get(0).getMaterialTypeName();
                            String materialTypeSpec = unitPriceList.get(0).getMaterialTypeSpec();
                            String materialTypeUnit = unitPriceList.get(0).getMaterialTypeUnit();
                            String category2 = unitPriceList.get(0).getCategory2();

                            // 金额
                            BigDecimal materialCost = unitPriceList.stream()
                                    .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                                    .filter(Objects::nonNull)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            // 备注
                            String remarks = unitPriceList.stream()
                                    .map(BuWorkOrderMaterial::getRemark)
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.joining(";"));

                            // 工单物料ids
                            String orderMaterialIds = unitPriceList.stream()
                                    .map(BuWorkOrderMaterial::getId)
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.joining(","));
                            Set<String> orderMaterialIdSet = unitPriceList.stream()
                                    .map(BuWorkOrderMaterial::getId)
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.toSet());

                            BuCostDetailMaterialVO materialVO = new BuCostDetailMaterialVO()
                                    .setMaterialTypeId(materialTypeId)
                                    .setMaterialTypeCode(materialTypeCode)
                                    .setMaterialTypeName(materialTypeName)
                                    .setMaterialTypeSpec(materialTypeSpec)
                                    .setMaterialTypeUnit(materialTypeUnit)
                                    .setUseCategory(useCategory)
                                    .setCategory2(category2)
                                    .setConsumeAmount(materialAmount)
                                    .setUnitPrice(unitPrice)
                                    .setConsumeTotalPrice(materialCost)
                                    .setRemark(remarks)
                                    .setOrderMaterialIds(orderMaterialIds)
                                    .setOrderMaterialIdSet(orderMaterialIdSet);
                            materialVOList.add(materialVO);
                        }
                    }
                }

                // 金额计算
                BigDecimal workstationCost = workstationNameList.stream()
                        .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                sysCost = sysCost.add(workstationCost);
                totalCost = totalCost.add(workstationCost);

                // 排序
                materialVOList.sort(Comparator.comparing(BuCostDetailMaterialVO::getMaterialTypeCode, Comparator.nullsLast(Comparator.naturalOrder())));
                BuCostDetailWorkstationVO workstationVO = new BuCostDetailWorkstationVO()
                        .setWorkstationId(workstationId)
                        .setWorkstationNo(workstationNo)
                        .setWorkstationName(workstationName)
                        .setMaterialList(materialVOList);
                workstationVOList.add(workstationVO);
            }

            // 排序
            workstationVOList.sort(Comparator.comparing(BuCostDetailWorkstationVO::getWorkstationNo, Comparator.nullsLast(Comparator.naturalOrder())));
            BuCostDetailSystemVO systemVO = new BuCostDetailSystemVO()
                    .setSysId(systemId)
                    .setSysCode(systemCode)
                    .setSysName(sysName)
                    .setSysTotalPrice(sysCost)
                    .setWorkstationList(workstationVOList);
            systemVOList.add(systemVO);
        }

        // 排序
        systemVOList.sort(Comparator.comparing(BuCostDetailSystemVO::getSysCode, Comparator.nullsLast(Comparator.naturalOrder())));
        return new BuCostDetailTotalVO()
                .setTotalCost(totalCost)
                .setSystemList(systemVOList);
    }

//    private BuCostDetailSystemVO getSystemVO(List<BuCostDetailSystemVO> systemVOList, String sysName) {
//        List<BuCostDetailSystemVO> filterSystemVOList = systemVOList.stream()
//                .filter(sysVO -> sysName.equals(sysVO.getSysName()))
//                .collect(Collectors.toList());
//
//        if (CollectionUtils.isEmpty(filterSystemVOList)) {
//            BuCostDetailSystemVO systemVO = new BuCostDetailSystemVO()
//                    .setSysName(sysName)
//                    .setWorkstationList(new ArrayList<>());
//            systemVOList.add(systemVO);
//            return systemVO;
//        } else {
//            return filterSystemVOList.get(0);
//        }
//    }
//
//    private BuCostDetailWorkstationVO getWorkstationVO(BuMtrWorkstation workstation, List<BuCostDetailWorkstationVO> workstationVOList) {
//        List<BuCostDetailWorkstationVO> filterWorkstationVOList = workstationVOList.stream()
//                .filter(workstationVO -> workstation.getId().equals(workstationVO.getWorkstationId()))
//                .collect(Collectors.toList());
//
//        if (CollectionUtils.isEmpty(filterWorkstationVOList)) {
//            BuCostDetailWorkstationVO workstationVO = new BuCostDetailWorkstationVO()
//                    .setWorkstationId(workstation.getId())
//                    .setWorkstationNo(workstation.getStationNo())
//                    .setWorkstationName(workstation.getName());
//            workstationVOList.add(workstationVO);
//            return workstationVO;
//        } else {
//            return filterWorkstationVOList.get(0);
//        }
//    }
//
//    private void addOrderMaterialToWorkstationVO(BuWorkOrderMaterial orderMaterial, BuCostDetailWorkstationVO workstationVO) {
//        List<BuWorkOrderMaterial> orderMaterialList = workstationVO.getMaterialList();
//        if (null == orderMaterialList) {
//            orderMaterialList = new ArrayList<>();
//            orderMaterialList.add(orderMaterial);
//        } else {
//            List<BuWorkOrderMaterial> filterOrderMaterialList = orderMaterialList.stream()
//                    .filter(detail -> orderMaterial.getId().equals(detail.getId()))
//                    .collect(Collectors.toList());
//            if (CollectionUtils.isEmpty(filterOrderMaterialList)) {
//                orderMaterialList.add(orderMaterial);
//            }
//        }
//        workstationVO.setMaterialList(orderMaterialList);
//    }
//
//    private void amountPrice(BuCostDetailTotalVO costDetailTotalVO) {
//        List<BuCostDetailSystemVO> systemList = costDetailTotalVO.getSystemList();
//        if (CollectionUtils.isEmpty(systemList)) {
//            costDetailTotalVO.setTotalCost(BigDecimal.ZERO);
//            return;
//        }
//
//        BigDecimal totalCost = BigDecimal.ZERO;
//        for (BuCostDetailSystemVO systemVO : systemList) {
//            List<BuCostDetailWorkstationVO> workstationVOList = systemVO.getWorkstationList();
//            if (CollectionUtils.isEmpty(workstationVOList)) {
//                systemVO.setSysTotalPrice(BigDecimal.ZERO);
//                continue;
//            }
//
//            BigDecimal sysTotalPrice = BigDecimal.ZERO;
//            for (BuCostDetailWorkstationVO workstationVO : workstationVOList) {
//                List<BuWorkOrderMaterial> orderMaterialList = workstationVO.getMaterialList();
//                if (CollectionUtils.isNotEmpty(orderMaterialList)) {
//                    for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
//                        sysTotalPrice = sysTotalPrice.add(orderMaterial.getConsumeTotalPrice());
//                    }
//                }
//            }
//            systemVO.setSysTotalPrice(sysTotalPrice);
//            totalCost = totalCost.add(sysTotalPrice);
//        }
//        costDetailTotalVO.setTotalCost(totalCost);
//    }

}
