package org.jeecg.modules.third.trans.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.trans.bean.BuMaximoTransData;

import java.util.List;

/**
 * <p>
 * 检修系统maximo数据同步中间表 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-06
 */
public interface BuMaximoTransDataService extends IService<BuMaximoTransData> {

    /**
     * 获取所有未同步到maximo的数据同步中间表数据
     *
     * @return 中间表数据列表
     * @throws Exception 异常
     */
    List<BuMaximoTransData> listAllNeedTransTransData() throws Exception;

    /**
     * 根据同步数据id获取未同步到maximo的数据同步中间表数据
     *
     * @param transDataIds 同步数据id
     * @return 中间表数据列表
     * @throws Exception 异常
     */
    List<BuMaximoTransData> listNeedTransTransDataByTransDataIds(String transDataIds) throws Exception;

    /**
     * 保存数据同步中间表数据
     *
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateTransData(BuMaximoTransData transData) throws Exception;

    /**
     * 更新数据的transId
     *
     * @param transDataList 数据列表
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateTransDataTransId(List<BuMaximoTransData> transDataList) throws Exception;

    /**
     * 根据id列表删除数据同步中间表数据
     *
     * @param idList 数据id列表
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteTransDataByIdList(List<String> idList) throws Exception;

    /**
     * 查询所有未消耗成功的物料消耗数据
     *
     * @return 未消耗成功的物料消耗数据
     * @throws Exception 异常
     */
    List<BuMaximoTransData> listAllTransferredNotSuccessMaterialTransData() throws Exception;

    /**
     * 查询所有未处理（根据库存变动处理消耗记录）的物料消耗数据
     *
     * @return 未处理的物料消耗数据
     * @throws Exception 异常
     */
    List<BuMaximoTransData> listAllTransferredNotHandledMaterialTransData() throws Exception;

    /**
     * 查询所有未传输的物料消耗数据
     *
     * @return 未传输的物料消耗数据
     * @throws Exception 异常
     */
    List<BuMaximoTransData> listAllNotTransferredMaterialTransData() throws Exception;

    /**
     * 查询所有未传输的工单修改数据
     *
     * @return 未传输的工单修改数据
     * @throws Exception 异常
     */
    List<BuMaximoTransData> listAllNotTransferredOrderReplaceTransData() throws Exception;

}
