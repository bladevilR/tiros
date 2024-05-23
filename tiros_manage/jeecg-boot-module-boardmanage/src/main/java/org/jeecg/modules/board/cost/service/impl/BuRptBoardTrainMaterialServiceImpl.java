package org.jeecg.modules.board.cost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.AreaChartVO;
import org.jeecg.modules.board.cost.bean.BuContractPay;
import org.jeecg.modules.board.cost.bean.BuRepairProgram;
import org.jeecg.modules.board.cost.bean.BuRptBoardTrainMaterial;
import org.jeecg.modules.board.cost.bean.vo.BuBoardCostQueryVO;
import org.jeecg.modules.board.cost.bean.vo.BuRptBoardTrainMaterialDepotVO;
import org.jeecg.modules.board.cost.bean.vo.BuRptBoardTrainMaterialProgramVO;
import org.jeecg.modules.board.cost.mapper.BuCostCustomMapper;
import org.jeecg.modules.board.cost.mapper.BuRptBoardTrainMaterialMapper;
import org.jeecg.modules.board.cost.service.BuRptBoardTrainMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物料成本统计 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
@Slf4j
@Service
public class BuRptBoardTrainMaterialServiceImpl extends ServiceImpl<BuRptBoardTrainMaterialMapper, BuRptBoardTrainMaterial> implements BuRptBoardTrainMaterialService {

    @Resource
    private BuRptBoardTrainMaterialMapper buRptBoardTrainMaterialMapper;
    @Resource
    private BuCostCustomMapper buCostCustomMapper;


