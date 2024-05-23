package org.jeecg.modules.report.turnover.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.report.turnover.bean.BuTrainHistoryChange;
import org.jeecg.modules.report.turnover.bean.vo.TurnoverDetailQueryVO;
import org.jeecg.modules.report.turnover.bean.vo.TurnoverDetailResultVO;
import org.jeecg.modules.report.turnover.mapper.BuTrainHistoryChangeReportMapper;
import org.jeecg.modules.report.turnover.service.BuTrainHistoryChangeReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * 车辆履历-更换记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
@Service
public class BuTrainHistoryChangeReportServiceImpl extends ServiceImpl<BuTrainHistoryChangeReportMapper, BuTrainHistoryChange> implements BuTrainHistoryChangeReportService {

    @Resource
    private BuTrainHistoryChangeReportMapper buTrainHistoryChangeReportMapper;


    /**
     * @see BuTrainHistoryChangeReportService#pageTurnoverDetail(TurnoverDetailQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<TurnoverDetailResultVO> pageTurnoverDetail(TurnoverDetailQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        queryVO.toStartEndDate();
        // 如果传入的时间为空，去查最后一次接车的时间，查这个时间到当前时间的记录
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            String trainNo = queryVO.getTrainNo();
            Date lastReceiveTime = buTrainHistoryChangeReportMapper.selectTrainLastReceiveTime(trainNo);
            queryVO.setStartDate(lastReceiveTime)
                    .setEndDate(new Date());
        }

        IPage<BuTrainHistoryChange> changePage = buTrainHistoryChangeReportMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        IPage<TurnoverDetailResultVO> resultPage = new Page<>();
        resultPage.setPages(changePage.getPages())
                .setCurrent(changePage.getCurrent())
                .setRecords(transformToTurnoverDetailResultVOList(changePage.getRecords(), changePage.offset()))
                .setSize(changePage.getSize())
                .setTotal(changePage.getTotal());

        return resultPage;
    }


    private List<TurnoverDetailResultVO> transformToTurnoverDetailResultVOList(List<BuTrainHistoryChange> changeList, long currentIndex) {
        List<TurnoverDetailResultVO> resultList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(changeList)) {
            AtomicLong index = new AtomicLong(currentIndex);
            for (BuTrainHistoryChange change : changeList) {
                TurnoverDetailResultVO resultVO = new TurnoverDetailResultVO()
                        .setIndex(index.getAndIncrement())
                        .setId(change.getId())
                        .setAssetName(change.getTrainAssetName())
                        .setSysName(change.getSysName())
                        .setDownAssetSn(change.getDownAssetSn())
                        .setDownAssetLocation("")
                        .setDownAssetDutyUser(change.getDutyUser())
                        .setDownAssetTime(change.getExchangeTime())
                        .setUpAssetSn(change.getUpAssetSn())
                        .setUpAssetTrainNo("")
                        .setUpAssetLocation("")
                        .setUpAssetDutyUser(change.getDutyUser())
                        .setUpAssetTime(change.getExchangeTime())
                        .setRemark("");

                resultList.add(resultVO);
            }
        }

        return resultList;
    }

}
