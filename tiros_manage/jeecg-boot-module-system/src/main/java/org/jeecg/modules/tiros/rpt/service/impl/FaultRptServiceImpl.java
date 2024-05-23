package org.jeecg.modules.tiros.rpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.rpt.service.FaultRptService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.SameUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.board.quality.bean.BuRptBoardSysFault;
import org.jeecg.modules.board.quality.bean.BuRptBoardTrainFault;
import org.jeecg.modules.board.quality.mapper.BuRptBoardSysFaultMapper;
import org.jeecg.modules.board.quality.mapper.BuRptBoardTrainFaultMapper;
import org.jeecg.modules.dispatch.exchange.bean.BuBaseTrainRepairPeriod;
import org.jeecg.modules.dispatch.exchange.mapper.BuBaseTrainRepairPeriodMapper;
import org.jeecg.modules.dispatch.fault.bean.BuFaultInfo;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultInfoMapper;
import org.jeecg.modules.tiros.rpt.bean.BuFaultRptBO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * 故障统计 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-23
 */
@Service
public class FaultRptServiceImpl implements FaultRptService {

    @Resource
    private BuRptBoardTrainFaultMapper buRptBoardTrainFaultMapper;
    @Resource
    private BuRptBoardSysFaultMapper buRptBoardSysFaultMapper;
    @Resource
    private BuFaultInfoMapper buFaultInfoMapper;
    @Resource
    private BuBaseTrainRepairPeriodMapper buBaseTrainRepairPeriodMapper;


//    /**
//     * @see FaultRptService#increaseFaultRpt(String)
//     */
//    @Override
//    public void increaseFaultRpt(String faultId) {
//        if (StringUtils.isBlank(faultId)) {
//            return;
//        }
//        // 查询故障
//        BuFaultInfo fault = buFaultInfoMapper.selectFaultForRptById(faultId);
//        if (null == fault) {
//            return;
//        }
//        // 只有已提交的故障才增加对应数量
//        if (null == fault.getSubmitStatus() || 0 == fault.getSubmitStatus()) {
//            return;
//        }
//
//        BuFaultRptBO faultRptBO = transToFaultRptBO(fault);
//        if (null == faultRptBO) {
//            return;
//        }
//
//        // 获取查询wrapper
//        LambdaQueryWrapper<BuRptBoardTrainFault> trainWrapper = initTrainQueryWrapper(faultRptBO);
//        LambdaQueryWrapper<BuRptBoardSysFault> sysWrapper = initSysQueryWrapper(faultRptBO);
//        if (null == trainWrapper || null == sysWrapper) {
//            return;
//        }
//
//        // 车辆故障统计，不存在新增，存在增加数量
//        BuRptBoardTrainFault trainFault = buRptBoardTrainFaultMapper.selectOne(trainWrapper);
//        if (null == trainFault) {
//            trainFault = new BuRptBoardTrainFault();
//            BeanUtils.copyProperties(faultRptBO, trainFault);
//            trainFault.setId(UUIDGenerator.generate());
//            buRptBoardTrainFaultMapper.insert(trainFault);
//        } else {
//            increaseSum(trainFault, faultRptBO);
//            buRptBoardTrainFaultMapper.updateById(trainFault);
//        }
//        // 系统故障统计，不存在新增，存在增加数量
//        BuRptBoardSysFault sysFault = buRptBoardSysFaultMapper.selectOne(sysWrapper);
//        if (null == sysFault) {
//            sysFault = new BuRptBoardSysFault();
//            BeanUtils.copyProperties(faultRptBO, sysFault);
//            sysFault.setId(UUIDGenerator.generate());
//            buRptBoardSysFaultMapper.insert(sysFault);
//        } else {
//            increaseSum(sysFault, faultRptBO);
//            buRptBoardSysFaultMapper.updateById(sysFault);
//        }
//    }

