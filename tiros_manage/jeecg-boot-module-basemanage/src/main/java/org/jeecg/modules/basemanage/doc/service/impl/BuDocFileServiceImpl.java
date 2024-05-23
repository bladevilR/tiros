package org.jeecg.modules.basemanage.doc.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.basemanage.doc.bean.*;
import org.jeecg.modules.basemanage.doc.bean.vo.BuDocFileQueryVO;
import org.jeecg.modules.basemanage.doc.mapper.*;
import org.jeecg.modules.basemanage.doc.service.BuDocFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 文件信息 服务实现类
 *
 * @author youGen
 * @since 2020-08-17
 */
@Service
public class BuDocFileServiceImpl extends ServiceImpl<BuDocFileMapper, BuDocFile>
        implements BuDocFileService {
    @Resource
    private BuDocFileMapper fileMapper;
    @Resource
    private BuDocDirectoryGroupMapper directoryGroupMapper;
    @Resource
    private BuDocDirectoryPersonMapper personMapper;
    @Resource
    private BuDocDirectoryMapper docDirectoryMapper;
    @Resource
    private BuDocPrivilegeMapper privilegeMapper;

    @Override
    public Page<BuFileAndDirectory> listPage(
            Page<BuFileAndDirectory> page, BuDocFileQueryVO fileQueryVO) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        String orgCode = sysUser.getOrgCode();
        String workGroupId = directoryGroupMapper.selectWorkGroupId(orgCode);
        List<String> role = directoryGroupMapper.selectRole(userId);
        if (oConvertUtils.listIsNotEmpty(role)) {
            if(role.contains("admin")){
                fileQueryVO.setIsAdmin(true);
            }
            List<String> roleId = directoryGroupMapper.selectRoleId(userId);
            fileQueryVO.setRoles(roleId);
        }
        if (Objects.isNull(fileQueryVO.getShowType())) {
            fileQueryVO.setShowType(0);
        }
        fileQueryVO.setPersonId(userId);
        fileQueryVO.setGroupId(workGroupId);
        if (StrUtil.isNotEmpty(fileQueryVO.getId())) {
            Integer dicType = getDirectoryType(fileQueryVO.getId());
            if (dicType == null) {
                dicType = getDicType(fileQueryVO.getId());
            }
            fileQueryVO.setDirectoryType(dicType);
        }
        List<BuFileAndDirectory> fileList = fileMapper.listPage(page, fileQueryVO);
        return page.setRecords(fileList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatch(List<String> asList) {
        asList.forEach(
                id -> {
                    try {
                        threadDelBatch(id);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    BuDocFile file = fileMapper.selectById(id);
                    if (file != null) {
                        file.setStatus(0);
                        fileMapper.updateById(file);
                        privilegeMapper.delete(
                                Wrappers.<BuDocPrivilege>lambdaUpdate().eq(BuDocPrivilege::getObjId, id));
                    }
                });
    }

    private void threadDelBatch(String id) throws InterruptedException {
        ExecutorService executorService = ThreadUtil.newExecutor(3);
        CountDownLatch downLatch = ThreadUtil.newCountDownLatch(3);
        executorService.execute(
                () -> {
                    BuDocDirectoryGroup docDirectoryGroup = directoryGroupMapper.selectById(id);
                    if (docDirectoryGroup != null) {
                        deleteGroup(id);
                        docDirectoryGroup.setStatus(0);
                        directoryGroupMapper.updateById(docDirectoryGroup);
                    }
                    downLatch.countDown();
                });
        executorService.execute(
                () -> {
                    BuDocDirectoryPerson directoryPerson = personMapper.selectById(id);
                    if (directoryPerson != null) {
                        deletePerson(id);
                        directoryPerson.setStatus(0);
                        personMapper.updateById(directoryPerson);
                    }
                    downLatch.countDown();
                });

        executorService.execute(
                () -> {
                    BuDocDirectory directory = docDirectoryMapper.selectById(id);
                    if (directory != null) {
                        directory.setStatus(0);
                        docDirectoryMapper.updateById(directory);
                        deleteDirectory(id);
                    }
                    downLatch.countDown();
                });

        downLatch.await();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatchFile(List<BuDocFile> file) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        file.forEach(
                f -> {
                    if (StrUtil.isNotEmpty(f.getSharedId())) {
                        insertShareFile(f);
                    }
                    if (StrUtil.isNotBlank(f.getDirectoryId())) {
                        Integer dicType = getDirectoryType(f.getDirectoryId());
                        if (dicType == null) {
                            dicType = getDicType(f.getDirectoryId());
                        }
                        f.setDirectoryType(dicType);
                        setFileBelonger(sysUser, f, dicType);
                    } else {
                        f.setBelonger(sysUser.getId());
                    }
                });

        return this.saveBatch(file);
    }

    @Override
    public boolean isPrivilege(String id, Integer operation) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String workGroupId = directoryGroupMapper.selectWorkGroupId(sysUser.getOrgCode());
        List<String> roleId = directoryGroupMapper.selectRoleId(sysUser.getId());
        List<String> role = directoryGroupMapper.selectRole(sysUser.getId());
        // 如果是管理员直接返回有权限
        if (role.contains("admin")) {
            return true;
        }
        // 获取权限
        List<BuDocPrivilege> privileges = getBuDocPrivilege(id, sysUser, workGroupId, roleId);
        // 如果是文件
        BuDocFile file = fileMapper.selectById(id);
        if (file != null) {
            // 判断是否是自己创建的，否则验证是否有权限
            if (file.getCreateBy().equals(sysUser.getUsername())) {
                return true;
            } else {
                // 是否共享文件
                if (file.getDirectoryType() == 2 && "23".contains(operation.toString())) {
                    return true;
                } else if (file.getDirectoryType() == 3 && workGroupId.equals(file.getBelonger()) && role.contains("groupLeader")) {
                    return true;
                } else {
                    return isPrivilegeAll(operation, privileges);
                }
            }
        } else {
            // 获取目录类型
            Integer dicType = getDirectoryType(id);
            if (dicType == null) {
                dicType = getDicType(id);
            }
            // 根据目录类型分别做判断
            if (dicType != null) {
                BuDocDirectory docDirectory = docDirectoryMapper.selectById(id);
                if (dicType == 2) {
                    return "23".contains(operation.toString());
                } else if (dicType == 1) {
                    BuDocDirectoryPerson dicPerson = personMapper.selectById(id);
                    if (dicPerson != null && dicPerson.getCreateBy().equals(sysUser.getUsername())) {
                        return true;
                    }
                    if(docDirectory != null&& docDirectory.getId().equals("1")){
                        return  true;
                    }
                    return isPrivilegeAll(operation, privileges);
                } else if (dicType == 3) {
                    BuDocDirectoryGroup dicGroup = directoryGroupMapper.selectById(id);
                    if (dicGroup != null) {
                        if(workGroupId.equals(dicGroup.getBelonger()) && role.contains("groupLeader")){
                            return true;
                        }
                        if (dicGroup.getCreateBy().equals(sysUser.getUsername())) {
                            return true;
                        }
                        if (!dicGroup.getBelonger().equals(workGroupId)) {
                            return isPrivilegeAll(operation, privileges);
                        } else {

                            if (oConvertUtils.listIsNotEmpty(privileges)) {
                                return isPrivilegeAll(operation, privileges);
                            } else {
                                return "23".contains(operation.toString());
                            }
                        }
                    }else{
                        if(role.contains("groupLeader")&&docDirectory != null&&docDirectory.getId().equals("3")){
                            return true;
                        }
                    }
                } else {
                    if (docDirectory != null) {
                        if (docDirectory.getCreateBy().equals(sysUser.getUsername())) {
                            return true;
                        }
                    }
                    return isPrivilegeAll(operation, privileges);
                }
            }
        }
        return false;
    }

    private boolean isPrivilegeAll(Integer operation, List<BuDocPrivilege> privileges) {
        if (!oConvertUtils.listIsNotEmpty(privileges)) {
            return false;
        } else {
            boolean flag = false;
            for (BuDocPrivilege privilege : privileges) {
                if (isPrivilegeCharOne(operation, privilege)) {
                    flag = true;
                    break;
                }
            }
            return flag;
        }
    }

    private List<BuDocPrivilege> getBuDocPrivilege(
            String id, LoginUser sysUser, String workGroupId, List<String> roleId) {
        List<BuDocPrivilege> docPrivileges = new ArrayList<>();
        BuDocPrivilege privilege = getBuDocPrivilege(id, sysUser.getId());
        Optional.ofNullable(privilege).ifPresent(docPrivileges::add);
        BuDocPrivilege privilegeGroup = getBuDocPrivilege(id, workGroupId);
        Optional.ofNullable(privilegeGroup).ifPresent(docPrivileges::add);
        for (String role : roleId) {
            BuDocPrivilege privilegeRole = getBuDocPrivilege(id, role);
            Optional.ofNullable(privilegeRole).ifPresent(docPrivileges::add);
        }
        return docPrivileges;
    }

    private boolean isPrivilegeCharOne(Integer operation, BuDocPrivilege privilege) {
        String privileges = privilege.getDocPrivileges();
        char pStr = (privileges.toCharArray()[operation - 1]);
        return pStr == '1';
    }

    private BuDocPrivilege getBuDocPrivilege(String id, String userId) {
        return privilegeMapper.selectOne(
                Wrappers.<BuDocPrivilege>lambdaQuery()
                        .eq(BuDocPrivilege::getObjId, id)
                        .eq(BuDocPrivilege::getTargetId, userId));
    }

    /**
     * 设置文件属于哪个对象
     *
     * @param sysUser
     * @param file
     * @param dicType
     */
    private void setFileBelonger(LoginUser sysUser, BuDocFile file, Integer dicType) {
        if (dicType == 1) {
            file.setBelonger(sysUser.getId());
        } else if (dicType == 3) {
            String workGroupId = directoryGroupMapper.selectWorkGroupId(sysUser.getOrgCode());
            file.setBelonger(workGroupId);
        } else {
            file.setBelonger(file.getDirectoryId());
        }
    }

    /**
     * 保存共享文件
     *
     * @param f
     */
    private void insertShareFile(BuDocFile f) {
        BuDocFile docFile = new BuDocFile();
        BeanUtils.copyProperties(f, docFile);
        docFile.setDirectoryId(f.getSharedId());
        docFile.setDirectoryType(2);
        docFile.setBelonger(f.getSharedId());
        docFile.insert();
    }

    private Integer getDicType(String dicId) {
        Integer dicType = getDirectoryPersonType(dicId);
        if (dicType == null) {
            dicType = getDirectoryGroupType(dicId);
        }
        return dicType;
    }

    private Integer getDirectoryType(String directoryId) {
        BuDocDirectory directory = docDirectoryMapper.selectById(directoryId);
        if (directory == null) {
            return null;
        } else {
            if (StrUtil.isNotBlank(directory.getParentId())) {
                return getDirectoryType(directory.getParentId());
            } else {
                return Integer.valueOf(directoryId);
            }
        }
    }

    private Integer getDirectoryPersonType(String directoryId) {
        BuDocDirectoryPerson directory = personMapper.selectById(directoryId);
        if (directory == null) {
            return getDirectoryType(directoryId);
        }
        return getDirectoryPersonType(directory.getParentId());
    }

    private Integer getDirectoryGroupType(String directoryId) {
        BuDocDirectoryGroup directory = directoryGroupMapper.selectById(directoryId);
        if (directory == null) {
            return getDirectoryType(directoryId);
        }
        return getDirectoryGroupType(directory.getParentId());
    }

    private void deleteDirectory(String id) {
        List<BuDocDirectory> directories =
                docDirectoryMapper.selectList(
                        Wrappers.<BuDocDirectory>lambdaQuery().eq(BuDocDirectory::getParentId, id));
        updateFile(id);
        privilegeMapper.delete(
                Wrappers.<BuDocPrivilege>lambdaUpdate().eq(BuDocPrivilege::getObjId, id));
        deletePerson(id);
        deleteGroup(id);
        if (oConvertUtils.listIsNotEmpty(directories)) {
            directories.forEach(
                    d -> {
                        d.setStatus(0);
                        docDirectoryMapper.updateById(d);
                        deleteDirectory(d.getId());
                    });
        }
    }

    private void deleteGroup(String id) {
        List<BuDocDirectoryGroup> directoryGroups =
                directoryGroupMapper.selectList(
                        Wrappers.<BuDocDirectoryGroup>lambdaQuery().eq(BuDocDirectoryGroup::getParentId, id));

        updateFile(id);
        privilegeMapper.delete(
                Wrappers.<BuDocPrivilege>lambdaUpdate().eq(BuDocPrivilege::getObjId, id));
        if (oConvertUtils.listIsNotEmpty(directoryGroups)) {
            directoryGroups.forEach(
                    d -> {
                        d.setStatus(0);
                        directoryGroupMapper.updateById(d);
                        deleteGroup(d.getId());
                    });
        }
    }

    private void updateFile(String id) {
        List<BuDocFile> files =
                fileMapper.selectList(Wrappers.<BuDocFile>lambdaQuery().eq(BuDocFile::getDirectoryId, id));
        files.forEach(
                f -> {
                    f.setStatus(0);
                    fileMapper.updateById(f);
                });
    }

    private void deletePerson(String id) {
        List<BuDocDirectoryPerson> directoryPersons =
                personMapper.selectList(
                        Wrappers.<BuDocDirectoryPerson>lambdaQuery().eq(BuDocDirectoryPerson::getParentId, id));
        updateFile(id);
        privilegeMapper.delete(
                Wrappers.<BuDocPrivilege>lambdaUpdate().eq(BuDocPrivilege::getObjId, id));
        if (oConvertUtils.listIsNotEmpty(directoryPersons)) {
            directoryPersons.forEach(
                    d -> {
                        d.setStatus(0);
                        personMapper.updateById(d);
                        deletePerson(d.getId());
                    });
        }
    }
}
