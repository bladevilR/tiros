package org.jeecg.modules.basemanage.regu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguMaterial;

import java.util.List;

/**
 * <p>
 * 规程额定物料 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
public interface BuRepairReguMaterialMapper extends BaseMapper<BuRepairReguMaterial> {

    /**
     * 批量插入
     *
     * @param list 规程额定物料列表
     */
    void insertList(@Param("list") List<BuRepairReguMaterial> list);

}
