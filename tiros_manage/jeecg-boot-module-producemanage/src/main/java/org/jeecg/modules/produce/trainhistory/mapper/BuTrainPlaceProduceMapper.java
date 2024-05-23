package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainPlace;

/**
 * <p>
 * 车辆位置信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-29
 */
public interface BuTrainPlaceProduceMapper extends BaseMapper<BuTrainPlace> {

    /**
     * 根据车辆结构明细id查询对应位置信息
     *
     * @param structureDetailId 车辆结构明细id
     * @return 位置信息
     */
    BuTrainPlace selectByStructureDetailId(@Param("structureDetailId") String structureDetailId);

}
