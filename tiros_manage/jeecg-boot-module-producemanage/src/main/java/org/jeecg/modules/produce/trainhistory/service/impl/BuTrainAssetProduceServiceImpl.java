package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainAsset;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainInfo;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainStructureDetail;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainAssetQueryVO;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainAssetProduceMapper;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainInfoProduceMapper;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainStructureDetailProduceMapper;
import org.jeecg.modules.produce.trainhistory.service.BuTrainAssetProduceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆设备 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
@Slf4j
@Service
public class BuTrainAssetProduceServiceImpl extends ServiceImpl<BuTrainAssetProduceMapper, BuTrainAsset> implements BuTrainAssetProduceService {

    @Resource
    private BuTrainAssetProduceMapper buTrainAssetProduceMapper;
    @Resource
    private BuTrainStructureDetailProduceMapper buTrainStructureDetailProduceMapper;
    @Resource
    private BuTrainInfoProduceMapper buTrainInfoProduceMapper;


    /**
     * @see BuTrainAssetProduceService#listChildAsset(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainAsset> listChildAsset(String trainNo, String structureDetailId) throws Exception {
        List<BuTrainAsset> resultList = new ArrayList<>();

        BuTrainInfo trainInfo = buTrainInfoProduceMapper.selectTrainInfoByTrainNo(trainNo);
        if (null != trainInfo) {
            // 根据车辆号和车辆结构明细id找到对应的车辆设备
            String trainId = trainInfo.getId();
            LambdaQueryWrapper<BuTrainAsset> assetWrapper = new LambdaQueryWrapper<BuTrainAsset>()
                    .eq(BuTrainAsset::getTrainId, trainId)
                    .eq(BuTrainAsset::getStructDetailId, structureDetailId);
            List<BuTrainAsset> assetList = buTrainAssetProduceMapper.selectList(assetWrapper);

            // 找出设备的下一层子设备列表
            if (CollectionUtils.isNotEmpty(assetList)) {
                List<String> assetIdList = assetList.stream()
                        .map(BuTrainAsset::getId)
                        .collect(Collectors.toList());

                LambdaQueryWrapper<BuTrainAsset> childAssetWrapper = new LambdaQueryWrapper<BuTrainAsset>()
                        .in(BuTrainAsset::getParentId, assetIdList);
                resultList = buTrainAssetProduceMapper.selectList(childAssetWrapper);
            }
        }

        return resultList;
    }

    /**
     * @see BuTrainAssetProduceService#queryAsset(BuTrainAssetQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainAsset> queryAsset(BuTrainAssetQueryVO queryVO) throws Exception {
        setRecursionCondition(queryVO);

        List<BuTrainAsset> assetList = buTrainAssetProduceMapper.selectListByBuTrainAssetQueryVO(queryVO);
        List<BuTrainAsset> resultList;
        Boolean asTree = queryVO.getAsTree();
        if (null != asTree && asTree) {
            resultList = extractListToTree(assetList);
        } else {
            resultList = assetList;
        }

        return resultList;
    }

    /**
     * @see BuTrainAssetProduceService#getAssetById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuTrainAsset getAssetById(String assetId) throws Exception {
        return buTrainAssetProduceMapper.selectAssetById(assetId);
    }

    /**
     * @see BuTrainAssetProduceService#listAssetId(BuTrainAssetQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<String> listAssetId(BuTrainAssetQueryVO queryVO) throws Exception {
        setRecursionCondition(queryVO);

        return buTrainAssetProduceMapper.selectIdListByBuTrainAssetQueryVO(queryVO);
    }

    /**
     * @see BuTrainAssetProduceService#getAssetIdByTrainNoAndStructDetailId(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String getAssetIdByTrainNoAndStructDetailId(String trainNo, String structDetailId) throws Exception {
        List<String> assetIdList = buTrainAssetProduceMapper.selectIdListByTrainNoAndStructDetailId(trainNo, structDetailId);
        if (CollectionUtils.isNotEmpty(assetIdList)) {
            return assetIdList.get(0);
        } else {
            return null;
        }
    }

    private void setRecursionCondition(BuTrainAssetQueryVO queryVO) {
        if (StringUtils.isNotBlank(queryVO.getParentAssetId())) {
            Boolean recurseAsset = queryVO.getRecurseAsset();
            if (null != recurseAsset && recurseAsset) {
                BuTrainAsset trainAsset = buTrainAssetProduceMapper.selectById(queryVO.getParentAssetId());
                if (null != trainAsset) {
                    queryVO.setParentAssetWbs(trainAsset.getWbs());
                }
            }
        } else {
            Boolean recurseStructDetail = queryVO.getRecurseStructDetail();
            if (null != recurseStructDetail && recurseStructDetail && StringUtils.isNotBlank(queryVO.getStructureDetailId())) {
                BuTrainStructureDetail structureDetail = buTrainStructureDetailProduceMapper.selectById(queryVO.getStructureDetailId());
                if (null != structureDetail) {
                    List<String> childIdList = buTrainStructureDetailProduceMapper.selectChildIdListByWbs(structureDetail.getWbs());
                    childIdList.add(structureDetail.getId());
                    queryVO.setStructureDetailIdList(childIdList);
                }
            }
        }
    }

    private List<BuTrainAsset> extractListToTree(List<BuTrainAsset> assetList) {
        List<BuTrainAsset> treeList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(assetList)) {
            List<String> idList = assetList.stream()
                    .map(BuTrainAsset::getId)
                    .collect(Collectors.toList());

            List<BuTrainAsset> topList = assetList.stream()
                    .filter(asset -> !idList.contains(asset.getParentId()))
                    .collect(Collectors.toList());
            extractChildren(topList, assetList);
            treeList = topList;
        }

        return treeList;
    }

    private void extractChildren(List<BuTrainAsset> topList, List<BuTrainAsset> assetList) {
        if (CollectionUtils.isNotEmpty(topList)) {
            for (BuTrainAsset trainAsset : topList) {
                List<BuTrainAsset> children = assetList.stream()
                        .filter(asset -> asset.getParentId().equals(trainAsset.getId()))
                        .collect(Collectors.toList());
                extractChildren(children, assetList);
                trainAsset.setChildren(children);
            }
        }
    }

    private void extractTreeToList(List<BuTrainAsset> childrenList, List<BuTrainAsset> resultList) {
        if (CollectionUtils.isNotEmpty(childrenList)) {
            for (BuTrainAsset trainAsset : childrenList) {
                resultList.add(trainAsset);
                List<BuTrainAsset> children = trainAsset.getChildren();
                extractTreeToList(children, resultList);
            }
        }
    }

}
