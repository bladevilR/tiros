package org.jeecg.modules.outsource.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.outsource.bean.BuContractPay;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.outsource.bean.vo.BuContractPayQueryVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 支付记录 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020 -10-16
 */
public interface BuContractPayMapper extends BaseMapper<BuContractPay> {
    /**
     * 支付记录分页查询
     *
     * @param objectPage the object page
     * @param pay        the pay
     * @return page page
     */
    IPage<BuContractPay> selectPageByCondition(Page<BuContractPay> objectPage, BuContractPayQueryVO pay);

    /**
     * 查询最新支付的一条记录
     *
     * @param contractId the contract id
     * @return bu contract pay
     */
    Integer selectMaxNewPay(String contractId);

    /**
     * Update leftover.
     *
     * @param subtract the subtract
     * @param id       the id
     */
    void updateLeftover(BigDecimal subtract, String id);

    /**
     * Update left qa money.
     *
     * @param subtract the subtract
     * @param id       the id
     */
    void updateLeftQaMoney(BigDecimal subtract, String id);
}
