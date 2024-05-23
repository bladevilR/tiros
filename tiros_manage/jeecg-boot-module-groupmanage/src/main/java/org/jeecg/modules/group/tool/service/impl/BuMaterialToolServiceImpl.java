package org.jeecg.modules.group.tool.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.group.tool.bean.vo.BuToolUsageRecordVO;
import org.jeecg.modules.group.tool.mapper.BuMaterialToolMapper;
import org.jeecg.modules.group.tool.service.BuMaterialToolService;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.group.toolequipment.bean.vo.BuMaterialToolsQueryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 工器具 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
@Slf4j
@Service
public class BuMaterialToolServiceImpl extends ServiceImpl<BuMaterialToolMapper, BuMaterialTools> implements BuMaterialToolService {

    @Resource
    private BuMaterialToolMapper buMaterialToolMapper;


    /**
     * @see BuMaterialToolService#page(BuMaterialToolsQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialTools> page(BuMaterialToolsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buMaterialToolMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialToolService#getById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialTools getById(String id) throws Exception {
        return buMaterialToolMapper.getById(id);
    }

    /**
     * @see BuMaterialToolService#setDutyUser(String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setDutyUser(String id, String userId) throws Exception {
        BuMaterialTools materialTools = new BuMaterialTools()
                .setId(id)
                .setUserId(userId);
        buMaterialToolMapper.updateById(materialTools);

        return true;
    }

    /**
     * @see BuMaterialToolService#pageUsage(String, String, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuToolUsageRecordVO> pageUsage(String id, String trainNo, Integer pageNo, Integer pageSize) throws Exception {
        return buMaterialToolMapper.selectUsageRecordList(new Page<>(pageNo, pageSize), id, trainNo);
    }

}
