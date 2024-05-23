package org.jeecg.modules.group.information.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.cache.workstation.WorkstationCacheService;
import org.jeecg.modules.group.information.bean.BuGroupWorkstation;
import org.jeecg.modules.group.information.bean.BuMtrWorkstation;
import org.jeecg.modules.group.information.mapper.BuGroupWorkstationMapper;
import org.jeecg.modules.group.information.service.BuGroupWorkstationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 班组工位关联 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/15
 */
@Slf4j
@Service
public class BuGroupWorkstationServiceImpl extends ServiceImpl<BuGroupWorkstationMapper, BuGroupWorkstation> implements BuGroupWorkstationService {

    @Resource
    private BuGroupWorkstationMapper buGroupWorkstationMapper;
    @Resource
    private WorkstationCacheService workstationCacheService;


    /**
     * @see BuGroupWorkstationService
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMtrWorkstation> pageWorkstationByGroupId(String groupId,String name,String position, Integer pageNo, Integer pageSize) throws Exception {
        return buGroupWorkstationMapper.pageWorkstationByGroupId(new Page<>(pageNo, pageSize), groupId,name,position);
    }

    /**
     * @see BuGroupWorkstationService#pageUnrelatedWorkstationByGroupId(String, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMtrWorkstation> pageUnrelatedWorkstationByGroupId(String groupId, Integer pageNo, Integer pageSize) throws Exception {
        return buGroupWorkstationMapper.pageUnrelatedWorkstationByGroupId(new Page<>(pageNo, pageSize), groupId);
    }

    /**
     * @see BuGroupWorkstationService#addWorkstationToGroup(BuGroupWorkstation)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addWorkstationToGroup(BuGroupWorkstation buGroupWorkstation) throws Exception {
        // 如果以前有关联关系，删除关联关系
        List<String> workstationIdList = Arrays.asList(buGroupWorkstation.getWorkstationId().split(","));
        deleteWorkstationRelation(buGroupWorkstation.getGroupId(), workstationIdList);

        // 重新插入关联关系
        for (String workstationId : workstationIdList) {
            BuGroupWorkstation groupWorkstation = new BuGroupWorkstation()
                    .setGroupId(buGroupWorkstation.getGroupId())
                    .setWorkstationId(workstationId);
            buGroupWorkstationMapper.insert(groupWorkstation);
        }

        // 更新工位缓存
        workstationCacheService.update();

        return true;
    }

    /**
     * @see BuGroupWorkstationService#deleteWorkstationFromGroup(BuGroupWorkstation)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteWorkstationFromGroup(BuGroupWorkstation buGroupWorkstation) throws Exception {
        List<String> workstationIdList = Arrays.asList(buGroupWorkstation.getWorkstationId().split(","));
        deleteWorkstationRelation(buGroupWorkstation.getGroupId(), workstationIdList);

        // 更新工位缓存
        workstationCacheService.update();

        return true;
    }


    private void deleteWorkstationRelation(String groupId, List<String> workstationIdList) {
        LambdaQueryWrapper<BuGroupWorkstation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuGroupWorkstation::getGroupId, groupId)
                .in(BuGroupWorkstation::getWorkstationId, workstationIdList);
        buGroupWorkstationMapper.delete(wrapper);
    }

}
