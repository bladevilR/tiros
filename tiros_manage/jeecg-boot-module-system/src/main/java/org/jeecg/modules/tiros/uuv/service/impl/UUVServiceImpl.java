package org.jeecg.modules.tiros.uuv.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.ADBBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.org.bean.BuPostionInfo;
import org.jeecg.modules.basemanage.org.bean.BuUserExtend;
import org.jeecg.modules.basemanage.org.mapper.BuPostionInfoMapper;
import org.jeecg.modules.basemanage.org.mapper.BuUserExtendMapper;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.tiros.importer.utils.ConvertUtil;
import org.jeecg.modules.tiros.uuv.client.MD5;
import org.jeecg.modules.tiros.uuv.client.UUVGetStub;
import org.jeecg.modules.tiros.uuv.service.UUVService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * UUV同步服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/1/25
 */
@Slf4j
@Service
public class UUVServiceImpl implements UUVService {

    @Resource
    private SysDepartMapper sysDepartMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private BuUserExtendMapper buUserExtendMapper;
    @Resource
    private BuPostionInfoMapper buPostionInfoMapper;
    @Resource
    private SysUserDepartMapper sysUserDepartMapper;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private ISysDepartService sysDepartService;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private SysConfigService sysConfigService;

    @Value(value = "${tiros.sync.address}")
    private String syncAddress;
    @Value(value = "${tiros.sync.account}")
    private String account;
    @Value(value = "${tiros.sync.password}")
    private String password;

    private static final String SYNC_DEFAULT_USER_ROLE = "架大修员工";
    private static final String SYNC_DEFAULT_GROUP_USER_ROLE = "维修员";
    private static final List<String> SYNC_CANNOT_DELETE_USERNAME_LIST = Arrays.asList(
            "admin", "baofeng", "qianyi1", "zhangtingting", "luwenxue", "renxinbing", "liusen", "liuyong",
            "800901", "800900", "led", "clzx", "jxdd", "work1");

