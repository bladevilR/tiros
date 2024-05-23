package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxInvcostOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.mapper.JdxInvcostOutMapper;
import org.jeecg.modules.third.maximo.service.JdxInvcostOutService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * 物资库存成本 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-10-31
 */
@Service
public class JdxInvcostOutServiceImpl extends ServiceImpl<JdxInvcostOutMapper, JdxInvcostOut> implements JdxInvcostOutService {

    @Resource
    private JdxInvcostOutMapper jdxInvcostOutMapper;


    /**
     * @see JdxInvcostOutService#listByOutTransList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxInvcostOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception {
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return new ArrayList<>();
        }

        List<Long> transIdList = new ArrayList<>();
        for (MxoutInterTransBak outTransBak : outTransBakList) {
            transIdList.add(outTransBak.getTransid());
        }
        transIdList.sort(Comparator.comparing(Long::longValue));

        List<JdxInvcostOut> resultList = new ArrayList<>();
        List<List<Long>> transIdBatchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> transIdBatchSub : transIdBatchSubList) {
            LambdaQueryWrapper<JdxInvcostOut> wrapper = new LambdaQueryWrapper<JdxInvcostOut>()
                    .in(JdxInvcostOut::getTransid, transIdBatchSub)
                    .orderByAsc(JdxInvcostOut::getTransid)
                    .orderByAsc(JdxInvcostOut::getTransseq);
            List<JdxInvcostOut> subMaximoStockPriceList = jdxInvcostOutMapper.selectList(wrapper);
            resultList.addAll(subMaximoStockPriceList);
        }
        resultList.sort(Comparator.comparing(JdxInvcostOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxInvcostOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));

        return resultList;
    }

}
