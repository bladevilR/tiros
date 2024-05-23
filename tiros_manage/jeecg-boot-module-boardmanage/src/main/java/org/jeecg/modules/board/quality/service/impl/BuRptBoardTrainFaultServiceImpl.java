package org.jeecg.modules.board.quality.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.modules.board.quality.bean.BuRptBoardTrainFault;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;
import org.jeecg.modules.board.quality.bean.vo.BuCenterQualityFaultDepotVO;
import org.jeecg.modules.board.quality.bean.vo.BuCenterQualityFaultProgramVO;
import org.jeecg.modules.board.quality.bean.vo.BuCenterQualityFaultTrendVO;
import org.jeecg.modules.board.quality.mapper.BuRptBoardTrainFaultMapper;
import org.jeecg.modules.board.quality.service.BuRptBoardTrainFaultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 故障统计-中间表-车辆维度 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-12
 */
@Slf4j
@Service
public class BuRptBoardTrainFaultServiceImpl extends ServiceImpl<BuRptBoardTrainFaultMapper, BuRptBoardTrainFault> implements BuRptBoardTrainFaultService {

    @Resource
    private BuRptBoardTrainFaultMapper buRptBoardTrainFaultMapper;


    /**
     * @see BuRptBoardTrainFaultService#listDepotFault(BuBoardQualityQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuCenterQualityFaultProgramVO> listDepotFault(BuBoardQualityQueryVO queryVO) throws Exception {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<BuRptBoardTrainFault> trainFaultList = buRptBoardTrainFaultMapper.listByCondition(queryVO, year);

        return getDepotVO(trainFaultList, StringUtils.isBlank(queryVO.getDepotId())).getPrograms();
    }

    /**
     * @see BuRptBoardTrainFaultService#listWarrantyPeriodFaultTrend(BuBoardQualityQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<LineChartVO> listWarrantyPeriodFaultTrend(BuBoardQualityQueryVO queryVO) throws Exception {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<BuRptBoardTrainFault> trainFaultList = buRptBoardTrainFaultMapper.listByCondition(queryVO, year);

        int warrantyPeriodFaultCount = trainFaultList.stream().mapToInt(BuRptBoardTrainFault::getSum3).sum();
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        Integer averageWarrantyPeriodFaultCount = new BigDecimal(warrantyPeriodFaultCount).divide(new BigDecimal(currentMonth), 1, BigDecimal.ROUND_HALF_UP).intValue();

        List<BuCenterQualityFaultTrendVO> trendVOList = new ArrayList<>();
        for (int i = 1; i <= currentMonth; i++) {
            int month = i;
            int monthWarrantyPeriodFaultCount = trainFaultList.stream().filter(fault -> fault.getMonth() == month).mapToInt(BuRptBoardTrainFault::getSum3).sum();
            trendVOList.add(
                    new BuCenterQualityFaultTrendVO()
                            .setYearMonth(year + "-" + month)
                            .setAverage(averageWarrantyPeriodFaultCount)
                            .setActual(monthWarrantyPeriodFaultCount)
            );
        }

        return transformToLineChartVOList(trendVOList);
    }


    private BuCenterQualityFaultDepotVO getDepotVO(List<BuRptBoardTrainFault> trainFaultList, boolean allDepot) {
        String depotName = allDepot ? "所有车辆段" : trainFaultList.get(0).getDepotName();
        List<BuCenterQualityFaultProgramVO> programVOList = new ArrayList<>();

        Map<String, List<BuRptBoardTrainFault>> programNameTrainFaultListMap = trainFaultList.stream()
                .filter(fault -> StringUtils.isNotBlank(fault.getProgramName()))
                .collect(Collectors.groupingBy(BuRptBoardTrainFault::getProgramName));
        for (Map.Entry<String, List<BuRptBoardTrainFault>> programNameTrainFaultListEntry : programNameTrainFaultListMap.entrySet()) {
            String programName = programNameTrainFaultListEntry.getKey();
            List<BuRptBoardTrainFault> boardTrainFaultList = programNameTrainFaultListEntry.getValue();

            programVOList.add(getBuCenterQualityFaultProgramVO(depotName, programName, boardTrainFaultList));
        }

        addTotalProgramVO(programVOList, depotName);

        return new BuCenterQualityFaultDepotVO()
                .setDepotName(depotName)
                .setPrograms(programVOList);
    }

    private BuCenterQualityFaultProgramVO getBuCenterQualityFaultProgramVO(String depotName, String programName, List<BuRptBoardTrainFault> boardTrainFaultList) {
        return new BuCenterQualityFaultProgramVO()
                .setDepotName(depotName)
                .setProgramName(programName)
                .setSum1(boardTrainFaultList.stream().mapToInt(BuRptBoardTrainFault::getSum1).sum())
                .setSum2(boardTrainFaultList.stream().mapToInt(BuRptBoardTrainFault::getSum2).sum())
                .setSum3(boardTrainFaultList.stream().mapToInt(BuRptBoardTrainFault::getSum3).sum())
                .setSum4(boardTrainFaultList.stream().mapToInt(BuRptBoardTrainFault::getSum4).sum())
                .setSum5(boardTrainFaultList.stream().mapToInt(BuRptBoardTrainFault::getSum5).sum())
                .setSum6(boardTrainFaultList.stream().mapToInt(BuRptBoardTrainFault::getSum6).sum())
                .setSum7(boardTrainFaultList.stream().mapToInt(BuRptBoardTrainFault::getSum7).sum());
    }

    private void addTotalProgramVO(List<BuCenterQualityFaultProgramVO> programVOList, String depotName) {
        if (CollectionUtils.isNotEmpty(programVOList)) {
            BuCenterQualityFaultProgramVO totalProgramVO = new BuCenterQualityFaultProgramVO()
                    .setDepotName(depotName)
                    .setProgramName("合计")
                    .setSum1(programVOList.stream().mapToInt(BuCenterQualityFaultProgramVO::getSum1).sum())
                    .setSum2(programVOList.stream().mapToInt(BuCenterQualityFaultProgramVO::getSum2).sum())
                    .setSum3(programVOList.stream().mapToInt(BuCenterQualityFaultProgramVO::getSum3).sum())
                    .setSum4(programVOList.stream().mapToInt(BuCenterQualityFaultProgramVO::getSum4).sum())
                    .setSum5(programVOList.stream().mapToInt(BuCenterQualityFaultProgramVO::getSum5).sum())
                    .setSum6(programVOList.stream().mapToInt(BuCenterQualityFaultProgramVO::getSum6).sum())
                    .setSum7(programVOList.stream().mapToInt(BuCenterQualityFaultProgramVO::getSum7).sum());
            programVOList.add(totalProgramVO);
        }
    }

    private List<LineChartVO> transformToLineChartVOList(List<BuCenterQualityFaultTrendVO> trendVOList) {
        List<LineChartVO> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(trendVOList)) {
            for (BuCenterQualityFaultTrendVO trendVO : trendVOList) {
                LineChartVO lineChartVO = new LineChartVO()
                        .setType(trendVO.getYearMonth())
                        .setJeecg(trendVO.getAverage().doubleValue())
                        .setJeebt(trendVO.getActual().doubleValue());
                result.add(lineChartVO);
            }
        }

        return result;
    }

}
