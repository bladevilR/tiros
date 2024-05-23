package org.jeecg.modules.outsource.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.outsource.bean.BuOutsourceOtherSource;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceResourceQueryVO;
import org.jeecg.modules.outsource.mapper.BuOutsourceOtherSourceMapper;
import org.jeecg.modules.outsource.service.BuOutsourceOtherSourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 委外文档资料，设备质量保证书、测试报告、监修日志、过程记录、质量报告 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Service
public class BuOutsourceOtherSourceServiceImpl extends ServiceImpl<BuOutsourceOtherSourceMapper, BuOutsourceOtherSource> implements BuOutsourceOtherSourceService {
    @Resource
    private BuOutsourceOtherSourceMapper otherSourceMapper;

    @Override
    public IPage<BuOutsourceOtherSource> page(BuOutsourceResourceQueryVO queryVO, Integer pageNo, Integer pageSize) {
        return otherSourceMapper.page(new Page<>(pageNo, pageSize), queryVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBatchOtherSource(List<BuOutsourceOtherSource> otherSource) {
        return this.saveBatch(otherSource);
    }
}
