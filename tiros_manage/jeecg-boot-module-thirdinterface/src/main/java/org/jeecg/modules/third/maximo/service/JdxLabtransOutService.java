package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxLabtransOut;

/**
 * <p>
 * 工单人员 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxLabtransOutService extends IService<JdxLabtransOut> {

    /**
     * 获取工单人员总数
     *
     * @return 工单人员总数
     */
    int countTotal();

    /**
     * 分页获取工单人员
     *
     * @param pageNo   页码
     * @param PageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<JdxLabtransOut> pageOrderUser(Integer pageNo, Integer PageSize) throws Exception;

}
