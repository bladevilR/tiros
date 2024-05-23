package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuTrainHistoryWork;

import java.util.List;

/**
 * <p>
 * 车辆履历-作业记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryWorkThirdMapper extends BaseMapper<BuTrainHistoryWork> {

    /**
     * 批量插入
     *
     * @param list 作业记录列表
     */
    void insertList(@Param("list") List<BuTrainHistoryWork> list);

}
