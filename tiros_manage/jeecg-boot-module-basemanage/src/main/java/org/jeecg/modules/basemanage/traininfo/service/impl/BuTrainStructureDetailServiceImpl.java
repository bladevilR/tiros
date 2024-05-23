package org.jeecg.modules.basemanage.traininfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.LevelCodeUtil;
import org.jeecg.common.tiros.wbs.entity.WbsConf;
import org.jeecg.common.tiros.wbs.service.WbsService;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructureDetail;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeCopyVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainStructureDetailQueryVO;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetTypeMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainStructureDetailMapper;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainStructureDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆结构明细，可以从车车型设备结构导入，导入的话应根据初始数量生成对应设备部件的数据 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
@Slf4j
@Service
public class BuTrainStructureDetailServiceImpl extends ServiceImpl<BuTrainStructureDetailMapper, BuTrainStructureDetail> implements IBuTrainStructureDetailService {

    @Resource
    private BuTrainStructureDetailMapper buTrainStructureDetailMapper;
    @Resource
    private BuTrainAssetTypeMapper buTrainAssetTypeMapper;
    @Resource
    private WbsService wbsService;


    /**
     * @see IBuTrainStructureDetailService#selectTrainStructureDetailChildren(BuTrainStructureDetailQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainStructureDetail> selectTrainStructureDetailChildren(BuTrainStructureDetailQueryVO queryVO) {
//        for (BuTrainStructureDetail detail : detailList) {
//            recurseAddChild(detail, detailList);
//        }
//
//        // 如果同时含父节点和子节点，去掉所有子节点
//        Set<String> detailIdSet = new HashSet<>();
//        recurseAddDetailId(detailList, detailIdSet);
//
//        List<BuTrainStructureDetail> resultList = detailList.stream()
//                .filter(detail -> !detailIdSet.contains(detail.getParentId()))
//                .collect(Collectors.toList());
//        if (null == queryVO.getNeedChildren() || !queryVO.getNeedChildren()) {
//            resultList.forEach(detail -> detail.setChildren(new ArrayList<>()));
//        }

        List<BuTrainStructureDetail> structureDetailList = buTrainStructureDetailMapper.selectListByBuTrainStructureDetailQueryVO(queryVO);

      /*  if (StringUtils.isNotBlank(queryVO.getSearchText())) {
            for (BuTrainStructureDetail structureDetail : structureDetailList) {
                structureDetail.setHasChildren(null);
            }
        }*/

