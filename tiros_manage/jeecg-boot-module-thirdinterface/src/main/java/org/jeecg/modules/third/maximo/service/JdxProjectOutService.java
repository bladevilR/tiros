package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.*;

import java.util.List;

/**
 * <p>
 * 财务项目 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-20
 */
public interface JdxProjectOutService extends IService<JdxProjectOut> {

    /**
     * 获取所有财务项目
     *
     * @return 财务项目列表
     */
    List<JdxProjectOut> listAll();

    /**
     * 根据trans列表获取财务项目数据列表
     *
     * @param outTransBakList trans列表
     * @return 财务项目数据列表
     * @throws Exception 异常
     */
    List<JdxProjectOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception;

}
