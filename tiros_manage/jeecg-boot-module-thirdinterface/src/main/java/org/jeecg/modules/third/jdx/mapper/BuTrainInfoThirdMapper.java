package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMtrLine;
import org.jeecg.modules.third.jdx.bean.BuTrainInfo;

import java.util.List;

/**
 * <p>
 * 车辆信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-21
 */
public interface BuTrainInfoThirdMapper extends BaseMapper<BuTrainInfo> {

    /**
     * 批量插入
     *
     * @param list 车辆信息列表
     */
    void insertList(@Param("list") List<BuTrainInfo> list);

    List<BuMtrLine> selectAllLine();

}
