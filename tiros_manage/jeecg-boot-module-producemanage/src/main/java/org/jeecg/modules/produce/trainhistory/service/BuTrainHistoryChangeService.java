package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryChange;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainAssetUseRecordVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryChangeQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.HistoryRecordsQueryVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆履历-更换记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryChangeService extends IService<BuTrainHistoryChange> {

    /**
     * 根据条件查询车辆履历更换记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 更换记录分页
     * @throws Exception 异常信息
     */
    IPage<BuTrainHistoryChange> pageChangeRecord(BuTrainHistoryChangeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据条件查询更换记录同比分析，
     * 取最近两年数据
     *
     * @param queryVO 查询条件
     * @return 更换记录同比分析
     * @throws Exception 异常信息
     */
    List<LineChartVO> countChangeRecordByTime(HistoryRecordsQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询更换记录分布分析，
     * 按系统分
     *
     * @param queryVO 查询条件
     * @return 更换记录同比分析
     * @throws Exception 异常信息
     */
    List<PieChartVO> countChangeRecordByDistribution(HistoryRecordsQueryVO queryVO) throws Exception;

    /**
     * 查询车辆设备履历使用记录(更换记录)
     *
     * @param assetId    车辆设备id
     * @param trainNo    车号
     * @param changeDate 更换时间
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 车辆设备使用记录
     * @throws Exception 异常信息
     */
    IPage<BuTrainAssetUseRecordVO> pageAssetUseRecordVO(String assetId, String trainNo, Date changeDate, Integer pageNo, Integer pageSize) throws Exception;

}
