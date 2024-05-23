package org.jeecg.modules.produce.fault.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.produce.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;
import org.jeecg.modules.produce.fault.bean.vo.*;
import org.jeecg.modules.produce.fault.mapper.BuFaultInfoProduceMapper;
import org.jeecg.modules.produce.fault.service.BuFaultInfoProduceService;
import org.jeecg.modules.produce.trainhistory.bean.BuBaseTrainRepairPeriod;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryFault;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainHistoryFaultMapper;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainInfoProduceMapper;
import org.jeecg.modules.produce.workshop.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.produce.workshop.mapper.BuMtrWorkshopGroupProduceMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Slf4j
@Service
public class BuFaultInfoProduceServiceImpl extends ServiceImpl<BuFaultInfoProduceMapper, BuFaultInfo> implements BuFaultInfoProduceService {

    @Resource
    private BuFaultInfoProduceMapper buFaultInfoProduceMapper;
    @Resource
    private BuTrainHistoryFaultMapper buTrainHistoryFaultMapper;
    @Resource
    private BuTrainInfoProduceMapper buTrainInfoProduceMapper;
    @Resource
    private BuMtrWorkshopGroupProduceMapper buMtrWorkshopGroupProduceMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see BuFaultInfoProduceService#page(BuFaultInfoQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFaultInfo> page(BuFaultInfoQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        queryVO.toStartEndDate();
        IPage<BuFaultInfo> page = buFaultInfoProduceMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

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
     * @see BuFaultInfoProduceService#getFaultById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuFaultInfo getFaultById(String id) {
        BuFaultInfo faultInfo = buFaultInfoProduceMapper.selectFaultById(id);

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
     * @see BuFaultInfoProduceService#listHandleRecordById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultHandleRecord> listHandleRecordById(String id) throws Exception {
        return buFaultInfoProduceMapper.selectHandleRecordListByFaultId(id);
    }

    /**
     * @see BuFaultInfoProduceService#countFault(BuFaultInfoQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public FaultCountInfoVO countFault(BuFaultInfoQueryVO queryVO) {
        queryVO.toStartEndDate();
        List<BuFaultInfo> faultInfoList = buFaultInfoProduceMapper.selectIdStatusListForCountByCondition(queryVO);

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
     * @see BuFaultInfoProduceService#countFaultGroupBySystem(BuFaultInfoQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public FaultCountInfoVO countFaultGroupBySystem(BuFaultInfoQueryVO queryVO) {
        queryVO.toStartEndDate();
        List<BuFaultInfo> faultInfoList = buFaultInfoProduceMapper.selectSimpleListByCondition(queryVO);

        return getFaultCountInfoVO(faultInfoList);
    }


    /**
     * @see BuFaultInfoProduceService#compareCountFaultGroupByPhaseAndSystem(BuFaultInfoCompareQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuFaultInfoCompareResultVO compareCountFaultGroupByPhaseAndSystem(BuFaultInfoCompareQueryVO compareQueryVO) {
        if (null == compareQueryVO.getDoCompare()) {
            compareQueryVO.setDoCompare(false);
        }
        boolean doCompare = compareQueryVO.getDoCompare();

        if (!doCompare) {
            String result1Title = getCompareResultTitle(compareQueryVO.getQuery1());
            List<FaultCountInfoVO> result1 = countFaultGroupByPhaseAndSystem(compareQueryVO.getQuery1());
            return new BuFaultInfoCompareResultVO()
                    .setDoCompare(false)
                    .setResult1Title(result1Title)
                    .setResult1(result1);
        }

        String result1Title = getCompareResultTitle(compareQueryVO.getQuery1());
        String result2Title = getCompareResultTitle(compareQueryVO.getQuery2());
        List<FaultCountInfoVO> result1 = countFaultGroupByPhaseAndSystem(compareQueryVO.getQuery1());
        List<FaultCountInfoVO> result2 = countFaultGroupByPhaseAndSystem(compareQueryVO.getQuery2());
        return new BuFaultInfoCompareResultVO()
                .setDoCompare(true)
                .setResult1Title(result1Title)
                .setResult1(result1)
                .setResult2Title(result2Title)
                .setResult2(result2);
    }

    /**
     * @see BuFaultInfoProduceService#listLatestFault(String, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultInfo> listLatestFault(String workshopId, Integer num) {
        if (null == num) {
            num = 3;
        }
        return buFaultInfoProduceMapper.listLatestFaultByWorkshopIdAndNum(workshopId, num);
    }

//    /**
//     * @see BuFaultInfoProduceService#getRepairBeforeAndAfterFaultDiffChart(BuFaultTimeEffectQueryVO)
//     */
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    @Override
//    public List<SingleBarChartVO> getRepairBeforeAndAfterFaultDiffChart(BuFaultTimeEffectQueryVO queryVO) throws Exception {
//        // 获取车辆架修周期中最近的时间点
//        List<BuBaseTrainRepairPeriod> periodList = buTrainInfoProduceMapper.selectTrainRepairPeriodListByTrainNo(queryVO.getTrainNo());
//        if (CollectionUtils.isEmpty(periodList)) {
//            throw new JeecgBootException("该车辆无架修周期记录");
//        }
//
//        // 设置时间点，前后12个月
//        Date datePoint;
//        List<Date> endList = periodList.stream()
//                .map(BuBaseTrainRepairPeriod::getEnd)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//        List<Date> startList = periodList.stream()
//                .map(BuBaseTrainRepairPeriod::getStart)
//                .collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(endList)) {
//            datePoint = Collections.max(endList);
//        } else {
//            datePoint = Collections.max(startList);
//        }
//        setTimeBeforeAndAfter12Month(datePoint, queryVO);
//        TreeMap<Integer, String> timeOrderMap = getTimeOrderMap(queryVO);
//
//        // 查询车辆履历故障记录
//        List<BuTrainHistoryFault> trainHistoryFaultList = buTrainHistoryFaultMapper.selectMaximoSimpleListByBuFaultTimeEffectQueryVO(queryVO);
//
//        List<SingleBarChartVO> resultList = new ArrayList<>();
//
//        for (Map.Entry<Integer, String> entry : timeOrderMap.entrySet()) {
//            Integer index = entry.getKey();
//            String yearMonth = entry.getValue();
//            String[] split = yearMonth.split("-");
//            int year = Integer.parseInt(split[0]);
//            int month = Integer.parseInt(split[1]);
//            String mapKey = index + "(" + yearMonth + ")";
//            if (0 == index) {
//                mapKey = "架修点";
//            }
//
//            long count = trainHistoryFaultList.stream()
//                    .filter(fault -> year == (fault.getHappenTime().getYear() + 1900) && month == (fault.getHappenTime().getMonth() + 1))
//                    .count();
//
//            SingleBarChartVO chartVO = new SingleBarChartVO()
//                    .setX(mapKey)
//                    .setY(new Long(count).doubleValue());
//
//            resultList.add(chartVO);
//        }
//
//        return resultList;
//    }

    /**
     * @see BuFaultInfoProduceService#listRepairBeforeAndAfterFaultDiffData(BuFaultTimeEffectQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultTimeEffectResultVO> listRepairBeforeAndAfterFaultDiffData(BuFaultTimeEffectQueryVO queryVO) throws Exception {
        // 默认周期类型=月
        Integer periodType = queryVO.getPeriodType();
        if (null == periodType) {
            periodType = 2;
            queryVO.setPeriodType(periodType);
        }

        // 获取车辆最近的架修周期
        List<BuBaseTrainRepairPeriod> periodList = buTrainInfoProduceMapper.selectTrainRepairPeriodListByTrainNo(queryVO.getTrainNo());
        if (CollectionUtils.isEmpty(periodList)) {
            throw new JeecgBootException("该车辆无架修周期记录");
        }
        BuBaseTrainRepairPeriod lastPeriod = periodList.get(0);

        // 设置时间点
        Date lastPeriodStart = lastPeriod.getStartTime();
        Date lastPeriodEnd = lastPeriod.getEnd();
        Date datePoint;
        if (null != lastPeriodEnd) {
            datePoint = lastPeriodEnd;
        } else {
            datePoint = lastPeriodStart;
        }
        setTimeBeforeAndAfter(datePoint, queryVO);
        TreeMap<Integer, String> timeOrderMap = getTimeOrderMap(queryVO);

        // 查询车辆履历故障记录--来自检修的故障
        List<BuTrainHistoryFault> maximoFaultList = buTrainHistoryFaultMapper.selectMaximoSimpleListByBuFaultTimeEffectQueryVO(queryVO);
        // 查询车辆履历故障记录--来自架大修的故障，时间在最近的架修周期内的
        BuFaultTimeEffectQueryVO queryVOCopy = new BuFaultTimeEffectQueryVO();
        BeanUtils.copyProperties(queryVO, queryVOCopy);
        queryVOCopy.setStartTime(lastPeriodStart)
                .setEndTime(lastPeriodEnd);
        List<BuTrainHistoryFault> jdxFaultList = buTrainHistoryFaultMapper.selectJdxSimpleListByBuFaultTimeEffectQueryVO(queryVOCopy);

        List<BuFaultTimeEffectResultVO> resultList = new ArrayList<>();
        List<Long> countList = initCountList(periodType);
        List<String> periodNameList = initPeriodNameList(periodType);
        BuFaultTimeEffectResultVO totalResult = new BuFaultTimeEffectResultVO()
                .setName("总数")
                .setJdx(0L)
                .setBefore(new ArrayList<>(countList))
                .setAfter(new ArrayList<>(countList))
                .setBeforePeriodName(new ArrayList<>(periodNameList))
                .setAfterPeriodName(new ArrayList<>(periodNameList));


        Map<String, BuTrainAssetTypeBO> idSysBOMap = assetTypeCacheService.mapSys(null);
        for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : idSysBOMap.entrySet()) {
            String sysId = sysEntry.getKey();
            BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
            String sysShortName = sysAssetTypeBO.getShortName();

            List<BuTrainHistoryFault> maximoSysFaultList = maximoFaultList.stream()
                    .filter(maximoFault -> sysId.equals(maximoFault.getSysId()))
                    .collect(Collectors.toList());

            long jdxSysFaultCount = jdxFaultList.stream()
                    .filter(jdxFault -> sysId.equals(jdxFault.getSysId()))
                    .count();

            BuFaultTimeEffectResultVO sysResult = new BuFaultTimeEffectResultVO()
                    .setName(sysShortName)
                    .setJdx(jdxSysFaultCount)
                    .setBefore(new ArrayList<>(countList))
                    .setAfter(new ArrayList<>(countList))
                    .setBeforePeriodName(new ArrayList<>(periodNameList))
                    .setAfterPeriodName(new ArrayList<>(periodNameList));
            for (Map.Entry<Integer, String> entry : timeOrderMap.entrySet()) {
                Integer index = entry.getKey();
                String periodName = entry.getValue();

                int indexAddend = 0;
                long maximoFaultCount = 0;
                if (2 == periodType) {
                    String[] split = periodName.split("-");
                    int year = Integer.parseInt(split[0]);
                    int month = Integer.parseInt(split[1]);

                    maximoFaultCount = maximoSysFaultList.stream()
                            .filter(fault -> year == (fault.getHappenTime().getYear() + 1900) && month == (fault.getHappenTime().getMonth() + 1))
                            .count();

                    indexAddend = 12;
                } else if (1 == periodType) {
                    int year = Integer.parseInt(periodName);

                    maximoFaultCount = maximoSysFaultList.stream()
                            .filter(fault -> year == (fault.getHappenTime().getYear() + 1900))
                            .count();

                    indexAddend = 5;
                }

                if (0 == index) {
                    sysResult.setJdxPeriodName(periodName);

                    Long totalCount = totalResult.getJdx();
                    totalResult.setJdx(totalCount + jdxSysFaultCount);
                    totalResult.setJdxPeriodName(periodName);
                } else if (index < 0) {
                    int listIndex = index + indexAddend;
                    sysResult.getBefore().set(listIndex, maximoFaultCount);
                    sysResult.getBeforePeriodName().set(listIndex, periodName);

                    Long totalCount = totalResult.getBefore().get(listIndex);
                    totalResult.getBefore().set(listIndex, totalCount + maximoFaultCount);
                    totalResult.getBeforePeriodName().set(listIndex, periodName);
                } else {
                    int listIndex = index - 1;
                    sysResult.getAfter().set(listIndex, maximoFaultCount);
                    sysResult.getAfterPeriodName().set(listIndex, periodName);

                    Long totalCount = totalResult.getAfter().get(listIndex);
                    totalResult.getAfter().set(listIndex, totalCount + maximoFaultCount);
                    totalResult.getAfterPeriodName().set(listIndex, periodName);
                }
            }

            resultList.add(sysResult);
        }
        resultList.add(0, totalResult);

        return resultList;
    }


    private String getCompareResultTitle(BuFaultInfoQueryVO query) {
        boolean allEmpty = true;
        // 时间
        String timeTitle = "";
        Integer dateType = query.getDateType();
        if (dateType != null && dateType != 0) {
            allEmpty = false;

            switch (dateType) {
                case 1:
                    timeTitle = query.getYear() + "年";
                    break;
                case 2:
                    timeTitle = query.getYear() + "年" + query.getQuarter() + "季度";
                    break;
                case 3:
                    timeTitle = query.getYear() + "年" + query.getMonth() + "月";
                    break;
                case 4:
                    String start = DateUtils.date_sdf_wz.get().format(query.getStartDate());
                    String end = DateUtils.date_sdf_wz.get().format(query.getEndDate());
                    timeTitle = start + "至" + end;
                    break;
                default:
                    break;
            }
        }
        // 车辆
        String trainNoTitle = "";
        if (StringUtils.isNotBlank(query.getTrainNo())) {
            allEmpty = false;

            trainNoTitle = query.getTrainNo() + "车";
        }
        // 工班
        String groupTitle = "";
        if (StringUtils.isNotBlank(query.getGroupId())) {
            allEmpty = false;

            BuMtrWorkshopGroup group = buMtrWorkshopGroupProduceMapper.selectById(query.getGroupId());
            if (null != group) {
                groupTitle = group.getGroupName();
            }
        }

        if (allEmpty) {
            return "所有故障统计";
        } else {
            return timeTitle + trainNoTitle + groupTitle + "故障统计";
        }
    }

    private List<FaultCountInfoVO> countFaultGroupByPhaseAndSystem(BuFaultInfoQueryVO queryVO) {
        queryVO.toStartEndDate();
        List<BuFaultInfo> faultInfoList = buFaultInfoProduceMapper.selectSimpleListByCondition(queryVO);

        // 获取请求的发现阶段
        List<Integer> requestPhaseList = getRequestPhaseList(queryVO);
        // 大修期故障归到架修期中
        requestPhaseList.removeIf(item -> item == 4);

        // 统计时，大修期故障归到架修期中
        for (BuFaultInfo fault : faultInfoList) {
            if (null != fault.getPhase() && 4 == fault.getPhase()) {
                fault.setPhase(1);
            }
        }
        Map<Integer, List<BuFaultInfo>> phaseFaultInfoListMap = faultInfoList.stream()
                .filter(fault -> null != fault.getPhase())
                .collect(Collectors.groupingBy(BuFaultInfo::getPhase));
        for (Integer requestPhase : requestPhaseList) {
            if (!phaseFaultInfoListMap.containsKey(requestPhase)) {
                phaseFaultInfoListMap.put(requestPhase, new ArrayList<>());
            }
        }

        List<FaultCountInfoVO> countInfoVOList = new ArrayList<>();
        for (Map.Entry<Integer, List<BuFaultInfo>> phaseFaultInfoListEntry : phaseFaultInfoListMap.entrySet()) {
            Integer phase = phaseFaultInfoListEntry.getKey();
            if (!requestPhaseList.contains(phase)) {
                continue;
            }

            List<BuFaultInfo> faultList = phaseFaultInfoListEntry.getValue();
            FaultCountInfoVO countInfoVO = getFaultCountInfoVO(faultList);
            countInfoVO.setPhase(phase);
            countInfoVOList.add(countInfoVO);
        }
        return countInfoVOList;
    }

    private List<Integer> getRequestPhaseList(BuFaultInfoQueryVO queryVO) {
        List<Integer> requestPhaseList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(queryVO.getFaultTypes())) {
            List<Integer> faultTypes = queryVO.getFaultTypes();
            if (faultTypes.contains(1) || faultTypes.contains(2)) {
                requestPhaseList.add(1);
                requestPhaseList.add(4);
            }
            if (faultTypes.contains(3) || faultTypes.contains(4)
                    || faultTypes.contains(5) || faultTypes.contains(6)) {
                requestPhaseList.add(2);
            }
        } else {
            requestPhaseList = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        }

        return requestPhaseList;
    }

    private FaultCountInfoVO getFaultCountInfoVO(List<BuFaultInfo> faultInfoList) {
        Map<String, BuTrainAssetTypeBO> idSysBOMap = assetTypeCacheService.mapSys(null);
        List<FaultCountSystemItemVO> systemItemList = new ArrayList<>();
        for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : idSysBOMap.entrySet()) {
            String sysId = sysEntry.getKey();
            BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
            String sysShortName = sysAssetTypeBO.getShortName();

            long sysFaultCount = faultInfoList.stream()
                    .filter(fault -> sysId.equals(fault.getSysId()))
                    .count();
            FaultCountSystemItemVO systemCountItemVO = new FaultCountSystemItemVO()
                    .setSystemName(sysShortName)
                    .setFaultCount(new Long(sysFaultCount).intValue());

            systemItemList.add(systemCountItemVO);
        }
        systemItemList.sort(Comparator.comparing(FaultCountSystemItemVO::getSystemName, Comparator.nullsLast(Comparator.naturalOrder())));

        return new FaultCountInfoVO()
                .setTotal(faultInfoList.size())
                .setSystemItemList(systemItemList);
    }

    private void setTimeBeforeAndAfter(Date datePoint, BuFaultTimeEffectQueryVO queryVO) {
        Integer periodType = queryVO.getPeriodType();
        int monthAmount = 12;
        int yearAmount = 5;

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(datePoint);
        if (periodType == 2) {
            // 月，取前后12个月
            startCalendar.add(Calendar.MONTH, -monthAmount);
        } else if (periodType == 1) {
            // 年，取前后5年
            startCalendar.add(Calendar.YEAR, -yearAmount);
        }

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(datePoint);
        if (periodType == 2) {
            // 月，取前后12个月
            endCalendar.add(Calendar.MONTH, monthAmount);
        } else if (periodType == 1) {
            // 年，取前后5年
            endCalendar.add(Calendar.YEAR, yearAmount);
        }

        queryVO.setStartTime(DateUtils.transToDayEnd(DateUtils.transToMonthEndDay(startCalendar)).getTime())
                .setEndTime(DateUtils.transToDayStart(DateUtils.transToMonthStartDay(endCalendar)).getTime());
    }

    private TreeMap<Integer, String> getTimeOrderMap(BuFaultTimeEffectQueryVO queryVO) {
        Integer periodType = queryVO.getPeriodType();

        TreeMap<Integer, String> timeOrderMap = new TreeMap<>();

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(queryVO.getStartTime());
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(queryVO.getEndTime());

        if (periodType == 2) {
            // 月，取前后12个月
            AtomicInteger index = new AtomicInteger(-12);
            while (!startCalendar.after(endCalendar)) {
                int year = startCalendar.get(Calendar.YEAR);
                int month = startCalendar.get(Calendar.MONTH) + 1;

                timeOrderMap.put(index.getAndIncrement(), year + "-" + month);

                startCalendar.add(Calendar.MONTH, 1);
            }
        } else if (periodType == 1) {
            // 年，取前后5年
            AtomicInteger index = new AtomicInteger(-5);
            while (!startCalendar.after(endCalendar)) {
                int year = startCalendar.get(Calendar.YEAR);

                timeOrderMap.put(index.getAndIncrement(), String.valueOf(year));

                startCalendar.add(Calendar.YEAR, 1);
            }
        }

        return timeOrderMap;
    }

    private List<Long> initCountList(Integer periodType) {
        int amount = 0;
        if (periodType == 2) {
            // 月，取前后12个月
            amount = 12;
        } else if (periodType == 1) {
            // 年，取前后5年
            amount = 5;
        }

        List<Long> countList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            countList.add(0L);
        }

        return countList;
    }

    private List<String> initPeriodNameList(Integer periodType) {
        int amount = 0;
        if (periodType == 2) {
            // 月，取前后12个月
            amount = 12;
        } else if (periodType == 1) {
            // 年，取前后5年
            amount = 5;
        }

        List<String> periodNameList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            periodNameList.add("");
        }

        return periodNameList;
    }

}
