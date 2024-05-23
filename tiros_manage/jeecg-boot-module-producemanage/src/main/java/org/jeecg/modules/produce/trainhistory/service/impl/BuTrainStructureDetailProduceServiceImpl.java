package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainInfo;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainStructureDetail;
import org.jeecg.modules.produce.trainhistory.bean.vo.*;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainInfoProduceMapper;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainStructureDetailProduceMapper;
import org.jeecg.modules.produce.trainhistory.service.BuTrainStructureDetailProduceService;
import org.jeecg.modules.produce.trainhistory.service.cache.StructTreeCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆结构明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
@Slf4j
@Service
public class BuTrainStructureDetailProduceServiceImpl extends ServiceImpl<BuTrainStructureDetailProduceMapper, BuTrainStructureDetail> implements BuTrainStructureDetailProduceService {

    @Resource
    private BuTrainStructureDetailProduceMapper buTrainStructureDetailProduceMapper;
    @Resource
    private BuTrainInfoProduceMapper buTrainInfoProduceMapper;
    @Resource
    private StructTreeCacheService structTreeCacheService;


    /**
     * @see BuTrainStructureDetailProduceService#treeLineAndTrain()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainHistoryTreeBaseVO> treeLineAndTrain() throws Exception {
        List<BuTrainHistoryTreeLineVO> lineTreeResult = new ArrayList<>();

        List<BuTrainInfo> buTrainInfoList = buTrainInfoProduceMapper.selectAll();
        if (CollectionUtils.isNotEmpty(buTrainInfoList)) {
            Map<String, List<BuTrainInfo>> lineIdTrainInfoListMap = buTrainInfoList.stream()
                    .collect(Collectors.groupingBy(BuTrainInfo::getLineId));
            for (Map.Entry<String, List<BuTrainInfo>> lineIdTrainInfoListEntry : lineIdTrainInfoListMap.entrySet()) {
                String lineId = lineIdTrainInfoListEntry.getKey();
                List<BuTrainInfo> trainInfoList = lineIdTrainInfoListEntry.getValue();

                BuTrainHistoryTreeLineVO treeLineVO = new BuTrainHistoryTreeLineVO()
                        .setLineId(lineId)
                        .setLineName(trainInfoList.get(0).getLineName())
                        .setTrains(transformTrainToTrainTree(trainInfoList));

                lineTreeResult.add(treeLineVO);
            }
        }

        return transformLineTreeToBaseTree(lineTreeResult);
    }


    private List<BuTrainHistoryTreeTrainVO> transformTrainToTrainTree(List<BuTrainInfo> trainInfoList) throws Exception {
        List<BuTrainHistoryTreeTrainVO> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(trainInfoList)) {
            for (BuTrainInfo trainInfo : trainInfoList) {
                BuTrainHistoryTreeTrainVO trainVO = new BuTrainHistoryTreeTrainVO()
                        .setTrainId(null == trainInfo ? "" : trainInfo.getId())
                        .setTrainNo(null == trainInfo ? "" : trainInfo.getTrainNo())
                        .setAssets(new ArrayList<>());
                result.add(trainVO);
            }
        }

        return result;
    }

    private List<BuTrainHistoryTreeTrainVO> transformTrainToTrainTree(List<BuTrainInfo> trainInfoList, boolean needStructTree) throws Exception {
        List<BuTrainHistoryTreeTrainVO> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(trainInfoList)) {
            for (BuTrainInfo trainInfo : trainInfoList) {
//                BuTrainHistoryTreeStructQueryVO queryVO = new BuTrainHistoryTreeStructQueryVO()
//                        .setTrainNo(trainInfo.getTrainNo());
//                List<BuTrainStructureDetail> topStructureDetailList = buTrainStructureDetailProduceMapper.selectListByBuTrainHistoryTreeStructQueryVO(queryVO);
                List<BuTrainStructureDetailTreeVO> detailTreeVOList = new ArrayList<>();
                if (needStructTree) {
                    String structId = null == trainInfo ? "" : trainInfo.getTrainStructId();

                    detailTreeVOList = structTreeCacheService.treeAllStructureByStructId(structId);
                }

                BuTrainHistoryTreeTrainVO trainVO = new BuTrainHistoryTreeTrainVO()
                        .setTrainId(null == trainInfo ? "" : trainInfo.getId())
                        .setTrainNo(null == trainInfo ? "" : trainInfo.getTrainNo())
                        .setStructureDetailTree(detailTreeVOList);

                result.add(trainVO);
            }
        }

        return result;
    }

    private List<BuTrainHistoryTreeBaseVO> transformLineTreeToBaseTree(List<BuTrainHistoryTreeLineVO> lineTreeResult) {
        List<BuTrainHistoryTreeBaseVO> treeResult = new ArrayList<>();
        for (BuTrainHistoryTreeLineVO treeLineVO : lineTreeResult) {
            BuTrainHistoryTreeBaseVO treeBaseVO = new BuTrainHistoryTreeBaseVO()
                    .setId(treeLineVO.getLineId())
                    .setTitle(treeLineVO.getLineName())
                    .setLongTitle(treeLineVO.getLineName())
                    .setType(1)
                    .setDisabled(true)
                    .setChildren(transformTrainTreeToBaseTree(treeLineVO.getTrains()));

            treeResult.add(treeBaseVO);
        }

        return treeResult;
    }

    private List<BuTrainHistoryTreeBaseVO> transformTrainTreeToBaseTree(List<BuTrainHistoryTreeTrainVO> trainTreeResult) {
        List<BuTrainHistoryTreeBaseVO> treeResult = new ArrayList<>();
        for (BuTrainHistoryTreeTrainVO trainTree : trainTreeResult) {
            BuTrainHistoryTreeBaseVO treeBaseVO = new BuTrainHistoryTreeBaseVO()
                    .setId(trainTree.getTrainId())
                    .setTitle(trainTree.getTrainNo())
                    .setLongTitle(trainTree.getTrainNo() + " - " + trainTree.getTrainId())
                    .setType(2)
                    .setDisabled(false)
                    .setChildren(transformAssetToBaseTree(trainTree.getAssets()));

            treeResult.add(treeBaseVO);
        }

        return treeResult;
    }

    private List<BuTrainHistoryTreeBaseVO> transformAssetToBaseTree(List<BuTrainHistoryTreeAssetVO> assetTreeResult) {
        List<BuTrainHistoryTreeBaseVO> treeResult = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(assetTreeResult)) {
            for (BuTrainHistoryTreeAssetVO assetTree : assetTreeResult) {
                List<BuTrainHistoryTreeBaseVO> children = transformAssetToBaseTree(assetTree.getChildren());

                BuTrainHistoryTreeBaseVO treeBaseVO = new BuTrainHistoryTreeBaseVO()
                        .setId(assetTree.getId())
                        .setTitle(assetTree.getName())
                        .setLongTitle(assetTree.getName())
                        .setType(3)
                        .setDisabled(false)
                        .setChildren(children);

                treeResult.add(treeBaseVO);
            }
        }

        return treeResult;
    }

}
