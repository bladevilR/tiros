package org.jeecg.modules.basemanage.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.org.bean.*;
import org.jeecg.modules.basemanage.org.bean.bo.BuDepartBO;
import org.jeecg.modules.basemanage.org.bean.vo.BuUserQueryVO;
import org.jeecg.modules.basemanage.org.mapper.*;
import org.jeecg.modules.basemanage.org.service.BuUserService;
import org.jeecg.modules.basemanage.workstation.entity.CompanyTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
@Service
public class BuUserServiceImpl implements BuUserService {

    @Resource
    private BuUserMapper buUserMapper;
    @Resource
    private BuUserExtendMapper buUserExtendMapper;
    @Resource
    private BuTraiiningAttendMapper buTraiiningAttendMapper;
    @Resource
    private BuTrainingMapper buTrainingMapper;
    @Resource
    private BuUserCertMapper buUserCertMapper;
    @Resource
    private SysUserTagMapper sysUserTagMapper;
    @Resource
    private SysTagMapper sysTagMapper;


    /**
     * @see BuUserService#selectTreeForOrgUser()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<CompanyTree> selectTreeForOrgUser() throws Exception {
        return buUserMapper.selectCompanyTree();
    }

    /**
     * @see BuUserService#pageUser(BuUserQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuUser> pageUser(BuUserQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuUser> page = buUserMapper.selectUserPage(new Page<>(pageNo, pageSize), queryVO);
        page.getRecords().stream().forEach(item -> {
            if (item.getMonitor() != null) {
                item.setType("1");
            } else if (item.getMonitor2() != null) {
                item.setType("2");
            }
        });
        setUserPositionAndGroup(page.getRecords());
        setUserTagsAndCerts(page.getRecords());
        setRoles(page.getRecords());
        return page;
    }

    /**
     * @see BuUserService#getUser(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuUser getUser(String id) throws Exception {
        BuUser user = buUserMapper.selectUserById(id);

        // 注释 by jakgong 不需要了，前端已通过原系统的获取用户信息的接口获取下面的信息了
        // setUserPositionAndGroup(Collections.singletonList(user));
        setUserTagsAndCerts(Collections.singletonList(user));

        return user;
    }

    /**
     * @see BuUserService#saveUser(BuUser)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean saveUser(BuUser user) throws Exception {
        String userId = user.getId();
        if (StringUtils.isNotBlank(userId)) {
            // 删除关联信息
            deleteUserRelation(userId);
            // 更新用户
            // 注释by jakgong 前段已经调用系统原有接口进行保存了
            // buUserMapper.updateUserById(user);
        }
        // 注释by jakgong 前段已经调用系统原有接口进行保存了
        /*else {
            userId = UUIDGenerator.generate();
            // 设置用户名密码
            // 新增用户
            user.setId(userId);
            buUserMapper.insertUser(user);
        }*/

        // 新增关联信息
        insertUserRelation(user);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteUser(String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        idList.forEach(id -> {
            this.deleteUserRelation(id);
        });
        this.buUserMapper.deleteBatchIds(idList);
        return true;
    }

