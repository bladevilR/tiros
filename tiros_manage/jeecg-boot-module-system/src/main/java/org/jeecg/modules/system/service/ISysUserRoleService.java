package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysUserRole;

import java.util.List;

/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据角色id获取角色关联人员id列表
     *
     * @param roleId 角色id
     * @return 角色关联人员id列表
     */
    List<String> listUserIdByRoleId(String roleId);

}
