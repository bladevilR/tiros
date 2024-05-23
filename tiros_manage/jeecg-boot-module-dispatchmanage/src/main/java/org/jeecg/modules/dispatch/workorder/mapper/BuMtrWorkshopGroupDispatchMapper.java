package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuMtrWorkshopGroup;

import java.util.List;

/**
 * <p>
 * 车间班组 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-26
 */
public interface BuMtrWorkshopGroupDispatchMapper extends BaseMapper<BuMtrWorkshopGroup> {

    /**
     * 根据班组id列表查询班组下人员id列表
     *
     * @param groupId 班组id列表
     * @return 班组下人员id列表
     */
    List<String> selectGroupUserIdListByGroupId(@Param("groupId") String groupId);

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

}
