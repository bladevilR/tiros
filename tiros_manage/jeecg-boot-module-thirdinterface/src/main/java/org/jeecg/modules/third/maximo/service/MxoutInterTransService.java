package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.MxoutInterTrans;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;

import java.util.List;

/**
 * <p>
 * maximo输出队列 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface MxoutInterTransService extends IService<MxoutInterTrans> {

    /**
     * 根据transIdList获取maximo输出队列消息
     *
     * @param transIdList transId列表
     * @return maximo输出队列消息
     * @throws Exception 异常
     */
    List<MxoutInterTransBak> listOutTransQueueByTransIdList(List<Long> transIdList) throws Exception;

    /**
     * 根据表名获取maximo输出队列消息
     *
     * @param iFaceName 表名
     * @return maximo输出队列消息
     * @throws Exception 异常
     */
    List<MxoutInterTransBak> listOutTransQueueByIFaceName(String iFaceName) throws Exception;

    /**
     * 删除maximo输出队列
     *
     * @param outTransBakList maximo输出队列
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteOutTransQueue(List<MxoutInterTransBak> outTransBakList) throws Exception;

//    /**
//     * 获取最新的更新信息
//     *
//     * @param iFaceName  表名
//     * @param minTransId 上次获取的最大id
//     * @return 最新的更新信息列表
//     * @throws Exception 异常
//     */
//    List<MxoutInterTrans> listTransQueueByMinTransId(String iFaceName, Long minTransId) throws Exception;

}
