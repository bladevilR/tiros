package org.jeecg.modules.dispatch.exchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangLeave;

/**
 * <p>
 * 开口项（遗留问题） Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
public interface BuRepairExchangLeaveMapper extends BaseMapper<BuRepairExchangLeave> {

    /**
     * 分页查询交接车开口项记录
     *
     * @param page 分页信息
     * @return 分页结果
     */
    IPage<BuRepairExchangLeave> selectExchangLeavePage(IPage<BuRepairExchangLeave> page, @Param("exchangeId") String exchangeId);

}
