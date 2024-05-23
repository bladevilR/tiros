package org.jeecg.common.tiros.third.maximo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.tiros.third.maximo.bean.BuMaximoTransData;

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
     * 添加数据同步中间表数据
     *
     * @param dataList 数据列表
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean addTransDataList(List<BuMaximoTransData> dataList) throws Exception;

    /**
     * 更新同步数据
     *
     * @param transDataList 同步数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateTransDataList(List<BuMaximoTransData> transDataList) throws Exception;

    /**
     * 更新库存消耗同步数据已处理
     *
     * @param idList 数据同步中间表数据id
     * @return 更新的数据数量
     * @throws Exception 异常
     */
    int updateConsumeTransDataHandled(List<String> idList) throws Exception;

    /**
     * 根据数据同步中间表数据id查询数据
     *
     * @param idList 数据同步中间表数据id
     * @return 未消耗成功的物料消耗数据
     * @throws Exception 异常
     */
    List<BuMaximoTransData> listTransDataByIdList(List<String> idList) throws Exception;

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
     * 根据数据类型查询所有未传输的数据
     *
     * @param typeList 数据类型
     * @return 未传输的数据
     * @throws Exception 异常
     */
    List<BuMaximoTransData> listAllNotTransferredTransDataByType(List<Integer> typeList) throws Exception;

    /**
     * 根据工单号查询未传输的工时数据
     *
     * @return 未传输的工时数据
     * @throws Exception 异常
     */
    List<BuMaximoTransData> listWorkTimeTransDataByOrderCode(String orderCode) throws Exception;

}
