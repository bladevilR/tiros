package org.jeecg;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.warehouse.WarehouseCacheService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <p>
 * 启动时执行特定操作
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-24
 */
@Slf4j
@Component
@Order(1)
public class JeecgApplicationRunner implements ApplicationRunner {

    @Resource
    private AssetTypeCacheService assetTypeCacheService;
    @Resource
    private WarehouseCacheService warehouseCacheService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        assetTypeCacheService.update();
        assetTypeCacheService.updateSys(null);
        log.info("应用启动时执行了方法：更新设备类型缓存信息");
        warehouseCacheService.update();
        log.info("应用启动时执行了方法：更新仓库缓存信息");
    }

}
