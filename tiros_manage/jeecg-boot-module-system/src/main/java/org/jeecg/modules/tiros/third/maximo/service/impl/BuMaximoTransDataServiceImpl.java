package org.jeecg.modules.tiros.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.third.maximo.bean.BuMaximoTransData;
import org.jeecg.common.tiros.third.maximo.service.BuMaximoTransDataService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.modules.tiros.third.maximo.mapper.BuMaximoTransDataMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 检修系统maximo数据同步中间表 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-06
 */
@Service
public class BuMaximoTransDataServiceImpl extends ServiceImpl<BuMaximoTransDataMapper, BuMaximoTransData> implements BuMaximoTransDataService {

    @Resource
    private BuMaximoTransDataMapper buMaximoTransDataMapper;


    /**
     * @see BuMaximoTransDataService#addTransDataList(List)
     */
    @Override
    public boolean addTransDataList(List<BuMaximoTransData> dataList) throws Exception {
        if (CollectionUtils.isEmpty(dataList)) {
            return true;
        }

        List<BuMaximoTransData> needAddList = new ArrayList<>();

        Map<Integer, List<BuMaximoTransData>> typeTransDataListMap = dataList.stream()
                .collect(Collectors.groupingBy(BuMaximoTransData::getType));
        for (Map.Entry<Integer, List<BuMaximoTransData>> typeTransDataListEntry : typeTransDataListMap.entrySet()) {
            Integer type = typeTransDataListEntry.getKey();
            List<BuMaximoTransData> transDataList = typeTransDataListEntry.getValue();

            if (null == type || CollectionUtils.isEmpty(transDataList)) {
                continue;
            }

            // 查询已存在
            Set<String> objIdSet = transDataList.stream()
                    .map(BuMaximoTransData::getObjId)
                    .collect(Collectors.toSet());
            LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                    .eq(BuMaximoTransData::getType, type)
                    .in(BuMaximoTransData::getObjId, objIdSet);
            List<BuMaximoTransData> existList = buMaximoTransDataMapper.selectList(wrapper);
            Set<String> existObjIdSet = existList.stream()
                    .map(BuMaximoTransData::getObjId)
                    .collect(Collectors.toSet());

            // 保存数据
            for (BuMaximoTransData transData : transDataList) {
                // 不存在才添加
                if (!existObjIdSet.contains(transData.getObjId())) {
                    needAddList.add(transData);
                }

                existObjIdSet.add(transData.getObjId());
            }

        }

        if (CollectionUtils.isNotEmpty(needAddList)) {
            buMaximoTransDataMapper.insertList(needAddList);
        }

        return true;
    }

    /**
     * @see BuMaximoTransDataService#updateTransDataList(List)
     */
    @Override
    public boolean updateTransDataList(List<BuMaximoTransData> transDataList) throws Exception {
        if (CollectionUtils.isEmpty(transDataList)) {
            return true;
        }

        for (BuMaximoTransData transData : transDataList) {
            buMaximoTransDataMapper.updateById(transData);
        }

        return true;
    }

    /**
     * @see BuMaximoTransDataService#updateConsumeTransDataHandled(List)
     */
    @Override
    public int updateConsumeTransDataHandled(List<String> idList) throws Exception {
        if (CollectionUtils.isEmpty(idList)) {
            return 0;
        }

        LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                .in(BuMaximoTransData::getId, idList);
        BuMaximoTransData transData = new BuMaximoTransData()
                .setHandleStatus(1);
        return buMaximoTransDataMapper.update(transData, wrapper);
    }

    /**
     * @see BuMaximoTransDataService#listTransDataByIdList(List)
     */
    @Override
    public List<BuMaximoTransData> listTransDataByIdList(List<String> idList) throws Exception {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }

        List<BuMaximoTransData> resultList = new ArrayList<>();
        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(idList);
        for (List<String> batchSub : batchSubList) {
            List<BuMaximoTransData> subTransDataList = buMaximoTransDataMapper.selectBatchIds(batchSub);
            resultList.addAll(subTransDataList);
        }
        return resultList;
    }

    /**
     * @see BuMaximoTransDataService#listAllTransferredNotSuccessMaterialTransData()
     */
    @Override
    public List<BuMaximoTransData> listAllTransferredNotSuccessMaterialTransData() throws Exception {
        LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                .in(BuMaximoTransData::getType, Arrays.asList(3, 4))
                .eq(BuMaximoTransData::getSuccessStatus, 0)
                .isNotNull(BuMaximoTransData::getTransId);
        return buMaximoTransDataMapper.selectList(wrapper);
    }

    /**
     * @see BuMaximoTransDataService#listAllTransferredNotHandledMaterialTransData()
     */
    @Override
    public List<BuMaximoTransData> listAllTransferredNotHandledMaterialTransData() throws Exception {
        LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                .in(BuMaximoTransData::getType, Arrays.asList(3, 4))
                .eq(BuMaximoTransData::getHandleStatus, 0)
                .isNotNull(BuMaximoTransData::getTransId);
        return buMaximoTransDataMapper.selectList(wrapper);
    }

    /**
     * @see BuMaximoTransDataService#listAllNotTransferredTransDataByType(List)
     */
    @Override
    public List<BuMaximoTransData> listAllNotTransferredTransDataByType(List<Integer> typeList) throws Exception {
        if (CollectionUtils.isEmpty(typeList)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                .in(BuMaximoTransData::getType, typeList)
                .isNull(BuMaximoTransData::getTransId);
        return buMaximoTransDataMapper.selectList(wrapper);
    }

    /**
     * @see BuMaximoTransDataService#listWorkTimeTransDataByOrderCode(String)
     */
    @Override
    public List<BuMaximoTransData> listWorkTimeTransDataByOrderCode(String orderCode) throws Exception {
        if (StringUtils.isBlank(orderCode)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                .eq(BuMaximoTransData::getType, 6)
                .like(BuMaximoTransData::getObjJson, orderCode);
        return buMaximoTransDataMapper.selectList(wrapper);
    }

}
