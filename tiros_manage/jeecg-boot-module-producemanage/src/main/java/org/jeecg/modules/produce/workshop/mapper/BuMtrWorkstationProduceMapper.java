package org.jeecg.modules.produce.workshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;
import org.jeecg.modules.produce.workshop.bean.BuMtrWorkstation;
import org.jeecg.modules.produce.workshop.bean.bo.BuWorkOrderTaskBO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工位信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-30
 */
public interface BuMtrWorkstationProduceMapper extends BaseMapper<BuMtrWorkstation> {

    /**
     * 根据条件查询车间工位信息列表
     *
     * @param workshopId 车间id
     * @param stationNo  工位号
     * @return 车间工位信息列表
     */
    List<BuMtrWorkstation> selectListByWorkshopIdAndStationNo(@Param("workshopId") String workshopId, @Param("stationNo") String stationNo);

    /**
     * 根据id查询工位信息
     *
     * @param id 工位id
     * @return 工位信息
     */
    BuMtrWorkstation selectWorkstationById(@Param("id") String id);

    /**
     * 根据工位id列表查询工位正在作业中的工单任务列表
     *
     * @param workstationIdList 工位id列表
     * @return 正在作业中的工单任务列表
     */
    List<BuWorkOrderTaskBO> selectWorkingOrderTaskListByWorkstationIdList(@Param("workstationIdList") List<String> workstationIdList);

    /**
     * 根据工位id列表查询工位正在作业中的工单任务列表
     *
     * @param workstationIdList 工位id列表
     * @return 正在作业中的工单任务列表
     */
    List<BuFaultInfo> selectUnHandleFaultListByWorkstationIdList(@Param("workstationIdList") List<String> workstationIdList);

    /**
     * 查询班组+工位
     *
     * @return 班组+工位
     */
    List<Map<String, Object>> selectGroupWorkstationNameMapList();

}
