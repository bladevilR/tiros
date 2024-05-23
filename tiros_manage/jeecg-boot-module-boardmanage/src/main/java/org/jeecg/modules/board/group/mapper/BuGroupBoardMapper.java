package org.jeecg.modules.board.group.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.group.bean.BuMaterialType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 班组看板 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
public interface BuGroupBoardMapper {

    /**
     * 根据用户id查询用户所属的班组id
     *
     * @param userId 用户id
     * @return 班组id
     */
    String selectGroupIdByUserId(@Param("userId") String userId);

    List<Map<String, Object>> selectOrderUserIdWorkTimeByGroupAndPlan(@Param("groupId") String groupId, @Param("planIdList") List<String> planIdList);

    List<String> selectFaultReportUserIdByGroupAndPlan(@Param("groupId") String groupId, @Param("planIdList") List<String> planIdList);

    List<Map<String, Object>> selectGroupUserInfoList(@Param("groupId") String groupId);

    List<Map<String, Object>> selectMustMaterialListByLineAndProgramAndGroup(@Param("lineId") String lineId, @Param("programId") String programId, @Param("groupId") String groupId, @Param("planId") String planId);

    List<Map<String, Object>> selectGroupStockAmount(@Param("groupId") String groupId, @Param("materialTypeIdList") List<String> materialTypeIdList);

    List<BuMaterialType> selectMaterialTypeListByMaterialTypeIdList(@Param("materialTypeIdList") List<String> materialTypeIdList);

    List<Map<String, Object>> selectGroupToolCheckAlertList(@Param("groupId") String groupId, @Param("date") Date date);

    List<String> selectFaultReportGroupIdByPlan(@Param("planIdList") List<String> planIdList);

    List<Map<String, Object>> selectOrderGroupIdStatusByPlan(@Param("planIdList") List<String> planIdList);

    /**
     * 查询班组名称
     *
     * @param groupId 班组id
     * @return string
     */
    String selectGroupName(@Param("groupId") String groupId);
}
