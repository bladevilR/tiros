package org.jeecg.modules.basemanage.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.basemanage.repair.bean.BuRepairAttrProgRel;
import org.jeecg.modules.basemanage.repair.bean.BuRepairAttribute;
import org.jeecg.modules.basemanage.repair.bean.vo.RepairAttributeQueryVO;
import org.jeecg.modules.basemanage.repair.mapper.BuRepairAttrProgRelMapper;
import org.jeecg.modules.basemanage.repair.mapper.BuRepairAttributeMapper;
import org.jeecg.modules.basemanage.repair.service.BuRepairAttributeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 检修属性 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
@Slf4j
@Service
public class BuRepairAttributeServiceImpl extends ServiceImpl<BuRepairAttributeMapper, BuRepairAttribute> implements BuRepairAttributeService {

    @Resource
    private BuRepairAttributeMapper buRepairAttributeMapper;
    @Resource
    private BuRepairAttrProgRelMapper buRepairAttrProgRelMapper;


    /**
     * @see BuRepairAttributeService#pageRepairAttribute(RepairAttributeQueryVO, Integer, Integer)
     */
    @Override
    public IPage<BuRepairAttribute> pageRepairAttribute(RepairAttributeQueryVO queryVO, Integer pageNo, Integer pageSize) {
        IPage<BuRepairAttribute> page = buRepairAttributeMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        for (BuRepairAttribute attribute : page.getRecords()) {
            List<BuRepairAttrProgRel> attrProgRelList = buRepairAttrProgRelMapper.selectListByAttributeId(attribute.getId());
            String programNames = attrProgRelList.stream()
                    .map(BuRepairAttrProgRel::getProgramName)
                    .collect(Collectors.joining(","));
            attribute.setProgramNames(programNames)
                    .setAttrProgRelList(attrProgRelList);
        }
        return page;
    }

    /**
     * @see BuRepairAttributeService#saveRepairAttribute(BuRepairAttribute)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveRepairAttribute(BuRepairAttribute repairAttribute) {
        // 保存主表
        this.saveOrUpdate(repairAttribute);
        String attributeId = repairAttribute.getId();

        // 删除旧的关联表
        LambdaQueryWrapper<BuRepairAttrProgRel> attrProgRelWrapper = new LambdaQueryWrapper<BuRepairAttrProgRel>()
                .eq(BuRepairAttrProgRel::getAttributeId, attributeId);
        buRepairAttrProgRelMapper.delete(attrProgRelWrapper);
        // 保存关联表
        if (CollectionUtils.isNotEmpty(repairAttribute.getAttrProgRelList())) {
            for (BuRepairAttrProgRel attrProgRel : repairAttribute.getAttrProgRelList()) {
                attrProgRel.setAttributeId(attributeId)
                        .setAttributeType(repairAttribute.getAttributeType());
                buRepairAttrProgRelMapper.insert(attrProgRel);
            }
        }

        return true;
    }

    /**
     * @see BuRepairAttributeService#deleteRepairAttribute(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteRepairAttribute(String ids) {
        List<String> idList = Arrays.asList(ids.split(","));

        if (CollectionUtils.isNotEmpty(idList)) {
            // 删除关联表
            LambdaQueryWrapper<BuRepairAttrProgRel> attrProgRelWrapper = new LambdaQueryWrapper<BuRepairAttrProgRel>()
                    .in(BuRepairAttrProgRel::getAttributeId, idList);
            buRepairAttrProgRelMapper.delete(attrProgRelWrapper);
            // 删除主表
            this.removeByIds(idList);
        }

        return true;
    }

}
