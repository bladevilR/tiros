package org.jeecg.modules.basemanage.regu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTechBookDetail;

import java.util.List;

/**
 * <p>
 * 规程明细指导书明细关联 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuRepairReguTechBookDetailMapper extends BaseMapper<BuRepairReguTechBookDetail> {

    /**
     * 批量插入
     *
     * @param list 规程明细指导书明细关联列表
     */
    void insertList(@Param("list") List<BuRepairReguTechBookDetail> list);

}
