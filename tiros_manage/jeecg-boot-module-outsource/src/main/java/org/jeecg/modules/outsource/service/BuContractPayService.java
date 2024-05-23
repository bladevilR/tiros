package org.jeecg.modules.outsource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.outsource.bean.BuContractPay;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.bean.vo.BuContractPayQueryVO;

import java.util.List;

/**
 * <p>
 * 支付记录 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuContractPayService extends IService<BuContractPay> {

    /**
     * 分页查询支付记录
     * @param queryVO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<BuContractPay> page(BuContractPayQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * 新增支付记录
     * @param contractPay
     * @return
     */
    boolean saveContractPay(BuContractPay contractPay) throws Exception;

    /**
     * 修改支付记录
     * @param contractPay
     * @return
     */
    boolean editContractPay(BuContractPay contractPay);

    /**
     * Remove by ids and restore boolean.
     *
     * @param asList the as list
     * @return the boolean
     */
    boolean removeByIdsAndRestore(List<String> asList);
}
