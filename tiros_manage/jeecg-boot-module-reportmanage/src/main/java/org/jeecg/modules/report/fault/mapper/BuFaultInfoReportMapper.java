package org.jeecg.modules.report.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.daily.bean.vo.BuDayReportQueryVO;
import org.jeecg.modules.report.fault.bean.BuFaultInfo;
import org.jeecg.modules.report.fault.bean.vo.FaultCountQueryVO;
import org.jeecg.modules.report.fault.bean.vo.FaultDetailQueryVO;

import java.util.List;

/**
 * <p>
 * 故障信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultInfoReportMapper extends BaseMapper<BuFaultInfo> {

    /**
     * 根据条件查询故障信息
     *
     * @param queryVO 查询条件
     * @return 故障信息结果
     */
    List<BuFaultInfo> selectListByCondition(@Param("queryVO") FaultDetailQueryVO queryVO);

    /**
     * 根据生产日报查询条件查询故障信息
     *
     * @param queryVO 生产日报查询条件
     * @return 故障信息列表
     */
    List<BuFaultInfo> selectListByBuDayReportQueryVO(@Param("queryVO") BuDayReportQueryVO queryVO);

    /**
     * 根据故障汇总查询条件查询故障信息
     *
     * @param queryVO 故障汇总查询条件
     * @return 故障信息列表
     */
    List<BuFaultInfo> selectListByFaultCountQueryVO(@Param("queryVO") FaultCountQueryVO queryVO);

}
