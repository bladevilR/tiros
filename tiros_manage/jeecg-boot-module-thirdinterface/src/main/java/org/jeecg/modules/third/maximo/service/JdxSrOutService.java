package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxSrOut;
import org.jeecg.modules.third.maximo.bean.JdxWoOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;

import java.util.List;

/**
 * <p>
 * 故障信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxSrOutService extends IService<JdxSrOut> {

    /**
     * 获取故障信息总数
     *
     * @return 故障信息总数
     */
    int countTotal();

    /**
     * 分页获取故障信息
     *
     * @param pageNo   页码
     * @param PageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<JdxSrOut> pageFault(Integer pageNo, Integer PageSize) throws Exception;

    /**
     * 根据maximo输出队列备份列表列表获取故障数据列表
     *
     * @param outTransBakList maximo输出队列备份列表
     * @return 故障数据列表
     * @throws Exception 异常
     */
    List<JdxSrOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception;

}