        return structureDetailList;
    }

    /**
     * @see IBuTrainStructureDetailService#importAssetTypeToStructure(BuTrainAssetTypeCopyVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean importAssetTypeToStructure(BuTrainAssetTypeCopyVO copyVO) throws Exception {
        if (StringUtils.isBlank(copyVO.getChildId())) {
            throw new JeecgBootException("请选择要导入的设备类型");
        }

        String childId = copyVO.getChildId();
        String trainStructId = copyVO.getTrainStructId();
        String parentId = StringUtils.isBlank(copyVO.getParentId()) ? null : copyVO.getParentId();

//        // 查询所有已存在的编码
//        List<String> existCodeList = buTrainStructureDetailMapper.selectCodeListByTrainStructId(trainStructId);

        // 查询设备类型
        List<String> assetTypeIdList = Arrays.asList(childId.split(","));
        List<String> assetTypeWbsList = buTrainAssetTypeMapper.selectWbsListByIdList(assetTypeIdList);
        List<BuTrainAssetType> assetTypeList = buTrainAssetTypeMapper.selectAssetTypeAndChildrenAsListByWbsList(assetTypeWbsList);
        assetTypeList.sort(Comparator.comparing(BuTrainAssetType::getSortNum, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTrainAssetType::getWbs, Comparator.nullsLast(Comparator.naturalOrder())));

        // 转换成设备类型树
        List<String> allAssetTypeIdList = assetTypeList.stream()
                .map(BuTrainAssetType::getId)
                .collect(Collectors.toList());
        List<BuTrainAssetType> topAssetTypeList = assetTypeList.stream()
                .filter(assetType -> !allAssetTypeIdList.contains(assetType.getParentId()))
                .collect(Collectors.toList());
        extractAssetTypeListToTree(topAssetTypeList, assetTypeList);

        // 查询上级
        BuTrainStructureDetail parent = null;
        String brotherMaxCode = null;
        if (StringUtils.isNotBlank(parentId)) {
            parent = buTrainStructureDetailMapper.selectById(parentId);
            // brotherMaxCode = getBrotherMaxCodeByParentIdAndTrainStructId(parentId, trainStructId);
        }
        List<BuTrainStructureDetail> structDetailList = new ArrayList<>();
        // 转化成车辆结构明细信息
        recurseAssetTypeTreeToStructDetailList(topAssetTypeList, structDetailList,parent.getCode(), parent, brotherMaxCode);

        if (CollectionUtils.isNotEmpty(structDetailList)) {
            // 设置车辆结构id
            structDetailList.forEach(structureDetail -> structureDetail.setTrainStructId(trainStructId));
            // 保存车辆结构明细
            List<List<BuTrainStructureDetail>> batchSubList = DatabaseBatchSubUtil.batchSubList(structDetailList);
            for (List<BuTrainStructureDetail> batchSub : batchSubList) {
                buTrainStructureDetailMapper.insertList(batchSub);
            }
        }

        // 更新wbs
        WbsConf wbsConf = new WbsConf("bu_train_structure_detail")
                .setFilter("train_struct_id = '" + trainStructId + "'");
        wbsService.updateWbs(wbsConf);

        return true;
    }

    /**
     * @see IBuTrainStructureDetailService#saveTrainStructureDetail(BuTrainStructureDetail)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveTrainStructureDetail(BuTrainStructureDetail structureDetail) throws Exception {
        setStructTypeIfNull(structureDetail);
        setCodeAndWbsByParent(structureDetail);

        buTrainStructureDetailMapper.insert(structureDetail);

        // 更新wbs
        WbsConf wbsConf = new WbsConf("bu_train_structure_detail")
                .setFilter("train_struct_id = '" + structureDetail.getTrainStructId() + "'");
        wbsService.updateWbs(wbsConf);

        return true;
    }

    /**
     * @see IBuTrainStructureDetailService#updateTrainStructureDetail(BuTrainStructureDetail)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTrainStructureDetail(BuTrainStructureDetail structureDetail) throws Exception {
        setStructTypeIfNull(structureDetail);
        setCodeAndWbsByParent(structureDetail);

        buTrainStructureDetailMapper.updateById(structureDetail);

        BuTrainStructureDetail dbTrainStructureDetail = buTrainStructureDetailMapper.selectById(structureDetail.getId());
        if (!dbTrainStructureDetail.getCode().equals(structureDetail.getCode())) {
            // 更新wbs
            WbsConf wbsConf = new WbsConf("bu_train_structure_detail")
                    .setFilter("train_struct_id = '" + structureDetail.getTrainStructId() + "'");
            wbsService.updateWbs(wbsConf);
        }

        return true;
    }

    /**
     * @see IBuTrainStructureDetailService#deleteBatchByDetailIds(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchByDetailIds(String detailIds) throws Exception {
        List<String> detailIdList = Arrays.asList(detailIds.split(","));
        for (String id : detailIdList) {
            BuTrainStructureDetail structureDetail = buTrainStructureDetailMapper.selectById(id);
            if (null != structureDetail) {
                LambdaQueryWrapper<BuTrainStructureDetail> childrenWrapper = new LambdaQueryWrapper<>();
                childrenWrapper
                        .eq(BuTrainStructureDetail::getTrainStructId, structureDetail.getTrainStructId())
                        .likeRight(BuTrainStructureDetail::getWbs, structureDetail.getWbs());
                buTrainStructureDetailMapper.delete(childrenWrapper);
            }
        }
        return true;
    }

    /**
     * @see IBuTrainStructureDetailService#deleteBatchByTrainStructureId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchByTrainStructureId(String trainStructureId) throws Exception {
        LambdaQueryWrapper<BuTrainStructureDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(BuTrainStructureDetail::getTrainStructId, trainStructureId);
        buTrainStructureDetailMapper.delete(detailWrapper);
        return true;
    }


    private void setStructTypeIfNull(BuTrainStructureDetail trainStructureDetail) {
        Integer structType = trainStructureDetail.getStructType();
        String parentId = trainStructureDetail.getParentId();
        if (null == structType) {
            if (StringUtils.isBlank(parentId)) {
                trainStructureDetail.setStructType(1);
            } else {
                BuTrainStructureDetail parent = buTrainStructureDetailMapper.selectById(parentId);
                int childStructType = parent.getStructType() + 1;
                trainStructureDetail.setStructType(Math.min(childStructType, 5));
            }
        }
    }

    private void setCodeAndWbsByParent(BuTrainStructureDetail structureDetail) {
        String parentId = structureDetail.getParentId();
        String parentWbs = "";
        if (StringUtils.isNotBlank(parentId)) {
            BuTrainStructureDetail parent = buTrainStructureDetailMapper.selectById(parentId);
            parentWbs = parent.getWbs();
        }

        String code = structureDetail.getCode();
        if (StringUtils.isNotBlank(code)) {
             // LevelCodeUtil.checkLengthAlphanumeric(code);
            LevelCodeUtil.checkInputCodeFormat(code, parentWbs);
        }
 /*       else {
//            String brotherMaxCode = buTrainStructureDetailMapper.selectBrotherMaxCodeByParentId(parentId);
            String brotherMaxCode = getBrotherMaxCodeByParentIdAndTrainStructId(parentId, structureDetail.getTrainStructId());
            code = LevelCodeUtil.getNextByBrotherMax(brotherMaxCode);
        }*/

        setCodeAndWbs(structureDetail, code, parentWbs);
    }

    private String getBrotherMaxCodeByParentIdAndTrainStructId(String parentId, String trainStructId) {
        LambdaQueryWrapper<BuTrainStructureDetail> wrapper = new LambdaQueryWrapper<BuTrainStructureDetail>()
                .eq(BuTrainStructureDetail::getTrainStructId, trainStructId);
        if (StringUtils.isNotBlank(parentId)) {
            wrapper.eq(BuTrainStructureDetail::getParentId, parentId);
        } else {
            wrapper.and(andWrapper -> andWrapper
                    .isNull(BuTrainStructureDetail::getParentId)
                    .or()
                    .eq(BuTrainStructureDetail::getParentId, ""));
        }
        wrapper.select(BuTrainStructureDetail::getCode)
                .orderByDesc(BuTrainStructureDetail::getCode);

        List<BuTrainStructureDetail> codeList = buTrainStructureDetailMapper.selectList(wrapper);

        if (CollectionUtils.isNotEmpty(codeList)) {
            return codeList.get(0).getCode();
        } else {
            return "";
        }
    }

    private void setCodeAndWbs(BuTrainStructureDetail structureDetail, String code, String parentWbs) {
        structureDetail.setCode(code)
                .setWbs(parentWbs + (StringUtils.isBlank(parentWbs) ? "" : ".") + code);
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

    private void recurseAssetTypeTreeToStructDetailList(List<BuTrainAssetType> assetTypeTree,
                                                        List<BuTrainStructureDetail> structDetailList,
                                                        String codePrefix,
                                                        BuTrainStructureDetail parent,
                                                        String brotherMaxCode) {
        if (CollectionUtils.isEmpty(assetTypeTree)) {
            return;
        }

        assetTypeTree.sort(Comparator.comparing(BuTrainAssetType::getSortNum, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTrainAssetType::getWbs, Comparator.nullsLast(Comparator.naturalOrder())));

        String parentId = null;
        String parentWbs = "";
        if (null != parent) {
            parentId = parent.getId();
            parentWbs = parent.getWbs();
        }
      /*   龚乾坤注释， 这里车辆结构不需要重新生成编码，导入时直接上一个前缀（导入到的节点的编码）
        boolean codeNeedGetNext = true;
        if (null != parent) {
            parentId = parent.getId();
            parentWbs = parent.getWbs();

            if (StringUtils.isBlank(brotherMaxCode)) {
                brotherMaxCode = LevelCodeUtil.getFirstByParent(parent.getCode());
                codeNeedGetNext = false;
            }
        }

        String code = brotherMaxCode;
        */

        for (BuTrainAssetType assetType : assetTypeTree) {
            String assetTypeId = assetType.getId();
            int initNum = null == assetType.getInitNum() ? 1 : assetType.getInitNum();

            for (int i = 1; i <= initNum; i++) {
                String structDetailIdId = UUIDGenerator.generate();
                /*
                if (codeNeedGetNext) {
                    code = LevelCodeUtil.getNextByBrotherMax(code);
                } else {
                    codeNeedGetNext = true;
                }
                */

                String code = assetType.getCode();
                String name = assetType.getName();
                if (initNum > 1) {
                    code = code + "-" + i;
                    name = name + "-" + i;
                }

                BuTrainStructureDetail structureDetail = new BuTrainStructureDetail()
                        .setId(structDetailIdId)
                        .setAssetTypeId(assetTypeId)
                        .setCode(codePrefix + code)
                        .setWbs(parentWbs + (StringUtils.isBlank(parentWbs) ? "" : ".") + code)
                        .setName(name)
                        .setParentId(parentId)
                        .setStructType(assetType.getStructType())
                        .setMaterialId(assetType.getMaterialId())
                        .setSubjunctive(null == assetType.getSubjunctive() ? 0 : assetType.getSubjunctive())
                        .setTurnover(null == assetType.getTurnover() ? 0 : assetType.getTurnover())
                        .setUnit(assetType.getUnit())
                        .setPlaceId(assetType.getPlaceId())
                        .setPlaceDesc(assetType.getPlaceDesc())
                        .setRemark(assetType.getRemark())
                        .setStatus(null == assetType.getStatus() ? 0 : assetType.getStatus())
                        .setSortNo(assetType.getSortNum());
                structDetailList.add(structureDetail);

                List<BuTrainAssetType> children = assetType.getChildren();
                if (CollectionUtils.isNotEmpty(children)) {
                    recurseAssetTypeTreeToStructDetailList(children, structDetailList,codePrefix, structureDetail, null);
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

}
