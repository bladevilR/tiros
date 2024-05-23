package org.jeecg.modules.dispatch.workorder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceOutin;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceOutinDetail;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceRepairRecord;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOutsourceOutinQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOutsourcePerformQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.DelayReasonVO;

import java.util.List;

/**
 * <p>
 * 委外出入库单(交接) 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-10-14
 */
public interface BuOutsourceOutinService extends IService<BuOutsourceOutin> {

    /**
     * 分页查询交接单
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    IPage<BuOutsourceOutin> pageOutIn(BuOutsourceOutinQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 分页查询交接单明细
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    IPage<BuOutsourceOutinDetail> pageOutInDetail(BuOutsourceOutinQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据工单任务id查询委外交接信息
     *
     * @param id 工单任务id
     * @return 委外交接信息
     */
    BuOutsourceOutin selectOutsourceOutinByTaskId(String id) throws Exception;

    /**
     * 保存委外交接信息
     *
     * @param outin 委外交接信息
     * @return 是否成功
     */
    boolean saveOutsourceOutin(BuOutsourceOutin outin) throws Exception;

    /**
     * 删除交接单
     *
     * @param ids 交接单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteOutinBatch(String ids) throws Exception;

    /**
     * 删除交接单明细
     *
     * @param ids 交接单明细ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteOutinDetailBatch(String ids) throws Exception;

    /**
     * 根据车号查询未返厂设备
     *
     * @param trainNo     车号
     * @param assetTypeId 设备类型id
     * @param assetName   设备名称
     * @return 未返厂设备出库明细
     * @throws Exception 异常信息
     */
    List<BuOutsourceOutinDetail> listNotReturnAssetAsOutinDetail(String trainNo, String assetTypeId, String assetName) throws Exception;

    /**
     * 执行管理任务分页
     *
     * @param queryVO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<BuOutsourceRepairRecord> repairRecordPage(BuOutsourcePerformQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * 设置逾期原因
     *
     * @param delayReason
     * @return
     */
    boolean settingDelayReason(DelayReasonVO delayReason);

}
