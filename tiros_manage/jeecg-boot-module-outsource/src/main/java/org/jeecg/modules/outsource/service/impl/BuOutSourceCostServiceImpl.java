package org.jeecg.modules.outsource.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.jeecg.modules.outsource.bean.*;
import org.jeecg.modules.outsource.mapper.BuContractInfoMapper;
import org.jeecg.modules.outsource.service.BuOutSourceCostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yyg
 */
@Service
public class BuOutSourceCostServiceImpl implements BuOutSourceCostService {

    @Resource
    private BuContractInfoMapper buContractInfoMapper;


    @Override
    public List<OutsourceCost> listCost() throws Exception {
        List<BuContractInfo> contractInfoList = buContractInfoMapper.selectListByDate(new Date());
        List<OutsourceCost> outsourceCostList = new ArrayList<>();
        Map<String, Integer> lineGroupMap = new HashMap<>(16);

        if (CollectionUtil.isNotEmpty(contractInfoList)) {
            List<String> lineIdList = contractInfoList.stream().map(BuContractInfo::getLineId).distinct().collect(Collectors.toList());
            lineIdList.forEach(lineId -> lineGroupMap.put(lineId, buContractInfoMapper.selectLineGroup(lineId)));
            Map<String, List<BuContractInfo>> contractInfoMap = contractInfoList.stream().collect(Collectors.groupingBy(item -> (item.getLineId() + item.getRepairProgramId())));
            contractInfoMap.forEach((key, contractInfos) -> {
                List<OutsourcrCostItem> costItemList = new ArrayList<>();
                BuContractInfo firstContractInfo = contractInfos.get(0);
                Integer groupCount = lineGroupMap.get(firstContractInfo.getLineId());
                OutsourceCost outsourceCost = new OutsourceCost()
                        .setLineId(firstContractInfo.getLineId())
                        .setLineName(firstContractInfo.getLineName())
                        .setRepairProgramId(firstContractInfo.getRepairProgramId())
                        .setRepairProgramName(firstContractInfo.getRepairProgramName());

                BigDecimal summary = contractInfos.stream().map(BuContractInfo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                costItemList.add(new OutsourcrCostItem().setType(1).setPrice(summary));

                int trainAmount = contractInfos.stream().mapToInt(BuContractInfo::getTrainAmount).sum();
                costItemList.add(new OutsourcrCostItem().setType(2).setPrice(summary.divide(BigDecimal.valueOf(trainAmount), 8, RoundingMode.HALF_UP)));

                costItemList.add(new OutsourcrCostItem().setType(3).setPrice(summary.divide(BigDecimal.valueOf((long) trainAmount * groupCount), 8, RoundingMode.HALF_UP)));

                outsourceCost.setCostItemList(costItemList);
                outsourceCostList.add(outsourceCost);
            });
        }
        return outsourceCostList;
    }

    @Override
    public List<OutsourceCostDetail> outsourceCostDetail(String lineId, String repairProgramId) throws Exception {
        List<BuContractInfo> contractInfoList = buContractInfoMapper.selectListByLineIdAndRepairProId(lineId, repairProgramId);
        Integer groupCount = buContractInfoMapper.selectLineGroup(lineId);
        List<OutsourceCostDetail> outsourceCostDetailList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(contractInfoList)) {
            contractInfoList.forEach(contractInfo -> {
                OutsourceCostDetail costDetail = new OutsourceCostDetail()
                        .setId(contractInfo.getId())
                        .setContractNo(contractInfo.getContractNo())
                        .setContractName(contractInfo.getContractName())
                        .setItemName(contractInfo.getAssetTypeAlias())
                        .setContractAmount(contractInfo.getAmount())
                        .setSinglePrice(contractInfo.getAmount().divide(BigDecimal.valueOf(contractInfo.getTrainAmount()), 8, RoundingMode.HALF_UP))
                        .setSectionPrice(contractInfo.getAmount().divide(BigDecimal.valueOf((long) contractInfo.getTrainAmount() * groupCount), 8, RoundingMode.HALF_UP))
                        .setPartPrice(contractInfo.getAmount().divide(BigDecimal.valueOf((long) contractInfo.getTrainAmount() *contractInfo.getAssetAmount()), 8, RoundingMode.HALF_UP));
                outsourceCostDetailList.add(costDetail);
            });
        }
        return outsourceCostDetailList;
    }

