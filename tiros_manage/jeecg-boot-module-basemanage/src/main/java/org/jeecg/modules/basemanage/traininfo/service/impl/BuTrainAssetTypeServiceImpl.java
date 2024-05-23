package org.jeecg.modules.basemanage.traininfo.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.LevelCodeUtil;
import org.jeecg.common.tiros.wbs.entity.WbsConf;
import org.jeecg.common.tiros.wbs.service.WbsService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeQueryVO;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetTypeMapper;
import org.jeecg.modules.basemanage.traininfo.service.BuTrainAssetTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备类型结构 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
@Service
public class BuTrainAssetTypeServiceImpl extends ServiceImpl<BuTrainAssetTypeMapper, BuTrainAssetType> implements BuTrainAssetTypeService {

    @Resource
    private BuTrainAssetTypeMapper buTrainAssetTypeMapper;
    @Resource
    private WbsService wbsService;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuTrainAssetTypeService#selectTrainAssetTypeChildren(BuTrainAssetTypeQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainAssetType> selectTrainAssetTypeChildren(BuTrainAssetTypeQueryVO queryVO) throws Exception {
//        for (BuTrainAssetType assetType : assetTypeList) {
//            recurseAddChild(assetType, assetTypeList);
//        }
//
//        // 如果同时含父节点和子节点，去掉所有子节点
//        Set<String> assetTypeIdSet = new HashSet<>();
//        recurseAddAssetTypeId(assetTypeList, assetTypeIdSet);
//
//        List<BuTrainAssetType> resultList = assetTypeList.stream()
//                .filter(assetType -> !assetTypeIdSet.contains(assetType.getParentId()))
//                .collect(Collectors.toList());
//        if (null == queryVO.getNeedChildren() || !queryVO.getNeedChildren()) {
//            resultList.forEach(assetType -> assetType.setChildren(new ArrayList<>()));
//        }
        List<BuTrainAssetType> assetTypeList = buTrainAssetTypeMapper.selectListByBuTrainAssetTypeQueryVO(queryVO);

      /* if (StringUtils.isNotBlank(queryVO.getSearchText())) {
           for (BuTrainAssetType assetType : assetTypeList) {
               assetType.setHasChildren(null);
           }
       }*/

