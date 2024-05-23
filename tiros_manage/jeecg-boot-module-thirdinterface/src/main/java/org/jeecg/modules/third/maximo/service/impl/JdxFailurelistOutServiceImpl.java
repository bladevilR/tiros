package org.jeecg.modules.third.maximo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.maximo.bean.JdxFailurelistOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.mapper.JdxFailurelistOutMapper;
import org.jeecg.modules.third.maximo.service.JdxFailurelistOutService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Service
public class JdxFailurelistOutServiceImpl extends ServiceImpl<JdxFailurelistOutMapper, JdxFailurelistOut> implements JdxFailurelistOutService {

    @Resource
    private JdxFailurelistOutMapper jdxFailurelistOutMapper;


    /**
     * @see JdxFailurelistOutService#listAll()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    @DataSource(DataSourceEnum.maximodb)
    public List<JdxFailurelistOut> listAll() {
        LambdaQueryWrapper<JdxFailurelistOut> wrapper = getJdxFaultCodeWrapper();
        wrapper.orderByAsc(JdxFailurelistOut::getFailtype)
                .orderByAsc(JdxFailurelistOut::getFailurecode)
                .orderByAsc(JdxFailurelistOut::getTransid)
                .orderByAsc(JdxFailurelistOut::getTransseq);
        return jdxFailurelistOutMapper.selectList(wrapper);
    }

    /**
     * @see JdxFailurelistOutService#listByOutTransList(List)
     */
    @DataSource(DataSourceEnum.maximodb)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<JdxFailurelistOut> listByOutTransList(List<MxoutInterTransBak> outTransBakList) throws Exception {
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return new ArrayList<>();
        }

        List<Long> transIdList = new ArrayList<>();
        Map<Long, String> transIdActionMap = new HashMap<>();
        for (MxoutInterTransBak outTrans : outTransBakList) {
            transIdList.add(outTrans.getTransid());
            transIdActionMap.put(outTrans.getTransid(), outTrans.getAction());
        }
        transIdList.sort(Comparator.comparing(Long::longValue));

        List<JdxFailurelistOut> resultList = new ArrayList<>();
        List<List<Long>> batchSubList = DatabaseBatchSubUtil.batchSubList(transIdList);
        for (List<Long> batchSub : batchSubList) {
            LambdaQueryWrapper<JdxFailurelistOut> wrapper = getJdxFaultCodeWrapper();
            wrapper.in(JdxFailurelistOut::getTransid, batchSub)
                    .orderByAsc(JdxFailurelistOut::getTransid)
                    .orderByAsc(JdxFailurelistOut::getTransseq);
            List<JdxFailurelistOut> jdxFailurelistOutList = jdxFailurelistOutMapper.selectList(wrapper);
            resultList.addAll(jdxFailurelistOutList);
        }

        resultList.sort(Comparator.comparing(JdxFailurelistOut::getTransid, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(JdxFailurelistOut::getTransseq, Comparator.nullsLast(Comparator.naturalOrder())));
        for (JdxFailurelistOut failurelist : resultList) {
            String action = transIdActionMap.get(failurelist.getTransid());
            if (StringUtils.isBlank(action)) {
                action = "Add";
            }
            failurelist.setTransAction(action);
        }

        return resultList;
    }


    private LambdaQueryWrapper<JdxFailurelistOut> getJdxFaultCodeWrapper() {
        return new LambdaQueryWrapper<JdxFailurelistOut>()
                .eq(JdxFailurelistOut::getSpecialty, "电客车");
    }

}
