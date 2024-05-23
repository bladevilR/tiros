package org.jeecg.modules.basemanage.repair.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.repair.bean.BuRepairAttrProgRel;

import java.util.List;

/**
 * <p>
 * 检修属性修程关联 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
public interface BuRepairAttrProgRelMapper extends BaseMapper<BuRepairAttrProgRel> {

    /**
     * 根据检修属性id查询检修属性修程关联列表
     *
     * @param attributeId 检修属性id
     * @return 检修属性修程关联列表
     */
    List<BuRepairAttrProgRel> selectListByAttributeId(@Param("attributeId") String attributeId);

}
