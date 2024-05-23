package org.jeecg.modules.basemanage.traininfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.traininfo.entity.BuMaximoTrainAssetExt;
import org.jeecg.modules.basemanage.traininfo.mapper.BuMaximoTrainAssetExtMapper;
import org.jeecg.modules.basemanage.traininfo.service.BuMaximoTrainAssetExtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * maximo资产设备扩展信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-29
 */
@Service
public class BuMaximoTrainAssetExtServiceImpl extends ServiceImpl<BuMaximoTrainAssetExtMapper, BuMaximoTrainAssetExt> implements BuMaximoTrainAssetExtService {

    @Resource
    private BuMaximoTrainAssetExtMapper buMaximoTrainAssetExtMapper;


    /**
     * @see BuMaximoTrainAssetExtService#saveAssetExt(BuMaximoTrainAssetExt)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveAssetExt(BuMaximoTrainAssetExt assetExt) throws Exception {
        // 删除旧的
        LambdaQueryWrapper<BuMaximoTrainAssetExt> deleteWrapper = new LambdaQueryWrapper<BuMaximoTrainAssetExt>()
                .eq(BuMaximoTrainAssetExt::getAssetId, assetExt.getAssetId());
        buMaximoTrainAssetExtMapper.delete(deleteWrapper);

        // 插入新的
        if (StringUtils.isBlank(assetExt.getId())) {
            assetExt.setId(UUIDGenerator.generate());
        }
        buMaximoTrainAssetExtMapper.insert(assetExt);

        return true;
    }

}
