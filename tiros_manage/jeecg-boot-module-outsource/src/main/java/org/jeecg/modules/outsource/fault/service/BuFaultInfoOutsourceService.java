package org.jeecg.modules.outsource.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.outsource.fault.bean.BuFaultInfo;
import org.jeecg.modules.outsource.fault.bean.vo.BuFaultInfoCompareQueryVO;
import org.jeecg.modules.outsource.fault.bean.vo.BuFaultInfoCompareResultVO;
import org.jeecg.modules.outsource.fault.bean.vo.BuFaultInfoQueryVO;
import org.jeecg.modules.outsource.fault.bean.vo.FaultCountInfoVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-19
 */
public interface BuFaultInfoOutsourceService extends IService<BuFaultInfo> {

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
     * 根据id查故障信息，处理记录返回最近的一条
     *
     * @param id 故障id
     * @return 故障信息
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

}
