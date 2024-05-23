package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainPlace;

/**
 * <p>
 * 车辆位置信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-29
 */
public interface BuTrainPlaceProduceService extends IService<BuTrainPlace> {

    /**
     * 根据车辆结构明细id查询对应位置信息
     *
     * @param structureDetailId 车辆结构明细id
     * @return 位置信息
     * @throws Exception 异常信息
     */
    BuTrainPlace selectTrainPlaceByStructureDetailId(String structureDetailId) throws Exception;

}
