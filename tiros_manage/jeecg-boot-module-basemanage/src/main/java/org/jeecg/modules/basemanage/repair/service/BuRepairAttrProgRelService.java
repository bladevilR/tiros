package org.jeecg.modules.basemanage.repair.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.repair.bean.BuRepairAttrProgRel;

import java.util.List;

/**
 * <p>
 * 检修属性修程关联 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
public interface BuRepairAttrProgRelService extends IService<BuRepairAttrProgRel> {

    /**
     * 根据检修属性id查询检修属性修程关联列表
     *
     * @param attributeId 检修属性id
     * @return 检修属性修程关联列表
     */
    List<BuRepairAttrProgRel> listByAttributeId(String attributeId);

}
