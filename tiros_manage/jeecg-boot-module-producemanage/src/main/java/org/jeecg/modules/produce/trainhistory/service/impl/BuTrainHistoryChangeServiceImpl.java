package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryChange;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainAssetUseRecordVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryChangeQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.HistoryRecordsQueryVO;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainHistoryChangeMapper;
import org.jeecg.modules.produce.trainhistory.service.BuTrainHistoryChangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆履历-更换记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Service
public class BuTrainHistoryChangeServiceImpl extends ServiceImpl<BuTrainHistoryChangeMapper, BuTrainHistoryChange> implements BuTrainHistoryChangeService {

    @Resource
    private BuTrainHistoryChangeMapper buTrainHistoryChangeMapper;


    /**
     * @see BuTrainHistoryChangeService#pageChangeRecord(BuTrainHistoryChangeQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuTrainHistoryChange> pageChangeRecord(BuTrainHistoryChangeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buTrainHistoryChangeMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuTrainHistoryChangeService#countChangeRecordByTime(HistoryRecordsQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<LineChartVO> countChangeRecordByTime(HistoryRecordsQueryVO queryVO) throws Exception {
        // 查询近两年的更换记录简单信息
        setLastTwoYearTime(queryVO);
        List<BuTrainHistoryChange> changeList = buTrainHistoryChangeMapper.selectSimpleListByCondition(queryVO);

        return getLineChartVOList(changeList);
    }

    /**
     * @see BuTrainHistoryChangeService#countChangeRecordByDistribution(HistoryRecordsQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<PieChartVO> countChangeRecordByDistribution(HistoryRecordsQueryVO queryVO) throws Exception {
        // 查询近两年的更换记录简单信息
        setLastTwoYearTime(queryVO);
        List<BuTrainHistoryChange> changeList = buTrainHistoryChangeMapper.selectSimpleListByCondition(queryVO);

        return getPieChartVOList(changeList, queryVO);
    }

    /**
     * @see BuTrainHistoryChangeService#pageAssetUseRecordVO(String, String, Date, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuTrainAssetUseRecordVO> pageAssetUseRecordVO(String assetId, String trainNo, Date changeDate, Integer pageNo, Integer pageSize) throws Exception {
        BuTrainHistoryChangeQueryVO queryVO = new BuTrainHistoryChangeQueryVO();
        queryVO.setAssetId(assetId);
        queryVO.setTrainNo(trainNo);
        queryVO.setChangeDate(changeDate);

        IPage<BuTrainAssetUseRecordVO> page = buTrainHistoryChangeMapper.selectAssetUseRecordVOPage(new Page<>(pageNo, pageSize), queryVO);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<BuTrainAssetUseRecordVO> useRecordVOList = page.getRecords();
            for (BuTrainAssetUseRecordVO useRecordVO : useRecordVOList) {
                if (assetId.equals(useRecordVO.getDownAssetId())) {
                    useRecordVO.setType("换下");
                } else if (assetId.equals(useRecordVO.getUpAssetId())) {
                    useRecordVO.setType("换上");
                }
            }
        }

        return page;
    }


    private void setLastTwoYearTime(HistoryRecordsQueryVO queryVO) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        int monthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, monthLastDay);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);
        queryVO.setEndTime(calendar.getTime());

        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        queryVO.setStartTime(calendar.getTime());
    }

    private List<LineChartVO> getLineChartVOList(List<BuTrainHistoryChange> changeList) {
        List<LineChartVO> lineChartVOList = new ArrayList<>();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int lastYear = currentYear - 1;

        int startMonth = 1;
        int endMonth = 12;
        for (int i = startMonth; i <= endMonth; i++) {
            int currentMonth = i;
            long currentYearCount = changeList.stream()
                    .filter(change -> null != change.getExchangeTime()
                            && (change.getExchangeTime().getYear() + 1900) == currentYear
                            && (change.getExchangeTime().getMonth() + 1) == currentMonth)
                    .count();
            long lastYearCount = changeList.stream()
                    .filter(change -> null != change.getExchangeTime()
                            && (change.getExchangeTime().getYear() + 1900) == lastYear
                            && (change.getExchangeTime().getMonth() + 1) == currentMonth)
                    .count();

            LineChartVO lineChartVO = new LineChartVO()
                    .setType(String.valueOf(currentMonth))
                    .setJeecg(new Long(currentYearCount).doubleValue())
                    .setJeebt(new Long(lastYearCount).doubleValue());
            lineChartVOList.add(lineChartVO);
        }

        return lineChartVOList;
    }

    private List<PieChartVO> getPieChartVOList(List<BuTrainHistoryChange> changeList, HistoryRecordsQueryVO queryVO) {
        List<PieChartVO> pieChartVOList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(changeList)) {
            // 按系统分
            Map<String, List<BuTrainHistoryChange>> itemListMap = changeList.stream()
                    .filter(change -> StringUtils.isNotBlank(change.getSysName()))
                    .collect(Collectors.groupingBy(BuTrainHistoryChange::getSysName));

            for (Map.Entry<String, List<BuTrainHistoryChange>> itemListMapEntry : itemListMap.entrySet()) {
                String item = itemListMapEntry.getKey();
                List<BuTrainHistoryChange> list = itemListMapEntry.getValue();

                PieChartVO pieChartVO = new PieChartVO()
                        .setItem(item)
                        .setCount(new Integer(list.size()).doubleValue());
                pieChartVOList.add(pieChartVO);
            }
        }

        // 取前10
        pieChartVOList.sort(Comparator.comparing(PieChartVO::getCount, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
        if (pieChartVOList.size() > 10) {
            pieChartVOList = pieChartVOList.subList(0, 10);
        }

        return pieChartVOList;
    }

}
