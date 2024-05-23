package org.jeecg.modules.dispatch.display.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.display.bean.BuDisplayConfig;

import java.util.List;

/**
 * <p>
 * 播放配置 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
public interface BuDisplayConfigMapper extends BaseMapper<BuDisplayConfig> {

    /**
     * 获取指定大屏的当前播放内容
     *
     * @param screenId     大屏id
     * @param resourceType 资源类型
     * @return 当前播放内容列表
     */
    List<BuDisplayConfig> selectListByScreenIdAndResourceType(@Param("screenId") String screenId, @Param("resourceType") Integer resourceType);

}
