package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormValues;

import java.util.List;

/**
 * <p>
 * 记录表数据值记录：如果数据量大，查询慢，则把预警为1的放入内存表，修正后更新这边对应记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuPlanFormValuesMapper extends BaseMapper<BuPlanFormValues> {

    /**
     * 批量插入
     *
     * @param list 数据记录表数据值记录列表
     */
    void insertList(@Param("list") List<BuPlanFormValues> list);

}
