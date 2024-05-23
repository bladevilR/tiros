package org.jeecg.modules.dispatch.fault.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCode;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeLevel;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultCodeQueryVO;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultCodeMapper;
import org.jeecg.modules.dispatch.fault.service.BuFaultCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 故障代码 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Service
public class BuFaultCodeServiceImpl extends ServiceImpl<BuFaultCodeMapper, BuFaultCode> implements BuFaultCodeService {

    @Resource
    private BuFaultCodeMapper buFaultCodeMapper;


    /**
     * @see BuFaultCodeService#pageFaultCode(BuFaultCodeQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFaultCode> pageFaultCode(BuFaultCodeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buFaultCodeMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

}
