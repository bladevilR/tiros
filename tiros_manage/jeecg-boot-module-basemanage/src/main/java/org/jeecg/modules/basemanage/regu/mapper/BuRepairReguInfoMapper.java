package org.jeecg.modules.basemanage.regu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguInfo;

import java.util.List;

/**
 * <p>
 * 规程信息 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
public interface BuRepairReguInfoMapper extends BaseMapper<BuRepairReguInfo> {

    /**
     * 根据所属车型id查询规程信息列表
     *
     * @param trainTypeId 所属车型id 可为空=查所有
     * @return 规程信息列表
     */
    List<BuRepairReguInfo> selectListByTrainTypeId(@Param("trainTypeId") String trainTypeId);

    /**
     * 根据规程id查询规程信息
     *
     * @param id 规程id
     * @return 规程信息
     */
    BuRepairReguInfo selectReguInfoById(@Param("id") String id);

}
