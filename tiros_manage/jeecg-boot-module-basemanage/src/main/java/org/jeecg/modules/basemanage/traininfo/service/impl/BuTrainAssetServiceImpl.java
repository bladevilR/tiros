package org.jeecg.modules.basemanage.traininfo.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.LevelCodeUtil;
import org.jeecg.common.tiros.wbs.entity.WbsConf;
import org.jeecg.common.tiros.wbs.service.WbsService;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAsset;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainInfo;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructureDetail;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetListQueryVOForApp;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetQueryVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTreeQueryVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeCopyVO;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetTypeMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainInfoMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainStructureDetailMapper;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainAssetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 车辆设备，按车辆分区存放，如果后期设备太多，可以线路分表，设备更换时，如果这个设备对应的是周转件，从车上换下的设备需要在 服务实现类
 *
 * @author 喻有根
 * @since 2020-07-15
 */
@Slf4j
@Service
public class BuTrainAssetServiceImpl extends ServiceImpl<BuTrainAssetMapper, BuTrainAsset> implements IBuTrainAssetService {

    @Resource
    private BuTrainAssetMapper buTrainAssetMapper;
    @Resource
    private BuTrainStructureDetailMapper buTrainStructureDetailMapper;
    @Resource
    private BuTrainInfoMapper buTrainInfoMapper;
    @Resource
    private BuTrainAssetTypeMapper buTrainAssetTypeMapper;
    @Resource
    private WbsService wbsService;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see IBuTrainAssetService#listTrainAsset(BuTrainAssetTreeQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainAsset> listTrainAsset(BuTrainAssetTreeQueryVO queryVO) throws Exception {
        String trainId = buTrainInfoMapper.selectIdByTrainNo(queryVO.getTrainNo());
        queryVO.setTrainId(trainId);

        // 根据车辆、设备类型、车辆结构，查询符合条件的车辆设备
        List<BuTrainAsset> topAssetList = buTrainAssetMapper.selectListByTreeQueryVO(queryVO);
        if (CollectionUtils.isEmpty(topAssetList)) {
            return topAssetList;
        }

        // 子设备
        Boolean queryChild = queryVO.getQueryChild();
        Boolean queryAllChildren = queryVO.getQueryAllChildren();
        if (null == queryChild) {
            queryChild = false;
        }
        if (null == queryAllChildren) {
            queryAllChildren = false;
        }
        // 查询所有子设备，以列表返回
        if (queryAllChildren) {
            List<String> wbsList = topAssetList.stream()
                    .map(BuTrainAsset::getWbs)
                    .collect(Collectors.toList());
            List<BuTrainAsset> children = buTrainAssetMapper.selectAllChildrenListByWbsList(wbsList);

            topAssetList.addAll(children);
        }
        // 查询所有子设备，以树返回
        if (queryChild) {
            List<String> wbsList = topAssetList.stream()
                    .map(BuTrainAsset::getWbs)
                    .collect(Collectors.toList());
            List<BuTrainAsset> children = buTrainAssetMapper.selectAllChildrenListByWbsList(wbsList);

            extractAssetListToTree(topAssetList, children);
        }

