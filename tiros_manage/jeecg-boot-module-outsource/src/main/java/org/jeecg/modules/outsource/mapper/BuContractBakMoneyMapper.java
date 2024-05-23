package org.jeecg.modules.outsource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.outsource.bean.BuContractBakMoney;
import org.jeecg.modules.outsource.bean.vo.BuContractBakMoneyQueryVO;

import java.math.BigDecimal;

/**
 * <p>
 * 暂列金使用记录 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuContractBakMoneyMapper extends BaseMapper<BuContractBakMoney> {
    /**
     * 分页查询
     *
     * @param page
     * @param bakMoney
     * @return
     */
    IPage<BuContractBakMoney> selectPageByCondition(Page<BuContractBakMoney> page, @Param("bakMoney") BuContractBakMoneyQueryVO bakMoney);

    /**
     * 最新一条记录
     *
     * @param contractId
     * @return
     */
    BuContractBakMoney selectMaxNewPay(@Param("contractId") String contractId);

    void updateLeftover(@Param("subtract") BigDecimal subtract, @Param("id") String id);
}
