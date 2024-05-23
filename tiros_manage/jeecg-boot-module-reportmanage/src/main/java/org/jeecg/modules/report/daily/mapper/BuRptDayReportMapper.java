package org.jeecg.modules.report.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.daily.bean.BuRptDayReport;
import org.jeecg.modules.report.daily.bean.vo.BuDayReportQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 生产日报 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
public interface BuRptDayReportMapper extends BaseMapper<BuRptDayReport> {

    /**
     * 根据条件查询生产日报
     *
     * @param queryVO 查询条件
     * @return 生产日报
     */
    BuRptDayReport selectByCondition(@Param("queryVO") BuDayReportQueryVO queryVO);

    /**
     * 根据条件查询列计划任务信息
     *
     * @param queryVO 查询条件
     * @return 列计划任务信息
     */
    List<Map<String, Object>> selectRepairInfo(@Param("queryVO") BuDayReportQueryVO queryVO);

    String selectWorkshopNameByWorkshopId(@Param("workshopId") String workshopId);

}