    /**
     * @see UUVService#syncDeptAndUser(boolean, String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String syncDeptAndUser(boolean syncUser, String userId, String syncWorkshopId) throws RuntimeException {
        Date now = new Date();
        if (StringUtils.isBlank(userId)) {
            // 获取登录人信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            userId = null == sysUser ? "admin" : sysUser.getId();
        }

        // 获取车间编码
        List<String> workshopIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(syncWorkshopId)) {
            workshopIdList.add(syncWorkshopId);
        } else {
            String currentWorkshopId = sysConfigService.getConfigValueByCode("current_workshop");
            if (StringUtils.isBlank(currentWorkshopId) || "0".equals(currentWorkshopId)) {
                throw new JeecgBootException("未指定同步的车间，请先配置当前车间");
            }
            List<String> currentWorkshopIdList = Arrays.asList(currentWorkshopId.split(","));
            workshopIdList.addAll(currentWorkshopIdList);
        }


        log.info("UUV同步服务：开始同步车间数据，车间有：" + String.join(",", workshopIdList));

        UUVGetStub stub;
        UUVGetStub.ESBSecurityTokenE eSBSecurityToken;
//        List<UUVGetStub.ADDepartment> uuvDepartList;
        try {
            stub = new UUVGetStub(syncAddress);
            eSBSecurityToken = (UUVGetStub.ESBSecurityTokenE) getObject(UUVGetStub.ESBSecurityTokenE.class);
            eSBSecurityToken.setESBSecurityToken(createESBSecurityToken());
        } catch (AxisFault | InstantiationException | IllegalAccessException axisFault) {
            throw new JeecgBootException("同步数据服务器出错!");
        }

        StringBuilder syncInfo = new StringBuilder();
        String departSyncInfo = "";
        List<UUVGetStub.ADUser> uuvUserList = new ArrayList<>();

        // 获取所有部门
        List<SysDepart> sysDepartList = sysDepartMapper.selectList(Wrappers.emptyWrapper());
        Map<String, SysDepart> sysCodeDepartMap = sysDepartList.stream()
                .collect(Collectors.toMap(SysDepart::getOrgCode, Function.identity()));
        for (String workshopId : workshopIdList) {
            SysDepart workshop = sysDepartMapper.selectById(workshopId);
            if (null == workshop) {
                throw new JeecgBootException("无对应车间（id=" + workshopId + "），请检查");
            }
            String orgCode = workshop.getOrgCode();
            if (StringUtils.isBlank(orgCode) || orgCode.length() != 9) {
                throw new JeecgBootException(String.format("车间%s（id=%s）的编码不对，请检查并设置为uuv编码", workshop.getDepartName(), workshopId));
            }

            // 从uuv获取信息
            List<UUVGetStub.ADDepartment> uuvDepartList = getDepartMeantList(stub, eSBSecurityToken, orgCode);
            if (CollectionUtils.isNotEmpty(uuvDepartList)) {
                uuvDepartList.sort(Comparator.comparing(UUVGetStub.ADDepartment::getDeptCode, Comparator.nullsLast(Comparator.naturalOrder())));
                Map<String, UUVGetStub.ADDepartment> uuvCodeDepartMap = uuvDepartList.stream()
                        .collect(Collectors.toMap(UUVGetStub.ADDepartment::getDeptCode, Function.identity()));

                // 部门
                departSyncInfo = transformDepartAndSave(uuvCodeDepartMap, sysCodeDepartMap, now, userId);

                // 人员：默认同步
                List<UUVGetStub.ADUser> workshopUuvUserList = getUserList(stub, eSBSecurityToken, orgCode, uuvCodeDepartMap);
                uuvUserList.addAll(workshopUuvUserList);
            } else {
                syncInfo.append(String.format("车间%s（id=%s）未从UUV获取到组织信息，无法同步", workshop.getDepartName(), workshopId)).append("。");
            }
        }

        // 获取所有人员
        List<SysUser> sysUserList = sysUserMapper.selectList(Wrappers.emptyWrapper());
        Map<String, SysUser> sysCodeUserMap = sysUserList.stream()
                .collect(Collectors.toMap(SysUser::getUsername, Function.identity()));
        if (syncUser) {
            // 人员：默认同步
            uuvUserList.sort(Comparator.comparing(UUVGetStub.ADUser::getAccountName, Comparator.nullsLast(Comparator.naturalOrder())));
            Map<String, UUVGetStub.ADUser> uuvCodeUserMap = uuvUserList.stream()
                    .collect(Collectors.toMap(UUVGetStub.ADUser::getAccountName, Function.identity()));

//                    // 获取车间下人员
//                    List<SysUser> sysUserList = new ArrayList<>();
//                    List<String> workshopUserIdList = getWorkshopUserIdList(workshopId);
//                    if (CollectionUtils.isNotEmpty(workshopUserIdList)) {
//                        List<List<String>> workshopUserIdBatchSubList = DatabaseBatchSubUtil.batchSubList(workshopUserIdList);
//                        for (List<String> workshopUserIdBatchSub : workshopUserIdBatchSubList) {
//                            List<SysUser> subUserList = sysUserMapper.selectBatchIds(workshopUserIdBatchSub);
//                            sysUserList.addAll(subUserList);
//                        }
//                    }
//                    Map<String, SysUser> sysCodeUserMap = sysUserList.stream()
//                            .collect(Collectors.toMap(SysUser::getUsername, Function.identity()));

            String userSyncInfo = transformUserAndSave(uuvCodeUserMap, sysCodeUserMap, sysCodeDepartMap, now, userId);

            syncInfo.append(departSyncInfo).append(userSyncInfo).append("。");
        } else {
            syncInfo.append(departSyncInfo).append("。");
        }

        log.info("UUV同步服务：同步结束，同步结果：" + syncInfo.toString());
        return syncInfo.toString();
    }

    private String transformDepartAndSave(Map<String, UUVGetStub.ADDepartment> uuvCodeDepartMap,
                                          Map<String, SysDepart> sysCodeDepartMap,
                                          Date now,
                                          String userId) {
        List<SysDepart> needAddSysDepartList = new ArrayList<>();
        List<SysDepart> needUpdateSysDepartList = new ArrayList<>();

        // 对比并处理部门信息
        for (Map.Entry<String, UUVGetStub.ADDepartment> uuvCodeDepartEntry : uuvCodeDepartMap.entrySet()) {
            String code = uuvCodeDepartEntry.getKey();
            UUVGetStub.ADDepartment uuvDepart = uuvCodeDepartEntry.getValue();

            if (sysCodeDepartMap.containsKey(code)) {
                // 有，更新
                SysDepart sysDepart = sysCodeDepartMap.get(code);
                updateSysDepartInfo(sysDepart, uuvDepart, now, userId);

                sysCodeDepartMap.put(code, sysDepart);
                needUpdateSysDepartList.add(sysDepart);
            } else {
                // 没有，添加
                SysDepart sysDepart = createSysDepartInfo(uuvDepart, now, userId);

                sysCodeDepartMap.put(code, sysDepart);
                needAddSysDepartList.add(sysDepart);
            }
        }
        // 设置上级部门
        for (Map.Entry<String, SysDepart> sysCodeDepartEntry : sysCodeDepartMap.entrySet()) {
            String code = sysCodeDepartEntry.getKey();
            SysDepart sysDepart = sysCodeDepartEntry.getValue();

            UUVGetStub.ADDepartment uuvDepart = uuvCodeDepartMap.get(code);
            if (null != uuvDepart) {
                String parentDeptCode = uuvDepart.getParentDeptCode();
                SysDepart parentSysDepart = sysCodeDepartMap.get(parentDeptCode);
                if (null != parentSysDepart) {
                    sysDepart.setParentId(parentSysDepart.getId());
                    sysCodeDepartMap.put(code, sysDepart);
                }
            }
        }

        StringBuilder syncInfoStringBuilder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(needAddSysDepartList)) {
            Map<String, List<SysDepart>> idListMap = new HashMap<>();
            for (SysDepart depart : needAddSysDepartList) {
                String id = depart.getId();
                List<SysDepart> departList = idListMap.getOrDefault(id, new ArrayList<>());
                departList.add(depart);
                idListMap.put(id, departList);
            }
            Map<String, List<SysDepart>> repeatMap = new HashMap<>();
            for (Map.Entry<String, List<SysDepart>> idListEntry : idListMap.entrySet()) {
                if (idListEntry.getValue().size() > 1) {
                    repeatMap.put(idListEntry.getKey(), idListEntry.getValue());
                }
            }
            if (!repeatMap.isEmpty()) {
                throw new JeecgBootException(String.format("新增的%s个部门，中有%s个重复的，数据如下：%s",
                        needAddSysDepartList.size(),
                        repeatMap.size(), JSON.toJSONString(repeatMap)));
            }
            log.info(String.format("新增的%s个部门数据：", JSON.toJSONString(needAddSysDepartList)));

            List<List<SysDepart>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddSysDepartList);
            for (List<SysDepart> batchSub : batchSubList) {
                sysDepartService.saveBatch(batchSub);
            }

            syncInfoStringBuilder.append(String.format("新增了%s个部门；", needAddSysDepartList.size()));
        }
        if (CollectionUtils.isNotEmpty(needUpdateSysDepartList)) {
            List<List<SysDepart>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateSysDepartList);
            for (List<SysDepart> batchSub : batchSubList) {
                sysDepartService.updateBatchById(batchSub);
            }

            syncInfoStringBuilder.append(String.format("更新了%s个部门；", needUpdateSysDepartList.size()));
        }
        return syncInfoStringBuilder.toString();
    }

    private SysDepart createSysDepartInfo(UUVGetStub.ADDepartment uuvDepart, Date now, String userId) {
        String code = uuvDepart.getDeptCode();

        SysDepart sysDepart = new SysDepart()
                .setId(code)
                .setParentId(null)
                .setDepartName(uuvDepart.getDeptFullName())
                .setDepartNameEn(null)
                .setDepartNameAbbr(uuvDepart.getDeptName())
                .setDepartOrder(uuvDepart.getOrderNo())
                .setDescription(uuvDepart.getGroupName())
                .setOrgCategory(null)
                .setOrgType("2")
                .setOrgCode(code)
                .setMobile(uuvDepart.getTelephone())
                .setFax(uuvDepart.getFax())
                .setAddress(null)
                .setMemo(null)
                .setStatus("enabled".equals(uuvDepart.getStatus()) ? "1" : "0")
                .setDelFlag("enabled".equals(uuvDepart.getStatus()) ? "0" : "1")
                .setCreateTime(now)
                .setCreateBy(userId)
                .setIsSync(1)
                .setMaximoCode(code);

        if (uuvDepart.getDeptFullName().endsWith("工班")) {
            sysDepart.setOrgCategory("4");
        } else {
            sysDepart.setOrgCategory("2");
        }
        setDepartAddress(uuvDepart, sysDepart);

        return sysDepart;
    }

    private void updateSysDepartInfo(SysDepart sysDepart, UUVGetStub.ADDepartment uuvDepart, Date now, String userId) {
        sysDepart.setDepartName(uuvDepart.getDeptFullName())
                .setOrgCode(uuvDepart.getDeptCode())
                .setStatus("enabled".equals(uuvDepart.getStatus()) ? "1" : "0")
                .setDelFlag("enabled".equals(uuvDepart.getStatus()) ? "0" : "1")
                .setUpdateTime(now)
                .setUpdateBy(userId)
                .setIsSync(1)
                .setMaximoCode(uuvDepart.getDeptCode());

        if (StringUtils.isBlank(sysDepart.getDescription())) {
            sysDepart.setDescription(uuvDepart.getGroupName());
        }
        if (StringUtils.isBlank(sysDepart.getMobile())) {
            sysDepart.setMobile(uuvDepart.getTelephone());
        }
        if (StringUtils.isBlank(sysDepart.getFax())) {
            sysDepart.setFax(uuvDepart.getFax());
        }
        setDepartAddress(uuvDepart, sysDepart);
        if (StringUtils.isBlank(sysDepart.getOrgCategory())) {
            if (uuvDepart.getDeptFullName().endsWith("工班")) {
                sysDepart.setOrgCategory("4");
            } else {
                sysDepart.setOrgCategory("2");
            }
        }
    }

    private void setDepartAddress(UUVGetStub.ADDepartment uuvDepart, SysDepart sysDepart) {
        if (StringUtils.isBlank(sysDepart.getAddress())) {
            if (StringUtils.isNotBlank(uuvDepart.getRegion()) || StringUtils.isNotBlank(uuvDepart.getStreet())) {
                String region = StringUtils.isNotBlank(uuvDepart.getRegion()) ? uuvDepart.getRegion() : "";
                String street = StringUtils.isNotBlank(uuvDepart.getStreet()) ? uuvDepart.getStreet() : "";
                sysDepart.setAddress(region + street);
            }
        }
    }

    private List<String> getWorkshopUserIdList(String workshopId) {
        List<String> workshopAndChildDepartIdList = getWorkshopAndChildDepartIdList(workshopId);

        LambdaQueryWrapper<SysUserDepart> userDepartWrapper = new LambdaQueryWrapper<SysUserDepart>()
                .in(SysUserDepart::getDepId, workshopAndChildDepartIdList);
        List<SysUserDepart> userDepartList = sysUserDepartMapper.selectList(userDepartWrapper);
        return userDepartList.stream()
                .map(SysUserDepart::getUserId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getWorkshopAndChildDepartIdList(String workshopId) {
        List<SysDepart> departList = sysDepartMapper.selectList(Wrappers.emptyWrapper());

        Set<String> childIdSet = new HashSet<>();
        childIdSet.add(workshopId);
        // 递归添加子节点id集合
        recurseAddChildId(workshopId, departList, childIdSet);

        return new ArrayList<>(childIdSet);
    }

    private void recurseAddChildId(String parentId, List<SysDepart> departList, Set<String> childIdSet) {
        List<SysDepart> childList = departList.stream()
                .filter(item -> parentId.equals(item.getParentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(childList)) {
            for (SysDepart child : childList) {
                String childId = child.getId();

                childIdSet.add(childId);
                recurseAddChildId(childId, departList, childIdSet);
            }
        }
    }

    private String transformUserAndSave(Map<String, UUVGetStub.ADUser> uuvCodeUserMap,
                                        Map<String, SysUser> sysCodeUserMap,
                                        Map<String, SysDepart> sysCodeDepartMap,
                                        Date now,
                                        String userId) {
        List<SysUser> needAddSysUserList = new ArrayList<>();
        List<SysUser> needUpdateSysUserList = new ArrayList<>();

        // 对比并处理部门信息
        for (Map.Entry<String, UUVGetStub.ADUser> uuvCodeUserEntry : uuvCodeUserMap.entrySet()) {
            String code = uuvCodeUserEntry.getKey();
            UUVGetStub.ADUser uuvUser = uuvCodeUserEntry.getValue();

            if (sysCodeUserMap.containsKey(code)) {
                // 有，更新
                SysUser sysUser = sysCodeUserMap.get(code);
                updateSysUserInfo(sysUser, uuvUser, now, userId);

                sysCodeUserMap.put(code, sysUser);
                needUpdateSysUserList.add(sysUser);
            } else {
                // 没有，添加
                SysUser sysUser = createSysUserInfo(uuvUser, now, userId);

                sysCodeUserMap.put(code, sysUser);
                needAddSysUserList.add(sysUser);
            }
        }
        // 查询相关关联信息
        List<BuPostionInfo> positionList = buPostionInfoMapper.selectList(Wrappers.emptyWrapper());
        Map<String, BuPostionInfo> namePositionMap = positionList.stream()
                .collect(Collectors.toMap(BuPostionInfo::getPositionName, Function.identity()));
        SysRole sysRole = sysRoleMapper.selectByRoleName(SYNC_DEFAULT_USER_ROLE);
        String defaultRoleId = null;
        if (null != sysRole) {
            defaultRoleId = sysRole.getId();
        }
        SysRole sysGroupRole = sysRoleMapper.selectByRoleName(SYNC_DEFAULT_GROUP_USER_ROLE);
        String defaultGroupRoleId = null;
        if (null != sysGroupRole) {
            defaultGroupRoleId = sysGroupRole.getId();
        }
        // 设置关联信息
        List<BuPostionInfo> needAddPositionList = new ArrayList<>();
        List<SysUserDepart> userDepartList = new ArrayList<>();
        List<SysUserRole> userRoleList = new ArrayList<>();
        List<BuUserExtend> userExtendList = new ArrayList<>();
        for (Map.Entry<String, SysUser> sysCodeUserEntry : sysCodeUserMap.entrySet()) {
            String code = sysCodeUserEntry.getKey();
            SysUser sysUser = sysCodeUserEntry.getValue();

            UUVGetStub.ADUser uuvUser = uuvCodeUserMap.get(code);
            if (null != uuvUser) {
                // 用户与部门关联
                SysDepart sysDepart = sysCodeDepartMap.get(uuvUser.getDeptCode());
                if (null != sysDepart) {
                    SysUserDepart userDepart = new SysUserDepart(UUIDGenerator.generate(), sysUser.getId(), sysDepart.getId());
                    userDepartList.add(userDepart);
                }

                // 用户与角色关联
                if (StringUtils.isNotBlank(defaultRoleId)) {
                    SysUserRole sysUserRole = new SysUserRole()
                            .setId(UUIDGenerator.generate())
                            .setRoleId(defaultRoleId)
                            .setUserId(sysUser.getId());
                    userRoleList.add(sysUserRole);
                }
                if (StringUtils.isNotBlank(defaultGroupRoleId) && null != sysDepart && sysDepart.getDepartName().contains("工班")) {
                    SysUserRole sysUserRole = new SysUserRole()
                            .setId(UUIDGenerator.generate())
                            .setRoleId(defaultGroupRoleId)
                            .setUserId(sysUser.getId());
                    userRoleList.add(sysUserRole);
                }

                // 用户与职位关联
                if (StringUtils.isNotBlank(uuvUser.getPosition())) {
                    String position = uuvUser.getPosition();

                    BuPostionInfo positionInfo = namePositionMap.get(position);
                    if (null == positionInfo) {
                        positionInfo = new BuPostionInfo()
                                .setId(UUIDGenerator.generate())
                                .setPositionName(position)
                                .setRemark(uuvUser.getPositionType() + (StringUtils.isBlank(uuvUser.getPositionCode()) ? "" : "(" + uuvUser.getPositionCode() + ")"))
                                .setWages(null);

                        needAddPositionList.add(positionInfo);
                        namePositionMap.put(positionInfo.getPositionName(), positionInfo);
                    }
                    BuUserExtend userExtend = new BuUserExtend()
                            .setUserId(sysUser.getId())
                            .setPostionId(positionInfo.getId())
                            .setSystemId(null);
                    userExtendList.add(userExtend);
                }
            }
        }

        StringBuilder syncInfoStringBuilder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(needAddSysUserList)) {
            Map<String, List<SysUser>> idListMap = new HashMap<>();
            for (SysUser user : needAddSysUserList) {
                String id = user.getId();
                List<SysUser> userList = idListMap.getOrDefault(id, new ArrayList<>());
                userList.add(user);
                idListMap.put(id, userList);
            }
            Map<String, List<SysUser>> repeatMap = new HashMap<>();
            for (Map.Entry<String, List<SysUser>> idListEntry : idListMap.entrySet()) {
                if (idListEntry.getValue().size() > 1) {
                    repeatMap.put(idListEntry.getKey(), idListEntry.getValue());
                }
            }
            if (!repeatMap.isEmpty()) {
                throw new JeecgBootException(String.format("新增的%s个用户，中有%s个重复的，数据如下：%s",
                        needAddSysUserList.size(),
                        repeatMap.size(), JSON.toJSONString(repeatMap)));
            }
            log.info(String.format("新增的%s个用户数据：", JSON.toJSONString(needAddSysUserList)));

            List<List<SysUser>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddSysUserList);
            for (List<SysUser> batchSub : batchSubList) {
                sysUserMapper.insertList(batchSub);
            }

            syncInfoStringBuilder.append(String.format("新增了%s个用户；", needAddSysUserList.size()));
        }
        if (CollectionUtils.isNotEmpty(needUpdateSysUserList)) {
            List<List<SysUser>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateSysUserList);
            for (List<SysUser> batchSub : batchSubList) {
                sysUserService.updateBatchById(batchSub);
            }

            syncInfoStringBuilder.append(String.format("更新了%s个用户；", needUpdateSysUserList.size()));
        }
        if (CollectionUtils.isNotEmpty(needAddPositionList)) {
            List<List<BuPostionInfo>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddPositionList);
            for (List<BuPostionInfo> batchSub : batchSubList) {
                buPostionInfoMapper.insertList(batchSub);
            }

            syncInfoStringBuilder.append(String.format("新增了%s个岗位；", needAddPositionList.size()));
        }
        if (CollectionUtils.isNotEmpty(userDepartList)) {
            List<List<SysUserDepart>> batchSubList = DatabaseBatchSubUtil.batchSubList(userDepartList);
            for (List<SysUserDepart> batchSub : batchSubList) {
                // 删除人员旧的部门
                List<String> needDeleteUserIdList = batchSub.stream()
                        .map(SysUserDepart::getUserId)
                        .distinct()
                        .collect(Collectors.toList());
                LambdaQueryWrapper<SysUserDepart> deleteWrapper = new LambdaQueryWrapper<SysUserDepart>()
                        .in(SysUserDepart::getUserId, needDeleteUserIdList);
                sysUserDepartMapper.delete(deleteWrapper);
//                sysUserDepartMapper.deleteListByUserIdAndDepartId(batchSub);
                sysUserDepartMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            List<List<SysUserRole>> batchSubList = DatabaseBatchSubUtil.batchSubList(userRoleList);
            for (List<SysUserRole> batchSub : batchSubList) {
                sysUserRoleMapper.deleteListByUserIdAndRoleId(batchSub);
                sysUserRoleMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(userExtendList)) {
            List<List<BuUserExtend>> batchSubList = DatabaseBatchSubUtil.batchSubList(userExtendList);
            for (List<BuUserExtend> batchSub : batchSubList) {
                List<String> userIdList = batchSub.stream()
                        .map(BuUserExtend::getUserId)
                        .collect(Collectors.toList());
                LambdaQueryWrapper<BuUserExtend> deleteUserExtendWrapper = new LambdaQueryWrapper<BuUserExtend>()
                        .in(BuUserExtend::getUserId, userIdList);
                buUserExtendMapper.delete(deleteUserExtendWrapper);
                buUserExtendMapper.insertList(batchSub);
            }
        }
        // 清除不在此架大修车间的用户的部门信息
        Set<String> uuvUsernameSet = uuvCodeUserMap.keySet();
        Set<String> hasDepartUserIdSet = userDepartList.stream()
                .map(SysUserDepart::getUserId)
                .collect(Collectors.toSet());
        List<String> needClearUseIdList = sysCodeUserMap.values().stream()
                .filter(user -> 1 == user.getIsSync())
                .filter(user -> !uuvUsernameSet.contains(user.getUsername())
                        && !hasDepartUserIdSet.contains(user.getId())
                        && !SYNC_CANNOT_DELETE_USERNAME_LIST.contains(user.getUsername()))
                .map(SysUser::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needClearUseIdList)) {
            sysUserMapper.clearOrgCodeBatch(needClearUseIdList, now, userId);
            LambdaQueryWrapper<SysUserDepart> deleteWrapper = new LambdaQueryWrapper<SysUserDepart>()
                    .in(SysUserDepart::getUserId, needClearUseIdList);
            sysUserDepartMapper.delete(deleteWrapper);

            syncInfoStringBuilder.append(String.format("清空了%s个用户的部门信息；", needClearUseIdList.size()));
        }
        return syncInfoStringBuilder.toString();
    }

    private SysUser createSysUserInfo(UUVGetStub.ADUser uuvUser, Date now, String userId) {
        String salt = ConvertUtil.randomGen(8);
        String username = uuvUser.getAccountName();
        String workNo = uuvUser.getUserCode();

        // 密码设置
        String password;
        if (StringUtils.isBlank(workNo)) {
            password = username + "@sz-mtr";
        } else {
            password = workNo + "@sz-mtr";
        }
        String passwordEncrypt = PasswordUtil.encrypt(username, password, salt);

        SysUser sysUser = new SysUser()
                .setId(username)
                .setUsername(username)
                .setRealname(uuvUser.getChsName())
                .setPassword(passwordEncrypt)
                .setSalt(salt)
                .setAvatar(null)
                .setBirthday(null)
                .setSex("False".equals(uuvUser.getSex()) ? 2 : 1)
                .setEmail(uuvUser.getEmailAddress())
                .setPhone(uuvUser.getMobilePhone())
                .setOrgCode(uuvUser.getDeptCode())
                .setStatus("enabled".equals(uuvUser.getStatus()) ? 1 : 0)
                .setDelFlag("enabled".equals(uuvUser.getStatus()) ? 0 : 1)
                .setThirdId(null)
                .setThirdType(null)
                .setActivitiSync(1)
                .setWorkNo(workNo)
                .setPost(uuvUser.getJob())
                .setTelephone(uuvUser.getOfficePhone())
                .setCreateTime(now)
                .setCreateBy(userId)
                .setUserIdentity(1)
                .setDepartIds(null)
                .setIsSync(1);

        if (null != uuvUser.getBirthday()) {
            sysUser.setBirthday(uuvUser.getBirthday().getTime());
        }

        return sysUser;
    }

    private void updateSysUserInfo(SysUser sysUser, UUVGetStub.ADUser uuvUser, Date now, String userId) {
        String salt = sysUser.getSalt();
        String username = uuvUser.getAccountName();
        String workNo = uuvUser.getUserCode();

        // 密码设置
        String password;
        if (StringUtils.isBlank(workNo)) {
            password = username + "@sz-mtr";
        } else {
            password = workNo + "@sz-mtr";
        }
        String passwordEncrypt = PasswordUtil.encrypt(username, password, salt);

        sysUser.setRealname(uuvUser.getChsName())
                .setPassword(passwordEncrypt)
                .setSalt(salt)
                .setOrgCode(uuvUser.getDeptCode())
                .setStatus("enabled".equals(uuvUser.getStatus()) ? 1 : 0)
                .setDelFlag("enabled".equals(uuvUser.getStatus()) ? 0 : 1)
                .setActivitiSync(1)
                .setWorkNo(workNo)
                .setUpdateTime(now)
                .setUpdateBy(userId)
                .setUserIdentity(1)
                .setIsSync(1);

        if (null != uuvUser.getBirthday()) {
            sysUser.setBirthday(uuvUser.getBirthday().getTime());
        }
        if (null == sysUser.getSex()) {
            sysUser.setSex("False".equals(uuvUser.getSex()) ? 2 : 1);
        }
        if (StringUtils.isBlank(sysUser.getEmail())) {
            sysUser.setEmail(uuvUser.getEmailAddress());
        }
        if (StringUtils.isBlank(sysUser.getPhone())) {
            sysUser.setPhone(uuvUser.getMobilePhone());
        }
        if (StringUtils.isBlank(sysUser.getPost())) {
            sysUser.setPost(uuvUser.getJob());
        }
        if (StringUtils.isBlank(sysUser.getTelephone())) {
            sysUser.setTelephone(uuvUser.getOfficePhone());
        }
    }

    /**
     * 获取部门信息
     *
     * @param stub             the stub
     * @param eSBSecurityToken the e sb security token
     * @return the set
     */
    private List<UUVGetStub.ADDepartment> getDepartMeantList(UUVGetStub stub, UUVGetStub.ESBSecurityTokenE eSBSecurityToken, String deptCode) {
        try {
            UUVGetStub.GetDepartMentList getDepartMeantList = (UUVGetStub.GetDepartMentList) getObject(UUVGetStub.GetDepartMentList.class);
            UUVGetStub.GetDepartMentListResponse departMeantList = stub.getDepartMentList(getDepartMeantList, eSBSecurityToken);
            UUVGetStub.ADDepartment[] arrayOfADDepartments = departMeantList.getGetDepartMentListResult().getADDepartment();
            List<UUVGetStub.ADDepartment> jdxDepartmentList = new ArrayList<>();
            if (arrayOfADDepartments != null && arrayOfADDepartments.length > 0) {
                List<UUVGetStub.ADDepartment> departmentList = Arrays.stream(arrayOfADDepartments).collect(Collectors.toList());
                // Map<String, UUVGetStub.ADDepartment> jdxDepartmentMap = departmentList.stream().collect(Collectors.toMap(UUVGetStub.ADDepartment::getDeptCode, addDepartment -> addDepartment, (k1, k2) -> k2));
                AtomicReference<UUVGetStub.ADDepartment> departmentCj = new AtomicReference<>();
                departmentList.forEach((subDept) -> {
                    if (subDept.getParentDeptCode().equals(deptCode)) {
                        subDept.setLevel(subDept.getLevel() - 1);
                        jdxDepartmentList.add(subDept);
                    }
                    if (subDept.getDeptCode().equals(deptCode)) {
                        subDept.setLevel(2);
                        departmentCj.set(subDept);
                    }
                });
                if (CollectionUtils.isNotEmpty(jdxDepartmentList)) {
                    List<UUVGetStub.ADDepartment> jdxDepartmentListChildren = new ArrayList<>();
                    jdxDepartmentList.forEach((parent) -> addDeptChildren(parent, departmentList, jdxDepartmentListChildren));
                    jdxDepartmentList.addAll(jdxDepartmentListChildren);
                }
                jdxDepartmentList.add(departmentCj.get());
            }
            return jdxDepartmentList;
        } catch (RemoteException re) {
            re.printStackTrace();
            log.error(re.getMessage());
            throw new JeecgBootException("同步数据服务器出错!");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new JeecgBootException("数据解析异常,同步失败!");
        }
    }

