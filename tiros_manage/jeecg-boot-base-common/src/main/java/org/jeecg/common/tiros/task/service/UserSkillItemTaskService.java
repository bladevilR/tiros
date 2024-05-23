package org.jeecg.common.tiros.task.service;

/**
 * <p>
 * 生成人员的技能项信息定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
public interface UserSkillItemTaskService {

    /**
     * 生成人员的技能项信息(用于定时任务)
     *
     * @return 是否成功
     * @throws RuntimeException 异常信息
     */
    boolean generateUserSkills() throws RuntimeException;

}
