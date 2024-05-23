package org.jeecg.modules.basemanage.appfunction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.appfunction.bean.AppFunction;

import java.util.List;

/**
 * <p>
 * APP功能模块 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
public interface AppFunctionService extends IService<AppFunction> {

    /**
     * 查询当前用户的app功能菜单列表
     *
     * @return 当前用户的app功能菜单列表
     * @throws Exception 异常信息
     */
    List<AppFunction> listCurrentUserFunction() throws Exception;

}
