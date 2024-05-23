package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuMaximoFinanceItem;
import org.jeecg.modules.third.maximo.bean.JdxProjectOut;

import java.util.List;

/**
 * <p>
 * 财务项目 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-27
 */
public interface BuMaximoFinanceItemThirdService extends IService<BuMaximoFinanceItem> {

    /**
     * 同步所有财务项目
     *
     * @param maximoProjectList              maximo财务项目
     * @param needUpdateOldBusinessTableData 是否需要更新业务表中的旧资产设备id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean insertAllFinanceFromMaximoData(List<JdxProjectOut> maximoProjectList, Boolean needUpdateOldBusinessTableData) throws Exception;

    /**
     * 消费maximo更新数据
     *
     * @param changeList maximo更新数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean consumeMaximoTransChangeData(List<JdxProjectOut> changeList) throws Exception;

}
