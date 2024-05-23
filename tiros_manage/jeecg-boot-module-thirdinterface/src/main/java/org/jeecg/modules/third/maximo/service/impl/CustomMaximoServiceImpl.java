package org.jeecg.modules.third.maximo.service.impl;

import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.mapper.CustomMaximoMapper;
import org.jeecg.modules.third.maximo.service.CustomMaximoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * maximo自定义 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-01
 */
@Service
public class CustomMaximoServiceImpl implements CustomMaximoService {

    @Resource
    private CustomMaximoMapper customMaximoMapper;


    /**
     * @see CustomMaximoService#listBlockingErrorInQueue() ()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<Map<String, Object>> listBlockingErrorInQueue() {
        return customMaximoMapper.selectBlockingErrorInQueueList();
    }

}
