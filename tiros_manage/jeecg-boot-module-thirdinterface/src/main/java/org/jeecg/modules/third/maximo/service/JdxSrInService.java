package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.common.bean.bo.FaultCompareBO;
import org.jeecg.modules.third.maximo.bean.JdxSrIn;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxSrInService extends IService<JdxSrIn> {

    /**
     * 单个插入
     *
     * @param maximoFault 数据
     * @throws Exception 异常
     */
    boolean saveOne(JdxSrIn maximoFault) throws Exception;

    /**
     * 单个插入
     *
     * @param maximoFault 数据
     * @param inTrans     队列
     * @throws Exception 异常
     */
    boolean saveOneAndInTrans(JdxSrIn maximoFault, MxinInterTrans inTrans) throws Exception;

    /**
     * 批量插入
     *
     * @param maximoFaultList 数据列表
     * @throws Exception 异常
     */
    boolean saveList(List<JdxSrIn> maximoFaultList) throws Exception;

    /**
     * 获取maximo故障号
     *
     * @return maximo故障号列表
     * @throws Exception 异常
     */
    List<String> listFaultSn() throws Exception;

    /**
     * 获取maximo故障号状态
     *
     * @return maximo故障号状态列表
     * @throws Exception 异常
     */
    Map<String, FaultCompareBO> mapFaultSnStatus() throws Exception;

}
