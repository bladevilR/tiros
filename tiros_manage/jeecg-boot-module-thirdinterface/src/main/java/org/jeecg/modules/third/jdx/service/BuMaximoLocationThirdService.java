package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuMaximoLocation;
import org.jeecg.modules.third.maximo.bean.JdxLocationsOut;

import java.util.List;

/**
 * <p>
 * maximo资产位置 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
public interface BuMaximoLocationThirdService extends IService<BuMaximoLocation> {

    /**
     * 同步所有资产位置
     *
     * @param maximoLocationsList maximo资产位置
     * @param needDeleteAllOld    是否需要删除旧数据
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean insertAllLocationFromMaximoData(List<JdxLocationsOut> maximoLocationsList, Boolean needDeleteAllOld) throws Exception;

}
