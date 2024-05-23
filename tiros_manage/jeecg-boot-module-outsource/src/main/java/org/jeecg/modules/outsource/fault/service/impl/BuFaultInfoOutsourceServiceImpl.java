package org.jeecg.modules.outsource.fault.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.modules.outsource.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.outsource.fault.bean.BuFaultInfo;
import org.jeecg.modules.outsource.fault.bean.vo.*;
import org.jeecg.modules.outsource.fault.mapper.BuFaultInfoOutsourceMapper;
import org.jeecg.modules.outsource.fault.service.BuFaultInfoOutsourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-19
 */
@Slf4j
@Service
public class BuFaultInfoOutsourceServiceImpl extends ServiceImpl<BuFaultInfoOutsourceMapper, BuFaultInfo> implements BuFaultInfoOutsourceService {

    @Resource
    private BuFaultInfoOutsourceMapper buFaultInfoOutsourceMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see BuFaultInfoOutsourceService#page(BuFaultInfoQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFaultInfo> page(BuFaultInfoQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        queryVO.toStartEndDate();

        IPage<BuFaultInfo> page = buFaultInfoOutsourceMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

        List<BuFaultInfo> faultInfoList = page.getRecords();
        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            for (BuFaultInfo faultInfo : faultInfoList) {
                faultInfo.setReportUserName(userIdNameMap.get(faultInfo.getReportUserId()));
                faultInfo.setDutyEngineerName(userIdNameMap.get(faultInfo.getDutyEngineer()));
                faultInfo.setCloseUserName(userIdNameMap.get(faultInfo.getCloseUserId()));
            }
        }

        return page;
    }

    /**
     * @see BuFaultInfoOutsourceService#getFaultById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuFaultInfo getFaultById(String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoOutsourceMapper.selectFaultById(id);

        Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

        if (null != faultInfo) {
            faultInfo.setReportUserName(userIdNameMap.get(faultInfo.getReportUserId()));
            faultInfo.setDutyEngineerName(userIdNameMap.get(faultInfo.getDutyEngineer()));
            faultInfo.setCloseUserName(userIdNameMap.get(faultInfo.getCloseUserId()));

            List<BuFaultHandleRecord> handleRecordList = faultInfo.getHandleRecordList();
            if (CollectionUtils.isNotEmpty(handleRecordList)) {
                handleRecordList.sort(Comparator.comparing(BuFaultHandleRecord::getSolutionTime, Comparator.nullsLast(Comparator.naturalOrder())));
                faultInfo.setHandleRecordList(handleRecordList.subList(0, 1));
            }
        }

        return faultInfo;
    }

    /**
     * @see BuFaultInfoOutsourceService#listHandleRecordById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultHandleRecord> listHandleRecordById(String id) throws Exception {
        return buFaultInfoOutsourceMapper.selectHandleRecordListByFaultId(id);
    }

    /**
     * @see BuFaultInfoOutsourceService#countFault(BuFaultInfoQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public FaultCountInfoVO countFault(BuFaultInfoQueryVO queryVO) {
        List<BuFaultInfo> faultInfoList = buFaultInfoOutsourceMapper.selectIdStatusListForCountByCondition(queryVO);

        FaultCountInfoVO countInfoVO = new FaultCountInfoVO()
                .setTotal(0)
                .setHandled(0)
                .setUnhandled(0);

        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            countInfoVO.setTotal(faultInfoList.size());
            countInfoVO.setUnhandled(new Long(faultInfoList.stream().filter(fault -> fault.getStatus() == 0).count()).intValue());
            countInfoVO.setHandled(countInfoVO.getTotal() - countInfoVO.getUnhandled());
        }

        return countInfoVO;
    }

    /**
     * @see BuFaultInfoOutsourceService#countFaultGroupBySystem(BuFaultInfoQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public FaultCountInfoVO countFaultGroupBySystem(BuFaultInfoQueryVO queryVO) {
        List<BuFaultInfo> faultInfoList = buFaultInfoOutsourceMapper.selectSimpleListByCondition(queryVO);

        return getFaultCountInfoVO(faultInfoList);
    }

    /**
     * @see BuFaultInfoOutsourceService#compareCountFaultGroupByPhaseAndSystem(BuFaultInfoCompareQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuFaultInfoCompareResultVO compareCountFaultGroupByPhaseAndSystem(BuFaultInfoCompareQueryVO compareQueryVO) {
        if (null == compareQueryVO.getDoCompare()) {
            compareQueryVO.setDoCompare(false);
        }
        Boolean doCompare = compareQueryVO.getDoCompare();

        if (!doCompare) {
            List<FaultCountInfoVO> result1 = countFaultGroupByPhaseAndSystem(compareQueryVO.getQuery1());
            return new BuFaultInfoCompareResultVO().setDoCompare(false).setResult1(result1);
        }

        List<FaultCountInfoVO> result1 = countFaultGroupByPhaseAndSystem(compareQueryVO.getQuery1());
        List<FaultCountInfoVO> result2 = countFaultGroupByPhaseAndSystem(compareQueryVO.getQuery2());
        return new BuFaultInfoCompareResultVO().setDoCompare(true).setResult1(result1).setResult2(result2);
    }


    private FaultCountInfoVO getFaultCountInfoVO(List<BuFaultInfo> faultInfoList) {
        FaultCountInfoVO countInfoVO = new FaultCountInfoVO()
                .setTotal(0);

        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            countInfoVO.setTotal(faultInfoList.size());

            List<FaultCountSystemItemVO> systemCountItemVOList = new ArrayList<>();
            Map<String, Long> sysNameCountMap = faultInfoList.stream()
                    .filter(fault -> StringUtils.isNotBlank(fault.getSysName()))
                    .collect(Collectors.groupingBy(BuFaultInfo::getSysName, Collectors.counting()));
            for (Map.Entry<String, Long> sysNameCountEntry : sysNameCountMap.entrySet()) {
                FaultCountSystemItemVO systemCountItemVO = new FaultCountSystemItemVO()
                        .setSystemName(sysNameCountEntry.getKey())
                        .setFaultCount(sysNameCountEntry.getValue().intValue());
                systemCountItemVOList.add(systemCountItemVO);
            }
            countInfoVO.setSystemItemList(systemCountItemVOList);
        }
        return countInfoVO;
    }

    private List<FaultCountInfoVO> countFaultGroupByPhaseAndSystem(BuFaultInfoQueryVO queryVO) {
        List<BuFaultInfo> faultInfoList = buFaultInfoOutsourceMapper.selectSimpleListByCondition(queryVO);

        List<FaultCountInfoVO> countInfoVOList = new ArrayList<>(2);

        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            Map<Integer, List<BuFaultInfo>> phaseFaultInfoListMap = faultInfoList.stream()
                    .filter(fault -> null != fault.getPhase())
                    .collect(Collectors.groupingBy(BuFaultInfo::getPhase));

            for (Map.Entry<Integer, List<BuFaultInfo>> phaseFaultInfoListEntry : phaseFaultInfoListMap.entrySet()) {
                FaultCountInfoVO countInfoVO = getFaultCountInfoVO(phaseFaultInfoListEntry.getValue());

                countInfoVO.setPhase(phaseFaultInfoListEntry.getKey());
                countInfoVOList.add(countInfoVO);
            }

        }

        return countInfoVOList;
    }

}
