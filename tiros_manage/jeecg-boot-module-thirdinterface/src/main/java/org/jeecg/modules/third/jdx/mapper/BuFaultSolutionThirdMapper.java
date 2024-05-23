package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuFaultReason;
import org.jeecg.modules.third.jdx.bean.BuFaultSolution;

import java.util.List;

/**
 * <p>
 * 故障处理措施 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultSolutionThirdMapper extends BaseMapper<BuFaultSolution> {

    /**
     * 批量插入
     *
     * @param list 故障处理措施列表
     */
    void insertList(@Param("list") List<BuFaultSolution> list);

    /**
     * 批量更新
     *
     * @param list 故障编码(新)列表
     */
    void updateList(@Param("list") List<BuFaultSolution> list);

}
