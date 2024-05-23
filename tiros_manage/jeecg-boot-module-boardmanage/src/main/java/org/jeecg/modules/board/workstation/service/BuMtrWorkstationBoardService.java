package org.jeecg.modules.board.workstation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.board.workstation.bean.BuMtrWorkstation;
import org.jeecg.modules.board.workstation.bean.vo.*;

import java.util.List;

/**
 * <p>
 * 工位信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-18
 */
public interface BuMtrWorkstationBoardService extends IService<BuMtrWorkstation> {

    /**
     * 根据条件查询工位作业数据(当天)
     *
     * @param queryVO 查询条件
     * @return 工位作业数据(当天)
     * @throws Exception 异常信息
     */
    List<BuWorkstationVO> listWorkstation(BuWorkstationQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询工位全部信息
     *
     * @param queryVO 查询条件(工班id+工位id)
     * @return 工位全部信息 任务+必换件+故障+作业人数
     * @throws Exception 异常信息
     */
    BuWorkstationVO getWorkstationAllData(BuWorkstationDataQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询工位任务信息
     *
     * @param queryVO 查询条件(工班id+工位id)
     * @return 工位任务信息
     * @throws Exception 异常信息
     */
    BuWorkstationTaskVO getWorkstationTaskData(BuWorkstationDataQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询工位必换件信息
     *
     * @param queryVO 查询条件(工班id+工位id)
     * @return 工位必换件信息
     * @throws Exception 异常信息
     */
    BuWorkstationMustReplaceVO getWorkstationMustReplaceData(BuWorkstationDataQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询工位故障信息
     *
     * @param queryVO 查询条件(工班id+工位id)
     * @return 工位故障信息
     * @throws Exception 异常信息
     */
    BuWorkstationFaultVO getWorkstationFaultData(BuWorkstationDataQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询工位作业人数信息
     *
     * @param queryVO 查询条件(工班id+工位id)
     * @return 工位作业人数信息
     * @throws Exception 异常信息
     */
    BuWorkstationWorkerVO getWorkstationWorkerData(BuWorkstationDataQueryVO queryVO) throws Exception;

}
