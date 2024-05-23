package org.jeecg.modules.third.trans.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.trans.bean.BuMaximoTransData;
import org.jeecg.modules.third.trans.mapper.BuMaximoTransDataMapper;
import org.jeecg.modules.third.trans.service.BuMaximoTransDataService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * @see BuMaximoTransDataService#listAllNeedTransTransData()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaximoTransData> listAllNeedTransTransData() throws Exception {
        return buMaximoTransDataMapper.selectUnTransList();
    }

    /**
     * @see BuMaximoTransDataService#listNeedTransTransDataByTransDataIds(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaximoTransData> listNeedTransTransDataByTransDataIds(String transDataIds) throws Exception {
        if (StringUtils.isBlank(transDataIds)) {
            throw new RuntimeException("请输入同步数据id");
        }

        List<String> transDataIdList = Arrays.asList(transDataIds.split(","));

        List<BuMaximoTransData> transDataList = new ArrayList<>();
        List<List<String>> transDataIdBatchSubList = DatabaseBatchSubUtil.batchSubList(transDataIdList);
        for (List<String> transDataIdBatchSub : transDataIdBatchSubList) {
            List<BuMaximoTransData> subTransDataList = buMaximoTransDataMapper.selectBatchIds(transDataIdBatchSub);
            transDataList.addAll(subTransDataList);
        }

        return transDataList;
    }

    /**
     * @see BuMaximoTransDataService#updateTransData(BuMaximoTransData)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTransData(BuMaximoTransData transData) throws Exception {
        buMaximoTransDataMapper.updateById(transData);

        return true;
    }

    /**
     * @see BuMaximoTransDataService#updateTransDataTransId(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTransDataTransId(List<BuMaximoTransData> transDataList) throws Exception {
        if (CollectionUtils.isEmpty(transDataList)) {
            return true;
        }

        List<List<BuMaximoTransData>> batchSubList = DatabaseBatchSubUtil.batchSubList(transDataList);
        for (List<BuMaximoTransData> batchSub : batchSubList) {
            buMaximoTransDataMapper.updateTransId(batchSub);
        }

        return false;
    }

    /**
     * @see BuMaximoTransDataService#deleteTransDataByIdList(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteTransDataByIdList(List<String> idList) throws Exception {
        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }

        buMaximoTransDataMapper.deleteBatchIds(idList);

        return true;
    }

    /**
     * @see BuMaximoTransDataService#listAllTransferredNotSuccessMaterialTransData()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<BuMaximoTransData> listAllTransferredNotSuccessMaterialTransData() throws Exception {
        LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                .in(BuMaximoTransData::getType, Arrays.asList(3, 4))
                .ne(BuMaximoTransData::getSuccessStatus, 1)
                .isNotNull(BuMaximoTransData::getTransId);
        return buMaximoTransDataMapper.selectList(wrapper);
    }

    /**
     * @see BuMaximoTransDataService#listAllTransferredNotHandledMaterialTransData()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<BuMaximoTransData> listAllTransferredNotHandledMaterialTransData() throws Exception {
        LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                .in(BuMaximoTransData::getType, Arrays.asList(3, 4))
                .ne(BuMaximoTransData::getHandleStatus, 1)
                .isNotNull(BuMaximoTransData::getTransId);
        return buMaximoTransDataMapper.selectList(wrapper);
    }

    /**
     * @see BuMaximoTransDataService#listAllNotTransferredMaterialTransData()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<BuMaximoTransData> listAllNotTransferredMaterialTransData() throws Exception {
        LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                .in(BuMaximoTransData::getType, Arrays.asList(3, 4))
                .isNull(BuMaximoTransData::getTransId);
        return buMaximoTransDataMapper.selectList(wrapper);
    }

    /**
     * @see BuMaximoTransDataService#listAllNotTransferredOrderReplaceTransData()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<BuMaximoTransData> listAllNotTransferredOrderReplaceTransData() throws Exception {
        LambdaQueryWrapper<BuMaximoTransData> wrapper = new LambdaQueryWrapper<BuMaximoTransData>()
                .eq(BuMaximoTransData::getType, 7)
                .isNull(BuMaximoTransData::getTransId);
        return buMaximoTransDataMapper.selectList(wrapper);
    }

}
