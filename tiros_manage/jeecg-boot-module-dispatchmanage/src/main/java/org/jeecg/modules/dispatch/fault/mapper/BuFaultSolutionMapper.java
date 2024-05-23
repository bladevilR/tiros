package org.jeecg.modules.dispatch.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCategory;
import org.jeecg.modules.dispatch.fault.bean.BuFaultReason;
import org.jeecg.modules.dispatch.fault.bean.BuFaultSolution;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultReasonQueryVO;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultSolutionQueryVO;

/**
 * <p>
 * 故障处理措施 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultSolutionMapper extends BaseMapper<BuFaultSolution> {

    /**
     * 根据条件分页查询故障处理措施
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuFaultSolution> selectPageByCondition(IPage<BuFaultCategory> page, @Param("queryVO") BuFaultSolutionQueryVO queryVO);

}
