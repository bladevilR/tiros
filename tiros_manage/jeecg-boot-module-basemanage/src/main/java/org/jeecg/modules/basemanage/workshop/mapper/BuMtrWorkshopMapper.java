package org.jeecg.modules.basemanage.workshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.workshop.entity.BuMtrWorkshop;

import java.util.List;

/**
 * <p>
 * 架修车间 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
public interface BuMtrWorkshopMapper extends BaseMapper<BuMtrWorkshop> {

    /**
     * 获取所有车间
     *
     * @return 所有车间列表
     */
    List<BuMtrWorkshop> selectAllList();

    /**
     * 获取所有车间
     *
     * @return 所有车间列表
     */
    List<BuMtrWorkshop> selectAllListWithoutGraphAddress();

    /**
     * 根据id获取车间
     *
     * @param id 车间id
     * @return 车间信息
     */
    BuMtrWorkshop selectWorkshopById(@Param("id") String id);

    /**
     * 根据id获取车间
     *
     * @param id 车间id
     * @return 车间信息
     */
    BuMtrWorkshop selectWorkshopWithoutGraphAddressById(@Param("id") String id);

}
