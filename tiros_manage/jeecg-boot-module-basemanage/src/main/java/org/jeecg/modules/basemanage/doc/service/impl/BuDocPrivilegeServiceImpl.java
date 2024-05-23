package org.jeecg.modules.basemanage.doc.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.basemanage.doc.bean.BuDocDirectory;
import org.jeecg.modules.basemanage.doc.bean.BuDocDirectoryGroup;
import org.jeecg.modules.basemanage.doc.bean.BuDocDirectoryPerson;
import org.jeecg.modules.basemanage.doc.bean.BuDocPrivilege;
import org.jeecg.modules.basemanage.doc.mapper.*;
import org.jeecg.modules.basemanage.doc.service.BuDocPrivilegeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * 可以对目录授权，也可以对文件授权，权限以最底层节点的权限为准，底层无授权信息则继承上层的设置，如果没有任何授权则表示没有 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
@Service
public class BuDocPrivilegeServiceImpl extends ServiceImpl<BuDocPrivilegeMapper, BuDocPrivilege> implements BuDocPrivilegeService {
    @Resource
    private BuDocPrivilegeMapper privilegeMapper;
    @Resource
    private BuDocDirectoryMapper directoryMapper;
    @Resource
    private BuDocDirectoryPersonMapper directoryPersonMapper;
    @Resource
    private BuDocDirectoryGroupMapper directoryGroupMapper;
    @Resource
    private BuDocFileMapper fileMapper;

    @Override
    public List<BuDocPrivilege> selectByTargetId(String id) {
        return privilegeMapper.selectByTargetId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean savePrivilege(BuDocPrivilege privilege) throws Exception {
        List<String> privileges = Arrays.asList(privilege.getDocPrivileges().split(","));
        List<String> targetIds = Arrays.asList(privilege.getTargetId().split(","));
        char[] pg = {'0', '0', '0', '0', '0'};
        privileges.forEach(p -> {
            pg[Integer.valueOf(p) - 1] = '1';
        });
        privilege.setDocPrivileges(String.valueOf(pg));
        if (privilege.getIsSub() && privilege.getObjType() == 1) {
            threadSaveIsSubShardeDicOrFile(privilege, targetIds);
        } else {
            targetIds.forEach(t -> {
                privilege.setId(UUIDGenerator.generate());
                privilege.setTargetId(t);
                this.save(privilege);
            });
        }
        return true;
    }

    /**
     * 保存子类权限跟父类一样
     * @param privilege
     * @param targetIds
     * @throws InterruptedException
     */
    private void threadSaveIsSubShardeDicOrFile(BuDocPrivilege privilege, List<String> targetIds) throws InterruptedException {
        ExecutorService executorService = ThreadUtil.newExecutor(3);
        CountDownLatch downLatch = ThreadUtil.newCountDownLatch(3);
        executorService.execute(() -> {
            getDicIds(privilege, targetIds);
            downLatch.countDown();
        });
        executorService.execute(() -> {
            getDicPersonIds(privilege, targetIds);
            downLatch.countDown();
        });
        executorService.execute(() -> {
            getDicGroupIds(privilege, targetIds);
            downLatch.countDown();
        });
        downLatch.await();
    }



    private void getDicGroupIds(BuDocPrivilege privilege, List<String> targetIds) {
        BuDocDirectoryGroup directory = directoryGroupMapper.selectById(privilege.getObjId());
        if (directory != null) {
            targetIds.forEach(t -> {
                privilege.setId(UUIDGenerator.generate());
                privilege.setTargetId(t);
                this.save(privilege);
            });
            List<BuDocDirectoryGroup> directories = directoryGroupMapper
                    .selectList(Wrappers.<BuDocDirectoryGroup>lambdaQuery()
                            .eq(BuDocDirectoryGroup::getParentId, directory.getId()));
            if (oConvertUtils.listIsNotEmpty(directories)) {
                directories.forEach(d -> {
                    privilege.setObjId(d.getId());
                    getDicGroupIds(privilege, targetIds);
                });
            }
        }
    }

    private void getDicPersonIds(BuDocPrivilege privilege, List<String> targetIds) {
        BuDocDirectoryPerson directory = directoryPersonMapper.selectById(privilege.getObjId());
        if (directory != null) {
            targetIds.forEach(t -> {
                privilege.setId(UUIDGenerator.generate());
                privilege.setTargetId(t);
                this.save(privilege);
            });
            List<BuDocDirectoryPerson> directories = directoryPersonMapper
                    .selectList(Wrappers.<BuDocDirectoryPerson>lambdaQuery()
                            .eq(BuDocDirectoryPerson::getParentId, directory.getId()));
            if (oConvertUtils.listIsNotEmpty(directories)) {
                directories.forEach(d -> {
                    privilege.setObjId(d.getId());
                    getDicPersonIds(privilege, targetIds);
                });
            }
        }
    }

    private void getDicIds(BuDocPrivilege privilege, List<String> targetIds) {
        BuDocDirectory directory = directoryMapper.selectById(privilege.getObjId());
        if (directory != null) {
            targetIds.forEach(t -> {
                privilege.setId(UUIDGenerator.generate());
                privilege.setTargetId(t);
                this.save(privilege);
            });
            List<BuDocDirectory> directories = directoryMapper
                    .selectList(Wrappers.<BuDocDirectory>lambdaQuery()
                            .eq(BuDocDirectory::getParentId, directory.getId()));
            if (oConvertUtils.listIsNotEmpty(directories)) {
                directories.forEach(d -> {
                    privilege.setObjId(d.getId());
                    getDicIds(privilege, targetIds);
                });
            }
        }
    }
}
