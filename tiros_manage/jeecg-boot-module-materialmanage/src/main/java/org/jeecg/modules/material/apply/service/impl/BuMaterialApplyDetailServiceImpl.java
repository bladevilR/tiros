package org.jeecg.modules.material.apply.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.util.TirosUtil;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;
import org.jeecg.modules.material.apply.mapper.BuMaterialApplyDetailMapper;
import org.jeecg.modules.material.apply.mapper.BuMaterialAssignDetailMapper;
import org.jeecg.modules.material.apply.service.BuMaterialApplyDetailService;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;
import org.jeecg.modules.material.pallet.bean.vo.BuMaterialAssignDetailVO;
import org.jeecg.modules.material.pallet.bean.vo.BuMaterialPalletVO;
import org.jeecg.modules.material.pallet.mapper.BuMaterialPalletMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 领用明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
@Slf4j
@Service
public class BuMaterialApplyDetailServiceImpl extends ServiceImpl<BuMaterialApplyDetailMapper, BuMaterialApplyDetail> implements BuMaterialApplyDetailService {

    @Resource
    private BuMaterialApplyDetailMapper buMaterialApplyDetailMapper;
    @Resource
    private BuMaterialAssignDetailMapper buMaterialAssignDetailMapper;
    @Resource
    private BuMaterialPalletMapper materialPalletMapper;


    /**
     * @see BuMaterialApplyDetailService#listByApplyId(String, List)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialApplyDetail> listByApplyId(String applyId, List<Integer> status) throws Exception {
        List<BuMaterialApplyDetail> applyDetailList = buMaterialApplyDetailMapper.selectListByApplyId(applyId, status);
        // 设置来源库位和托盘名称信息
        setLocationWarehouseNamesAndPalletNamesAndAssignDetail(applyDetailList);

        return applyDetailList;
    }

    /**
     * @see BuMaterialApplyDetailService#listByOrderId(String, List)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialApplyDetail> listByOrderId(String orderId, List<Integer> status) throws Exception {
        List<BuMaterialApplyDetail> applyDetailList = buMaterialApplyDetailMapper.selectListByOrderId(orderId, status);
        // 设置来源库位和托盘名称信息
        setLocationWarehouseNamesAndPalletNamesAndAssignDetail(applyDetailList);

        return applyDetailList;
    }

    /**
     * @see BuMaterialApplyDetailService#getMaterialByPalletId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialPalletVO getMaterialByPalletId(String palletId) throws Exception {
        BuMaterialPallet pallet = materialPalletMapper.selectById(palletId);
        BuMaterialPalletVO buMaterialPalletVO = new BuMaterialPalletVO()
                .setPalletCode(pallet.getCode())
                .setPalletName(pallet.getName())
                .setId(palletId);

        List<BuMaterialAssignDetailVO> materialList = new ArrayList<>();
        List<BuMaterialAssignDetail> assignDetails = buMaterialAssignDetailMapper.selectListByPalletId(palletId);
        if (CollectionUtils.isNotEmpty(assignDetails)) {
            Map<String, List<BuMaterialAssignDetail>> assignDetailMap = assignDetails.stream()
                    .collect(Collectors.groupingBy(BuMaterialAssignDetail::getMaterialTypeId));
            assignDetailMap.keySet().forEach(key -> {
                List<BuMaterialAssignDetail> details = assignDetailMap.get(key);
                double amount = details.stream()
                        .mapToDouble(BuMaterialAssignDetail::getAmount)
                        .sum();
                BuMaterialAssignDetailVO detail = new BuMaterialAssignDetailVO()
                        .setMaterialCode(details.get(0).getMaterialCode())
                        .setMaterialName(details.get(0).getMaterialName())
                        .setAmount(amount);
                materialList.add(detail);
            });

            buMaterialPalletVO.setMaterialList(materialList);
        }
        return buMaterialPalletVO;
    }


    private void setLocationWarehouseNamesAndPalletNamesAndAssignDetail(List<BuMaterialApplyDetail> applyDetailList) {
        if (CollectionUtils.isEmpty(applyDetailList)) {
            return;
        }

        List<String> applyDetailIdList = applyDetailList.stream()
                .map(BuMaterialApplyDetail::getId)
                .collect(Collectors.toList());
        List<BuMaterialAssignDetail> allAssignDetailList = buMaterialAssignDetailMapper.selectListByApplyDetailIdList(applyDetailIdList);
        if (CollectionUtils.isEmpty(allAssignDetailList)) {
            return;
        }
        for (BuMaterialAssignDetail assignDetail : allAssignDetailList) {
            assignDetail.setSourceLocationName(TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName()));
        }

        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            List<String> sourceLocationNameList = new ArrayList<>();
            List<String> palletNameList = new ArrayList<>();
            List<String> sourceLocationAndPalletNameList = new ArrayList<>();

            List<BuMaterialAssignDetail> assignDetailList = allAssignDetailList.stream()
                    .filter(assignDetail -> applyDetail.getId().equals(assignDetail.getApplyDetailId()))
                    .collect(Collectors.toList());
            applyDetail.setAssignDetailList(assignDetailList);

            if (CollectionUtils.isEmpty(assignDetailList)) {
                continue;
            }

            for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                String sourceLocationName = TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName());
                String palletName = assignDetail.getPalletName();
                if (!sourceLocationNameList.contains(sourceLocationName)) {
                    sourceLocationNameList.add(sourceLocationName);
                }
                if (StringUtils.isNotBlank(palletName)) {
                    if (!palletNameList.contains(palletName)) {
                        palletNameList.add(palletName);
                    }
                }
                sourceLocationAndPalletNameList.add(sourceLocationName + " | " + assignDetail.getAmount() + " | " + (StringUtils.isNotBlank(palletName) ? palletName : ""));
            }

            applyDetail.setSourceLocationName(String.join(", ", sourceLocationNameList))
                    .setPalletName(String.join(", ", palletNameList))
                    .setSourceLocationAndPalletName(String.join(", ", sourceLocationAndPalletNameList));
        }
    }

}
