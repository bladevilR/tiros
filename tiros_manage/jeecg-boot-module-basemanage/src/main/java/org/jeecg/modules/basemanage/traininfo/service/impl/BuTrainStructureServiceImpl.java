package org.jeecg.modules.basemanage.traininfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.LevelCodeUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.mapper.BuMtrLineMapper;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructure;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructureDetail;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainStructureVO;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainInfoMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainStructureDetailMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainStructureMapper;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainStructureService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆结构 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
@Slf4j
@Service
public class BuTrainStructureServiceImpl extends ServiceImpl<BuTrainStructureMapper, BuTrainStructure> implements IBuTrainStructureService {

    @Resource
    private BuTrainStructureMapper buTrainStructureMapper;
    @Resource
    private BuTrainStructureDetailMapper buTrainStructureDetailMapper;
    @Resource
    private BuMtrLineMapper buMtrLineMapper;
    @Resource
    private BuTrainInfoMapper buTrainInfoMapper;


    /**
     * @see IBuTrainStructureService#listAll(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTrainStructure> listAll(String lineId) {
        LambdaQueryWrapper<BuTrainStructure> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BuTrainStructure::getName);

        if (StringUtils.isNotBlank(lineId)) {
            wrapper.eq(BuTrainStructure::getLineId, lineId);
        }

        return buTrainStructureMapper.selectList(wrapper);

    }

    /**
     * @see IBuTrainStructureService#saveTrainStructure(BuTrainStructureVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveTrainStructure(BuTrainStructureVO trainStructureVO) throws Exception {
        BuTrainStructure buTrainStructure = new BuTrainStructure();
        BeanUtils.copyProperties(trainStructureVO, buTrainStructure);
        BuMtrLine line = buMtrLineMapper.selectById(trainStructureVO.getLineId());
        buTrainStructure.setTrainTypeId(line.getTrainTypeId());
        buTrainStructureMapper.insert(buTrainStructure);

        return true;
    }

    /**
     * @see IBuTrainStructureService#updateTrainStructure(BuTrainStructureVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTrainStructure(BuTrainStructureVO trainStructureVO) throws Exception {
        BuTrainStructure buTrainStructure = new BuTrainStructure();
        BeanUtils.copyProperties(trainStructureVO, buTrainStructure);
        BuMtrLine line = buMtrLineMapper.selectById(trainStructureVO.getLineId());
        buTrainStructure.setTrainTypeId(line.getTrainTypeId());
        buTrainStructureMapper.updateById(buTrainStructure);

        return true;
    }

    /**
     * @see IBuTrainStructureService#deleteBatchByStructureIds(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchByStructureIds(String structureIds) throws Exception {
        List<String> structureIdList = Arrays.asList(structureIds.split(","));

        // 更新车辆车型信息
        if (CollectionUtils.isNotEmpty(structureIdList)) {
            buTrainInfoMapper.updateStructIdNull(structureIdList);
        }

        // 删除结构明细
        LambdaQueryWrapper<BuTrainStructureDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(BuTrainStructureDetail::getTrainStructId, structureIdList);
        buTrainStructureDetailMapper.delete(detailWrapper);

        // 删除结构
        buTrainStructureMapper.deleteBatchIds(structureIdList);

        return true;
    }

    /**
     * @see IBuTrainStructureService#copyTrainStructureByStructureId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean copyTrainStructureByStructureId(String structureId) throws Exception {
        // 复制车辆结构
        BuTrainStructure structure = buTrainStructureMapper.selectById(structureId);
        String newStructureId = UUIDGenerator.generate();
        String newStructureCode = getCopyStructureCode(structure.getCode());
        String newStructureName = getCopyStructureName(structure.getName());
        structure
                .setId(newStructureId)
                .setCode(newStructureCode)
                .setName(newStructureName);
        buTrainStructureMapper.insert(structure);

        // 复制车辆结构明细
        LambdaQueryWrapper<BuTrainStructureDetail> detailWrapper = new LambdaQueryWrapper<BuTrainStructureDetail>()
                .eq(BuTrainStructureDetail::getTrainStructId, structureId)
                .orderByAsc(BuTrainStructureDetail::getSortNo)
                .orderByAsc(BuTrainStructureDetail::getWbs);
        List<BuTrainStructureDetail> oldStructureDetailList = buTrainStructureDetailMapper.selectList(detailWrapper);

        List<String> structureDetailIdList = oldStructureDetailList.stream()
                .map(BuTrainStructureDetail::getId)
                .collect(Collectors.toList());
        // 旧新id映射
        Map<String, String> oldNewIdMap = new HashMap<>();
        for (String structureDetailId : structureDetailIdList) {
            oldNewIdMap.put(structureDetailId, UUIDGenerator.generate());
        }

        List<BuTrainStructureDetail> newStructureDetailList = new ArrayList<>();
        for (BuTrainStructureDetail oldStructureDetail : oldStructureDetailList) {
            String oldId = oldStructureDetail.getId();
            String oldParentId = oldStructureDetail.getParentId();
            String newId = oldNewIdMap.get(oldId);
            String newParentId = StringUtils.isBlank(oldParentId) ? null : oldNewIdMap.get(oldParentId);

            BuTrainStructureDetail newStructureDetail = new BuTrainStructureDetail();
            BeanUtils.copyProperties(oldStructureDetail, newStructureDetail);

            newStructureDetail
                    .setTrainStructId(newStructureId)
                    .setId(newId)
                    .setParentId(newParentId);
            if (null == newStructureDetail.getStatus()) {
                newStructureDetail.setStatus(0);
            }
            if (null == newStructureDetail.getTurnover()) {
                newStructureDetail.setTurnover(0);
            }
            if (null == newStructureDetail.getSubjunctive()) {
                newStructureDetail.setSubjunctive(0);
            }

            newStructureDetailList.add(newStructureDetail);
        }

        if (CollectionUtils.isNotEmpty(newStructureDetailList)) {
            // 不需要重排，车辆结构的编码重复也没事，需要不重复了再根据规则设置
//            // 从顶级节点，一层层的设置code和wbs
//            List<BuTrainStructureDetail> topList = newStructureDetailList.stream()
//                    .filter(structureDetail -> StringUtils.isBlank(structureDetail.getParentId()))
//                    .collect(Collectors.toList());
//            setTopListCodeAndWbs(topList, structureId);
//            for (BuTrainStructureDetail top : topList) {
//                List<BuTrainStructureDetail> topChildren = newStructureDetailList.stream()
//                        .filter(structureDetail -> top.getId().equals(structureDetail.getParentId()))
//                        .collect(Collectors.toList());
//                sortListAndSetCodeWbs(topChildren, top, newStructureDetailList);
//            }

            List<List<BuTrainStructureDetail>> batchSubList = DatabaseBatchSubUtil.batchSubList(newStructureDetailList);
            for (List<BuTrainStructureDetail> batchSub : batchSubList) {
                buTrainStructureDetailMapper.insertList(batchSub);
            }
        }

        return true;
    }

    /**
     * @see IBuTrainStructureService#rewriteCodeAndWbsByStructId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean rewriteCodeAndWbsByStructId(String structId) throws Exception {
        LambdaQueryWrapper<BuTrainStructureDetail> wrapper = new LambdaQueryWrapper<BuTrainStructureDetail>()
                .eq(BuTrainStructureDetail::getTrainStructId, structId)
                .orderByAsc(BuTrainStructureDetail::getSortNo)
                .orderByAsc(BuTrainStructureDetail::getWbs);
        List<BuTrainStructureDetail> structureDetailList = buTrainStructureDetailMapper.selectList(wrapper);

        // 先删除再重新插入
        LambdaQueryWrapper<BuTrainStructureDetail> deleteWrapper = new LambdaQueryWrapper<BuTrainStructureDetail>()
                .eq(BuTrainStructureDetail::getTrainStructId, structId);
        buTrainStructureDetailMapper.delete(deleteWrapper);

        // 从顶级节点，一层层的设置code和wbs
        List<BuTrainStructureDetail> topList = structureDetailList.stream()
                .filter(structureDetail -> StringUtils.isBlank(structureDetail.getParentId()))
                .collect(Collectors.toList());
        // 顶级、结构类型=其他的，为车厢，编码不重排（使用车辆结构里的车厢的编码），仅加前缀CL+车号
//        setTopListCodeAndWbs(topList, structId);
        for (BuTrainStructureDetail top : topList) {
            List<BuTrainStructureDetail> topChildren = structureDetailList.stream()
                    .filter(structureDetail -> top.getId().equals(structureDetail.getParentId()))
                    .collect(Collectors.toList());
            sortListAndSetCodeWbs(topChildren, top, structureDetailList);
        }

        // 先删除再重新插入
        List<List<BuTrainStructureDetail>> batchSubList = DatabaseBatchSubUtil.batchSubList(structureDetailList);
        for (List<BuTrainStructureDetail> batchSub : batchSubList) {
            buTrainStructureDetailMapper.insertList(batchSub);
        }

        return true;
    }


    private String getCopyStructureName(String oldName) {
        String newName = oldName + "-复制";
        LambdaQueryWrapper<BuTrainStructure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuTrainStructure::getName, newName);
        Integer count = buTrainStructureMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            newName = getCopyStructureName(newName);
        }
        return newName;
    }

    private String getCopyStructureCode(String oldCode) {
        String newCode = oldCode + "-copy";
        LambdaQueryWrapper<BuTrainStructure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuTrainStructure::getCode, newCode);
        Integer count = buTrainStructureMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            newCode = getCopyStructureName(newCode);
        }
        return newCode;
    }

    private void setTopListCodeAndWbs(List<BuTrainStructureDetail> topList, String trainStructId) {
        if (CollectionUtils.isEmpty(topList)) {
            return;
        }

        topList.sort(Comparator.comparing(BuTrainStructureDetail::getSortNo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTrainStructureDetail::getWbs, Comparator.nullsLast(Comparator.naturalOrder())));
//        String topMaxCode = buTrainStructureDetailMapper.selectBrotherMaxCodeByParentId(null);
        String topMaxCode = getBrotherMaxCodeByParentIdAndTrainStructId(null, trainStructId);
        for (BuTrainStructureDetail top : topList) {
            topMaxCode = LevelCodeUtil.getNextByBrotherMax(topMaxCode);
            top.setCode(topMaxCode)
                    .setWbs(topMaxCode);
        }
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

    private void sortListAndSetCodeWbs(List<BuTrainStructureDetail> parentList, BuTrainStructureDetail currentParent, List<BuTrainStructureDetail> allStructureDetailList) {
        if (CollectionUtils.isEmpty(parentList)) {
            return;
        }

        parentList.sort(Comparator.comparing(BuTrainStructureDetail::getSortNo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuTrainStructureDetail::getWbs, Comparator.nullsLast(Comparator.naturalOrder())));

        String parentCode = null == currentParent ? "" : currentParent.getCode();
        String parentWbs = null == currentParent ? "" : currentParent.getWbs();
        String code = LevelCodeUtil.getFirstByParent(parentCode);
        for (int i = 0; i < parentList.size(); i++) {
            BuTrainStructureDetail structureDetail = parentList.get(i);
            if (i != 0) {
                code = LevelCodeUtil.getNextByBrotherMax(code);
            }

            setCodeAndWbs(structureDetail, code, parentWbs);
        }

        for (BuTrainStructureDetail parent : parentList) {
            List<BuTrainStructureDetail> children = allStructureDetailList.stream()
                    .filter(structureDetail -> parent.getId().equals(structureDetail.getParentId()))
                    .collect(Collectors.toList());
            sortListAndSetCodeWbs(children, parent, allStructureDetailList);
        }
    }

    private void setCodeAndWbs(BuTrainStructureDetail structureDetail, String code, String parentWbs) {
        structureDetail.setCode(code)
                .setWbs(parentWbs + (StringUtils.isBlank(parentWbs) ? "" : ".") + code);
    }

}
