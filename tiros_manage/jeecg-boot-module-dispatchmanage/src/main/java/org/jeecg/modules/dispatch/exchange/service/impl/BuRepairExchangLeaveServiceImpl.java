package org.jeecg.modules.dispatch.exchange.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangLeave;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangLeaveMapper;
import org.jeecg.modules.dispatch.exchange.service.BuRepairExchangLeaveService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 开口项（遗留问题） 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
@Slf4j
@Service
public class BuRepairExchangLeaveServiceImpl extends ServiceImpl<BuRepairExchangLeaveMapper, BuRepairExchangLeave> implements BuRepairExchangLeaveService {

    @Resource
    private BuRepairExchangLeaveMapper buRepairExchangLeaveMapper;


    /**
     * @see BuRepairExchangLeaveService#page(String, Integer, Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public IPage<BuRepairExchangLeave> page(String exchangeId, Integer pageNo, Integer pageSize) throws Exception {
        return buRepairExchangLeaveMapper.selectExchangLeavePage(new Page<>(pageNo, pageSize), exchangeId);
    }

    /**
     * @see BuRepairExchangLeaveService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        buRepairExchangLeaveMapper.deleteBatchIds(idList);

        return true;
    }


}
