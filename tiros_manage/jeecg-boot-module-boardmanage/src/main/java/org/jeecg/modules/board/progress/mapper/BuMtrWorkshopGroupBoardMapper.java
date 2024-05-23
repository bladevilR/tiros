package org.jeecg.modules.board.progress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.progress.bean.BuMtrWorkshopGroup;

import java.util.List;

/**
 * <p>
 * 车间班组 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-26
 */
public interface BuMtrWorkshopGroupBoardMapper extends BaseMapper<BuMtrWorkshopGroup> {

    /**
     * 查询班组
     *
     * @return 班组信息
     */
    List<BuMtrWorkshopGroup> selectAllList();

}
