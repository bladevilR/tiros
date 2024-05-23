package org.jeecg.modules.produce.workshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.produce.workshop.bean.BuMtrWorkshopGroup;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 车间班组 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-26
 */
public interface BuMtrWorkshopGroupProduceMapper extends BaseMapper<BuMtrWorkshopGroup> {

    List<BuMtrWorkshopGroup> selectAll();

}
