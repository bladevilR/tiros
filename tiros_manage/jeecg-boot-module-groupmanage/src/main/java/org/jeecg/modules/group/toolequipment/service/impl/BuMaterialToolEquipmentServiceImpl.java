package org.jeecg.modules.group.toolequipment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.group.tool.bean.vo.BuToolUsageRecordVO;
import org.jeecg.modules.group.tool.service.BuMaterialToolService;
import org.jeecg.modules.group.toolequipment.bean.vo.BuMaterialToolsQueryVO;
import org.jeecg.modules.group.toolequipment.mapper.BuMaterialToolEquipmentMapper;
import org.jeecg.modules.group.toolequipment.service.BuMaterialToolEquipmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 工装信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
@Slf4j
@Service
public class BuMaterialToolEquipmentServiceImpl extends ServiceImpl<BuMaterialToolEquipmentMapper, BuMaterialTools> implements BuMaterialToolEquipmentService {

    @Resource
    private BuMaterialToolEquipmentMapper buMaterialToolEquipmentMapper;


    /**
     * @see BuMaterialToolEquipmentService#page(BuMaterialToolsQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialTools> page(BuMaterialToolsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buMaterialToolEquipmentMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialToolEquipmentService#getById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialTools getById(String id) throws Exception {
        return buMaterialToolEquipmentMapper.getById(id);
    }

    /**
     * @see BuMaterialToolEquipmentService#setStatus(String, Integer)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setStatus(String id, Integer status) throws Exception {
        BuMaterialTools materialTools = new BuMaterialTools()
                .setId(id)
                .setStatus(status);
        buMaterialToolEquipmentMapper.updateById(materialTools);

        return true;
    }

    /**
     * @see BuMaterialToolEquipmentService#pageUsage(String, String, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuToolUsageRecordVO> pageUsage(String id, String trainNo, Integer pageNo, Integer pageSize) throws Exception {
        return buMaterialToolEquipmentMapper.selectUsageRecordList(new Page<>(pageNo, pageSize), id, trainNo);
    }

}
