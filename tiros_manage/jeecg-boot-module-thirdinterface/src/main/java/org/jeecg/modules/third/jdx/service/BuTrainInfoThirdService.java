package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuTrainInfo;
import org.jeecg.modules.third.maximo.bean.JdxAssetOut;

import java.util.List;

/**
 * <p>
 * 车辆信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-21
 */
public interface BuTrainInfoThirdService extends IService<BuTrainInfo> {

    /**
     * 根据线路id删除
     *
     * @param lineId 线路id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteByLineId(String lineId) throws Exception;

    /**
     * 通过maximo车辆数据插入车辆信息
     *
     * @param maximoTrainList maximo车辆数据
     * @param lineId          线路id
     * @param companyId       公司id
     * @param workshopId      车间id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean insertTrainFromMaximoData(List<JdxAssetOut> maximoTrainList, String lineId, String companyId, String workshopId) throws Exception;

}
