package org.jeecg.common.tiros.task.service;

import java.util.List;

/**
 * <p>
 * 归档车辆履历 定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-04
 */
public interface TrainHistoryRecordCollectTaskService {

    /**
     * 归档车辆履历(用于定时任务)
     * 已完成的工单、故障、更换、测量数据转移到车辆履历表
     *
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean generateTrainHistoryRecord() throws RuntimeException;

    /**
     * 更新车辆履历故障记录
     *
     * @param faultIdList 故障id列表
     * @param type        1=删除，2=更新(删除重新生成)
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean reWriteTrainHistoryFaultRecord(List<String> faultIdList, int type) throws RuntimeException;

}
