package org.jeecg.modules.outsource.service;

import org.jeecg.modules.outsource.bean.BuOutsourceRateingAnnex;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 评分附件 服务类
 * </p>
 *
 * @author youGen
 * @since 2020 -10-16
 */
public interface BuOutsourceRateingAnnexService extends IService<BuOutsourceRateingAnnex> {

    /**
     * Remove batch boolean.
     *
     * @param ids the ids
     * @return the boolean
     * @throws Exception the exception
     */
    boolean removeBatch(List<String> ids) throws Exception;
}
