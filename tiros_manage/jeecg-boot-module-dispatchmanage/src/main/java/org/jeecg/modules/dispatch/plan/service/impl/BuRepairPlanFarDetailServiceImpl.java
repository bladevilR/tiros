package org.jeecg.modules.dispatch.plan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFarDetail;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanFarDetailMapper;
import org.jeecg.modules.dispatch.plan.service.BuRepairPlanFarDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 远期规划明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Slf4j
@Service
public class BuRepairPlanFarDetailServiceImpl extends ServiceImpl<BuRepairPlanFarDetailMapper, BuRepairPlanFarDetail> implements BuRepairPlanFarDetailService {

    @Resource
    private BuRepairPlanFarDetailMapper buRepairPlanFarDetailMapper;

    /**
     * @see BuRepairPlanFarDetailService#listByPlanFarId(String)
     */
    @Transactional(readOnly = true)
    @Override
    public List<BuRepairPlanFarDetail> listByPlanFarId(String planFarId) throws Exception {
        return buRepairPlanFarDetailMapper.selectListByPlanFarId(planFarId);
    }

}
