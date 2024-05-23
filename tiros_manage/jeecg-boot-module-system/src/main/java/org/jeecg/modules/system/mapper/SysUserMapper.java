package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.SysUserSysDepartModel;
import org.jeecg.modules.system.vo.SysUserDepVo;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018 -12-20
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 批量插入
     *
     * @param list 用户列表
     */
    void insertList(@Param("list") List<SysUser> list);

    /**
     * 批量更新密码
     *
     * @param list 用户列表
     */
    void updateListPassword(@Param("list") List<SysUser> list);

    /**
     * 根据用户名设置部门编码
     *
     * @param username 用户名
     * @param orgCode  部门编码
     */
    void updateUserDepart(@Param("username") String username, @Param("orgCode") String orgCode);

    /**
     * 更新空字符串为null【此写法有sql注入风险，禁止随便用】
     *
     * @param fieldName 字段名
     * @return 更新记录数
     */
    int updateNullByEmptyString(@Param("fieldName") String fieldName);

    /**
     * 批量逻辑删除
     *
     * @param noExistingUserCodeList 人员工号
     * @param updateTime             更新时间
     * @param updateBy               更新人
     */
    void deleteLogicDeletedBatch(@Param("list") List<String> noExistingUserCodeList, @Param("updateTime") Date updateTime, @Param("updateBy") String updateBy);

    /**
     * 批量清空人员orgCode
     *
     * @param userIdList 人员id
     * @param updateTime 更新时间
     * @param updateBy   更新人
     */
    void clearOrgCodeBatch(@Param("list") List<String> userIdList, @Param("updateTime") Date updateTime, @Param("updateBy") String updateBy);

    /**
     * 还原被逻辑删除的用户
     *
     * @param userIds the user ids
     * @param entity  the entity
     * @return the int
     */
    int revertLogicDeleted(@Param("userIds") String userIds, @Param("entity") SysUser entity);

    /**
     * 彻底删除被逻辑删除的用户
     *
     * @param userIds the user ids
     * @return the int
     */
    int deleteLogicDeleted(@Param("userIds") String userIds);

    /**
     * 批量删除角色与用户关系
     *
     * @param roleIdArray 角色id集合
     */
    void deleteBathRoleUserRelation(@Param("roleIdArray") String[] roleIdArray);

    /**
     * 批量删除角色与权限关系
     *
     * @param roleIdArray 角色id集合
     */
    void deleteBathRolePermissionRelation(@Param("roleIdArray") String[] roleIdArray);

    /**
     * 通过用户账号查询用户信息
     *
     * @param username 用户名
     * @return 用户
     */
    SysUser selectUserByUsername(@Param("username") String username);

    /**
     * 根据用户id列表查询用户所属部门名称信息
     *
     * @param userIds 用户id列表
     * @return 用户所属部门名称信息
     */
    List<SysUserDepVo> selectDepNamesByUserIds(@Param("userIds") List<String> userIds);

    /**
     * 根据部门id列表查询部门下用户信息-分页
     *
     * @param page      分页信息
     * @param departIds 部门id列表
     * @param username  用户名
     * @return 用户分页结果
     */
    IPage<SysUser> selectUserPageByDepIds(Page<SysUser> page, @Param("departIds") List<String> departIds, @Param("username") String username);

    /**
     * 根据角色id查询用户信息-分页
     *
     * @param page     分页信息
     * @param roleId   角色id
     * @param username 用户名
     * @return 用户分页结果
     */
    IPage<SysUser> selectUserPageByRoleId(Page<SysUser> page, @Param("roleId") String roleId, @Param("username") String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户
     */
    SysUser getUserByPhone(@Param("phone") String phone);

    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     *
     * @param page       分页对象, xml中可以从里面进行取值,传递参数 Page 即自动分页,必须放在第一位(你可以继承Page实现自己的分页对象)
     * @param orgCode    the org code
     * @param userParams 用户查询条件，可为空
     * @return user by org code
     */
    List<SysUserSysDepartModel> getUserByOrgCode(IPage page, @Param("orgCode") String orgCode, @Param("userParams") SysUser userParams);

    /**
     * 查询 getUserByOrgCode 的Total
     *
     * @param orgCode    the org code
     * @param userParams 用户查询条件，可为空
     * @return user by org code total
     */
    Integer getUserByOrgCodeTotal(@Param("orgCode") String orgCode, @Param("userParams") SysUser userParams);

    /**
     * 查询被逻辑删除的用户
     *
     * @param wrapper the wrapper
     * @return the list
     */
    List<SysUser> selectLogicDeleted(@Param(Constants.WRAPPER) Wrapper<SysUser> wrapper);

    /**
     * 根据班组id获取用户列表
     *
     * @param groupId 班组id
     * @return 用户列表
     */
    List<SysUser> selectListByGroupId(@Param("groupId") String groupId);

    /**
     * 根据班组id和搜索文本获取班组人员列表
     *
     * @param groupId    班组id
     * @param searchText 用户名或账号
     * @return 用户列表
     */
    List<SysUser> selectListByGroupIdAndSearchText(@Param("groupId") String groupId, @Param("searchText") String searchText);

}
