package org.jeecg.modules.report.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.fault.bean.BuRptBoardSysFault;
import org.jeecg.modules.report.fault.bean.vo.FaultCountQueryVO;

import java.util.List;

/**
 * <p>
 * 故障统计-中间表-系统维度 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-12
 */
public interface BuRptBoardSysFaultReportMapper extends BaseMapper<BuRptBoardSysFault> {

    /**
     * 根据条件查询系统维度故障统计列表
     *
     * @param queryVO 查询条件
     * @return 系统维度故障统计列表
     */
    List<BuRptBoardSysFault> selectListByFaultCountQueryVO(@Param("queryVO") FaultCountQueryVO queryVO);

}
