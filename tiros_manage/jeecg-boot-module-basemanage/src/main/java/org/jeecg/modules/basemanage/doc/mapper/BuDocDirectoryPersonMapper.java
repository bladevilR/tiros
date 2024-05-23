package org.jeecg.modules.basemanage.doc.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.doc.bean.BuDocDirectoryPerson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 个人创建的目录
 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
public interface BuDocDirectoryPersonMapper extends BaseMapper<BuDocDirectoryPerson> {
    /**
     * 树形结构查询
     * @param
     * @param userId
     * @param id
     * @param roles
     * @return
     */
    List<BuDocDirectoryPerson> listDirectoryTree(@Param("userId") String userId, @Param("id")String id,@Param("roles")List<String> roles,@Param("isAdmin") Boolean isAdmin);
}
