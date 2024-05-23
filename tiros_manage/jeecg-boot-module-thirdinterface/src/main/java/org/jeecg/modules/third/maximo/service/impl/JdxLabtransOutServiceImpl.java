package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxLabtransOut;
import org.jeecg.modules.third.maximo.mapper.JdxLabtransOutMapper;
import org.jeecg.modules.third.maximo.service.JdxLabtransOutService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Service
public class JdxLabtransOutServiceImpl extends ServiceImpl<JdxLabtransOutMapper, JdxLabtransOut> implements JdxLabtransOutService {

    @Resource
    private JdxLabtransOutMapper jdxLabtransOutMapper;


    /**
     * @see JdxLabtransOutService#countTotal()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int countTotal() {
        return jdxLabtransOutMapper.selectCount(Wrappers.emptyWrapper());
    }

    /**
     * @see JdxLabtransOutService#pageOrderUser(Integer, Integer)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<JdxLabtransOut> pageOrderUser(Integer pageNo, Integer PageSize) throws Exception {
        LambdaQueryWrapper<JdxLabtransOut> wrapper = new LambdaQueryWrapper<JdxLabtransOut>()
                .orderByAsc(JdxLabtransOut::getTransdate)
                .orderByAsc(JdxLabtransOut::getTransid)
                .orderByAsc(JdxLabtransOut::getTransseq);
        return jdxLabtransOutMapper.selectPage(new Page<>(pageNo, PageSize), wrapper);
    }

}
