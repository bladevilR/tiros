package org.jeecg.modules.basemanage.workshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.basemanage.workshop.entity.BuMtrDepot;

import java.util.List;

/**
 * <p>
 * 车辆段 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-23
 */
public interface BuMtrDepotMapper extends BaseMapper<BuMtrDepot> {

    /**
     * 获取所有车辆段
     *
     * @return 所有车辆段列表
     */
    List<BuMtrDepot> selectAllList();

}
