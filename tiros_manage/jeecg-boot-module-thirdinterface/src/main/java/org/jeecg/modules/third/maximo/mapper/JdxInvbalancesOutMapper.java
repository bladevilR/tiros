package org.jeecg.modules.third.maximo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.bo.PriceZero;
import org.jeecg.modules.third.maximo.bean.JdxInvbalancesOut;

import java.util.List;

/**
 * <p>
 * 物资库存 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxInvbalancesOutMapper extends BaseMapper<JdxInvbalancesOut> {

    List<JdxInvbalancesOut> selectWarehouseList();

    IPage<JdxInvbalancesOut> pageJdxInvbalancesOut(IPage<Object> page);

    List<JdxInvbalancesOut> selectListByTransIdList(@Param("transIdList") List<Long> transIdList);

    String selectSiteByLocation(@Param("location") String location);

    List<JdxInvbalancesOut> selectPrice(@Param("priceZero") PriceZero priceZero);

}
