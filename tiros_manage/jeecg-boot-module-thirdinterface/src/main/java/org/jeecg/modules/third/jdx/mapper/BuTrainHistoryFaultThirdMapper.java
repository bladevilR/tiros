package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuTrainHistoryFault;

import java.util.List;

/**
 * <p>
 * 车辆履历-故障记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryFaultThirdMapper extends BaseMapper<BuTrainHistoryFault> {

    /**
     * 批量插入
     *
     * @param list 故障记录列表
     */
    void insertList(@Param("list") List<BuTrainHistoryFault> list);

}
