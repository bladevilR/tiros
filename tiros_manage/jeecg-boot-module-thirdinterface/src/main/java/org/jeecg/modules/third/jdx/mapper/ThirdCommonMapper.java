package org.jeecg.modules.third.jdx.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * maximo第三方对接 通用mapper
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-15
 */
public interface ThirdCommonMapper {

    void updateSysConfig(@Param("configCode") String configCode, @Param("configValue") String configValue);

    List<Map<String, Object>> selectDepartIdAndMaximoCode();

    List<Map<String, Object>> selectGroupIdAndName();

    List<Map<String, Object>> selectUserIdAndWorkNo();

    List<Map<String, Object>> selectUserIdAndRealName();

    List<Map<String, Object>> selectWorkstationIdAndName();

    List<Map<String, Object>> selectAssetTypeSystemIdAndName();

    String selectTrainIdByTrainNo(@Param("trainNo") String trainNo);

    String selectDepartMaximoCodeById(@Param("departId") String departId);

    String selectMonitorWorkNoByGroupId(@Param("groupId") String groupId);

}