   /* @Override
    public List<OutsourceCostPart> outsourceCostPart(String lineId,Integer type) throws Exception {
        List<BuContractInfo> contractInfoList = buContractInfoMapper.selectListByLineId(lineId);
        Integer groupCount = buContractInfoMapper.selectLineGroup(lineId);
        List<OutsourceCostPart> costPartList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(contractInfoList)) {
            Map<String, List<BuContractInfo>> contractInfoMap = contractInfoList.stream().collect(Collectors.groupingBy(item ->item.getRepairProgramId() + item.getSignDate().getYear()));
            contractInfoMap.forEach((key, contractInfos) -> {
                List<OutSourceCostPartPrice> costPartPriceList = new ArrayList<>();
                BuContractInfo firstContractInfo = contractInfos.get(0);
                OutsourceCostPart costPart = new OutsourceCostPart()
                        .setYear(firstContractInfo.getSignDate().getYear()+1900)
                        .setLineId(firstContractInfo.getLineId())
                        .setLineName(firstContractInfo.getLineName())
                        .setRepairProgramId(firstContractInfo.getRepairProgramName())
                        .setRepairProgramName(firstContractInfo.getRepairProgramName());


                Map<String, List<BuContractInfo>> sameAssetMap = contractInfos.stream().collect(Collectors.groupingBy(BuContractInfo::getAssetTypeName));

                sameAssetMap.forEach((assetName, contractInfo) -> {
                            OutSourceCostPartPrice costPartPrice = new OutSourceCostPartPrice()
                                    .setPartName(assetName);
                            BigDecimal summary = contractInfo.stream().map(BuContractInfo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                            int trainAmount = contractInfo.stream().mapToInt(BuContractInfo::getTrainAmount).sum();
                            if (type == 1) {
                                costPartPrice.setPrice(summary);
                            } else if (type == 2) {
                                costPartPrice.setPrice(summary.divide(BigDecimal.valueOf(trainAmount), 8, RoundingMode.HALF_UP));
                            } else if (type == 3) {
                                costPartPrice.setPrice(summary.divide(BigDecimal.valueOf((long) trainAmount * groupCount), 8, RoundingMode.HALF_UP));
                            } else {
                                int assetAmount = contractInfo.stream().mapToInt(BuContractInfo::getAssetAmount).sum();
                                costPartPrice.setPrice(summary.divide(BigDecimal.valueOf((long) trainAmount * groupCount * assetAmount), 8, RoundingMode.HALF_UP));
                            }
                            costPartPriceList.add(costPartPrice);
                        }
                );
                costPart.setPartPriceList(costPartPriceList);
                costPartList.add(costPart);
            });
        }
        return costPartList;
    }*/

    @Override
    public List<OutsourceAsset> outsourceAssetList() throws Exception {
        return buContractInfoMapper.selectAssetList();
    }

    @Override
    public List<OutsourceCostPart> outsourceCostPartByAssetId(String assetId, Integer type) throws Exception {
        List<BuContractInfo> contractInfoList = buContractInfoMapper.selectListByAssetId(assetId);
        List<OutsourceCostPart> costPartList = new ArrayList<>();
        Map<String, Integer> lineGroupMap = new HashMap<>(16);

        if (CollectionUtil.isNotEmpty(contractInfoList)) {
            List<String> lineIdList = contractInfoList.stream().map(BuContractInfo::getLineId).distinct().collect(Collectors.toList());
            lineIdList.forEach(lineId -> lineGroupMap.put(lineId, buContractInfoMapper.selectLineGroup(lineId)));
            Map<String, List<BuContractInfo>> contractInfoMap = contractInfoList.stream().collect(Collectors.groupingBy(item -> item.getLineId() + item.getRepairProgramId() + item.getSignDate().getYear()));
            contractInfoMap.forEach((key, contractInfos) -> {
                BuContractInfo firstContractInfo = contractInfos.get(0);
                Integer groupCount = lineGroupMap.get(firstContractInfo.getLineId());
                OutsourceCostPart costPart = new OutsourceCostPart()
                        .setYear(firstContractInfo.getSignDate().getYear() + 1900)
                        .setLineId(firstContractInfo.getLineId())
                        .setLineName(firstContractInfo.getLineName())
                        .setRepairProgramId(firstContractInfo.getRepairProgramName())
                        .setRepairProgramName(firstContractInfo.getRepairProgramName())
                        .setPartName(firstContractInfo.getAssetTypeName());

                BigDecimal summary = contractInfos.stream().map(BuContractInfo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                int trainAmount = contractInfos.stream().mapToInt(BuContractInfo::getTrainAmount).sum();
                if (type == 1) { //总价
                    costPart.setPrice(summary);
                } else if (type == 2) { //单列价格
                    costPart.setPrice(summary.divide(BigDecimal.valueOf(trainAmount), 8, RoundingMode.HALF_UP));
                } else if (type == 3) { //单节价格
                    costPart.setPrice(summary.divide(BigDecimal.valueOf((long) trainAmount * groupCount), 8, RoundingMode.HALF_UP));
                } else { //单部件价格
                    int assetAmount = contractInfos.stream().mapToInt(BuContractInfo::getAssetAmount).sum();
                    costPart.setPrice(summary.divide(BigDecimal.valueOf((long) trainAmount *  assetAmount), 8, RoundingMode.HALF_UP));
                }
                costPartList.add(costPart);
            });
        }
        return costPartList.stream().sorted(Comparator.comparingInt(OutsourceCostPart::getYear)).collect(Collectors.toList());
    }
}
