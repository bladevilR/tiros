package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainHistoryFormRecordVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryFormQueryVO;

/**
 * <p>
 * 车辆履历-表单记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-04
 */
public interface BuTrainHistoryFormService {

    /**
     * 查询车辆履历相关表单记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 表单记录
     * @throws Exception 异常
     */
    IPage<BuTrainHistoryFormRecordVO> pageFormRecordVO(BuTrainHistoryFormQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
