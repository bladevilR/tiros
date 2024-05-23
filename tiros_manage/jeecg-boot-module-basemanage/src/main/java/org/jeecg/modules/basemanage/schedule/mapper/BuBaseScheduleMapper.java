package org.jeecg.modules.basemanage.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.schedule.bean.BuBaseSchedule;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 日程信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-22
 */
public interface BuBaseScheduleMapper extends BaseMapper<BuBaseSchedule> {

    /**
     * 根据开始时间和结束时间查询用户日程信息
     *
     * @param userId    用户id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 日程列表
     */
    List<BuBaseSchedule> selectListByUserIdAndTime(@Param("userId") String userId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据日期查询用户日程信息
     *
     * @param userId 用户id
     * @param date   日期
     * @return 日程列表
     */
    List<BuBaseSchedule> selectListByUserIdAndDate(@Param("userId") String userId, @Param("date") Date date);

}
