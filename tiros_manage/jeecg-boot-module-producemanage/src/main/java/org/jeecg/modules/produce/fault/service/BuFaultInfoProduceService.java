package org.jeecg.modules.produce.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.SingleBarChartVO;
import org.jeecg.modules.produce.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;
import org.jeecg.modules.produce.fault.bean.vo.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 故障信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultInfoProduceService extends IService<BuFaultInfo> {

    /**
     * 根据条件分页查询故障信息
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuFaultInfo> page(BuFaultInfoQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询故障详细信息
     *
     * @param id 故障id
     * @return 故障详细信息
     * @throws Exception 异常信息
     */
    BuFaultInfo getFaultById(String id) throws Exception;

    /**
     * 根据id查故障处理记录
     *
     * @param id 故障id
     * @return 故障处理记录
     * @throws Exception 异常信息
     */
    List<BuFaultHandleRecord> listHandleRecordById(String id) throws Exception;

    /**
     * 根据条件统计故障处理数量
     *
     * @param queryVO 查询条件
     * @return 故障处理数量
     * @throws Exception 异常信息
     */
    FaultCountInfoVO countFault(BuFaultInfoQueryVO queryVO) throws Exception;

    /**
     * 根据条件统计故障各系统分布数量
     *
     * @param queryVO 查询条件
     * @return 各系统分布数量
     * @throws Exception 异常信息
     */
    FaultCountInfoVO countFaultGroupBySystem(BuFaultInfoQueryVO queryVO) throws Exception;

    /**
     * 根据条件比较分析故障各阶段时各系统分布数量
     *
     * @param compareQueryVO 比较分析查询条件
     * @return 各阶段时各系统分布数量
     * @throws Exception 异常信息
     */
    BuFaultInfoCompareResultVO compareCountFaultGroupByPhaseAndSystem(BuFaultInfoCompareQueryVO compareQueryVO) throws Exception;

    /**
     * 获取车间最新故障信息
     *
     * @param workshopId 车间id
     * @param num        故障条数
     * @return 最新故障信息结果
     * @throws Exception 异常信息
     */
    List<BuFaultInfo> listLatestFault(String workshopId, Integer num) throws Exception;

//    /**
//     * 实效分析-对比图形，
//     * 查询架修点前后故障数量对比
//     *
//     * @param queryVO 查询条件
//     * @return 对比图形数据
//     * @throws Exception 异常信息
//     */
//    List<SingleBarChartVO> getRepairBeforeAndAfterFaultDiffChart(BuFaultTimeEffectQueryVO queryVO) throws Exception;

    /**
     * 实效分析-表格数据，按系统分
     * 查询架修点前后故障数量对比
     *
     * @param queryVO 查询条件
     * @return 表格数据
     * @throws Exception 异常信息
     */
    List<BuFaultTimeEffectResultVO> listRepairBeforeAndAfterFaultDiffData(BuFaultTimeEffectQueryVO queryVO) throws Exception;

}
