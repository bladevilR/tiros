package org.jeecg.modules.basemanage.regu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.regu.bean.*;
import org.jeecg.modules.basemanage.regu.bean.vo.RepairReguDetailQueryVO;
import org.jeecg.modules.basemanage.regu.mapper.*;
import org.jeecg.modules.basemanage.regu.service.BuRepairReguDetailService;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanReguInfo;
import org.jeecg.modules.basemanage.tpplan.mapper.BuTpRepairPlanReguInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 规程明细 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
@Service
public class BuRepairReguDetailServiceImpl extends ServiceImpl<BuRepairReguDetailMapper, BuRepairReguDetail> implements BuRepairReguDetailService {

    @Resource
    private BuRepairReguDetailMapper buRepairReguDetailMapper;
    @Resource
    private BuRepairReguTechFileMapper buRepairReguTechFileMapper;
    @Resource
    private BuRepairReguMaterialMapper buRepairReguMaterialMapper;
    @Resource
    private BuRepairReguPersonMapper buRepairReguPersonMapper;
    @Resource
    private BuRepairReguToolsMapper buRepairReguToolsMapper;
    @Resource
    private BuRepairReguTechBookDetailMapper buRepairReguTechBookDetailMapper;
    @Resource
    private BuTpRepairPlanReguInfoMapper buTpRepairPlanReguInfoMapper;


