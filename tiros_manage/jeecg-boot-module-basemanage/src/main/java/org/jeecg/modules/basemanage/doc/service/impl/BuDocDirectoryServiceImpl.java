package org.jeecg.modules.basemanage.doc.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.models.auth.In;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.basemanage.doc.bean.*;
import org.jeecg.modules.basemanage.doc.mapper.*;
import org.jeecg.modules.basemanage.doc.service.BuDocDirectoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 目录信息
 * 1 顶级目录(默认创建)：个人文件、共享文件、班组文件、工艺文件，通过id查找区别，分别对应1、2 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
@Service
public class BuDocDirectoryServiceImpl extends ServiceImpl<BuDocDirectoryMapper, BuDocDirectory> implements BuDocDirectoryService {
    @Resource
    private BuDocDirectoryMapper directoryMapper;
    @Resource
    private BuDocDirectoryPersonMapper directoryPersonMapper;
    @Resource
    private BuDocDirectoryGroupMapper directoryGroupMapper;
    @Resource
    private BuDocFileMapper fileMapper;

    @Override
    public List<BuDocDirectory> listDirectoryTree(String id) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String userId = sysUser.getId();
            String orgCode = sysUser.getOrgCode();
            String workGroupId = directoryGroupMapper.selectWorkGroupId(orgCode);
             List<String> roleCode = directoryGroupMapper.selectRole(userId);
              Boolean isAdmin=false;
            if (oConvertUtils.listIsNotEmpty(roleCode)) {
                if(roleCode.contains("admin")){
                    isAdmin=true;
                }
            }
            List<BuDocDirectory> directoryList = directoryMapper.listDirectoryTree(id);
            directoryPackage(id, userId, workGroupId, directoryList,isAdmin);
            return directoryList;
    }

    @Override
    public List<String> listFileType() {
        return fileMapper.selectFileType();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDirectory(BuDocDirectory directory) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = sysUser.getOrgCode();
        String workGroupId = directoryGroupMapper.selectWorkGroupId(orgCode);
        String dicId = UUIDGenerator.generate();
        String parentId = directory.getParentId();
        directory.setId(dicId).setHidden(0).setCreateType(2).setStatus(1);

        //获取第一级父类ID来保存到对应的表
        if (StrUtil.isNotEmpty(parentId)) {
            if (parentId.equals("1") ||
                    Optional.ofNullable(directoryPersonMapper.selectById(parentId)).isPresent()) {
                BuDocDirectoryPerson directoryPerson = getBuDocDirectoryPerson(directory, sysUser.getId(), parentId);
                return directoryPersonMapper.insert(directoryPerson) > 0;
            } else if (parentId.equals("3") ||
                    Optional.ofNullable(directoryGroupMapper.selectById(parentId)).isPresent()) {
                BuDocDirectoryGroup directoryGroup = getBuDocDirectoryGroup(directory, workGroupId, parentId);
                return directoryGroupMapper.insert(directoryGroup) > 0;
            } else {
                return this.save(directory);
            }
        }
        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editFileAndDirectory(BuFileAndDirectory fileAndDirectory) throws Exception {
        String id = fileAndDirectory.getId();
        Integer isFile = fileAndDirectory.getIsFile();
        String name = fileAndDirectory.getName();
        String remark = fileAndDirectory.getRemark();
        if (isFile == 1) {
            BuDocFile docFile = new BuDocFile();
            docFile.setId(id);
            docFile.setName(name);
            docFile.setRemark(remark);
            docFile.setFileTags(fileAndDirectory.getFileTags());
            fileMapper.updateById(docFile);
        } else {
            threadUpdateFileOrDic(id, name, remark);
        }
        return true;
    }

    @Override
    public List<BuDocDirectory> listDirectoryTreeTech() {
        return directoryMapper.listDirectoryTreeTech();
    }

    private void threadUpdateFileOrDic(String id, String name, String remark) throws InterruptedException {
        ExecutorService executorService = ThreadUtil.newExecutor(3);
        CountDownLatch downLatch = ThreadUtil.newCountDownLatch(3);
        executorService.execute(() -> {
            updateDic(id, name, remark);
            downLatch.countDown();
        });
        executorService.execute(() -> {
            updateDicPerson(id, name, remark);
            downLatch.countDown();
        });
        executorService.execute(() -> {
            updateDIcGroup(id, name, remark);
            downLatch.countDown();
        });
        downLatch.await();
    }

    private void updateDIcGroup(String id, String name, String remark) {
        BuDocDirectoryGroup directoryGroup = directoryGroupMapper.selectById(id);
        if (directoryGroup != null) {
            directoryGroup.setName(name);
            directoryGroup.setRemark(remark);
            directoryGroupMapper.updateById(directoryGroup);
        }
    }

    private void updateDicPerson(String id, String name, String remark) {
        BuDocDirectoryPerson directoryPerson = directoryPersonMapper.selectById(id);
        if (directoryPerson != null) {
            directoryPerson.setName(name);
            directoryPerson.setRemark(remark);
            directoryPersonMapper.updateById(directoryPerson);
        }
    }

    private void updateDic(String id, String name, String remark) {
        BuDocDirectory directory = directoryMapper.selectById(id);
        if (directory != null) {
            directory.setName(name);
            directory.setRemark(remark);
            directoryMapper.updateById(directory);
        }
    }

    private BuDocDirectoryGroup getBuDocDirectoryGroup(BuDocDirectory directory, String workGroupId, String parentId) {
        BuDocDirectoryGroup directoryGroup = new BuDocDirectoryGroup();
        directoryGroup.setName(directory.getName());
        directoryGroup.setBelonger(workGroupId);
        directoryGroup.setParentId(parentId);
        directoryGroup.setRemark(directory.getRemark());
        directoryGroup.setStatus(1);
        return directoryGroup;
    }

    private BuDocDirectoryPerson getBuDocDirectoryPerson(BuDocDirectory directory, String userId, String parentId) {
        BuDocDirectoryPerson directoryPerson = new BuDocDirectoryPerson();
        directoryPerson.setBelonger(userId);
        directoryPerson.setName(directory.getName());
        directoryPerson.setParentId(parentId);
        directoryPerson.setRemark(directory.getRemark());
        directoryPerson.setStatus(1);
        return directoryPerson;
    }

    private void directoryPackage(String id, String userId, String workGroupId, List<BuDocDirectory> directoryList,Boolean isAdmin) {
        List<String> roles=directoryGroupMapper.selectRoleId(userId);
        List<BuDocDirectoryPerson> directoryPersons = directoryPersonMapper.listDirectoryTree(userId, id,roles,isAdmin);
        List<BuDocDirectoryGroup> directoryGroups = directoryGroupMapper.listDirectoryTree(workGroupId, id,roles,isAdmin);
        directoryList.forEach(d -> {
            List<BuDocDirectory> directories = new ArrayList<>();
            getDirectoryCustom(id, userId, workGroupId, directoryPersons, directoryGroups, directories, d,isAdmin);
            directories.addAll(d.getChildren());
            d.setChildren(directories);
        });
    }

    private void getDirectoryCustom(String id, String userId, String workGroupId, List<BuDocDirectoryPerson> directoryPersons, List<BuDocDirectoryGroup> directoryGroups, List<BuDocDirectory> docDirectories, BuDocDirectory d,Boolean isAdmin) {
        directoryGroups.forEach(group -> {
            if (group.getParentId().equals(d.getId())) {
                BuDocDirectory directory = getBuDocDirectory(group);
                if (oConvertUtils.listIsNotEmpty(group.getChildren())) {
                    groupConvertDirectory(group.getChildren(), directory);
                }
                docDirectories.add(directory);
            }
        });
        directoryPersons.forEach(person -> {
            if (person.getParentId().equals(d.getId())) {
                BuDocDirectory directory = getBuDocDirectory(person);
                if (oConvertUtils.listIsNotEmpty(person.getChildren())) {
                    personConvertDirectory(person.getChildren(), directory);
                }
                docDirectories.add(directory);
            }
        });
        if (oConvertUtils.listIsNotEmpty(d.getChildren())) {
            directoryPackage(id, userId, workGroupId, d.getChildren(),isAdmin);
        }
    }

    private BuDocDirectory getBuDocDirectory(BuDocDirectoryPerson person) {
        BuDocDirectory directory = new BuDocDirectory();
        BeanUtils.copyProperties(person, directory);
        return directory;
    }

    private BuDocDirectory getBuDocDirectory(BuDocDirectoryGroup group) {
        BuDocDirectory directory = new BuDocDirectory();
        BeanUtils.copyProperties(group, directory);
        return directory;
    }

    private void personConvertDirectory(List<BuDocDirectoryPerson> children, BuDocDirectory directory) {
        List<BuDocDirectory> directoryChills = new ArrayList<>();
        children.forEach(personChild -> {
                    BuDocDirectory directoryChild = getBuDocDirectory(personChild);
                    directoryChills.add(directoryChild);
                    if (oConvertUtils.listIsNotEmpty(personChild.getChildren())) {
                        personConvertDirectory(personChild.getChildren(), directoryChild);
                    }
                }
        );
        directory.setChildren(directoryChills);

    }

    private void groupConvertDirectory(List<BuDocDirectoryGroup> children, BuDocDirectory directory) {
        List<BuDocDirectory> directoryChills = new ArrayList<>();
        children.forEach(groupChild -> {
                    BuDocDirectory directoryChild = getBuDocDirectory(groupChild);
                    directoryChills.add(directoryChild);
                    if (oConvertUtils.listIsNotEmpty(groupChild.getChildren())) {
                        groupConvertDirectory(groupChild.getChildren(), directoryChild);
                    }
                }
        );
        directory.setChildren(directoryChills);
    }

    @Override
    public String selectDirParentId(String id) {
        String dirParentId = getDirParentId(id);
        if (StringUtils.isEmpty(dirParentId)) {
            dirParentId = getDirPersonParentId(id);
        }
        if (StringUtils.isEmpty(dirParentId)) {
            dirParentId = getDirGroupParentId(id);
        }

        return dirParentId;

    }

    @Override
    public List<BuDocDirectory> selectSharedDirectory(String id) throws Exception  {
        return directoryMapper.selectSharedDirectory(id);
    }

    private String getDirParentId(String id) {
        BuDocDirectory directory = directoryMapper.selectById(id);
        if (directory != null) {
            if (StringUtils.isNotEmpty(directory.getParentId())) {
              return   getDirParentId(directory.getParentId());
            } else {
                return directory.getId();
            }
        }else{
            return "";
        }
    }

    private String getDirPersonParentId(String id) {
        BuDocDirectoryPerson directory = directoryPersonMapper.selectById(id);
        if (directory != null) {
            if (StringUtils.isNotEmpty(directory.getParentId())) {
               return getDirPersonParentId(directory.getParentId());
            } else {
                return directory.getId();
            }
        }else {
            return getDirParentId(id);
        }
    }

    private String getDirGroupParentId(String id) {
        BuDocDirectoryGroup directory = directoryGroupMapper.selectById(id);
        if (directory != null) {
            if (StringUtils.isNotEmpty(directory.getParentId())) {
              return   getDirGroupParentId(directory.getParentId());
            } else {
                return directory.getId();
            }
        }else{
            return getDirParentId(id);
        }
    }
}