    /**
     * @see BuUserService#compareUserSkill(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<LinkedHashMap<String, Object>> compareUserSkill(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        // 查询人员
        List<BuUser> userList = buUserMapper.selectBatchIds(idList);
        List<String> userNameList = userList.stream()
                .map(BuUser::getRealname)
                .collect(Collectors.toList());

        // 查询人员技能
        List<BuUserSkill> userSkillList = buUserMapper.selectSkillListByUserIdList(idList);

        // 生成结果集
        Map<String, List<BuUserSkill>> skillNameListMap = userSkillList.stream()
                .collect(Collectors.groupingBy(BuUserSkill::getSkillName));
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<BuUserSkill>> skillNameListEntry : skillNameListMap.entrySet()) {
            String skillName = skillNameListEntry.getKey();
            List<BuUserSkill> skillList = skillNameListEntry.getValue();

            LinkedHashMap<String, Object> skillItemMap = new LinkedHashMap<>();
            skillItemMap.put("userName", userNameList);
            skillItemMap.put("skillName", skillName);

            for (BuUser user : userList) {
                List<BuUserSkill> theUserSkillList = skillList.stream()
                        .filter(skill -> user.getId().equals(skill.getUserId()))
                        .collect(Collectors.toList());

                Long score = 0L;
                if (CollectionUtils.isNotEmpty(theUserSkillList)) {
                    score = theUserSkillList.get(0).getScore();
                }
                skillItemMap.put(user.getRealname(), score);
            }

            result.add(skillItemMap);
        }

        return result;
    }


    private void setUserPositionAndGroup(List<BuUser> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        List<String> userIdList = userList.stream()
                .map(BuUser::getId)
                .collect(Collectors.toList());

        List<BuDepartBO> allUserDepartBOList = buUserMapper.selectDepartBOListByUserIdList(userIdList);
        List<BuPostionInfo> allUserPostionInfoList = buUserMapper.selectPositionListByUserIdList(userIdList);

        for (BuUser user : userList) {
            // 设置班组
            String orgCode = user.getOrgCode();
            List<BuDepartBO> departBOList = allUserDepartBOList.stream()
                    .filter(depart -> user.getId().equals(depart.getUserId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(departBOList)) {
                if (StringUtils.isBlank(orgCode)) {
                    // 不存在用户当前选择的部门orgCode，选择第一个班组
                    for (BuDepartBO departBO : departBOList) {
                        // 4=班组
                        if ("4".equals(departBO.getOrgCategory())) {
                            user.setGroupId(departBO.getId())
                                    .setGroupName(departBO.getDepartName());
                            break;
                        }
                    }
                } else {
                    // 存在用户当前选择的部门orgCode，匹配该班组
                    for (BuDepartBO departBO : departBOList) {
                        // 4=班组
                        if ("4".equals(departBO.getOrgCategory()) && orgCode.equals(departBO.getOrgCode())) {
                            user.setGroupId(departBO.getId())
                                    .setGroupName(departBO.getDepartName());
                            break;
                        }
                    }
                }
            }

            // 设置岗位
            List<BuPostionInfo> postionInfoList = allUserPostionInfoList.stream()
                    .filter(position -> user.getId().equals(position.getUserId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(postionInfoList)) {
                BuPostionInfo firstPosition = postionInfoList.get(0);
                user.setPositionId(firstPosition.getId())
                        .setPositionName(firstPosition.getPositionName())
                        .setPositionWages(firstPosition.getWages());
            }
        }
    }

    private void setUserTagsAndCerts(List<BuUser> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        List<String> idList = userList.stream()
                .map(BuUser::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<SysUserTag> userTagWrapper = new LambdaQueryWrapper<SysUserTag>()
                .in(SysUserTag::getUserId, idList);
        List<SysUserTag> userTagList = sysUserTagMapper.selectList(userTagWrapper);
        LambdaQueryWrapper<BuUserCert> userCertWrapper = new LambdaQueryWrapper<BuUserCert>()
                .in(BuUserCert::getUserId, idList);
        List<BuUserCert> userCertList = buUserCertMapper.selectList(userCertWrapper);

        Date now = new Date();
        for (BuUser user : userList) {
            String userId = user.getId();

            List<String> tagTitleList = userTagList.stream()
                    .filter(tag -> userId.equals(tag.getUserId()))
                    .map(SysUserTag::getTagTitle)
                    .collect(Collectors.toList());
            String tags = userTagList.stream()
                    .filter(tag -> userId.equals(tag.getUserId()))
                    .map(SysUserTag::getTagTitle)
                    .collect(Collectors.joining(","));

            List<BuUserCert> certList = userCertList.stream()
                    .filter(cert -> userId.equals(cert.getUserId()))
                    .filter(cert -> cert.getValidDate().after(now))
                    .collect(Collectors.toList());
            String certs = userCertList.stream()
                    .filter(cert -> userId.equals(cert.getUserId()))
                    .filter(cert -> cert.getValidDate().after(now))
                    .map(BuUserCert::getCertName)
                    .collect(Collectors.joining(","));
            user.setTagTitleList(tagTitleList)
                    .setTags(tags)
                    .setCertList(certList)
                    .setCerts(certs);
        }
    }

    private void setRoles(List<BuUser> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        List<String> idList = userList.stream()
                .map(BuUser::getId)
                .collect(Collectors.toList());

        List<Map<String, Object>> userIdRoleNameMapList = buUserMapper.selectUserIdRoleNameListByUserIdList(idList);
        Map<String, List<String>> userIdRoleNameListMap = new HashMap<>(16);
        for (Map<String, Object> userIdRoleNameMap : userIdRoleNameMapList) {
            String userId = (String) userIdRoleNameMap.get("userId");
            String roleName = (String) userIdRoleNameMap.get("roleName");

            if (!userIdRoleNameListMap.containsKey(userId)) {
                List<String> roleNameList = new ArrayList<>();
                roleNameList.add(roleName);
                userIdRoleNameListMap.put(userId, roleNameList);
            } else {
                List<String> roleNameList = userIdRoleNameListMap.get(userId);
                roleNameList.add(roleName);
            }
        }

        for (BuUser user : userList) {
            String userId = user.getId();

            List<String> roleNameList = userIdRoleNameListMap.get(userId);
            if (CollectionUtils.isNotEmpty(roleNameList)) {
                String roleNames = String.join(",", roleNameList);
                user.setRoleNames(roleNames);
            }
        }
    }

    private void deleteUserRelation(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }

        // 删除岗位信息
        LambdaQueryWrapper<BuUserExtend> userExtendWrapper = new LambdaQueryWrapper<BuUserExtend>()
                .eq(BuUserExtend::getUserId, userId);
        buUserExtendMapper.delete(userExtendWrapper);
        // 删除培训记录关联
        LambdaQueryWrapper<BuTraiiningAttend> traiiningAttendWrapper = new LambdaQueryWrapper<BuTraiiningAttend>()
                .eq(BuTraiiningAttend::getUserId, userId);
        List<BuTraiiningAttend> traiiningAttendList = buTraiiningAttendMapper.selectList(traiiningAttendWrapper);
        List<String> trainingIdList = traiiningAttendList.stream()
                .map(BuTraiiningAttend::getTrainingId)
                .collect(Collectors.toList());
        buTraiiningAttendMapper.delete(traiiningAttendWrapper);
        //TODO-zhaiyantao 2021/1/21 19:30 暂时先删除培训信息，修改需配合前端，修改培训信息添加途径（先添加培训信息，再添加关联信息）
        if (CollectionUtils.isNotEmpty(trainingIdList)) {
            buTrainingMapper.deleteBatchIds(trainingIdList);
        }
        // 删除证书
        LambdaQueryWrapper<BuUserCert> userCertWrapper = new LambdaQueryWrapper<BuUserCert>()
                .eq(BuUserCert::getUserId, userId);
        buUserCertMapper.delete(userCertWrapper);
        // 删除技能标签
        LambdaQueryWrapper<SysUserTag> userTagWrapper = new LambdaQueryWrapper<SysUserTag>()
                .eq(SysUserTag::getUserId, userId);
        sysUserTagMapper.delete(userTagWrapper);
    }

    private void insertUserRelation(BuUser user) {
        if (null == user) {
            return;
        }

        String userId = user.getId();
        // 新增岗位信息
        // 注释by jakgong 前段已经调用系统原有接口进行保存了
       /* String positionId = user.getPositionId();
        if (StringUtils.isNotBlank(positionId)) {
            BuUserExtend userExtend = new BuUserExtend()
                    .setUserId(userId)
                    .setPostionId(positionId);
            buUserExtendMapper.insert(userExtend);
        }*/
        // 新增培训记录
        List<BuTraining> trainingList = user.getTrainingList();
        saveTrainingIfNew(trainingList);
        if (CollectionUtils.isNotEmpty(trainingList)) {
            for (BuTraining training : trainingList) {
                BuTraiiningAttend traiiningAttend = new BuTraiiningAttend()
                        .setUserId(userId)
                        .setTrainingId(training.getId());
                buTraiiningAttendMapper.insert(traiiningAttend);
            }
        }
        // 新增证书
        List<BuUserCert> certList = user.getCertList();
        if (CollectionUtils.isNotEmpty(certList)) {
            for (BuUserCert userCert : certList) {
                userCert.setUserId(userId);
                buUserCertMapper.insert(userCert);
            }
        }
        // 新增技能标签
        List<String> tagTitleList = user.getTagTitleList();
        if (CollectionUtils.isNotEmpty(tagTitleList)) {
            List<SysTag> tagList = sysTagMapper.selectList(Wrappers.emptyWrapper());

            for (String tagTitle : tagTitleList) {
                String tagId = getTagId(tagList, tagTitle);
                SysUserTag userTag = new SysUserTag()
                        .setUserId(userId)
                        .setTagId(tagId)
                        .setTagTitle(tagTitle);
                sysUserTagMapper.insert(userTag);
            }
        }
    }

    private void saveTrainingIfNew(List<BuTraining> trainingList) {
        if (CollectionUtils.isEmpty(trainingList)) {
            return;
        }

        List<BuTraining> dbTrainingList = buTrainingMapper.selectList(Wrappers.emptyWrapper());
        List<String> dbTrainingIdList = dbTrainingList.stream()
                .map(BuTraining::getId)
                .collect(Collectors.toList());

        for (BuTraining training : trainingList) {
            if (StringUtils.isBlank(training.getId())) {
                training.setId(UUIDGenerator.generate());
                buTrainingMapper.insert(training);
            } else {
                if (!dbTrainingIdList.contains(training.getId())) {
                    buTrainingMapper.insert(training);
                }
            }
        }
    }

    private String getTagId(List<SysTag> tagList, String tagTitle) {
        for (SysTag tag : tagList) {
            if (tagTitle.equals(tag.getTagTitle())) {
                return tag.getId();
            }
        }

        String tagId = UUIDGenerator.generate();
        SysTag tag = new SysTag()
                .setId(tagId)
                .setTagTitle(tagTitle)
                .setRemark("保存用户标签时自动新增");
        sysTagMapper.insert(tag);

        return tagId;
    }

}
