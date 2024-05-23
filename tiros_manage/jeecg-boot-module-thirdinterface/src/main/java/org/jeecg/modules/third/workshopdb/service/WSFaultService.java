package org.jeecg.modules.third.workshopdb.service;

import org.jeecg.modules.third.workshopdb.bean.WSFault;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车间故障 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-03-23
 */
public interface WSFaultService {

    /**
     * 统计所有车间故障数量
     *
     * @return 车间故障数量
     */
    String countAll(Map<String, String> lineNameIdMap);

    /**
     * 查询所有车间故障
     *
     * @return 车间故障列表
     */
    List<WSFault> selectAllList(Map<String, String> lineNameIdMap);

}
