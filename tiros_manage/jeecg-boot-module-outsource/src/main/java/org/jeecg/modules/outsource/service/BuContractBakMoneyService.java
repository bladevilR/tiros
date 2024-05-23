package org.jeecg.modules.outsource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.outsource.bean.BuContractBakMoney;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.bean.vo.BuContractBakMoneyQueryVO;

import java.util.List;

/**
 * <p>
 * 暂列金使用记录 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuContractBakMoneyService extends IService<BuContractBakMoney> {
    /**
     * 分页查询
     * @param queryVO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<BuContractBakMoney> page(BuContractBakMoneyQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * 新增
     * @param bakMoney
     * @return
     */
    boolean saveBakMoney(BuContractBakMoney bakMoney);

    /**
     * 修改
     * @param bakMoney
     * @return
     */
    boolean editBakMoney(BuContractBakMoney bakMoney);

    /**
     * Remove by ids and restore boolean.
     *
     * @param asList the as list
     * @return the boolean
     */
    boolean removeByIdsAndRestore(List<String> asList);
}