    /**
     * @see BuRptBoardTrainMaterialService#getYearTotalCostAreaChart(BuBoardCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<AreaChartVO> getYearTotalCostAreaChart(BuBoardCostQueryVO queryVO) throws Exception {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<AreaChartVO> areaChartVOList = initYearAreaChartVOList(year);

        List<BuRptBoardTrainMaterial> trainMaterialList = buRptBoardTrainMaterialMapper.listByYearAndCondition(year, queryVO);
        setTotalCost(trainMaterialList);
        if (CollectionUtils.isNotEmpty(trainMaterialList)) {
            Map<String, List<BuRptBoardTrainMaterial>> yearMonthTrainMaterialListMap = trainMaterialList.stream()
                    .collect(Collectors.groupingBy(cost -> String.format("%s-%s", cost.getYear(), cost.getMonth())));
            for (AreaChartVO areaChartVO : areaChartVOList) {
                String yearMonth = areaChartVO.getX();
                if (yearMonthTrainMaterialListMap.containsKey(yearMonth)) {
                    List<BuRptBoardTrainMaterial> costList = yearMonthTrainMaterialListMap.get(yearMonth);
                    BigDecimal totalCost = costList.stream()
                            .map(BuRptBoardTrainMaterial::getTotalCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    areaChartVO.setY(totalCost.doubleValue());
                }
            }
        }

        return areaChartVOList;
    }

    /**
     * @see BuRptBoardTrainMaterialService#listMaterialCost(BuBoardCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRptBoardTrainMaterialProgramVO> listMaterialCost(BuBoardCostQueryVO queryVO) throws Exception {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<BuRptBoardTrainMaterial> trainMaterialList = buRptBoardTrainMaterialMapper.listByYearAndCondition(year, queryVO);
        List<BuContractPay> contractPayList = buCostCustomMapper.selectContractPayListByYearAndCondition(year, queryVO);

        boolean allDepot = StringUtils.isBlank(queryVO.getDepotId());

        return getDepotVO(trainMaterialList, contractPayList, allDepot).getPrograms();
    }

    /**
     * @see BuRptBoardTrainMaterialService#getLastTenTrainNo(BuBoardCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<String> getLastTenTrainNo(BuBoardCostQueryVO queryVO) {
        int resultSize = 10;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<BuRptBoardTrainMaterial> trainMaterialList = buRptBoardTrainMaterialMapper.listByYearAndCondition(year, queryVO);

        return computeLastTenTrainNo(trainMaterialList, resultSize);
    }

    /**
     * @see BuRptBoardTrainMaterialService#getLastTenTrainCost(BuBoardCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<Map<String, Object>> getLastTenTrainCost(BuBoardCostQueryVO queryVO) throws Exception {
        int resultSize = 10;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<BuRptBoardTrainMaterial> trainMaterialList = buRptBoardTrainMaterialMapper.listByYearAndCondition(year, queryVO);

        if (CollectionUtils.isEmpty(trainMaterialList)) {
            return new ArrayList<>();
        }

        setTotalCost(trainMaterialList);
        return computeLastTenTrainCost(trainMaterialList, resultSize);
    }


    private BuRptBoardTrainMaterialDepotVO getDepotVO(List<BuRptBoardTrainMaterial> trainMaterialList, List<BuContractPay> contractPayList, boolean allDepot) {
        String depotName = allDepot ? "所有车辆段" : trainMaterialList.get(0).getDepotName();

        // 查询修程信息
        List<BuRepairProgram> programList = buCostCustomMapper.selectAllRepairProgram();
        Map<String, BuRepairProgram> idProgramMap = new HashMap<>(4);
        programList.forEach(program -> idProgramMap.put(program.getId(), program));

        // 按修程分组
        Map<String, List<BuRptBoardTrainMaterial>> programIdTrainMaterialListMap = trainMaterialList.stream()
                .collect(Collectors.groupingBy(BuRptBoardTrainMaterial::getProgramId));

        List<BuRptBoardTrainMaterialProgramVO> programVOList = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;
        for (Map.Entry<String, List<BuRptBoardTrainMaterial>> programIdTrainMaterialListEntry : programIdTrainMaterialListMap.entrySet()) {
            String programId = programIdTrainMaterialListEntry.getKey();
            List<BuRptBoardTrainMaterial> boardTrainMaterialList = programIdTrainMaterialListEntry.getValue();
            if (CollectionUtils.isEmpty(boardTrainMaterialList)) {
                continue;
            }

            String programName = null;
            BuRepairProgram program = idProgramMap.get(programId);
            if (null != program) {
                programName = program.getName();
            }

            BigDecimal mustCost = boardTrainMaterialList.stream()
                    .map(BuRptBoardTrainMaterial::getMustCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal randomCost = boardTrainMaterialList.stream()
                    .map(BuRptBoardTrainMaterial::getRandomCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal consumeCost = boardTrainMaterialList.stream()
                    .map(BuRptBoardTrainMaterial::getConsumeCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal programTotalCost = mustCost.add(randomCost).add(consumeCost);
            long programTrainQuantity = boardTrainMaterialList.stream()
                    .map(BuRptBoardTrainMaterial::getTrainNo)
                    .distinct()
                    .count();
            BigDecimal singleTrainAverageCost = programTotalCost.divide(new BigDecimal(programTrainQuantity), 8, BigDecimal.ROUND_HALF_UP);

            // 计算委外修
            BigDecimal outPayCost = contractPayList.stream()
                    .filter(pay -> programId.equals(pay.getRepairProgramId()))
                    .map(BuContractPay::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BuRptBoardTrainMaterialProgramVO programVO = new BuRptBoardTrainMaterialProgramVO()
                    .setDepotName(depotName)
                    .setProgramName(programName)
                    .setTotalCost(programTotalCost)
                    .setMustCost(mustCost)
                    .setRandomCost(randomCost)
                    .setConsumeCost(consumeCost)
                    .setSingleTrainAverageCost(singleTrainAverageCost)
                    .setSelfRepairCost(programTotalCost)
                    .setOutsourceRepairCost(outPayCost);
            programVOList.add(programVO);

            totalCost = totalCost.add(programTotalCost);
        }

        long totalTrainQuantity = trainMaterialList.stream()
                .map(BuRptBoardTrainMaterial::getTrainNo)
                .distinct()
                .count();
        if (totalTrainQuantity > 0) {
            BigDecimal totalSingleTrainAverageCost = totalCost.divide(new BigDecimal(totalTrainQuantity), 8, BigDecimal.ROUND_HALF_UP);
            addTotalProgramVO(programVOList, depotName, totalSingleTrainAverageCost);
        }

        return new BuRptBoardTrainMaterialDepotVO()
                .setDepotName(depotName)
                .setPrograms(programVOList);
    }

    private void addTotalProgramVO(List<BuRptBoardTrainMaterialProgramVO> programVOList, String depotName, BigDecimal totalSingleTrainAverageCost) {
        if (CollectionUtils.isNotEmpty(programVOList)) {
            BuRptBoardTrainMaterialProgramVO totalProgramVO = new BuRptBoardTrainMaterialProgramVO()
                    .setDepotName(depotName)
                    .setProgramName("合计")
                    .setTotalCost(programVOList.stream()
                            .map(BuRptBoardTrainMaterialProgramVO::getTotalCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .setMustCost(programVOList.stream()
                            .map(BuRptBoardTrainMaterialProgramVO::getMustCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .setRandomCost(programVOList.stream()
                            .map(BuRptBoardTrainMaterialProgramVO::getRandomCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .setConsumeCost(programVOList.stream()
                            .map(BuRptBoardTrainMaterialProgramVO::getConsumeCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .setSingleTrainAverageCost(totalSingleTrainAverageCost)
                    .setSelfRepairCost(programVOList.stream()
                            .map(BuRptBoardTrainMaterialProgramVO::getSelfRepairCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .setOutsourceRepairCost(programVOList.stream()
                            .map(BuRptBoardTrainMaterialProgramVO::getOutsourceRepairCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
            programVOList.add(totalProgramVO);
        }
    }

    private void setTotalCost(List<BuRptBoardTrainMaterial> trainMaterialList) {
        if (CollectionUtils.isNotEmpty(trainMaterialList)) {
            for (BuRptBoardTrainMaterial trainMaterial : trainMaterialList) {
                trainMaterial.setTotalCost(trainMaterial.getMustCost().add(trainMaterial.getRandomCost()).add(trainMaterial.getConsumeCost()));
            }
        }
    }

    private List<AreaChartVO> initYearAreaChartVOList(int year) {
        int monthSize = 12;
        List<AreaChartVO> areaChartVOList = new ArrayList<>(monthSize);
        for (int i = 1; i <= monthSize; i++) {
            String yearMonth = year + "-" + i;
            areaChartVOList.add(new AreaChartVO()
                    .setX(yearMonth)
                    .setY(0D));
        }

        return areaChartVOList;
    }

    private List<String> computeLastTenTrainNo(List<BuRptBoardTrainMaterial> trainMaterialList, int resultSize) {
        List<String> resultList = new ArrayList<>();
        List<String> trainNoList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(trainMaterialList)) {
            int index = 1;
            for (BuRptBoardTrainMaterial trainMaterial : trainMaterialList) {
                if (index >= resultSize) {
                    break;
                }

                String trainNo = trainMaterial.getTrainNo();
                if (!trainNoList.contains(trainNo)) {
                    resultList.add(index + "." + trainNo);
                    index++;
                    trainNoList.add(trainNo);
                }
            }
        }

        return resultList;
    }

    private List<Map<String, Object>> computeLastTenTrainCost(List<BuRptBoardTrainMaterial> trainMaterialList, int resultSize) {
        // 获得最后10辆车车号
        List<String> trainNoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(trainMaterialList)) {
            int index = 1;
            for (BuRptBoardTrainMaterial trainMaterial : trainMaterialList) {
                if (index >= resultSize) {
                    break;
                }

                String trainNo = trainMaterial.getTrainNo();
                if (!trainNoList.contains(trainNo)) {
                    index++;
                    trainNoList.add(trainNo);
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> mustCostMap = new LinkedHashMap<>(16);
        mustCostMap.put("type", "必换件");
        Map<String, Object> randomCostMap = new LinkedHashMap<>(16);
        randomCostMap.put("type", "偶换件");
        Map<String, Object> consumeCostMap = new LinkedHashMap<>(16);
        consumeCostMap.put("type", "耗材");
//        Map<String, Object> totalCostMap = new LinkedHashMap<>(16);
//        totalCostMap.put("type", "总计");

        if (CollectionUtils.isNotEmpty(trainMaterialList)) {
            int index = 1;
            for (String trainNo : trainNoList) {
                List<BuRptBoardTrainMaterial> trainCostList = trainMaterialList.stream()
                        .filter(cost -> trainNo.equals(cost.getTrainNo()))
                        .collect(Collectors.toList());
                BigDecimal mustCost = trainCostList.stream()
                        .map(BuRptBoardTrainMaterial::getMustCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal randomCost = trainCostList.stream()
                        .map(BuRptBoardTrainMaterial::getRandomCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal consumeCost = trainCostList.stream()
                        .map(BuRptBoardTrainMaterial::getConsumeCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                String indexTrainNo = index + "." + trainNo;
                mustCostMap.put(indexTrainNo, mustCost);
                randomCostMap.put(indexTrainNo, randomCost);
                consumeCostMap.put(indexTrainNo, consumeCost);
//                totalCostMap.put(indexTrainNo, trainMaterial.getTotalCost());
                index++;
            }
        }

        result.add(mustCostMap);
        result.add(randomCostMap);
        result.add(consumeCostMap);
//        result.add(totalCostMap);

        return result;
    }

}
