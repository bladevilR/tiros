package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.produce.trainhistory.bean.*;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainPeriodTimeLine;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuWorkLeaveRecordQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuWorkRectifyQueryVO;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainInfoProduceMapper;
import org.jeecg.modules.produce.trainhistory.service.BuTrainInfoProduceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 车辆信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
@Slf4j
@Service
public class BuTrainInfoProduceServiceImpl extends ServiceImpl<BuTrainInfoProduceMapper, BuTrainInfo> implements BuTrainInfoProduceService {

    @Resource
    private BuTrainInfoProduceMapper buTrainInfoProduceMapper;


    /**
     * @see BuTrainInfoProduceService#getTrainInfoByTrainNo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuTrainInfo getTrainInfoByTrainNo(String trainNo) throws Exception {
        return buTrainInfoProduceMapper.selectTrainInfoByTrainNo(trainNo);
    }

    /**
     * @see BuTrainInfoProduceService#listTrainHasTrack(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainInfo> listTrainHasTrack(String lineId) throws Exception {
        return buTrainInfoProduceMapper.selectListHasTrack(lineId);
    }

    /**
     * @see BuTrainInfoProduceService#listTrainPeriodTimeLine(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainPeriodTimeLine> listTrainPeriodTimeLine(String trainNo) throws Exception {
        List<BuBaseTrainRepairPeriod> periodList = buTrainInfoProduceMapper.selectTrainRepairPeriodListByTrainNo(trainNo);
        return transToTrainPeriodTimeLineList(periodList, trainNo);
    }

    @Override
    public IPage<BuWorkLeaveRecord> pageLeaveRecord(BuWorkLeaveRecordQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return buTrainInfoProduceMapper.pageLeaveRecord(new Page<>(pageNo, pageSize), queryVO);
    }

    @Override
    public IPage<BuWorkRectify> pageRectify(BuWorkRectifyQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return buTrainInfoProduceMapper.pageRectify(new Page<>(pageNo, pageSize), queryVO);
    }


    private List<BuTrainPeriodTimeLine> transToTrainPeriodTimeLineList(List<BuBaseTrainRepairPeriod> periodList, String trainNo) {
        if (CollectionUtils.isEmpty(periodList)) {
            return new ArrayList<>();
        }

        List<BuTrainPeriodTimeLine> periodTimeLineList = new ArrayList<>();
        for (BuBaseTrainRepairPeriod period : periodList) {
            Date startTime = period.getStartTime();
            Date endTime = period.getEnd();

            if (null != startTime) {
                boolean dateExist = checkDateExistInPeriodTimeLineList(periodTimeLineList, startTime);
                if (!dateExist) {
                    BuTrainPeriodTimeLine startPeriodTimeLine = new BuTrainPeriodTimeLine()
                            .setTrainNo(trainNo)
                            .setDate(startTime)
                            .setDescription(period.getRepairProgramName() + "（开始）")
                            .setIsCurrent(false);
                    periodTimeLineList.add(startPeriodTimeLine);
                }
            }
            if (null != endTime) {
                boolean dateExist = checkDateExistInPeriodTimeLineList(periodTimeLineList, endTime);
                if (!dateExist) {
                    BuTrainPeriodTimeLine endPeriodTimeLine = new BuTrainPeriodTimeLine()
                            .setTrainNo(trainNo)
                            .setDate(endTime)
                            .setDescription(period.getRepairProgramName() + "（结束）")
                            .setIsCurrent(false);
                    periodTimeLineList.add(endPeriodTimeLine);
                }
            }
        }
        Date now = new Date();
        boolean dateExist = checkDateExistInPeriodTimeLineList(periodTimeLineList, now);
        if (!dateExist) {
            BuTrainPeriodTimeLine nowPeriodTimeLine = new BuTrainPeriodTimeLine()
                    .setTrainNo(trainNo)
                    .setDate(new Date())
                    .setDescription("（当前时间）")
                    .setIsCurrent(true);
            periodTimeLineList.add(nowPeriodTimeLine);
        }

        periodTimeLineList.sort(Comparator.comparing(BuTrainPeriodTimeLine::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
        return periodTimeLineList;
    }

    private boolean checkDateExistInPeriodTimeLineList(List<BuTrainPeriodTimeLine> periodTimeLineList, Date date) {
        for (BuTrainPeriodTimeLine periodTimeLine : periodTimeLineList) {
            boolean sameDay = DateUtils.isSameDay(periodTimeLine.getDate(), date);
            if (sameDay) {
                return true;
            }
        }
        return false;
    }

}
