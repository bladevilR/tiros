package org.jeecg.modules.board.workstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.progress.bean.BuWorkOrderTask;
import org.jeecg.modules.board.quality.bean.BuFaultInfo;
import org.jeecg.modules.board.workstation.bean.BuMtrWorkstation;
import org.jeecg.modules.board.workstation.bean.BuRepairTaskStaffRequire;
import org.jeecg.modules.board.workstation.bean.vo.BuWorkstationDataQueryVO;
import org.jeecg.modules.board.workstation.bean.vo.BuWorkstationQueryVO;
import org.jeecg.modules.board.workstation.bean.vo.BuWorkstationVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工位信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-18
 */
public interface BuMtrWorkstationBoardMapper extends BaseMapper<BuMtrWorkstation> {

    /**
     * 根据条件查询工位列表
     *
     * @param queryVO 查询条件
     * @return 工位列表
     */
    List<BuWorkstationVO> selectWorkstationVOListByCondition(@Param("queryVO") BuWorkstationQueryVO queryVO);

    /**
     * 根据工位id和工班id查询工位信息
     *
     * @param workstationId 工位id
     * @param groupId       工班id
     * @return 工位信息
     */
    BuWorkstationVO selectWorkstationVOById(@Param("workstationId") String workstationId, @Param("groupId") String groupId);

    /**
     * 根据工班id和工位id查询工位任务(当天)
     *
     * @param queryVO 工班id+工位id
     * @return 工位任务
     */
    List<BuWorkOrderTask> selectTodayTaskListByGroupIdAndWorkstationId(@Param("queryVO") BuWorkstationDataQueryVO queryVO);

    /**
     * 根据工班id和工位id查询工位逾期任务(当天之前)
     *
     * @param queryVO 工班id+工位id
     * @return 工位逾期任务
     */
    List<BuWorkOrderTask> selectTodayDelayTaskListByGroupIdAndWorkstationId(@Param("queryVO") BuWorkstationDataQueryVO queryVO);

    /**
     * 根据工班id和工位id查询工位工单物料必换件(当天)
     *
     * @param queryVO 工班id+工位id
     * @return 工位工单物料必换件
     */
    List<Map<String, Object>> selectTodayMustMaterialListByGroupIdAndWorkstationId(@Param("queryVO") BuWorkstationDataQueryVO queryVO);

    /**
     * 根据工班id和工位id查询工位故障信息(当天)
     * 1.故障提报班组=工班id
     * 3.故障提报时间在当天
     *
     * @param queryVO 工班id+工位id+当天开始和结束时间
     * @return 工位故障信息(当天)
     */
    List<BuFaultInfo> selectTodayFaultListByByGroupIdAndWorkstationId(@Param("queryVO") BuWorkstationDataQueryVO queryVO);

    /**
     * 根据工班id和工位id查询工位任务人员需求(当天)
     *
     * @param queryVO 工班id+工位id+当天开始和结束时间
     * @return 工位任务人员需求(当天)
     */
    List<BuRepairTaskStaffRequire> selectTodayStaffRequireListByGroupIdAndWorkstationId(@Param("queryVO") BuWorkstationDataQueryVO queryVO);

    /**
     * 根据工班id和工位id统计查询工位任务人员安排数量(当天)
     *
     * @param queryVO 工班id+工位id+当天开始和结束时间
     * @return 工位任务人员安排数量(当天)
     */
    Integer countTodayStaffArrangeByGroupIdAndWorkstationId(@Param("queryVO") BuWorkstationDataQueryVO queryVO);

}
