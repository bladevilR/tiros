package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuMtrWarehouse;
import org.jeecg.modules.third.jdx.mapper.BuMtrWarehouseThirdMapper;
import org.jeecg.modules.third.jdx.mapper.ThirdCommonMapper;
import org.jeecg.modules.third.jdx.service.BuMtrWarehouseThirdService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.jeecg.modules.third.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 仓库信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-07
 */
@Slf4j
@Service
public class BuMtrWarehouseThirdServiceImpl extends ServiceImpl<BuMtrWarehouseThirdMapper, BuMtrWarehouse> implements BuMtrWarehouseThirdService {

    @Resource
    private BuMtrWarehouseThirdMapper buMtrWarehouseThirdMapper;
    @Resource
    private ThirdCommonMapper thirdCommonMapper;

    @Value("${tiros.base.rootWarehouse:2}")
    private String rootWarehouse;
    @Value("${tiros.base.defaultSysHouseCategory:AJ1}")
    private String defaultSysHouseCategory;


    /**
     * @see BuMtrWarehouseThirdService#insertWarehouseFromMaximoData(Map)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertWarehouseFromMaximoData(Map<String, List<String>> warehouseMap) throws Exception {
        if (null == warehouseMap || warehouseMap.size() == 0) {
            return true;
        }

        Date now = new Date();

        // 查询上级仓库
        String parentId = rootWarehouse;
        BuMtrWarehouse parent = buMtrWarehouseThirdMapper.selectById(parentId);
        if (null == parent) {
            throw new RuntimeException("上级仓库不存在");
        }
        // 清空上级仓库下子仓库
        LambdaQueryWrapper<BuMtrWarehouse> deleteWrapper = new LambdaQueryWrapper<BuMtrWarehouse>()
                .likeRight(BuMtrWarehouse::getWbs, parent.getWbs())
                .ne(BuMtrWarehouse::getWbs, parent.getWbs());
        buMtrWarehouseThirdMapper.delete(deleteWrapper);

        // 获取仓库编码，避免重复
        List<BuMtrWarehouse> allWarehouseList = buMtrWarehouseThirdMapper.selectList(Wrappers.emptyWrapper());
        Set<String> warehouseCodeSet = allWarehouseList.stream()
                .map(BuMtrWarehouse::getCode)
                .collect(Collectors.toSet());

        // 上级仓库信息
        int parentLevel = null == parent.getWhLevel() ? 0 : parent.getWhLevel();
        // 库存组织
        String parentSysHouseCategory = StringUtils.isBlank(parent.getSysHouseCategory()) ? defaultSysHouseCategory : parent.getSysHouseCategory();

        List<BuMtrWarehouse> warehouseList = new ArrayList<>();
        for (Map.Entry<String, List<String>> warehouseEntry : warehouseMap.entrySet()) {
            String ebsCode = warehouseEntry.getKey();
            List<String> childCodeList = warehouseEntry.getValue();

            // 库房
            int level = parentLevel + 1;
            String selfCode = ebsCode;
            BuMtrWarehouse warehouse = new BuMtrWarehouse()
                    .setId(UUIDGenerator.generate())
                    .setName(ebsCode)
                    .setCode(selfCode)
                    .setWhLevel(level)
                    .setType(1)
                    .setClose(0)
                    .setParentId(parentId)
                    .setWbs(parent.getWbs() + "." + selfCode)
                    .setSysHouseCode(ebsCode)
                    .setSysHouseCategory(parentSysHouseCategory)
                    .setLineId(parent.getLineId())
                    .setDepotId(parent.getDepotId())
                    .setWorkshopId(parent.getWorkshopId())
                    .setCompanyId(parent.getCompanyId())
                    .setSync(1)
                    .setStatus(1)
                    .setRemark("maximo导入")
                    .setCreateTime(now)
                    .setCreateBy("admin");
            warehouseList.add(warehouse);
            warehouseCodeSet.add(selfCode);

            for (String childEbsCode : childCodeList) {
                // 库位
                int childLevel = warehouse.getWhLevel() + 1;
                String childSelfCode = getWarehouseCode(childLevel, warehouseCodeSet);
                BuMtrWarehouse child = new BuMtrWarehouse()
                        .setId(UUIDGenerator.generate())
                        .setName(childEbsCode)
                        .setCode(childSelfCode)
                        .setWhLevel(childLevel)
                        .setType(2)
                        .setClose(0)
                        .setParentId(warehouse.getId())
                        .setWbs(warehouse.getWbs() + "." + childSelfCode)
                        .setSysHouseCode(childEbsCode)
                        .setSysHouseCategory(parentSysHouseCategory)
                        .setLineId(warehouse.getLineId())
                        .setDepotId(warehouse.getDepotId())
                        .setWorkshopId(warehouse.getWorkshopId())
                        .setCompanyId(warehouse.getCompanyId())
                        .setSync(1)
                        .setStatus(1)
                        .setRemark("maximo导入")
                        .setCreateTime(now)
                        .setCreateBy("admin");
                warehouseList.add(child);
                warehouseCodeSet.add(childSelfCode);
            }
        }

        if (CollectionUtils.isNotEmpty(warehouseList)) {
            List<List<BuMtrWarehouse>> batchSubList = DatabaseBatchSubUtil.batchSubList(warehouseList);
            for (List<BuMtrWarehouse> batchSub : batchSubList) {
                buMtrWarehouseThirdMapper.insertList(batchSub);
            }

            // 仓库更新缓存
            thirdCommonMapper.updateSysConfig(MaximoThirdConstant.SYS_CONFIG_UPDATE_WAREHOUSE_CACHE, "true");
        }

        return true;
    }

    /**
     * @see BuMtrWarehouseThirdService#getShouldNotExistWarehouseIds(Map)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String getShouldNotExistWarehouseIds(Map<String, List<String>> warehouseMap) throws Exception {
        if (null == warehouseMap) {
            warehouseMap = new HashMap<>();
        }

        // 架大修仓库信息：2级库和3级库
        LambdaQueryWrapper<BuMtrWarehouse> warehouseWrapper = new LambdaQueryWrapper<BuMtrWarehouse>()
                .in(BuMtrWarehouse::getWhLevel, Arrays.asList(3, 4));
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseThirdMapper.selectList(warehouseWrapper);

        List<String> warehouseIdList = warehouseList.stream()
                .map(BuMtrWarehouse::getId)
                .collect(Collectors.toList());
        List<BuMtrWarehouse> topList = warehouseList.stream()
                .filter(warehouse -> !warehouseIdList.contains(warehouse.getParentId()))
                .collect(Collectors.toList());
        for (BuMtrWarehouse top : topList) {
            recurseAddChild(top, warehouseList);
        }
        topList.sort(Comparator.comparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())));

        // 对比，获得maximo不存在的
        List<String> notExistWarehouseIdList = new ArrayList<>();
        for (BuMtrWarehouse warehouse : topList) {
            String name = warehouse.getName();

            if (warehouseMap.containsKey(name)) {
                List<BuMtrWarehouse> children = warehouse.getChildren();
                if (CollectionUtils.isNotEmpty(children)) {
                    List<String> level3CodeList = warehouseMap.get(name);
                    for (BuMtrWarehouse child : children) {
                        String childName = child.getName();
                        if (!level3CodeList.contains(childName)) {
                            notExistWarehouseIdList.add(child.getId());
                        }
                    }
                }
            } else {
                notExistWarehouseIdList.add(warehouse.getId());
            }
        }

        return String.join(",", notExistWarehouseIdList);
    }


    private String getWarehouseCode(int level, Set<String> warehouseCodeSet) {
        String childSelfCode = level + RandomStringUtils.randomAlphanumeric(5);
        if (warehouseCodeSet.contains(childSelfCode)) {
            return getWarehouseCode(level, warehouseCodeSet);
        } else {
            return childSelfCode;
        }
    }

    private void recurseAddChild(BuMtrWarehouse warehouse, List<BuMtrWarehouse> warehouseList) {
        if (null == warehouse) {
            return;
        }

        String id = warehouse.getId();
        List<BuMtrWarehouse> children = warehouseList.stream()
                .filter(detail -> StringUtils.isNotBlank(id) && id.equals(detail.getParentId()))
                .sorted(Comparator.comparing(BuMtrWarehouse::getHasChildren, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        warehouse.setChildren(children);
        if (CollectionUtils.isNotEmpty(children)) {
            for (BuMtrWarehouse child : children) {
                recurseAddChild(child, warehouseList);
            }
        }
    }

}
