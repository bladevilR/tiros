package org.jeecg.modules.report.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.cost.bean.BuRptPersonKpi;
import org.jeecg.modules.report.cost.bean.vo.KpiQueryVO;

import java.util.List;

/**
 * <p>
 * 个人绩效 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
public interface BuRptPersonKpiReportMapper extends BaseMapper<BuRptPersonKpi> {

    /**
     * 根据条件查询个人绩效
     *
     * @param queryVO 查询条件
     * @return 个人绩效列表
     */
    List<BuRptPersonKpi> selectListForTimeByKpiQueryVO(@Param("queryVO") KpiQueryVO queryVO);

}
