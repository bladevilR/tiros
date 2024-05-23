package org.jeecg.modules.basemanage.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.org.bean.BuUserSkill;
import org.jeecg.modules.basemanage.org.bean.bo.RepairReguDetailBO;
import org.jeecg.modules.basemanage.org.bean.bo.UserWorkRecordReduDetailBO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 人员技能 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-03
 */
public interface BuUserSkillMapper extends BaseMapper<BuUserSkill> {

    /**
     * 查询人员作业记录和规程明细关联信息
     *
     * @param date 指定时间
     * @return 人员作业记录和规程明细关联信息
     */
    List<UserWorkRecordReduDetailBO> selectUserWorkRecordReguDetailList(@Param("date") Date date);

    /**
     * 查询所有规程明细
     *
     * @return 所有规程明细
     */
    List<RepairReguDetailBO> selectAllReguDetail();

}
