package org.jeecg.modules.material.material.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;
import org.jeecg.modules.material.material.bean.BuMaterialTypeTool;
import org.jeecg.modules.material.material.bean.vo.BuMaterialTypeQueryVO;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeAttrMapper;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.material.service.BuMaterialTypeReplaceService;
import org.jeecg.modules.material.material.service.BuMaterialTypeService;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.service.BuMaterialStockService;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.mapper.BuMtrWarehouseMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物资类型 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-08-07
 */
@Slf4j
@Service
public class BuMaterialTypeServiceImpl extends ServiceImpl<BuMaterialTypeMapper, BuMaterialType> implements BuMaterialTypeService {

    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private BuMaterialTypeAttrMapper buMaterialTypeAttrMapper;
    @Resource
    private BuMaterialStockService buMaterialStockService;
    @Resource
    private BuMtrWarehouseMapper buMtrWarehouseMapper;
    @Resource
    private BuMaterialTypeReplaceService buMaterialTypeReplaceService;


    /**
     * @see BuMaterialTypeService#pageMaterialType(BuMaterialTypeQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Page<BuMaterialType> pageMaterialType(BuMaterialTypeQueryVO queryVO, Integer pageNo, Integer pageSize) {
        extractCategory1(queryVO);
        // 关联可替换物资
        if (StringUtils.isNotBlank(queryVO.getSearchText())) {
            List<String> canReplaceMaterialTypeIdList = buMaterialTypeReplaceService.listCanReplaceMaterialTypeIdByMaterialTypeText(queryVO.getSearchText());
            if (CollectionUtils.isEmpty(canReplaceMaterialTypeIdList)) {
                // 输入搜索了物资编码或者名称，且无法找到对应物资类型和可替换物资
                canReplaceMaterialTypeIdList = Collections.singletonList("-1");
            }
            queryVO.setSearchMaterialTypeIdList(canReplaceMaterialTypeIdList);
        }
        Page<BuMaterialType> page = buMaterialTypeMapper.selectMaterialTypePage(new Page<>(pageNo, pageSize), queryVO);

        setMaterialTypeStock(queryVO, page);

//        // 设置可替换物资属性
//        setMaterialCanReplace(page.getRecords());

        return page;
    }

    /**
     * @see BuMaterialTypeService#pageMaterialTypeTool(BuMaterialTypeQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Page<BuMaterialTypeTool> pageMaterialTypeTool(BuMaterialTypeQueryVO queryVO, Integer pageNo, Integer pageSize) {
        extractCategory1(queryVO);
        Page<BuMaterialTypeTool> page = buMaterialTypeMapper.selectMaterialTypeToolPage(new Page<>(pageNo, pageSize), queryVO);

        setMaterialTypeToolStock(queryVO, page);

        return page;
    }

    /**
     * @see BuMaterialTypeService#selectMustMaterialTypePage(BuMaterialTypeQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Page<BuMaterialType> selectMustMaterialTypePage(BuMaterialTypeQueryVO queryVO, Integer pageNo, Integer pageSize) {
        extractCategory1(queryVO);
        // 关联可替换物资
        if (StringUtils.isNotBlank(queryVO.getSearchText())) {
            List<String> canReplaceMaterialTypeIdList = buMaterialTypeReplaceService.listCanReplaceMaterialTypeIdByMaterialTypeText(queryVO.getSearchText());
            queryVO.setSearchMaterialTypeIdList(canReplaceMaterialTypeIdList);
        }
        Page<BuMaterialType> page = buMaterialTypeMapper.selectMustMaterialTypePage(new Page<>(pageNo, pageSize), queryVO);

        setMaterialTypeStock(queryVO, page);

//        // 设置可替换物资属性
//        setMaterialCanReplace(page.getRecords());

        return page;
    }

    /**
     * @see BuMaterialTypeService#getMaterialTypeById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialType getMaterialTypeById(String id) throws Exception {
        return buMaterialTypeMapper.selectMaterialTypeById(id);
//        // 设置可替换物资属性
//        setMaterialCanReplace(Collections.singletonList(materialType));
    }

    /**
     * @see BuMaterialTypeService#getMaterialTypeToolById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialTypeTool getMaterialTypeToolById(String id) throws Exception {
        BuMaterialType materialType = buMaterialTypeMapper.selectMaterialTypeById(id);
        BuMaterialTypeTool materialTypeTool = new BuMaterialTypeTool();
        BeanUtils.copyProperties(materialType, materialTypeTool);
        return materialTypeTool;
    }

    /**
     * @see BuMaterialTypeService#addMaterialType(BuMaterialType)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addMaterialType(BuMaterialType materialType) throws Exception {
        if (StringUtils.isBlank(materialType.getCode())) {
            materialType.setCode(RandomUtil.randomNumbers(12));
        }
        if (StringUtils.isBlank(materialType.getId())) {
            materialType.setId(materialType.getCode());
        }
        if (StringUtils.isBlank(materialType.getCategory2())) {
            materialType.setCategory2(null == materialType.getExtAttr() ? null : String.valueOf(materialType.getExtAttr()));
        }
        buMaterialTypeMapper.insert(materialType);

        // 更新物质属性
        LambdaQueryWrapper<BuMaterialTypeAttr> materialTypeAttrWrapper = new LambdaQueryWrapper<BuMaterialTypeAttr>()
                .eq(BuMaterialTypeAttr::getMaterialTypeId, materialType.getId());
        buMaterialTypeAttrMapper.delete(materialTypeAttrWrapper);
        BuMaterialTypeAttr materialTypeAttr = new BuMaterialTypeAttr()
                .setMaterialTypeId(materialType.getId())
                .setLead(null == materialType.getLead() ? 25 : materialType.getLead())
                .setTheshold(null == materialType.getTheshold() ? -1D : materialType.getTheshold().doubleValue());
        buMaterialTypeAttrMapper.insert(materialTypeAttr);

        return true;
    }

    /**
     * @see BuMaterialTypeService#updateMaterialType(BuMaterialType)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateMaterialType(BuMaterialType materialType) throws Exception {
        if (StringUtils.isBlank(materialType.getCode())) {
            materialType.setCode(RandomUtil.randomNumbers(12));
        }
        if (StringUtils.isBlank(materialType.getCategory2())) {
            materialType.setCategory2(null == materialType.getExtAttr() ? null : String.valueOf(materialType.getExtAttr()));
        }
        buMaterialTypeMapper.updateById(materialType);

        // 更新物质属性
        LambdaQueryWrapper<BuMaterialTypeAttr> materialTypeAttrWrapper = new LambdaQueryWrapper<BuMaterialTypeAttr>()
                .eq(BuMaterialTypeAttr::getMaterialTypeId, materialType.getId());
        buMaterialTypeAttrMapper.delete(materialTypeAttrWrapper);
        BuMaterialTypeAttr materialTypeAttr = new BuMaterialTypeAttr()
                .setMaterialTypeId(materialType.getId())
                .setLead(null == materialType.getLead() ? 25 : materialType.getLead())
                .setTheshold(null == materialType.getTheshold() ? -1D : materialType.getTheshold().doubleValue());
        buMaterialTypeAttrMapper.insert(materialTypeAttr);

        return true;
    }

    /**
     * @see BuMaterialTypeService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        buMaterialTypeMapper.deleteBatchIds(idList);

        return true;
    }


    private void extractCategory1(BuMaterialTypeQueryVO queryVO) {
        String category1 = queryVO.getCategory1();
        if (StringUtils.isBlank(category1)) {
            return;
        }

        List<Integer> category1List = new ArrayList<>();
        String[] split = category1.split(",");
        for (String item : split) {
            category1List.add(Integer.parseInt(item));
        }
        // 如果查非必换件的物料，同时也要查询出来分类为其他的（category1 = -1）
        if (category1List.contains(2) && category1List.contains(3) && !category1List.contains(-1)) {
            category1List.add(-1);
        }
        queryVO.setCategory1List(category1List);
    }

    private void setMaterialTypeStock(BuMaterialTypeQueryVO queryVO, Page<BuMaterialType> page) {
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return;
        }

        List<BuMaterialType> materialTypeList = page.getRecords();

        // 查库存
        List<String> materialTypeIdList = materialTypeList.stream()
                .map(BuMaterialType::getId)
                .distinct()
                .collect(Collectors.toList());
        List<BuMaterialStock> stockList = buMaterialStockService.listStockByMaterialTypeIdList(materialTypeIdList, true);
        // 减去库存已占用的数量
        buMaterialStockService.deleteStockUsedAmount(stockList, null, null);
        if (StringUtils.isNotBlank(queryVO.getWarehouseId())) {
            BuMtrWarehouse warehouse = buMtrWarehouseMapper.selectById(queryVO.getWarehouseId());
            if (null != warehouse) {
                String warehouseWbs = warehouse.getWbs();
                // 过滤掉非选定仓库及子仓库的
                stockList.removeIf(stock -> StringUtils.isNotBlank(stock.getWarehouseWbs()) && !stock.getWarehouseWbs().startsWith(warehouseWbs));
            }
        }

        Map<String, List<BuMaterialStock>> materialTypeIdStockListMap = stockList.stream()
                .collect(Collectors.groupingBy(BuMaterialStock::getMaterialTypeId));
        for (BuMaterialType materialType : materialTypeList) {
            String materialTypeId = materialType.getId();
            List<BuMaterialStock> materialStockList = materialTypeIdStockListMap.get(materialTypeId);
            if (CollectionUtils.isEmpty(materialStockList)) {
                materialType.setAmount(BigDecimal.ZERO);
            } else {
                BigDecimal stockAmount = materialStockList.stream()
                        .map(BuMaterialStock::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                materialType.setAmount(stockAmount);
            }
        }
    }

    private void setMaterialTypeToolStock(BuMaterialTypeQueryVO queryVO, Page<BuMaterialTypeTool> page) {
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return;
        }

        List<BuMaterialTypeTool> materialTypeToolList = page.getRecords();

        // 查库存
        List<String> materialTypeIdList = materialTypeToolList.stream()
                .map(BuMaterialTypeTool::getId)
                .distinct()
                .collect(Collectors.toList());
        List<BuMaterialStock> stockList = buMaterialStockService.listStockByMaterialTypeIdList(materialTypeIdList, true);
        // 减去库存已占用的数量
        buMaterialStockService.deleteStockUsedAmount(stockList, null, null);
        if (StringUtils.isNotBlank(queryVO.getWarehouseId())) {
            BuMtrWarehouse warehouse = buMtrWarehouseMapper.selectById(queryVO.getWarehouseId());
            if (null != warehouse) {
                String warehouseWbs = warehouse.getWbs();
                // 过滤掉非选定仓库及子仓库的
                stockList.removeIf(stock -> StringUtils.isNotBlank(stock.getWarehouseWbs()) && !stock.getWarehouseWbs().startsWith(warehouseWbs));
            }
        }

        Map<String, List<BuMaterialStock>> materialTypeIdStockListMap = stockList.stream()
                .collect(Collectors.groupingBy(BuMaterialStock::getMaterialTypeId));
        for (BuMaterialTypeTool materialTypeTool : materialTypeToolList) {
            String materialTypeId = materialTypeTool.getId();
            List<BuMaterialStock> materialStockList = materialTypeIdStockListMap.get(materialTypeId);
            if (CollectionUtils.isEmpty(materialStockList)) {
                materialTypeTool.setAmount(BigDecimal.ZERO);
            } else {
                BigDecimal stockAmount = materialStockList.stream()
                        .map(BuMaterialStock::getAmount)
                        .filter(Objects::isNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                materialTypeTool.setAmount(stockAmount);
            }
        }
    }

//    private void setMaterialCanReplace(List<BuMaterialType> materialTypeList) {
//        if (CollectionUtils.isEmpty(materialTypeList)) {
//            return;
//        }
//
//        List<String> materialTypeIdList = materialTypeList.stream()
//                .map(BuMaterialType::getId)
//                .collect(Collectors.toList());
//        List<BuMaterialTypeReplace> replaceList = buMaterialTypeReplaceMapper.selectBatchIds(materialTypeIdList);
//
//        for (BuMaterialType materialType : materialTypeList) {
//            String materialTypeId = materialType.getId();
//            List<BuMaterialTypeReplace> list = replaceList.stream()
//                    .filter(item -> materialTypeId.equals(item.getId()))
//                    .collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(list)) {
//                String canReplaces = list.stream()
//                        .map(BuMaterialTypeReplace::getCanReplace)
//                        .collect(Collectors.joining(","));
//                materialType.setCanReplace(canReplaces);
//            }
//        }
//    }

}