        return assetTypeList;
    }

    /**
     * @see BuTrainAssetTypeService#getAssetTypeById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuTrainAssetType getAssetTypeById(String id) throws Exception {
        return buTrainAssetTypeMapper.selectAssetTypeById(id);
    }

    /**
     * @see BuTrainAssetTypeService#saveAssetType(BuTrainAssetType)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveAssetType(BuTrainAssetType trainAssetType) {
        setStructTypeIfNull(trainAssetType);
        setCodeAndWbsByParent(trainAssetType);

        buTrainAssetTypeMapper.insert(trainAssetType);

        // 注释原因：已经根据上级设置了编码和wbs，无需再更新整个表数据
//        // 更新wbs
//        WbsConf wbsConf = new WbsConf("bu_train_asset_type")
//                .setFilter("train_type_id = '" + trainAssetType.getTrainTypeId() + "'");
//        wbsService.updateWbs(wbsConf);

        // 更新设备类型缓存
        updateAssetTypeCachesByNewThread();

        return true;
    }

    /**
     * @see BuTrainAssetTypeService#updateTrainAssetTypeById(BuTrainAssetType)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTrainAssetTypeById(BuTrainAssetType trainAssetType) {
        setStructTypeIfNull(trainAssetType);
        setCodeAndWbsByParent(trainAssetType);

        BuTrainAssetType dbTrainAssetType = buTrainAssetTypeMapper.selectById(trainAssetType.getId());
        buTrainAssetTypeMapper.updateById(trainAssetType);
        if (!dbTrainAssetType.getCode().equals(trainAssetType.getCode())) {
            // 更新wbs
            WbsConf wbsConf = new WbsConf("bu_train_asset_type")
                    .setFilter("train_type_id = '" + trainAssetType.getTrainTypeId() + "'");
            wbsService.updateWbs(wbsConf);
        }

        // 更新设备类型缓存
        updateAssetTypeCachesByNewThread();

        return true;
    }

    /**
     * @see BuTrainAssetTypeService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) {
        List<String> assetTypeIdList = Arrays.asList(ids.split(","));
        if (CollectionUtils.isEmpty(assetTypeIdList)) {
            return true;
        }

        List<String> wbsList = buTrainAssetTypeMapper.selectWbsListByIdList(assetTypeIdList);
        buTrainAssetTypeMapper.deleteAssetTypeAndChildrenByWbsList(wbsList);

        // 更新设备类型缓存
        updateAssetTypeCachesByNewThread();

        return true;
    }

    /**
     * @see BuTrainAssetTypeService#listTopSystemAssetType(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainAssetType> listTopSystemAssetType(String trainNo, String parentId) throws Exception {
        return buTrainAssetTypeMapper.selectTopSystemListByTrainNo(trainNo, parentId);
    }

    /**
     * @see BuTrainAssetTypeService#rewriteCodeAndWbsByTrainTypeId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean rewriteCodeAndWbsByTrainTypeId(String trainTypeId) throws Exception {
        LambdaQueryWrapper<BuTrainAssetType> wrapper = new LambdaQueryWrapper<BuTrainAssetType>()
                .eq(BuTrainAssetType::getTrainTypeId, trainTypeId)
                .orderByAsc(BuTrainAssetType::getSortNum)
                .orderByAsc(BuTrainAssetType::getWbs);
        List<BuTrainAssetType> assetTypeList = buTrainAssetTypeMapper.selectList(wrapper);

        // 先删除再重新插入
        LambdaQueryWrapper<BuTrainAssetType> deleteWrapper = new LambdaQueryWrapper<BuTrainAssetType>()
                .eq(BuTrainAssetType::getTrainTypeId, trainTypeId);
        buTrainAssetTypeMapper.delete(deleteWrapper);

        // 从顶级节点，一层层的设置code和wbs
        List<BuTrainAssetType> topList = assetTypeList.stream()
                .filter(assetType -> StringUtils.isBlank(assetType.getParentId()))
                .collect(Collectors.toList());
        setTopListCodeAndWbs(topList, trainTypeId);
        for (BuTrainAssetType top : topList) {
            List<BuTrainAssetType> topChildren = assetTypeList.stream()
                    .filter(assetType -> top.getId().equals(assetType.getParentId()))
                    .collect(Collectors.toList());
            sortListAndSetCodeWbs(topChildren, top, assetTypeList);
        }

        // 先删除再重新插入
        List<List<BuTrainAssetType>> batchSubList = DatabaseBatchSubUtil.batchSubList(assetTypeList);
        for (List<BuTrainAssetType> batchSub : batchSubList) {
            buTrainAssetTypeMapper.insertList(batchSub);
        }

        return true;
    }


    private void setStructTypeIfNull(BuTrainAssetType trainAssetType) {
        Integer structType = trainAssetType.getStructType();
        String parentId = trainAssetType.getParentId();
        if (null == structType) {
            if (StringUtils.isBlank(parentId)) {
                trainAssetType.setStructType(1);
            } else {
                BuTrainAssetType parent = buTrainAssetTypeMapper.selectById(parentId);
                int childStructType = parent.getStructType() + 1;
                trainAssetType.setStructType(Math.min(childStructType, 5));
            }
        }
    }

    private void updateAssetTypeCachesByNewThread() {
        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                assetTypeCacheService.update();
                assetTypeCacheService.updateSys(null);
            } catch (Exception ex) {
                log.error("修改设备类型数据时，开线程更新设备类型缓存，执行失败，原因如下：" + ex.getMessage());
                StringWriter stringWriter = new StringWriter();
                try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                    ex.printStackTrace(printWriter);
                }
                log.error(stringWriter.toString());
            }

        });
        singleExecutor.shutdown();
    }

    private void setTopListCodeAndWbs(List<BuTrainAssetType> topList, String trainTypeId) {
        String topMaxCode = getBrotherMaxCodeByParentIdAndTrainTypeId(null, trainTypeId);
//        String topMaxCode = buTrainAssetTypeMapper.selectBrotherMaxCodeByParentIdAndTrainTypeId(null, trainTypeId);
        for (BuTrainAssetType top : topList) {
            topMaxCode = LevelCodeUtil.getNextByBrotherMax(topMaxCode);
            top.setCode(topMaxCode)
                    .setWbs(topMaxCode);
        }
    }

    private String getBrotherMaxCodeByParentIdAndTrainTypeId(String parentId, String trainTypeId) {
        LambdaQueryWrapper<BuTrainAssetType> wrapper = new LambdaQueryWrapper<BuTrainAssetType>()
                .eq(BuTrainAssetType::getTrainTypeId, trainTypeId);
        if (StringUtils.isNotBlank(parentId)) {
            wrapper.eq(BuTrainAssetType::getParentId, parentId);
        } else {
            wrapper.and(andWrapper -> andWrapper
                    .isNull(BuTrainAssetType::getParentId)
                    .or()
                    .eq(BuTrainAssetType::getParentId, ""));
        }
        wrapper.select(BuTrainAssetType::getCode)
                .orderByDesc(BuTrainAssetType::getCode);

        List<BuTrainAssetType> codeList = buTrainAssetTypeMapper.selectList(wrapper);

        if (CollectionUtils.isNotEmpty(codeList)) {
            return codeList.get(0).getCode();
        } else {
            return "";
        }
    }

    private void sortListAndSetCodeWbs(List<BuTrainAssetType> parentList, BuTrainAssetType currentParent, List<BuTrainAssetType> allAssetTypeList) {
        if (CollectionUtils.isEmpty(parentList)) {
            return;
        }

        parentList.sort(Comparator.comparing(BuTrainAssetType::getSortNum, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTrainAssetType::getWbs, Comparator.nullsLast(Comparator.naturalOrder())));

        String parentCode = null == currentParent ? "" : currentParent.getCode();
        String parentWbs = null == currentParent ? "" : currentParent.getWbs();
        String code = LevelCodeUtil.getFirstByParent(parentCode);
        for (int i = 0; i < parentList.size(); i++) {
            BuTrainAssetType assetType = parentList.get(i);
            if (i != 0) {
                code = LevelCodeUtil.getNextByBrotherMax(code);
            }

            setCodeAndWbs(assetType, code, parentWbs);
        }

        for (BuTrainAssetType parent : parentList) {
            List<BuTrainAssetType> children = allAssetTypeList.stream()
                    .filter(assetType -> parent.getId().equals(assetType.getParentId()))
                    .collect(Collectors.toList());
            sortListAndSetCodeWbs(children, parent, allAssetTypeList);
        }
    }

    private void setCodeAndWbsByParent(BuTrainAssetType assetType) {
        String parentId = assetType.getParentId();
        String parentWbs = "";
        if (StringUtils.isNotBlank(parentId)) {
            BuTrainAssetType parent = buTrainAssetTypeMapper.selectById(parentId);
            parentWbs = parent.getWbs();
        }

        String code = assetType.getCode();
        if (StringUtils.isNotBlank(code)) {
            LevelCodeUtil.checkLengthAlphanumeric(code);
        } else {
//            String brotherMaxCode = buTrainAssetTypeMapper.selectBrotherMaxCodeByParentIdAndTrainTypeId(parentId, assetType.getTrainTypeId());
            String brotherMaxCode = getBrotherMaxCodeByParentIdAndTrainTypeId(parentId, assetType.getTrainTypeId());
            code = LevelCodeUtil.getNextByBrotherMax(brotherMaxCode);
        }

        setCodeAndWbs(assetType, code, parentWbs);
    }

    private void setCodeAndWbs(BuTrainAssetType assetType, String code, String parentWbs) {
        assetType.setCode(code)
                .setWbs(parentWbs + (StringUtils.isBlank(parentWbs) ? "" : ".") + code);
    }

}
