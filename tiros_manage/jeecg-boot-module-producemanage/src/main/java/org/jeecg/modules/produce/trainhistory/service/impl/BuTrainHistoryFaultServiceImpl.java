package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryFault;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryFaultQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.HistoryRecordsQueryVO;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainHistoryFaultMapper;
import org.jeecg.modules.produce.trainhistory.service.BuTrainHistoryFaultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆履历-故障记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Service
public class BuTrainHistoryFaultServiceImpl extends ServiceImpl<BuTrainHistoryFaultMapper, BuTrainHistoryFault> implements BuTrainHistoryFaultService {

    @Resource
    private BuTrainHistoryFaultMapper buTrainHistoryFaultMapper;


    /**
     * @see BuTrainHistoryFaultService#pageFaultRecord(BuTrainHistoryFaultQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuTrainHistoryFault> pageFaultRecord(BuTrainHistoryFaultQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buTrainHistoryFaultMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuTrainHistoryFaultService#countFaultRecordByTime(HistoryRecordsQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<LineChartVO> countFaultRecordByTime(HistoryRecordsQueryVO queryVO) throws Exception {
        // 查询近两年的故障记录简单信息
        setLastTwoYearTime(queryVO);
        List<BuTrainHistoryFault> faultList = buTrainHistoryFaultMapper.selectSimpleListByCondition(queryVO);

        return getLineChartVOList(faultList);
    }

    /**
     * @see BuTrainHistoryFaultService#countFaultRecordByTime(HistoryRecordsQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<PieChartVO> countFaultRecordByDistribution(HistoryRecordsQueryVO queryVO) throws Exception {
        // 查询近两年的故障记录简单信息
        setLastTwoYearTime(queryVO);
        List<BuTrainHistoryFault> faultList = buTrainHistoryFaultMapper.selectSimpleListByCondition(queryVO);

        return getPieChartVOList(faultList, queryVO);
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

    private List<LineChartVO> getLineChartVOList(List<BuTrainHistoryFault> faultList) {
        List<LineChartVO> lineChartVOList = new ArrayList<>();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int lastYear = currentYear - 1;

        int startMonth = 1;
        int endMonth = 12;
        for (int i = startMonth; i <= endMonth; i++) {
            int currentMonth = i;
            long currentYearCount = faultList.stream()
                    .filter(fault -> null != fault.getHappenTime()
                            && (fault.getHappenTime().getYear() + 1900) == currentYear
                            && (fault.getHappenTime().getMonth() + 1) == currentMonth)
                    .count();
            long lastYearCount = faultList.stream()
                    .filter(fault -> null != fault.getHappenTime()
                            && (fault.getHappenTime().getYear() + 1900) == lastYear
                            && (fault.getHappenTime().getMonth() + 1) == currentMonth)
                    .count();

            LineChartVO lineChartVO = new LineChartVO()
                    .setType(String.valueOf(currentMonth))
                    .setJeecg(new Long(currentYearCount).doubleValue())
                    .setJeebt(new Long(lastYearCount).doubleValue());
            lineChartVOList.add(lineChartVO);
        }

        return lineChartVOList;
    }

    private List<PieChartVO> getPieChartVOList(List<BuTrainHistoryFault> faultList, HistoryRecordsQueryVO queryVO) {
        List<PieChartVO> pieChartVOList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(faultList)) {
            Map<String, List<BuTrainHistoryFault>> itemListMap = null;
            if (StringUtils.isBlank(queryVO.getAssetId())) {
                // 选择车辆或车厢：按系统分
                itemListMap = faultList.stream()
                        .filter(fault -> StringUtils.isNotBlank(fault.getSysName()))
                        .collect(Collectors.groupingBy(BuTrainHistoryFault::getSysName));
            } else {
                // 选择具体设备：按故障编码分
                itemListMap = faultList.stream()
                        .filter(fault -> StringUtils.isNotBlank(fault.getFaultCodeName()))
                        .collect(Collectors.groupingBy(BuTrainHistoryFault::getFaultCodeName));
            }
            for (Map.Entry<String, List<BuTrainHistoryFault>> itemListMapEntry : itemListMap.entrySet()) {
                String item = itemListMapEntry.getKey();
                List<BuTrainHistoryFault> list = itemListMapEntry.getValue();

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
