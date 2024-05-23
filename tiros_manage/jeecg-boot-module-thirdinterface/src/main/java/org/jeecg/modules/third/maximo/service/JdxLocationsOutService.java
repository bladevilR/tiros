package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxLocationsOut;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxLocationsOutService extends IService<JdxLocationsOut> {

    /**
     * 获取所有位置
     *
     * @return 位置列表
     */
    List<JdxLocationsOut> listAll();

    /**
     * 根据transId列表获取位置数据列表
     *
     * @param transIdList transId列表
     * @return 位置数据列表
     * @throws Exception 异常
     */
    List<JdxLocationsOut> listByTransIdList(List<Long> transIdList) throws Exception;

}
