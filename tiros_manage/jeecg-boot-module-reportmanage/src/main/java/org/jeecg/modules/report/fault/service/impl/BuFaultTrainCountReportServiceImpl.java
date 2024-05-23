package org.jeecg.modules.report.fault.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.modules.report.fault.bean.BuFaultInfo;
import org.jeecg.modules.report.fault.bean.BuRptBoardSysFault;
import org.jeecg.modules.report.fault.bean.vo.FaultCountQueryVO;
import org.jeecg.modules.report.fault.bean.vo.FaultTrainCountSysVO;
import org.jeecg.modules.report.fault.bean.vo.FaultTrainCountVO;
import org.jeecg.modules.report.fault.mapper.BuFaultInfoReportMapper;
import org.jeecg.modules.report.fault.mapper.BuRptBoardSysFaultReportMapper;
import org.jeecg.modules.report.fault.service.BuFaultTrainCountReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 故障汇总(单列) 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-15
 */
@Slf4j
@Service
public class BuFaultTrainCountReportServiceImpl extends ServiceImpl<BuRptBoardSysFaultReportMapper, BuRptBoardSysFault> implements BuFaultTrainCountReportService {

    @Resource
    private BuRptBoardSysFaultReportMapper buRptBoardSysFaultReportMapper;
    @Resource
    private BuFaultInfoReportMapper buFaultInfoReportMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuFaultTrainCountReportService#countTrainSysFault(FaultCountQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<FaultTrainCountSysVO> countTrainSysFault(FaultCountQueryVO queryVO) throws Exception {
//        queryVO.toYearMonthList();
//        List<BuRptBoardSysFault> sysFaultList = buRptBoardSysFaultReportMapper.selectListByFaultCountQueryVO(queryVO);
        if (null != queryVO.getStartDate()) {
            queryVO.setStartDate(DateUtil.beginOfDay(queryVO.getStartDate()));
        }
        if (null != queryVO.getEndDate()) {
            queryVO.setEndDate(DateUtil.endOfDay(queryVO.getEndDate()));
        }
        List<BuFaultInfo> faultList = buFaultInfoReportMapper.selectListByFaultCountQueryVO(queryVO);

        return getTrainCountSysVOList(faultList);
    }


    //    private List<FaultTrainCountSysVO> getTrainCountSysVOList(List<BuRptBoardSysFault> sysFaultList) {
//        List<FaultTrainCountSysVO> trainCountSysVOList = new ArrayList<>();
//
//        Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(null);
//        for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
//            String sysId = sysEntry.getKey();
//            BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
//            String sysShortName = sysAssetTypeBO.getShortName();
//
//            List<BuRptBoardSysFault> faultList = sysFaultList.stream()
//                    .filter(fault -> sysId.equals(fault.getSysId()))
//                    .collect(Collectors.toList());
//
//            int repair = faultList.stream().mapToInt(BuRptBoardSysFault::getSum1).sum();
//            int repairAB = faultList.stream().mapToInt(BuRptBoardSysFault::getSum2).sum();
//            int warranty = faultList.stream().mapToInt(BuRptBoardSysFault::getSum3).sum();
//            int warrantyAB = faultList.stream().mapToInt(BuRptBoardSysFault::getSum5).sum();
//            int warrantyOnline = faultList.stream().mapToInt(BuRptBoardSysFault::getSum4).sum();
//            int warrantyHasDuty = faultList.stream().mapToInt(BuRptBoardSysFault::getSum6).sum();
//
//            FaultTrainCountSysVO trainCountSysVO = new FaultTrainCountSysVO()
//                    .setSys(sysShortName)
//                    .setRepair(repair)
//                    .setRepairAB(repairAB)
//                    .setWarranty(warranty)
//                    .setWarrantyAB(warrantyAB)
//                    .setWarrantyOnline(warrantyOnline)
//                    .setWarrantyHasDuty(warrantyHasDuty);
//
//            trainCountSysVOList.add(trainCountSysVO);
//        }
//
//        trainCountSysVOList.sort(Comparator.comparing(FaultTrainCountSysVO::getSys));
//        return trainCountSysVOList;
//    }
    private List<FaultTrainCountSysVO> getTrainCountSysVOList(List<BuFaultInfo> faultList) {
        List<FaultTrainCountSysVO> trainCountSysVOList = new ArrayList<>();

        Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(null);
        for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
            String sysId = sysEntry.getKey();
            BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
            String sysShortName = sysAssetTypeBO.getShortName();

            int repair = 0;
            int repairAB = 0;
            int warranty = 0;
            int warrantyOnline = 0;
            int warrantyAB = 0;
            int warrantyHasDuty = 0;
            int expireWarranty = 0;

            for (BuFaultInfo fault : faultList) {
                if (sysId.equals(fault.getSysId())) {
                    int phase = null == fault.getPhase() ? -1 : fault.getPhase();
                    int level = null == fault.getFaultLevel() ? -1 : fault.getFaultLevel();
                    int online = null == fault.getFaultOnline() ? -1 : fault.getFaultOnline();
                    int hasDuty = null == fault.getHasDuty() ? -1 : fault.getHasDuty();
                    if (phase == 1 || phase == 4) {
                        repair ++;
                        if (level == 1 || level == 2) {
                            repairAB ++;
                        }
                    } else if (phase == 2) {
                        warranty ++;
                        if (online == 1) {
                            warrantyOnline ++;
                        }
                        if (level == 1 || level == 2) {
                            warrantyAB ++;
                        }
                        if (hasDuty == 1) {
                            warrantyHasDuty ++;
                        }
                    } else if (phase == 3) {
                        expireWarranty ++;
                    }
                }
            }

            FaultTrainCountSysVO trainCountSysVO = new FaultTrainCountSysVO()
                    .setSys(sysShortName)
                    .setRepair(repair)
                    .setRepairAB(repairAB)
                    .setWarranty(warranty)
                    .setWarrantyAB(warrantyAB)
                    .setWarrantyOnline(warrantyOnline)
                    .setWarrantyHasDuty(warrantyHasDuty);

            trainCountSysVOList.add(trainCountSysVO);
        }

        trainCountSysVOList.sort(Comparator.comparing(FaultTrainCountSysVO::getSys));
        return trainCountSysVOList;
    }

}
