package org.jeecg.modules.report.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.daily.bean.BuWorkOrderTask;
import org.jeecg.modules.report.daily.bean.vo.BuDayReportQueryVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工单任务 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
public interface BuWorkOrderTaskReportMapper extends BaseMapper<BuWorkOrderTask> {

    /**
     * 根据生产日报查询条件查询工单任务
     *
     * @param queryVO 生产日报查询条件
     * @return 工单任务列表
     */
    List<BuWorkOrderTask> selectListByBuDayReportQueryVO(@Param("queryVO") BuDayReportQueryVO queryVO);

    /**
     * 根据工单任务id列表查询工单任务完成工时合计
     *
     * @param orderTaskIdList 工单任务id列表
     * @return 工单任务完成工时合计
     */
    List<Map<String, Object>> selectTaskStaffArrangeByTaskIdList(@Param("orderTaskIdList") List<String> orderTaskIdList);

}