    private void addDeptChildren(UUVGetStub.ADDepartment parent, List<UUVGetStub.ADDepartment> departmentList, List<UUVGetStub.ADDepartment> jdxDepartmentNodeList) {
        List<UUVGetStub.ADDepartment> jdxDepartmentListTemp = new ArrayList<>();
        departmentList.forEach((subDept) -> {
            if (subDept.getParentDeptCode().equals(parent.getDeptCode())) {
                subDept.setLevel(subDept.getLevel() - 1);
                jdxDepartmentListTemp.add(subDept);
            }
            if (CollectionUtils.isNotEmpty(jdxDepartmentListTemp)) {
                jdxDepartmentNodeList.addAll(jdxDepartmentListTemp);
                addDeptChildren(subDept, departmentList, jdxDepartmentNodeList);
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param stub             the stub
     * @param eSBSecurityToken the e sb security token
     * @param deptCode         the deptCode
     * @return the user list
     */
    private List<UUVGetStub.ADUser> getUserList(UUVGetStub stub, UUVGetStub.ESBSecurityTokenE eSBSecurityToken, String deptCode, Map<String, UUVGetStub.ADDepartment> jdxDepartmentMap) {
        try {
            UUVGetStub.GetUserList getUserList = (UUVGetStub.GetUserList) getObject(UUVGetStub.GetUserList.class);
            UUVGetStub.GetUserListResponse userListResponse = stub.getUserList(getUserList, eSBSecurityToken);
            UUVGetStub.ADUser[] arrayOfADUsers = userListResponse.getGetUserListResult().getADUser();
            List<UUVGetStub.ADUser> jdxUserList = new ArrayList<>();
            if (arrayOfADUsers != null && arrayOfADUsers.length > 0) {
                List<UUVGetStub.ADUser> adUserList = Arrays.stream(arrayOfADUsers).collect(Collectors.toList());
                adUserList.forEach(user -> {
                    if (jdxDepartmentMap.get(user.getDeptCode()) != null ||
                            user.getDeptCode().equals(deptCode)) {
                        jdxUserList.add(user);
                    }
                });
            }
            return jdxUserList;
        } catch (RemoteException re) {
            re.printStackTrace();
            log.error(re.getMessage());
            throw new JeecgBootException("同步数据服务器出错!");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new JeecgBootException("数据解析异常,同步失败!");
        }
    }

    /**
     * Gets object.
     *
     * @param type the type
     * @return the object
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    private ADBBean getObject(Class<?> type) throws InstantiationException, IllegalAccessException {
        return (ADBBean) type.newInstance();
    }

    /**
     * Create esb security token uuv get stub . esb security token.
     *
     * @return the uuv get stub . esb security token
     */
    private UUVGetStub.ESBSecurityToken createESBSecurityToken() {
        UUVGetStub.ESBSecurityToken token = new UUVGetStub.ESBSecurityToken();
        token.setUsername(account);
        String strCurTime = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN);
        MD5 md5 = new MD5();
        String strPass = md5.getMD5ofStr(md5.getMD5ofStr(password) + strCurTime);
        token.setPassword(strPass);
        token.setTimestamp(strCurTime);
        return token;
    }

}
