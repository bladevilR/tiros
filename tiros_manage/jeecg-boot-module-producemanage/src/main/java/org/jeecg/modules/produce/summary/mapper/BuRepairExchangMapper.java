package org.jeecg.modules.produce.summary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.summary.bean.BuRepairExchang;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 交接车记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020 -09-24
 */
@Mapper
@Repository(value = "produceRepairExchang")
public interface BuRepairExchangMapper extends BaseMapper<BuRepairExchang> {

    /**
     * 根据id查询交接车记录
     *
     * @param id 交接车记录id
     * @return 交接车记录
     */
    BuRepairExchang selectRepairExchangeById(@Param("id") String id);

}
