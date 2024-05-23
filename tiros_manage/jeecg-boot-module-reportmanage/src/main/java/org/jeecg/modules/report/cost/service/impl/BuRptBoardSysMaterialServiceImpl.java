package org.jeecg.modules.report.cost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.modules.report.cost.bean.BuRptBoardSysMaterial;
import org.jeecg.modules.report.cost.bean.vo.BuCostSystemTotalCountVO;
import org.jeecg.modules.report.cost.bean.vo.BuReportCostSingleTrainQueryVO;
import org.jeecg.modules.report.cost.mapper.BuRptBoardSysMaterialMapper;
import org.jeecg.modules.report.cost.service.BuRptBoardSysMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 物料成本统计 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-07
 */
@Slf4j
@Service
public class BuRptBoardSysMaterialServiceImpl extends ServiceImpl<BuRptBoardSysMaterialMapper, BuRptBoardSysMaterial> implements BuRptBoardSysMaterialService {

    @Resource
    private BuRptBoardSysMaterialMapper buRptBoardSysMaterialMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuRptBoardSysMaterialService#getTrainCarsNumber(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Integer getTrainCarsNumber(String lineId, String trainNo) throws Exception {
        if (StringUtils.isBlank(trainNo)) {
            throw new JeecgBootException("请输入车号");
        }

        Integer carsNumber = buRptBoardSysMaterialMapper.selectTrainCarsNumberByLineIdAndTrainNo(lineId, trainNo);
        if (null == carsNumber) {
            throw new JeecgBootException("车号" + trainNo + "编组数量为空，请核实数据");
        }
        if (carsNumber <= 0) {
            throw new JeecgBootException("车号" + trainNo + "编组数量小于等于0，请核实数据");
        }

        return carsNumber;
    }

    /**
     * @see BuRptBoardSysMaterialService#getCostSystemStatistic(BuReportCostSingleTrainQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuCostSystemTotalCountVO> getCostSystemStatistic(BuReportCostSingleTrainQueryVO queryVO) throws Exception {
        queryVO.toYearMonthList();
        List<BuRptBoardSysMaterial> sysMaterialList = buRptBoardSysMaterialMapper.selectListByCondition(queryVO);

        return getBuCostSystemTotalCountVOList(sysMaterialList);
    }

//    /**
//     * @see BuRptBoardSysMaterialService#getCostSystemTotalStatistic(BuReportCostSingleTrainQueryVO)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public List<BuCostSystemTotalCountVO> getCostSystemTotalStatistic(BuReportCostSingleTrainQueryVO queryVO) throws Exception {
//        queryVO.setYear(null);
//        queryVO.setMonthList(null);
//        List<BuRptBoardSysMaterial> sysMaterialList = buRptBoardSysMaterialMapper.selectListByCondition(queryVO);
//
//        return getBuCostSystemTotalCountVOList(sysMaterialList);
//    }


//    private BuCostSystemStatisticVO getBuCostSystemStatisticVO(List<BuRptBoardSysMaterial> sysMaterialList) {
//        List<String> systemFieldList = new ArrayList<>();
//        Map<String, Object> systemData1 = new HashMap<>();
//        systemData1.put("type", "备品备件（元）-合计");
//        Map<String, Object> systemData2 = new HashMap<>();
//        systemData2.put("type", "耗材（元）");
//
//        if (CollectionUtils.isNotEmpty(sysMaterialList)) {
//            Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(null);
//            for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
//                String sysId = sysEntry.getKey();
//                BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
//                String sysShortName = sysAssetTypeBO.getShortName();
//
//                List<BuRptBoardSysMaterial> materialList = sysMaterialList.stream()
//                        .filter(material -> sysId.equals(material.getSysId()))
//                        .collect(Collectors.toList());
//
//                BigDecimal totalMustCost = materialList.stream()
//                        .map(BuRptBoardSysMaterial::getMustCost)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                BigDecimal totalRandomCost = materialList.stream()
//                        .map(BuRptBoardSysMaterial::getRandomCost)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                BigDecimal totalConsumeCost = materialList.stream()
//                        .map(BuRptBoardSysMaterial::getConsumeCost)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//                systemFieldList.add(sysShortName);
//                systemData1.put(sysShortName, totalMustCost.add(totalRandomCost));
//                systemData2.put(sysShortName, totalConsumeCost);
//            }
//        }
//
//        return new BuCostSystemStatisticVO()
//                .setSystemFieldList(systemFieldList)
//                .setSystemDataList(Arrays.asList(systemData1, systemData2));
//    }

    private List<BuCostSystemTotalCountVO> getBuCostSystemTotalCountVOList(List<BuRptBoardSysMaterial> sysMaterialList) {
        List<BuCostSystemTotalCountVO> costSystemTotalCountVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sysMaterialList)) {
            BigDecimal totalMustCost = BigDecimal.ZERO;
            BigDecimal totalRandomCost = BigDecimal.ZERO;
            BigDecimal totalConsumeCost = BigDecimal.ZERO;

            Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(null);
            for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
                String sysId = sysEntry.getKey();
                BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
                String sysShortName = sysAssetTypeBO.getShortName();

                List<BuRptBoardSysMaterial> materialList = sysMaterialList.stream()
                        .filter(material -> sysId.equals(material.getSysId()))
                        .collect(Collectors.toList());

                BigDecimal mustCost = materialList.stream()
                        .map(BuRptBoardSysMaterial::getMustCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal randomCost = materialList.stream()
                        .map(BuRptBoardSysMaterial::getRandomCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal consumeCost = materialList.stream()
                        .map(BuRptBoardSysMaterial::getConsumeCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                totalMustCost = totalMustCost.add(mustCost);
                totalRandomCost = totalRandomCost.add(randomCost);
                totalConsumeCost = totalConsumeCost.add(consumeCost);

                BuCostSystemTotalCountVO costSystemTotalCountVO = new BuCostSystemTotalCountVO()
                        .setSysName(sysShortName)
                        .setMustCost(mustCost)
                        .setRandomCost(randomCost)
                        .setMustRandomCost(mustCost.add(randomCost))
                        .setConsumeCost(consumeCost);
                costSystemTotalCountVOList.add(costSystemTotalCountVO);
            }
            costSystemTotalCountVOList.sort(Comparator.comparing(BuCostSystemTotalCountVO::getSysName, Comparator.nullsLast(Comparator.naturalOrder())));
//            BuCostSystemTotalCountVO total = new BuCostSystemTotalCountVO()
//                    .setSysName("合计")
//                    .setMustCost(totalMustCost)
//                    .setRandomCost(totalRandomCost)
//                    .setMustRandomCost(totalMustCost.add(totalRandomCost))
//                    .setConsumeCost(totalConsumeCost);
//            costSystemTotalCountVOList.add(total);
        }

        return costSystemTotalCountVOList;
    }

}
