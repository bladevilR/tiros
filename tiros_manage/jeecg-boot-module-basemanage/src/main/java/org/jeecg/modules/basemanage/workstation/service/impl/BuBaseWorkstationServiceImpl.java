package org.jeecg.modules.basemanage.workstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.workstation.WorkstationCacheService;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseRefStationForm;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseWorkstation;
import org.jeecg.modules.basemanage.workstation.entity.CompanyTree;
import org.jeecg.modules.basemanage.workstation.entity.vo.BuBaseWorkstationQueryVO;
import org.jeecg.modules.basemanage.workstation.mapper.BuBaseRefStationFormMapper;
import org.jeecg.modules.basemanage.workstation.mapper.BuBaseWorkstationMapper;
import org.jeecg.modules.basemanage.workstation.service.IBuBaseWorkstationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 工位信息 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
@Slf4j
@Service
public class BuBaseWorkstationServiceImpl extends ServiceImpl<BuBaseWorkstationMapper, BuBaseWorkstation> implements IBuBaseWorkstationService {

    @Resource
    private BuBaseWorkstationMapper buBaseWorkstationMapper;
    @Resource
    private BuBaseRefStationFormMapper buBaseRefStationFormMapper;
    @Resource
    private WorkstationCacheService workstationCacheService;


    /**
     * @see IBuBaseWorkstationService#selectTreeForWorkstation()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<CompanyTree> selectTreeForWorkstation() {
        return buBaseWorkstationMapper.selectCompanyTree();
    }

    @Override
    public Page<BuBaseWorkstation> selectWorkstationPage(Page<BuBaseWorkstation> page, BuBaseWorkstationQueryVO workstation) {
        return page.setRecords(buBaseWorkstationMapper.selectWorkstationPage(page, workstation));
    }

    /**
     * @see IBuBaseWorkstationService#selectByWorkGroupId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuBaseWorkstation> selectByWorkGroupId(String id) {
        return buBaseWorkstationMapper.selectByWorkGroupId(id);
    }

    /**
     * @see IBuBaseWorkstationService#saveWorkstation(BuBaseWorkstation)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveWorkstation(BuBaseWorkstation buBaseWorkstation) throws Exception {
        if (isStationNoRepeated(buBaseWorkstation)) {
            throw new JeecgBootException("工位号重复");
        }

        buBaseWorkstationMapper.insert(buBaseWorkstation);

        // 更新工位缓存
        workstationCacheService.update();

        return true;
    }

    /**
     * @see IBuBaseWorkstationService#updateWorkstation(BuBaseWorkstation)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateWorkstation(BuBaseWorkstation buBaseWorkstation) throws Exception {
        if (isStationNoRepeated(buBaseWorkstation)) {
            throw new JeecgBootException("工位号重复");
        }

        buBaseWorkstationMapper.updateById(buBaseWorkstation);

        // 更新工位缓存
        workstationCacheService.update();

        return true;
    }

    /**
     * @see IBuBaseWorkstationService#deleteWorkstationBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteWorkstationBatch(String ids) {
        if (StringUtils.isBlank(ids)) {
            throw new JeecgBootException("请选择要删除的工位");
        }
        List<String> workstationIdList = Arrays.asList(ids.split(","));

        // 删除工位关联表单
        LambdaQueryWrapper<BuBaseRefStationForm> refStationFormWrapper = new LambdaQueryWrapper<BuBaseRefStationForm>()
                .in(BuBaseRefStationForm::getWorkstationId, workstationIdList);
        buBaseRefStationFormMapper.delete(refStationFormWrapper);

        // 删除工位
        buBaseWorkstationMapper.deleteBatchIds(workstationIdList);

        // 更新工位缓存
        workstationCacheService.update();

        return true;
    }

    private boolean isStationNoRepeated(BuBaseWorkstation buBaseWorkstation) {
        LambdaQueryWrapper<BuBaseWorkstation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuBaseWorkstation::getStationNo, buBaseWorkstation.getStationNo());
        List<BuBaseWorkstation> workstationList = buBaseWorkstationMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(workstationList)) {
            return false;
        }
        if (StringUtils.isBlank(buBaseWorkstation.getId())) {
            return true;
        }
        return !buBaseWorkstation.getId().equals(workstationList.get(0).getId());
    }

}
