package org.jeecg.modules.quality.fault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.quality.fault.bean.BuFaultSolution;
import org.jeecg.modules.quality.fault.mapper.BuFaultSolutionQualityMapper;
import org.jeecg.modules.quality.fault.service.BuFaultSolutionQualityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Slf4j
@Service
public class BuFaultSolutionQualityServiceImpl extends ServiceImpl<BuFaultSolutionQualityMapper, BuFaultSolution> implements BuFaultSolutionQualityService {

    @Resource
    private BuFaultSolutionQualityMapper buFaultSolutionQualityMapper;

    /**
     * @see BuFaultSolutionQualityService#list(String, String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultSolution> list(String categoryId, String faultCodeId, String faultReasonId) throws Exception {
        LambdaQueryWrapper<BuFaultSolution> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(categoryId)) {
            wrapper.eq(BuFaultSolution::getCategoryId, categoryId);
        }
        if (StringUtils.isNotBlank(faultCodeId)) {
            wrapper.eq(BuFaultSolution::getFaultCodeId, faultCodeId);
        }
        if (StringUtils.isNotBlank(faultReasonId)) {
            wrapper.eq(BuFaultSolution::getFaultReasonId, faultReasonId);
        }

        return buFaultSolutionQualityMapper.selectList(wrapper);
    }

}
