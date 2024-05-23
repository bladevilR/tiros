package org.jeecg.modules.third.workshopdb.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.workshopdb.bean.WSFault;

import java.util.List;

/**
 * <p>
 * 车间故障 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-03-23
 */
public interface WSFaultMapper {

    Long countAll(@Param("lineNameList") List<String> lineNameList);

    List<WSFault> selectAllList(@Param("lineNameList") List<String> lineNameList);

}
