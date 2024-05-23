package org.jeecg.modules.outsource.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.outsource.bean.BuOutsourceOutinQuality;

import java.util.List;

/**
 * <p>
 * 委外部件质保期 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-11-17
 */
public interface BuOutsourceOutinQualityMapper extends BaseMapper<BuOutsourceOutinQuality> {

    /**
     * 更新委外部件质保日期
     *
     * @param list 委外部件质保期列表
     */
    void updateListWarranty(@Param("list") List<BuOutsourceOutinQuality> list);

}
