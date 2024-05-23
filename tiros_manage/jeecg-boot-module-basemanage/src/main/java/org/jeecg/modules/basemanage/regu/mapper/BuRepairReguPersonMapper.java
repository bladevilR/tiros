package org.jeecg.modules.basemanage.regu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguPerson;

import java.util.List;

/**
 * <p>
 * 规程人员需求 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
public interface BuRepairReguPersonMapper extends BaseMapper<BuRepairReguPerson> {

    /**
     * 批量插入
     *
     * @param list 规程人员需求列表
     */
    void insertList(@Param("list") List<BuRepairReguPerson> list);

}
