package org.jeecg.modules.material.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeAttrMapper;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.material.service.BuMaterialTypeAttrService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 物资属性 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-08-07
 */
@Slf4j
@Service
public class BuMaterialTypeAttrServiceImpl extends ServiceImpl<BuMaterialTypeAttrMapper, BuMaterialTypeAttr> implements BuMaterialTypeAttrService {

    @Resource
    private BuMaterialTypeAttrMapper buMaterialTypeAttrMapper;

    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;

    /**
     * @see BuMaterialTypeAttrService#getByMaterialTypeId(String)
     */
    @Transactional(readOnly = true)
    @Override
    public BuMaterialTypeAttr getByMaterialTypeId(String materialTypeId) throws Exception {
        return buMaterialTypeAttrMapper.selectByMaterialTypeId(materialTypeId);
    }

    /**
     * @see BuMaterialTypeAttrService#setMaterialAttr(BuMaterialTypeAttr)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setMaterialAttr(BuMaterialTypeAttr buMaterialTypeAttr) throws Exception {
        // 删除旧的
        LambdaQueryWrapper<BuMaterialTypeAttr> attrWrapper = new LambdaQueryWrapper<>();
        attrWrapper.eq(BuMaterialTypeAttr::getMaterialTypeId, buMaterialTypeAttr.getMaterialTypeId());
        buMaterialTypeAttrMapper.delete(attrWrapper);

        // 重新插入
        buMaterialTypeAttrMapper.insert(buMaterialTypeAttr);

        return true;
    }
}
