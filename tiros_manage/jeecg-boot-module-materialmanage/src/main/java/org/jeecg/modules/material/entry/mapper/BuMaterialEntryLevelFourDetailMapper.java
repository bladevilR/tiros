package org.jeecg.modules.material.entry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryLevelFourDetail;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
public interface BuMaterialEntryLevelFourDetailMapper extends BaseMapper<BuMaterialEntryLevelFourDetail> {


    void insertBatch(@Param("list") List<BuMaterialEntryLevelFourDetail> detailList);
}
