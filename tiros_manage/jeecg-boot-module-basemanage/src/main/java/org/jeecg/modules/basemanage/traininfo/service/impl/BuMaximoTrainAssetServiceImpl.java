package org.jeecg.modules.basemanage.traininfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.modules.basemanage.traininfo.entity.BuMaximoLocation;
import org.jeecg.modules.basemanage.traininfo.entity.BuMaximoTrainAsset;
import org.jeecg.modules.basemanage.traininfo.entity.BuMaximoTrainAssetExt;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuMaximoTrainAssetChildrenQueryVO;
import org.jeecg.modules.basemanage.traininfo.mapper.BuMaximoLocationMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuMaximoTrainAssetExtMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuMaximoTrainAssetMapper;
import org.jeecg.modules.basemanage.traininfo.service.BuMaximoTrainAssetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * maximo资产设备 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
@Service
public class BuMaximoTrainAssetServiceImpl extends ServiceImpl<BuMaximoTrainAssetMapper, BuMaximoTrainAsset> implements BuMaximoTrainAssetService {

    @Resource
    private BuMaximoTrainAssetMapper buMaximoTrainAssetMapper;
    @Resource
    private BuMaximoTrainAssetExtMapper buMaximoTrainAssetExtMapper;
    @Resource
    private BuMaximoLocationMapper buMaximoLocationMapper;


    /**
     * @see BuMaximoTrainAssetService#listMaximoTrainAssetChild(BuMaximoTrainAssetChildrenQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaximoTrainAsset> listMaximoTrainAssetChild(BuMaximoTrainAssetChildrenQueryVO queryVO) throws Exception {
        List<BuMaximoTrainAsset> assetList = buMaximoTrainAssetMapper.selectChildrenByQueryVO(queryVO);

        if (CollectionUtils.isNotEmpty(assetList)) {
            // 查询位置名称
            Map<String, String> locationCodeNameMap = new HashMap<>();
            List<String> locationCodeList = assetList.stream()
                    .map(BuMaximoTrainAsset::getLocationCode)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(locationCodeList)) {
                List<List<String>> locationCodeBatchSubList = DatabaseBatchSubUtil.batchSubList(locationCodeList);
                for (List<String> locationCodeBatchSub : locationCodeBatchSubList) {
                    LambdaQueryWrapper<BuMaximoLocation> locationWrapper = new LambdaQueryWrapper<BuMaximoLocation>()
                            .in(BuMaximoLocation::getCode, locationCodeBatchSub);
                    List<BuMaximoLocation> subLocationList = buMaximoLocationMapper.selectList(locationWrapper);
                    subLocationList.forEach(location -> locationCodeNameMap.put(location.getCode(), location.getName()));
                }
            }
            // 查询扩展信息
            List<BuMaximoTrainAssetExt> assetExtList = new ArrayList<>();
            List<String> assetIdList = assetList.stream()
                    .map(BuMaximoTrainAsset::getId)
                    .collect(Collectors.toList());
            List<List<String>> assetIdBatchSubList = DatabaseBatchSubUtil.batchSubList(assetIdList);
            for (List<String> assetIdBatchSub : assetIdBatchSubList) {
                List<BuMaximoTrainAssetExt> subAssetExtList = buMaximoTrainAssetExtMapper.selectListByAssetIdList(assetIdBatchSub);
                assetExtList.addAll(subAssetExtList);
            }

            for (BuMaximoTrainAsset asset : assetList) {
                // 设置位置名称
                asset.setLocationName(locationCodeNameMap.get(asset.getLocationCode()));
                // 设置扩展信息
                List<BuMaximoTrainAssetExt> extList = assetExtList.stream()
                        .filter(assetExt -> asset.getId().equals(assetExt.getAssetId()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(extList)) {
                    asset.setExt(extList.get(0));
                } else {
                    asset.setExt(new BuMaximoTrainAssetExt().setAssetId(asset.getId()));
                }
            }
        }

        return assetList;
    }

    /**
     * @see BuMaximoTrainAssetService#getTrainAssetById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaximoTrainAsset getTrainAssetById(String id) throws Exception {
        BuMaximoTrainAsset asset = buMaximoTrainAssetMapper.selectAssetById(id);
        if (null == asset) {
            throw new JeecgBootException("资产设备不存在");
        }

        // 查询位置名称
        String locationName = null;
        if (StringUtils.isNotBlank(asset.getLocationCode())) {
            LambdaQueryWrapper<BuMaximoLocation> locationWrapper = new LambdaQueryWrapper<BuMaximoLocation>()
                    .eq(BuMaximoLocation::getCode, asset.getLocationCode());
            List<BuMaximoLocation> locationList = buMaximoLocationMapper.selectList(locationWrapper);
            if (CollectionUtils.isNotEmpty(locationList)) {
                locationName = locationList.get(0).getName();
            }
        }
        // 查询扩展信息
        List<BuMaximoTrainAssetExt> assetExtList = buMaximoTrainAssetExtMapper.selectListByAssetIdList(Collections.singletonList(asset.getId()));

        // 设置位置名称
        asset.setLocationName(locationName);
        // 设置扩展信息
        if (CollectionUtils.isNotEmpty(assetExtList)) {
            asset.setExt(assetExtList.get(0));
        } else {
            asset.setExt(new BuMaximoTrainAssetExt().setAssetId(asset.getId()));
        }

        return asset;
    }

}
