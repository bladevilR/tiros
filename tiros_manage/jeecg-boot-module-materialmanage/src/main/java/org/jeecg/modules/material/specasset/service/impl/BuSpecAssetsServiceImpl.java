package org.jeecg.modules.material.specasset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.material.specasset.bean.BuSpecAssets;
import org.jeecg.modules.material.specasset.bean.vo.BuSpecAssetQueryVO;
import org.jeecg.modules.material.specasset.mapper.BuSpecAssetsMapper;
import org.jeecg.modules.material.specasset.service.BuSpecAssetsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 特种设备 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-02
 */
@Service
public class BuSpecAssetsServiceImpl extends ServiceImpl<BuSpecAssetsMapper, BuSpecAssets> implements BuSpecAssetsService {

    @Resource
    private BuSpecAssetsMapper buSpecAssetsMapper;


    /**
     * @see BuSpecAssetsService#pageSpecAsset(BuSpecAssetQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuSpecAssets> pageSpecAsset(BuSpecAssetQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buSpecAssetsMapper.selectPageByQueryVO(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuSpecAssetsService#getSpecAssetById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuSpecAssets getSpecAssetById(String id) throws Exception {
        return buSpecAssetsMapper.selectSpecAssetById(id);
    }

    /**
     * @see BuSpecAssetsService#saveSpecAsset(BuSpecAssets)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveSpecAsset(BuSpecAssets specAsset) throws Exception {
        if (StringUtils.isBlank(specAsset.getId())) {
            buSpecAssetsMapper.insert(specAsset);
        } else {
            BuSpecAssets dbSpecAsset = buSpecAssetsMapper.selectById(specAsset.getId());
            if (null == dbSpecAsset) {
                buSpecAssetsMapper.insert(specAsset);
            } else {
                LambdaQueryWrapper<BuSpecAssets> wrapper = new LambdaQueryWrapper<BuSpecAssets>()
                        .eq(BuSpecAssets::getId, specAsset.getId());
                buSpecAssetsMapper.update(specAsset, wrapper);
            }
        }

        return true;
    }

    /**
     * @see BuSpecAssetsService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> specAssetIdList = Arrays.asList(ids.split(","));

        if (CollectionUtils.isNotEmpty(specAssetIdList)) {
            buSpecAssetsMapper.deleteBatchIds(specAssetIdList);
        }

        return true;
    }

}
