package org.jeecg.modules.basemanage.appfunction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.basemanage.appfunction.bean.AppFunction;
import org.jeecg.modules.basemanage.appfunction.mapper.AppFunctionMapper;
import org.jeecg.modules.basemanage.appfunction.service.AppFunctionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * APP功能模块 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
@Service
public class AppFunctionServiceImpl extends ServiceImpl<AppFunctionMapper, AppFunction> implements AppFunctionService {

    @Resource
    private AppFunctionMapper appFunctionMapper;


    /**
     * @see AppFunctionService#listCurrentUserFunction()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<AppFunction> listCurrentUserFunction() throws Exception {
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        return appFunctionMapper.selectListByUserId(sysUser.getId());
    }

}
