package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkTurnoverChange;

/**
 * <p>
 * 周转件更换，如果换上见没有记录，应先建立记录 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-12
 */
public interface BuWorkTurnoverChangeDispatchMapper extends BaseMapper<BuWorkTurnoverChange> {
    /**
     * 车号
     *
     * @param trainId
     * @return
     */
    String selectTrainNo(@Param("trainId") String trainId);

    int updateMustReplacePlan(@Param("mustReplaceId") String mustReplaceId, @Param("uuid") String uuid);

}
