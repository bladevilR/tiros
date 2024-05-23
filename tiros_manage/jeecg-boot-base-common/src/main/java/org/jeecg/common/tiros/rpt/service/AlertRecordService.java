package org.jeecg.common.tiros.rpt.service;

import java.util.List;

/**
 * <p>
 * 预警信息记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-25
 */
public interface AlertRecordService {

    /**
     * 设置预警信息为已处理
     *
     * @param alertType      预警类型
     * @param alertObjIdList 预警对象id列表
     * @return 是否成功
     */
    boolean setAlertRecordHandled(Integer alertType, List<String> alertObjIdList);

    /**
     * 删除预警信息
     *
     * @param alertType      预警类型
     * @param alertObjIdList 预警对象id列表
     * @return 是否成功
     */
    boolean deleteAlertRecord(Integer alertType, List<String> alertObjIdList);

}
