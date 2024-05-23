package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.modules.produce.trainhistory.bean.BuMaximoLocation;
import org.jeecg.modules.produce.trainhistory.bean.BuMaximoTrainAsset;
import org.jeecg.modules.produce.trainhistory.bean.BuMaximoTrainAssetExt;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainInfo;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuMaximoTrainAssetChildrenQueryVO;
import org.jeecg.modules.produce.trainhistory.mapper.BuMaximoLocationProduceMapper;
import org.jeecg.modules.produce.trainhistory.mapper.BuMaximoTrainAssetExtProduceMapper;
import org.jeecg.modules.produce.trainhistory.mapper.BuMaximoTrainAssetProduceMapper;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainInfoProduceMapper;
import org.jeecg.modules.produce.trainhistory.service.BuMaximoTrainAssetProduceService;
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
public class BuMaximoTrainAssetProduceServiceImpl extends ServiceImpl<BuMaximoTrainAssetProduceMapper, BuMaximoTrainAsset> implements BuMaximoTrainAssetProduceService {

    @Resource
    private BuMaximoTrainAssetProduceMapper buMaximoTrainAssetProduceMapper;
    @Resource
    private BuMaximoTrainAssetExtProduceMapper buMaximoTrainAssetExtProduceMapper;
    @Resource
    private BuMaximoLocationProduceMapper buMaximoLocationProduceMapper;
    @Resource
    private BuTrainInfoProduceMapper buTrainInfoProduceMapper;


    /**
     * @see BuMaximoTrainAssetProduceService#listMaximoTrainAssetChild(BuMaximoTrainAssetChildrenQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaximoTrainAsset> listMaximoTrainAssetChild(BuMaximoTrainAssetChildrenQueryVO queryVO) throws Exception {
        List<BuMaximoTrainAsset> assetList = buMaximoTrainAssetProduceMapper.selectChildrenByQueryVO(queryVO);

        if (CollectionUtils.isNotEmpty(assetList)) {
            // 如果查到的是第一级车辆，查下级
            if (assetList.size() == 1) {
                String id = assetList.get(0).getId();
                String code = assetList.get(0).getCode();
                BuTrainInfo trainInfo = buTrainInfoProduceMapper.selectTrainInfoByTrainNo(queryVO.getTrainNo());
                if (code.equals(trainInfo.getId())) {
                    queryVO.setParentId(id);
                    assetList = buMaximoTrainAssetProduceMapper.selectChildrenByQueryVO(queryVO);
                }
            }

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
                    List<BuMaximoLocation> subLocationList = buMaximoLocationProduceMapper.selectList(locationWrapper);
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
                List<BuMaximoTrainAssetExt> subAssetExtList = buMaximoTrainAssetExtProduceMapper.selectListByAssetIdList(assetIdBatchSub);
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
     * @see BuMaximoTrainAssetProduceService#getTrainAssetById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaximoTrainAsset getTrainAssetById(String id) throws Exception {
        BuMaximoTrainAsset asset = buMaximoTrainAssetProduceMapper.selectAssetById(id);
        if (null == asset) {
            throw new JeecgBootException("资产设备不存在");
        }

        // 查询位置名称
        String locationName = null;
        if (StringUtils.isNotBlank(asset.getLocationCode())) {
            LambdaQueryWrapper<BuMaximoLocation> locationWrapper = new LambdaQueryWrapper<BuMaximoLocation>()
                    .eq(BuMaximoLocation::getCode, asset.getLocationCode());
            List<BuMaximoLocation> locationList = buMaximoLocationProduceMapper.selectList(locationWrapper);
            if (CollectionUtils.isNotEmpty(locationList)) {
                locationName = locationList.get(0).getName();
            }
        }
        // 查询扩展信息
        List<BuMaximoTrainAssetExt> assetExtList = buMaximoTrainAssetExtProduceMapper.selectListByAssetIdList(Collections.singletonList(asset.getId()));

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
