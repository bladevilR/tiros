package org.jeecg.modules.material.pallet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;
import org.jeecg.modules.material.pallet.bean.vo.PalletMaterialTypesVO;
import org.jeecg.modules.material.pallet.mapper.BuMaterialPalletMapper;
import org.jeecg.modules.material.pallet.service.BuMaterialPalletService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物资托盘 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
@Service
@Slf4j
public class BuMaterialPalletServiceImpl extends ServiceImpl<BuMaterialPalletMapper, BuMaterialPallet> implements BuMaterialPalletService {

    @Resource
    private BuMaterialPalletMapper buMaterialPalletMapper;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;


    /**
     * @see BuMaterialPalletService#page(String, String, String, String, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialPallet> page(String searchText, String typeId, String status, String useStatus, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuMaterialPallet> page = buMaterialPalletMapper.selectPageBySearchText(new Page<>(pageNo, pageSize), searchText, typeId, status, useStatus);

        // 设置物资类型名称，多个逗号分割
        setPalletMaterialTypeNames(page.getRecords());

        return page;
    }

    /**
     * @see BuMaterialPalletService#getPalletById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialPallet getPalletById(String id) throws Exception {
        BuMaterialPallet pallet = buMaterialPalletMapper.selectById(id);

        // 设置物资类型名称，多个逗号分割
        setPalletMaterialTypeNames(Collections.singletonList(pallet));

        return pallet;
    }

    /**
     * @see BuMaterialPalletService#listUnusedPalletByMaterialTypeId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialPallet> listUnusedPalletByMaterialTypeId(String materialTypeId) throws Exception {
        LambdaQueryWrapper<BuMaterialPallet> wrapper = new LambdaQueryWrapper<BuMaterialPallet>()
                .eq(BuMaterialPallet::getUseStatus, 0)
                .like(BuMaterialPallet::getMaterialTypes, materialTypeId);
        List<BuMaterialPallet> palletList = buMaterialPalletMapper.selectList(wrapper);

        // 设置物资类型名称，多个逗号分割
        setPalletMaterialTypeNames(palletList);

        return palletList;
    }

    /**
     * @see BuMaterialPalletService#savePallet(BuMaterialPallet)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean savePallet(BuMaterialPallet buMaterialPallet) throws Exception {
        if (null == buMaterialPallet.getStatus()) {
            buMaterialPallet.setStatus(1);
        }
        Boolean isBatchAdd = buMaterialPallet.getIsBatchAdd();

        if (null != isBatchAdd && isBatchAdd) {
            String code = buMaterialPallet.getCode();
            String name = buMaterialPallet.getName();
            Integer batchAddStartNum = buMaterialPallet.getBatchAddStartNum();
            Integer batchAddEndNum = buMaterialPallet.getBatchAddEndNum();

            int length = batchAddEndNum.toString().length();

            List<BuMaterialPallet> palletList = new ArrayList<>();
            for (int i = batchAddStartNum; i <= batchAddEndNum; i++) {
                BuMaterialPallet palletBatch = new BuMaterialPallet();
                BeanUtils.copyProperties(buMaterialPallet, palletBatch);

                StringBuilder value = new StringBuilder(String.valueOf(i));
                while (value.length() < length) {
                    value.insert(0, "0");
                }
                palletBatch.setCode(code + value);
                palletBatch.setName(name + value);

                palletList.add(palletBatch);
            }

            buMaterialPalletMapper.insertList(palletList);
        } else {
            buMaterialPalletMapper.insert(buMaterialPallet);
        }

        return true;
    }

    /**
     * @see BuMaterialPalletService#setPalletMaterialTypes(PalletMaterialTypesVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setPalletMaterialTypes(PalletMaterialTypesVO palletMaterialTypesVO) throws Exception {
        String palletIds = palletMaterialTypesVO.getPalletIds();
        String materialTypes = palletMaterialTypesVO.getMaterialTypes();

        List<String> palletIdList = Arrays.asList(palletIds.split(","));
        List<BuMaterialPallet> palletList = buMaterialPalletMapper.selectBatchIds(palletIdList);
        if (CollectionUtils.isNotEmpty(palletIdList)) {
            for (BuMaterialPallet pallet : palletList) {
                pallet.setMaterialTypes(materialTypes);
                buMaterialPalletMapper.updateById(pallet);
            }
        }

        return true;
    }

    /**
     * @see BuMaterialPalletService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        buMaterialPalletMapper.deleteBatchIds(idList);

        return true;
    }


    private void setPalletMaterialTypeNames(List<BuMaterialPallet> palletList) {
        if (CollectionUtils.isEmpty(palletList)) {
            return;
        }
        Map<String, String> materialTypeIdNameMap = new HashMap<>();

        String materialTypeIds = palletList.stream()
                .map(BuMaterialPallet::getMaterialTypes)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(","));
        if (StringUtils.isNotBlank(materialTypeIds)) {
            List<String> materialTypeIdList = Arrays.asList(materialTypeIds.split(","));

            LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                    .in(BuMaterialType::getId, materialTypeIdList)
                    .select(BuMaterialType::getId, BuMaterialType::getName);
            List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);

            materialTypeList.forEach(materialType -> materialTypeIdNameMap.put(materialType.getId(), materialType.getName()));
        }

        for (BuMaterialPallet pallet : palletList) {
            String materialTypes = pallet.getMaterialTypes();
            if (StringUtils.isNotBlank(materialTypes)) {
                List<String> materialTypeNameList = new ArrayList<>();

                String[] materialTypeIdArray = materialTypes.split(",");
                for (String materialTypeId : materialTypeIdArray) {
                    String materialTypeName = materialTypeIdNameMap.get(materialTypeId);
                    if (StringUtils.isNotBlank(materialTypeName)) {
                        materialTypeNameList.add(materialTypeName);
                    }
                }

                pallet.setMaterialTypeNames(String.join(",", materialTypeNameList));
            }
        }
    }

}
