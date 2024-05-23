package org.jeecg.modules.basemanage.doc.service;

import org.jeecg.modules.basemanage.doc.bean.BuDocPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 可以对目录授权，也可以对文件授权，权限以最底层节点的权限为准，底层无授权信息则继承上层的设置，如果没有任何授权则表示没有 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
public interface BuDocPrivilegeService extends IService<BuDocPrivilege> {
    /**
     * 查询权限
     * @param id
     * @return
     */
    List<BuDocPrivilege> selectByTargetId(String id);

    /**
     * 设置权限
     * @param privilege
     * @return
     */
    boolean savePrivilege(BuDocPrivilege privilege) throws Exception;

}
