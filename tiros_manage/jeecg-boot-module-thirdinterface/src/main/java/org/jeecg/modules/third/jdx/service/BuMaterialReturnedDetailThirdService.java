package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuMaterialReturnedDetail;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransIn;

import java.util.List;

/**
 * <p>
 * 退料明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
public interface BuMaterialReturnedDetailThirdService extends IService<BuMaterialReturnedDetail> {

    /**
     * 根据退料单id获取需写入到maximo的物料退库
     *
     * @param returnedId 退料单id
     * @return 需写入到maximo的物料退库
     * @throws Exception 异常
     */
    List<JdxMatusetransIn> getMaximoMaterialReturnNeedWriteByReturnedId(String returnedId) throws Exception;

    /**
     * 根据退料明细获取需写入到maximo的物料退库
     *
     * @param returnedDetail 退料明细
     * @return 需写入到maximo的物料退库
     * @throws Exception 异常
     */
    JdxMatusetransIn getMaximoMaterialReturnNeedWriteByReturnedDetail(BuMaterialReturnedDetail returnedDetail) throws Exception;

    /**
     * 更新退料单的同步时间
     *
     * @param returnDetailIdList 退料明细id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateReturnSyncTime(List<String> returnDetailIdList) throws Exception;

    /**
     * 更新退料单的同步返回状态和时间时间
     *
     * @param returnDetailIdList 退料明细id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateReturnSyncResult(List<String> returnDetailIdList) throws Exception;

}
