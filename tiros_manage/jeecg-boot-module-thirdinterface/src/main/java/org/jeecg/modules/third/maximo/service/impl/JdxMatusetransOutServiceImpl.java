package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransOut;
import org.jeecg.modules.third.maximo.mapper.JdxMatusetransOutMapper;
import org.jeecg.modules.third.maximo.service.JdxMatusetransOutService;
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
public class JdxMatusetransOutServiceImpl extends ServiceImpl<JdxMatusetransOutMapper, JdxMatusetransOut> implements JdxMatusetransOutService {

    @Resource
    private JdxMatusetransOutMapper jdxMatusetransOutMapper;


    /**
     * @see JdxMatusetransOutService#countTotal()
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public int countTotal() {
        LambdaQueryWrapper<JdxMatusetransOut> maximoFilterWrapper = getMaximoWrapper();
        return jdxMatusetransOutMapper.selectCount(maximoFilterWrapper);
    }

    /**
     * @see JdxMatusetransOutService#pageOrderMaterial(Integer, Integer)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<JdxMatusetransOut> pageOrderMaterial(Integer pageNo, Integer PageSize) throws Exception {
        LambdaQueryWrapper<JdxMatusetransOut> maximoFilterWrapper = getMaximoWrapper();
        maximoFilterWrapper
                .orderByAsc(JdxMatusetransOut::getTransdate)
                .orderByAsc(JdxMatusetransOut::getTransid)
                .orderByAsc(JdxMatusetransOut::getTransseq);
        return jdxMatusetransOutMapper.selectPage(new Page<>(pageNo, PageSize), maximoFilterWrapper);
    }

    /**
     * @see JdxMatusetransOutService#listByTransIdList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxMatusetransOut> listByTransIdList(List<Long> transIdList) throws Exception {
        if (CollectionUtils.isEmpty(transIdList)) {
            return new ArrayList<>();
        }

        transIdList.sort(Comparator.comparing(Long::longValue, Comparator.nullsLast(Comparator.naturalOrder())));

        List<JdxMatusetransOut> resultList = new ArrayList<>();

        List<List<Long>> batchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> batchSub : batchSubList) {
            LambdaQueryWrapper<JdxMatusetransOut> wrapper = new LambdaQueryWrapper<JdxMatusetransOut>()
                    .in(JdxMatusetransOut::getTransid, batchSub)
                    .orderByAsc(JdxMatusetransOut::getTransdate)
                    .orderByAsc(JdxMatusetransOut::getTransid)
                    .orderByAsc(JdxMatusetransOut::getTransseq);
            List<JdxMatusetransOut> jdxMatusetransOutList = jdxMatusetransOutMapper.selectList(wrapper);
            resultList.addAll(jdxMatusetransOutList);
        }

        resultList.sort(Comparator.comparing(JdxMatusetransOut::getTransdate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxMatusetransOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder())));
        return resultList;
    }


    private LambdaQueryWrapper<JdxMatusetransOut> getMaximoWrapper() {
        return new LambdaQueryWrapper<JdxMatusetransOut>()
                .notLike(JdxMatusetransOut::getRefwo, "J%");
    }

}
