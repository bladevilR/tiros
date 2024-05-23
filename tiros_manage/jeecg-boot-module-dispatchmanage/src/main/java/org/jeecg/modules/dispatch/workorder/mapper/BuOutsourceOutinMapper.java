package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceOutin;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceOutinDetail;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceRepairRecord;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOutsourceOutinQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOutsourcePerformQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.DelayReasonVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 委外出入库单(交接) Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020 -10-14
 */
public interface BuOutsourceOutinMapper extends BaseMapper<BuOutsourceOutin> {

    /**
     * 分页查询
     *
     * @param page  the page
     * @param outin the outin
     * @return page
     */
    IPage<BuOutsourceOutin> selectOutinPage(Page<Object> page, @Param("outin") BuOutsourceOutinQueryVO outin);

    /**
     * 明细分页查询
     *
     * @param page  the page
     * @param outin the outin
     * @return page
     */
    IPage<BuOutsourceOutinDetail> selectOutinDetailPage(Page<Object> page, @Param("outin") BuOutsourceOutinQueryVO outin);

    /**
     * 执行管理任务分页
     *
     * @param page    the page
     * @param perform the perform
     * @return page
     */
    IPage<BuOutsourceRepairRecord> repairRecordPage(Page<Object> page, @Param("perform") BuOutsourcePerformQueryVO perform);

    /**
     * Sets delay reason.
     *
     * @param delayReason the delay reason
     * @return the delay reason
     */
    int settingDelayReason(@Param("delayReason") DelayReasonVO delayReason);

    /**
     * 根据工单任务id查询委外交接单信息
     *
     * @param orderTaskId 工单任务id
     * @return 委外交接单 list
     */
    List<BuOutsourceOutin> selectListByOrderTaskId(@Param("orderTaskId") String orderTaskId);

    /**
     * Update contract cur trian.
     *
     * @param contractId the contract id
     * @param trainNo    the train no
     * @param billType   the bill type
     */
    void updateContractCurTrain(@Param("contractId")String contractId, @Param("trainNo")String trainNo, @Param("billType")Integer billType);
}
