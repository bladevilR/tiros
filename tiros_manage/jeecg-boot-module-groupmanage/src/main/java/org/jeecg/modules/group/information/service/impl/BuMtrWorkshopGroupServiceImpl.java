package org.jeecg.modules.group.information.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.group.information.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.group.information.mapper.BuMtrWorkshopGroupMapper;
import org.jeecg.modules.group.information.service.BuMtrWorkshopGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 车间班组 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-18
 */
@Service
public class BuMtrWorkshopGroupServiceImpl extends ServiceImpl<BuMtrWorkshopGroupMapper, BuMtrWorkshopGroup> implements BuMtrWorkshopGroupService {

    @Resource
    private BuMtrWorkshopGroupMapper buMtrWorkshopGroupMapper;

    /**
     * @see BuMtrWorkshopGroupService#getGroupById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMtrWorkshopGroup getGroupById(String groupId) throws Exception {
        return buMtrWorkshopGroupMapper.selectWorkshopGroupById(groupId);
    }


    /**
     * @see BuMtrWorkshopGroupService#saveWorkGroupUser(String,String,String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveWorkGroupUser(String groupId, String userId, String type) throws Exception {
        LambdaQueryWrapper<BuMtrWorkshopGroup> workshopGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        workshopGroupLambdaQueryWrapper.eq(BuMtrWorkshopGroup::getId,groupId);

        BuMtrWorkshopGroup buMtrWorkshopGroup = buMtrWorkshopGroupMapper.selectOne(workshopGroupLambdaQueryWrapper);

        if ("1".equals(type)){
            buMtrWorkshopGroup.setMonitor(userId);
            buMtrWorkshopGroup.setMonitor2(null);
        } else {
            buMtrWorkshopGroup.setMonitor2(userId);
            buMtrWorkshopGroup.setMonitor(null);
        }
        buMtrWorkshopGroupMapper.updateById(buMtrWorkshopGroup);

        return true;
    }

}
