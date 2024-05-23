package org.jeecg.modules.dispatch.display.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.display.bean.BuDisplayScreen;

import java.util.List;

/**
 * <p>
 * 大屏信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
public interface BuDisplayScreenMapper extends BaseMapper<BuDisplayScreen> {

    /**
     * 根据大屏类型和班组id获取大屏信息列表
     *
     * @param screenType 大屏类型
     * @param groupId    班组id 大屏类型screenType=2时需要
     * @return 大屏信息列表
     */
    List<BuDisplayScreen> selectListByTypeAndGroupId(@Param("screenType") Integer screenType, @Param("groupId") String groupId);

}
