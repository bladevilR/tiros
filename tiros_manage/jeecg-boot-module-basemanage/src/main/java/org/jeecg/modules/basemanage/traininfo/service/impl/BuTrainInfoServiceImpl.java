package org.jeecg.modules.basemanage.traininfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.mapper.BuMtrLineMapper;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAsset;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainInfo;
import org.jeecg.modules.basemanage.traininfo.entity.LineTrainTree;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainInfoQueryVO;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainInfoMapper;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车辆结构，平时在界面显示时从车辆结构中查数据，只有在保存业务数据要用到具体的设备时，才用对应的：结构id+车辆ID去查到 服务实现类
 *
 * @author 喻有根
 * @since 2020-07-10
 */
@Slf4j
@Service
public class BuTrainInfoServiceImpl extends ServiceImpl<BuTrainInfoMapper, BuTrainInfo> implements IBuTrainInfoService {

    @Resource
    private BuTrainInfoMapper buTrainInfoMapper;
    @Resource
    private BuTrainAssetMapper buTrainAssetMapper;
    @Resource
    private BuMtrLineMapper buMtrLineMapper;


    /**
     * @see IBuTrainInfoService#pageTrainInfo(BuTrainInfoQueryVO, Page)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Page<BuTrainInfo> pageTrainInfo(BuTrainInfoQueryVO queryVO, Page<BuTrainInfo> page) throws Exception {
        //TODO-zhaiyantao 2021/2/24 15:20 根据当前人员所属车辆段过滤
        return buTrainInfoMapper.selectPageByCondition(page, queryVO);
    }

    /**
     * @see IBuTrainInfoService#getTrainInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuTrainInfo getTrainInfo(String trainNo) throws Exception {
        return buTrainInfoMapper.selectByTrainNo(trainNo);
    }

    /**
     * @see IBuTrainInfoService#saveTrainInfo(BuTrainInfo)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveTrainInfo(BuTrainInfo buTrainInfo) throws Exception {
        BuMtrLine line = buMtrLineMapper.selectById(buTrainInfo.getLineId());
        buTrainInfo.setTrainTypeId(line.getTrainTypeId());

        if (isTrainNoRepeated(buTrainInfo)) {
            throw new JeecgBootException("车辆编号重复");
        }

        buTrainInfoMapper.insert(buTrainInfo);

        return true;
    }

    /**
     * @see IBuTrainInfoService#updateTrainInfo(BuTrainInfo)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTrainInfo(BuTrainInfo buTrainInfo) throws Exception {
        BuMtrLine line = buMtrLineMapper.selectById(buTrainInfo.getLineId());
        buTrainInfo.setTrainTypeId(line.getTrainTypeId());

        if (isTrainNoRepeated(buTrainInfo)) {
            throw new JeecgBootException("车辆编号重复");
        }

        buTrainInfoMapper.updateById(buTrainInfo);

        return true;
    }

    /**
     * @see IBuTrainInfoService#deleteBatchByTrainInfoIds(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchByTrainInfoIds(String trainInfoIds) {
        List<String> trainInfoIdList = Arrays.asList(trainInfoIds.split(","));

        // 删除车辆设备
        LambdaQueryWrapper<BuTrainAsset> assetWrapper = new LambdaQueryWrapper<>();
        assetWrapper.in(BuTrainAsset::getTrainId, trainInfoIdList);
        buTrainAssetMapper.delete(assetWrapper);

        // 删除车辆信息
        buTrainInfoMapper.deleteBatchIds(trainInfoIdList);

        return true;
    }

    /**
     * @see IBuTrainInfoService#isExistTrainCode(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public boolean isExistTrainCode(String code) {
        LambdaQueryWrapper<BuTrainInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuTrainInfo::getTrainNo, code);
        Integer count = buTrainInfoMapper.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * @see IBuTrainInfoService#getLineTrainTree()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<LineTrainTree> getLineTrainTree() throws Exception {
        List<BuMtrLine> lineList = buMtrLineMapper.selectList(Wrappers.emptyWrapper());
        List<BuTrainInfo> trainInfoList = buTrainInfoMapper.selectList(Wrappers.emptyWrapper());
        if (null == lineList) {
            lineList = new ArrayList<>();
        }
        if (null == trainInfoList) {
            trainInfoList = new ArrayList<>();
        }
        lineList.sort(Comparator.comparing(BuMtrLine::getCompanyId, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(BuMtrLine::getLineNum, Comparator.nullsLast(Comparator.naturalOrder())));
        trainInfoList.sort(Comparator.comparing(BuTrainInfo::getTrainNo, Comparator.nullsLast(Comparator.naturalOrder())));

        List<LineTrainTree> lineTreeList = new ArrayList<>();
        for (BuMtrLine line : lineList) {
            List<BuTrainInfo> trainList =
                    trainInfoList.stream()
                            .filter(train -> line.getLineId().equals(train.getLineId()))
                            .collect(Collectors.toList());
            List<LineTrainTree> trainTreeList = new ArrayList<>();
            for (BuTrainInfo trainInfo : trainList) {
                LineTrainTree trainTree =
                        new LineTrainTree()
                                .setId(trainInfo.getId())
                                .setName(trainInfo.getTrainNo())
                                .setType(2)
                                .setDisabled(false)
                                .setTrainTypeId(trainInfo.getTrainTypeId());
                trainTreeList.add(trainTree);
            }

            LineTrainTree lineTree =
                    new LineTrainTree()
                            .setId(line.getLineId())
                            .setName(line.getLineName())
                            .setType(1)
                            .setDisabled(true)
                            .setChildren(trainTreeList);
            lineTreeList.add(lineTree);
        }

        return lineTreeList;
    }

    /**
     * @see IBuTrainInfoService#updateTrainMileage(String, Double)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTrainMileage(String trainNo, Double mileage) throws Exception {
        BuTrainInfo trainInfo = buTrainInfoMapper.selectByTrainNo(trainNo);
        if (null != trainInfo) {
            trainInfo.setMileage(mileage);
            buTrainInfoMapper.updateById(trainInfo);
        }

        return true;
    }


    private boolean isTrainNoRepeated(BuTrainInfo buTrainInfo) {
        LambdaQueryWrapper<BuTrainInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuTrainInfo::getTrainNo, buTrainInfo.getTrainNo());
        List<BuTrainInfo> buTrainInfoList = buTrainInfoMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(buTrainInfoList)) {
            return false;
        }
        if (StringUtils.isBlank(buTrainInfo.getId())) {
            return true;
        }
        return !buTrainInfo.getId().equals(buTrainInfoList.get(0).getId());
    }

}
