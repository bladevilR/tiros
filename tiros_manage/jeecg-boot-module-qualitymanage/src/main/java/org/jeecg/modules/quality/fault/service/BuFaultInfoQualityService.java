package org.jeecg.modules.quality.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.quality.fault.bean.BuFaultInfo;
import org.jeecg.modules.quality.fault.bean.vo.BuFaultInfoQueryVO;

import java.util.List;

/**
 * <p>
 * 故障信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultInfoQualityService extends IService<BuFaultInfo> {

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

}
