package org.jeecg.modules.group.information.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.information.bean.BuMtrWorkshopGroup;

import java.util.List;

/**
 * <p>
 * 车间班组 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-26
 */
public interface BuMtrWorkshopGroupMapper extends BaseMapper<BuMtrWorkshopGroup> {

    /**
     * 根据班组id查询班组信息
     *
     * @param id 班组id
     * @return 班组信息
     */
    BuMtrWorkshopGroup selectWorkshopGroupById(@Param("id") String id);

    /**
     * 查询班组信息
     *
     * @return 班组信息
     */
    List<BuMtrWorkshopGroup> selectAllGroupInfo();
//    /**
//     * 根据车辆段id查询线路id列表
//     *
//     * @param depotId 车辆段id
//     * @return 线路id列表
//     */
//    List<String> selectLineIdListByDepotId(@Param("depotId") String depotId);
//
//    /**
//     * 根据工位id查询班组信息
//     *
//     * @param id 班组id
//     * @return 班组信息
//     */
//    BuMtrWorkshopGroup selectWorkshopGroupByWorkstationId(@Param("id") String id);
}
