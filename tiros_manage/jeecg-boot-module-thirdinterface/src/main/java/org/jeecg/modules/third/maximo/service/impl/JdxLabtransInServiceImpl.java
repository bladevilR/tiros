package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxLabtransIn;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;
import org.jeecg.modules.third.maximo.mapper.JdxLabtransInMapper;
import org.jeecg.modules.third.maximo.mapper.MxinInterTransMapper;
import org.jeecg.modules.third.maximo.service.JdxLabtransInService;
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
 * @since 2021-03-31
 */
@Service
public class JdxLabtransInServiceImpl extends ServiceImpl<JdxLabtransInMapper, JdxLabtransIn> implements JdxLabtransInService {

    @Resource
    private JdxLabtransInMapper jdxLabtransInMapper;
    @Resource
    private MxinInterTransMapper mxinInterTransMapper;


    /**
     * @see JdxLabtransInService#saveOne(JdxLabtransIn)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOne(JdxLabtransIn maximoWorkTime) throws Exception {
        if (null == maximoWorkTime) {
            return true;
        }

        jdxLabtransInMapper.insert(maximoWorkTime);

        return true;
    }

    /**
     * @see JdxLabtransInService#saveOneAndInTrans(JdxLabtransIn, MxinInterTrans)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOneAndInTrans(JdxLabtransIn maximoWorkTime, MxinInterTrans inTrans) throws Exception {
        if (null == maximoWorkTime || null == inTrans) {
            return true;
        }

        jdxLabtransInMapper.insert(maximoWorkTime);
        mxinInterTransMapper.insert(inTrans);

        return true;
    }

    /**
     * @see JdxLabtransInService#saveList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveList(List<JdxLabtransIn> maximoWorkTimeList) throws Exception {
        if (CollectionUtils.isEmpty(maximoWorkTimeList)) {
            return true;
        }

        for (JdxLabtransIn maximoWorkTime : maximoWorkTimeList) {
            jdxLabtransInMapper.insert(maximoWorkTime);
        }

        return true;
    }

}
