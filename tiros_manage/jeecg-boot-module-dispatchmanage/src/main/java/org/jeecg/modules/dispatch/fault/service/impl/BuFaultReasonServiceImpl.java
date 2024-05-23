package org.jeecg.modules.dispatch.fault.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.dispatch.fault.bean.BuFaultReason;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultReasonQueryVO;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultReasonMapper;
import org.jeecg.modules.dispatch.fault.service.BuFaultReasonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
public class BuFaultReasonServiceImpl extends ServiceImpl<BuFaultReasonMapper, BuFaultReason> implements BuFaultReasonService {

    @Resource
    private BuFaultReasonMapper buFaultReasonMapper;


    /**
     * @see BuFaultReasonService#pageFaultReason(BuFaultReasonQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFaultReason> pageFaultReason(BuFaultReasonQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buFaultReasonMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

}
