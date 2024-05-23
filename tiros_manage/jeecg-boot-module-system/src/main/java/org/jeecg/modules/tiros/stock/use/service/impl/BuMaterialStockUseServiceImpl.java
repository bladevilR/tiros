package org.jeecg.modules.tiros.stock.use.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.stock.use.bean.BuMaterialStockUse;
import org.jeecg.common.tiros.stock.use.service.BuMaterialStockUseService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.modules.tiros.stock.use.mapper.BuMaterialStockUseMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-11-25
 */
@Slf4j
@Service
public class BuMaterialStockUseServiceImpl extends ServiceImpl<BuMaterialStockUseMapper, BuMaterialStockUse> implements BuMaterialStockUseService {

    @Resource
    private BuMaterialStockUseMapper buMaterialStockUseMapper;


    /**
     * @see BuMaterialStockUseService#addStockUseList(List)
     */
    @Override
    public boolean addStockUseList(List<BuMaterialStockUse> useList) {
        if (CollectionUtils.isEmpty(useList)) {
            return true;
        }

        List<List<BuMaterialStockUse>> batchSubList = DatabaseBatchSubUtil.batchSubList(useList);
        for (List<BuMaterialStockUse> batchSub : batchSubList) {
            buMaterialStockUseMapper.insertList(batchSub);
        }

        return true;
    }

    /**
     * @see BuMaterialStockUseService#deleteStockUseByAssignDetailIdList(List)
     */
    @Override
    public int deleteStockUseByAssignDetailIdList(List<String> assignDetailIdList) {
        if (CollectionUtils.isEmpty(assignDetailIdList)) {
            return 0;
        }

        int deleteSum = 0;
        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(assignDetailIdList);
        for (List<String> batchSub : batchSubList) {
            LambdaQueryWrapper<BuMaterialStockUse> stockUseWrapper = new LambdaQueryWrapper<BuMaterialStockUse>()
                    .in(BuMaterialStockUse::getAssignDetailId, batchSub);
            int delete = buMaterialStockUseMapper.delete(stockUseWrapper);
            deleteSum = deleteSum + delete;
        }

        return deleteSum;
    }

    /**
     * @see BuMaterialStockUseService#clearAllStockUse()
     */
    @Override
    public int clearAllStockUse() {
        return buMaterialStockUseMapper.delete(Wrappers.emptyWrapper());
    }

    /**
     * @see BuMaterialStockUseService#listStockUse(BuMaterialStockUse)
     */
    @Override
    public List<BuMaterialStockUse> listStockUse(BuMaterialStockUse stockUse) {
        LambdaQueryWrapper<BuMaterialStockUse> wrapper = new LambdaQueryWrapper<>();
        if (null != stockUse) {
            if (null != stockUse.getStockType()) {
                wrapper.eq(BuMaterialStockUse::getStockType, stockUse.getStockType());
            }
            if (StringUtils.isNotBlank(stockUse.getStockId())) {
                wrapper.eq(BuMaterialStockUse::getStockId, stockUse.getStockId());
            }
            if (StringUtils.isNotBlank(stockUse.getWarehouseId())) {
                wrapper.eq(BuMaterialStockUse::getWarehouseId, stockUse.getWarehouseId());
            }
            if (StringUtils.isNotBlank(stockUse.getMaterialTypeId())) {
                wrapper.eq(BuMaterialStockUse::getMaterialTypeId, stockUse.getMaterialTypeId());
            }
            if (StringUtils.isNotBlank(stockUse.getTradeNo())) {
                wrapper.eq(BuMaterialStockUse::getTradeNo, stockUse.getTradeNo());
            }
            if (StringUtils.isNotBlank(stockUse.getOrderCode())) {
                wrapper.eq(BuMaterialStockUse::getOrderCode, stockUse.getOrderCode());
            }
            if (StringUtils.isNotBlank(stockUse.getAssignDetailId())) {
                wrapper.eq(BuMaterialStockUse::getAssignDetailId, stockUse.getAssignDetailId());
            }
            if (StringUtils.isNotBlank(stockUse.getOrderMaterialActId())) {
                wrapper.eq(BuMaterialStockUse::getOrderMaterialActId, stockUse.getOrderMaterialActId());
            }
        }

        return buMaterialStockUseMapper.selectList(wrapper);
    }

    /**
     * @see BuMaterialStockUseService#listStockUseByMaterialTypeIdList(List)
     */
    @Override
    public List<BuMaterialStockUse> listStockUseByMaterialTypeIdList(List<String> materialTypeIdList) {
        if (CollectionUtils.isEmpty(materialTypeIdList)) {
            return new ArrayList<>();
        }

        List<BuMaterialStockUse> stockUseList = new ArrayList<>();
        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(new HashSet<>(materialTypeIdList)));
        for (List<String> batchSub : batchSubList) {
            List<BuMaterialStockUse> subStockUseList = buMaterialStockUseMapper.selectListByMaterialTypeIdList(batchSub);
            stockUseList.addAll(subStockUseList);
        }
        return stockUseList;
    }

    /**
     * @see BuMaterialStockUseService#listStockUseByMaterialTypeId(String)
     */
    @Override
    public List<BuMaterialStockUse> listStockUseByMaterialTypeId(String materialTypeId) {
        if (StringUtils.isBlank(materialTypeId)) {
            return new ArrayList<>();
        }

        return buMaterialStockUseMapper.selectListByMaterialTypeId(materialTypeId);
    }

}