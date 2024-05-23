package org.jeecg.modules.basemanage.regu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.mapper.BuMtrLineMapper;
import org.jeecg.modules.basemanage.regu.bean.*;
import org.jeecg.modules.basemanage.regu.mapper.*;
import org.jeecg.modules.basemanage.regu.service.BuRepairReguInfoService;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlan;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanReguInfo;
import org.jeecg.modules.basemanage.tpplan.mapper.BuTpRepairPlanMapper;
import org.jeecg.modules.basemanage.tpplan.mapper.BuTpRepairPlanReguInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 规程信息 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
@Service
public class BuRepairReguInfoServiceImpl extends ServiceImpl<BuRepairReguInfoMapper, BuRepairReguInfo> implements BuRepairReguInfoService {

    @Resource
    private BuMtrLineMapper buMtrLineMapper;
    @Resource
    private BuRepairReguInfoMapper buRepairReguInfoMapper;
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
    private BuTpRepairPlanMapper buTpRepairPlanMapper;
    @Resource
    private BuTpRepairPlanReguInfoMapper buTpRepairPlanReguInfoMapper;


    /**
     * @see BuRepairReguInfoService#listReguInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairReguInfo> listReguInfo(String trainTypeId) throws Exception {
        return buRepairReguInfoMapper.selectListByTrainTypeId(trainTypeId);
    }

    /**
     * @see BuRepairReguInfoService#getReguInfoById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairReguInfo getReguInfoById(String reguInfoId) throws Exception {
        return buRepairReguInfoMapper.selectReguInfoById(reguInfoId);
    }

    /**
     * @see BuRepairReguInfoService#saveReguInfo(BuRepairReguInfo)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveReguInfo(BuRepairReguInfo reguInfo) {
        BuMtrLine line = buMtrLineMapper.selectById(reguInfo.getLineId());
        reguInfo.setTrainTypeId(line.getTrainTypeId());

        if (StringUtils.isNotBlank(reguInfo.getId())) {
            buRepairReguInfoMapper.updateById(reguInfo);
        } else {
            buRepairReguInfoMapper.insert(reguInfo);
        }

        return true;
    }

    /**
     * @see BuRepairReguInfoService#copyReguInfoByReguInfoId(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean copyReguInfoByReguInfoId(String reguInfoId) {
        // 复制规程
        BuRepairReguInfo reguInfo = buRepairReguInfoMapper.selectById(reguInfoId);
        String newReguInfoId = UUIDGenerator.generate();
        String newReguInfoName = getCopyReguInfoName(reguInfo.getName());
        reguInfo
                .setId(newReguInfoId)
                .setName(newReguInfoName)
                 .setCode(reguInfo.getCode()+"-copy");
        buRepairReguInfoMapper.insert(reguInfo);

        // 规程明细
        LambdaQueryWrapper<BuRepairReguDetail> detailWrapper = new LambdaQueryWrapper<BuRepairReguDetail>()
                .eq(BuRepairReguDetail::getReguId, reguInfoId);
        List<BuRepairReguDetail> oldReguDetailList = buRepairReguDetailMapper.selectList(detailWrapper);
        if (CollectionUtils.isNotEmpty(oldReguDetailList)) {
            List<String> oldReguDetailIdList = oldReguDetailList.stream()
                    .map(BuRepairReguDetail::getId)
                    .collect(Collectors.toList());
            // 旧新id映射
            Map<String, String> oldNewIdMap = new HashMap<>();
            for (String oldReguDetailId : oldReguDetailIdList) {
                oldNewIdMap.put(oldReguDetailId, UUIDGenerator.generate());
            }

            // 转换规程明细
            List<BuRepairReguDetail> newReguDetailList = new ArrayList<>();
            for (BuRepairReguDetail oldReguDetail : oldReguDetailList) {
                String oldId = oldReguDetail.getId();
                String oldParentId = oldReguDetail.getParentId();
                String newId = oldNewIdMap.get(oldId);
                String newParentId = StringUtils.isBlank(oldNewIdMap.get(oldParentId)) ? null : oldNewIdMap.get(oldParentId);

                BuRepairReguDetail newReguDetail = new BuRepairReguDetail();
                BeanUtils.copyProperties(oldReguDetail, newReguDetail);
                newReguDetail.setReguId(newReguInfoId)
                        .setId(newId)
                        .setParentId(newParentId);
                if (null == newReguDetail.getMeasure()) {
                    newReguDetail.setMeasure(0);
                }
                if (null == newReguDetail.getMustReplace()) {
                    newReguDetail.setMustReplace(0);
                }
                if (null == newReguDetail.getOutsource()) {
                    newReguDetail.setOutsource(0);
                }
                if (null == newReguDetail.getImportant()) {
                    newReguDetail.setImportant(0);
                }
                newReguDetailList.add(newReguDetail);
            }

            // 规程明细关联信息
            LambdaQueryWrapper<BuRepairReguTechFile> techFileWrapper = new LambdaQueryWrapper<BuRepairReguTechFile>()
                    .in(BuRepairReguTechFile::getReguDetailId, oldReguDetailIdList);
            List<BuRepairReguTechFile> techFileList = buRepairReguTechFileMapper.selectList(techFileWrapper);
            LambdaQueryWrapper<BuRepairReguMaterial> materialWrapper = new LambdaQueryWrapper<BuRepairReguMaterial>()
                    .in(BuRepairReguMaterial::getReguDetailId, oldReguDetailIdList);
            List<BuRepairReguMaterial> materialList = buRepairReguMaterialMapper.selectList(materialWrapper);
            LambdaQueryWrapper<BuRepairReguPerson> personWrapper = new LambdaQueryWrapper<BuRepairReguPerson>()
                    .in(BuRepairReguPerson::getReguDetailId, oldReguDetailIdList);
            List<BuRepairReguPerson> personList = buRepairReguPersonMapper.selectList(personWrapper);
            LambdaQueryWrapper<BuRepairReguTools> toolWrapper = new LambdaQueryWrapper<BuRepairReguTools>()
                    .in(BuRepairReguTools::getReguDetailId, oldReguDetailIdList);
            List<BuRepairReguTools> toolList = buRepairReguToolsMapper.selectList(toolWrapper);
            LambdaQueryWrapper<BuRepairReguTechBookDetail> techBookDetailWrapper = new LambdaQueryWrapper<BuRepairReguTechBookDetail>()
                    .in(BuRepairReguTechBookDetail::getReguDetailId, oldReguDetailIdList);
            List<BuRepairReguTechBookDetail> techBookDetailList = buRepairReguTechBookDetailMapper.selectList(techBookDetailWrapper);
            // 转换规程明细关联信息
            List<BuRepairReguTechFile> newTechFileList = transformTechFile(oldNewIdMap, techFileList);
            List<BuRepairReguMaterial> newMaterialList = transformMaterial(oldNewIdMap, materialList);
            List<BuRepairReguPerson> newPersonList = transformPerson(oldNewIdMap, personList);
            List<BuRepairReguTools> newToolsList = transformTool(oldNewIdMap, toolList);
            List<BuRepairReguTechBookDetail> newTechBookDetailList = transformTechBookDetail(oldNewIdMap, techBookDetailList);

            // 保存规程明细和关联信息
            if (CollectionUtils.isNotEmpty(newReguDetailList)) {
                buRepairReguDetailMapper.insertList(newReguDetailList);
            }
            if (CollectionUtils.isNotEmpty(newTechFileList)) {
                buRepairReguTechFileMapper.insertList(newTechFileList);
            }
            if (CollectionUtils.isNotEmpty(newMaterialList)) {
                buRepairReguMaterialMapper.insertList(newMaterialList);
            }
            if (CollectionUtils.isNotEmpty(newPersonList)) {
                buRepairReguPersonMapper.insertList(newPersonList);
            }
            if (CollectionUtils.isNotEmpty(newToolsList)) {
                buRepairReguToolsMapper.insertList(newToolsList);
            }
            if (CollectionUtils.isNotEmpty(newTechBookDetailList)) {
                buRepairReguTechBookDetailMapper.insertList(newTechBookDetailList);
            }
        }

        return true;
    }

    /**
     * @see BuRepairReguInfoService#deleteById(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteById(String reguId) {
        boolean relativeWithTpPlan = isRelativeWithTpPlan(reguId);
        if (relativeWithTpPlan) {
            throw new JeecgBootException("规程已关联计划模板，不能删除");
        }

        // 删除规程
        buRepairReguInfoMapper.deleteById(reguId);

        return true;
    }

    /**
     * @see BuRepairReguInfoService#isRelativeWithTpPlan(String)
     */
    @Override
    public boolean isRelativeWithTpPlan(String reguId) {
        if (StringUtils.isBlank(reguId)) {
            return false;
        }

        LambdaQueryWrapper<BuTpRepairPlan> tpRepairPlanWrapper = new LambdaQueryWrapper<BuTpRepairPlan>()
                .eq(BuTpRepairPlan::getReguId, reguId);
        Integer tpRepairPlanCount = buTpRepairPlanMapper.selectCount(tpRepairPlanWrapper);
        if (null != tpRepairPlanCount && tpRepairPlanCount > 0) {
            return true;
        }

        LambdaQueryWrapper<BuTpRepairPlanReguInfo> tpRepairPlanReguInfoWrapper = new LambdaQueryWrapper<BuTpRepairPlanReguInfo>()
                .eq(BuTpRepairPlanReguInfo::getReguId, reguId);
        Integer tpRepairPlanReguInfoCount = buTpRepairPlanReguInfoMapper.selectCount(tpRepairPlanReguInfoWrapper);
        return null != tpRepairPlanReguInfoCount && tpRepairPlanReguInfoCount > 0;
    }


