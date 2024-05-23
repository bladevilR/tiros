package org.jeecg.modules.dispatch.fault.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.dispatch.fault.bean.BuFaultSolution;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultSolutionQueryVO;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultSolutionMapper;
import org.jeecg.modules.dispatch.fault.service.BuFaultSolutionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 故障处理措施 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Slf4j
@Service
public class BuFaultSolutionServiceImpl extends ServiceImpl<BuFaultSolutionMapper, BuFaultSolution> implements BuFaultSolutionService {

    @Resource
    private BuFaultSolutionMapper buFaultSolutionMapper;


    /**
     * @see BuFaultSolutionService#pageFaultSolution(BuFaultSolutionQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFaultSolution> pageFaultSolution(BuFaultSolutionQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buFaultSolutionMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

}
