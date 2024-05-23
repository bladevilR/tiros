package org.jeecg.modules.basemanage.regu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTools;

import java.util.List;

/**
 * <p>
 * 规程额定工器具，包括：工具、器具、工装 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
public interface BuRepairReguToolsMapper extends BaseMapper<BuRepairReguTools> {

    /**
     * 批量插入
     *
     * @param list 规程额定工器具列表
     */
    void insertList(@Param("list") List<BuRepairReguTools> list);

}
