package org.jeecg.modules.board.quality.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.bean.vo.chart.SingleBarChartVO;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.board.quality.bean.BuFaultInfo;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;
import org.jeecg.modules.board.quality.bean.vo.BuGroupFaultRankingVO;
import org.jeecg.modules.board.quality.bean.vo.BuLatestFaultItemVO;
import org.jeecg.modules.board.quality.bean.vo.BuLatestFaultVO;
import org.jeecg.modules.board.quality.mapper.BuFaultInfoBoardMapper;
import org.jeecg.modules.board.quality.service.BuFaultInfoBoardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 故障信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-17
 */
@Slf4j
@Service
public class BuFaultInfoBoardServiceImpl extends ServiceImpl<BuFaultInfoBoardMapper, BuFaultInfo> implements BuFaultInfoBoardService {

    @Resource
    private BuFaultInfoBoardMapper buFaultInfoBoardMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see BuFaultInfoBoardService#getLatestFaultInfo(BuBoardQualityQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuLatestFaultVO getLatestFaultInfo(BuBoardQualityQueryVO queryVO) throws Exception {
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }
        // 设置故障来源
        queryVO.setFromType(1);

        List<BuFaultInfo> faultInfoList = buFaultInfoBoardMapper.selectListByCondition(queryVO);

        Integer faultQuantity = queryVO.getFaultQuantity();
        if (null == faultQuantity || 0 == faultQuantity) {
            faultQuantity = 4;
        }

        return transformToLatestFaultVO(faultInfoList, faultQuantity);
    }

    /**
     * @see BuFaultInfoBoardService#getGroupFaultRanking(BuBoardQualityQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuGroupFaultRankingVO> getGroupFaultRanking(BuBoardQualityQueryVO queryVO) throws Exception {
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置上月到本月时间
            queryVO.setLatestTwoMonthQueryDate();
        }
        // 设置故障来源
        queryVO.setFromType(1);

        List<BuFaultInfo> faultInfoList = buFaultInfoBoardMapper.selectListByCondition(queryVO);

        List<BuGroupFaultRankingVO> groupFaultRankingVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

            Map<String, List<BuFaultInfo>> groupNameFaultListMap = faultInfoList.stream()
                    .filter(fault -> StringUtils.isNotBlank(fault.getReportGroupName()))
                    .collect(Collectors.groupingBy(BuFaultInfo::getReportGroupName));
            for (Map.Entry<String, List<BuFaultInfo>> groupNameFaultListEntry : groupNameFaultListMap.entrySet()) {
                String groupName = groupNameFaultListEntry.getKey();
                List<BuFaultInfo> faultList = groupNameFaultListEntry.getValue();

                long currentMonthCount = faultList.stream()
                        .filter(fault -> currentMonth == fault.getReportTime().getMonth() + 1)
                        .count();
                long latestMonthCount = faultList.stream()
                        .filter(fault -> currentMonth - 1 == fault.getReportTime().getMonth() + 1)
                        .count();

                groupFaultRankingVOList.add(
                        new BuGroupFaultRankingVO()
                                .setGroupName(groupName)
                                .setLatestMonth(new Long(latestMonthCount).intValue())
                                .setCurrentMonth(new Long(currentMonthCount).intValue())
                                .setIncrease(new Long(currentMonthCount - latestMonthCount).intValue())
                );
            }

            AtomicInteger index = new AtomicInteger(1);
            groupFaultRankingVOList.sort(Comparator.comparing(BuGroupFaultRankingVO::getIncrease).reversed());
            groupFaultRankingVOList = groupFaultRankingVOList.stream()
                    .map(ranking -> ranking.setOrder(index.getAndIncrement()))
                    .collect(Collectors.toList());
        }

        Integer rankingQuantity = queryVO.getRankingQuantity();
        if (null == rankingQuantity || 0 == rankingQuantity) {
            rankingQuantity = 5;
        }

        if (groupFaultRankingVOList.size() > rankingQuantity) {
            return groupFaultRankingVOList.subList(0, rankingQuantity);
        } else {
            return groupFaultRankingVOList;
        }
    }

    /**
     * @see BuFaultInfoBoardService#getLatestOutsourceFaultInfo(BuBoardQualityQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuLatestFaultVO getLatestOutsourceFaultInfo(BuBoardQualityQueryVO queryVO) throws Exception {
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }
        // 设置故障来源
        queryVO.setFromType(1);

        List<BuFaultInfo> faultInfoList = buFaultInfoBoardMapper.selectOutsourceListByCondition(queryVO);

        Integer faultQuantity = queryVO.getFaultQuantity();
        if (null == faultQuantity || 0 == faultQuantity) {
            faultQuantity = 4;
        }

        return transformToLatestFaultVO(faultInfoList, faultQuantity);
    }

    /**
     * @see BuFaultInfoBoardService#getFaultSystemCount(BuBoardQualityQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<PieChartVO> getFaultSystemCount(BuBoardQualityQueryVO queryVO) throws Exception {
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }

        List<BuFaultInfo> faultInfoList = buFaultInfoBoardMapper.selectListByCondition(queryVO);

        return transformToPieChartVOList(faultInfoList);
    }

    /**
     * @see BuFaultInfoBoardService#getFaultSystemCount(BuBoardQualityQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<SingleBarChartVO> getDayFaultTrend(BuBoardQualityQueryVO queryVO) throws Exception {
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }

        List<BuFaultInfo> faultInfoList = buFaultInfoBoardMapper.selectListByCondition(queryVO);

        return transformToSingleBarChartVOList(faultInfoList, queryVO.getStartDate(), queryVO.getEndDate());
    }

    /**
     * @see BuFaultInfoBoardService#listLatestFault(Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultInfo> listLatestFault(Integer num) throws Exception {
        if (null == num) {
            num = 8;
        }
        return buFaultInfoBoardMapper.listLatestFaultByNum(num);
    }

    /**
     * @see BuFaultInfoBoardService#getFaultInfoById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuFaultInfo getFaultInfoById(String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoBoardMapper.selectById(id);

        Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

        if (null != faultInfo) {
            faultInfo.setReportUserName(userIdNameMap.get(faultInfo.getReportUserId()));
            faultInfo.setDutyEngineerName(userIdNameMap.get(faultInfo.getDutyEngineer()));
            faultInfo.setCloseUserName(userIdNameMap.get(faultInfo.getCloseUserId()));
        }

        return faultInfo;
    }


    private BuLatestFaultVO transformToLatestFaultVO(List<BuFaultInfo> faultInfoList, int resultSize) {
        BuLatestFaultVO latestFaultVO = new BuLatestFaultVO()
                .setTodayAdded(0)
                .setTotal(0)
                .setResolved(0)
                .setUnresolved(0)
                .setLatestFaults(new ArrayList<>(0));

        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            long todayAdded = faultInfoList.stream()
                    .filter(fault -> DateUtils.isSameDay(fault.getReportTime(), new Date()))
                    .count();
            long total = faultInfoList.size();
            long unresolved = faultInfoList.stream()
                    .filter(fault -> 0 == fault.getStatus())
                    .count();
            long resolved = total - unresolved;

            latestFaultVO
                    .setTodayAdded(new Long(todayAdded).intValue())
                    .setTotal(new Long(total).intValue())
                    .setResolved(new Long(resolved).intValue())
                    .setUnresolved(new Long(unresolved).intValue())
                    .setLatestFaults(getLatestFaultItemVOList(faultInfoList, resultSize));
        }

        return latestFaultVO;
    }

    private List<BuLatestFaultItemVO> getLatestFaultItemVOList(List<BuFaultInfo> faultInfoList, int resultSize) {
        List<BuLatestFaultItemVO> faultInfoVOList = new ArrayList<>(resultSize);

        AtomicInteger index = new AtomicInteger(1);
        for (BuFaultInfo faultInfo : faultInfoList) {
            if (faultInfoVOList.size() < resultSize) {
                faultInfoVOList.add(
                        new BuLatestFaultItemVO()
                                .setOrder(index.getAndIncrement())
                                .setFaultDesc(faultInfo.getFaultDesc())
                                .setReportTime(faultInfo.getReportTime())
                                .setWorkstationName(faultInfo.getWorkstationName())
                                .setAssetName(faultInfo.getFaultAssetName())
                                .setId(faultInfo.getId())
                );
            }
        }

        return faultInfoVOList;
    }

    private List<PieChartVO> transformToPieChartVOList(List<BuFaultInfo> faultInfoList) {
        List<PieChartVO> pieChartVOList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(null);
            for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
                String sysId = sysEntry.getKey();
                BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
                String sysShortName = sysAssetTypeBO.getShortName();

                long sysFaultCount = faultInfoList.stream()
                        .filter(fault -> sysId.equals(fault.getSysId()))
                        .count();
                pieChartVOList.add(
                        new PieChartVO()
                                .setItem(sysShortName)
                                .setCount(new Long(sysFaultCount).doubleValue())
                );
            }
        }

        pieChartVOList.sort(Comparator.comparing(PieChartVO::getCount).reversed()
                .thenComparing(PieChartVO::getItem));
        return pieChartVOList;
    }

    private List<SingleBarChartVO> transformToSingleBarChartVOList(List<BuFaultInfo> faultInfoList, Date startDate, Date endDate) {
        List<SingleBarChartVO> singleBarChartVOList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            Map<String, Long> reportDayCountMap = faultInfoList.stream()
                    .collect(Collectors.groupingBy(fault -> DateUtils.date_sdf.get().format(fault.getReportTime()), Collectors.counting()));

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            while (startCalendar.before(endCalendar) || startCalendar.equals(endCalendar)) {
                String day = DateUtils.date_sdf.get().format(startCalendar.getTime());
                long dayFaultCount = null == reportDayCountMap.get(day) ? 0L : reportDayCountMap.get(day);

                singleBarChartVOList.add(
                        new SingleBarChartVO()
                                .setX(day)
                                .setY((double) dayFaultCount)
                );

                startCalendar.add(Calendar.DATE, 1);
            }
        }

        return singleBarChartVOList;
    }

}
