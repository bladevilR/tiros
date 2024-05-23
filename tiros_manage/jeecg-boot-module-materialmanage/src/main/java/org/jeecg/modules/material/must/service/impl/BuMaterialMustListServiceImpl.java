package org.jeecg.modules.material.must.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeAttrMapper;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.bean.vo.BuMaterialMustListSetGroupVO;
import org.jeecg.modules.material.must.bean.vo.BuMaterialMustQueryVO;
import org.jeecg.modules.material.must.mapper.BuMaterialMustListMapper;
import org.jeecg.modules.material.must.service.BuMaterialMustListService;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.service.BuMaterialStockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2021-04-30
 */
@Service
public class BuMaterialMustListServiceImpl extends ServiceImpl<BuMaterialMustListMapper, BuMaterialMustList> implements BuMaterialMustListService {
    @Resource
    private BuMaterialMustListMapper buMaterialMustListMapper;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private BuMaterialTypeAttrMapper buMaterialTypeAttrMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private BuMaterialStockService buMaterialStockService;


    @Override
    public Page<BuMaterialMustList> pageMaterialMustList(BuMaterialMustQueryVO queryVO, Page<BuMaterialMustList> page) throws Exception {
        Page<BuMaterialMustList> pageResult = buMaterialMustListMapper.selectMaterialMustListPage(page, queryVO);

        setMaterialTypeStock(pageResult);

        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMaterialMustList(BuMaterialMustList materialMustList) throws Exception {
        createdMaterialType(materialMustList);
        setSysId(materialMustList);
        return buMaterialMustListMapper.insert(materialMustList) > 0;
    }

    private void setSysId(BuMaterialMustList materialMustList) {
        String location = materialMustList.getLocation();
        if (StringUtils.isNotEmpty(location)) {
            String assetTypeId = buMaterialMustListMapper.selectAssetTypeId(location);
            if (StringUtils.isNotEmpty(assetTypeId)) {
                Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = assetTypeCacheService.map(null);
                BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOMap.get(assetTypeId);
                if (null != assetTypeBO) {
                    // String sysId = materialTypeMapper.selectSysIdByPartId(location);
                    materialMustList.setSysId(assetTypeBO.getSysId());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterialMustList(BuMaterialMustList materialMustList) throws Exception {
        createdMaterialType(materialMustList);
        setSysId(materialMustList);
        updateMaterialType(materialMustList);
        return buMaterialMustListMapper.updateById(materialMustList) > 0;
    }

    private void updateMaterialType(BuMaterialMustList materialMustList) {
        BuMaterialType buMaterialType = new BuMaterialType()
                .setId(materialMustList.getMaterialTypeId())
                .setTheshold(new BigDecimal(materialMustList.getNeedAmount() * 3));
        buMaterialType.updateById();
        List<BuMaterialTypeAttr> attrList = buMaterialTypeAttrMapper.selectList(Wrappers.<BuMaterialTypeAttr>lambdaQuery().select(BuMaterialTypeAttr::getId).eq(BuMaterialTypeAttr::getMaterialTypeId, materialMustList.getMaterialTypeId()));
        if (CollectionUtils.isEmpty(attrList)) {
            BuMaterialTypeAttr materialTypeAttr = new BuMaterialTypeAttr()
                    .setId(UUIDGenerator.generate())
                    .setMaterialTypeId(materialMustList.getMaterialTypeId())
                    .setTheshold(buMaterialType.getTheshold().doubleValue());
            materialTypeAttr.insert();
        } else {
            BuMaterialTypeAttr materialTypeAttr = new BuMaterialTypeAttr()
                    .setMaterialTypeId(materialMustList.getMaterialTypeId())
                    .setTheshold(buMaterialType.getTheshold().doubleValue());
            materialTypeAttr.update(Wrappers.<BuMaterialTypeAttr>lambdaUpdate().eq(BuMaterialTypeAttr::getMaterialTypeId, materialMustList.getMaterialTypeId()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idsList = Arrays.asList(ids.split(","));
        return buMaterialMustListMapper.deleteBatchIds(idsList) > 0;
    }

    /**
     * @see BuMaterialMustListService#setMustListGroup(BuMaterialMustListSetGroupVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setMustListGroup(BuMaterialMustListSetGroupVO setGroupVO) throws Exception {
        String groupId = setGroupVO.getGroupId();
        List<String> idList = setGroupVO.getIdList();

        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }

        LambdaQueryWrapper<BuMaterialMustList> wrapper = new LambdaQueryWrapper<BuMaterialMustList>()
                .in(BuMaterialMustList::getId, idList);
        BuMaterialMustList mustList = new BuMaterialMustList()
                .setGroupId(groupId);
        buMaterialMustListMapper.update(mustList, wrapper);

        return true;
    }

    /**
     * @see BuMaterialMustListService#validMustList(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean validMustList(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuMaterialMustList> wrapper = new LambdaQueryWrapper<BuMaterialMustList>()
                .in(BuMaterialMustList::getId, idList);
        BuMaterialMustList plan = new BuMaterialMustList()
                .setStatus(1);
        buMaterialMustListMapper.update(plan, wrapper);

        return true;
    }

    /**
     * @see BuMaterialMustListService#invalidMustList(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean invalidMustList(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuMaterialMustList> wrapper = new LambdaQueryWrapper<BuMaterialMustList>()
                .in(BuMaterialMustList::getId, idList);
        BuMaterialMustList plan = new BuMaterialMustList()
                .setStatus(0);
        buMaterialMustListMapper.update(plan, wrapper);

        return true;
    }


    private void createdMaterialType(BuMaterialMustList materialMustList) {
        String materialTypeId = UUIDGenerator.generate();
        BuMaterialType localMaterialType = buMaterialTypeMapper.selectById(materialMustList.getMaterialTypeId());
        if (localMaterialType == null) {
            materialMustList.setMaterialTypeId(materialTypeId);
            BuMaterialType materialType = new BuMaterialType();
            materialType.setId(materialTypeId);
            materialType.setCode(materialMustList.getCode());
            materialType.setCategory2(materialMustList.getMaterialTypeCategory());
            materialType.setName(materialMustList.getName());
            materialType.setUnit(materialMustList.getUnit());
            materialType.setSpec(materialMustList.getSpec());
            materialType.setPrice(BigDecimal.ZERO);
            materialType.setTheshold(new BigDecimal(materialMustList.getNeedAmount() * 3));
            materialType.setCategory1(1);
            buMaterialTypeMapper.insert(materialType);

            BuMaterialTypeAttr materialTypeAttr = new BuMaterialTypeAttr()
                    .setId(UUIDGenerator.generate())
                    .setMaterialTypeId(materialTypeId)
                    .setTheshold(materialType.getTheshold().doubleValue());
            materialTypeAttr.insert();
        }
    }

    private void setMaterialTypeStock(Page<BuMaterialMustList> page) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(page.getRecords())) {
            return;
        }

        List<BuMaterialMustList> mustListList = page.getRecords();

        // 查库存
        List<String> materialTypeIdList = mustListList.stream()
                .map(BuMaterialMustList::getMaterialTypeId)
                .distinct()
                .collect(Collectors.toList());
        List<BuMaterialStock> stockList = buMaterialStockService.listStockByMaterialTypeIdList(materialTypeIdList, true);
        // 减去库存已占用的数量
        buMaterialStockService.deleteStockUsedAmount(stockList, null, null);

        Map<String, List<BuMaterialStock>> materialTypeIdStockListMap = stockList.stream()
                .collect(Collectors.groupingBy(BuMaterialStock::getMaterialTypeId));
        for (BuMaterialMustList mustList : mustListList) {
            String materialTypeId = mustList.getMaterialTypeId();
            List<BuMaterialStock> materialStockList = materialTypeIdStockListMap.get(materialTypeId);
            if (org.apache.commons.collections.CollectionUtils.isEmpty(materialStockList)) {
                mustList.setAmount(0D);
            } else {
                BigDecimal stockAmount = materialStockList.stream()
                        .map(BuMaterialStock::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                mustList.setAmount(stockAmount.doubleValue());
            }
        }
    }

}
