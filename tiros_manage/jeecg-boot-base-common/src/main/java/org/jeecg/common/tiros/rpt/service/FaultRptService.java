package org.jeecg.common.tiros.rpt.service;

import java.util.List;

/**
 * <p>
 * 故障统计 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-23
 */
public interface FaultRptService {

//    /**
//     * 增加故障统计中间表数据
//     *
//     * @param faultId 故障id
//     */
//    void increaseFaultRpt(String faultId);

    /**
     * 重新统计所有的故障到故障统计中间表数据
     *
     * @param trainNoList 车号
     * @return 执行结果
     */
    String rebuildFaultRpt(List<String> trainNoList);

}
