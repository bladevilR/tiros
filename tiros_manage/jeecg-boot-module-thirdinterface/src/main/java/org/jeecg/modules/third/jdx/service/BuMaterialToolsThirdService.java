package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuMaterialTools;
import org.jeecg.modules.third.maximo.bean.JdxRealassetOut;

import java.util.List;

/**
 * <p>
 * 工具信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-09
 */
public interface BuMaterialToolsThirdService extends IService<BuMaterialTools> {

    /**
     * 通过maximo全部数据插入工器具和列管备件
     *
     * @param allList maximo全部数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean insertToolFromMaximoData(List<JdxRealassetOut> allList) throws Exception;

    /**
     * 消费maximo更新数据
     *
     * @param changeList maximo更新数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean consumeMaximoTransChangeData(List<JdxRealassetOut> changeList) throws Exception;

}
