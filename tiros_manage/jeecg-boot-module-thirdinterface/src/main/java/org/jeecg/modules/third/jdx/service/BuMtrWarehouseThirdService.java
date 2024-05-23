package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuMtrWarehouse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 仓库信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-07
 */
public interface BuMtrWarehouseThirdService extends IService<BuMtrWarehouse> {

    /**
     * 从检修maximo导入仓库信息，
     * 先清空二级(架大修)下的仓库
     *
     * @param warehouseMap 检修maximo物资库存表中获取的库房+库位信息，去重
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean insertWarehouseFromMaximoData(Map<String, List<String>> warehouseMap) throws Exception;

    /**
     * 获取不应该存在的仓库信息（跟maximo对比后，获取3级库及以上）
     *
     * @param warehouseMap 检修maximo物资库存表中获取的库房+库位信息，去重
     * @return 不应该存在的仓库信息树
     * @throws Exception 异常
     */
    String getShouldNotExistWarehouseIds(Map<String, List<String>> warehouseMap) throws Exception;

}
