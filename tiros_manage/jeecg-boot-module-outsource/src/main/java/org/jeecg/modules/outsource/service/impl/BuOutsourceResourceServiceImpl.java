package org.jeecg.modules.outsource.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.outsource.bean.BuOutsourceRateingAnnex;
import org.jeecg.modules.outsource.bean.BuOutsourceResource;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceResourceQueryVO;
import org.jeecg.modules.outsource.mapper.BuOutsourceRateingAnnexMapper;
import org.jeecg.modules.outsource.mapper.BuOutsourceResourceMapper;
import org.jeecg.modules.outsource.service.BuOutsourceResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 委外过程资料 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Service
public class BuOutsourceResourceServiceImpl extends ServiceImpl<BuOutsourceResourceMapper, BuOutsourceResource> implements BuOutsourceResourceService {
    @Resource
    private BuOutsourceResourceMapper resourceMapper;
    @Resource
    private BuOutsourceRateingAnnexMapper annexMapper;

    @Override
    public IPage<BuOutsourceResource> resourcePage(BuOutsourceResourceQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return resourceMapper.resourcePage(new Page<>(pageNo, pageSize), queryVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatch(List<String> ids) throws Exception {
        ids.forEach(id -> {
            BuOutsourceResource resource = resourceMapper.selectById(id);
            if (resource != null) {
                annexMapper.deleteFileById(resource.getFileId());
            }
        });
        resourceMapper.deleteBatchIds(ids);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatchResource(List<BuOutsourceResource> outsourceResources) {
        return this.saveBatch(outsourceResources);
    }
}
