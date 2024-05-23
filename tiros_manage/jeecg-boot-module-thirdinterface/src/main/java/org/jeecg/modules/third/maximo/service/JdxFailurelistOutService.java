package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxFailurelistOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxFailurelistOutService extends IService<JdxFailurelistOut> {

    /**
     * 获取所有故障代码
     *
     * @return 故障代码列表
     */
    List<JdxFailurelistOut> listAll();

    /**
     * 根据trans列表获取故障代码数据列表
     *
     * @param outTransBakList trans列表
     * @return 故障代码数据列表
     * @throws Exception 异常
     */
    List<JdxFailurelistOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception;

}
