package org.jeecg.modules.produce.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;
import org.jeecg.modules.produce.fault.bean.vo.BuFaultInfoQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 故障信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultInfoProduceMapper extends BaseMapper<BuFaultInfo> {

    /**
     * 根据条件分页查询故障信息（调度只能看到已提交的故障）
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuFaultInfo> selectPageByCondition(IPage<BuFaultInfo> page, @Param("queryVO") BuFaultInfoQueryVO queryVO);

    /**
     * 根据条件查询故障信息列表--仅查询id+status，用于count
     *
     * @param queryVO 查询条件
     * @return 分页结果
     */
    List<BuFaultInfo> selectIdStatusListForCountByCondition(@Param("queryVO") BuFaultInfoQueryVO queryVO);

    /**
     * 根据id查询故障详细信息
     *
     * @param id 故障id
     * @return 故障详细信息
     */
    BuFaultInfo selectFaultById(@Param("id") String id);

    /**
     * 根据故障id查询故障处理记录
     *
     * @param id 故障id
     * @return 故障处理记录
     */
    List<BuFaultHandleRecord> selectHandleRecordListByFaultId(@Param("id") String id);

    /**
     * 根据故障id查询故障处理记录
     *
     * @param idList 故障id
     * @return 故障处理记录
     */
    List<BuFaultHandleRecord> selectHandleRecordListByFaultIdList(@Param("idList") List<String> idList);

    /**
     * 根据条件查询故障简单信息，用于统计数量
     *
     * @param queryVO 查询条件
     * @return 故障简单信息
     */
    List<BuFaultInfo> selectSimpleListByCondition(@Param("queryVO") BuFaultInfoQueryVO queryVO);

    /**
     * 获取车间最新故障信息
     *
     * @param workshopId 车间id
     * @param num        故障条数
     * @return 最新故障信息结果
     */
    List<BuFaultInfo> listLatestFaultByWorkshopIdAndNum(@Param("workshopId") String workshopId, @Param("num") Integer num);

    /**
     * 根据列计划id查询故障id+工单id
     *
     * @param planId 列计划id
     * @return 故障id+工单id
     */
    List<BuFaultInfo> selectListForCountByPlanId(@Param("planId") String planId);

    /**
     * 查询所有已维修的故障的原因描述和数量统计
     *
     * @param repairProgramId 修程id
     * @return 原因描述和数量统计
     */
    List<Map<String, Object>> selectReasonDescFaultCountByRepairProgram(@Param("repairProgramId") String repairProgramId);

}
