package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxAssetOutService extends IService<JdxAssetOut> {

    /**
     * 获取车辆总数
     *
     * @param line 线路 1号线=1,2号线=2...
     * @return 车辆总数
     */
    int countTrainTotal(String line);

    /**
     * 分页获取车辆
     *
     * @param pageNo   页码
     * @param PageSize 页大小
     * @param line     线路 1号线=1,2号线=2...
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<JdxAssetOut> pageTrain(Integer pageNo, Integer PageSize, String line) throws Exception;

    /**
     * 获取所有车辆资产
     *
     * @param line 线路 1号线=1,2号线=2...
     * @return 车辆资产
     * @throws Exception 异常
     */
    List<JdxAssetOut> listAssetOut(String line) throws Exception;

    /**
     * 获取所有资产设备
     *
     * @return 资产设备列表
     */
    List<JdxAssetOut> listAll();

    /**
     * 根据transId列表获取资产设备数据列表
     *
     * @param transIdList transId列表
     * @return 资产设备数据列表
     * @throws Exception 异常
     */
    List<JdxAssetOut> listByTransIdList(List<Long> transIdList) throws Exception;

}
