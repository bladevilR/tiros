package org.jeecg.modules.outsource.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.outsource.bean.BuBaseSupplier;
import org.jeecg.modules.outsource.bean.BuContractInfo;
import org.jeecg.modules.outsource.bean.VendorTree;
import org.jeecg.modules.outsource.bean.vo.BuBaseSupplierQueryVO;
import org.jeecg.modules.outsource.bean.vo.SystemContractVO;
import org.jeecg.modules.outsource.mapper.BuBaseSupplierMapper;
import org.jeecg.modules.outsource.mapper.BuContractInfoMapper;
import org.jeecg.modules.outsource.service.BuBaseSupplierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 厂商信息表 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Service
public class BuBaseSupplierServiceImpl extends ServiceImpl<BuBaseSupplierMapper, BuBaseSupplier> implements BuBaseSupplierService {

    @Resource
    private BuBaseSupplierMapper buBaseSupplierMapper;
    @Resource
    private BuContractInfoMapper buContractInfoMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuBaseSupplierService#vendorTree(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<SystemContractVO> vendorTree(String trainTypeId) throws Exception {
        // 获取系统数据
        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = assetTypeCacheService.map(trainTypeId);
        List<BuTrainAssetTypeBO> assetTypeBOList = new ArrayList<>(assetTypeIdBOMap.values());
        List<BuTrainAssetTypeBO> topAssetTypeBOList = assetTypeBOList.stream()
                .filter(assetType -> StringUtils.isBlank(assetType.getParentId()))
                .collect(Collectors.toList());

        List<SystemContractVO> systemList = new ArrayList<>();
        for (BuTrainAssetTypeBO topAssetTypeBO : topAssetTypeBOList) {
            SystemContractVO system = new SystemContractVO()
                    .setId(topAssetTypeBO.getId())
                    .setName(topAssetTypeBO.getShortName())
                    .setType(1)
                    .setChildren(new ArrayList<>());
            systemList.add(system);
        }

        // 合同数据
        List<BuContractInfo> contractInfoList = buContractInfoMapper.selectList(Wrappers.emptyWrapper());

        List<SystemContractVO> noSystemContractList = new ArrayList<>();
        for (BuContractInfo contractInfo : contractInfoList) {
             StringBuffer nameOthers=new StringBuffer(contractInfo.getContractName());
            if (StringUtils.isNotEmpty(contractInfo.getCurTrain())) {
                nameOthers.append(contractInfo.getCurTrain() + "车");
            }
            if (contractInfo.getFinishAmount() != null) {
                nameOthers.append("第" + contractInfo.getFinishAmount() + "列");
            }
            SystemContractVO contract = new SystemContractVO()
                    .setId(contractInfo.getId())
                    .setName(nameOthers.toString())
                    .setType(2)
                    .setContractIds(contractInfo.getId());

            BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOMap.get(contractInfo.getAssetTypeId());
            if (null == assetTypeBO) {
                noSystemContractList.add(contract);
                continue;
            }

            String sysId = assetTypeBO.getSysId();
            List<SystemContractVO> matchSystemList = systemList.stream()
                    .filter(system -> sysId.equals(system.getId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(matchSystemList)) {
                noSystemContractList.add(contract);
                continue;
            }

            SystemContractVO system = matchSystemList.get(0);
            List<SystemContractVO> contractList = system.getChildren();
            contractList.add(contract);
        }

        // 未匹配到系统的合同信息
        SystemContractVO noSystem = new SystemContractVO()
                .setId("-1")
                .setName("其他合同(未匹配到系统)")
                .setType(1)
                .setChildren(noSystemContractList);
        systemList.add(noSystem);

        for (SystemContractVO system : systemList) {
            List<SystemContractVO> contractList = system.getChildren();
            if (CollectionUtils.isNotEmpty(contractList)) {
                String contractIds = contractList.stream()
                        .map(SystemContractVO::getId)
                        .collect(Collectors.joining(","));
                system.setContractIds(contractIds);
            } else {
                system.setContractIds("-1");
            }
        }

        return systemList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSupplier(BuBaseSupplier supplier) {
        return this.save(supplier);
    }

    @Override
    public String selectAssetTypeName(String objTypeId) {
        return buBaseSupplierMapper.selectAssetTypeName(objTypeId);
    }

    @Override
    public IPage<BuBaseSupplier> pageList(Page<BuBaseSupplier> page, BuBaseSupplierQueryVO queryVO) {
        return buBaseSupplierMapper.selectPageByCondition(page, queryVO);
    }

    private void getChilds(VendorTree t, List<VendorTree> childs) {
        List<VendorTree> treeChilds = buBaseSupplierMapper.selectVendorTreeChild(t.getId());
        if (oConvertUtils.listIsNotEmpty(treeChilds)) {
            childs.addAll(treeChilds);
            treeChilds.forEach(c -> {
                getChilds(c, childs);
            });
        }
    }
}
