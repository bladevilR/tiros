package org.jeecg.modules.tiros.stock.change.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.stock.change.bean.BuMaterialStockChange;
import org.jeecg.common.tiros.stock.change.service.BuMaterialStockChangeService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.modules.tiros.stock.change.mapper.BuMaterialStockChangeMapper;
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
public class BuMaterialStockChangeServiceImpl implements BuMaterialStockChangeService {

    @Resource
    private BuMaterialStockChangeMapper buMaterialStockChangeMapper;


    /**
     * @see BuMaterialStockChangeService#addChangeList(List)
     */
    @Override
    public boolean addChangeList(List<BuMaterialStockChange> changeList) {
        if (CollectionUtils.isEmpty(changeList)) {
            return true;
        }

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (null != sysUser) {
            for (BuMaterialStockChange change : changeList) {
                change.setOperationUser(sysUser.getId());
            }
        }

        List<List<BuMaterialStockChange>> batchSubList = DatabaseBatchSubUtil.batchSubList(changeList);
        for (List<BuMaterialStockChange> batchSub : batchSubList) {
            buMaterialStockChangeMapper.insertList(batchSub);
        }

        return true;
    }

}
