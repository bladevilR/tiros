package org.jeecg.modules.group.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.group.fault.bean.BuFaultInfo;
import org.jeecg.modules.group.fault.bean.vo.BuFaultInfoQueryVO;

import java.util.List;

/**
 * <p>
 * 故障信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/12
 */
public interface BuFaultInfoGroupService extends IService<BuFaultInfo> {

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
     * 保存故障信息
     *
     * @param buFaultInfo 故障信息
     * @return 是否保存成功
     * @throws Exception 异常信息
     */
    boolean saveAndSubmitFaultInfo(BuFaultInfo buFaultInfo) throws Exception;

    /**
     * 修改故障信息
     *
     * @param buFaultInfo 故障信息
     * @return 是否修改成功
     * @throws Exception 异常信息
     */
    boolean updateFaultInfo(BuFaultInfo buFaultInfo) throws Exception;

    /**
     * 批量删除故障信息
     *
     * @param ids 故障信息记录ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 批量取消故障信息
     *
     * @param ids 故障信息记录ids 多个逗号分隔
     * @return 是否取消成功
     * @throws Exception 异常信息
     */
    boolean cancelBatch(String ids) throws Exception;

    /**
     * 提交故障
     *
     * @param ids 故障信息记录ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean submitFault(String ids) throws Exception;

    /**
     * 新增故障
     * @param buFaultInfo
     * @return
     */
    boolean saveFaultInfo(BuFaultInfo buFaultInfo) throws Exception;
}
