package org.jeecg.modules.third.jdx.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.modules.third.common.LoginUser;
import org.jeecg.modules.third.jdx.bean.BuMaterialStockChange;
import org.jeecg.modules.third.jdx.mapper.BuMaterialStockChangeThirdMapper;
import org.jeecg.modules.third.jdx.service.BuMaterialStockChangeThirdService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-10-23
 */
@Service
public class BuMaterialStockChangeThirdServiceImpl implements BuMaterialStockChangeThirdService {

    @Resource
    private BuMaterialStockChangeThirdMapper buMaterialStockChangeThirdMapper;


    /**
     * @see BuMaterialStockChangeThirdService#addChangeList(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addChangeList(List<BuMaterialStockChange> changeList) {
        if (CollectionUtils.isEmpty(changeList)) {
            return true;
        }

        // 获取登录人信息
        String userId = "maximoAdmin";
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (null != sysUser) {
                userId = sysUser.getId();
            }
        } catch (Exception ignored) {

        }
        for (BuMaterialStockChange change : changeList) {
            change.setOperationUser(userId);
        }

        List<List<BuMaterialStockChange>> batchSubList = DatabaseBatchSubUtil.batchSubList(changeList);
        for (List<BuMaterialStockChange> batchSub : batchSubList) {
            buMaterialStockChangeThirdMapper.insertList(batchSub);
        }

        return true;
    }

}
