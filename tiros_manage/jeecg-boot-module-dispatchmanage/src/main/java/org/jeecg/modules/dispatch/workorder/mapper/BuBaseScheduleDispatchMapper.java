package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuBaseSchedule;

import java.util.List;

/**
 * <p>
 * 日程信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-22
 */
public interface BuBaseScheduleDispatchMapper extends BaseMapper<BuBaseSchedule> {

    /**
     * 批量插入
     *
     * @param list 日程信息列表
     */
    void insertList(@Param("list") List<BuBaseSchedule> list);

}
