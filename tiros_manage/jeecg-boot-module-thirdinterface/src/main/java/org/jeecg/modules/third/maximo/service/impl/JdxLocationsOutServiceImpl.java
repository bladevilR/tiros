package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxLocationsOut;
import org.jeecg.modules.third.maximo.mapper.JdxLocationsOutMapper;
import org.jeecg.modules.third.maximo.service.JdxLocationsOutService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
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
public class JdxLocationsOutServiceImpl extends ServiceImpl<JdxLocationsOutMapper, JdxLocationsOut> implements JdxLocationsOutService {

    @Resource
    private JdxLocationsOutMapper jdxLocationsOutMapper;


    /**
     * @see JdxLocationsOutService#listAll()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    @DataSource(DataSourceEnum.maximodb)
    public List<JdxLocationsOut> listAll() {
        LambdaQueryWrapper<JdxLocationsOut> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(JdxLocationsOut::getChangedate)
                .orderByAsc(JdxLocationsOut::getTransid)
                .orderByAsc(JdxLocationsOut::getTransseq);
        return jdxLocationsOutMapper.selectList(wrapper);
    }

    /**
     * @see JdxLocationsOutService#listByTransIdList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxLocationsOut> listByTransIdList(List<Long> transIdList) throws Exception {
        if (CollectionUtils.isEmpty(transIdList)) {
            return new ArrayList<>();
        }
        transIdList.sort(Comparator.comparing(Long::longValue));

        List<JdxLocationsOut> resultList = new ArrayList<>();

        List<List<Long>> batchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> batchSub : batchSubList) {
            LambdaQueryWrapper<JdxLocationsOut> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(JdxLocationsOut::getTransid, batchSub)
                    .orderByAsc(JdxLocationsOut::getChangedate)
                    .orderByAsc(JdxLocationsOut::getTransid)
                    .orderByAsc(JdxLocationsOut::getTransseq);
            List<JdxLocationsOut> jdxFailurelistOutList = jdxLocationsOutMapper.selectList(wrapper);
            resultList.addAll(jdxFailurelistOutList);
        }

        resultList.sort(Comparator.comparing(JdxLocationsOut::getChangedate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxLocationsOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }

}
