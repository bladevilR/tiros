package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryWork;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryWorkQueryVO;

/**
 * <p>
 * 车辆履历-作业记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryWorkService extends IService<BuTrainHistoryWork> {

    /**
     * 根据条件查询车辆履历作业记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 作业记录分页
     * @throws Exception 异常信息
     */
    IPage<BuTrainHistoryWork> pageWorkRecord(BuTrainHistoryWorkQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
