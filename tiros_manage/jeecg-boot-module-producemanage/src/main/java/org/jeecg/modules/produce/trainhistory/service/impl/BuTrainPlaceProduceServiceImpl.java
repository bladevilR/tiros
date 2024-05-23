package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainPlace;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainPlaceProduceMapper;
import org.jeecg.modules.produce.trainhistory.service.BuTrainPlaceProduceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 车辆位置信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-29
 */
@Slf4j
@Service
public class BuTrainPlaceProduceServiceImpl extends ServiceImpl<BuTrainPlaceProduceMapper, BuTrainPlace> implements BuTrainPlaceProduceService {

    @Resource
    private BuTrainPlaceProduceMapper buTrainPlaceProduceMapper;

    /**
     * @see BuTrainPlaceProduceService#selectTrainPlaceByStructureDetailId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuTrainPlace selectTrainPlaceByStructureDetailId(String structureDetailId) throws Exception {
        BuTrainPlace trainPlace = buTrainPlaceProduceMapper.selectByStructureDetailId(structureDetailId);
        if (null == trainPlace) {
            trainPlace = new BuTrainPlace()
                    .setPlaceName("")
                    .setRemark("");
        }

        return trainPlace;
    }
}
