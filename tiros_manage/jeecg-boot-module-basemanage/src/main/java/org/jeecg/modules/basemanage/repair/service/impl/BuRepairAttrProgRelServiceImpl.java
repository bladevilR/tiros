package org.jeecg.modules.basemanage.repair.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.basemanage.repair.bean.BuRepairAttrProgRel;
import org.jeecg.modules.basemanage.repair.mapper.BuRepairAttrProgRelMapper;
import org.jeecg.modules.basemanage.repair.service.BuRepairAttrProgRelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 检修属性修程关联 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
@Service
public class BuRepairAttrProgRelServiceImpl extends ServiceImpl<BuRepairAttrProgRelMapper, BuRepairAttrProgRel> implements BuRepairAttrProgRelService {

    @Resource
    private BuRepairAttrProgRelMapper buRepairAttrProgRelMapper;


    /**
     * @see BuRepairAttrProgRelService#listByAttributeId(String)
     */
    @Override
    public List<BuRepairAttrProgRel> listByAttributeId(String attributeId) {
        return buRepairAttrProgRelMapper.selectListByAttributeId(attributeId);
    }

}