    /**
     * @see FaultRptService#rebuildFaultRpt(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String rebuildFaultRpt(List<String> trainNoList) {
        // 查询故障
        List<BuFaultInfo> faultList = buFaultInfoMapper.selectAllFaultForRpt(trainNoList);
        if (CollectionUtils.isEmpty(faultList)) {
            return "无未取消的故障，未生成统计信息";
        }

        // 查询车辆架修周期
        List<BuBaseTrainRepairPeriod> periodList = buBaseTrainRepairPeriodMapper.selectList(Wrappers.emptyWrapper());

        List<BuRptBoardTrainFault> trainFaultList = new ArrayList<>();
        List<BuRptBoardSysFault> sysFaultList = new ArrayList<>();
        for (BuFaultInfo fault : faultList) {
            BuFaultRptBO faultRptBO = transToFaultRptBO(fault, periodList);
            if (null != faultRptBO) {
                BuRptBoardTrainFault trainFault = getTrainFaultFromList(trainFaultList, faultRptBO);
                if (null == trainFault) {
                    trainFault = new BuRptBoardTrainFault();
                    BeanUtils.copyProperties(faultRptBO, trainFault);
                    trainFault.setId(UUIDGenerator.generate());
                    trainFaultList.add(trainFault);
                } else {
                    increaseSum(trainFault, faultRptBO);
                }

                BuRptBoardSysFault sysFault = getSysFaultFromList(sysFaultList, faultRptBO);
                if (null == sysFault) {
                    sysFault = new BuRptBoardSysFault();
                    BeanUtils.copyProperties(faultRptBO, sysFault);
                    sysFault.setId(UUIDGenerator.generate());
                    sysFaultList.add(sysFault);
                } else {
                    increaseSum(sysFault, faultRptBO);
                }
            }
        }

        // 删除旧的统计信息
        if (CollectionUtils.isEmpty(trainNoList)) {
            buRptBoardTrainFaultMapper.delete(Wrappers.emptyWrapper());
            buRptBoardSysFaultMapper.delete(Wrappers.emptyWrapper());
        } else {
            LambdaQueryWrapper<BuRptBoardTrainFault> trainFaultWrapper = new LambdaQueryWrapper<BuRptBoardTrainFault>()
                    .in(BuRptBoardTrainFault::getTrainNo, trainNoList);
            buRptBoardTrainFaultMapper.delete(trainFaultWrapper);
            LambdaQueryWrapper<BuRptBoardSysFault> sysFaultWrapper = new LambdaQueryWrapper<BuRptBoardSysFault>()
                    .in(BuRptBoardSysFault::getTrainNo, trainNoList);
            buRptBoardSysFaultMapper.delete(sysFaultWrapper);
        }

        // 插入新的统计信息
        if (CollectionUtils.isNotEmpty(trainFaultList)) {
            List<List<BuRptBoardTrainFault>> batchSubList = DatabaseBatchSubUtil.batchSubList(trainFaultList);
            for (List<BuRptBoardTrainFault> batchSub : batchSubList) {
                buRptBoardTrainFaultMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(sysFaultList)) {
            List<List<BuRptBoardSysFault>> batchSubList = DatabaseBatchSubUtil.batchSubList(sysFaultList);
            for (List<BuRptBoardSysFault> batchSub : batchSubList) {
                buRptBoardSysFaultMapper.insertList(batchSub);
            }
        }

        return String.format("插入了%s条车辆故障统计，%s条系统故障统计；", trainFaultList.size(), sysFaultList.size());
    }


    private BuFaultRptBO transToFaultRptBO(BuFaultInfo fault, List<BuBaseTrainRepairPeriod> periodList) {
        if (null == fault) {
            return null;
        }
        // 设置架修周期
        BuBaseTrainRepairPeriod period = getRepairPeriodByFault(periodList, fault);
        if (null != period) {
            fault.setPeriodId(period.getId());
            fault.setRepairIndex(period.getRepairIndex());
        }

        BuFaultRptBO faultRptBO = new BuFaultRptBO()
                .setDepotId(fault.getDepotId())
                .setLineId(fault.getLineId())
                .setWorkshopId(fault.getWorkshopId())
                .setCompanyId(fault.getCompanyId())
                .setRepairPeriod(fault.getPeriodId())
                .setTrainId(fault.getTrainId())
                .setTrainNo(fault.getTrainNo())
                .setRepairIndex(fault.getRepairIndex())
                .setYear(DateUtils.getYear(fault.getReportTime()))
                .setMonth(DateUtils.getMonth(fault.getReportTime()))
                .setProgramId(fault.getProgramId())
                .setSysId(fault.getSysId())
                .setSum1(0)
                .setSum2(0)
                .setSum3(0)
                .setSum4(0)
                .setSum5(0)
                .setSum6(0)
                .setSum7(0);

        int phase = null == fault.getPhase() ? -1 : fault.getPhase();
        int level = null == fault.getFaultLevel() ? -1 : fault.getFaultLevel();
        int online = null == fault.getFaultOnline() ? -1 : fault.getFaultOnline();
        int hasDuty = null == fault.getHasDuty() ? -1 : fault.getHasDuty();
        if (phase == 1 || phase == 4) {
            faultRptBO.setSum1(1);
            if (level == 1 || level == 2) {
                faultRptBO.setSum2(1);
            }
        } else if (phase == 2) {
            faultRptBO.setSum3(1);
            if (online == 1) {
                faultRptBO.setSum4(1);
            }
            if (level == 1 || level == 2) {
                faultRptBO.setSum5(1);
            }
            if (hasDuty == 1) {
                faultRptBO.setSum6(1);
            }
        } else if (phase == 3) {
            faultRptBO.setSum7(1);
        }

        return faultRptBO;
    }

    private BuBaseTrainRepairPeriod getRepairPeriodByFault(List<BuBaseTrainRepairPeriod> periodList, BuFaultInfo fault) {
        if (CollectionUtils.isEmpty(periodList) || null == fault) {
            return null;
        }

        String programId = fault.getProgramId();
        if (StringUtils.isBlank(programId)) {
            return null;
        }
        if (null == fault.getHappenTime()) {
            return null;
        }
        int year = DateUtils.getYear(fault.getHappenTime());
        String lineId = fault.getLineId();
        String workshopId = fault.getWorkshopId();
        String companyId = fault.getCompanyId();

        List<BuBaseTrainRepairPeriod> filterList = new ArrayList<>();
        for (BuBaseTrainRepairPeriod period : periodList) {
            if (programId.equals(period.getRepairProgram())
                    && year == period.getYear()
                    && lineId.equals(period.getLineId())
                    && workshopId.equals(period.getWorkshopId())
                    && companyId.equals(period.getCompanyId())) {
                filterList.add(period);
            }
        }
        if (CollectionUtils.isEmpty(filterList)) {
            return null;
        } else {
            filterList.sort(Comparator.comparing(BuBaseTrainRepairPeriod::getRepairIndex, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            return filterList.get(0);
        }
    }

    private LambdaQueryWrapper<BuRptBoardTrainFault> initTrainQueryWrapper(BuFaultRptBO faultRptBO) {
        boolean valid = checkFaultRptBOValid(faultRptBO);
        if (!valid) {
            return null;
        }

        return new LambdaQueryWrapper<BuRptBoardTrainFault>()
                .eq(BuRptBoardTrainFault::getDepotId, faultRptBO.getDepotId())
                .eq(BuRptBoardTrainFault::getLineId, faultRptBO.getLineId())
                .eq(BuRptBoardTrainFault::getWorkshopId, faultRptBO.getWorkshopId())
                .eq(BuRptBoardTrainFault::getCompanyId, faultRptBO.getCompanyId())
                .eq(BuRptBoardTrainFault::getRepairPeriod, faultRptBO.getRepairPeriod())
                .eq(BuRptBoardTrainFault::getTrainId, faultRptBO.getTrainId())
                .eq(BuRptBoardTrainFault::getTrainNo, faultRptBO.getTrainNo())
                .eq(BuRptBoardTrainFault::getRepairIndex, faultRptBO.getRepairIndex())
                .eq(BuRptBoardTrainFault::getYear, faultRptBO.getYear())
                .eq(BuRptBoardTrainFault::getMonth, faultRptBO.getMonth())
                .eq(BuRptBoardTrainFault::getProgramId, faultRptBO.getProgramId());
    }

    private LambdaQueryWrapper<BuRptBoardSysFault> initSysQueryWrapper(BuFaultRptBO faultRptBO) {
        boolean valid = checkFaultRptBOValid(faultRptBO);
        if (!valid) {
            return null;
        }

        return new LambdaQueryWrapper<BuRptBoardSysFault>()
                .eq(BuRptBoardSysFault::getDepotId, faultRptBO.getDepotId())
                .eq(BuRptBoardSysFault::getLineId, faultRptBO.getLineId())
                .eq(BuRptBoardSysFault::getWorkshopId, faultRptBO.getWorkshopId())
                .eq(BuRptBoardSysFault::getCompanyId, faultRptBO.getCompanyId())
                .eq(BuRptBoardSysFault::getRepairPeriod, faultRptBO.getRepairPeriod())
                .eq(BuRptBoardSysFault::getTrainId, faultRptBO.getTrainId())
                .eq(BuRptBoardSysFault::getTrainNo, faultRptBO.getTrainNo())
                .eq(BuRptBoardSysFault::getRepairIndex, faultRptBO.getRepairIndex())
                .eq(BuRptBoardSysFault::getYear, faultRptBO.getYear())
                .eq(BuRptBoardSysFault::getMonth, faultRptBO.getMonth())
                .eq(BuRptBoardSysFault::getProgramId, faultRptBO.getProgramId())
                .eq(BuRptBoardSysFault::getSysId, faultRptBO.getSysId());
    }

    private boolean checkFaultRptBOValid(BuFaultRptBO faultRptBO) {
        if (StringUtils.isBlank(faultRptBO.getDepotId())) {
            return false;
        }
        if (StringUtils.isBlank(faultRptBO.getLineId())) {
            return false;
        }
        if (StringUtils.isBlank(faultRptBO.getWorkshopId())) {
            return false;
        }
        if (StringUtils.isBlank(faultRptBO.getCompanyId())) {
            return false;
        }
        if (StringUtils.isBlank(faultRptBO.getRepairPeriod())) {
            return false;
        }
        if (StringUtils.isBlank(faultRptBO.getTrainId())) {
            return false;
        }
        if (StringUtils.isBlank(faultRptBO.getTrainNo())) {
            return false;
        }
        if (null == faultRptBO.getRepairIndex()) {
            return false;
        }
        if (null == faultRptBO.getYear()) {
            return false;
        }
        if (null == faultRptBO.getMonth()) {
            return false;
        }
        if (StringUtils.isBlank(faultRptBO.getProgramId())) {
            return false;
        }
        if (StringUtils.isBlank(faultRptBO.getSysId())) {
            return false;
        }

        return true;
    }

    private BuRptBoardTrainFault getTrainFaultFromList(List<BuRptBoardTrainFault> trainFaultList, BuFaultRptBO faultRptBO) {
        if (CollectionUtils.isEmpty(trainFaultList)) {
            return null;
        }

        for (BuRptBoardTrainFault trainFault : trainFaultList) {
            boolean same = isSame(trainFault, faultRptBO);
            if (same) {
                return trainFault;
            }
        }

        return null;
    }

    private BuRptBoardSysFault getSysFaultFromList(List<BuRptBoardSysFault> sysFaultList, BuFaultRptBO faultRptBO) {
        if (CollectionUtils.isEmpty(sysFaultList)) {
            return null;
        }

        for (BuRptBoardSysFault sysFault : sysFaultList) {
            boolean same = isSame(sysFault, faultRptBO);
            if (same) {
                return sysFault;
            }
        }

        return null;
    }

    private boolean isSame(BuRptBoardTrainFault trainFault, BuFaultRptBO faultRptBO) {
        if (!SameUtil.same(trainFault.getDepotId(), faultRptBO.getDepotId())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getLineId(), faultRptBO.getLineId())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getWorkshopId(), faultRptBO.getWorkshopId())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getCompanyId(), faultRptBO.getCompanyId())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getRepairPeriod(), faultRptBO.getRepairPeriod())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getTrainId(), faultRptBO.getTrainId())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getTrainNo(), faultRptBO.getTrainNo())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getRepairIndex(), faultRptBO.getRepairIndex())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getYear(), faultRptBO.getYear())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getMonth(), faultRptBO.getMonth())) {
            return false;
        }
        if (!SameUtil.same(trainFault.getProgramId(), faultRptBO.getProgramId())) {
            return false;
        }

        return true;
    }

    private boolean isSame(BuRptBoardSysFault sysFault, BuFaultRptBO faultRptBO) {
        if (!SameUtil.same(sysFault.getDepotId(), faultRptBO.getDepotId())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getLineId(), faultRptBO.getLineId())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getWorkshopId(), faultRptBO.getWorkshopId())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getCompanyId(), faultRptBO.getCompanyId())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getRepairPeriod(), faultRptBO.getRepairPeriod())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getTrainId(), faultRptBO.getTrainId())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getTrainNo(), faultRptBO.getTrainNo())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getRepairIndex(), faultRptBO.getRepairIndex())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getYear(), faultRptBO.getYear())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getMonth(), faultRptBO.getMonth())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getProgramId(), faultRptBO.getProgramId())) {
            return false;
        }
        if (!SameUtil.same(sysFault.getSysId(), faultRptBO.getSysId())) {
            return false;
        }

        return true;
    }

    private void increaseSum(BuRptBoardTrainFault trainFault, BuFaultRptBO faultRptBO) {
        trainFault.setSum1(trainFault.getSum1() + faultRptBO.getSum1());
        trainFault.setSum2(trainFault.getSum2() + faultRptBO.getSum2());
        trainFault.setSum3(trainFault.getSum3() + faultRptBO.getSum3());
        trainFault.setSum4(trainFault.getSum4() + faultRptBO.getSum4());
        trainFault.setSum5(trainFault.getSum5() + faultRptBO.getSum5());
        trainFault.setSum6(trainFault.getSum6() + faultRptBO.getSum6());
        trainFault.setSum7(trainFault.getSum7() + faultRptBO.getSum7());
    }

    private void increaseSum(BuRptBoardSysFault sysFault, BuFaultRptBO faultRptBO) {
        sysFault.setSum1(sysFault.getSum1() + faultRptBO.getSum1());
        sysFault.setSum2(sysFault.getSum2() + faultRptBO.getSum2());
        sysFault.setSum3(sysFault.getSum3() + faultRptBO.getSum3());
        sysFault.setSum4(sysFault.getSum4() + faultRptBO.getSum4());
        sysFault.setSum5(sysFault.getSum5() + faultRptBO.getSum5());
        sysFault.setSum6(sysFault.getSum6() + faultRptBO.getSum6());
        sysFault.setSum7(sysFault.getSum7() + faultRptBO.getSum7());
    }

    private void decreaseSum(BuRptBoardTrainFault trainFault, BuFaultRptBO faultRptBO) {
        trainFault.setSum1(trainFault.getSum1() - faultRptBO.getSum1());
        trainFault.setSum2(trainFault.getSum2() - faultRptBO.getSum2());
        trainFault.setSum3(trainFault.getSum3() - faultRptBO.getSum3());
        trainFault.setSum4(trainFault.getSum4() - faultRptBO.getSum4());
        trainFault.setSum5(trainFault.getSum5() - faultRptBO.getSum5());
        trainFault.setSum6(trainFault.getSum6() - faultRptBO.getSum6());
        trainFault.setSum7(trainFault.getSum7() - faultRptBO.getSum7());
    }

    private void decreaseSum(BuRptBoardSysFault sysFault, BuFaultRptBO faultRptBO) {
        sysFault.setSum1(sysFault.getSum1() - faultRptBO.getSum1());
        sysFault.setSum2(sysFault.getSum2() - faultRptBO.getSum2());
        sysFault.setSum3(sysFault.getSum3() - faultRptBO.getSum3());
        sysFault.setSum4(sysFault.getSum4() - faultRptBO.getSum4());
        sysFault.setSum5(sysFault.getSum5() - faultRptBO.getSum5());
        sysFault.setSum6(sysFault.getSum6() - faultRptBO.getSum6());
        sysFault.setSum7(sysFault.getSum7() - faultRptBO.getSum7());
    }

}
