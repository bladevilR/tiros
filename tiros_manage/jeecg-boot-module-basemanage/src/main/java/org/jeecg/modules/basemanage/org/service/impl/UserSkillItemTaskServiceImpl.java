package org.jeecg.modules.basemanage.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.tiros.task.service.UserSkillItemTaskService;
import org.jeecg.modules.basemanage.org.bean.BuUserSkill;
import org.jeecg.modules.basemanage.org.bean.bo.RepairReguDetailBO;
import org.jeecg.modules.basemanage.org.bean.bo.UserWorkRecordReduDetailBO;
import org.jeecg.modules.basemanage.org.mapper.BuUserSkillMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 生成人员的技能项信息定时任务 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@Service
public class UserSkillItemTaskServiceImpl extends ServiceImpl<BuUserSkillMapper, BuUserSkill> implements UserSkillItemTaskService {

    @Resource
    private BuUserSkillMapper buUserSkillMapper;
    @Resource
    private SysConfigService sysConfigService;


    /**
     * @see UserSkillItemTaskService#generateUserSkills()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean generateUserSkills() throws RuntimeException {
        // 获取上次更新时间
        String configCode = "generateUserSkillsTime";
        Date lastTime = sysConfigService.getScheduleTaskLastTime(configCode);
        // 本次更新时间
        Date now = new Date();

        // 查询人员作业记录和规程明细关联信息
        List<UserWorkRecordReduDetailBO> recordUserBOList = buUserSkillMapper.selectUserWorkRecordReguDetailList(lastTime);

        // 查询规程明细作业项目和作业分类关系
        List<RepairReguDetailBO> reguDetailBOList = buUserSkillMapper.selectAllReguDetail();
        Map<String, String> topIdNameMap = reguDetailBOList.stream()
                .filter(detail -> StringUtils.isBlank(detail.getParentId()) && detail.getType() == 1)
                .collect(Collectors.toMap(RepairReguDetailBO::getId, RepairReguDetailBO::getTitle));
        Map<String, String> reguDetailTopIdMap = getReguDetailTopIdMap(reguDetailBOList);

        // 查询已有的人员技能信息
        Set<String> userIdSet = recordUserBOList.stream()
                .map(UserWorkRecordReduDetailBO::getUserId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIdSet)) {
            userIdSet.add("-1");
        }
        LambdaQueryWrapper<BuUserSkill> userSkillWrapper = new LambdaQueryWrapper<BuUserSkill>()
                .in(BuUserSkill::getUserId, userIdSet);
        List<BuUserSkill> userSkillList = buUserSkillMapper.selectList(userSkillWrapper);

        // 计算人员技能
        for (UserWorkRecordReduDetailBO recordUserBO : recordUserBOList) {
            String userId = recordUserBO.getUserId();
            String topId = reguDetailTopIdMap.get(recordUserBO.getReguDetailId());
            boolean needNewSkill = true;

            for (BuUserSkill userSkill : userSkillList) {
                if (userSkill.getUserId().equals(userId) && userSkill.getSkillId().equals(topId)) {
                    userSkill.setScore(userSkill.getScore() + 1);
                    needNewSkill = false;
                }
            }

            if (needNewSkill && StringUtils.isNotBlank(topId)) {
                userSkillList.add(
                        new BuUserSkill()
                                .setUserId(userId)
                                .setSkillId(topId)
                                .setSkillName(topIdNameMap.get(topId))
                                .setScore(1L)
                );
            }
        }

        // 更新人员技能数据
        if (CollectionUtils.isNotEmpty(userSkillList)) {
            saveOrUpdateBatch(userSkillList);
        }

        // 更新本次更新时间
        sysConfigService.updateScheduleTaskLastTime(configCode, "生成人员的技能项信息", now);

        return true;
    }


    private Map<String, String> getReguDetailTopIdMap(List<RepairReguDetailBO> reguDetailBOList) {
        Map<String, String> reguDetailTopIdMap = new HashMap<>(64);

        List<RepairReguDetailBO> topReguDetailBOList = reguDetailBOList.stream().filter(detail -> StringUtils.isBlank(detail.getParentId()) && detail.getType() == 1).collect(Collectors.toList());

        for (RepairReguDetailBO reguDetailBO : topReguDetailBOList) {
            List<String> childIdList = new ArrayList<>();
            recursionAddChildrenDetailId(reguDetailBO, childIdList, reguDetailBOList);

            String topId = reguDetailBO.getId();
            childIdList.forEach(itemDetailId -> reguDetailTopIdMap.put(itemDetailId, topId));
        }

        return reguDetailTopIdMap;
    }

    private void recursionAddChildrenDetailId(RepairReguDetailBO reguDetailBO, List<String> childIdList, List<RepairReguDetailBO> reguDetailBOList) {
        List<RepairReguDetailBO> children = reguDetailBOList.stream().filter(detail -> StringUtils.isNotBlank(detail.getParentId()) && detail.getParentId().equals(reguDetailBO.getId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(children)) {
            for (RepairReguDetailBO child : children) {
                childIdList.add(child.getId());
                recursionAddChildrenDetailId(child, childIdList, reguDetailBOList);
            }
        }
    }

}
