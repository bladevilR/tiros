package org.jeecg.modules.basemanage.track.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.track.entity.BuMtrTrack;
import org.jeecg.modules.basemanage.track.entity.vo.BuMtrTrackQueryVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 股道信息 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
@Component("buMtrTrackMapper")
public interface BuMtrTrackMapper extends BaseMapper<BuMtrTrack> {

    /**
     * 自定义分页股道信息
     *
     * @param page
     * @param track
     * @return
     */
    List<BuMtrTrack> selectTrackPage(Page<BuMtrTrack> page,@Param("track") BuMtrTrackQueryVO track);


}
