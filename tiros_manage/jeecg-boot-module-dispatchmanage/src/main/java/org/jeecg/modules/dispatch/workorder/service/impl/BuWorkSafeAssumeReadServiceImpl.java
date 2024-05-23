package org.jeecg.modules.dispatch.workorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkSafeAssumeRead;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkSafeAssumeReadMapper;
import org.jeecg.modules.dispatch.workorder.service.BuWorkSafeAssumeReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * 安全预想阅读记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Slf4j
@Service
public class BuWorkSafeAssumeReadServiceImpl extends ServiceImpl<BuWorkSafeAssumeReadMapper, BuWorkSafeAssumeRead> implements BuWorkSafeAssumeReadService {

    @Resource
    private BuWorkSafeAssumeReadMapper buWorkSafeAssumeReadMapper;


    /**
     * @see BuWorkSafeAssumeReadService#addSafeAssumeRead(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addSafeAssumeRead(String safeAssumeId) throws Exception {
        // 获取登录用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        // 删除原有的该人员该安全预想的阅读记录
        LambdaQueryWrapper<BuWorkSafeAssumeRead> wrapper = new LambdaQueryWrapper<BuWorkSafeAssumeRead>()
                .eq(BuWorkSafeAssumeRead::getSafeAssumeId, safeAssumeId)
                .eq(BuWorkSafeAssumeRead::getUserId, userId);
        buWorkSafeAssumeReadMapper.delete(wrapper);

        // 增加安全预想阅读记录
        BuWorkSafeAssumeRead safeAssumeRead = new BuWorkSafeAssumeRead()
                .setSafeAssumeId(safeAssumeId)
                .setUserId(userId)
                .setReadTime(new Date());
        buWorkSafeAssumeReadMapper.insert(safeAssumeRead);

        return true;
    }
}
