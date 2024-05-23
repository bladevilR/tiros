package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuTrainHistoryChange;

import java.util.List;

/**
 * <p>
 * 车辆履历-更换记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryChangeThirdMapper extends BaseMapper<BuTrainHistoryChange> {

    /**
     * 批量插入
     *
     * @param list 更换记录列表
     */
    void insertList(@Param("list") List<BuTrainHistoryChange> list);

}
