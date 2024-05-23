package org.jeecg.modules.material.entry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryLevelFourDetail;

/**
 * <p>
 * 每次同步新增的物资或数量都都在该表记录，一次同步如果发现有新增，则创建一条入库单及相关物资明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
public interface BuMaterialEntryLevelFourDetailService extends IService<BuMaterialEntryLevelFourDetail> {



}
