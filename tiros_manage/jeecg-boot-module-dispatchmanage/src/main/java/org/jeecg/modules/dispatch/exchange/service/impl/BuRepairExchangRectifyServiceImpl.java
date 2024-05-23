package org.jeecg.modules.dispatch.exchange.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangRectify;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangRectifyMapper;
import org.jeecg.modules.dispatch.exchange.service.BuRepairExchangRectifyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 交接车整改项 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
@Slf4j
@Service
public class BuRepairExchangRectifyServiceImpl extends ServiceImpl<BuRepairExchangRectifyMapper, BuRepairExchangRectify> implements BuRepairExchangRectifyService {

    @Resource
    private BuRepairExchangRectifyMapper buRepairExchangRectifyMapper;

    /**
     * @see BuRepairExchangRectifyService#page(String, Integer, Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public IPage<BuRepairExchangRectify> page(String exchangeId, Integer pageNo, Integer pageSize) throws Exception {
        return buRepairExchangRectifyMapper.selectExchangRectifyPage(new Page<>(pageNo, pageSize), exchangeId);
    }

    /**
     * @see BuRepairExchangRectifyService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        buRepairExchangRectifyMapper.deleteBatchIds(idList);

        return true;
    }

}
