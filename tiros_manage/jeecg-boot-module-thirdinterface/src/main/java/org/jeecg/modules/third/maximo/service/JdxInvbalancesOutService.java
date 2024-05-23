package org.jeecg.modules.third.maximo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.bo.PriceZero;
import org.jeecg.modules.third.maximo.bean.JdxInvbalancesOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 物资及库存信息 服务类
 * 这里会存在同一物资的多条不同库存记录，同步时要注意
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface JdxInvbalancesOutService extends IService<JdxInvbalancesOut> {

    /**
     * 从检修maximo系统获取库房和库位信息
     *
     * @return 库房+库位信息，去重
     * @throws Exception 异常
     */
    Map<String, List<String>> getAllWarehouse() throws Exception;

    /**
     * 获取物资总数
     *
     * @return 物资总数
     */
    int countTotal();

    /**
     * 分页获取物资
     *
     * @param pageNo   页码
     * @param PageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<JdxInvbalancesOut> pageMaterial(Integer pageNo, Integer PageSize) throws Exception;

    /**
     * 根据maximo输出队列备份列表获取物资库存数据列表
     *
     * @param outTransBakList maximo输出队列备份列表
     * @return 物资库存数据列表
     * @throws Exception 异常
     */
    List<JdxInvbalancesOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception;

    /**
     * 根据库房获取物资库存数据列表
     *
     * @param location 库房 如JDX01,JDX04...
     * @return 物资库存数据列表
     * @throws Exception 异常
     */
    List<JdxInvbalancesOut> listMaterialStockByLocation(String location) throws Exception;

    /**
     * 根据仓库获取对应的地点
     *
     * @param location 仓库
     * @return 地点
     * @throws Exception 异常
     */
    String getSiteByLocation(String location) throws Exception;

    /**
     * 查询maximo中对应价格
     *
     * @param priceZeroList 物料价格为0的数据
     * @throws Exception 异常
     */
    List<PriceZero> selectPriceOfPriceZeroList(List<PriceZero> priceZeroList) throws Exception;

}