    /**
     * @see BuRepairReguDetailService#listReguDetail(RepairReguDetailQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairReguDetail> listReguDetail(RepairReguDetailQueryVO queryVO) {
        // 没有制定规程，返回空
        if(StringUtils.isBlank(queryVO.getReguId())){
            return new ArrayList<>();
        }

        List<BuRepairReguDetail> reguDetailList = buRepairReguDetailMapper.selectListByRepairReguDetailQueryVO(queryVO);

        if (StringUtils.isNotBlank(queryVO.getTpPlanId())) {
            setReguDetailTpPlanRelatedInfo(reguDetailList, queryVO);

            Integer tpPlanRelated = queryVO.getTpPlanRelated();
            if (null != tpPlanRelated) {
                if (1 == tpPlanRelated) {
                    reguDetailList.removeIf(reguDetail -> 0 == reguDetail.getTpPlanRelated());
                } else if (0 == tpPlanRelated) {
                    reguDetailList.removeIf(reguDetail -> 1 == reguDetail.getTpPlanRelated());
                }
            }
        }

        return reguDetailList;
    }

    private void setReguDetailTpPlanRelatedInfo(List<BuRepairReguDetail> reguDetailList, RepairReguDetailQueryVO queryVO) {
        String reguId = queryVO.getReguId();
        String tpPlanId = queryVO.getTpPlanId();

        // 所有的规程明细
        LambdaQueryWrapper<BuRepairReguDetail> reguDetailWrapper = new LambdaQueryWrapper<BuRepairReguDetail>()
                .eq(BuRepairReguDetail::getReguId, reguId);
        List<BuRepairReguDetail> allReguDetailList = buRepairReguDetailMapper.selectList(reguDetailWrapper);
        // 计划模板已关联的规程明细
        List<BuTpRepairPlanReguInfo> tpRepairPlanReguInfoList = buTpRepairPlanReguInfoMapper.selectTpPlanRelatedList(reguId, tpPlanId);
        List<String> tpPlanRelatedReguDetailIdList = tpRepairPlanReguInfoList.stream()
                .map(BuTpRepairPlanReguInfo::getReguDetailId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        for (BuRepairReguDetail reguDetail : reguDetailList) {
            Integer type = reguDetail.getType();
            if (2 == type) {
                // 作业项目
                boolean tpPlanRelated = tpPlanRelatedReguDetailIdList.contains(reguDetail.getId());
                if (tpPlanRelated) {
                    reguDetail.setTpPlanRelated(1)
                            .setTpPlanRelatedInfo("是");
                } else {
                    reguDetail.setTpPlanRelated(0)
                            .setTpPlanRelatedInfo("否");
                }
            } else if (1 == type) {
                List<BuRepairReguDetail> reguDetailLeafNodeList = extractReguDetailLeafNodeList(reguDetail, allReguDetailList);
                int all = reguDetailLeafNodeList.size();
                int related = (int) reguDetailLeafNodeList.stream()
                        .filter(reguDetailLeafNode -> tpPlanRelatedReguDetailIdList.contains(reguDetailLeafNode.getId()))
                        .count();

                reguDetail.setTpPlanRelated((all > 0 && all == related) ? 1 : 0)
                        .setTpPlanRelatedInfo(related + "/" + all);
            }

        }
    }

    private List<BuRepairReguDetail> extractReguDetailLeafNodeList(BuRepairReguDetail parent, List<BuRepairReguDetail> reguDetailList) {
        if (CollectionUtils.isEmpty(reguDetailList)) {
            return new ArrayList<>();
        }

        List<BuRepairReguDetail> parentList;
        if (null == parent) {
            Set<String> reguDetailIdSet = reguDetailList.stream()
                    .map(BuRepairReguDetail::getId)
                    .collect(Collectors.toSet());
            // 父规程明细=顶级规程明细
            parentList = reguDetailList.stream()
                    .filter(planTask -> !reguDetailIdSet.contains(planTask.getParentId()))
                    .collect(Collectors.toList());
        } else {
            // 父规程明细=指定规程明细
            parentList = Collections.singletonList(parent);
        }

        Set<BuRepairReguDetail> resultSet = new HashSet<>();
        // 递归添加父规程明细下的叶子节点
        for (BuRepairReguDetail parentReguDetail : parentList) {
            addReguDetailAllLeafNode(parentReguDetail, reguDetailList, resultSet);
        }

        List<BuRepairReguDetail> resultList = new ArrayList<>(resultSet);
        resultList.sort(Comparator.comparing(BuRepairReguDetail::getNo, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }

    private void addReguDetailAllLeafNode(BuRepairReguDetail parent, List<BuRepairReguDetail> reguDetailList, Set<BuRepairReguDetail> resultSet) {
        String parentId = parent.getId();

        List<BuRepairReguDetail> children = reguDetailList.stream()
                .filter(planTask -> parentId.equals(planTask.getParentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(children)) {
            resultSet.add(parent);
        } else {
            for (BuRepairReguDetail child : children) {
                addReguDetailAllLeafNode(child, reguDetailList, resultSet);
            }
        }
    }

    /**
     * @see BuRepairReguDetailService#getReguDetailById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairReguDetail getReguDetailById(String id) throws Exception {
        return buRepairReguDetailMapper.selectReguDetailById(id);
    }

    /**
     * @see BuRepairReguDetailService#saveReguDetail(BuRepairReguDetail)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveReguDetail(BuRepairReguDetail reguDetail) throws Exception {
        if (StringUtils.isNotEmpty(reguDetail.getId())) {
            // 更新规程明细
            buRepairReguDetailMapper.updateById(reguDetail);

//            // 删除工艺文件
//            deleteReguDetailTechFile(reguDetail);
            if (reguDetail.getType() == 2) {
                // 删除关联信息
                deleteReguDetailRelation(Collections.singletonList(reguDetail.getId()));
            }
        } else {
            // 插入规程明细
            buRepairReguDetailMapper.insert(reguDetail);
        }

//        // 插入工艺文件
//        insertReguDetailTechFile(reguDetail);
        if (reguDetail.getType() == 2) {
            // 插入关联信息
            insertReguDetailRelation(reguDetail);
        }

        return true;
    }

    /**
     * @see BuRepairReguDetailService#deleteBatchByDetailIds(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchByDetailIds(String ids) throws Exception {
        List<String> detailIdList = Arrays.asList(ids.split(","));

        List<BuRepairReguDetail> reguDetailTreeList = buRepairReguDetailMapper.selectTreeByIdList(detailIdList);
        List<String> needDeleteIdList = new ArrayList<>();
        recurseAddChildIdList(reguDetailTreeList, needDeleteIdList);

        deleteReguDetailAndRelationByIdList(needDeleteIdList);

        return true;
    }

    /**
     * @see BuRepairReguDetailService#deleteByReguId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByReguId(String reguId) throws Exception {
//        List<BuRepairReguDetail> reguDetailList = buRepairReguDetailMapper.selectList(Wrappers.<BuRepairReguDetail>lambdaQuery()
//                .eq(BuRepairReguDetail::getReguId, reguInfoId));
//
//        // 如果关联了计划模板，返回错误信息
//        boolean relativeTpPlan = isRelativeWithTpPlan(reguInfoId);
//        if (relativeTpPlan) {
//            throw new JeecgBootException("规程已关联计划模板，不能删除");
//        }
//
//        if (CollectionUtils.isNotEmpty(reguDetailList)) {
//            List<String> detailIdList = reguDetailList.stream()
//                    .map(BuRepairReguDetail::getId)
//                    .collect(Collectors.toList());
//
//            deleteOthersByDetailIdList(detailIdList);
//            buRepairReguDetailMapper.deleteBatchIds(detailIdList);
//        }

        LambdaQueryWrapper<BuRepairReguDetail> reguDetailWrapper = new LambdaQueryWrapper<BuRepairReguDetail>()
                .eq(BuRepairReguDetail::getReguId, reguId)
                .select(BuRepairReguDetail::getId);
        List<BuRepairReguDetail> reguDetailList = buRepairReguDetailMapper.selectList(reguDetailWrapper);
        if (CollectionUtils.isNotEmpty(reguDetailList)) {
            List<String> detailIdList = reguDetailList.stream()
                    .map(BuRepairReguDetail::getId)
                    .collect(Collectors.toList());

            deleteReguDetailAndRelationByIdList(detailIdList);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importTechBook(ImportTechBook importTechBook) throws Exception {
        List<BuRepairReguTechBookDetail> reguTechBookDetailList = buRepairReguTechBookDetailMapper.selectList(Wrappers.<BuRepairReguTechBookDetail>lambdaQuery().
                eq(BuRepairReguTechBookDetail::getReguDetailId, importTechBook.getTargetReguItemId()));
        if (CollectionUtils.isEmpty(reguTechBookDetailList)) {
            throw new JeecgBootException("选择的规程作业项未关联作业指导书，请先关联!");
        }
        List<String> toReguItemIdList = importTechBook.getToReguItemIds();
        buRepairReguTechBookDetailMapper.delete(Wrappers.<BuRepairReguTechBookDetail>lambdaQuery().in(BuRepairReguTechBookDetail::getReguDetailId, toReguItemIdList));
        toReguItemIdList.stream().forEach(reguItemId -> {
            reguTechBookDetailList.stream().forEach(reguTechBookDetail -> {
                reguTechBookDetail.setId(UUIDGenerator.generate()).setReguDetailId(reguItemId);
            });
            buRepairReguTechBookDetailMapper.insertList(reguTechBookDetailList);
        });
        return true;
    }


    private void deleteReguDetailTechFile(BuRepairReguDetail reguDetail) {
        if (null == reguDetail) {
            return;
        }

        LambdaQueryWrapper<BuRepairReguTechFile> wrapper = new LambdaQueryWrapper<BuRepairReguTechFile>()
                .eq(BuRepairReguTechFile::getReguDetailId, reguDetail.getId());
        buRepairReguTechFileMapper.delete(wrapper);
    }

    private void insertReguDetailTechFile(BuRepairReguDetail reguDetail) {
        if (null == reguDetail) {
            return;
        }
        List<BuRepairReguTechFile> reguTechFileList = reguDetail.getReguTechFiles();
        if (CollectionUtils.isEmpty(reguTechFileList)) {
            return;
        }

        for (BuRepairReguTechFile reguTechFile : reguTechFileList) {
            reguTechFile.setReguDetailId(reguDetail.getId());
            buRepairReguTechFileMapper.insert(reguTechFile);
        }
    }

    private void deleteReguDetailRelation(List<String> reguDetailIdList) {
        if (CollectionUtils.isEmpty(reguDetailIdList)) {
            return;
        }

        // 删除工艺文件
        LambdaQueryWrapper<BuRepairReguTechFile> techFileWrapper = new LambdaQueryWrapper<BuRepairReguTechFile>()
                .in(BuRepairReguTechFile::getReguDetailId, reguDetailIdList);
        buRepairReguTechFileMapper.delete(techFileWrapper);
        // 删除工器具
        LambdaQueryWrapper<BuRepairReguTools> toolsWrapper = new LambdaQueryWrapper<BuRepairReguTools>()
                .in(BuRepairReguTools::getReguDetailId, reguDetailIdList);
        buRepairReguToolsMapper.delete(toolsWrapper);
        // 删除物料
        LambdaQueryWrapper<BuRepairReguMaterial> materialWrapper = new LambdaQueryWrapper<BuRepairReguMaterial>()
                .in(BuRepairReguMaterial::getReguDetailId, reguDetailIdList);
        buRepairReguMaterialMapper.delete(materialWrapper);
        // 删除人员
        LambdaQueryWrapper<BuRepairReguPerson> personWrapper = new LambdaQueryWrapper<BuRepairReguPerson>()
                .in(BuRepairReguPerson::getReguDetailId, reguDetailIdList);
        buRepairReguPersonMapper.delete(personWrapper);
        // 删除规程明细指导书明细关联
        LambdaQueryWrapper<BuRepairReguTechBookDetail> techBookDetailWrapper = new LambdaQueryWrapper<BuRepairReguTechBookDetail>()
                .in(BuRepairReguTechBookDetail::getReguDetailId, reguDetailIdList);
        buRepairReguTechBookDetailMapper.delete(techBookDetailWrapper);
    }

    private void insertReguDetailRelation(BuRepairReguDetail reguDetail) {
        if (null == reguDetail) {
            return;
        }

        setRelationReguDetailId(reguDetail);

        // 插入工艺文件
        if (CollectionUtils.isNotEmpty(reguDetail.getReguTechFiles())) {
            List<BuRepairReguTechFile> techFileList = reguDetail.getReguTechFiles();
            List<List<BuRepairReguTechFile>> batchSubList = DatabaseBatchSubUtil.batchSubList(techFileList);
            for (List<BuRepairReguTechFile> batchSub : batchSubList) {
                buRepairReguTechFileMapper.insertList(batchSub);
            }
        }
        // 插入工器具
        if (CollectionUtils.isNotEmpty(reguDetail.getReguTools())) {
            List<BuRepairReguTools> toolList = reguDetail.getReguTools();
            List<List<BuRepairReguTools>> batchSubList = DatabaseBatchSubUtil.batchSubList(toolList);
            for (List<BuRepairReguTools> batchSub : batchSubList) {
                buRepairReguToolsMapper.insertList(batchSub);
            }
        }
        // 插入物料
        if (CollectionUtils.isNotEmpty(reguDetail.getReguMaterials())) {
            List<BuRepairReguMaterial> materialList = reguDetail.getReguMaterials();
            for (BuRepairReguMaterial material : materialList) {
                if (null == material.getUseCategory()) {
                    material.setUseCategory(-1);
                }
            }
            List<List<BuRepairReguMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(materialList);
            for (List<BuRepairReguMaterial> batchSub : batchSubList) {
                buRepairReguMaterialMapper.insertList(batchSub);
            }
        }
        // 插入人员
        if (CollectionUtils.isNotEmpty(reguDetail.getReguPersons())) {
            List<BuRepairReguPerson> personList = reguDetail.getReguPersons();
            List<List<BuRepairReguPerson>> batchSubList = DatabaseBatchSubUtil.batchSubList(personList);
            for (List<BuRepairReguPerson> batchSub : batchSubList) {
                buRepairReguPersonMapper.insertList(batchSub);
            }
        }
        // 插入规程明细指导书明细关联
        if (CollectionUtils.isNotEmpty(reguDetail.getTechBookDetails())) {
            List<BuRepairReguTechBookDetail> techBookDetailList = reguDetail.getTechBookDetails();
            List<List<BuRepairReguTechBookDetail>> batchSubList = DatabaseBatchSubUtil.batchSubList(techBookDetailList);
            for (List<BuRepairReguTechBookDetail> batchSub : batchSubList) {
                buRepairReguTechBookDetailMapper.insertList(batchSub);
            }
        }
    }

    private void setRelationReguDetailId(BuRepairReguDetail reguDetail) {
        if (null == reguDetail) {
            return;
        }

        String reguDetailId = reguDetail.getId();

        List<BuRepairReguTechFile> techFileList = reguDetail.getReguTechFiles();
        if (CollectionUtils.isNotEmpty(techFileList)) {
            techFileList.forEach(techFile -> techFile.setReguDetailId(reguDetailId));
        }

        List<BuRepairReguTools> toolsList = reguDetail.getReguTools();
        if (CollectionUtils.isNotEmpty(toolsList)) {
            toolsList.forEach(tool -> tool.setReguDetailId(reguDetailId));
        }

        List<BuRepairReguMaterial> materialList = reguDetail.getReguMaterials();
        if (CollectionUtils.isNotEmpty(materialList)) {
            materialList.forEach(material -> material.setReguDetailId(reguDetailId));
        }

        List<BuRepairReguPerson> personList = reguDetail.getReguPersons();
        if (CollectionUtils.isNotEmpty(personList)) {
            personList.forEach(person -> person.setReguDetailId(reguDetailId));
        }
        List<BuRepairReguTechBookDetail> techBookDetailList = reguDetail.getTechBookDetails();
        if (CollectionUtils.isNotEmpty(techBookDetailList)) {
            techBookDetailList.forEach(techBookDetail -> techBookDetail.setReguDetailId(reguDetailId));
        }
    }

    private void recurseAddChildIdList(List<BuRepairReguDetail> reguDetailList, List<String> idList) {
        if (CollectionUtils.isNotEmpty(reguDetailList)) {
            for (BuRepairReguDetail reguDetail : reguDetailList) {
                if (null != reguDetail) {
                    idList.add(reguDetail.getId());
                    List<BuRepairReguDetail> children = reguDetail.getChildren();
                    recurseAddChildIdList(children, idList);
                }
            }
        }
    }

    private void deleteReguDetailAndRelationByIdList(List<String> needDeleteIdList) {
        // 如果关联了计划模板，返回错误信息
        boolean relativeWithTpPlan = isRelativeWithTpPlan(needDeleteIdList);
        if (relativeWithTpPlan) {
            throw new JeecgBootException("规程明细已关联计划模板，不能删除");
        }

        deleteReguDetailRelation(needDeleteIdList);
        buRepairReguDetailMapper.deleteBatchIds(needDeleteIdList);
    }

    private boolean isRelativeWithTpPlan(List<String> reguDetailIdList) {
        if (CollectionUtils.isEmpty(reguDetailIdList)) {
            return false;
        }

        LambdaQueryWrapper<BuTpRepairPlanReguInfo> tpRepairPlanReguInfoWrapper = new LambdaQueryWrapper<BuTpRepairPlanReguInfo>()
                .in(BuTpRepairPlanReguInfo::getReguDetailId, reguDetailIdList);
        Integer tpRepairPlanReguInfoCount = buTpRepairPlanReguInfoMapper.selectCount(tpRepairPlanReguInfoWrapper);
        if (null != tpRepairPlanReguInfoCount && tpRepairPlanReguInfoCount > 0) {
            return true;
        }

        return false;
    }

}
