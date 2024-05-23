package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.SysUserCacheInfo;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.SysUserSysDepartModel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ISysUserService extends IService<SysUser> {

    void logout(HttpServletRequest request);


    /**
     * 重置密码
     *
     * @param username        用户名
     * @param oldpassword     旧密码
     * @param newpassword     新密码
     * @param confirmpassword 新密码确认
     * @return
     */
    Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword);

    /**
     * 重置密码
     * 管理员不修改，工号非空的密码重置=工号@sz-mtr，工号为空的重置=用户名@sz-mtr
     *
     * @param usernames 用户名，多个逗号分隔
     * @param resetAll  更新所有用户（当用户名不传参时有用）
     * @return 重置信息
     * @throws Exception 异常
     */
    String resetPasswordNew(String usernames, Boolean resetAll) throws Exception;

    /**
     * 修改密码
     *
     * @param sysUser
     * @return
     */
    Result<?> changePassword(SysUser sysUser);

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    boolean deleteUser(String userId);

    /**
     * 批量删除用户
     *
     * @param userIds
     * @return
     */
    boolean deleteBatchUsers(String userIds);

    SysUser getUserByName(String username);

    /**
     * 添加用户和用户角色关系
     *
     * @param user
     * @param roles
     */
    void addUserWithRole(SysUser user, String roles);


    /**
     * 修改用户和用户角色关系
     *
     * @param user
     * @param roles
     */
    void editUserWithRole(SysUser user, String roles);

    /**
     * 查询用户信息包括 部门信息
     *
     * @param username
     * @return
     */
    SysUserCacheInfo getCacheUser(String username);

    /**
     * 根据部门Ids查询
     *
     * @param
     * @return
     */
    IPage<SysUser> getUserByDepIds(Page<SysUser> page, List<String> departIds, String username);

    /**
     * 根据 userIds查询，查询用户所属部门的名称（多个部门名逗号隔开）
     *
     * @param
     * @return
     */
    Map<String, String> getDepNamesByUserIds(List<String> userIds);

    Map<String, String> getRoleNamesByUserIdList(List<String> userIdList);

    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     *
     * @param orgCode
     * @param userParams 用户查询条件，可为空
     * @param page       分页参数
     * @return
     */
    IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, SysUser userParams, IPage page);

    /**
     * 根据角色Id查询
     *
     * @param
     * @return
     */
    IPage<SysUser> getUserByRoleId(Page<SysUser> page, String roleId, String username);

    /**
     * 通过用户名获取用户角色集合
     *
     * @param username 用户名
     * @return 角色集合
     */
    Set<String> getUserRolesSet(String username);

    /**
     * 通过用户名获取用户权限集合
     *
     * @param username 用户名
     * @return 权限集合
     */
    Set<String> getUserPermissionsSet(String username);

    /**
     * 根据用户名设置部门ID
     *
     * @param username
     * @param orgCode
     */
    void updateUserDepart(String username, String orgCode);

    /**
     * 根据手机号获取用户名和密码
     */
    SysUser getUserByPhone(String phone);

    /**
     * 添加用户和用户部门关系
     *
     * @param user
     * @param selectedParts
     */
    void addUserWithDepart(SysUser user, String selectedParts);

    /**
     * 编辑用户和用户部门关系
     *
     * @param user
     * @param departs
     */
    void editUserWithDepart(SysUser user, String departs);

    /**
     * 校验用户是否有效
     *
     * @param sysUser
     * @return
     */
    Result checkUserIsEffective(SysUser sysUser);

    /**
     * 查询被逻辑删除的用户
     */
    List<SysUser> queryLogicDeleted();

    /**
     * 查询被逻辑删除的用户（可拼装查询条件）
     */
    List<SysUser> queryLogicDeleted(LambdaQueryWrapper<SysUser> wrapper);

    /**
     * 还原被逻辑删除的用户
     */
    boolean revertLogicDeleted(List<String> userIds, SysUser updateEntity);

    /**
     * 彻底删除被逻辑删除的用户
     */
    boolean removeLogicDeleted(List<String> userIds);

    /**
     * 更新手机号、邮箱空字符串为 null
     */
    boolean updateNullPhoneEmail();

    /**
     * 保存第三方用户信息
     *
     * @param sysUser
     */
    void saveThirdUser(SysUser sysUser);

    /**
     * 根据班组id获取班组人员信息
     *
     * @param groupId 班组id
     * @return 班组人员信息列表
     * @throws Exception 异常信息
     */
    List<SysUser> listByGroupId(String groupId) throws Exception;

    /**
     * APP-我的二维码生成
     *
     * @param userId 用户id
     * @return 二维码图片 Base64
     */
    String getMyQRCode(String userId, String userName);

    /**
     * 根据班组和搜索文本查询用户
     *
     * @param groupId    班组id
     * @param searchText 用户名和账号
     * @return 用户列表
     */
    List<SysUser> selectListByGroupIdAndSearchText(String groupId, String searchText);

}