        return topAssetList;
    }

    /**
     * @see IBuTrainAssetService#listChildByTrainNoAndStructDetailId(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainAsset> listChildByTrainNoAndStructDetailId(String trainNo, String structDetailId) throws Exception {
        // 车辆号转车辆id
        String trainId = buTrainInfoMapper.selectIdByTrainNo(trainNo);
        if (StringUtils.isBlank(structDetailId)) {
            return buTrainAssetMapper.selectTopListByTrainId(trainId);
        }

        // 根据车辆id、车辆结构明细id，查询符合条件的车辆设备
        List<BuTrainAsset> topAssetList = buTrainAssetMapper.selectListByTrainIdAndStructDetailId(trainId, structDetailId);
        if (CollectionUtils.isEmpty(topAssetList)) {
            return new ArrayList<>();
        }

        // 查询子设备列表
        List<String> parentIdList = topAssetList.stream()
                .map(BuTrainAsset::getId)
                .collect(Collectors.toList());
        return buTrainAssetMapper.selectListByParentIdList(parentIdList);
    }

    /**
     * @see IBuTrainAssetService#listTrainAssetForApp(BuTrainAssetListQueryVOForApp)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainAsset> listTrainAssetForApp(BuTrainAssetListQueryVOForApp queryVO) throws Exception {
        List<BuTrainAsset> assetList = new ArrayList<>();

        Boolean queryAllChildren = queryVO.getQueryAllChildren();
        if (null != queryAllChildren && queryAllChildren) {
            List<BuTrainAsset> assetTree = buTrainAssetMapper.selectTreeListByListQueryVOForApp(queryVO);
            extractAssetTreeToList(assetTree, assetList);
        } else {
            assetList = buTrainAssetMapper.selectListByListQueryVOForApp(queryVO);
        }

        setSystemIdAndName(assetList);

        return assetList;
    }

    /**
     * @see IBuTrainAssetService#selectTrainAssetChild(BuTrainAssetQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainAsset> selectTrainAssetChild(BuTrainAssetQueryVO queryVO) {
        //        for (BuTrainAsset trainAsset : trainAssetList) {
        //            recurseAddChild(trainAsset, trainAssetList);
        //        }
        //
        //        // 如果同时含父节点和子节点，去掉所有子节点
        //        Set<String> assetIdSet = new HashSet<>();
        //        recurseAddAssetId(trainAssetList, assetIdSet);
        //
        //        List<BuTrainAsset> resultList = trainAssetList.stream()
        //                .filter(asset -> !assetIdSet.contains(asset.getParentId()))
        //                .collect(Collectors.toList());
        //        if (null == queryVO.getNeedChildren() || !queryVO.getNeedChildren()) {
        //            resultList.forEach(asset -> asset.setChildren(new ArrayList<>()));
        //        }
        List<BuTrainAsset> assetList = buTrainAssetMapper.selectListByBuTrainAssetQueryVO(queryVO);

//        if (StringUtils.isNotBlank(queryVO.getTitle())) {
//            for (BuTrainAsset asset : assetList) {
//                asset.setHasChildren(null);
//            }
//        }

        return assetList;
    }

    /**
     * @see IBuTrainAssetService#produceTrainAsset(String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean produceTrainAsset(String id, String structId) throws Exception {
        BuTrainInfo trainInfo = buTrainInfoMapper.selectById(id);
        if (null == trainInfo) {
            throw new JeecgBootException("车辆不存在");
        }

        // 获取车辆结构详情
        List<BuTrainStructureDetail> structureDetailList = buTrainStructureDetailMapper.selectListByStructId(structId);
        if (CollectionUtils.isEmpty(structureDetailList)) {
            throw new JeecgBootException("该车辆结构没有具体的数据!");
        }
        Map<String, String> structDetailIdTrainAssetIdMap = new HashMap<>();
        for (BuTrainStructureDetail structureDetail : structureDetailList) {
            structDetailIdTrainAssetIdMap.put(structureDetail.getId(), UUIDGenerator.generate());
        }

        // 先删除已有的车辆设备
        LambdaQueryWrapper<BuTrainAsset> trainAssetDeleteWrapper = new LambdaQueryWrapper<>();
        trainAssetDeleteWrapper.eq(BuTrainAsset::getTrainId, id);
        buTrainAssetMapper.delete(trainAssetDeleteWrapper);

        // 构建车辆设备对象
        List<BuTrainAsset> assetList = new ArrayList<>();
        for (BuTrainStructureDetail structureDetail : structureDetailList) {
            BuTrainAsset asset = transformToTrainAsset(structureDetail, structDetailIdTrainAssetIdMap, trainInfo);
            assetList.add(asset);
        }

        if (CollectionUtils.isNotEmpty(assetList)) {
            // 设置车辆设备编码和wbs
            // 从顶级节点，一层层的设置code和wbs
            List<BuTrainAsset> topList = assetList.stream()
                    .filter(asset -> StringUtils.isBlank(asset.getParentId()))
                    .collect(Collectors.toList());
            //龚乾坤注释
            /*setTopListCodeAndWbs(topList, trainInfo);
            for (BuTrainAsset top : topList) {
                top.setWbs(top.getAssetNo());
                List<BuTrainAsset> topChildren = assetList.stream()
                        .filter(asset -> top.getId().equals(asset.getParentId()))
                        .collect(Collectors.toList());
                sortListAndSetCodeWbs(topChildren, top, assetList);
            }*/
            this.setWbs(topList, null, assetList);

            // 保存车辆设备
            List<List<BuTrainAsset>> batchSubList = DatabaseBatchSubUtil.batchSubList(assetList);
            for (List<BuTrainAsset> batchSub : batchSubList) {
                buTrainAssetMapper.insertList(batchSub);
            }
        }

        // 更新车辆的车型信息、是否已生成车辆设备
        trainInfo.setTrainStructId(structId)
                .setIsGenAsset(1);
        buTrainInfoMapper.updateById(trainInfo);

        return true;
    }

    /**
     *  设置列表的wbs编码
     *
     * @author: Jak
     * @date: 2021-07-04 2:39
     *@version 1.0
     */
    public void setWbs(List<BuTrainAsset> list, BuTrainAsset parent, List<BuTrainAsset> alllist) {
        String parentWbs = "";
        if (parent != null) {
            parentWbs = parent.getWbs()+".";
        }

        for (BuTrainAsset asset : list) {
            asset.setWbs(parentWbs + asset.getAssetNo());

            List<BuTrainAsset> children = alllist.stream()
                    .filter(item -> asset.getId().equals(item.getParentId()))
                    .collect(Collectors.toList());
            if (children.size() > 0) {
                this.setWbs(children, asset, alllist);
            }
        }
    }

    /**
     * @see IBuTrainAssetService#importByAssetType(BuTrainAssetTypeCopyVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean importByAssetType(BuTrainAssetTypeCopyVO importVO) throws InterruptedException {
        if (StringUtils.isBlank(importVO.getChildId())) {
            throw new JeecgBootException("请选择要导入的设备类型");
        }

        BuTrainInfo trainInfo = buTrainInfoMapper.selectByTrainNo(importVO.getTrainNo());
        if (null == trainInfo) {
            throw new JeecgBootException("车辆信息不存在，车号=" + importVO.getTrainNo());
        }

        String parentId = StringUtils.isBlank(importVO.getParentId()) ? null : importVO.getParentId();

//        // 查询所有已存在的编码
//        List<String> existAssetNoList = buTrainAssetMapper.selectAssetNoListByTrainId(trainInfo.getId());

        // 查询设备类型
        List<String> assetTypeIdList = Arrays.asList(importVO.getChildId().split(","));
        List<String> assetTypeWbsList = buTrainAssetTypeMapper.selectWbsListByIdList(assetTypeIdList);
        List<BuTrainAssetType> assetTypeList = buTrainAssetTypeMapper.selectAssetTypeAndChildrenAsListByWbsList(assetTypeWbsList);

        // 转换成设备类型树
        List<String> allAssetTypeIdList = assetTypeList.stream()
                .map(BuTrainAssetType::getId)
                .collect(Collectors.toList());
        List<BuTrainAssetType> topAssetTypeList = assetTypeList.stream()
                .filter(assetType -> !allAssetTypeIdList.contains(assetType.getParentId()))
                .collect(Collectors.toList());
        extractAssetTypeListToTree(topAssetTypeList, assetTypeList);

        // 查询上级
        BuTrainAsset parent = null;
        String brotherMaxCode = null;
        if (StringUtils.isNotBlank(parentId)) {
            parent = buTrainAssetMapper.selectById(parentId);
            brotherMaxCode = getBrotherMaxCodeByParentIdAndTrainId(parentId, trainInfo.getId());
        }
        List<BuTrainAsset> assetList = new ArrayList<>();
        // 转化成车辆设备信息
        recurseAssetTypeTreeToAssetList(topAssetTypeList, assetList, parent, brotherMaxCode);

        if (CollectionUtils.isNotEmpty(assetList)) {
            // 设置车辆id、线路id
            for (BuTrainAsset asset : assetList) {
                asset.setTrainId(trainInfo.getId())
                        .setLineId(trainInfo.getLineId());
            }
            // 保存车辆设备
            List<List<BuTrainAsset>> batchSubList = DatabaseBatchSubUtil.batchSubList(assetList);
            for (List<BuTrainAsset> batchSub : batchSubList) {
                buTrainAssetMapper.insertList(batchSub);
            }
        }

        // 更新wbs
        WbsConf wbsConf = new WbsConf("bu_train_asset")
                .setCode("asset_no")
                .setFilter("train_id = '" + trainInfo.getId() + "'");
        wbsService.updateWbs(wbsConf);

        // 更新车辆的是否已生成车辆设备
        trainInfo.setIsGenAsset(1);
        buTrainInfoMapper.updateById(trainInfo);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAllByIds(List<String> ids) throws Exception {
        ids.forEach(
                id -> {
                    BuTrainAsset trainAsset = buTrainAssetMapper.selectById(id);
                    if (Objects.nonNull(trainAsset)) {
                        LambdaQueryWrapper<BuTrainAsset> wrapper = new LambdaQueryWrapper<>();
                        wrapper.eq(BuTrainAsset::getTrainId, trainAsset.getTrainId());
                        wrapper.likeRight(BuTrainAsset::getWbs, trainAsset.getWbs());
                        buTrainAssetMapper.delete(wrapper);
                    }
                });

        return true;
    }

    /**
     * @see IBuTrainAssetService#saveTrainAsset(BuTrainAsset)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveTrainAsset(BuTrainAsset buTrainAsset) throws Exception {
        BuTrainInfo trainInfo =
                buTrainInfoMapper.selectOne(
                        Wrappers.<BuTrainInfo>lambdaQuery()
                                .eq(BuTrainInfo::getTrainNo, buTrainAsset.getTrainId()));
        buTrainAsset.setTrainId(trainInfo.getId()).setLineId(trainInfo.getLineId());

        setStructTypeIfNull(buTrainAsset);
        setCodeAndWbsByParent(buTrainAsset);

        buTrainAsset.setAssetNo(RandomUtil.randomString(12));
        this.save(buTrainAsset);

        // 更新wbs
        WbsConf wbsConf = new WbsConf("bu_train_asset")
                .setCode("asset_no")
                .setFilter("train_id = '" + trainInfo.getId() + "'");
        wbsService.updateWbs(wbsConf);

        return true;
    }

    /**
     * @see IBuTrainAssetService#updateTrainAsset(BuTrainAsset) (BuTrainAsset)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTrainAsset(BuTrainAsset buTrainAsset) throws Exception {
        LambdaQueryWrapper<BuTrainInfo> trainInfoWrapper = new LambdaQueryWrapper<>();
        trainInfoWrapper.eq(BuTrainInfo::getTrainNo, buTrainAsset.getTrainId());
        BuTrainInfo trainInfo = buTrainInfoMapper.selectOne(trainInfoWrapper);
        buTrainAsset.setTrainId(trainInfo.getId()).setLineId(trainInfo.getLineId());

        setStructTypeIfNull(buTrainAsset);
        setCodeAndWbsByParent(buTrainAsset);

        buTrainAssetMapper.updateById(buTrainAsset);

        // 更新wbs
        WbsConf wbsConf = new WbsConf("bu_train_asset")
                .setCode("asset_no")
                .setFilter("train_id = '" + trainInfo.getId() + "'");
        wbsService.updateWbs(wbsConf);

        return true;
    }


    private void extractAssetTreeToList(List<BuTrainAsset> assetTree, List<BuTrainAsset> assetList) {
        if (CollectionUtils.isNotEmpty(assetTree)) {
            for (BuTrainAsset asset : assetTree) {
                List<BuTrainAsset> children = asset.getChildren();
                extractAssetTreeToList(children, assetList);

                asset.setChildren(new ArrayList<>());
                assetList.add(asset);
            }
        }
    }

    private void setSystemIdAndName(List<BuTrainAsset> assetList) {
        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = assetTypeCacheService.map(null);

        for (BuTrainAsset asset : assetList) {
            BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOMap.get(asset.getAssetTypeId());
            if (null != assetTypeBO) {
                asset.setSystemId(assetTypeBO.getSysId())
                        .setSystemName(assetTypeBO.getSysName());
            }
        }
    }

    private void extractAssetTypeListToTree(List<BuTrainAssetType> parentAssetTypeList, List<BuTrainAssetType> allAssetTypeList) {
        if (CollectionUtils.isEmpty(parentAssetTypeList)) {
            return;
        }

        for (BuTrainAssetType parent : parentAssetTypeList) {
            String parentId = parent.getId();
            List<BuTrainAssetType> children = allAssetTypeList.stream()
                    .filter(assetTypeItem -> parentId.equals(assetTypeItem.getParentId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(children)) {
                extractAssetTypeListToTree(children, allAssetTypeList);
            }
            parent.setChildren(children);
        }
    }

    private void extractAssetListToTree(List<BuTrainAsset> parentAssetList, List<BuTrainAsset> allAssetList) {
        if (CollectionUtils.isEmpty(parentAssetList)) {
            return;
        }

        for (BuTrainAsset parent : parentAssetList) {
            String parentId = parent.getId();
            List<BuTrainAsset> children = allAssetList.stream()
                    .filter(assetItem -> parentId.equals(assetItem.getParentId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(children)) {
                extractAssetListToTree(children, allAssetList);
            }
            parent.setChildren(children);
        }
    }

    private void recurseAssetTypeTreeToAssetList(List<BuTrainAssetType> assetTypeTree,
                                                 List<BuTrainAsset> assetList,
                                                 BuTrainAsset parent,
                                                 String brotherMaxCode) {
        if (CollectionUtils.isEmpty(assetTypeTree)) {
            return;
        }

        assetTypeTree.sort(Comparator.comparing(BuTrainAssetType::getSortNum, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTrainAssetType::getWbs, Comparator.nullsLast(Comparator.naturalOrder())));

        String parentId = null;
        String parentWbs = "";
        boolean codeNeedGetNext = true;
        if (null != parent) {
            parentId = parent.getId();
            parentWbs = parent.getWbs();

            if (StringUtils.isBlank(brotherMaxCode)) {
                brotherMaxCode = LevelCodeUtil.getFirstByParent(parent.getAssetNo());
                codeNeedGetNext = false;
            }
        }

        String code = brotherMaxCode;
        for (BuTrainAssetType assetType : assetTypeTree) {
            String assetTypeId = assetType.getId();
            int initNum = null == assetType.getInitNum() ? 1 : assetType.getInitNum();

            for (int i = 1; i <= initNum; i++) {
                String assetId = UUIDGenerator.generate();
                if (codeNeedGetNext) {
                    code = LevelCodeUtil.getNextByBrotherMax(code);
                } else {
                    codeNeedGetNext = true;
                }

                BuTrainAsset asset = new BuTrainAsset()
                        .setId(assetId)
                        .setAssetName(assetType.getName() + (initNum > 1 ? ("-" + i) : ""))
                        .setAssetNo(code)
                        .setWbs(parentWbs + (StringUtils.isBlank(parentWbs) ? "" : ".") + code)
                        .setParentId(parentId)
                        .setStructDetailId(null)
                        .setAssetTypeId(assetTypeId)
                        .setStructType(assetType.getStructType())
                        .setSubjunctive(null == assetType.getSubjunctive() ? 0 : assetType.getSubjunctive())
                        .setTurnover(null == assetType.getTurnover() ? 0 : assetType.getTurnover())
                        .setPlaceId(assetType.getPlaceId())
                        .setPlaceDesc(assetType.getPlaceDesc())
                        .setMaterialTypeId(assetType.getMaterialId())
                        .setRemark(assetType.getRemark())
                        .setStatus(null == assetType.getStatus() ? 0 : assetType.getStatus())
                        .setSortNo(assetType.getSortNum());
                assetList.add(asset);

                List<BuTrainAssetType> children = assetType.getChildren();
                if (CollectionUtils.isNotEmpty(children)) {
                    recurseAssetTypeTreeToAssetList(children, assetList, asset, LevelCodeUtil.getFirstByParent(asset.getAssetNo()));
                }
            }
        }
    }

//    private String getCopyCode(String code, List<String> existCodeList) {
//        String newCode = code + "-c";
//        if (existCodeList.contains(newCode)) {
//            return getCopyCode(newCode, existCodeList);
//        } else {
//            existCodeList.add(newCode);
//            return newCode;
//        }
//    }

    private void setStructTypeIfNull(BuTrainAsset asset) {
        Integer structType = asset.getStructType();
        String parentId = asset.getParentId();
        if (null == structType) {
            if (StringUtils.isBlank(parentId)) {
                asset.setStructType(1);
            } else {
                BuTrainAsset parent = buTrainAssetMapper.selectById(parentId);
                int childStructType = parent.getStructType() + 1;
                asset.setStructType(Math.min(childStructType, 5));
            }
        }
    }

    private void setCodeAndWbsByParent(BuTrainAsset asset) {
        String parentId = asset.getParentId();
        String parentWbs = "";
        if (StringUtils.isNotBlank(parentId)) {
            BuTrainAsset parent = buTrainAssetMapper.selectById(parentId);
            parentWbs = parent.getWbs();
        }

        String code = asset.getAssetNo();
        if (StringUtils.isNotBlank(code)) {
            LevelCodeUtil.checkLengthAlphanumeric(code);
        } else {
//            String brotherMaxCode = buTrainAssetMapper.selectBrotherMaxCodeByParentId(parentId);
            String brotherMaxCode = getBrotherMaxCodeByParentIdAndTrainId(parentId, asset.getTrainId());
            code = LevelCodeUtil.getNextByBrotherMax(brotherMaxCode);
        }

        setCodeAndWbs(asset, code, parentWbs);
    }

    private String getBrotherMaxCodeByParentIdAndTrainId(String parentId, String trainId) {
        LambdaQueryWrapper<BuTrainAsset> wrapper = new LambdaQueryWrapper<BuTrainAsset>()
                .eq(BuTrainAsset::getTrainId, trainId);
        if (StringUtils.isNotBlank(parentId)) {
            wrapper.eq(BuTrainAsset::getParentId, parentId);
        } else {
            wrapper.and(andWrapper -> andWrapper
                    .isNull(BuTrainAsset::getParentId)
                    .or()
                    .eq(BuTrainAsset::getParentId, ""));
        }
        wrapper.select(BuTrainAsset::getAssetNo)
                .orderByDesc(BuTrainAsset::getAssetNo);

        List<BuTrainAsset> codeList = buTrainAssetMapper.selectList(wrapper);

        if (CollectionUtils.isNotEmpty(codeList)) {
            return codeList.get(0).getAssetNo();
        } else {
            return "";
        }
    }

    private BuTrainAsset transformToTrainAsset(BuTrainStructureDetail structureDetail,
                                               Map<String, String> structDetailIdTrainAssetIdMap,
                                               BuTrainInfo trainInfo) {
        String assetId = structDetailIdTrainAssetIdMap.get(structureDetail.getId());
        String assetParentId = StringUtils.isBlank(structureDetail.getParentId()) ? null : structDetailIdTrainAssetIdMap.get(structureDetail.getParentId());

        return new BuTrainAsset()
                .setId(assetId)
                .setAssetName(structureDetail.getName())
                .setAssetNo(trainInfo.getTrainNo() + structureDetail.getCode()) // 设备编码为车辆好+车辆结构明细编码
                .setParentId(assetParentId)
                .setStructDetailId(structureDetail.getId())
                .setAssetTypeId(structureDetail.getAssetTypeId())
                .setStructType(structureDetail.getStructType())
                .setLineId(trainInfo.getLineId())
                .setTrainId(trainInfo.getId())
                .setSubjunctive(null == structureDetail.getSubjunctive() ? 0 : structureDetail.getSubjunctive())
                .setTurnover(null == structureDetail.getTurnover() ? 0 : structureDetail.getTurnover())
                .setPlaceId(structureDetail.getPlaceId())
                .setPlaceDesc(structureDetail.getPlaceDesc())
                .setStatus(null == structureDetail.getStatus() ? 0 : structureDetail.getStatus())
                .setWbs(structureDetail.getWbs())
                .setRemark(structureDetail.getRemark())
                .setSortNo(structureDetail.getSortNo());
    }


    private void setTopListCodeAndWbs(List<BuTrainAsset> topList, BuTrainInfo trainInfo) {
        if (CollectionUtils.isEmpty(topList)) {
            return;
        }

        topList.sort(Comparator.comparing(BuTrainAsset::getSortNo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTrainAsset::getWbs, Comparator.nullsLast(Comparator.naturalOrder())));

//        String topMaxCode = buTrainAssetMapper.selectBrotherMaxCodeByParentId(null);
        // 顶级、结构类型=其他的，为车厢，编码不重排（使用车辆结构里的车厢的编码），仅加前缀CL+车号
//        String topMaxCode = getBrotherMaxCodeByParentIdAndTrainId(null, trainId);
        for (BuTrainAsset top : topList) {
            // 龚乾坤注释， 设备的编码已在构造车辆设备记录时生成好了，为：车辆号+车辆结构明细编码
           /* String assetNo = top.getAssetNo();
            assetNo = "CL" + trainInfo.getTrainNo() + assetNo;
            top.setAssetNo(assetNo)
                    .setWbs(assetNo);*/
            // 顶级记录的wbs编码为 记录的编码
            top.setWbs(top.getAssetNo());

