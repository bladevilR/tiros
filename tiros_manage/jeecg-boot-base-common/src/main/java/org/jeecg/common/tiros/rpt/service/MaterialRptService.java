package org.jeecg.common.tiros.rpt.service;

import java.util.List;

/**
 * <p>
 * 物料消耗统计 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-23
 */
public interface MaterialRptService {

    /**
     * 重新计算工单所有的物料成本到中间表
     *
     * @return 是否成功
     */
    boolean reBuildRptMaterial();

    /**
     * 重新计算工单所有的物料成本到中间表
     *
     * @param trainNo 车辆号
     * @return 是否成功
     */
    boolean reBuildRptMaterialByTrainNo(String trainNo);

}
