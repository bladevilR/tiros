package org.jeecg.modules.dispatch.fault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCategory;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultCategoryQueryVO;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultCategoryMapper;
import org.jeecg.modules.dispatch.fault.service.BuFaultCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Service
public class BuFaultCategoryServiceImpl extends ServiceImpl<BuFaultCategoryMapper, BuFaultCategory> implements BuFaultCategoryService {

    @Resource
    private BuFaultCategoryMapper buFaultCategoryMapper;


    /**
     * @see BuFaultCategoryService#pageFaultCategory(BuFaultCategoryQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFaultCategory> pageFaultCategory(BuFaultCategoryQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buFaultCategoryMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

}