    private String getCopyReguInfoName(String oldName) {
        String newName = oldName + "-复制";
        LambdaQueryWrapper<BuRepairReguInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuRepairReguInfo::getName, newName);
        Integer count = buRepairReguInfoMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            newName = getCopyReguInfoName(newName);
        }
        return newName;
    }

    private List<BuRepairReguTechFile> transformTechFile(Map<String, String> oldNewIdMap,
                                                         List<BuRepairReguTechFile> oldTechFileList) {
        if (CollectionUtils.isEmpty(oldTechFileList)) {
            return new ArrayList<>();
        }

        List<BuRepairReguTechFile> newTechFileList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(oldTechFileList)) {
            for (BuRepairReguTechFile oldTechFile : oldTechFileList) {
                BuRepairReguTechFile newTechFile = new BuRepairReguTechFile();
                BeanUtils.copyProperties(oldTechFile, newTechFile);
                newTechFile.setId(UUIDGenerator.generate())
                        .setReguDetailId(oldNewIdMap.get(oldTechFile.getReguDetailId()));
                newTechFileList.add(newTechFile);
            }
        }

        return newTechFileList;
    }

    private List<BuRepairReguMaterial> transformMaterial(Map<String, String> oldNewIdMap,
                                                         List<BuRepairReguMaterial> oldMaterialList) {
        if (CollectionUtils.isEmpty(oldMaterialList)) {
            return new ArrayList<>();
        }

        List<BuRepairReguMaterial> newMaterialList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(oldMaterialList)) {
            for (BuRepairReguMaterial oldMaterial : oldMaterialList) {
                BuRepairReguMaterial newMaterial = new BuRepairReguMaterial();
                BeanUtils.copyProperties(oldMaterial, newMaterial);
                newMaterial.setId(UUIDGenerator.generate())
                        .setReguDetailId(oldNewIdMap.get(oldMaterial.getReguDetailId()));
                if (null == newMaterial.getAmount()) {
                    newMaterial.setAmount(0D);
                }
                newMaterialList.add(newMaterial);
            }
        }

        return newMaterialList;
    }

    private List<BuRepairReguPerson> transformPerson(Map<String, String> oldNewIdMap,
                                                     List<BuRepairReguPerson> oldPersonList) {
        if (CollectionUtils.isEmpty(oldPersonList)) {
            return new ArrayList<>();
        }

        List<BuRepairReguPerson> newPersonList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(oldPersonList)) {
            for (BuRepairReguPerson oldPerson : oldPersonList) {
                BuRepairReguPerson newPerson = new BuRepairReguPerson();
                BeanUtils.copyProperties(oldPerson, newPerson);
                newPerson.setId(UUIDGenerator.generate())
                        .setReguDetailId(oldNewIdMap.get(oldPerson.getReguDetailId()));
                if (null == newPerson.getAmount()) {
                    newPerson.setAmount(0);
                }
                newPersonList.add(newPerson);
            }
        }

        return newPersonList;
    }

    private List<BuRepairReguTools> transformTool(Map<String, String> oldNewIdMap,
                                                  List<BuRepairReguTools> oldTooList) {
        if (CollectionUtils.isEmpty(oldTooList)) {
            return new ArrayList<>();
        }

        List<BuRepairReguTools> newToolList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(oldTooList)) {
            for (BuRepairReguTools oldTool : oldTooList) {
                BuRepairReguTools newTool = new BuRepairReguTools();
                BeanUtils.copyProperties(oldTool, newTool);
                newTool.setId(UUIDGenerator.generate())
                        .setReguDetailId(oldNewIdMap.get(oldTool.getReguDetailId()));
                if (null == newTool.getAmount()) {
                    newTool.setAmount(0D);
                }
                newToolList.add(newTool);
            }
        }

        return newToolList;
    }

    private List<BuRepairReguTechBookDetail> transformTechBookDetail(Map<String, String> oldNewIdMap,
                                                                     List<BuRepairReguTechBookDetail> oldTechBookDetailList) {
        if (CollectionUtils.isEmpty(oldTechBookDetailList)) {
            return new ArrayList<>();
        }

        List<BuRepairReguTechBookDetail> newTechBookDetailList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(oldTechBookDetailList)) {
            for (BuRepairReguTechBookDetail oldTechBookDetail : oldTechBookDetailList) {
                BuRepairReguTechBookDetail newTechBookDetail = new BuRepairReguTechBookDetail();
                BeanUtils.copyProperties(oldTechBookDetail, newTechBookDetail);
                newTechBookDetail.setId(UUIDGenerator.generate())
                        .setReguDetailId(oldNewIdMap.get(oldTechBookDetail.getReguDetailId()));
                newTechBookDetailList.add(newTechBookDetail);
            }
        }

        return newTechBookDetailList;
    }

}
