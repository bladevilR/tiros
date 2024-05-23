package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;
import org.jeecg.modules.third.maximo.mapper.MxinInterTransMapper;
import org.jeecg.modules.third.maximo.service.MxinInterTransService;
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
public class MxinInterTransServiceImpl extends ServiceImpl<MxinInterTransMapper, MxinInterTrans> implements MxinInterTransService {

    @Resource
    private MxinInterTransMapper mxinInterTransMapper;


    /**
     * @see MxinInterTransService#getInTransId()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Long getInTransId() throws Exception {
        Long transId = mxinInterTransMapper.selectTransId();
        if (null == transId) {
            transId = 0L;
        }

        return transId;
    }

    /**
     * @see MxinInterTransService#saveOne(MxinInterTrans)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOne(MxinInterTrans inTrans) throws Exception {
        if (null == inTrans) {
            return true;
        }

        mxinInterTransMapper.insert(inTrans);

        return true;
    }

    /**
     * @see MxinInterTransService#saveList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveList(List<MxinInterTrans> inTransList) throws Exception {
        if (CollectionUtils.isEmpty(inTransList)) {
            return true;
        }

        for (MxinInterTrans inTrans : inTransList) {
            mxinInterTransMapper.insert(inTrans);
        }

        return true;
    }

}