//            topMaxCode = LevelCodeUtil.getNextByBrotherMax(topMaxCode);
//            top.setAssetNo(topMaxCode)
//                    .setWbs(topMaxCode);
        }
    }

    private void sortListAndSetCodeWbs(List<BuTrainAsset> parentList, BuTrainAsset currentParent, List<BuTrainAsset> allAssetList) {
        if (CollectionUtils.isEmpty(parentList)) {
            return;
        }

        parentList.sort(Comparator.comparing(BuTrainAsset::getSortNo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTrainAsset::getWbs, Comparator.nullsLast(Comparator.naturalOrder())));

        String parentCode = null == currentParent ? "" : currentParent.getAssetNo();
        String parentWbs = null == currentParent ? "" : currentParent.getWbs();
        String code = LevelCodeUtil.getFirstByParent(parentCode);
        for (int i = 0; i < parentList.size(); i++) {
            BuTrainAsset asset = parentList.get(i);
            if (i != 0) {
                code = LevelCodeUtil.getNextByBrotherMax(code);
            }

            setCodeAndWbs(asset, code, parentWbs);
        }

        for (BuTrainAsset parent : parentList) {
            List<BuTrainAsset> children = allAssetList.stream()
                    .filter(asset -> parent.getId().equals(asset.getParentId()))
                    .collect(Collectors.toList());
            sortListAndSetCodeWbs(children, parent, allAssetList);
        }
    }

    private void setCodeAndWbs(BuTrainAsset asset, String code, String parentWbs) {
        asset.setAssetNo(code)
                .setWbs(parentWbs + (StringUtils.isBlank(parentWbs) ? "" : ".") + code);
    }

}
