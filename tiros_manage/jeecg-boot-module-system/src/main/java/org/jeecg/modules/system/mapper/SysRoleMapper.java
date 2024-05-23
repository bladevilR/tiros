package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysRole;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    void deleteRoleUserRelation(@Param("roleId") String roleId);

    void deleteRolePermissionRelation(@Param("roleId") String roleId);

    SysRole selectByRoleName(@Param("roleName") String roleName);

}
