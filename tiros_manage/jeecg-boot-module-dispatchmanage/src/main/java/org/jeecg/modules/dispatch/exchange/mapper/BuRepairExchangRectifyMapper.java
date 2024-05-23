package org.jeecg.modules.dispatch.exchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangRectify;

/**
 * <p>
 * 交接车整改项 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
public interface BuRepairExchangRectifyMapper extends BaseMapper<BuRepairExchangRectify> {

    /**
     * 分页查询交接车整改项记录
     *
     * @param page 分页信息
     * @return 分页结果
     */
    IPage<BuRepairExchangRectify> selectExchangRectifyPage(IPage<BuRepairExchangRectify> page, @Param("exchangeId") String exchangeId);

}
