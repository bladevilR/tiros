package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormWorkRecordChecks;
import org.jeecg.modules.dispatch.workorder.bean.bo.SysUserBO;

import java.util.List;

/**
 * <p>
 * 列计划作业记录检查结果 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuPlanFormWorkRecordChecksMapper extends BaseMapper<BuPlanFormWorkRecordChecks> {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUserBO selectUserBOByUserName(@Param("username") String username);

}
