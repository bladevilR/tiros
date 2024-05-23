package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryFault;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryFaultQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.HistoryRecordsQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆履历-故障记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryFaultService extends IService<BuTrainHistoryFault> {

    /**
     * 根据条件查询车辆履历故障记录
     *
     * @param queryVO 查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 故障记录分页
     * @throws Exception 异常信息
     */
    IPage<BuTrainHistoryFault> pageFaultRecord(BuTrainHistoryFaultQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据条件查询故障记录同比分析，取最近两年数据
     *
     * @param queryVO 查询条件
     * @return 故障记录同比分析
     * @throws Exception 异常信息
     */
    List<LineChartVO> countFaultRecordByTime(HistoryRecordsQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询故障记录分布分析，
     * 如果选择车辆或车厢：按系统分（取前10）
     * 如果选择具体设备：按故障编码分（取前10）
     *
     * @param queryVO 查询条件
     * @return 故障记录同比分析
     * @throws Exception 异常信息
     */
    List<PieChartVO> countFaultRecordByDistribution(HistoryRecordsQueryVO queryVO) throws Exception;

}
