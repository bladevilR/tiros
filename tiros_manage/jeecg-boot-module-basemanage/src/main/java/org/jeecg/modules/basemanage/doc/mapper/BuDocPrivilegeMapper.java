package org.jeecg.modules.basemanage.doc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.doc.bean.BuDocPrivilege;

import java.util.List;

/**
 * <p>
 * 可以对目录授权，也可以对文件授权，权限以最底层节点的权限为准，底层无授权信息则继承上层的设置，如果没有任何授权则表示没有 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
public interface BuDocPrivilegeMapper extends BaseMapper<BuDocPrivilege> {
    /**
     * 查询权限
     *
     * @param id
     * @return
     */
    List<BuDocPrivilege> selectByTargetId(@Param("id") String id);
}
