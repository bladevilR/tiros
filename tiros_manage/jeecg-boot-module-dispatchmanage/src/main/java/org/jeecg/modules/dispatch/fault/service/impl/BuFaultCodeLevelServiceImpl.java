package org.jeecg.modules.dispatch.fault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeLevel;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultCodeLevelMapper;
import org.jeecg.modules.dispatch.fault.service.BuFaultCodeLevelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yfy
 * @since 2021-05-11
 */
@Service
public class BuFaultCodeLevelServiceImpl extends ServiceImpl<BuFaultCodeLevelMapper, BuFaultCodeLevel> implements BuFaultCodeLevelService {

    @Resource
    private BuFaultCodeLevelMapper buFaultCodeLevelMapper;


    @Override
    public BuFaultCodeLevel FindFaultCodeLevelById(String id) {

        LambdaQueryWrapper<BuFaultCodeLevel> faultCodeLevelWrapper = new LambdaQueryWrapper<BuFaultCodeLevel>()
                .eq(BuFaultCodeLevel ::getFaultCodeId,id);
        BuFaultCodeLevel  buFaultCodeLevel  = buFaultCodeLevelMapper.selectOne(faultCodeLevelWrapper);
        return buFaultCodeLevel;
    }
}
