package org.jeecg.modules.quality.fault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.quality.fault.bean.BuFaultReason;
import org.jeecg.modules.quality.fault.mapper.BuFaultReasonQualityMapper;
import org.jeecg.modules.quality.fault.service.BuFaultReasonQualityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 故障原因 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Slf4j
@Service
public class BuFaultReasonQualityServiceImpl extends ServiceImpl<BuFaultReasonQualityMapper, BuFaultReason> implements BuFaultReasonQualityService {

    @Resource
    private BuFaultReasonQualityMapper buFaultReasonQualityMapper;

    /**
     * @see BuFaultReasonQualityService#list(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultReason> list(String categoryId, String faultCodeId) throws Exception {
        LambdaQueryWrapper<BuFaultReason> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(categoryId)) {
            wrapper.eq(BuFaultReason::getCategoryId, categoryId);
        }
        if (StringUtils.isNotBlank(faultCodeId)) {
            wrapper.eq(BuFaultReason::getFaultCodeId, faultCodeId);
        }

        return buFaultReasonQualityMapper.selectList(wrapper);
    }

}
