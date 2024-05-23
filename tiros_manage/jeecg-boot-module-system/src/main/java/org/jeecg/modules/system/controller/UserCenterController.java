package org.jeecg.modules.system.controller;

import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author Jak
 * @date 2021/3/14 14:58
 * @version: V1.0
 */
@RestController
@RequestMapping("/center/user/")
public class UserCenterController {
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 1查询用户角色信息
     *
     * @param username
     * @return
     */
    @GetMapping("/roles")
    public Set<String> queryUserRoles(@RequestParam String username) {
        return sysUserService.getUserRolesSet(username);
    }

    /**
     * 2查询用户权限信息
     *
     * @param username
     * @return
     */
    @GetMapping("/auths")
    public Set<String> queryUserAuths(@RequestParam String username) {
        return sysUserService.getUserPermissionsSet(username);
    }

    /**
     * 5根据用户账号查询用户信息
     *
     * @param username
     * @return
     */
    @GetMapping("/info")
    public SysUser getUserByName(@RequestParam String username) {
        SysUser userInfo=sysUserService.getUserByName(username);
        userInfo.getRoles().addAll(sysUserService.getUserRolesSet(username));
        userInfo.setPermissions(sysUserService.getUserPermissionsSet(username));
        return userInfo;
    }
}
