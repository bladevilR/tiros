package org.jeecg.modules.basemanage.line.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 线路 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
@Component("buMtrLineMapper")
public interface BuMtrLineMapper extends BaseMapper<BuMtrLine> {

    /**
     * 根据id获取线路信息
     *
     * @param lineId 线路id
     * @return 线路信息
     */
    BuMtrLine selectLineById(@Param("lineId") String lineId);

    List<BuMtrLine> selectAllList();

}
