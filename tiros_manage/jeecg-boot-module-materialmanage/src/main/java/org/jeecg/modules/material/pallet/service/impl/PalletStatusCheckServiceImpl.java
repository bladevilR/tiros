package org.jeecg.modules.material.pallet.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.tiros.pallet.PalletStatusCheckService;
import org.jeecg.modules.material.apply.mapper.BuMaterialAssignDetailMapper;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;
import org.jeecg.modules.material.pallet.mapper.BuMaterialPalletMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 托盘状态检查并设置 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-31
 */
@Service
public class PalletStatusCheckServiceImpl implements PalletStatusCheckService {

    @Resource
    private BuMaterialPalletMapper buMaterialPalletMapper;
    @Resource
    private BuMaterialAssignDetailMapper buMaterialAssignDetailMapper;


    /**
     * @see PalletStatusCheckService#checkAndSetPalletStatus()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean checkAndSetPalletStatus() {
        // 查询未提交的工单的发放明细中存在的托盘id，这些托盘为使用中
        List<String> usingPalletIdList = buMaterialAssignDetailMapper.selectUnCommitOrderAssignDetailPalletIdList();
        // 查询所有托盘
        List<BuMaterialPallet> palletList = buMaterialPalletMapper.selectList(Wrappers.emptyWrapper());
        if (CollectionUtils.isEmpty(palletList)) {
            return true;
        }

        for (BuMaterialPallet pallet : palletList) {
            pallet.setUseStatus(0);
            if (usingPalletIdList.contains(pallet.getId())) {
                pallet.setUseStatus(1);
            }
        }
        buMaterialPalletMapper.updateListUseStatus(palletList);

        return true;
    }

}
