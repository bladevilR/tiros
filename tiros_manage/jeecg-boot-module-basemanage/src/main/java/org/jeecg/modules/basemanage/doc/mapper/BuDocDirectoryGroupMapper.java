package org.jeecg.modules.basemanage.doc.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.doc.bean.BuDocDirectoryGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 班组创建的目录，班组文档管理员角色可进行授权
 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
public interface BuDocDirectoryGroupMapper extends BaseMapper<BuDocDirectoryGroup> {
    /**
     * 树结构
     * @param id
     * @return
     */
    List<BuDocDirectoryGroup> listDirectoryTree( @Param("workGroupId") String workGroupId,@Param("id")String id,@Param("roles") List<String> roles,@Param("isAdmin") Boolean isAdmin);

    /**
     * 班组id
     * @param orgCode
     * @return
     */
    String selectWorkGroupId(@Param("orgCode")String orgCode);

    /**
     * 角色
     * @param userId
     * @return
     */
    List<String> selectRole(@Param("userId")String userId);

    /**
     *
     * @param id
     */
    void deleteFile(String id);

    List<String> selectRoleId(@Param("userId")String userId);
}
