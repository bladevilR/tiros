package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialEntryOrder;

/**
 * <p>
 * 入库单 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
public interface BuMaterialEntryOrderThirdMapper extends BaseMapper<BuMaterialEntryOrder> {

    /**
     * 根据入库单号查询入库单
     *
     * @param entryNo 入库单号
     * @return 入库单
     */
    BuMaterialEntryOrder selectByEntryNo(@Param("entryNo") String entryNo);

}
