package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysUserRole;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018 -12-21
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 批量插入
     *
     * @param list 用户角色列表
     */
    void insertList(@Param("list") List<SysUserRole> list);

    /**
     * 根据用户id和角色id批量删除
     *
     * @param list 用户角色列表
     * @return 删除记录数量
     */
    int deleteListByUserIdAndRoleId(@Param("list") List<SysUserRole> list);

    /**
     * Gets role by user name.
     *
     * @param username the username
     * @return the role by user name
     */
    List<String> getRoleByUserName(@Param("username") String username);

    /**
     * Gets role id by user name.
     *
     * @param username the username
     * @return the role id by user name
     */
    List<String> getRoleIdByUserName(@Param("username") String username);

    /**
     * Select user name list by role code list.
     *
     * @param roleCode the role code
     * @return the list
     */
    List<String> selectUserNameListByRoleCode(@Param("roleCode") String roleCode);

    /**
     * Select user id role name map list by user id list list.
     *
     * @param userIdList the user id list
     * @return the list
     */
    List<Map<String, Object>> selectUserIdRoleNameMapListByUserIdList(@Param("userIdList") List<String> userIdList);

}
